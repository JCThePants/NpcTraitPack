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
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.utils.entity.EntityUtils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.lang.ref.WeakReference;
import javax.annotation.Nullable;

/**
 * {@link LookingTrait} handler which handles looking at {@link org.bukkit.entity.LivingEntity}'s
 * casually when they get within range.
 */
public class LookCasual extends LookHandler {

    private static final Location NPC_LOCATION = new Location(null, 0, 0, 0);
    private static final int LOOK_DURATION_MS = 1000;
    private static final int LOOK_AWAY_DURATION_MS = 2000;
    private static final int DELAY_TICKS = 5;

    private double _range = 5;
    private long _startTime = 0;
    private Mode _mode = Mode.NONE;
    private WeakReference<Entity> _previousEntity;
    private WeakReference<Entity> _currentEntity;

    private final Location _startLook = new Location(null, 0, 0, 0);
    private ReturnLook _returnLook = null;

    // delay used to prevent excessive searching for entities to look at.
    // (state variable)
    private int _delayTicks = DELAY_TICKS;

    /**
     * Constructor.
     *
     * @param trait  The parent {@link Looking} trait.
     */
    public LookCasual(Looking trait) {
        super(trait);

        setLookSteps(20);
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
    public LookCasual setRange(double range) {
        _range = range;

        return this;
    }

    /**
     * Set the look yaw and pitch the NPC will always return to after
     * casually inspecting a nearby entity.
     *
     * <p>By default, the NPC returns to looking at the direction it was
     * looking at before it began looking towards a nearby entity. Invoking this
     * method overrides this behaviour.</p>
     *
     * @param yaw    The yaw angle to look towards.
     * @param pitch  The pitch angle to look towards.
     *
     * @return  Self for chaining.
     */
    public LookCasual setReturnLook(float yaw, float pitch) {
        _returnLook = new ReturnLook(yaw, pitch, false);

        return this;
    }

    /**
     * Clear the return look yaw and pitch.
     *
     * <p>The NPC will return to looking in whatever direction it was looking
     * at before it began looking towards a nearby entity.</p>
     *
     * @return  Self for chaining.
     */
    public LookCasual clearReturnLook() {
        if (_returnLook != null)
            _returnLook.isResettable = true;

        return this;
    }
    @Override
    protected Location getLookLocation(Location output) {

        Entity closestEntity = getCurrentEntity();
        Location npcLocation = getNpc().getLocation(NPC_LOCATION);

        switch (_mode) {
            case NONE:

                // delay to prevent excessive entity searching
                if (isDelay())
                    return null;

                closestEntity = getClosest();

                if (isNewEntity(closestEntity)) {

                    _previousEntity = new WeakReference<Entity>(closestEntity);

                    Entity npcEntity = getNpc().getEntity();
                    assert npcEntity != null;

                    if (isReturnLookStatic()) {
                        LocationUtils.getYawLocation(npcLocation, 3.0D, _startLook);

                        if (_returnLook == null)
                            _returnLook = new ReturnLook(_startLook.getYaw(), _startLook.getPitch(), true);
                    }
                    else {
                        LocationUtils.getYawLocation(npcLocation, 3.0D, _returnLook.yaw, _startLook);
                    }

                    _currentEntity = new WeakReference<Entity>(closestEntity);
                    _startTime = System.currentTimeMillis();
                    _mode = Mode.LOOK_TOWARDS;
                }
                break;

            case LOOK_TOWARDS:
                if (reset(closestEntity == null))
                    return null;

                assert closestEntity != null;

                if (_startTime + LOOK_DURATION_MS > System.currentTimeMillis()) {

                    Location target = closestEntity.getLocation(output);
                    float mcAngle = LocationUtils.getYawAngle(npcLocation, target);

                    return LocationUtils.getYawLocation(npcLocation,
                            3, mcAngle, output);

                } else {
                    _startTime = System.currentTimeMillis();
                    _mode = Mode.LOOK_PAUSE;
                }
                break;

            case LOOK_PAUSE:
                if (reset(closestEntity == null))
                    return null;

                assert closestEntity != null;

                if (_startTime + LOOK_DURATION_MS < System.currentTimeMillis()) {
                    _startTime = System.currentTimeMillis();
                    _mode = Mode.LOOK_AWAY;
                }
                break;

            case LOOK_AWAY:
                if (_startTime + LOOK_AWAY_DURATION_MS > System.currentTimeMillis()) {

                    return LocationUtils.copy(_startLook, output);
                }
                else {
                    reset(true);
                }

                break;
        }

        return null;
    }

    private boolean reset(boolean doReset) {
        if (doReset) {
            _startTime = System.currentTimeMillis();
            if (_mode == Mode.LOOK_AWAY) {
                _currentEntity = null;
                _mode = Mode.NONE;
            } else {
                _mode = Mode.LOOK_AWAY;
            }
            return true;
        }
        return false;
    }

    @Nullable
    private Entity getCurrentEntity() {
        if (_currentEntity == null)
            return null;

        return _currentEntity.get();
    }

    @Nullable
    private Entity getPreviousEntity() {
        if (_previousEntity == null)
            return null;

        return _previousEntity.get();
    }

    @Nullable
    private Entity getClosest() {

        Entity npcEntity = getNpc().getEntity();
        assert npcEntity != null;

        return EntityUtils.getClosestLivingEntity(npcEntity, _range);
    }

    private float getMaxAngle(float startAngle, float currentAngle) {
        return (float)(startAngle + (0.65 * (currentAngle - startAngle)));
    }

    private boolean isNewEntity(Entity closestEntity) {
        return closestEntity != null && !closestEntity.equals(getPreviousEntity());
    }

    private boolean isReturnLookStatic() {
        return _returnLook == null || _returnLook.isResettable;
    }

    private boolean isDelay() {
        if (_delayTicks > 0) {
            _delayTicks--;
            return true;
        }
        else {
            _delayTicks = DELAY_TICKS;
            return false;
        }
    }

    private enum Mode {
        NONE,
        LOOK_TOWARDS,
        LOOK_PAUSE,
        LOOK_AWAY
    }

    private static class ReturnLook {
        float yaw;
        float pitch;
        boolean isResettable;

        ReturnLook(float yaw, float pitch, boolean isResettable) {
            this.yaw = yaw;
            this.pitch = pitch;
            this.isResettable = isResettable;
        }
    }
}

