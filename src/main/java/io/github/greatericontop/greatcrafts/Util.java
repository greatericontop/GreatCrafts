package io.github.greatericontop.greatcrafts;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {

    public static ItemStack createItemStack(Material mat, int amount, String name, String... lore) {
        ItemStack stack = new ItemStack(mat, amount);
        ItemMeta im = stack.getItemMeta();
        im.setDisplayName(name);
        im.setLore(java.util.Arrays.asList(lore));
        stack.setItemMeta(im);
        return stack;
    }

    public static void appendLore(ItemStack stack, String... moreLore) {
        ItemMeta im = stack.getItemMeta();
        List<String> lore = im.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.addAll(Arrays.asList(moreLore));
        im.setLore(lore);
        stack.setItemMeta(im);
    }

    public static void successSound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1L, 1L);
    }

    public static List<List<Material>> defaultMaterialChoiceExtra() {
        List<List<Material>> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.add(new ArrayList<>());
        }
        return list;
    }

}
