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

import com.jcwhatever.nucleus.npc.traits.nms.INmsSpigotActivationHandler;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.utils.scheduler.IScheduledTask;

import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Keeps an NPC entity activated so its Minecraft AI is run at full speed
 * even when a player is not nearby.
 *
 * <p>Spigot specific feature.</p>
 */
public class SpigotActivatedTrait extends NpcTraitType {

    @Override
    public Plugin getPlugin() {
        return NpcTraitPack.getPlugin();
    }

    @Override
    public String getName() {
        return "SpigotActivated";
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new SpigotActivated(npc, this);
    }

    public static class SpigotActivated extends NpcTrait {

        private static IScheduledTask _task;
        private static Map<Entity, SpigotActivated> _entities = new WeakHashMap<>(20);

        /**
         * Constructor.
         *
         * @param npc   The NPC the trait is for.
         * @param type  The parent type that instantiated the trait.
         */
        public SpigotActivated(INpc npc, NpcTraitType type) {
            super(npc, type);

            if (_task == null) {

                final INmsSpigotActivationHandler handler = NpcTraitPack.getNmsManager()
                        .getNmsHandler("SPIGOT_ACTIVATION");
                if (handler == null || !handler.isAvailable())
                    return;

                _task = Scheduler.runTaskRepeat(NpcTraitPack.getPlugin(), 1, 1, new Runnable() {

                    @Override
                    public void run() {

                        for (SpigotActivated trait : _entities.values()) {

                            INpc npc = trait.getNpc();
                            if (!npc.isSpawned() || !trait.isEnabled() || trait.isDisposed())
                                continue;

                            Entity entity = npc.getEntity();
                            handler.activateEntity(entity);
                        }
                    }
                });
            }
        }

        @Override
        public void onSpawn() {

            Entity entity = getNpc().getEntity();
            if (entity != null)
                _entities.put(entity, this);
        }

        @Override
        public void onDespawn() {

            Entity entity = getNpc().getEntity();
            if (entity != null)
                _entities.remove(entity);
        }
    }
}
