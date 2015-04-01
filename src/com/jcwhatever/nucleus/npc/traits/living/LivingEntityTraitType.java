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

import com.jcwhatever.nucleus.npc.traits.NpcTraitPack;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.EntityType;

/**
 * The trait type for a living entity.
 */
public class LivingEntityTraitType extends NpcTraitType {

    /**
     * Constructor.
     */
    public LivingEntityTraitType() {
        super(NpcTraitPack.getPlugin(), "LivingEntity");
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {

        EntityType type = npc.getTraits().getType();

        switch (type) {
            case HORSE:
                return new EntityHorseTrait(this);
            case CREEPER:
                return new EntityCreeperTrait(this);
            case OCELOT:
                return new EntityOcelotTrait(this);
            case PIG_ZOMBIE:
                return new EntityPigZombieTrait(this);
            case SHEEP:
                return new EntitySheepTrait(this);
            case SLIME:
                return new EntitySlimeTrait(this);
            case VILLAGER:
                return new EntityVillagerTrait(this);
            case WOLF:
                return new EntityWolfTrait(this);
            case ZOMBIE:
                return new EntityZombieTrait(this);
            case RABBIT:
                return new EntityRabbitTrait(this);
        }

        if (Ageable.class.isAssignableFrom(type.getEntityClass()))
            return new EntityAgeableTrait(this, type);

        return new LivingEntityTrait(this, type);
    }
}
