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
import com.jcwhatever.nucleus.providers.npc.events.NpcDamageEvent;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * When riding the NPC, the passenger is protected from damage.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:ProtectPassenger"</p>
 */
public class ProtectPassengerTrait extends NpcTraitType {

    private static final String NAME = "ProtectPassenger";

    private static EventListener _listener;

    /**
     * Constructor.
     */
    public ProtectPassengerTrait() {
        super(NpcTraitPack.getPlugin(), NAME);
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {

        if (_listener == null) {
            _listener = new EventListener();
            Bukkit.getPluginManager().registerEvents(_listener, getPlugin());
        }

        return new ProtectPassenger(npc, this);
    }

    public static class ProtectPassenger extends NpcTrait {

        /**
         * Constructor.
         *
         * @param npc  The NPC the trait is for.
         * @param type The parent type that instantiated the trait.
         */
        ProtectPassenger(INpc npc, NpcTraitType type) {
            super(npc, type);
        }
    }

    private static class EventListener implements Listener {

        @EventHandler
        private void onDamage(NpcDamageEvent event) {

            INpc npc = event.getNpc();

            INpc vehicle = npc.getNPCVehicle();
            if (vehicle == null)
                return;

            if (vehicle.getTraits().isEnabled(NpcTraitPack.getLookup(NAME))) {

                event.setCancelled(true);
                event.getParentEvent().setDamage(0.0D);
            }
        }
    }
}
