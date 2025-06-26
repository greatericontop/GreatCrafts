package io.github.greatericontop.greatcrafts.updatechecker;

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

import io.github.greatericontop.greatcrafts.GreatCrafts;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateCheckerPlayerJoinListener implements Listener {

    private final GreatCrafts plugin;
    public UpdateCheckerPlayerJoinListener(GreatCrafts plugin) {
        this.plugin = plugin;
    }

    @EventHandler()
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("greatcrafts.greatcraftscommand")
                && plugin.latestVersion != null
                && !plugin.latestVersion.equals(plugin.getDescription().getVersion().split("---")[0])
        ) {
            player.sendMessage(String.format("§3An update for GreatCrafts is available! You have §6%s§3, §6%s§3 is available.",
                    plugin.getDescription().getVersion(), plugin.latestVersion));
            player.spigot().sendMessage(new ComponentBuilder("Click here to download").color(ChatColor.AQUA)
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/plugin/greatcrafts"))
                    .build());
        }
    }

}
