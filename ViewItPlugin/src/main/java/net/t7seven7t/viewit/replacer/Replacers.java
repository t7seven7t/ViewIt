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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.t7seven7t.viewit.Dependency;
import net.t7seven7t.viewit.ViewItPlugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.clip.placeholderapi.PlaceholderAPI;

/**
 *
 */
public class Replacers {

    private static final Pattern EMPTY = Pattern.compile("");
    private static List<Replacer> replacerList;

    public Replacers() {
        replacerList = Lists.newArrayList();

        // Default replacers:
        registerReplacer(Defaults.NAME);
        registerReplacer(Defaults.PING);
    }

    public static void registerReplacer(Replacer replacer) {
        replacerList.add(replacer);
    }

    public static List<String> replace(Player recipient, List<String> message) {
        return replace(recipient, recipient, message);
    }

    public static String replace(Player recipient, String message) {
        return replace(recipient, recipient, message);
    }

    public static List<String> replace(Player target, Player recipient, List<String> messages) {
        final Map<Replacer, String> replaceResults = Maps.newHashMap();
        final List<String> result = Lists.newArrayList();
        messages.forEach(
                message -> result.add(replace(target, recipient, message, replaceResults)));
        if (Dependency.PlaceholderAPI.isPresent()) {
            return PlaceholderAPI.setPlaceholders(target, result);
        }
        return result;
    }

    public static String replace(Player target, Player recipient, String message,
                                 Map<Replacer, String> replaceResults) {
        Matcher m = EMPTY.matcher(message);
        for (Replacer replacer : replacerList) {
            m.usePattern(replacer.getPattern());
            m.reset(message);
            if (m.find()) {
                Optional<String> replaceResult = Optional.ofNullable(replaceResults.get(replacer));
                if (!replaceResult.isPresent()) {
                    replaceResult = Optional.of(replacer.getResult(target, recipient));
                    replaceResults.put(replacer, replaceResult.get());
                }
                message = m.replaceAll(replaceResult.get());
            }
        }
        return message;
    }

    public static String replace(Player target, Player recipient, String message) {
        final String result = replace(target, recipient, message, Maps.newHashMap());
        if (Dependency.PlaceholderAPI.isPresent()) {
            return PlaceholderAPI.setPlaceholders(target, result);
        }
        return result;
    }

    public void reset() {
        replacerList.clear();
    }

    public static class Defaults {
        private static Class<?> craftPlayer;

        static {
            String bukkitVersion = Bukkit.getServer().getClass().getPackage().getName()
                    .substring(23);
            try {
                craftPlayer = Class
                        .forName("org.bukkit.craftbukkit." + bukkitVersion + ".entity.CraftPlayer");
            } catch (ClassNotFoundException e) {
                ViewItPlugin.getInstance().getLogger()
                        .warning("Could not obtain CraftPlayer class for ping checking.");
            }
        }

        public static Replacer PING = Replacer.of("ping", Defaults::getPing);
        public static Replacer NAME = Replacer.of("name", Player::getName);

        private static String getPing(Player player) {
            int ping = _getPing(player);
            String result = "";
            if (ping < 100) {
                result += ChatColor.GREEN;
            } else if (ping < 250) {
                result += ChatColor.YELLOW;
            } else {
                result += ChatColor.RED;
            }
            return result + ping;
        }

        private static int _getPing(Player player) {
            try {
                Object handle = craftPlayer.getMethod("getHandle").invoke(player);
                return (Integer) handle.getClass().getDeclaredField("ping").get(handle);
            } catch (Exception e) {
                return -1;
            }
        }

    }

}
