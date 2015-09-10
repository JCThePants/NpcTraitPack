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

package com.jcwhatever.nucleus.npc.traits.nms;

import java.util.Collection;

import com.jcwhatever.nucleus.utils.nms.INmsHandler;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Interface for an entity animation handler.
 */
public interface INmsAnimation extends INmsHandler {

    /**
     * Send animation packet.
     *
     * @param recipient  The packet recipient.
     * @param animated   The entity being animated.
     * @param type       The animation type.
     */
    void sendAnimation(Player recipient, Entity animated, AnimationType type);

    /**
     * Send animation packet to multiple recipients.
     *
     * @param recipients  The packet recipients.
     * @param animated    The entity being animated.
     * @param type        The animation type.
     */
    void sendAnimation(Collection<? extends Player> recipients, Entity animated, AnimationType type);

    enum AnimationType {
        ARM_SWING,
        TAKE_DAMAGE,
        LEAVE_BED,
        EAT_FOOD,
        CRIT_EFFECT,
        MAGIC_CRIT_EFFECT
    }
}
