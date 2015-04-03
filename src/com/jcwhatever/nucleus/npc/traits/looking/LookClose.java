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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.npc.traits.looking.LookingTrait.Looking;
import com.jcwhatever.nucleus.providers.npc.INpcProvider;
import com.jcwhatever.nucleus.providers.npc.Npcs;
import com.jcwhatever.nucleus.utils.entity.EntityUtils;
import com.jcwhatever.nucleus.utils.validate.IValidator;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.ref.WeakReference;
import javax.annotation.Nullable;

/**
 * {@link LookingTrait} handler which handles looking at players that
 * get within range of the NPC.
 */
public class LookClose extends LookHandler {

    private static final Location TARGET_LOCATION = new Location(null, 0, 0, 0);
    private static final IValidator<LivingEntity> CLOSE_VALIDATOR = new IValidator<LivingEntity>() {
        @Override
        public boolean isValid(LivingEntity element) {
            return element instanceof Player &&
                    !Npcs.isNpc(element);
        }
    };

    private double _range = 5;
    private WeakReference<Entity> _lookEntity;
    private int _lookTicks = 0;

    /**
     * Constructor.
     *
     * @param trait  The parent {@link Looking} trait.
     */
    public LookClose(Looking trait) {
        super(trait);
    }

    /**
     * Get the range that a player must be within in order for the
     * NPC to look.
     */
    public double getRange() {
        return _range;
    }

    /**
     * Set the range that a player must be within in order for the
     * NPC to look while in look-close mode.
     *
     * @param range  The range.
     *
     * @return  Self for chaining.
     */
    public LookClose setRange(double range) {
        _range = range;

        return this;
    }

    @Nullable
    @Override
    protected Location getLookLocation(Location output) {

        Entity current = currentEntity();
        _lookTicks++;

        if (current != null && _lookTicks < 5) {
            return current.getLocation(output);
        }
        else if (_lookTicks < 5) {
            return null;
        }

        _lookTicks = 0;

        Entity npcEntity = getNpc().getEntity();
        assert npcEntity != null;

        final INpcProvider provider = Nucleus.getProviders().getNpcs();
        assert provider != null;

        LivingEntity close = EntityUtils.getClosestLivingEntity(
                npcEntity, _range, CLOSE_VALIDATOR);

        if (close != null && distanceSquared(close.getLocation(TARGET_LOCATION)) > 1) {
            _lookEntity = new WeakReference<Entity>(close);
            return close.getLocation(output);
        }

        _lookEntity = null;
        return null;
    }

    @Nullable
    private Entity currentEntity() {
        if (_lookEntity == null)
            return null;

        return _lookEntity.get();
    }
}
