/**
 * Copyright 2015 t7seven7t
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package net.t7seven7t.viewit;

import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;

import net.t7seven7t.viewit.scoreboard.ScoreboardElement;
import net.t7seven7t.viewit.scoreboard.ScoreboardService;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 */
class SimpleScoreboardService implements ScoreboardService {

    public static final String DUMMY_PREFIX = "dummy_viewit";
    // Map of elements that are visible to a player
    private final Map<Player, List<ScoreboardElement>> elementsMap;
    // Map of when elements have last been updated for every player
    private final Map<ScoreboardElement, Long> elementUpdatesMap;
    // List of players that have toggled their scoreboard visiblity off
    private final Set<Player> invisibleTo;

    public SimpleScoreboardService(Plugin plugin) {
        this.elementsMap = new MapMaker().weakKeys().makeMap();
        this.elementUpdatesMap = new MapMaker().makeMap();
        this.invisibleTo = Sets.newConcurrentHashSet();
        Bukkit.getPluginManager().registerEvents(new SimpleScoreboardListener(plugin), plugin);
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::update, 1L,
                plugin.getConfig().getLong("scoreboard-tick-interval", 1L));
    }

    private void update() {
        Bukkit.getOnlinePlayers().forEach(this::update);
        elementsMap.values()
                .forEach(list -> list
                        .forEach(element -> elementUpdatesMap
                                .put(element, element.lastUpdateMillis())));
    }

    private void update(Player player) {
        List<ScoreboardElement> elements = getElements(player);

        if (elements == null || elements.isEmpty() || !isVisible(player)) {
            // Player has no elements to show
            return;
        }

        Scoreboard board = player.getScoreboard();
        Objective objective = board.getObjective(DUMMY_PREFIX);

        boolean forceUpdate = false;
        if (objective == null) {
            // objective not yet created for this player; will force add all elements to scoreboard
            forceUpdate = true;
            objective = board.registerNewObjective(DUMMY_PREFIX, "dummy");
        }

        if (objective.getDisplaySlot() != DisplaySlot.SIDEBAR) {
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        // title always the last element because of sorting: -ve < +ve
        ScoreboardElement titleElement = elements.get(elements.size() - 1);
        if (titleElement.isTitle() && titleElement.getSize() > 0) {
            String title = fixFormat(titleElement.getContents(player).get(0));
            if (!title.equals(objective.getDisplayName())) {
                objective.setDisplayName(title);
            }
        } else if (objective.getDisplayName() != null || !objective.getDisplayName().isEmpty()) {
            // remove title if there isn't one
            objective.setDisplayName("");
        }

        // list of elements to remove after; can't remove in iterator for CopyOnWriteArrayList
        List<ScoreboardElement> removals = null;
        int i = 24; // 24 -> 10 all double digit for less annoying score placement
        Iterator<ScoreboardElement> it = elements.iterator();
        while (it.hasNext()) {
            ScoreboardElement element = it.next();

            if (element.isTitle() || element.getSize() == 0) {
                continue;
            }

            if (!element.getPlugin().isEnabled()) {
                // may cause errors to display so remove this element
                removals = Optional.ofNullable(removals).orElse(Lists.newArrayList());
                removals.add(element);
                continue;
            }

            if (!forceUpdate && !hasUpdate(element)) {
                i -= element.getSize();
                continue;
            }

            for (String text : element.getContents(player)) {
                if (i < 10) {
                    break;
                }

                // set text for current line
                setLine(board, objective, i, text);
                --i;
            }
        }

        // clear up scoreboard if nothing there
        while (i >= 10) {
            removeLine(board, i);
            --i;
        }

        Optional.ofNullable(removals).ifPresent(r -> r.stream().forEach(elements::remove));
    }

    /**
     * Removes a line from the scoreboard
     */
    private void removeLine(Scoreboard board, int score) {
        Optional.ofNullable(board.getTeam(DUMMY_PREFIX + score))
                .ifPresent(team -> team.getEntries().forEach(board::resetScores));
    }

    /**
     * Sets a line on the scoreboard to the text specified (max 30 chars)
     */
    private void setLine(Scoreboard board, Objective objective, int score,
                         String text) {
        String teamName = DUMMY_PREFIX + score;
        Team team = board.getTeam(teamName);

        if (team == null) {
            team = board.registerNewTeam(teamName);
            String entry = ChatColor.values()[score & 0xF].toString(); // hex 16; 16 colors
            team.addEntry(entry);
            objective.getScore(entry).setScore(score);
        }

        List<String> parts = splitString(fixFormat(text));

        team.setPrefix(parts.get(0));
        team.setSuffix(parts.size() > 1 ? parts.get(1) : "");
    }

    /**
     * Splits a string into 16 character parts
     */
    private List<String> splitString(String string) {
        if (string.length() <= 16) {
            return Lists.newArrayList(string);
        }

        String part1 = string.substring(0, 16);
        String part2 = string.substring(16, Math.min(string.length(), 32));
        char last = part1.charAt(15);
        char first = part2.charAt(0);
        // check if split on a color code
        if (last == 167 && ChatColor.getByChar(first) != null) {
            part1 = part1.substring(0, 15);
            part2 = last + part2;
        }

        // Apply part 1 end color to beginning of part 2
        String lastColors = ChatColor.getLastColors(part1);
        if (lastColors.isEmpty()) lastColors = ChatColor.WHITE.toString();
        part2 = lastColors + part2;
        part2 = part2.substring(0, Math.min(part2.length(), 16));

        return Lists.newArrayList(part1, part2);
    }

    /**
     * Checks if an element has an update that needs to be applied
     */
    private boolean hasUpdate(ScoreboardElement element) {
        Optional<Long> value = Optional.ofNullable(elementUpdatesMap.get(element));
        // check if time since last recorded update later than the last textual update for element
        return value.orElse(0L) < element.lastUpdateMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ScoreboardElement getTitle(Player player) {
        return getElements(player).stream().filter(ScoreboardElement::isTitle)
                .sorted(PRIORITY_COMPARATOR.reversed()).findFirst()
                .orElse(null);
    }

    /**
     * Translates formatting codes to section symbols ready for the client to receive
     */
    private String fixFormat(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPlayer(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        player.setScoreboard(board);
        elementsMap.put(player, new CopyOnWriteArrayList<>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePlayer(Player player) {
        invalidateScores(player);
        elementsMap.remove(player);
        invisibleTo.remove(player);
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void invalidateScores(Player player) {
        Scoreboard board = player.getScoreboard();
        // copy just in case of concurrent modification during removal
        List<Objective> objectives = Lists.newArrayList(board.getObjectives());
        objectives.stream().filter(o -> o.getName().startsWith(DUMMY_PREFIX))
                .forEach(Objective::unregister);

        List<Team> teams = Lists.newArrayList(board.getTeams());
        teams.stream().filter(t -> t.getName().startsWith(DUMMY_PREFIX)).forEach(Team::unregister);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisibility(Player player, boolean visible) {
        if (visible) {
            invisibleTo.remove(player);
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        } else {
            invisibleTo.add(player);
            invalidateScores(player);
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible(Player player) {
        return !invisibleTo.contains(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addElement(Player player, ScoreboardElement element) {
        addElements(player, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addElements(Player player, ScoreboardElement... elements) {
        List<ScoreboardElement> list = getElements(player);
        if (list == null) {
            addPlayer(player);
            list = getElements(player);
        }

        list.addAll(Arrays.asList(elements));
        recalculateElementOrder(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeElement(Player player, ScoreboardElement element) {
        removeElements(player, element);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeElements(Player player, ScoreboardElement... elements) {
        List<ScoreboardElement> list = getElements(player);
        if (list == null) {
            // can't remove from player not being tracked
            return;
        }

        Arrays.asList(elements).forEach(list::remove);

        if (list.isEmpty()) {
            removePlayer(player);
        } else {
            invalidateScores(player);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recalculateElementOrder(Player player) {
        List<ScoreboardElement> elements = getElements(player);
        if (elements == null) {
            return;
        }

        elements.sort(PRIORITY_COMPARATOR);
        invalidateScores(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ScoreboardElement> getElements(Player player) {
        return elementsMap.get(player);
    }
}
