package io.github.greatericontop.greatcrafts.internal;

/*
 * Copyright (C) 2024-present greateric.
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

public class ShapeAnalyzer {

    public static String[] shrink(String[] shape) {
        if (shape.length != 3)  throw new IllegalArgumentException("shape.length != 3");
        if (shape[0].length() != 3)  throw new IllegalArgumentException("shape[0].length() != 3");
        if (shape[1].length() != 3)  throw new IllegalArgumentException("shape[1].length() != 3");
        if (shape[2].length() != 3)  throw new IllegalArgumentException("shape[2].length() != 3");
        if (shape[0].equals("   ") && shape[1].equals("   ") && shape[2].equals("   ")) {
            throw new IllegalArgumentException("shape is empty");
        }

        // Shrink vertically
        // This essentially trims all empty strings (since they represent empty rows)
        // A middle row can't be deleted unless the edge row (otherwise you would be collapsing the middle row)
        // Examples: [ABC, DEF, ___] -> [ABC, DEF];    [___, ___, GHI] -> [GHI]
        if (shape[2].equals("   ")) {
            shape = trimRow(shape, 2); // remove last
            if (shape[1].equals("   ")) {
                shape = trimRow(shape, 1); // remove last
            }
        }
        if (shape[0].equals("   ")) {
            shape = trimRow(shape, 0); // remove first
            if (shape[0].equals("   ")) {
                shape = trimRow(shape, 0); // remove first
            }
        }

        // Shrink horizontally
        // Check for (:i: moving backwards) shape[0][i] == shape[1][i] == ... == empty
        // Again can't delete middle without deleting edges first
        // Examples: [AB_, DE_, GH_] -> [AB, DE, GH];    [_A_, __B] -> [A_, _B]
        if (checkColumnEmpty(shape, 2)) {
            trimColumn(shape, 2);
            if (checkColumnEmpty(shape, 1)) {
                trimColumn(shape, 1);
            }
        }
        if (checkColumnEmpty(shape, 0)) {
            trimColumn(shape, 0);
            if (checkColumnEmpty(shape, 0)) {
                trimColumn(shape, 0);
            }
        }

        return shape;
    }

    private static boolean checkColumnEmpty(String[] shape, int col) {
        for (String s : shape) {
            if (s.charAt(col) != ' ') {
                return false;
            }
        }
        return true;
    }

    private static String[] trimRow(String[] shape, int row) {
        // Basically the java equivalent of shape[:row] + shape[row+1:]
        // Note that this is NOT done in-place
        String[] newShape = new String[shape.length - 1];
        if (row >= 0) {
            System.arraycopy(shape, 0, newShape, 0, row);
        }
        if (shape.length - (row + 1) >= 0) {
            System.arraycopy(shape, row + 1, newShape, row, shape.length - row - 1);
        }
        return newShape;
    }

    private static void trimColumn(String[] shape, int col) {
        for (int i = 0; i < shape.length; i++) {
            shape[i] = shape[i].substring(0, col) + shape[i].substring(col + 1);
        }
    }

}
