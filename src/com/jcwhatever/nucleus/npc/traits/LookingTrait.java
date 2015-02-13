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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.INpcProvider;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.LocationUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.entity.EntityUtils;
import com.jcwhatever.nucleus.utils.validate.IValidator;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.ref.WeakReference;
import javax.annotation.Nullable;

/**
 * Provides 3 different ways to position the NPC. The NPC can be commanded to look
 * at a specific entity, a specific location, or any player that gets within range.
 *
 * <p>The NPC can also simulated talking by nodding its head when the talkNod flag
 * is set.</p>
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:Looking"</p>
 */
public class LookingTrait extends NpcTraitType {

    @Override
    public Plugin getPlugin() {
        return NpcTraitPack.getPlugin();
    }

    @Override
    public String getName() {
        return "Looking";
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new Looking(npc, this);
    }

    public static class Looking extends NpcTrait implements Runnable {

        private final Location _adjustedLocation = new Location(null, 0, 0, 0);
        private final Location _currentLook = new Location(null, 0, 0, 0);
        private final Location _talkNodLocation = new Location(null, 0, 0, 0);

        private WeakReference<Entity> _lookEntity;
        private Location _lookLocation;

        private boolean _isEnabled;
        private boolean _isTalkNod;

        private int _currentLookStep = 0;
        private double _range = 5;

        /**
         * Constructor.
         *
         * @param npc      The NPC the trait is for.
         * @param type     The parent type that instantiated the trait.
         */
        Looking(INpc npc, NpcTraitType type) {
            super(npc, type);
        }

        /**
         * Determine if the trait is enabled.
         *
         * <p>Disabled by default.</p>
         */
        public boolean isEnabled() {
            return _isEnabled;
        }

        /**
         * Set the traits enabled state.
         *
         * @param isEnabled  True to enable, false to disable.
         *
         * @return  Self for chaining.
         */
        public Looking setEnabled(boolean isEnabled) {
            if (_isEnabled == isEnabled)
                return this;

            _isEnabled = isEnabled;
            _currentLookStep = 0;

            return this;
        }

        /**
         * Determine if the NPC nods head as though talking
         * while looking.
         */
        public boolean isTalkNod() {
            return _isTalkNod;
        }

        /**
         * Set if the NPC nods head as though talking while looking.
         *
         * @param isEnabled  True to enable head node, otherwise false.
         *
         * @return  Self for chaining.
         */
        public Looking setTalkNod(boolean isEnabled) {
            _isTalkNod = isEnabled;

            return this;
        }

        /**
         * Look at the specified entity.
         *
         * <p>Enables trait.</p>
         *
         * @param entity  The entity to look at.
         *
         * @return  Self for chaining.
         */
        public Looking lookEntity(Entity entity) {
            PreCon.notNull(entity);

            _lookEntity = new WeakReference<Entity>(entity);
            _lookLocation = null;

            setEnabled(true);

            return this;
        }

        /**
         * Look at the specified location.
         *
         * <p>Enables trait.</p>
         *
         * @param location  The location to look at.
         *
         * @return  Self for chaining.
         */
        public Looking lookLocation(Location location) {
            PreCon.notNull(location);

            _lookEntity = null;
            _lookLocation = location;

            setEnabled(true);

            return this;
        }

        /**
         * Look at any player that gets within range.
         *
         * <p>Enables trait.</p>
         *
         * @return  Self for chaining.
         */
        public Looking lookClose() {

            _lookEntity = null;
            _lookLocation = null;

            setEnabled(true);

            return this;
        }

        /**
         * Pause the trait without clearing the current look target.
         *
         * @return  Self for chaining.
         */
        public Looking pause() {
            setEnabled(false);

            return this;
        }

        /**
         * Stop/Disable the trait. Clears look target.
         *
         * @return  Self for chaining.
         */
        public Looking stop() {

            _lookEntity = null;
            _lookLocation = null;
            setEnabled(false);

            return this;
        }

        /**
         * Get the entity that is being looked at.
         *
         * @return  The {@code Entity} or null if an entity is not set.
         */
        @Nullable
        public Entity getEntity() {
            if (_lookEntity == null)
                return null;

            return _lookEntity.get();
        }

        /**
         * Get the location that is being looked at.
         *
         * @return  The {@code Location} or null if an entity is not set.
         */
        @Nullable
        public Location getLocation() {
            return _lookLocation;
        }

        /**
         * Get the range that a player must be within in order for the
         * NPC to look while in look-close mode.
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
        public Looking setRange(double range) {
            _range = range;

            return this;
        }

        @Override
        public void run() {
            if (!_isEnabled || !getNpc().isSpawned())
                return;

            // don't perform look if npc is currently navigating
            if (getNpc().getNavigator().isRunning())
                return;

            Entity entity = getEntity();
            if (entity != null) {
                lookAtEntity();
            }
            else if (_lookLocation != null) {
                lookAtLocation();
            }
            else {
                lookAtClose();
            }
        }

        @Override
        public void save(IDataNode dataNode) {
            dataNode.set("range", _range);
        }

        @Override
        public void load(IDataNode dataNode) {
            _range = dataNode.getDouble("range", _range);
        }

        private void lookAtEntity() {

            Entity entity = getEntity();
            Location location = getNpc().getLocation();
            assert location != null;

            if (entity == null ||
                    !entity.getWorld().equals(location.getWorld())) {
                return;
            }

            if (entity.getLocation().distanceSquared(location) > 1) {

                Location finalLook = entity instanceof LivingEntity
                        ? entity.getLocation().clone().add(0, ((LivingEntity) entity).getEyeHeight() - 1.5, 0)
                        : entity.getLocation();

                Location look = getNextLook(finalLook, _currentLook, 5);

                if (_isTalkNod)
                    look = getTalkNodLocation(location, _currentLook, _talkNodLocation);

                getNpc().lookTowards(look);
            }
        }

        private void lookAtLocation() {

            Location location = getNpc().getLocation();
            assert location != null;

            if (_lookLocation == null ||
                    !_lookLocation.getWorld().equals(location.getWorld())) {
                return;
            }

            Location look = getNextLook(_lookLocation, _currentLook, 5);

            if (_isTalkNod)
                look = getTalkNodLocation(location, _currentLook, _talkNodLocation);

            getNpc().lookTowards(look);
        }

        private void lookAtClose() {

            Entity npcEntity = getNpc().getEntity();
            assert npcEntity != null;

            final INpcProvider provider = Nucleus.getProviderManager().getNpcProvider();
            assert provider != null;

            LivingEntity close = EntityUtils.getClosestLivingEntity(
                    npcEntity, _range, new IValidator<LivingEntity>() {
                        @Override
                        public boolean isValid(LivingEntity element) {
                            return element instanceof Player &&
                                    !provider.isNpc(element);
                        }
                    });

            if (close != null && close.getLocation().distanceSquared(npcEntity.getLocation()) > 1) {

                Location target = close.getLocation().clone().add(0, close.getEyeHeight() - 1.5, 0);

                Location look = getNextLook(target, _currentLook, 5);

                if (_isTalkNod)
                    look = getTalkNodLocation(close.getLocation(), _currentLook, _talkNodLocation);

                getNpc().lookTowards(look);
            }
        }

        // get next look towards target location, return result in output location,
        private Location getNextLook(Location target, Location output, int steps) {

            Location current = _currentLook.getWorld() != null ? _currentLook : target;
            boolean isTarget = LocationUtils.isLocationMatch(current, target, 0.01D);

            double deltaX = 0;
            double deltaY = 0;
            double deltaZ = 0;

            if (isTarget) {
                current = target;
            } else {

                deltaX = (target.getX() - current.getX()) / steps;
                deltaY = (target.getY() - current.getY()) / steps;
                deltaZ = (target.getZ() - current.getZ()) / steps;
            }

            output.setWorld(current.getWorld());
            output.setX(current.getX() + deltaX);
            output.setY(current.getY() + deltaY);
            output.setZ(current.getZ() + deltaZ);

            return output;
        }

        private Location getTalkNodLocation(Location npcLocation, Location lookLocation, Location output) {

            // normalize location for consistent head movement regardless
            // of player distance.

            double x = lookLocation.getX() - npcLocation.getX();
            double y = lookLocation.getY() - npcLocation.getY();
            double z = lookLocation.getZ() - npcLocation.getZ();

            double magnitude = Math.sqrt((x * x) + (y * y) + (z * z));

            if (magnitude > 1) {

                x = npcLocation.getX() + (x / magnitude);
                y = npcLocation.getY() + (y / magnitude);
                z = npcLocation.getZ() + (z / magnitude);

                _adjustedLocation.setWorld(npcLocation.getWorld());
                _adjustedLocation.setX(x);
                _adjustedLocation.setY(y);
                _adjustedLocation.setZ(z);
            }

            return LocationUtils.addNoise(_adjustedLocation, output, 0.2D, 0.4D, 0.2D);
        }
    }
}
