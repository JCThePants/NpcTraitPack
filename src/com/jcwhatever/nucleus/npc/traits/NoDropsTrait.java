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

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

/**
 * A trait that prevents the NPC from dropping its items when killed.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:NoDrops"</p>
 */
public class NoDropsTrait extends NpcTraitType {

    private static final String NAME = "NoDrops";

    private static EventListener _listener;

    /**
     * Constructor.
     */
    public NoDropsTrait() {
        super(NpcTraitPack.getPlugin(), NAME);
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {

        if (_listener == null) {
            _listener = new EventListener();
            Bukkit.getPluginManager().registerEvents(_listener, getPlugin());
        }

        return new NoDrops(npc, this);
    }

    public static class NoDrops extends NpcTrait {

        /**
         * Constructor.
         *
         * @param npc  The NPC the trait is for.
         * @param type The parent type that instantiated the trait.
         */
        NoDrops(INpc npc, NpcTraitType type) {
            super(npc, type);
        }
    }

    private static class EventListener implements Listener {

        @EventHandler
        private void onDeath(NpcDeathEvent event) {

            INpc npc = event.getNpc();

            if (npc.getTraits().isEnabled(NpcTraitPack.getLookup("NoDrops"))) {

                EntityDeathEvent deathEvent = event.getParentEvent();

                deathEvent.getDrops().clear();
                deathEvent.setDroppedExp(0);
            }
        }
    }
}
