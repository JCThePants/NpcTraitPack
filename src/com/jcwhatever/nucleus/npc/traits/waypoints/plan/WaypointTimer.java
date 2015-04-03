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

package com.jcwhatever.nucleus.npc.traits.waypoints.plan;

import com.jcwhatever.nucleus.npc.traits.NpcTraitPack;
import com.jcwhatever.nucleus.npc.traits.waypoints.provider.SimpleWaypointProvider;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * Times and increments waypoint positions.
 *
 * <p>Used to determine approximately where a despawned NPC should be within its
 * waypoint path given time.</p>
 */
public abstract class WaypointTimer {

    private static final Location PREVIOUS_PATH = new Location(null, 0, 0, 0);
    private static final Location CURRENT_PATH = new Location(null, 0, 0, 0);
    private static final Map<WaypointTimer, Void> _instances = new WeakHashMap<>(25);
    private static PositionTimer _timer;

    private final WaypointPlan _plan = new WaypointPlan();

    private SimpleWaypointProvider _waypoints;
    private int _pathIndex = -1;
    private int _pairIndex;
    private long _nextStepTime = 0;
    private double _speed;
    private final Location _currentPosition = new Location(null, 0, 0, 0);

    private boolean _isRunning;

    /**
     * Constructor.
     */
    public WaypointTimer () {

        if (_timer == null) {
            _timer = new PositionTimer();
            Scheduler.runTaskRepeat(NpcTraitPack.getPlugin(), 1, 1, new PositionTimer());
        }
    }

    /**
     * Initialize timer.
     *
     * @param waypoints  The waypoints to use.
     */
    public void init(SimpleWaypointProvider waypoints) {
        PreCon.notNull(waypoints);

        _waypoints = waypoints;

        if (_waypoints.hasNext()) {
            Location current = _waypoints.next(CURRENT_PATH);
            assert current != null;

            _plan.set(current.getWorld(), waypoints.getWaypoints(), true);
        } else {
            _plan.clear();
        }
    }

    /**
     * Determine if the timer is running.
     */
    public boolean isRunning() {
        return _isRunning;
    }

    /**
     * Get the speed the timer is running at.
     */
    public double getSpeed() {
        return _speed;
    }

    /**
     * Start the timer.
     *
     * @return  True if the timer was started, otherwise false.
     */
    public boolean start(double speed) {

        if (_isRunning)
            return false;

        _speed = speed;

        // get the path point index of the current destination.

        _pathIndex = -1;

        while (true) {
            _pathIndex = _plan.getPairStartIndex(_waypoints.getCurrent(CURRENT_PATH));
            if (_pathIndex == -1 && _waypoints.hasNext())
                _waypoints.next(CURRENT_PATH);
            else
                break;
        }

        if (_pathIndex == -1) {
            finish();
        } else {
            // get the pair index of the current path point index.
            _pairIndex = _plan.getPairIndex(_pathIndex);
            _isRunning = true;
            _instances.put(this, null);
            return true;
        }

        return false;
    }

    /**
     * Stop the timer and return the position the NPC should be at.
     *
     * @param output  The output location to put the current position values into.
     *
     * @return  The output location with the current position the NPC should be at or null if none.
     */
    @Nullable
    public Location stop(@Nullable Location output) {

        _isRunning = false;
        _instances.remove(this);

        if (output == null)
            return null;

        return LocationUtils.copy(_currentPosition, output);
    }

    /**
     * Dispose the timer. Can be re-initialized.
     */
    public void dispose() {
        stop(null);
        _plan.clear();
    }

    /**
     * Invoked when the NPC moves to the next position.
     *
     * @param currentPosition  The current position.
     */
    protected abstract void onMove(Location currentPosition);

    /**
     * Invoked when all waypoints have been used.
     */
    protected abstract void onPathComplete();

    /*
     * Attempt to move to the next path point position.
     */
    private void next() {

        if (System.currentTimeMillis() < _nextStepTime)
            return;

        _pathIndex++;

        // check if path is finished
        if (_pathIndex >= _plan.getPathSize() || !_waypoints.hasNext()) {

            finish();
            return;
        }

        int pairIndex = _plan.getPairIndex(_pathIndex);
        if (pairIndex == -1) {
            finish();
        } else {

            // see if the current path index is in a new path pair
            if (pairIndex != _pairIndex) {
                _waypoints.next(CURRENT_PATH);
                _pairIndex = pairIndex;
            }

            Location current = _plan.getLocation(_pathIndex, _currentPosition);

            onMove(LocationUtils.copy(current));

            // calculate when the next increment should happen
            _nextStepTime = calculateNextStepTime();
        }
    }

    /*
     * Calculate when to move to the next position.
     */
    private long calculateNextStepTime() {

        double distance;

        if (_pathIndex == 0)
            return System.currentTimeMillis();

        Location previous = _plan.getLocation(_pathIndex - 1, PREVIOUS_PATH);
        Location current = _plan.getLocation(_pathIndex, CURRENT_PATH);

        distance = previous.distanceSquared(current);

        double time = distance / (_speed * _speed);

        return System.currentTimeMillis() + (int)(time * 50 * 3);
    }

    /*
     * Stop the timer and invoke onPathComplete.
     */
    private void finish() {
        stop(null);
        onPathComplete();
    }

    /*
     * Global position timer.
     */
    private static class PositionTimer implements Runnable {

        final ArrayList<WaypointTimer> timers = new ArrayList<>(25);

        @Override
        public void run() {

            timers.addAll(_instances.keySet());

            if (timers.isEmpty())
                return;

            for (WaypointTimer timer : timers) {

                if (!timer.isRunning())
                    continue;

                timer.next();
            }

            timers.clear();
        }
    }
}
