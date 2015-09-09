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
package net.t7seven7t.viewit.scoreboard;

import net.t7seven7t.viewit.ViewItPlugin;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <P>The scoreboard service provides more intuitive control and interoperability between plugins
 * over a player's scoreboard than Bukkit's {@link ScoreboardManager}. The default implementation
 * uses a separate Scoreboard for each player.</P>
 *
 * <P>{@link ScoreboardElement}s are the key component in this system. They allow far more
 * flexibility for scoreboard values and allow developers to focus on what they would like to show
 * to players using the scoreboard rather than the complex details of how it works and why it may
 * appear laggy.</P>
 *
 * @see ScoreboardElement
 */
public interface ScoreboardService {

    /**
     * A comparator that sorts elements by their priority in descending order
     */
    Comparator<ScoreboardElement> PRIORITY_COMPARATOR = Comparator
            .comparingInt(ScoreboardElement::getPriority).reversed();

    /**
     * Gets the implementation instance of this ScoreboardService
     */
    static ScoreboardService getInstance() {
        return ViewItPlugin.getInstance().getScoreboardService();
    }

    /**
     * Returns a list of Players can viewit the specified element
     */
    static List<Player> getPlayers(ScoreboardElement element) {
        return Bukkit.getOnlinePlayers().stream().filter(
                player -> Optional.ofNullable(getInstance().getElements(player))
                        .orElse(Collections.emptyList()).contains(element)).collect(
                Collectors.toList());
    }

    /**
     * Gets whether the player has a title
     */
    default boolean hasTitle(Player player) {
        return getTitle(player) != null;
    }

    /**
     * Gets the ScoreboardElement for the title displayed to the player if there is one otherwise
     * null
     */
    ScoreboardElement getTitle(Player player);

    /**
     * Takes over control of a player's scoreboard
     */
    void addPlayer(Player player);

    /**
     * Removes control of a player's scoreboard from the scoreboard service
     */
    void removePlayer(Player player);

    /**
     * Forces an update to the scoreboard on the next pass. Also unregisters all teams and
     * objectives associated with this service.
     */
    void invalidateScores(Player player);

    /**
     * Adds an element to show to a player
     */
    void addElement(Player player, ScoreboardElement element);

    /**
     * Add an array of elements to show to a player
     */
    void addElements(Player player, ScoreboardElement... elements);

    /**
     * Removes an element from the list shown to a player
     */
    void removeElement(Player player, ScoreboardElement element);

    /**
     * Removes an array of elements from the list shown to a player
     */
    void removeElements(Player player, ScoreboardElement... elements);

    /**
     * Reorders the elements shown to this player by their priority
     */
    void recalculateElementOrder(Player player);

    /**
     * Gets the list of ScoreboardElements that are tracked for this player
     */
    List<ScoreboardElement> getElements(Player player);

    /**
     * Gets whether the scoreboard is visible to the player
     */
    boolean isVisible(Player player);

    /**
     * Sets whether the scoreboard is visible to the player. Has no effect if the player hasn't been
     * added yet.
     */
    void setVisibility(Player player, boolean visible);
}
