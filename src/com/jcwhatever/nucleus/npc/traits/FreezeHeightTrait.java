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

package com.jcwhatever.nucleus.npc.traits;

import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.events.NpcSpawnEvent.NpcSpawnReason;
import com.jcwhatever.nucleus.providers.npc.traits.NpcRunnableTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

/**
 * Causes the NPC's Y coordinate position to be frozen.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:FreezeHeight"</p>
 */
public class FreezeHeightTrait extends NpcTraitType {

    private static final String NAME = "FreezeHeight";

    /**
     * Constructor.
     */
    public FreezeHeightTrait() {
        super(NpcTraitPack.getPlugin(), NAME);
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new FreezeHeight(this);
    }

    public static class FreezeHeight extends NpcRunnableTrait {

        private double _y;

        /**
         * Constructor.
         *
         * @param type The parent type that instantiated the trait.
         */
        FreezeHeight(NpcTraitType type) {
            super(type);
        }

        @Override
        protected void onAttach(INpc npc) {
            setY();
        }

        @Override
        protected void onSpawn(NpcSpawnReason reason) {
            setY();
        }

        @Override
        protected void onRun() {

            Entity entity = getNpc().getEntity();
            assert entity != null;

            Vector vector = entity.getVelocity();

            Location location = getNpc().getLocation();
            assert location != null;

            if (Double.compare(location.getY(), _y) < 0) {
                vector.setY(0.20);
            }
            else {
                vector.setY(0);
            }

            entity.setVelocity(vector);
        }

        @Override
        protected void onEnable() {
            setY();
        }

        private void setY() {
            if (getNpc().isSpawned()) {
                Location location = getNpc().getLocation();
                assert location != null;

                _y = location.getY();
            }
        }
    }
}
