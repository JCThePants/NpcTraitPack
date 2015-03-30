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
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.Coords3Di;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.annotation.Nullable;

/**
 * Creates a planned path to get to each waypoint from the previous in
 * a waypoint list.
 */
public class WaypointPlan {

    private World _world;
    private List<WaypointPair> _waypointPairs;
    private List<Coords3Di> _path;

    // map waypoint pair end location to the number of path coords that come before
    // the first location in the waypoint pair.
    private final Map<Location, Integer> _indexMap = new HashMap<>(20);

    // map path index to pair index
    private final TreeMap<Integer, Integer> _pathPairIndexMap = new TreeMap<>();

    /**
     * Clear waypoints.
     */
    public void clear() {
        _world = null;

        if (_waypointPairs != null)
            _waypointPairs.clear();

        if (_path != null)
            _path.clear();

        _indexMap.clear();
        _pathPairIndexMap.clear();
    }

    /**
     * Set the plans world and waypoint locations.
     *
     * @param world       The {@link org.bukkit.World} the waypoints are in.
     * @param waypoints   The waypoint locations.
     * @param cachePairs  True to cache paths between pairs of waypoints for other instances to use.
     */
    public void set(World world, List<Location> waypoints, boolean cachePairs) {
        PreCon.notNull(world);
        PreCon.notNull(waypoints);
        PreCon.isValid(!waypoints.isEmpty(), "waypoints cannot be empty.");

        _world = world;

        if (_waypointPairs == null) {
            _waypointPairs = new ArrayList<>((waypoints.size() / 2) + 1);
        }
        else {
            _waypointPairs.clear();
        }

        int pathSize = 0;
        int pairIndex = 0;
        Location previous = null;
        WaypointPairFactory pairFactory = NpcTraitPack.getWaypointPairFactory();

        for (Location location : waypoints) {

            if (location.getWorld() == null)
                throw new IllegalStateException("Waypoint cannot have a null world.");

            if (!world.equals(location.getWorld())) {
                throw new IllegalStateException("Waypoint is not in the correct world. " +
                        "Should be in: " + world.getName() + ", is in: " + location.getWorld().getName());
            }

            if (previous != null) {

                WaypointPair pair = pairFactory.getPair(previous, location, cachePairs);
                if (!pair.hasPath())
                    throw new RuntimeException("Failed to find path for waypoint pair.");

                _waypointPairs.add(pair);

                _indexMap.put(location, pathSize);
                _pathPairIndexMap.put(pathSize, pairIndex);

                pathSize += pair.getPathSize() - 1;
            }
            previous = location;
            pairIndex++;
        }

        pathSize += 1;

        if (_path == null) {
            _path = new ArrayList<>(pathSize);
        }
        else {
            _path.clear();
        }

        for (int i=0; i < _waypointPairs.size(); i++) {
            WaypointPair pair = _waypointPairs.get(i);
            pair.getPath(_path, i == 0, true);
        }
    }

    /**
     * Get the world the plan is in.
     */
    @Nullable
    public World getWorld() {
        return _world;
    }

    /**
     * Get all generated {@link WaypointPair}'s for the current plan.
     *
     * @param output  A list to add the results to.
     */
    public void getPairs(List<WaypointPair> output) {
        PreCon.notNull(output);

        output.addAll(_waypointPairs);
    }

    /**
     * Get the number of locations in the planned path.
     */
    public int getPathSize() {
        return _path.size();
    }

    /**
     * Get the location of a planned path point based on its index position.
     *
     * @param pathIndex  The index position of the path point.
     * @param output     The output {@link org.bukkit.Location} to put the result values into.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public Location getLocation(int pathIndex, Location output) {
        PreCon.positiveNumber(pathIndex);
        PreCon.notNull(output);

        return _path.get(pathIndex).copyTo(getWorld(), output);
    }

    /**
     * Get the path index of a location.
     *
     * @param location  The location to check.
     *
     * @return  The path index or -1 if the location is not on the path.
     */
    public int getPathIndex(Location location) {
        PreCon.notNull(location);

        Coords3Di coords = Coords3Di.fromLocation(location);

        for (int i=0; i < _path.size(); i++) {
            Coords3Di pathCoord = _path.get(0);

            if (pathCoord.equals(coords))
                return i;
        }
        return -1;
    }

    /**
     * Get all coordinate points in the planned path.
     *
     * @param output  The collection to put the results into.
     */
    public void getPath(Collection<Coords3Di> output) {
        PreCon.notNull(output);

        output.addAll(_path);
    }

    /**
     * Get the path index of the location that is the beginning
     * of the waypoint pair that ends with the specified location.
     *
     * @param pairEndLocation  The destination location of the waypoint pair.
     *
     * @return  The path index of the first location in the pair or -1 if not found.
     */
    public int getPairStartIndex(Location pairEndLocation) {
        PreCon.notNull(pairEndLocation);

        Integer index = _indexMap.get(pairEndLocation);
        if (index == null)
            return -1;

        return index;
    }

    /**
     * Get the index position of the pair of waypoints that a path point
     * is between.
     *
     * @param pathIndex  The index of the path point.
     *
     * @return  The index position of the waypoint pair.
     */
    public int getPairIndex(int pathIndex) {
        PreCon.positiveNumber(pathIndex);

        Entry<Integer, Integer> entry = _pathPairIndexMap.floorEntry(pathIndex);
        if (entry == null)
            return -1;
        return entry.getValue();
    }
}
