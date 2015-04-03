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
import com.jcwhatever.nucleus.utils.ProjectileUtils;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.nucleus.managed.scheduler.IScheduledTask;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

/**
 * Causes the NPC to fire arrows at its current target of aggression.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:Archer"</p>
 */
public class ArcherTrait extends NpcTraitType {

    private static final String NAME = "Archer";

    /**
     * Constructor.
     */
    public ArcherTrait() {
        super(NpcTraitPack.getPlugin(), NAME);
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new Archer(this);
    }

    public static class Archer extends NpcTrait {

        private IScheduledTask _task;

        /**
         * Constructor.
         *
         * @param type  The parent type that instantiated the trait.
         */
        Archer(NpcTraitType type) {
            super(type);
        }

        @Override
        protected void onAdd(INpc npc) {
            runTask();
        }

        @Override
        protected void onSpawn(NpcSpawnReason reason) {
            runTask();
        }

        @Override
        protected void onDespawn(NpcDespawnReason reason) {
            stopTask();
        }

        @Override
        protected void onRemove() {
            stopTask();
        }

        private void runTask() {
            if (_task != null)
                return;

            INpc npc = getNpc();

            if (!npc.isSpawned())
                return;

            _task = Scheduler.runTaskRepeat(getType().getPlugin(), 40, 40, new ShootArrow());
        }

        private void stopTask() {
            if (_task == null)
                return;

            _task.cancel();
            _task = null;
        }

        private class ShootArrow implements Runnable {

            @Override
            public void run() {

                INpc npc = getNpc();

                if (!npc.isSpawned())
                    return;

                Entity entity = npc.getEntity();

                Entity target = npc.getNavigator().getTargetEntity();
                if (target == null)
                    return;

                Location targetLocation;

                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity)entity;

                    targetLocation = target instanceof HumanEntity
                            ? ProjectileUtils.getHeartLocation((HumanEntity) target)
                            : livingEntity.getEyeLocation();
                }
                else {
                    targetLocation = target.getLocation();
                }

                ProjectileUtils.shootBallistic(entity, targetLocation, 0.01D, Arrow.class);
            }
        }
    }
}
