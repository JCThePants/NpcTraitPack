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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.INpcProvider;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.entity.EntityUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.Plugin;

/**
 * The weapon of the NPC does not take damage.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:UnbreakingWeapon"</p>
 */
public class UnbreakingWeaponTrait extends NpcTraitType {

    private EventListener _listener;

    @Override
    public Plugin getPlugin() {
        return NpcTraitPack.getPlugin();
    }

    @Override
    public String getName() {
        return "UnbreakingWeapon";
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {

        if (_listener == null) {
            _listener = new EventListener();
            Nucleus.getEventManager().register(_listener);
        }

        return new UnbreakingWeapon(npc, this);
    }

    public static class UnbreakingWeapon extends NpcTrait {

        /**
         * Constructor.
         *
         * @param npc   The NPC the trait is for.
         * @param type  The parent type that instantiated the trait.
         */
        UnbreakingWeapon(INpc npc, NpcTraitType type) {
            super(npc, type);
        }
    }

    private static class EventListener implements IEventListener {

        @Override
        public Plugin getPlugin() {
            return NpcTraitPack.getPlugin();
        }

        @EventMethod
        private void onDamage(EntityDamageByEntityEvent event) {

            Entity damager = EntityUtils.getDamager(event.getDamager());
            if (!(damager instanceof LivingEntity))
                return;

            INpcProvider provider = Nucleus.getProviderManager().getNpcProvider();
            assert provider != null;

            INpc npc = provider.getNpc(damager);
            if (npc == null)
                return;

            if (npc.getTraits().has(NpcTraitPack.getLookup("UnbreakingWeapon"))) {
                ItemStackUtils.repair(((LivingEntity) damager).getEquipment().getItemInHand());
            }
        }
    }

}
