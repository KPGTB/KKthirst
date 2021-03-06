/*
 * Copyright 2022 KPG-TB
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package io.github.kpgtb.kkthirst.listener;

import io.github.kpgtb.kkcore.manager.DataManager;
import io.github.kpgtb.kkcore.manager.UsefulObjects;
import io.github.kpgtb.kkthirst.object.ThirstUsefulObjects;
import io.github.kpgtb.kkthirst.object.User;
import io.github.kpgtb.kkthirst.manager.UserManager;
import io.github.kpgtb.kkui.KKui;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinListener implements Listener {
    private final DataManager dataManager;
    private final FileConfiguration config;
    private final UserManager userManager;

    public JoinListener(UsefulObjects usefulObjects){
        ThirstUsefulObjects thirstUsefulObjects = null;
        try {
            thirstUsefulObjects = (ThirstUsefulObjects) usefulObjects;
        } catch(ClassCastException e) {
            System.out.println("KKthirst >> Error while creating JoinListener!");
            Bukkit.shutdown();
        }

        this.dataManager = thirstUsefulObjects.getDataManager();
        this.config = thirstUsefulObjects.getConfig();
        this.userManager = thirstUsefulObjects.getUserManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if(userManager.hasUser(uuid)) {
            KKui.getUiManager().removeUI(uuid, userManager.getUser(uuid).getBaseUI());
            userManager.removeUser(uuid);
        }

        double maxThirst = config.getDouble("maxThirst");
        // If user do not exists in database
        if(!dataManager.getKeys("users").contains(uuid.toString())) {
            dataManager.set("users", uuid.toString(), "thirst", maxThirst);
        }

        double playerThirst = (double) dataManager.get("users", uuid.toString(), "thirst");
        User user = new User(uuid, playerThirst, maxThirst,dataManager, config.getInt("uiOffset"));

        userManager.addUser(uuid, user);

        KKui.getUiManager().addUI(uuid, user.getBaseUI());
    }
}
