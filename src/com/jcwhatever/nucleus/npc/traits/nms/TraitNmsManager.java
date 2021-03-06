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

import com.jcwhatever.nucleus.npc.traits.NpcTraitPack;
import com.jcwhatever.nucleus.npc.traits.nms.v1_8_R2.SpigotActivationHandler_v1_8_R2;
import com.jcwhatever.nucleus.npc.traits.nms.v1_8_R3.AnimationHandler_v1_8_R3;
import com.jcwhatever.nucleus.npc.traits.nms.v1_8_R3.SpigotActivationHandler_v1_8_R3;
import com.jcwhatever.nucleus.utils.nms.NmsManager;

/**
 * Nms manager.
 */
public class TraitNmsManager extends NmsManager {

    public TraitNmsManager() {
        super(NpcTraitPack.getPlugin());

        registerHandler("v1_8_R2", "SPIGOT_ACTIVATION", SpigotActivationHandler_v1_8_R2.class);

        registerHandler("v1_8_R3", "SPIGOT_ACTIVATION", SpigotActivationHandler_v1_8_R3.class);
        registerHandler("v1_8_R3", "ENTITY_ANIMATION", AnimationHandler_v1_8_R3.class);
    }
}
