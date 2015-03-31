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

package com.jcwhatever.nucleus.npc.traits.waypoints;

import com.jcwhatever.nucleus.npc.traits.NpcTraitPack;
import com.jcwhatever.nucleus.npc.traits.waypoints.plan.WaypointTimer;
import com.jcwhatever.nucleus.npc.traits.waypoints.provider.IWaypointProvider;
import com.jcwhatever.nucleus.npc.traits.waypoints.provider.SimpleWaypointProvider;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.events.NpcDespawnEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcDespawnEvent.NpcDespawnReason;
import com.jcwhatever.nucleus.providers.npc.events.NpcEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcSpawnEvent;
import com.jcwhatever.nucleus.providers.npc.traits.INpcTraits;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.coords.ChunkUtils;
import com.jcwhatever.nucleus.utils.coords.Coords2Di;
import com.jcwhatever.nucleus.utils.coords.MutableCoords2Di;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Collection;
import javax.annotation.Nullable;

/**
 * Pre-planned Waypoints that run even when the NPC is not spawned (due to chunk unload).
 *
 * <p>Plans each step of the path of waypoints using AStar. It uses the plan to calculate
 * where the NPC should be over time while it's despawned due to chunk unload. When the chunk is reloaded
 * or the NPC reaches a waypoint where the chunk is loaded, the NPC is spawned and moved to the point
 * it should be at.</p>
 *
 * <p>Only works with ground based paths.</p>
 *
 * <p>Waypoint location pairs are cached for reuse among multiple NPC's.</p>
 *
 * <p>Recommended only for waypoints that are not dynamically generated, the waypoints are reused (not transient),
 * the path moves across many chunks and where it's important that the NPC continues pathing even when no
 * players are around to keep the path chunks loaded.</p>
 *
 * <p>It is also recommended to use the {@link com.jcwhatever.nucleus.npc.traits.SpigotActivatedTrait} in
 * conjunction with this trait.</p>
 *
 * <p>Use instead of a chunk loader trait to path large distances at all times to improve performance and
 * memory consumption.</p>
 *
 */
public class PlannedWaypointsTrait  extends NpcTraitType {

    public static final String NAME = "PlannedWaypoints";

    /**
     * Constructor.
     */
    public PlannedWaypointsTrait() {
        super(NpcTraitPack.getPlugin(), NAME);
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new PlannedWaypoints(this);
    }

    public static class PlannedWaypoints extends WaypointsTrait {

        private static final int CHUNK_RADIUS = 2;
        private static final String META_AWAITING_RESPAWN = "__NpcTraitPack:PlannedWaypoints:AwaitingRespawn_";
        private static final MutableCoords2Di CHUNK_COORDS = new MutableCoords2Di();
        private static final Location NPC_LOCATION = new Location(null, 0, 0, 0);
        private static final Location CURRENT = new Location(null, 0, 0, 0);
        private static BukkitListener _listener;

        private final SimpleWaypointProvider _provider = new SimpleWaypointProvider();
        private final Timer _timer = new Timer();

        /**
         * Constructor.
         *
         * @param type The parent type that instantiated the trait.
         */
        PlannedWaypoints(NpcTraitType type) {
            super(type);

            if (_listener == null) {
                _listener = new BukkitListener();
                Bukkit.getPluginManager().registerEvents(_listener, NpcTraitPack.getPlugin());
            }
        }

        /**
         * Set the waypoints.
         *
         * @param locations The locations to use as waypoints.
         * @return Self for chaining.
         */
        public PlannedWaypoints setWaypoints(Collection<Location> locations) {
            PreCon.notNull(locations);

            _provider.reset();
            _provider.getWaypoints().addAll(locations);

            _timer.init(_provider);

            return this;
        }

        @Override
        protected void onAdd(INpc npc) {
            setInterval(10);
        }

        @Override
        protected void onRun() {

            if (_provider.getCurrent(CURRENT) == null || _timer.isRunning())
                return;

            // despawn NPC and start timer if surrounding chunks are not loaded.
            // This prevents the NPC from being slowed by spigot activation.

            Location npcLocation = getNpc().getLocation(NPC_LOCATION);

            if (!ChunkUtils.isNearbyChunksLoaded(npcLocation, CHUNK_RADIUS)) {
                despawn();
            }
        }

        @Override
        protected IWaypointProvider getWaypointProvider() {
            return _provider;
        }

        private void despawn() {
            double speed = getNpc().getNavigator().getCurrentSettings().getSpeed();
            if (_timer.start(speed)) {
                getNpc().despawn();
                setAwaitingRespawn(AwaitRespawnReason.INVOKED);
            }
        }

        private void spawn(Location location) {

            if (isAwaitingChunkReload()) {
                // load chunk NPC is in to cause respawn.
                Location npcLocation = getNpc().getLocation(NPC_LOCATION);
                assert npcLocation != null;

                Coords2Di chunkCoords = ChunkUtils.getChunkCoords(npcLocation, CHUNK_COORDS);
                npcLocation.getWorld().loadChunk(chunkCoords.getX(), chunkCoords.getZ());
            } else {
                // directly spawn NPC.
                getNpc().spawn(location);
            }
        }

        /*
         * Determine if the trait timer is awaiting NPC respawn.
         */
        private boolean isAwaitingRespawn() {
            return getNpc().getMeta(META_AWAITING_RESPAWN) != null;
        }

        /*
         * Determine if the trait timer is awaiting NPC respawn due to chunk unload.
         */
        private boolean isAwaitingChunkReload() {
            return AwaitRespawnReason.CHUNK_UNLOAD.equals(getNpc().getMeta(META_AWAITING_RESPAWN));
        }

        /*
         * Set the respawn flag.
         */
        private void setAwaitingRespawn(@Nullable AwaitRespawnReason reason) {
            getNpc().setMeta(META_AWAITING_RESPAWN, reason);
        }

        /*
         * Respawn flags.
         */
        private enum AwaitRespawnReason {
            CHUNK_UNLOAD,
            INVOKED
        }

        /*
         * Waypoint timer to run waypoint path while NPC is despawned.
         */
        private class Timer extends WaypointTimer {

            @Override
            protected void onMove(Location current) {

                Coords2Di chunkCoords = ChunkUtils.getChunkCoords(current, CHUNK_COORDS);
                Location npcLocation = getNpc().getLocation(NPC_LOCATION);
                if (npcLocation == null)
                    return;

                // make sure enough chunks are loaded around the location so that
                // spigot will allow the entity to be activated.
                boolean isNearbyChunksLoaded = ChunkUtils.isNearbyChunksLoaded(
                        npcLocation.getWorld(), chunkCoords.getX(), chunkCoords.getZ(), CHUNK_RADIUS);

                if (isNearbyChunksLoaded) {
                    stop(null);
                    setAwaitingRespawn(AwaitRespawnReason.INVOKED);
                    spawn(current);
                }
            }

            @Override
            protected void onPathComplete() {
                _provider.reset();
                getUpdateAgents().update("onFinish", getNpc());

                if (!getNpc().isSpawned() && _provider.hasNext()) {
                    start(getSpeed());
                }
            }
        }

        private static class BukkitListener implements Listener {

            String traitName = NpcTraitPack.getLookup(NAME);

            private PlannedWaypoints getTrait(NpcEvent event) {
                INpcTraits traits = event.getNpc().getTraits();

                if (!traits.isEnabled(traitName))
                    return null;

                return (PlannedWaypoints) traits.get(traitName);
            }

            @EventHandler(priority = EventPriority.HIGH)
            private void onNpcSpawn(final NpcSpawnEvent event) {

                final PlannedWaypoints trait = getTrait(event);
                if (trait == null)
                    return;

                trait._timer.stop(null);
                trait.setAwaitingRespawn(null);
            }

            @EventHandler
            private void onNpcDespawn(NpcDespawnEvent event) {

                PlannedWaypoints trait = getTrait(event);
                if (trait == null || !trait.isEnabled())
                    return;

                if (event.getReason() == NpcDespawnReason.CHUNK_UNLOAD) {

                    trait.setAwaitingRespawn(AwaitRespawnReason.CHUNK_UNLOAD);
                    double speed = event.getNpc().getNavigator().getCurrentSettings().getSpeed();
                    trait._timer.start(speed);
                }
            }
        }
    }
}