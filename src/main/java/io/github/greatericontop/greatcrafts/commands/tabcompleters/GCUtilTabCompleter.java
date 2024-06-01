package io.github.greatericontop.greatcrafts.commands.tabcompleters;

/*
 * Copyright (C) 2023-present greateric.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty  of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class GCUtilTabCompleter implements TabCompleter {

    private List<String> enchantments = new ArrayList<>();

    public GCUtilTabCompleter() {
        for (Enchantment enchantment : Enchantment.values()) {
            enchantments.add(enchantment.getKey().getKey());
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> completions = List.of("setcustomname", "setloreline", "deletelorelines", "enchant");
            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>(completions.size()));
        }

        if (args[0].equalsIgnoreCase("setcustomname")) {
            return List.of("<name...>");
        }

        if (args[0].equalsIgnoreCase("setloreline")) {
            if (args.length == 2) {
                return List.of("<line #>");
            } else {
                return List.of("[<lore line...>]");
            }
        }

        if (args[0].equalsIgnoreCase("deletelorelines")) {
            if (args.length == 2) {
                return List.of("<line #>");
            } else {
                return List.of("[<line #>]");
            }
        }

        if (args[0].equalsIgnoreCase("enchant")) {
            if (args.length == 2) {
                return StringUtil.copyPartialMatches(args[1], enchantments, new ArrayList<>(enchantments.size()));
            } else if (args.length == 3) {
                return List.of("<level>");
            }
        }

        return null;
    }

}
