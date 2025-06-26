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

import com.google.gson.Gson;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UpdateChecker {

    @Nullable
    public static String getLatestVersion(Plugin plugin) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://api.modrinth.com/v2/project/greatcrafts/version"))
                .header("User-Agent", "greatericontop/GreatCrafts")
                .GET()
                .build();
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            plugin.getLogger().warning("Update checker failed: " + e.getMessage());
            return null;
        }
        if (response.statusCode() != 200) {
            plugin.getLogger().warning("Update checker failed, HTTP status code " + response.statusCode());
            plugin.getLogger().warning(response.body());
            return null;
        }
        Gson gson = new Gson();
        ModrinthVersion[] versions = gson.fromJson(response.body(), ModrinthVersion[].class);
        return versions[0].version_number().split("---")[0]; // e.g. "1.2.3---1.19-1.20 -> 1.2.3"
    }

}
