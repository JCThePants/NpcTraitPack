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

package com.jcwhatever.nucleus.npc.traits.particles;

import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;

import org.bukkit.Location;

/**
 * Abstract implementation of a trait that applies a particle effect
 * to an NPC.
 */
public abstract class ParticlesTrait extends NpcTrait implements Runnable {

    private static final Location NPC_LOCATION = new Location(null, 0, 0, 0);

    private int _interval = 1;
    private float _chance = 0.3f;
    private int _currentTick = 0;
    private int _runCount = -1;
    private int _currentRunCount = 0;
    private int _height = 1;
    private int _verticalOffset = 0;

    /**
     * Constructor.
     *
     * @param npc  The NPC the trait is for.
     * @param type The parent type that instantiated the trait.
     */
    protected ParticlesTrait(INpc npc, NpcTraitType type) {
        super(npc, type);
    }

    /**
     * Get the interval the effect is played at.
     */
    public int getInterval() {
        return _interval;
    }

    /**
     * Set the interval the effect is played at.
     *
     * @param interval  The interval in ticks.
     *
     * @return  Self for chaining.
     */
    public ParticlesTrait setInterval(int interval) {
        _interval = interval;

        return this;
    }

    /**
     * Get the chance the effect will be played at each interval.
     *
     * <p>1.0 is 100%</p>
     */
    public float getChance() {
        return _chance;
    }

    /**
     * Set the chance the effect will be played at each interval.
     *
     * @param chance  The chance. A value of 1.0 is 100%.
     *
     * @return  Self for chaining.
     */
    public ParticlesTrait setChance(float chance) {
        _chance = chance;

        return this;
    }

    /**
     * Get the number of times the effect will play before
     * the trait disables itself.
     *
     * <p>Default value is -1.</p>
     */
    public int getRunCount() {
        return _runCount;
    }

    /**
     * Set the number of times the effect will play before
     * the trait disables itself.
     *
     * @param count  The run count. A value of -1 indicates infinite count.
     *
     * @return  Self for chaining.
     */
    public ParticlesTrait setRunCount(int count) {
        _runCount = count;

        return this;
    }

    /**
     * Get the height of the effect.
     *
     * <p>Default is 1.</p>
     *
     * <p>A value of 1 means the effect will be played at the NPC's location.
     * A value of 2 would indicated the effect will be played at the NPC's location
     * plus 1 block above and so forth.</p>
     */
    public int getHeight() {
        return _height;
    }

    /**
     * Set the height of the effect.
     *
     * @param height  The height. A value of 1 means the effect will be played at the
     *                NPC's location. A value of 2 would indicated the effect will be
     *                played at the NPC's location plus 1 block above and so forth. The
     *                value must be an integer greater than or equal to 1.
     *
     * @return  Self for chaining.
     */
    public ParticlesTrait setHeight(int height) {
        PreCon.greaterThanZero(height, "height");

        _height = height;

        return this;
    }

    /**
     * Get the vertical offset the effect is played at relative
     * to the NPC's location.
     *
     * <p>Default is 0.</p>
     */
    public int getVerticalOffset() {
        return _verticalOffset;
    }

    /**
     * Set the vertical offset the effect is played at relative
     * to the NPC's location.
     *
     * @param offset  The offset.
     *
     * @return  Self for chaining.
     */
    public ParticlesTrait setVerticalOffset(int offset) {
        _verticalOffset = offset;

        return this;
    }

    @Override
    public void run() {
        if (!canIntervalRun())
            return;

        Location location = getNpc().getLocation(NPC_LOCATION);
        location.add(0, _verticalOffset, 0);

        for (int i = 0; i < _height; i++) {
            onEffect(location);
            location.add(0, 1.0D, 0);
        }

        if (_runCount > 0) {
            _currentRunCount++;

            if (_runCount >= _currentRunCount) {
                _currentRunCount = 0;
                setEnabled(false);
            }
        }
    }

    /**
     * Invoked to play the traits effect at the specified location.
     *
     * @param location  The location the effect should be played at.
     */
    protected abstract void onEffect(Location location);

    private boolean canIntervalRun() {
        _currentTick++;

        if (_currentTick < _interval)
            return false;

        _currentTick = 0;

        return _chance >= 1.0f || Rand.chance((int) (_chance * 100));
    }
}
