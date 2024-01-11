package io.github.greatericontop.greatcrafts.gui;

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

        Bukkit.getScheduler().runTaskLater(guiManager.getPlugin(), () -> player.openInventory(lastInventory), 1L);
    }

}
