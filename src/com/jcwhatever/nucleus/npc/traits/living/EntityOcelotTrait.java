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
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Ocelot.Type;

import javax.annotation.Nullable;

/**
 * An implementation of {@link LivingEntityTrait} specific to
 * {@link org.bukkit.entity.Ocelot} entities.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:LivingEntity"</p>
 */
public class EntityOcelotTrait extends EntityAgeableTrait {

    private Ocelot.Type _type;
    private boolean _isSitting;

    /**
     * Constructor.
     *
     * @param type The parent type that instantiated the trait.
     */
    EntityOcelotTrait(NpcTraitType type) {
        super(type, EntityType.OCELOT);
    }

    /**
     * Get the cat type the ocelot is spawned with.
     */
    public Ocelot.Type getCatType() {
        return _type;
    }

    /**
     * Set the cat type the ocelot is spawned with. If the NPC is currently
     * spawned, the NPC's entity cat type is also updated.
     *
     * @param catType  The {@link Ocelot.Type} or type name.
     *
     * @return  Self for chaining.
     */
    public EntityOcelotTrait setCatType(Object catType) {
        PreCon.notNull(catType);

        Ocelot.Type type = getEnum(catType, Ocelot.Type.class);

        _type = type;

        Ocelot ocelot = getOcelot();

        if (ocelot != null)
            ocelot.setCatType(type);

        return this;
    }

    /**
     * Get the sitting flag the ocelot is spawned with.
     */
    public boolean isSitting() {
        return _isSitting;
    }

    /**
     * Set the sitting flag the ocelot is spawned with. If the NPC is
     * currently spawned, the spawned entity is also updated.
     *
     * @param isSitting  True to sit, otherwise false.
     *
     * @return  Self for chaining.
     */
    public EntityOcelotTrait setSitting(boolean isSitting) {
        _isSitting = isSitting;

        Ocelot ocelot = getOcelot();
        if (ocelot != null)
            ocelot.setSitting(isSitting);

        return this;
    }

    @Override
    protected void onAdd(INpc npc) {
        super.onAdd(npc);

        _type = Type.WILD_OCELOT;
        _isSitting = false;
    }

    @Override
    protected void onSpawn(NpcSpawnReason reason) {

        super.onSpawn(reason);

        if (isDisposed())
            return;

        Ocelot ocelot = getOcelot();
        if (ocelot == null)
            return;

        ocelot.setCatType(_type);
        ocelot.setSitting(_isSitting);
    }

    @Nullable
    private Ocelot getOcelot() {
        return (Ocelot)getLivingEntity();
    }
}
