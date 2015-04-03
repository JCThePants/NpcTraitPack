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
import com.jcwhatever.nucleus.providers.npc.events.NpcDeathEvent;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.providers.npc.Npcs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A trait that prevents an NPC's victim from dropping its items and optionally
 * drops those items when the NPC is killed.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:PickupVictimDrops"</p>
 */
public class PickupVictimDropsTrait extends NpcTraitType {

    private static final String NAME = "PickupVictimDrops";

    private static EventListener _listener;

    /**
     * Constructor.
     */
    public PickupVictimDropsTrait() {
        super(NpcTraitPack.getPlugin(), NAME);
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {

        if (_listener == null) {
            _listener = new EventListener();
            Bukkit.getPluginManager().registerEvents(_listener, getPlugin());
        }

        return new PickupVictimDrops(this);
    }

    public static class PickupVictimDrops extends NpcTrait {

        private List<ItemStack> _pickedUp;
        private boolean _isItemsDropped;
        private boolean _isXpPickedUp = true;

        /**
         * Constructor.
         *
         * @param type The parent type that instantiated the trait.
         */
        PickupVictimDrops(NpcTraitType type) {
            super(type);
        }

        /**
         * Determine if Xp is removed from victim drops.
         */
        public boolean isXpPickedUp() {
            return _isXpPickedUp;
        }

        /**
         * Set Xp drops from victims are removed.
         *
         * @param isPickedUp  True to remove dropped XP, otherwise false.
         *
         * @return  Self for chaining.
         */
        public PickupVictimDrops setXpPickedUp(boolean isPickedUp) {
            _isXpPickedUp = isPickedUp;

            return this;
        }

        /**
         * Determine if items the NPC picks up are dropped when the NPC is killed.
         */
        public boolean isItemsDropped() {
            return _isItemsDropped;
        }

        /**
         * Set items picked up by NPC are dropped when the NPC is killed.
         *
         * @param isDropped  True to drop picked up items, otherwise false.
         *
         * @return  Self for chaining.
         */
        public PickupVictimDrops setItemsDropped(boolean isDropped) {
            _isItemsDropped = isDropped;
            return this;
        }

        /**
         * Get a direct reference to the list of items the NPC has picked up.
         *
         * <p>The list is not filled if the NPC is not set to drop items when
         * it is killed.</p>
         *
         * @see #isItemsDropped
         * @see #setItemsDropped
         */
        public List<ItemStack> getPickedUp() {
            if (_pickedUp == null)
                _pickedUp = new ArrayList<>(15);

            return _pickedUp;
        }

        @Override
        protected void onRemove() {
            // prep for reuse
            _pickedUp = null;
            _isItemsDropped = false;
            _isXpPickedUp = true;
        }
    }

    private static class EventListener implements Listener {

        private Map<Entity, INpc> _attackerMap = new WeakHashMap<>(20);

        // record NPC damager
        @EventHandler(priority = EventPriority.MONITOR)
        private void onEntityDamage(EntityDamageByEntityEvent event) {

            Entity entity = event.getEntity();

            if (!(entity instanceof LivingEntity))
                return;

            Entity damager = event.getDamager();
            if (damager == null)
                return;

            INpc npc = Npcs.getNpc(damager);
            if (npc == null) {
                _attackerMap.remove(entity);
                return;
            }

            if (!npc.getTraits().isEnabled(NpcTraitPack.getLookup(NAME))) {
                _attackerMap.remove(entity);
                return;
            }

            _attackerMap.put(entity, npc);
        }

        // clear victim drops
        @EventHandler(priority = EventPriority.HIGHEST)
        private void onEntityDeath(EntityDeathEvent event) {

            INpc killer = _attackerMap.remove(event.getEntity());
            if (killer == null)
                return;

            PickupVictimDrops trait = (PickupVictimDrops)killer.getTraits()
                    .get(NpcTraitPack.getLookup(NAME));

            if (trait == null || !trait.isEnabled())
                return;

            if (trait.isXpPickedUp())
                event.setDroppedExp(0);

            if (trait.isItemsDropped()) {
                trait.getPickedUp().addAll(event.getDrops());
            }

            event.getDrops().clear();
        }

        // drop items that are picked up when NPC is killed
        @EventHandler
        private void onNpcDeath(NpcDeathEvent event) {

            PickupVictimDrops trait = (PickupVictimDrops)event.getNpc().getTraits()
                    .get(NpcTraitPack.getLookup(NAME));

            if (trait == null || !trait.isEnabled())
                return;

            if (trait.isItemsDropped()) {
                event.getParentEvent().getDrops().addAll(trait.getPickedUp());
            }
        }
    }
}

