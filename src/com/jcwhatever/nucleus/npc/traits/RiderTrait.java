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
import com.jcwhatever.nucleus.providers.npc.events.NpcDespawnEvent.NpcDespawnReason;
import com.jcwhatever.nucleus.providers.npc.events.NpcSpawnEvent.NpcSpawnReason;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;

/**
 * Creates another NPC for the trait owning NPC to ride.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:Rider"</p>
 */
public class RiderTrait extends NpcTraitType {

    @Override
    public Plugin getPlugin() {
        return NpcTraitPack.getPlugin();
    }

    @Override
    public String getName() {
        return "Rider";
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new Rider(npc, this);
    }

    public static class Rider extends NpcTrait {

        private INpc _vehicle;

        /**
         * Constructor.
         *
         * @param npc  The NPC the trait is for.
         * @param type The parent type that instantiated the trait.
         */
        Rider(INpc npc, NpcTraitType type) {
            super(npc, type);
        }

        /**
         * Get the current vehicle NPC.
         *
         * @return  The vehicle {@link INpc} or null if one is not set.
         */
        @Nullable
        public INpc getVehicle() {
            return _vehicle;
        }

        /**
         * Mount the NPC to a vehicle of the specified entity type.
         *
         * If the NPC is already riding a vehicle created by the trait, the
         * vehicle is disposed.
         *
         * @param vehicleName  The vehicles NPC name.
         * @param entityType   The {@link org.bukkit.entity.EntityType} or entity type name.
         *
         * @return  Self for chaining.
         */
        public Rider mount(String vehicleName, Object entityType) {
            PreCon.notNullOrEmpty(vehicleName);
            PreCon.notNull(entityType);

            if (_vehicle != null)
                dispose();

            EntityType type = getEntityType(entityType);

            _vehicle = getNpc().getRegistry().create(vehicleName, type);

            if (_vehicle != null && getNpc().isSpawned()) {

                Location location = getNpc().getLocation();
                assert location != null;

                _vehicle.spawn(location);
                getNpc().mountNPC(_vehicle);
            }

            return this;
        }

        /**
         * Dismount from the current vehicle created by the trait.
         *
         * @return  Self for chaining.
         */
        public Rider dismount() {

            if (_vehicle == null)
                return this;

            _vehicle.dispose();

            _vehicle = null;

            return this;
        }

        @Override
        public void onSpawn(NpcSpawnReason reason) {
            if (isEnabled())
                mountCurrent();
        }

        @Override
        public void onDespawn(NpcDespawnReason reason) {
            dismountCurrent();
        }

        @Override
        protected void onEnable() {
            mountCurrent();
        }

        @Override
        protected void onDisable() {
            dismountCurrent();
        }

        // mount to the current vehicle NPC
        private void mountCurrent() {
            if (_vehicle == null || !getNpc().isSpawned())
                return;

            if (!_vehicle.isSpawned())
                _vehicle.spawn(getNpc().getLocation());

            getNpc().mountNPC(_vehicle);
        }

        // dismount from the current vehicle npc without disposing it
        private void dismountCurrent() {
            if (_vehicle == null || !getNpc().isSpawned() || !_vehicle.isSpawned())
                return;

            Entity entity = _vehicle.getEntity();
            assert entity != null;

            entity.eject();

            _vehicle.despawn();
        }

        // get the entity type from the object
        private EntityType getEntityType(Object type) {

            if (type instanceof String) {
                String name = ((String) type).toUpperCase();

                try {
                    return EntityType.valueOf(name);
                }
                catch (Exception e) {
                    throw new IllegalArgumentException("Invalid entity type name.");
                }
            }
            else if (type instanceof EntityType) {
                return (EntityType)type;
            }
            else {
                throw new IllegalArgumentException("Invalid entity type. EntityType constant or name expected.");
            }
        }
    }
}
