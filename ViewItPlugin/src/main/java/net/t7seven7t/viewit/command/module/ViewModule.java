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
package net.t7seven7t.viewit.command.module;

import com.sk89q.intake.parametric.AbstractModule;

import net.t7seven7t.viewit.ViewItPlugin;
import net.t7seven7t.viewit.command.module.provider.CommandSenderProvider;
import net.t7seven7t.viewit.scoreboard.ScoreboardService;

import org.bukkit.command.CommandSender;

/**
 *
 */
public class ViewModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ScoreboardService.class).toInstance(ScoreboardService.getInstance());
        bind(ViewItPlugin.class).toInstance(ViewItPlugin.getInstance());
        bind(CommandSender.class).toProvider(new CommandSenderProvider());
    }
}
