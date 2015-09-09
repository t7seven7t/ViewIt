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
package net.t7seven7t.viewit.command.module.provider;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 *
 */
public class PlayerArgumentProvider implements Provider<Player> {

    @Override
    public boolean isProvided() {
        return false;
    }

    @Nullable
    @Override
    public Player get(CommandArgs arguments,
                      List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        String name = arguments.next();
        Player player = Bukkit.getPlayer(name);
        if (player != null) {
            return player;
        } else {
            throw new ArgumentParseException("No player by the name of '" + name + "' is known!");
        }
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        return Bukkit.getOnlinePlayers().stream().filter(
                player -> player.getName().startsWith(prefix)).map(Player::getName)
                .collect(Collectors.toList());
    }
}
