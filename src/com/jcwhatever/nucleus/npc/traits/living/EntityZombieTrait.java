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

package com.jcwhatever.nucleus.npc.traits.living;

import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.events.NpcSpawnEvent.NpcSpawnReason;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

import javax.annotation.Nullable;

/**
 * An implementation of {@link LivingEntityTrait} specific to
 * {@link Zombie} entities.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:LivingEntity"</p>
 */
public class EntityZombieTrait extends LivingEntityTrait {

    private boolean _isBaby;
    private boolean _isVillager;

    /**
     * Constructor.
     *
     * @param type The parent type that instantiated the trait.
     */
    EntityZombieTrait(NpcTraitType type) {
        super(type, EntityType.ZOMBIE);
    }

    /**
     * Determine if the zombie is spawned as a baby.
     */
    public boolean isBaby() {
        return _isBaby;
    }

    /**
     * Set the zombies baby flag which is used whenever it is spawned.
     * If the zombie is already spawned, the current entity is also
     * updated.
     *
     * @param isBaby  True to make the zombie a baby, otherwise false.
     *
     * @return  Self for chaining.
     */
    public EntityZombieTrait setBaby(boolean isBaby) {
        _isBaby = isBaby;

        Zombie zombie = getZombie();
        if (zombie != null)
            zombie.setBaby(isBaby);

        return this;
    }

    /**
     * Determine if the zombie is a villager.
     */
    public boolean isVillager() {
        return _isVillager;
    }

    /**
     * Set the zombies villager flag which is used whenever it is spawned.
     * If the zombie is already spawned, the current entity is also
     * updated.
     *
     * @param isVillager  True to make the zombie a villager, otherwise false.
     *
     * @return  Self for chaining.
     */
    public EntityZombieTrait setVillager(boolean isVillager) {
        _isVillager = isVillager;

        Zombie zombie = getZombie();
        if (zombie != null)
            zombie.setVillager(isVillager);

        return this;
    }

    @Override
    protected void onAttach(INpc npc) {
        super.onAttach(npc);

        _isBaby = false;
        _isVillager = false;
    }

    @Override
    protected void onSpawn(NpcSpawnReason reason) {

        super.onSpawn(reason);

        if (isDisposed())
            return;

        Zombie zombie = getZombie();
        if (zombie != null) {
            zombie.setBaby(_isBaby);
            zombie.setVillager(_isVillager);
        }
    }

    @Nullable
    private Zombie getZombie() {
        return (Zombie)getLivingEntity();
    }
}