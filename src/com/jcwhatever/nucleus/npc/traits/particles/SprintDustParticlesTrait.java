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
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

/**
 * Sprinting particles effect.
 */
public class SprintDustParticlesTrait extends NpcTraitType {

    @Override
    public Plugin getPlugin() {
        return NpcTraitPack.getPlugin();
    }

    @Override
    public String getName() {
        return "SprintDustParticles";
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new SprintDustParticles(npc, this);
    }

    public static class SprintDustParticles extends ParticlesTrait {

        private int _material;

        /**
         * Constructor.
         *
         * @param npc  The NPC the trait is for.
         * @param type The parent type that instantiated the trait.
         */
        protected SprintDustParticles(INpc npc, NpcTraitType type) {
            super(npc, type);
        }

        /**
         * Get the material of the particles.
         */
        public Material getMaterial() {
            return Material.getMaterial(_material);
        }

        /**
         * Set the material of the particles.
         *
         * @param material  The {@link org.bukkit.Material} or material name.
         *
         * @return  Self for chaining.
         */
        public SprintDustParticles setMaterial(Object material) {
            Material mat = getEnum(material, Material.class);
            _material = mat.getId();
            return this;
        }

        @Override
        protected void onEffect(Location location) {
            location.getWorld().playEffect(location, Effect.TILE_DUST, _material);
        }
    }
}
