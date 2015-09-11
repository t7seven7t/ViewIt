/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 t7seven7t
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.t7seven7t.viewittest;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.t7seven7t.viewit.scoreboard.ScoreboardElement;
import static net.t7seven7t.viewit.scoreboard.ScoreboardElement.Priority.*;
import net.t7seven7t.viewit.scoreboard.ScoreboardService;
import net.t7seven7t.viewit.supply.FrameSupply;
import net.t7seven7t.viewit.supply.Supply;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.NumberConversions;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Test cases: <UL> <li>Adding element to scoreboard</li> <li>Removing elements from scoreboard (and
 * they disappear)</li> <li>No elements on scoreboard</li> <li>Multiple titles and priorities of
 * each</li> <li>Modifying priorities of elements</li> <li>Adding lines to elements (insert and &gt;
 * getSize())</li> <li>Removing lines from elements</li> <li>Element removal on plugin disable</li>
 * <li>Multiple players with my code for teams (only using a single color cuz scoreboards are
 * separate)</li>
 *
 * </UL>
 */
public class ViewItTest extends JavaPlugin {

    public static final Map<String, ScoreboardElement> elements = Maps.newHashMap();
    private final List<String> addedElements = Lists.newArrayList();

    double money = 0;
    Random random = new Random();

    @Override
    public void onEnable() {
        elements.put("TITLE1", ScoreboardElement
                .of(this, NORMAL(0).title(), 20L, Supply.of("&4Boring ol' title", "&2Boring ol' title")));
        elements.put("TITLE2",
                ScoreboardElement.of(this, HIGH(0).title(), 60L, Supply.of("&3Weeeeee", "&5Weeeeee")));
        elements.put("E1", ScoreboardElement
                .of(this, HIGH(0), Supply.of("&6E1: Line 1"), Supply.of("&3E1: Line 2")));
        elements.put("E2", ScoreboardElement.of(this, HIGH(10),
                Supply.of("&9Really long line like this ya know yeahhhh")));
        elements.put("E3", ScoreboardElement
                .of(this, HIGH(20), Supply.of("abc"), Supply.of("def"), Supply.of("ghi"),
                        Supply.of("jkl")));
        elements.put("SPACER", ScoreboardElement.of(this, HIGH(30), Long.MAX_VALUE, Supply.of("")));
        elements.put("E4",
                ScoreboardElement.of(this, HIGH(40), Supply.of("&bMoney:"), p -> "&e$" + money));
        elements.put("E5", ScoreboardElement
                .of(this, HIGH(50), 20L, IntStream.range(1, 10).mapToObj(this::supp).collect(
                        Collectors.toList())));
        elements.put("E6", ScoreboardElement
                .of(this, HIGH(60), 15L, Supply.of(rand("%name%"), rand("%name%"), rand("%name%"))));
        elements.put("E7", ScoreboardElement.of(this, HIGH(55), 30L, Supply.of("%ping%")));
    }

    @Override
    public void onDisable() {
        elements.clear();
    }

    FrameSupply supp(int i) {
        return Supply.of(rand(i), rand(i), rand(i), rand(i), rand(i));
    }

    String rand(Object o) {
        return ChatColor.values()[random.nextInt(12)].toString() + o;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 2 || !Player.class.isInstance(sender)) {
            return false;
        }

        Player p = (Player) sender;
        ScoreboardService ss = ScoreboardService.getInstance();
        ScoreboardElement element = elements.get(args[1]);
        if (element == null) {
            p.sendMessage(ChatColor.RED + "Bad element");
            p.sendMessage(getElements());
            return true;
        }

        if (args[0].equalsIgnoreCase("add")) {
            ss.addElement(p, element);
            addedElements.add(args[1]);
        } else if (args[0].equalsIgnoreCase("remove")) {
            ss.removeElement(p, element);
            addedElements.remove(args[1]);
        } else if (args[0].equalsIgnoreCase("priority")) {
            if (args.length != 3) {
                p.sendMessage("arg length");
                return false;
            }

            element.setPriority(NumberConversions.toInt(args[2]));
        } else if (args[0].equalsIgnoreCase("setline")) {
            if (args.length < 3) {
                p.sendMessage("arg length");
                return false;
            }
            if (args.length == 3) {
                try {
                    element.setLine(NumberConversions.toInt(args[2]), null);
                } catch (IndexOutOfBoundsException e) {
                    p.sendMessage(e.getMessage());
                }
            } else {
                element.setLine(NumberConversions.toInt(args[2]), Supply.of(Joiner.on(" ").join(
                        Arrays.copyOfRange(args, 3, args.length))));
            }
        }

        p.sendMessage(Joiner.on(' ').join(addedElements));
        return true;
    }

    private String getElements() {
        return Joiner.on(", ").join(elements.keySet());
    }
}
