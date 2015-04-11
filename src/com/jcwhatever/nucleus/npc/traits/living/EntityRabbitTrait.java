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
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;

import javax.annotation.Nullable;

/**
 * An implementation of {@link LivingEntityTrait} specific to
 * {@link org.bukkit.entity.Rabbit} entities.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:LivingEntity"</p>
 */
public class EntityRabbitTrait extends EntityAgeableTrait {

    private Rabbit.Type _type;

    /**
     * Constructor.
     *
     * @param type The parent type that instantiated the trait.
     */
    EntityRabbitTrait(NpcTraitType type) {
        super(type, EntityType.RABBIT);
    }

    /**
     * Get the rabbit type.
     */
    public Rabbit.Type getRabbitType() {
        return _type;
    }

    /**
     * Set the rabbit type. If the rabbit is currently spawned,
     * the current entity is also updated.
     *
     * @param type  The {@link org.bukkit.entity.Rabbit.Type} or type name.
     *
     * @return Self for chaining.
     */
    public EntityRabbitTrait setRabbitType(Object type) {
        PreCon.notNull(type);

        _type = getEnum(type, Rabbit.Type.class);

        Rabbit rabbit = getRabbit();

        if (rabbit != null)
            rabbit.setRabbitType(_type);

        return this;
    }

    @Override
    protected void onAttach(INpc npc) {
        super.onAttach(npc);

        _type = Type.SALT_AND_PEPPER;
    }

    @Override
    protected void onSpawn(NpcSpawnReason reason) {

        super.onSpawn(reason);

        if (isDisposed())
            return;

        Rabbit rabbit = getRabbit();
        if (rabbit == null)
            return;

        rabbit.setRabbitType(_type);
    }

    @Nullable
    private Rabbit getRabbit() {
        return (Rabbit)getLivingEntity();
    }
}