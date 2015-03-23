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

package com.jcwhatever.nucleus.npc.traits;

import com.jcwhatever.nucleus.collections.ElementCounter;
import com.jcwhatever.nucleus.collections.ElementCounter.RemovalPolicy;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.events.NpcDespawnEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcDespawnEvent.NpcDespawnReason;
import com.jcwhatever.nucleus.providers.npc.events.NpcSpawnEvent.NpcSpawnReason;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.NpcUtils;
import com.jcwhatever.nucleus.utils.coords.Coords2Di;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

/**
 * A trait that prevents the chunk the NPC is in from unloading and optionally prevents
 * surrounding chunks from unloading.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:ChunkLoader"</p>
 */
public class ChunkLoaderTrait extends NpcTraitType {

    private static EventListener _listener;
    private static ElementCounter<Coords2Di> _keepLoaded = new ElementCounter<Coords2Di>(RemovalPolicy.REMOVE);

    @Override
    public Plugin getPlugin() {
        return NpcTraitPack.getPlugin();
    }

    @Override
    public String getName() {
        return "ChunkLoader";
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new ChunkLoader(npc, this);
    }

    public static class ChunkLoader extends NpcTrait implements Runnable {

        private static Location NPC_LOCATION = new Location(null, 0, 0, 0);

        private Coords2Di _current;
        private Set<Coords2Di> _chunks;
        private int _radius = 0;

        /**
         * Constructor.
         *
         * @param npc  The NPC the trait is for.
         * @param type The parent type that instantiated the trait.
         */
        ChunkLoader(INpc npc, NpcTraitType type) {
            super(npc, type);
        }

        /**
         * Get the chunk load radius.
         *
         * <p>Default is 0.</p>
         */
        public int getRadius() {
            return _radius;
        }

        /**
         * Set the chunk load radius.
         *
         * @param radius  The radius, not including the chunk the NPC is in.
         *
         * @return  Self for chaining.
         */
        public ChunkLoader setRadius(int radius) {
            _radius = radius;
            _chunks = null;

            return this;
        }

        @Override
        public void onSpawn(NpcSpawnReason reason) {
            if (_listener == null) {
                _listener = new EventListener();
                Bukkit.getPluginManager().registerEvents(_listener, NpcTraitPack.getPlugin());
            }
        }

        @Override
        public void onDespawn(NpcDespawnReason reason) {
            clearChunks();
        }

        @Override
        public void run() {

            Chunk chunk = getNpc().getLocation(NPC_LOCATION).getChunk();

            // check if NPC is still in same chunk
            if (_current != null && _current.getX() == chunk.getX() &&
                    _current.getZ() == chunk.getZ()) {
                return;
            }

            if (_chunks == null) {
                int capacity = ((_radius * 2) + 1) * 3;
                _chunks = new HashSet<>(
                        (int) Math.ceil(capacity + (capacity * 0.75f)));
            }
            else {
                clearChunks();
            }

            int xStart = chunk.getX() - _radius;
            int zStart = chunk.getZ() - _radius;
            int xEnd = chunk.getX() + _radius;
            int zEnd = chunk.getZ() + _radius;

            for (int x = xStart; x <= xEnd; x++) {
                for (int z = zStart; z <= zEnd; z++) {

                    Coords2Di coord = new Coords2Di(x, z);
                    _chunks.add(coord);
                    _keepLoaded.add(coord);

                    Chunk ch = chunk.getWorld().getChunkAt(x, z);
                    if (!ch.isLoaded())
                        ch.load();
                }
            }
        }

        private void clearChunks() {
            if (_chunks == null)
                return;

            for (Coords2Di coord : _chunks)
                _keepLoaded.subtract(coord);

            _current = null;
            _chunks.clear();
        }
    }

    private static class EventListener implements Listener {

        private static final CoordLookup COORD_LOOKUP = new CoordLookup();

        @EventHandler(priority = EventPriority.HIGHEST)
        private void onNpcDespawn(NpcDespawnEvent event) {

            if (event.getReason() != NpcDespawnReason.CHUNK_UNLOAD)
                return;

            if (!event.getNpc().getTraits().has(NpcTraitPack.getLookup("ChunkLoader")))
                return;

            event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.HIGH)
        private void onChunkUnload(ChunkUnloadEvent event) {

            COORD_LOOKUP.set(event.getChunk().getX(), event.getChunk().getZ());

            if (_keepLoaded.contains(COORD_LOOKUP))
                event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.MONITOR)
        private void onTeleport(EntityTeleportEvent event) {

            INpc npc = NpcUtils.getNpc(event.getEntity());
            if (npc == null)
                return;

            ChunkLoader trait = (ChunkLoader)npc.getTraits().get(NpcTraitPack.getLookup("ChunkLoader"));
            if (trait == null)
                return;

            trait.clearChunks();
        }
    }

    private static class CoordLookup {
        int x;
        int z;

        public void set(int x, int z) {
            this.x = x;
            this.z = z;
        }

        @Override
        public int hashCode() {
            return x ^ z;
        }

        public boolean equals(Object obj) {
            return obj instanceof Coords2Di &&
                    ((Coords2Di) obj).getX() == x &&
                    ((Coords2Di) obj).getZ() == z;
        }
    }
}