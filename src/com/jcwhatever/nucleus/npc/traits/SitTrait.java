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
import com.jcwhatever.nucleus.providers.npc.events.NpcDespawnEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcSpawnEvent;
import com.jcwhatever.nucleus.providers.npc.traits.NpcRunnableTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

/*
 * 
 */
public class SitTrait extends NpcTraitType {

    private static final String NAME = "Sit";

    /**
     * Constructor.
     */
    public SitTrait() {
        super(NpcTraitPack.getPlugin(), NAME);
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new Sit(this);
    }

    public static class Sit extends NpcRunnableTrait {

        private static final Location CHAIR_LOCATION = new Location(null, 0, 0, 0);
        private static final Vector ZERO_VELOCITY = new Vector();

        private Entity _chair;

        /**
         * Constructor.
         *
         * @param type  The parent type that instantiated the trait.
         */
        public Sit(NpcTraitType type) {
            super(type);
        }

        @Override
        protected void onRun() {
            if (_chair == null)
                return;

            if (_chair.isValid()) {
                _chair.setVelocity(ZERO_VELOCITY);
            } else {
                sit();
            }
        }

        @Override
        protected void onAttach(INpc npc) {
            setInterval(1);
            if (isEnabled())
                sit();
        }

        @Override
        protected void onDetach() {
            if (_chair != null) {
                _chair.remove();
                _chair = null;
            }
        }

        @Override
        protected void onSpawn(NpcSpawnEvent.NpcSpawnReason reason) {
            if (isEnabled())
                sit();
        }

        @Override
        protected void onDespawn(NpcDespawnEvent.NpcDespawnReason reason) {
            if (_chair != null) {
                _chair.remove();
                _chair = null;
            }
        }

        @Override
        protected void onEnable() {
            sit();
        }

        @Override
        protected void onDisable() {
            if (_chair != null) {
                _chair.eject();
                _chair.remove();
                _chair = null;
            }
        }

        private void sit() {
            if (!getNpc().isSpawned())
                return;

            if (_chair != null && _chair.isValid())
                return;

            Entity entity = getNpc().getEntity();
            assert entity != null;

            _chair = spawnChair();
            if (_chair == null)
                return;

            _chair.setPassenger(entity);
        }

        @Nullable
        private Entity spawnChair() {
            Location location = getNpc().getLocation(CHAIR_LOCATION);
            if (location == null)
                return null;

            location.add(0, -1.6, 0);
            ArmorStand chair = (ArmorStand)location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            chair.setVisible(false);
            chair.setGravity(false);
            return chair;
        }
    }
}

