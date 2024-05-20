package io.github.greatericontop.greatcrafts.events;

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

import io.github.greatericontop.greatcrafts.gui.GUIManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class InventoryCloseListener implements Listener {

    private final GUIManager guiManager;
    public InventoryCloseListener(GUIManager guiManager) {
        this.guiManager = guiManager;
    }

    @EventHandler()
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory lastInventory = guiManager.playerMainInventories.get(player.getUniqueId());
        if (lastInventory == null)  return;

        // Check if trying to close out of the main inventory
        if (event.getInventory().equals(lastInventory)) {
            player.sendMessage("§cDiscard or save your changes first!");
        }

        Bukkit.getScheduler().runTaskLater(guiManager.getPlugin(), () -> player.openInventory(lastInventory), 1L);
    }

}
