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

import io.github.greatericontop.greatcrafts.GreatCrafts;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EditRecipeTabCompleter implements TabCompleter {

    private final GreatCrafts plugin;
    public EditRecipeTabCompleter(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            // Same as ViewRecipeTabCompleter
            Set<String> keys = plugin.recipes.getKeys(false);
            return StringUtil.copyPartialMatches(args[0], keys, new ArrayList<String>(keys.size()));
        }
        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1], List.of("auto-unlock-setting"), new ArrayList<String>(1));
        }
        if (args.length == 3) {
            if (args[1].equals("auto-unlock-setting")) {
                return StringUtil.copyPartialMatches(args[2], List.of("never", "have-each", "have-one", "always"), new ArrayList<String>(4));
            }
        }
        return null;
    }


}
