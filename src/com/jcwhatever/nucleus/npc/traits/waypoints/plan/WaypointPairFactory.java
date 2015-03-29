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

import com.jcwhatever.nucleus.collections.RetrievableSet;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

/**
 * Used to generate and cache {@link WaypointPair}'s.
 */
public class WaypointPairFactory {

    private static final WaypointPairMatcher MATCHER = new WaypointPairMatcher();
    private Map<World, RetrievableSet<WaypointPair>> _caches = new WeakHashMap<>(30);

    /**
     * Create or retrieve from cache a new {@link WaypointPair}.
     *
     * @param start  The start location of the pair.
     * @param end    The end location of the pair.
     * @param cache  True to cache created waypoint if not found in cache, otherwise false.
     */
    public WaypointPair getPair(Location start, Location end, boolean cache) {
        PreCon.notNull(start, "start");
        PreCon.notNull(end, "end");
        PreCon.notNull(start.getWorld(), "world");
        PreCon.isValid(start.getWorld().equals(end.getWorld()), "Worlds do not match.");

        WaypointPair pair = getCached(start, end);
        if (pair != null)
            return pair;

        pair = new WaypointPair(start, end);

        if (cache)
            cache(pair);

        return pair;
    }

    /**
     * Retrieve a {@link WaypointPair} from the cache.
     *
     * @param start  The start location of the pair.
     * @param end    The end location of the pair.
     *
     * @return  The cached {@link WaypointPair} or null if not cached.
     */
    @Nullable
    private WaypointPair getCached(Location start, Location end) {

        RetrievableSet<WaypointPair> cached = _caches.get(start.getWorld());
        if (cached == null)
            return null;

        MATCHER.setCoords(start, end);

        return cached.retrieve(MATCHER);
    }

    private void cache(WaypointPair pair) {

        RetrievableSet<WaypointPair> cached = _caches.get(pair.getWorld());
        if (cached == null) {
            cached = new RetrievableSet<>(20);
            _caches.put(pair.getWorld(), cached);
        }

        cached.add(pair);
    }
}
