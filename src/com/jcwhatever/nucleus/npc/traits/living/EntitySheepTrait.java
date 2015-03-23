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
import org.bukkit.entity.Sheep;

import javax.annotation.Nullable;

/**
 * An implementation of {@link LivingEntityTrait} specific to
 * {@link org.bukkit.entity.Sheep} entities.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:LivingEntity"</p>
 */
public class EntitySheepTrait extends EntityAgeableTrait {

    private boolean _isSheared;
    private DyeColor _color = DyeColor.WHITE;

    /**
     * Constructor.
     *
     * @param npc  The NPC the trait is for.
     * @param type The parent type that instantiated the trait.
     */
    EntitySheepTrait(INpc npc, NpcTraitType type) {
        super(npc, type, EntityType.SHEEP);
    }

    /**
     * Determine if the sheep is spawned sheared.
     */
    public boolean isSheared() {
        return _isSheared;
    }

    /**
     * Set if the sheep is spawned sheared. If the sheep is currently spawned,
     * the current entity is also updated.
     *
     * @param isSheared  True to shear, otherwise false.
     *
     * @return  Self for chaining.
     */
    public EntitySheepTrait setSheared(boolean isSheared) {
        _isSheared = isSheared;

        Sheep sheep = getSheep();
        if (sheep != null)
            sheep.setSheared(isSheared);

        return this;
    }

    /**
     * Get the dye color the sheep is spawned with.
     */
    public DyeColor getColor() {
        return _color;
    }

    /**
     * Set the dye color the sheep is spawned with. If the sheep is currently
     * spawned, the current entity is also updated.
     *
     * @param dyeColor  The {@link org.bukkit.DyeColor} or dye color name.
     *
     * @return  Self for chaining.
     */
    public EntitySheepTrait setColor(Object dyeColor) {
        PreCon.notNull(dyeColor);

        DyeColor color = getEnum(dyeColor, DyeColor.class);

        _color = color;

        Sheep sheep = getSheep();
        if (sheep != null)
            sheep.setColor(color);

        return this;
    }

    @Override
    public void onSpawn(NpcSpawnReason reason) {

        super.onSpawn(reason);

        if (isDisposed())
            return;

        Sheep sheep = getSheep();
        if (sheep == null)
            return;

        sheep.setSheared(_isSheared);
        sheep.setColor(_color);
    }

    @Nullable
    private Sheep getSheep() {
        return (Sheep)getLivingEntity();
    }
}
