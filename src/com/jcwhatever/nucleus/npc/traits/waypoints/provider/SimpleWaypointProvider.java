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

package com.jcwhatever.nucleus.npc.traits.waypoints.provider;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;

import org.bukkit.Location;

import java.util.LinkedList;
import javax.annotation.Nullable;

/*
 * 
 */
public class SimpleWaypointProvider implements IWaypointProvider {

    private final LinkedList<Location> _waypoints = new LinkedList<>();
    private Location _current;

    public LinkedList<Location> getWaypoints() {
        return _waypoints;
    }

    @Override
    public void reset() {
        _waypoints.clear();
        _current = null;
    }

    @Override
    public boolean hasNext() {
        return !_waypoints.isEmpty();
    }

    @Override
    @Nullable
    public Location next(Location output) {
        PreCon.notNull(output);

        if (!hasNext())
            return null;

        _current = _waypoints.removeFirst();
        return LocationUtils.copy(_current, output);
    }

    @Override
    @Nullable
    public Location getCurrent(Location output) {
        if (_current == null)
            return null;

        return LocationUtils.copy(_current, output);
    }
}
