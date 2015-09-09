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

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 *
 */
public class Supply {

    public static AnimatedFrameSupply of(String... frames) {
        return new AnimatedFrameSupply(Arrays.stream(frames).map(Supply::of).collect(
                Collectors.toList()));
    }

    public static AnimatedFrameSupply of(SingularFrameSupply... frames) {
        return new AnimatedFrameSupply(frames);
    }

    public static SingularFrameSupply of(String text) {
        return (p) -> text;
    }

}
