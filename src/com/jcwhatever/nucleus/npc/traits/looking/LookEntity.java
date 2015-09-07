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

package com.jcwhatever.nucleus.npc.traits.looking;

import com.jcwhatever.nucleus.npc.traits.looking.LookingTrait.Looking;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.lang.ref.WeakReference;
import javax.annotation.Nullable;

/**
 * {@link LookingTrait} handler which handles looking at a specified
 * {@link org.bukkit.entity.Entity}.
 */
public class LookEntity extends LookHandler {

    private static final Location NPC_LOCATION = new Location(null, 0, 0, 0);

    private WeakReference<Entity> _entity;

    /**
     * Constructor.
     *
     * @param trait  The parent {@link Looking} trait.
     */
    public LookEntity(Looking trait) {
        super(trait);
    }

    /**
     * Get the entity that the NPC is set to look at.
     *
     * @return  The {@link org.bukkit.entity.Entity} or null if not set.
     */
    @Nullable
    public Entity getLookEntity() {
        return _entity == null ? null : _entity.get();
    }

    /**
     * Set the entity that the NPC is should look at.
     *
     * @param entity  The {@link org.bukkit.entity.Entity} to look at.
     *
     * @return  Self for chaining.
     */
    public LookEntity setLookEntity(Entity entity) {
        PreCon.notNull(entity);

        _entity = new WeakReference<Entity>(entity);

        return this;
    }

    @Nullable
    @Override
    protected Location getLookLocation(Location output) {

        if (_entity == null)
            return null;

        Entity entity = _entity.get();
        if (entity == null || entity.isDead() || !entity.isValid())
            return null;

        entity.getLocation(output);

        Location npcLocation = getNpc().getLocation(NPC_LOCATION);
        if (npcLocation == null || !entity.getWorld().equals(npcLocation.getWorld()))
            return null;

        return output;
    }
}
