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

import java.util.Collection;

import com.jcwhatever.nucleus.npc.traits.nms.INmsAnimation;
import com.jcwhatever.nucleus.utils.PreCon;

import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Animation Handler for v1_8_R3
 */
public class AnimationHandler_v1_8_R3 implements INmsAnimation {

    @Override
    public void sendAnimation(Player recipient, Entity animated, AnimationType type) {
        PreCon.notNull(recipient);
        PreCon.notNull(animated);
        PreCon.notNull(type);

        int packetType = getPacketType(type);
        if (packetType == -1)
            return;

        PacketPlayOutAnimation packet = new PacketPlayOutAnimation(((CraftEntity)animated).getHandle(), packetType);
        ((CraftPlayer) recipient).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public void sendAnimation(Collection<? extends Player> recipients, Entity animated, AnimationType type) {
        PreCon.notNull(recipients);
        PreCon.notNull(animated);
        PreCon.notNull(type);

        int packetType = getPacketType(type);
        if (packetType == -1)
            return;

        PacketPlayOutAnimation packet = new PacketPlayOutAnimation(
                ((CraftEntity)animated).getHandle(), packetType);

        for (Player player : recipients) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    private int getPacketType(AnimationType type) {
        switch (type) {
            case ARM_SWING:
                return 0;
            case TAKE_DAMAGE:
                return 1;
            case LEAVE_BED:
                return 2;
            case EAT_FOOD:
                return 3;
            case CRIT_EFFECT:
                return 4;
            case MAGIC_CRIT_EFFECT:
                return 5;
            default:
                return -1;
        }
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
