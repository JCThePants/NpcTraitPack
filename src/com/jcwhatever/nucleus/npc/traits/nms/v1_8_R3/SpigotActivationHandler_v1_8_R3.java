/*
 * This file is part of NpcTraitPack for NucleusFramework, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.jcwhatever.nucleus.npc.traits.nms.v1_8_R3;

import com.jcwhatever.nucleus.npc.traits.nms.INmsSpigotActivationHandler;
import com.jcwhatever.nucleus.utils.PreCon;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;

/**
 * Spigot activation handler for v1_8_R3
 */
public class SpigotActivationHandler_v1_8_R3 implements INmsSpigotActivationHandler {

    private boolean _isAvailable;

    public SpigotActivationHandler_v1_8_R3() {
        _isAvailable = Bukkit.getServer().toString().toLowerCase().contains("spigot");
    }

    @Override
    public boolean isAvailable() {
        return _isAvailable;
    }

    @Override
    public void activateEntity(Entity entity) {
        PreCon.notNull(entity);

        if (!isAvailable())
            return;

        if (!(entity instanceof CraftEntity))
            return;

        CraftEntity craftEntity = (CraftEntity)entity;

        craftEntity.getHandle().activatedTick = MinecraftServer.currentTick;
    }
}
