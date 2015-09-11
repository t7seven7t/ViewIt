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
import net.t7seven7t.viewit.supply.FrameSupply;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

/**
 * Players may see different animation states if the ScoreboardElement implementation supports it.
 */
public interface ScoreboardElement {
    /**
     * Returns a ScoreboardElement of the specified frames.
     *
     * @param plugin      Plugin that plans to register the element
     * @param priority    Priority for where to display this element. Higher priorities will be
     *                    displayed first. Lower priorities will not display on the scoreboard if
     *                    there are too many elements. A negative priority specifies this element is
     *                    to be a title. Title displayed will be the most negative value
     * @param updateDelay Delay in ticks between animation updates
     * @param contents    Frames to display
     */
    static ScoreboardElement of(Plugin plugin, int priority, long updateDelay,
                                List<FrameSupply> contents) {
        return ViewItPlugin.getInstance().of(plugin, priority, updateDelay, contents);
    }

    /**
     * Returns a ScoreboardElement of the specified frames.
     *
     * @param plugin      Plugin that plans to register the element
     * @param priority    Priority for where to display this element.
     * @param updateDelay Delay in ticks between animation updates
     * @param contents    Frames to display
     */
    static ScoreboardElement of(Plugin plugin, Priority priority, long updateDelay,
                                List<FrameSupply> contents) {
        return of(plugin, priority.intValue(), updateDelay, contents);
    }

    /**
     * Returns a ScoreboardElement of the specified frames
     *
     * @param plugin      Plugin that plants to register the element
     * @param priority    Priority for where to display this element
     * @param updateDelay Delay in ticks between animation updates
     * @param contents    Frames to display
     * @see ScoreboardElement#of(Plugin, int, long, List) for more documentation of priorities
     */
    static ScoreboardElement of(Plugin plugin, int priority, long updateDelay,
                                FrameSupply... contents) {
        return of(plugin, priority, updateDelay, Arrays.asList(contents));
    }

    /**
     * Returns a ScoreboardElement of the specified frames.
     *
     * @param plugin      Plugin that plans to register the element
     * @param priority    Priority for where to display this element.
     * @param updateDelay Delay in ticks between animation updates
     * @param contents    Frames to display
     */
    static ScoreboardElement of(Plugin plugin, Priority priority, long updateDelay,
                                FrameSupply... contents) {
        return of(plugin, priority, updateDelay, Arrays.asList(contents));
    }

    /**
     * Returns a ScoreboardElement of the specified frames that defaults to updating every second
     *
     * @param plugin   Plugin that plants to register the element
     * @param priority Priority for where to display this element
     * @param contents Frames to display
     * @see ScoreboardElement#of(Plugin, int, long, List) for more documentation of priorities
     */
    static ScoreboardElement of(Plugin plugin, int priority, FrameSupply... contents) {
        return of(plugin, priority, Arrays.asList(contents));
    }

    /**
     * Returns a ScoreboardElement of the specified frames that defaults to updating every second.
     *
     * @param plugin   Plugin that plants to register the element
     * @param priority Priority for where to display this element
     * @param contents Frames to display
     * @see ScoreboardElement#of(Plugin, int, long, List) for more documentation of priorities
     */
    static ScoreboardElement of(Plugin plugin, Priority priority, FrameSupply... contents) {
        return of(plugin, priority, Arrays.asList(contents));
    }

    /**
     * Returns a ScoreboardElement of the specified frames that defaults to updating every second.
     *
     * @param plugin   Plugin that plants to register the element
     * @param priority Priority for where to display this element
     * @param contents Frames to display
     * @see ScoreboardElement#of(Plugin, int, long, List) for more documentation of priorities
     */
    static ScoreboardElement of(Plugin plugin, int priority, List<FrameSupply> contents) {
        return of(plugin, priority, 20L, contents);
    }

    /**
     * Returns a ScoreboardElement of the specified frames that defaults to updating every second.
     *
     * @param plugin   Plugin that plants to register the element
     * @param priority Priority for where to display this element
     * @param contents Frames to display
     * @see ScoreboardElement#of(Plugin, int, long, List) for more documentation of priorities
     */
    static ScoreboardElement of(Plugin plugin, Priority priority, List<FrameSupply> contents) {
        return of(plugin, priority.intValue(), 20L, contents);
    }

    /**
     * Gets the backing FrameSupplies that supply this element with what to display on each line
     */
    List<FrameSupply> getFrames();

    /**
     * Gets the delay in ticks between animation updates
     */
    long getUpdateDelay();

    /**
     * Gets the text contents of this element as should be displayed to the player specified
     */
    List<String> getContents(Player player);

    /**
     * Gets the number of lines this element contains
     */
    int getSize();

    /**
     * <p>Inserts a line into this element</p>
     *
     * <p>If the index is &gt;= getSize() then the line will be appended to the end.</p> <p>If
     * supply is null then the line will be removed.</p>
     *
     * @param index  index to insert at
     * @param supply provider of textual goodness
     * @throws IndexOutOfBoundsException if index exceeds size in a removal operation
     */
    void setLine(int index, FrameSupply supply);

    /**
     * Gets whether this element is a title
     */
    boolean isTitle();

    /**
     * Gets the priority of this element
     */
    int getPriority();

    /**
     * Sets the priority to the specified priority and recalculates its order in the scoreboard
     */
    void setPriority(int priority);

    /**
     * Sets the priority to the specified priority and recalculates its order in the scoreboard
     */
    default void setPriority(Priority priority) {
        setPriority(priority.intValue());
    }

    /**
     * Gets the last time this in millis since UNIX time (Jan 1st 1970) that this element updated
     * its contents
     */
    long lastUpdateMillis();

    /**
     * Gets the plugin that created this element
     */
    Plugin getPlugin();

    /**
     * A priority to display a ScoreboardElement at.
     */
    class Priority {
        private static final int LOWEST = 0;
        private static final int LOW = 100;
        private static final int NORMAL = 200;
        private static final int HIGH = 300;
        private static final int HIGHEST = 400;
        private final int priority;

        private Priority(int priority) {
            this.priority = priority;
        }

        public static Priority LOWEST(int subPriority) {
            return new Priority(LOWEST + subPriority);
        }

        public static Priority LOW(int subPriority) {
            return new Priority(LOW + subPriority);
        }

        public static Priority NORMAL(int subPriority) {
            return new Priority(NORMAL + subPriority);
        }

        public static Priority HIGH(int subPriority) {
            return new Priority(HIGH + subPriority);
        }

        public static Priority HIGHEST(int subPriority) {
            return new Priority(HIGHEST + subPriority);
        }

        private static void checkSubPriority(int value) {
            if (value >= 100) {
                throw new IllegalArgumentException("Cannot have a subPriority > 100");
            }
        }

        public int intValue() {
            return priority;
        }

        /**
         * Converts the priority to title priority ordering
         */
        public Priority title() {
            return priority == 0 ? new Priority(-1) : new Priority(Math.abs(priority) * -1);
        }
    }
}
