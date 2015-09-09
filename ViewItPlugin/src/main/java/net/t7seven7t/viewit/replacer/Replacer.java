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
package net.t7seven7t.viewit.replacer;

import org.bukkit.entity.Player;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 *
 */
public abstract class Replacer {

    // String to be replaced
    private final Pattern pattern;

    public Replacer(String replace) {
        this.pattern = Pattern.compile("%" + replace + "%");
    }

    public static Replacer of(String replace, Function<Player, String> function) {
        return new Replacer(replace) {
            @Override
            public String getResult(Player target, Player recipient) {
                return function.apply(target);
            }
        };
    }

    public static Replacer of(String replace, BiFunction<Player, Player, String> biFunction) {
        return new Replacer(replace) {
            @Override
            public String getResult(Player target, Player recipient) {
                return biFunction.apply(target, recipient);
            }
        };
    }

    public static Replacer of(String replace, Supplier<String> supplier) {
        return new Replacer(replace) {
            @Override
            public String getResult(Player target, Player recipient) {
                return supplier.get();
            }
        };
    }

    /**
     * Gets the result of this replacer when passed the target player as should be visible to the
     * recipient player
     *
     * @param target    player
     * @param recipient player
     * @return result replacement message
     */
    public abstract String getResult(Player target, Player recipient);

    /**
     * Gets the pattern for this replacer to replace
     *
     * @return pattern
     */
    public Pattern getPattern() {
        return pattern;
    }

}
