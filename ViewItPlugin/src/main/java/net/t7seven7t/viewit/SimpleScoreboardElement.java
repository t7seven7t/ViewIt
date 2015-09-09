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

import net.t7seven7t.viewit.replacer.Replacers;
import net.t7seven7t.viewit.scoreboard.ScoreboardElement;
import net.t7seven7t.viewit.scoreboard.ScoreboardService;
import net.t7seven7t.viewit.supply.AnimatedFrameSupply;
import net.t7seven7t.viewit.supply.FrameSupply;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
class SimpleScoreboardElement implements ScoreboardElement {

    // The plugin that created this scoreboard element
    private final Plugin plugin;
    // The delay between animation updates in ticks
    private final long updateDelay;
    // Contents to display
    private final List<FrameSupply> contents;
    // Map of FrameSupplies to tick independently for each player
    private final Map<Player, List<FrameSupply>> playerContentsMap;
    // Cached values waiting til next animation update to refresh
    private final Map<Player, List<String>> cachedContents;
    // Whether this element represents a scoreboard title
    private final boolean isTitle;
    // Priority of this element that affects its display order
    private int priority;
    // The last time in millis when this element updated
    private long lastUpdate;

    public SimpleScoreboardElement(Plugin plugin, int priority, long updateDelay,
                                   List<FrameSupply> contents) {
        this.priority = priority;
        this.contents = Lists.newCopyOnWriteArrayList(contents);
        this.plugin = plugin;
        // forced update delay so that players can be cleaned out // implementation specific
        this.updateDelay = updateDelay > 0 ? updateDelay : 20L * 10;
        this.isTitle = priority < 0;

        MapMaker mapMaker = new MapMaker().weakKeys();
        this.playerContentsMap = mapMaker.makeMap();
        this.cachedContents = mapMaker.makeMap();

        Bukkit.getScheduler()
                .runTaskTimerAsynchronously(plugin, this::updateAnimations, 1l, this.updateDelay);
    }

    @Override
    public List<FrameSupply> getFrames() {
        return Collections.unmodifiableList(contents);
    }

    @Override
    public long getUpdateDelay() {
        return updateDelay;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public List<String> getContents(Player player) {
        List<String> result = cachedContents.get(player);
        if (result == null) {
            result = getFrames(player).stream().map(f -> f.getCurrentFrame(player))
                    .collect(Collectors.toList());
            result = Replacers.replace(player, result);
            cachedContents.put(player, result);
        }
        return result;
    }

    @Override
    public int getSize() {
        return contents.size();
    }

    @Override
    public void setLine(int index, FrameSupply supply) {
        if (supply == null) {
            // remove
            if (index < getSize()) {
                contents.remove(index);
            } else {
                throw new IndexOutOfBoundsException("There is no line " + index + " to remove");
            }
        } else if (index >= getSize()) {
            // add to end
            contents.add(supply);
        } else {
            // insert
            contents.add(index, supply);
        }
        cachedContents.clear();
        playerContentsMap.clear();
        ScoreboardService.getPlayers(this)
                .forEach(ScoreboardService.getInstance()::invalidateScores);
    }

    @Override
    public boolean isTitle() {
        return isTitle;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
        ScoreboardService.getPlayers(this)
                .forEach(ScoreboardService.getInstance()::recalculateElementOrder);
    }

    @Override
    public long lastUpdateMillis() {
        return lastUpdate;
    }

    private void updateAnimations() {
        cachedContents.clear();
        ScoreboardService.getPlayers(this).forEach(this::updateAnimations);
        lastUpdate = System.currentTimeMillis();
        // remove values for player keys:
        cleanPlayerResources();
    }

    private List<FrameSupply> getFrames(Player player) {
        List<FrameSupply> frames = playerContentsMap.get(player);
        if (frames == null) {
            frames = contents.stream()
                    .map(f -> f instanceof AnimatedFrameSupply ? ((AnimatedFrameSupply) f)
                            .copy() : f)
                    .collect(Collectors.toList());
            playerContentsMap.put(player, frames);
        }

        return frames;
    }

    private void updateAnimations(Player player) {
        List<FrameSupply> frames = getFrames(player);
        frames.stream().filter(f -> f instanceof AnimatedFrameSupply)
                .map(f -> (AnimatedFrameSupply) f)
                .forEach(a -> a.nextFrame(player));

        // Add frames to cache
        cachedContents.put(player, Replacers.replace(player, playerContentsMap.get(player).stream()
                .map(f -> f.getCurrentFrame(player)).collect(Collectors.toList())));
    }

    private void cleanPlayerResources() {
        playerContentsMap.keySet().stream().filter(p -> !p.isOnline())
                .forEach(playerContentsMap::remove);
        cachedContents.keySet().stream().filter(p -> !p.isOnline()).forEach(cachedContents::remove);
    }

}
