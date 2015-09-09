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

import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.Optional;

import net.t7seven7t.viewit.command.annotation.Sender;
import net.t7seven7t.viewit.scoreboard.ScoreboardService;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 */
class ScoreboardCommands {

    @Command(
            aliases = {"toggle"},
            usage = "[on/off] - Change the visibility of your scoreboard",
            desc = "Toggles your scoreboard's visibility",
            max = 1
    )
    @Require("viewit.scoreboard.toggle")
    public void toggle(ScoreboardService scoreboardService, @Sender Player player,
                       @Optional String arg) {
        boolean visible;
        if (arg == null) {
            visible = !scoreboardService.isVisible(player);
        } else {
            visible = arg.matches("^(?i)on|true|enable|yes$");
        }
        scoreboardService.setVisibility(player, visible);
        player.sendMessage(
                ChatColor.GOLD + "Your scoreboard will now be " + (visible ? "shown" : "hidden"));
    }

}
