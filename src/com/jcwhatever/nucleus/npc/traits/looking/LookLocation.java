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
import com.jcwhatever.nucleus.utils.LocationUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;

import javax.annotation.Nullable;

/**
 * {@link LookingTrait} handler which handles looking at a specified
 * {@link org.bukkit.Location}.
 */
public class LookLocation extends LookHandler {

    private final Location _location = new Location(null, 0, 0, 0);

    /**
     * Constructor.
     *
     * @param trait
     */
    public LookLocation(Looking trait) {
        super(trait);
    }

    /**
     * Get the {@link org.bukkit.Location} the NPC should look at.
     */
    public Location getLookLocation() {
        return _location;
    }

    /**
     * Set the {@link org.bukkit.Location} the NPC should look at.
     *
     * @param location  The location to look at.
     *
     * @return  Self for chaining.
     */
    public LookLocation setLookLocation(Location location) {
        PreCon.notNull(location);

        LocationUtils.copy(location, _location);

        return this;
    }

    @Nullable
    @Override
    protected Location getLookLocation(Location output) {

        if (_location.getWorld() == null)
            return null;

        return LocationUtils.copy(_location, output);
    }
}
