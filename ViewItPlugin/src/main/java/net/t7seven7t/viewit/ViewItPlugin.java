/**
 * ViewIt, a resource for displaying information on Bukkit servers.
 *
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

import net.t7seven7t.viewit.command.CommandsManager;
import net.t7seven7t.viewit.replacer.Replacers;
import net.t7seven7t.viewit.scoreboard.ScoreboardElement;
import net.t7seven7t.viewit.scoreboard.ScoreboardService;
import net.t7seven7t.viewit.supply.FrameSupply;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ViewItPlugin extends JavaPlugin {

    // ViewIt instance
    private static ViewItPlugin instance;
    // Default implementation of ScoreboardService
    private ScoreboardService scoreboardService;
    // Replacers instance
    private Replacers replacers;
    // Commands manager
    private CommandsManager commands;
    // Elements that were defined in the config
    private ConfigElements configElements;

    /**
     * Gets the currently running instance of ViewIt
     *
     * @return ViewIt instance
     */
    public static ViewItPlugin getInstance() {
        return instance;
    }

    /**
     * Gets the default scoreboard service created by ViewIt. For other instances use Bukkit's
     * ServiceManager
     */
    public ScoreboardService getScoreboardService() {
        return scoreboardService;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        ViewItPlugin.instance = this;

        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        saveDefaultConfig();
        reloadConfig();

        replacers = new Replacers();
        scoreboardService = new SimpleScoreboardService(this);
        Bukkit.getServicesManager().register(ScoreboardService.class, scoreboardService, this,
                ServicePriority.Normal);

        if (getConfig().getBoolean("track-players-on-join")) {
            Bukkit.getOnlinePlayers().forEach(scoreboardService::addPlayer);
        }

        configElements = new ConfigElements(this);
        commands = new CommandsManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commands.onCommand(sender, command, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
                                      String[] args) {
        return commands.onTabComplete(sender, command, alias, args);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Bukkit.getOnlinePlayers().forEach(scoreboardService::removePlayer);
        Bukkit.getServicesManager().unregisterAll(this);
        replacers.reset();
    }

    public void reloadConfigElements() {
        ScoreboardElement[] elements = configElements.getElementsMap().values()
                .toArray(new ScoreboardElement[configElements.getElementsMap().size()]);
        Bukkit.getOnlinePlayers()
                .forEach(player -> scoreboardService.removeElements(player, elements));
        reloadConfig();
        configElements.loadFromConfiguration(getConfig());
        Bukkit.getOnlinePlayers().forEach(configElements::addAllToPlayer);
    }

    /**
     * Creates a ScoreboardElement using the default implementation. For more options see {@link
     * ScoreboardElement}
     */
    public ScoreboardElement of(Plugin plugin, int priority, long updateDelay,
                                List<FrameSupply> contents) {
        return new SimpleScoreboardElement(plugin, priority, updateDelay, contents);
    }
}
