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

import com.jcwhatever.nucleus.npc.traits.flock.IFlockBehaviour;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.util.Vector;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Abstract implementation of {@link IFlockBehaviour}.
 */
public abstract class FlockBehaviour implements IFlockBehaviour {

    private final Deque<INpc> _flock = new ArrayDeque<>(15);
    private double _weight = 1.0D;
    private double _radius = 3.0D;

    @Override
    public double getWeight() {
        return _weight;
    }

    @Override
    public FlockBehaviour setWeight(double weight) {
        PreCon.positiveNumber(weight, "weight");

        _weight = weight;

        return this;
    }

    @Override
    public double getRadius() {
        return _radius;
    }

    @Override
    public FlockBehaviour setRadius(double radius) {
        PreCon.greaterThanZero(radius, "radius");

        _radius = radius;

        return this;
    }

    @Override
    public Deque<INpc> getFlock() {
        return _flock;
    }

    /**
     * Add a vector to another if the vector to add contains no
     * NaN values.
     *
     * @param vector  The vector to add another vector to.
     * @param toAdd   The vector to be added.
     */
    protected void addVector(Vector vector, Vector toAdd) {
        if (isValidVector(toAdd))
            vector.add(toAdd);
    }

    /**
     * Determine if a vector is valid.
     *
     * <p>A valid vector has no NaN values.</p>
     *
     * @param vector  The vector to check.
     */
    protected boolean isValidVector(Vector vector) {
        return !Double.isNaN(vector.getX()) &&
                !Double.isNaN(vector.getY()) &&
                !Double.isNaN(vector.getZ());
    }

    /**
     * Reset a vectors values back to 0.
     *
     * @param vector  The vector to reset.
     */
    protected void resetVector(Vector vector) {
        // reset; set each value in case resultant has NaN values
        vector.setX(0).setY(0).setZ(0);
    }
}
