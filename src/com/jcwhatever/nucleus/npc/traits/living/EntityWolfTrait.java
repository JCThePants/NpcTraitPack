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

import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wolf;

import javax.annotation.Nullable;

/**
 * An implementation of {@link LivingEntityTrait} specific to {@link org.bukkit.entity.Wolf} entities.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:LivingEntity"</p>
 */
public class EntityWolfTrait extends EntityAgeableTrait {

    private DyeColor _collarColor = DyeColor.RED;
    private boolean _isAngry;
    private boolean _isSitting;
    private boolean _isTame;

    /**
     * Constructor.
     *
     * @param npc  The NPC the trait is for.
     * @param type The parent type that instantiated the trait.
     */
    EntityWolfTrait(INpc npc, NpcTraitType type) {
        super(npc, type, EntityType.WOLF);
    }

    /**
     * Get the color of the wolfs collar when spawned.
     */
    public DyeColor getCollarColor() {
        return _collarColor;
    }

    /**
     * Set the color of the wolfs collar when spawned. If the wolf is already
     * spawned, the current entity is also updated.
     *
     * @param dyeColor  The {@link org.bukkit.DyeColor} or dye color name.
     *
     * @return  Self for chaining.
     */
    public EntityWolfTrait setCollarColor(Object dyeColor) {
        PreCon.notNull(dyeColor);

        DyeColor color = getEnum(dyeColor, DyeColor.class);

        _collarColor = color;

        Wolf wolf = getWolf();
        if (wolf != null)
            wolf.setCollarColor(color);

        return this;
    }

    /**
     * Determine if the wolf is spawned angry.
     */
    public boolean isAngry() {
        return _isAngry;
    }

    /**
     * Set the wolfs anger flag used when it is spawned. If the wolf is already
     * spawned, the current entity is also updated.
     *
     * @param isAngry  True to make the wolf angry, otherwise false.
     *
     * @return  Self for chaining.
     */
    public EntityWolfTrait setAngry(boolean isAngry) {
        _isAngry = isAngry;

        Wolf wolf = getWolf();
        if (wolf != null)
            wolf.setAngry(isAngry);

        return this;
    }

    /**
     * Determine if the wolf is spawned sitting.
     */
    public boolean isSitting() {
        return _isSitting;
    }

    /**
     * Set the wolfs sitting state when it is spawned. If the wolf is already
     * spawned, the current entity is also updated.
     *
     * @param isSitting  True to sit, otherwise false.
     *
     * @return  Self for chaining.
     */
    public EntityWolfTrait setSitting(boolean isSitting) {
        _isSitting = isSitting;

        Wolf wolf = getWolf();
        if (wolf != null)
            wolf.setSitting(isSitting);

        return this;
    }

    /**
     * Determine if the wolf is spawned tame.
     */
    public boolean isTamed() {
        return _isTame;
    }

    /**
     * Set the wolfs tame state when spawned. If the wolf is already spawned,
     * the current entity is also updated.
     *
     * @param isTame  True to tame, otherwise false.
     *
     * @return  Self for chaining.
     */
    public EntityWolfTrait setTamed(boolean isTame) {
        _isTame = isTame;

        Wolf wolf = getWolf();
        if (wolf != null)
            wolf.setTamed(isTame);

        return this;
    }

    @Override
    public void onSpawn(NpcSpawnReason reason) {

        super.onSpawn(reason);

        if (isDisposed())
            return;

        Wolf wolf = getWolf();
        if (wolf == null)
            return;

        wolf.setCollarColor(_collarColor);
        wolf.setAngry(_isAngry);
        wolf.setSitting(_isSitting);
        wolf.setTamed(_isTame);
    }

    @Nullable
    private Wolf getWolf() {
        return (Wolf) getLivingEntity();
    }
}