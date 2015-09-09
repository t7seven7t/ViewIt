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
package net.t7seven7t.viewit.command;

import com.google.common.base.Joiner;

import com.sk89q.intake.CommandCallable;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Intake;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.fluent.CommandGraph;
import com.sk89q.intake.parametric.Injector;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.parametric.provider.PrimitivesModule;
import com.sk89q.intake.util.auth.AuthorizationException;

import net.t7seven7t.viewit.command.module.PlayerModule;
import net.t7seven7t.viewit.command.module.ViewModule;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class CommandsManager implements TabExecutor {

    // Joiner that joins on space character
    private static final Joiner SPACE_JOINER = Joiner.on(" ");
    // Just the one, add more groups to builder for additional commands
    private CommandCallable dispatcher;

    public CommandsManager() {
        setupCommands();
    }

    public void setupCommands() {
        Injector injector = Intake.createInjector();
        injector.install(new PrimitivesModule());
        injector.install(new PlayerModule());
        injector.install(new ViewModule());

        ParametricBuilder builder = new ParametricBuilder(injector);
        builder.setAuthorizer((namespace, permission) -> namespace.get(CommandSender.class)
                .hasPermission(permission));

        dispatcher = new CommandGraph()
                .builder(builder)
                .commands()
                .group("scoreboard")
                .registerMethods(new ScoreboardCommands())
                .parent()
                .graph()
                .getDispatcher();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Namespace namespace = new Namespace();
        namespace.put(CommandSender.class, sender);
        try {
            dispatcher.call(joinCommandArgs(command.getName(), args), namespace,
                    Collections.<String>emptyList());
        } catch (AuthorizationException e) {
            sender.sendMessage(ChatColor.RED + "I'm sorry " + sender
                    .getName() + ", I'm afraid I can't do that.");
        } catch (InvocationCommandException e) {
            if (e.getCause() instanceof NumberFormatException) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            } else {
                sender.sendMessage(ChatColor.RED + "An error ocurred. See console.");
                e.printStackTrace();
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias,
                                      String[] args) {
        Namespace namespace = new Namespace();
        namespace.put(CommandSender.class, sender);

        try {
            return dispatcher.getSuggestions(joinCommandArgs(command.getName(), args), namespace);
        } catch (CommandException e) {
            // o:
        }

        return Collections.emptyList();
    }

    private String joinCommandArgs(String command, String[] args) {
        return command + " " + SPACE_JOINER.join(args);
    }
}
