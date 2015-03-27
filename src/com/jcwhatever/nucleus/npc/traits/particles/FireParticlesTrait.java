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

/**
 * Fire effect.
 */
public class FireParticlesTrait extends NpcTraitType {

    /**
     * Constructor.
     */
    public FireParticlesTrait() {
        super(NpcTraitPack.getPlugin(), "FireParticles");
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new FireParticles(this);
    }

    public static class FireParticles extends ParticlesTrait {

        /**
         * Constructor.
         *
         * @param type The parent type that instantiated the trait.
         */
        protected FireParticles(NpcTraitType type) {
            super(type);
        }

        @Override
        protected void onEffect(Location location) {
            location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
        }
    }
}
