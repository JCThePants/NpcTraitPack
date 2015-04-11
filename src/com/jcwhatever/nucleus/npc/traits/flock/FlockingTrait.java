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

package com.jcwhatever.nucleus.npc.traits.flock;

import com.jcwhatever.nucleus.npc.traits.NpcTraitPack;
import com.jcwhatever.nucleus.npc.traits.flock.behaviours.Alignment;
import com.jcwhatever.nucleus.npc.traits.flock.behaviours.Cohesion;
import com.jcwhatever.nucleus.npc.traits.flock.behaviours.Separation;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.Npcs;
import com.jcwhatever.nucleus.providers.npc.traits.NpcRunnableTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.EnumUtils;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nullable;

/**
 * Navigation flocking behaviour trait.
 */
public class FlockingTrait extends NpcTraitType {

    public enum NpcFilterPolicy {
        BLACKLIST,
        WHITELIST
    }

    /**
     * Constructor.
     */
    public FlockingTrait() {
        super(NpcTraitPack.getPlugin(), "Flocking");
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new Flocking(this);
    }

    public static class Flocking extends NpcRunnableTrait {

        private static final Location ENTITY_LOCATION = new Location(null, 0, 0, 0);
        private static final Location TARGET_LOCATION = new Location(null, 0, 0, 0);

        private Alignment _alignment;
        private Cohesion _cohesion;
        private Separation _separation;
        private List<IFlockBehaviour> _behaviours;
        private Collection<INpc> _flockFilter;
        private NpcFilterPolicy _policy;

        /**
         * Constructor.
         *
         * @param type The parent type that instantiated the trait.
         */
        protected Flocking(NpcTraitType type) {
            super(type);
        }

        @Override
        protected void onAttach(INpc npc) {

            setInterval(7);

            _alignment = new Alignment();
            _cohesion = new Cohesion();
            _separation = new Separation();

            _behaviours = null;
            _flockFilter = null;
            _policy = NpcFilterPolicy.BLACKLIST;
        }

        /**
         * Get the flock NPC filter policy.
         */
        public NpcFilterPolicy getPolicy() {
            return _policy;
        }

        /**
         * Set the flock NPC filter policy.
         *
         * @param policy  The {@link NpcFilterPolicy} or policy name.
         *
         * @return  Self for chaining.
         */
        public Flocking setPolicy(Object policy) {
            PreCon.notNull(policy, "policy");

            _policy = EnumUtils.getEnum(policy, NpcFilterPolicy.class);

            return this;
        }

        /**
         * Get the collection of {@link INpc}'s in the filter.
         */
        public Collection<INpc> getFlockFilter() {
            if (_flockFilter == null)
                _flockFilter = new HashSet<>(10);

            return _flockFilter;
        }

        /**
         * Set the collection used for NPC filtering.
         *
         * <p>The collection is not copied; direct reference to the collection
         * passed in as an argument is used.</p>
         *
         * @param filter  The NPC filter collection.
         *
         * @return  Self for chaining.
         */
        public Flocking setFlockFilter(Collection<INpc> filter) {
            PreCon.notNull(filter, "filter");

            _flockFilter = filter;

            return this;
        }

        /**
         * Get flock alignment behaviour.
         */
        public IFlockBehaviour getAlignment() {
            return _alignment;
        }

        /**
         * Get flock cohesion behaviour.
         */
        public IFlockBehaviour getCohesion() {
            return _cohesion;
        }

        /**
         * Get flock separation behaviour.
         */
        public IFlockBehaviour getSeparation() {
            return _separation;
        }

        /**
         * Add a custom flock behaviour.
         *
         * @param behaviour The behaviour to add.
         *
         * @return Self for chaining.
         */
        public Flocking addBehaviour(IFlockBehaviour behaviour) {
            PreCon.notNull(behaviour);

            if (_behaviours == null)
                _behaviours = new ArrayList<>(5);

            _behaviours.add(behaviour);

            return this;
        }

        /**
         * Clear custom flock behaviours.
         *
         * @return Self for chaining.
         */
        public Flocking clearBehaviours() {
            if (_behaviours != null)
                _behaviours.clear();

            return this;
        }

        @Override
        protected void onRun() {

            if (!getNpc().getNavigator().isRunning())
                return;

            if (!fillFlocks(_alignment, _cohesion, _separation, _behaviours))
                return;

            Vector vector = new Vector(0, 0, 0);

            applyBehaviour(vector, _alignment);
            applyBehaviour(vector, _cohesion);
            applyBehaviour(vector, _separation);

            if (_behaviours != null) {
                for (IFlockBehaviour behaviour : _behaviours) {
                    applyBehaviour(vector, behaviour);
                }
            }

            Entity entity = getNpc().getEntity();
            assert entity != null;

            entity.setVelocity(entity.getVelocity().add(vector));
        }

        /**
         * Fill behaviours flock NPC collections.
         *
         * @param alignment   The alignment behaviour.
         * @param cohesion    The cohesion behaviour.
         * @param separation  The separation behaviour.
         * @param behaviours  Additional behaviours.
         *
         * @return  True if an NPC was added to any behaviour, otherwise false.
         */
        protected boolean fillFlocks(IFlockBehaviour alignment,
                                     IFlockBehaviour cohesion,
                                     IFlockBehaviour separation,
                                     @Nullable Collection<IFlockBehaviour> behaviours) {

            Entity npcEntity = getNpc().getEntity();
            assert npcEntity != null;

            double radius = getMaxRadius();
            boolean isAdded = false;

            List<Entity> entities = npcEntity.getNearbyEntities(radius, radius, radius);
            Location npcLocation = npcEntity.getLocation(ENTITY_LOCATION);

            for (Entity entity : entities) {

                if (!(entity instanceof LivingEntity))
                    continue;

                INpc npc = Npcs.getNpc(entity);
                if (npc == null)
                    continue;

                if (!isValidNpc(npc))
                    continue;

                Location targetLocation = entity.getLocation(TARGET_LOCATION);
                double distance = npcLocation.distanceSquared(targetLocation);

                isAdded = addFlockNpc(alignment, npc, distance) || isAdded;
                isAdded = addFlockNpc(cohesion, npc, distance) || isAdded;
                isAdded = addFlockNpc(separation, npc, distance) || isAdded;

                if (behaviours != null) {
                    for (IFlockBehaviour behaviour : behaviours) {
                        isAdded = addFlockNpc(behaviour, npc, distance) || isAdded;
                    }
                }
            }

            return isAdded;
        }

        /**
         * Determine if an NPC is valid so it can be added to behaviours.
         *
         * @param npc  The NPC to check.
         */
        protected boolean isValidNpc(INpc npc) {
            return _policy == NpcFilterPolicy.BLACKLIST
                    ? _flockFilter == null || !_flockFilter.contains(npc)
                    : _flockFilter != null && _flockFilter.contains(npc);
        }

        // apply a behaviours vector the result vector
        private void applyBehaviour(Vector result, IFlockBehaviour behaviour) {
            if (behaviour.getWeight() > 0.0D)
                behaviour.modifyVector(getNpc(), result);
            behaviour.getFlock().clear();
        }

        // Add an NPC to a behaviour if the behaviours weight > 0 and is within the
        // behaviours radius.
        private boolean addFlockNpc(IFlockBehaviour behaviour, INpc npc, double distance) {
            return behaviour.getWeight() > 0.0D &&
                    distance <= behaviour.getRadius() &&
                    behaviour.getFlock().add(npc);
        }

        // get the largest radius used by the flocking behaviours
        private double getMaxRadius() {
            double radius;

            radius = _alignment.getWeight() > 0.0D ? _alignment.getRadius() : 0;
            radius = Math.max(radius, _cohesion.getWeight() > 0.0D ? _cohesion.getRadius() : 0);
            radius = Math.max(radius, _separation.getWeight() > 0.0D ? _separation.getRadius() : 0);

            if (_behaviours != null) {
                for (IFlockBehaviour behaviour : _behaviours) {
                    radius = Math.max(radius, behaviour.getWeight() > 0.0D ? behaviour.getRadius() : 0);
                }
            }

            return radius;
        }
    }
}
