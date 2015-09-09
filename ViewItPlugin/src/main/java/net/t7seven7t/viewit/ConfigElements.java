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
import com.google.common.collect.Maps;

import net.t7seven7t.viewit.scoreboard.ScoreboardElement;
import net.t7seven7t.viewit.scoreboard.ScoreboardService;
import net.t7seven7t.viewit.supply.FrameSupply;
import net.t7seven7t.viewit.supply.Supply;

import org.bukkit.Bukkit;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Used for loading elements from the config into the game and displaying them to players
 */
public class ConfigElements implements Listener {

    private final Map<String, ScoreboardElement> elementsMap;
    private final Plugin plugin;

    public ConfigElements(Plugin plugin) {
        this.elementsMap = Maps.newHashMap();
        this.plugin = plugin;
        loadFromConfiguration(plugin.getConfig());
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getOnlinePlayers().forEach(this::addAllToPlayer);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (plugin.getConfig().isBoolean("track-players-on-join")) {
            addAllToPlayer(event.getPlayer());
        }
    }

    public void addAllToPlayer(Player player) {
        ScoreboardService.getInstance().addElements(player,
                elementsMap.values().toArray(new ScoreboardElement[elementsMap.size()]));
    }

    public void loadFromConfiguration(Configuration config) {
        if (!config.isConfigurationSection("scoreboard-elements")) {
            return;
        }

        ConfigurationSection elementsRoot = config.getConfigurationSection("scoreboard-elements");
        String textPath, priorityPath, delayPath;

        for (String child : elementsRoot.getKeys(false)) {
            if (!elementsRoot.isConfigurationSection(child)) {
                continue;
            }

            textPath = child + ".text";
            priorityPath = child + ".priority";
            delayPath = child + ".delay";

            if (!elementsRoot.isInt(priorityPath)) {
                plugin.getLogger().severe(
                        "Scoreboard element '" + child + "' in config has no priority property");
                continue;
            } else if (!elementsRoot.contains(textPath)) {
                plugin.getLogger().severe(
                        "Scoreboard element '" + child + "' in config has no text property");
                continue;
            }

            int priority = elementsRoot.getInt(priorityPath);
            long delay = elementsRoot.getLong(delayPath, -1);
            List<FrameSupply> supplyList = Lists.newArrayList();

            if (elementsRoot.isConfigurationSection(textPath)) {
                for (String line : elementsRoot.getConfigurationSection(textPath).getKeys(false)) {
                    String linePath = textPath + "." + line;
                    addSupply(supplyList, elementsRoot, linePath);
                }
            } else {
                addSupply(supplyList, elementsRoot, textPath);
            }

            if (supplyList.isEmpty()) {
                plugin.getLogger().severe("Scoreboard element '" + child
                        + "' text property is incorrectly defined");
                continue;
            }

            elementsMap.put(child, ScoreboardElement.of(plugin, priority, delay, supplyList));
        }
    }

    /**
     * Attempt to add a supply to a list if the config value at the path is a valid string or string
     * list
     *
     * @param list   supply list to add to
     * @param config config to read from
     * @param path   path of the value in the config
     */
    private void addSupply(List<FrameSupply> list, ConfigurationSection config, String path) {
        if (config.isString(path)) {
            list.add(Supply.of(config.getString(path)));
        } else if (config.isList(path)) {
            list.add(Supply.of(config.getStringList(path).toArray(new String[0])));
        }
    }
}
