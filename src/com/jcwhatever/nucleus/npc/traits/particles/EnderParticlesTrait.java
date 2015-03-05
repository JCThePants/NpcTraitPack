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

package com.jcwhatever.nucleus.npc.traits.particles;

import com.jcwhatever.nucleus.npc.traits.NpcTraitPack;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

/**
 * Ender particle effect.
 */
public class EnderParticlesTrait extends NpcTraitType {

    @Override
    public Plugin getPlugin() {
        return NpcTraitPack.getPlugin();
    }

    @Override
    public String getName() {
        return "EnderParticles";
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new EnderParticles(npc, this);
    }

    public static class EnderParticles extends ParticlesTrait {

        /**
         * Constructor.
         *
         * @param npc  The NPC the trait is for.
         * @param type The parent type that instantiated the trait.
         */
        protected EnderParticles(INpc npc, NpcTraitType type) {
            super(npc, type);
        }

        @Override
        protected void onEffect(Location location) {
            location.getWorld().playEffect(location, Effect.ENDER_SIGNAL, 0);
        }
    }
}