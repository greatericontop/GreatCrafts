package io.github.greatericontop.greatcrafts.commands;

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

/*
 * Supposed to be a command helper, it's kind of general so you can steal this if you want (just follow GPL)
 * It is kind of quirky though, so see the other command handlers for examples
 */

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

import javax.annotation.Nullable;
import java.util.Arrays;

public class GreatCommands {

    public static @Nullable String argumentString(int i, String[] args) {
        if (args.length <= i) {
            return null;
        }
        return args[i];
    }

    public static @Nullable String argumentStringFromChoices(int i, String[] args, String[] choices) {
        if (args.length <= i) {
            return null;
        }
        for (String choice : choices) {
            if (args[i].equals(choice)) {
                return choice;
            }
        }
        return null;
    }

    public static @Nullable String argumentStringConsumeRest(int iStart, String[] args) {
        if (args.length <= iStart) {
            return null;
        }
        return String.join(" ", Arrays.copyOfRange(args, iStart, args.length));
    }

    public static @Nullable Integer argumentInteger(int i, String[] args) {
        if (args.length <= i) {
            return null;
        }
        try {
            return Integer.parseInt(args[i]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static @Nullable int[] argumentIntegerConsumeRest(int iStart, String[] args) {
        if (args.length <= iStart) {
            return null;
        }
        int[] result = new int[args.length - iStart];
        for (int argIndex = iStart; argIndex < args.length; argIndex++) {
            try {
                result[argIndex - iStart] = Integer.parseInt(args[argIndex]);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return result;
    }

    public static @Nullable Enchantment argumentMinecraftEnchantment(int i, String[] args) {
        if (args.length <= i) {
            return null;
        }
        try {
            return Enchantment.getByKey(new NamespacedKey("minecraft", args[i]));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
