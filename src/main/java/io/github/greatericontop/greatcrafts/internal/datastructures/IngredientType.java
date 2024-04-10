package io.github.greatericontop.greatcrafts.internal.datastructures;

public enum IngredientType {
    NORMAL,
    EXACT_CHOICE,
    MATERIAL_CHOICE,

    ;

    public static IngredientType[] defaults() {
        return new IngredientType[] {
                NORMAL, NORMAL, NORMAL,
                NORMAL, NORMAL, NORMAL,
                NORMAL, NORMAL, NORMAL
        };
    }

}
