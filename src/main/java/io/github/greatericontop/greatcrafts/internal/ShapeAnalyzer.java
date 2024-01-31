package io.github.greatericontop.greatcrafts.internal;

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
            shape = new String[]{shape[0], shape[1]};
            if (shape[1].equals("   ")) {
                shape = new String[]{shape[0]};
            }
        }
        if (shape[0].equals("   ")) {
            shape = new String[]{shape[1], shape[2]};
            // the middle row is now shape[0]
            if (shape[0].equals("   ")) {
                shape = new String[]{shape[1]};
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
            // again, middle is now 0
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

    private static void trimColumn(String[] shape, int col) {
        for (int i = 0; i < shape.length; i++) {
            shape[i] = shape[i].substring(0, col) + shape[i].substring(col + 1);
        }
    }

}
