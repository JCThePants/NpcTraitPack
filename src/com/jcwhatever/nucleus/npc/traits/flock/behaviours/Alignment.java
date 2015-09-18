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

import java.util.Deque;

import com.jcwhatever.nucleus.providers.npc.INpc;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;


/**
 * Flocking alignment behaviour.
 *
 * <p>Default weight is 0.0D</p
 *
 * <p>Default radius is 3.0D</p>>
 */
public class Alignment extends FlockBehaviour {

    private static final Vector RESULTANT = new Vector(0, 0, 0);

    /**
     * Constructor.
     */
    public Alignment() {
        setWeight(0.0D);
    }

    @Override
    public void modifyVector(INpc npc, Vector vector) {

        if (!npc.isSpawned() || getFlock().isEmpty())
            return;

        Deque<INpc> flock = getFlock();
        int flockSize = flock.size();

        resetVector(RESULTANT);

        while (!flock.isEmpty()) {
            INpc flockNpc = flock.remove();

            if (flockNpc.equals(npc) || !flockNpc.isSpawned())
                continue;

            Entity flockEntity = flockNpc.getEntity();
            assert flockEntity != null;

            Vector flockVector = flockEntity.getVelocity();

            if (!isValidVector(flockVector)) {
                flockSize--;
                continue;
            }

            RESULTANT.add(flockVector);
        }

        if (flockSize == 0)
            return;

        Entity entity = npc.getEntity();
        assert entity != null;

        addVector(vector, RESULTANT
                .multiply(1.0D / flockSize) // divide by flock size
                .normalize()
                .multiply(0.1D)  // adjust for minimal impact
                .multiply(getWeight()));
    }
}
