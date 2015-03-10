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

package com.jcwhatever.nucleus.npc.traits.flock.behaviours;

import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.utils.LocationUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.LinkedList;

/**
 * Flocking separation behaviour.
 *
 * <p>Default weight is 3.0D</p>
 *
 * <p>Default radius is 3.0D</p>
 */
public class Separation extends FlockBehaviour {

    private static final Location LOCATION = new Location(null, 0, 0, 0);
    private static final Vector VECTOR = new Vector(0, 0, 0);
    private static final Vector RESULTANT = new Vector(0, 0, 0);

    /**
     * Constructor.
     */
    public Separation() {
        setWeight(3.0D);
    }

    @Override
    public void modifyVector(INpc npc, Vector vector) {

        if (!npc.isSpawned() || getFlock().isEmpty())
            return;

        resetVector(RESULTANT);

        Entity entity = npc.getEntity();
        assert entity != null;

        Vector npcVector = getLocation(entity).toVector();

        LinkedList<INpc> flock = getFlock();
        int flockSize = flock.size();

        while (!flock.isEmpty()) {
            INpc flockNpc = flock.remove();

            if (flockNpc.equals(npc) || !flockNpc.isSpawned())
                continue;

            Entity flockEntity = flockNpc.getEntity();
            assert flockEntity != null;

            Vector flockVector = getLocationVector(flockEntity).subtract(npcVector);

            RESULTANT.add(flockVector);
        }

        addVector(vector, RESULTANT
                .multiply(-(1.0D / flockSize)) // divide by flock size and negate
                .normalize()
                .multiply(0.1D) // adjust for minimal impact
                .multiply(getWeight()));
    }

    private Vector getLocationVector(Entity entity) {
        return Bukkit.isPrimaryThread()
                ? LocationUtils.copy(getLocation(entity), VECTOR)
                : getLocation(entity).toVector();
    }

    private Location getLocation(Entity entity) {
        return Bukkit.isPrimaryThread()
                ? entity.getLocation(LOCATION)
                : entity.getLocation();
    }
}
