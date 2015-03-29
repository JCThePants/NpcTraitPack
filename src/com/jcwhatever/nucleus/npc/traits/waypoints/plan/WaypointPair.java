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
import com.jcwhatever.nucleus.utils.astar.AStar;
import com.jcwhatever.nucleus.utils.astar.AStarResult;
import com.jcwhatever.nucleus.utils.astar.AStarResult.AStarResultStatus;
import com.jcwhatever.nucleus.utils.astar.AStarUtils;
import com.jcwhatever.nucleus.utils.coords.Coords3Di;
import com.jcwhatever.nucleus.utils.coords.MutableCoords3Di;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

/**
 * Represents a pair of waypoints and the path between them.
 *
 * <p>Uses {@link com.jcwhatever.nucleus.utils.astar.AStar} to generate path points between the start
 * and end locations of the waypoint pair.</p>
 */
public class WaypointPair {

    private final World _world;
    private final MutableCoords3Di _start = new MutableCoords3Di();
    private final MutableCoords3Di _end = new MutableCoords3Di();
    private final AStarResult _path;
    private final int _hash;

    /**
     * Constructor.
     *
     * @param start  The start location.
     * @param end    The end location.
     */
    public WaypointPair(Location start, Location end) {
        PreCon.notNull(start);
        PreCon.notNull(end);
        PreCon.notNull(start.getWorld());

        _start.copyFrom(start);
        _end.copyFrom(end);
        _world = start.getWorld();

        _hash = start.getBlockX() ^ start.getBlockY() ^ start.getBlockZ() ^
                end.getBlockX() ^ end.getBlockY() ^ end.getBlockZ();

        AStar astar = AStarUtils.getAStar(start.getWorld());
        _path = AStarUtils.searchSurface(astar, start, end);
    }

    /**
     * The world the pair is in.
     */
    public World getWorld() {
        return _world;
    }

    /**
     * Determine if AStar was able to find a path from the start location
     * to the end location.
     */
    public boolean hasPath() {
        return _path.getStatus() == AStarResultStatus.RESOLVED;
    }

    /**
     * Copy the start location coordinates into a {@link com.jcwhatever.nucleus.utils.coords.MutableCoords3Di}
     * instance.
     *
     * @param output  The output coordinates.
     *
     * @return  The output coordinates.
     */
    public MutableCoords3Di getStart(MutableCoords3Di output) {
        PreCon.notNull(output);

        output.copyFrom(_start);
        return output;
    }

    /**
     * Copy the end location coordinates into a {@link com.jcwhatever.nucleus.utils.coords.MutableCoords3Di}
     * instance.
     *
     * @param output  The output coordinates.
     *
     * @return  The output coordinates.
     */
    public MutableCoords3Di getEnd(MutableCoords3Di output) {
        PreCon.notNull(output);

        output.copyFrom(_end);
        return output;
    }

    /**
     * Get the generated path coordinates and place into a list.
     *
     * @param list  The list to place the results into.
     */
    public void getPath(List<Coords3Di> list) {
        PreCon.notNull(list);

        list.addAll(_path.values());
    }

    /**
     * Get the number of path coordinates from the start to the end of
     * the waypoint pair.
     */
    public int getPathSize() {
        return _path.getPathDistance();
    }

    @Override
    public int hashCode() {
        return _hash;
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this ||
                obj instanceof WaypointPair &&
                        ((WaypointPair) obj)._start.equals(_start) &&
                        ((WaypointPair) obj)._end.equals(_end);
    }
}
