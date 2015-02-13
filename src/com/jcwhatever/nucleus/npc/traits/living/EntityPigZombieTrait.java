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
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;

import javax.annotation.Nullable;

/**
 * An implementation of {@link LivingEntityTrait} specific to
 * {@link PigZombie} entities.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:LivingEntity"</p>
 */
public class EntityPigZombieTrait extends EntityZombieTrait {

    private int _anger;

    /**
     * Constructor.
     *
     * @param npc  The NPC the trait is for.
     * @param type The parent type that instantiated the trait.
     */
    EntityPigZombieTrait(INpc npc, NpcTraitType type) {
        super(npc, type);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.PIG_ZOMBIE;
    }

    /**
     * Get the anger level the zombie is spawned with.
     */
    public int getAnger() {
        return _anger;
    }

    /**
     * Set the anger level the zombie is spawned with. If the zombie is
     * already spawned, the current entity is also updated.
     *
     * @param anger  The anger level.
     *
     * @return  Self for chaining.
     */
    public EntityPigZombieTrait setAnger(int anger) {
        _anger = anger;

        PigZombie zombie = getZombie();
        if (zombie != null)
            zombie.setAnger(anger);

        return this;
    }

    /**
     * Determine if the zombie is spawned angry.
     */
    public boolean isAngry() {
        return _anger > 0;
    }

    /**
     * Set the zombie anger flag used when it is spawned. If the zombie
     * is already spawned, the current entity is also updated.
     *
     * @param isAngry  True for angry, otherwise false.
     *
     * @return  Self for chaining.
     */
    public EntityPigZombieTrait setAngry(boolean isAngry) {
        _anger = isAngry ? 400 : 0;

        setAnger(_anger);

        return this;
    }

    @Override
    public void onSpawn() {

        super.onSpawn();

        if (isDisposed())
            return;

        PigZombie zombie = getZombie();
        if (zombie != null) {
            zombie.setAnger(_anger);
        }
    }

    @Nullable
    private PigZombie getZombie() {
        return (PigZombie)getLivingEntity();
    }
}