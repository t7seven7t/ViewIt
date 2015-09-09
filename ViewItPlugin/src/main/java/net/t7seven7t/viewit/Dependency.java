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

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/**
 * A pluguin that ViewIt may depend on in order for part of it to function.
 */
public class Dependency {

    public static final Dependency PlaceholderAPI = new Dependency("PlaceholderAPI");
    private final String name;

    public Dependency(String name) {
        this.name = name;
    }

    /**
     * Gets the plugin name
     *
     * @return plugin name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the plugin if its loaded
     *
     * @return Optional containing the plugin if it is loaded otherwise Optional.empty()
     */
    public Optional<Plugin> getPlugin() {
        return Optional.ofNullable(Bukkit.getPluginManager().getPlugin(getName()));
    }

    /**
     * Checks whether the plugin is loaded
     *
     * @return true if the plugin is loaded, otherwise false
     */
    public boolean isPresent() {
        return getPlugin().isPresent();
    }
}
