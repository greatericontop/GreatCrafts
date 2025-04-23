package org.bukkit.block;

import org.bukkit.loot.Lootable;

public interface Crafter extends Container, Lootable {

    int getCraftingTicks();

    boolean isSlotDisabled(int slot);

    boolean isTriggered();

    void setCraftingTicks(int ticks);

    void setSlotDisabled(int slot, boolean disabled);

    void setTriggered(boolean triggered);

}
