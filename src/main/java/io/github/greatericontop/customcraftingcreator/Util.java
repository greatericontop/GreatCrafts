package io.github.greatericontop.customcraftingcreator;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Util {

    public static ItemStack createItemStack(Material mat,int amount, String name, String... lore) {
        ItemStack stack = new ItemStack(mat, amount);
        ItemMeta im = stack.getItemMeta();
        im.setDisplayName(name);
        im.setLore(java.util.Arrays.asList(lore));
        stack.setItemMeta(im);
        return stack;
    }

    public static void successSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1L, 1L);
    }

}
