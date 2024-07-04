package io.github.greatericontop.greatcrafts.internal.datastructures;

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

import javax.annotation.Nullable;

public enum AutoUnlockSetting {
    NEVER,
    EACH,
    ONE,
    ALWAYS,

    ;

    public static @Nullable AutoUnlockSetting fromConfig(String s) {
        return switch (s) {
            case "never" -> NEVER;
            case "have-each" -> EACH;
            case "have-one" -> ONE;
            case "always" -> ALWAYS;
            default -> null;
        };
    }
}
