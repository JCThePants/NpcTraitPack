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
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

/**
 * Smoke particles effect.
 */
public class SmokeParticlesTrait extends NpcTraitType {

    @Override
    public Plugin getPlugin() {
        return NpcTraitPack.getPlugin();
    }

    @Override
    public String getName() {
        return "SmokeParticles";
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new SmokeParticles(npc, this);
    }

    public static class SmokeParticles extends ParticlesTrait {

        private int _density = 5;

        /**
         * Constructor.
         *
         * @param npc  The NPC the trait is for.
         * @param type The parent type that instantiated the trait.
         */
        protected SmokeParticles(INpc npc, NpcTraitType type) {
            super(npc, type);
        }

        /**
         * Get the density of the smoke.
         *
         * <p>Default value is 5.</p>
         */
        public int getDensity() {
            return _density;
        }

        /**
         * Set the density of the smoke.
         *
         * @param density  The density value. Must be greater than 0.
         *
         * @return  Self for chaining.
         */
        public SmokeParticles setDensity(int density) {
            PreCon.greaterThanZero(density);

            _density = density;

            return this;
        }

        @Override
        protected void onEffect(Location location) {
            for (int i=0; i <= _density; i++) {
                location.getWorld().playEffect(location, Effect.SMOKE, i);
            }
        }
    }
}
