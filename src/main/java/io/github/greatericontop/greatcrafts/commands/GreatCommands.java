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

import javax.annotation.Nullable;
import java.util.Arrays;

public class GreatCommands {

    public static @Nullable String argumentString(int i, String[] args) {
        if (args.length <= i) {
            return null;
        }
        return args[i];
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

}
