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

/**
 * An object that supplies a String representing its current animation frame
 */
@FunctionalInterface
public interface FrameSupply {

    /**
     * Returns the current frame
     *
     * @param player Receiving player
     * @return formatted frame contents
     */
    String getCurrentFrame(Player player);

}
