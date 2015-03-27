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

import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;

import javax.annotation.Nullable;

/**
 * An implementation of {@link LivingEntityTrait} specific to
 * {@link Creeper} entities.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:LivingEntity"</p>
 */
public class EntityCreeperTrait extends LivingEntityTrait {

    private boolean _isPowered;

    /**
     * Constructor.
     *
     * @param type The parent type that instantiated the trait.
     */
    EntityCreeperTrait(NpcTraitType type) {
        super(type, EntityType.CREEPER);
    }

    /**
     * Determine if the creeper is spawned powered.
     */
    public boolean isPowered() {
        return _isPowered;
    }

    /**
     * Set the creepers powered flag which is used whenever it is spawned.
     * If the creeper is already spawned, the current entity is also
     * updated.
     *
     * @param isPowered  True to make the creeper powered, otherwise false.
     *
     * @return  Self for chaining.
     */
    public EntityCreeperTrait setPowered(boolean isPowered) {
        _isPowered = isPowered;

        Creeper creeper = getCreeper();
        if (creeper != null)
            creeper.setPowered(isPowered);

        return this;
    }

    @Override
    protected void onAdd(INpc npc) {
        super.onAdd(npc);

        _isPowered = false;
    }

    @Override
    protected void onSpawn(NpcSpawnReason reason) {

        super.onSpawn(reason);

        if (isDisposed())
            return;

        Creeper creeper = getCreeper();
        if (creeper == null)
            return;

        creeper.setPowered(_isPowered);
    }

    @Nullable
    private Creeper getCreeper() {
        return (Creeper)getLivingEntity();
    }
}
