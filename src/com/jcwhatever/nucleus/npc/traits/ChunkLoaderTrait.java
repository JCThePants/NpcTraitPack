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
import com.jcwhatever.nucleus.providers.npc.traits.NpcRunnableTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.providers.npc.Npcs;
import com.jcwhatever.nucleus.utils.coords.ChunkUtils;
import com.jcwhatever.nucleus.utils.coords.Coords2Di;
import com.jcwhatever.nucleus.utils.coords.MutableCoords2Di;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.HashSet;
import java.util.Set;

/**
 * A trait that prevents the chunk the NPC is in from unloading and optionally prevents
 * surrounding chunks from unloading.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:ChunkLoader"</p>
 */
public class ChunkLoaderTrait extends NpcTraitType {

    private static final String NAME = "ChunkLoader";

    private static EventListener _listener;
    private static ElementCounter<Coords2Di> _keepLoaded = new ElementCounter<>(RemovalPolicy.REMOVE);

    /**
     * Constructor.
     */
    public ChunkLoaderTrait() {
        super(NpcTraitPack.getPlugin(), NAME);
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new ChunkLoader(this);
    }

    public static class ChunkLoader extends NpcRunnableTrait {

        private static Location NPC_LOCATION = new Location(null, 0, 0, 0);

        private Coords2Di _current;
        private final Set<Coords2Di> _chunks = new HashSet<>(27);
        private int _radius = 0;

        private final MutableCoords2Di _matcher = new MutableCoords2Di(0, 0);
        private final MutableCoords2Di _chunkCoords = new MutableCoords2Di(0, 0);

        /**
         * Constructor.
         *
         * @param type The parent type that instantiated the trait.
         */
        ChunkLoader(NpcTraitType type) {
            super(type);
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
            _current = null;

            return this;
        }

        @Override
        protected void onSpawn(NpcSpawnReason reason) {
            if (_listener == null) {
                _listener = new EventListener();
                Bukkit.getPluginManager().registerEvents(_listener, NpcTraitPack.getPlugin());
            }
        }

        @Override
        protected void onDespawn(NpcDespawnReason reason) {
            clearChunks();
        }

        @Override
        protected void onDetach() {
            // prep for reuse
            _chunks.clear();
            _current = null;
        }

        @Override
        protected void onRun() {

            Location npcLocation = getNpc().getLocation(NPC_LOCATION);
            MutableCoords2Di chunkCoord = ChunkUtils.getChunkCoords(npcLocation, _chunkCoords);

            // check if NPC is still in same chunk
            if (_current != null && _current.getX() == chunkCoord.getX() &&
                    _current.getZ() == chunkCoord.getZ()) {
                return;
            }

            clearChunks();

            int xStart = chunkCoord.getX() - _radius;
            int zStart = chunkCoord.getZ() - _radius;
            int xEnd = chunkCoord.getX() + _radius;
            int zEnd = chunkCoord.getZ() + _radius;

            for (int x = xStart; x <= xEnd; x++) {
                for (int z = zStart; z <= zEnd; z++) {

                    _matcher.setX(x);
                    _matcher.setZ(z);

                    if (!_chunks.contains(_matcher)) {

                        Coords2Di coord = new Coords2Di(_matcher);
                        _chunks.add(coord);
                        _keepLoaded.add(coord);
                    }

                    Chunk ch = chunkCoord.getChunk(npcLocation.getWorld());
                    if (!ch.isLoaded())
                        ch.load();
                }
            }
        }

        private void clearChunks() {
            if (_current == null)
                return;

            for (Coords2Di coord : _chunks)
                _keepLoaded.subtract(coord);

            _current = null;
            _chunks.clear();
        }
    }

    private static class EventListener implements Listener {

        private static final CoordLookup COORD_LOOKUP = new CoordLookup();

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        private void onNpcDespawn(NpcDespawnEvent event) {

            if (event.getReason() != NpcDespawnReason.CHUNK_UNLOAD)
                return;

            if (!event.getNpc().getTraits().has(NpcTraitPack.getLookup(NAME)))
                return;

            event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        private void onChunkUnload(ChunkUnloadEvent event) {

            COORD_LOOKUP.set(event.getChunk().getX(), event.getChunk().getZ());

            if (_keepLoaded.contains(COORD_LOOKUP))
                event.setCancelled(true);
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        private void onTeleport(EntityTeleportEvent event) {

            INpc npc = Npcs.getNpc(event.getEntity());
            if (npc == null)
                return;

            ChunkLoader trait = (ChunkLoader)npc.getTraits().get(NpcTraitPack.getLookup(NAME));
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