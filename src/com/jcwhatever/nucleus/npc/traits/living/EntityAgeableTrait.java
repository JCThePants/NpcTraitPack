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

import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;

/**
 * An implementation of {@link LivingEntityTrait} specific to entities that implement
 * {@link Ageable}.
 *
 * <p>Extended with more specific implementations where available, used when a more specific
 * trait is not available.</p>
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:LivingEntity"</p>
 */
public class EntityAgeableTrait extends LivingEntityTrait {

    // < 0 = Baby
    private int _age;
    private boolean _isAgeLock;

    /**
     * Constructor.
     *
     * @param type        The parent type that instantiated the trait.
     * @param entityType  The entity type.
     */
    EntityAgeableTrait(NpcTraitType type, EntityType entityType) {
        super(type, entityType);
    }

    /**
     * Get the age the entity is spawned at.
     */
    public int getAge() {
        return _age;
    }

    /**
     * Set the age of the NPC when it is spawned. If the entity
     * is currently spawned, the age of the entity is also updated.
     *
     * @param age  The age. Less than 0 is baby, greater than is adult.
     *
     * @return  Self for chaining.
     */
    public EntityAgeableTrait setAge(int age) {

        _age = age;

        Ageable ageable = getAgeable();
        if (ageable != null)
            ageable.setAge(age);

        return this;
    }

    /**
     * Determine if the age is locked when the entity is spawned.
     */
    public boolean getAgeLock() {
        return _isAgeLock;
    }

    /**
     * Set the age lock the NPC is spawned with. If the NPC is currently
     * spawned, its entity is also updated.
     *
     * @param isAgeLock  True to lock age, otherwise false.
     *
     * @return  Self for chaining.
     */
    public EntityAgeableTrait setAgeLock(boolean isAgeLock) {
        _isAgeLock = isAgeLock;

        Ageable ageable = getAgeable();
        if (ageable != null)
            ageable.setAgeLock(isAgeLock);

        return this;
    }

    @Override
    protected void onAdd(INpc npc) {
        super.onAdd(npc);

        _age = 0;
        _isAgeLock = false;
    }

    @Override
    protected void onSpawn(NpcSpawnReason reason) {

        super.onSpawn(reason);

        if (isDisposed())
            return;

        Ageable ageable = getAgeable();
        if (ageable == null)
            return;

        ageable.setAge(_age);
        ageable.setAgeLock(_isAgeLock);
    }

    @Nullable
    private Ageable getAgeable() {
        return (Ageable)getLivingEntity();
    }
}
