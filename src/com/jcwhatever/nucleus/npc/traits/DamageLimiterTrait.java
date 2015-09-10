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
import com.jcwhatever.nucleus.providers.npc.Npcs;
import com.jcwhatever.nucleus.providers.npc.events.NpcSpawnEvent;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * A trait that limits the max amount of damage an NPC can take per damage event.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:DamageLimiter"</p>
 */
public class DamageLimiterTrait extends NpcTraitType {

    private static final String NAME = "DamageLimiter";
    private static DamageListener _listener;

    /**
     * Constructor.
     */
    public DamageLimiterTrait() {
        super(NpcTraitPack.getPlugin(), NAME);
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new DamageLimiter(this);
    }

    public static class DamageLimiter extends NpcTrait {

        private double _maxDamage = 1.0D;

        /**
         * Constructor.
         *
         * @param type The parent type that instantiated the trait.
         */
        DamageLimiter(NpcTraitType type) {
            super(type);
        }

        /**
         * Get the max damage.
         */
        public double getMaxDamage() {
            return _maxDamage;
        }

        /**
         * Set the max damage.
         *
         * @param maxDamage  The max damage value.
         *
         * @return  Self for chaining.
         */
        public DamageLimiter setMaxDamage(double maxDamage) {
            _maxDamage = maxDamage;
            return this;
        }

        @Override
        protected void onSpawn(NpcSpawnEvent.NpcSpawnReason reason) {
            if (_listener == null) {
                _listener = new DamageListener();
                Bukkit.getPluginManager().registerEvents(_listener, NpcTraitPack.getPlugin());
            }
        }
    }

    private static class DamageListener implements Listener {

        private static final String LOOKUP = NpcTraitPack.getLookup(NAME);

        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        private void onNpcDamage(EntityDamageEvent event) {

            Entity entity = event.getEntity();
            INpc npc = Npcs.getNpc(entity);
            if (npc == null)
                return;

            DamageLimiter trait = (DamageLimiter)npc.getTraits().get(LOOKUP);
            if (trait == null)
                return;

            if (event.getDamage() > trait._maxDamage) {
                event.setDamage(trait._maxDamage);
            }
        }
    }
}
