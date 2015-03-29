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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.Coords3Di;
import com.jcwhatever.nucleus.utils.coords.MutableCoords3Di;

import org.bukkit.Location;

/**
 * Used to find a {@link WaypointPair} in a {@link java.util.HashSet} or
 * {@link java.util.HashMap}.
 */
public class WaypointPairMatcher {

    private static final MutableCoords3Di OTHER_START = new MutableCoords3Di();
    private static final MutableCoords3Di OTHER_END = new MutableCoords3Di();

    private final MutableCoords3Di _start = new MutableCoords3Di();
    private final MutableCoords3Di _end = new MutableCoords3Di();

    /**
     * Set the coordinates of the {@link WaypointPair} that the matcher will match.
     *
     * @param start  The start location.
     * @param end    The end location.
     */
    public void setCoords(Location start, Location end) {
        PreCon.notNull(start);
        PreCon.notNull(end);

        _start.copyFrom(start);
        _end.copyFrom(end);
    }

    /**
     * Set the coordinates of the {@link WaypointPair} that the matcher will match.
     *
     * @param start  The start coordinates.
     * @param end    The end coordinates.
     */
    public void setCoords(Coords3Di start, Coords3Di end) {
        PreCon.notNull(start);
        PreCon.notNull(end);

        _start.copyFrom(start);
        _end.copyFrom(end);
    }

    @Override
    public int hashCode() {
        return _start.getX() ^ _start.getY() ^ _start.getZ() ^
                _end.getX() ^ _end.getY() ^ _end.getZ();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        Coords3Di otherStart;
        Coords3Di otherEnd;

        if (obj instanceof WaypointPair) {
            WaypointPair pair = (WaypointPair)obj;
            otherStart = pair.getStart(OTHER_START);
            otherEnd = pair.getEnd(OTHER_END);
        }
        else if (obj instanceof WaypointPairMatcher) {
            WaypointPairMatcher matcher = (WaypointPairMatcher)obj;
            otherStart = matcher._start;
            otherEnd = matcher._end;
        }
        else {
            return false;
        }

        return otherStart.equals(_start) && otherEnd.equals(_end);
    }
}
