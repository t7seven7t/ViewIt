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
package net.t7seven7t.viewit.supply;

import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * A simple implementation of an AnimatedFrameSupply
 */
public class AnimatedFrameSupply implements FrameSupply {

    // Current frame index
    private int index;
    // Animation frames
    private List<SingularFrameSupply> frames;

    public AnimatedFrameSupply(List<SingularFrameSupply> frames) {
        this.index = 0;
        // Shallow copy of list
        this.frames = frames;
    }

    public AnimatedFrameSupply(SingularFrameSupply... frames) {
        this(Arrays.asList(frames));
    }

    /**
     * {@inheritDoc}
     *
     * @param player Receiving player
     */
    public String getCurrentFrame(Player player) {
        return getFrame(player, index);
    }

    /**
     * <P>Gets the animated frame at the ith position in this animation.</P> <P>Override this method
     * to provide your alternative way of obtaining certain frames. If this method is overriden then
     * so must {@link AnimatedFrameSupply#getFrameCount()}</P>
     *
     * @param player Receiving player
     * @param index  frame position in the animation
     * @return formatted frame contents
     */
    public String getFrame(Player player, int index) {
        if (index < 0 || index >= frames.size()) {
            index = 0;
        }

        return frames.size() == 0 ? "" : frames.get(index).getCurrentFrame(player);
    }

    /**
     * Gets the total number of frames in this sequence
     *
     * @return frame count
     */
    public int getFrameCount() {
        return frames.size();
    }

    /**
     * Gets the next frame in the animation sequence
     *
     * @param player Receiving player
     * @return formatted frame contents
     */
    public String nextFrame(Player player) {
        setFrameIndex(getFrameIndex() + 1);
        if (getFrameIndex() >= getFrameCount()) {
            setFrameIndex(0);
        }

        return getCurrentFrame(player);
    }

    /**
     * Java's cloneable interface sucks and copy constructors can't be used in interfaces for
     * obvious reasons. This method should return a shallow copy of the implementing class. Animated
     * Supplies must provide this so that each player can have their own AnimatedSupply with
     * separate internal animation states.
     *
     * @return shallow copy
     */
    public AnimatedFrameSupply copy() {
        return new AnimatedFrameSupply(frames);
    }

    /**
     * Gets the current frame index
     *
     * @return current frame index
     */
    public int getFrameIndex() {
        return index;
    }

    /**
     * Sets the current frame index to the ith index
     *
     * @param index frame number
     */
    public void setFrameIndex(int index) {
        this.index = index;
    }

}
