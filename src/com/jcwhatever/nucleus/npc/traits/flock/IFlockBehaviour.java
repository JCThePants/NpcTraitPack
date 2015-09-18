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

package com.jcwhatever.nucleus.npc.traits.flock;

import com.jcwhatever.nucleus.providers.npc.INpc;

import org.bukkit.util.Vector;

import java.util.Deque;
import java.util.List;

/**
 * Interface for a flock behaviour.
 *
 * @see  FlockingTrait
 * @see  FlockingTrait.Flocking
 */
public interface IFlockBehaviour {

    /**
     * Get the behaviours weight.
     */
    double getWeight();

    /**
     * Set the behaviours weight.
     *
     * @param weight  The weight. A value of 0 disables the behaviour.
     *
     * @return  Self for chaining.
     */
    IFlockBehaviour setWeight(double weight);

    /**
     * Get the radius that NPC's should be within for the behaviour to
     * be applied.
     */
    double getRadius();

    /**
     * Set the radius that NPC's should be withing for the behaviour to
     * be applied.
     *
     * @param radius  The radius in blocks.
     *
     * @return  Self for chaining.
     */
    IFlockBehaviour setRadius(double radius);

    /**
     * Get the behaviours que of {@link INpc's} that will be used to
     * calculate a new vector when {@link #modifyVector} is invoked.
     *
     * <p>NPC's can be directly added and removed from the returned que.</p>
     */
    Deque<INpc> getFlock();

    /**
     * Modify a vector that will be added to the NPC.
     *
     * <p>Invoking this causes the list returned by {@link #getFlock} to
     * be cleared.</p>
     *
     * <p>Intended for use by {@link FlockingTrait}</p>
     *
     * @param npc     The NPC whose vector is to be modified.
     * @param vector  The vector to modify. The vector is added to the
     *                NPC after all behaviours have a chance to modify it.
     */
    void modifyVector(INpc npc, Vector vector);
}
