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
import com.jcwhatever.nucleus.collections.players.PlayerSet;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.INpcProvider;
import com.jcwhatever.nucleus.providers.npc.ai.goals.INpcGoals;
import com.jcwhatever.nucleus.providers.npc.navigator.INpcNav;
import com.jcwhatever.nucleus.providers.npc.traits.NpcRunnableTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.entity.EntityUtils;
import com.jcwhatever.nucleus.utils.validate.IValidator;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Set;
import javax.annotation.Nullable;

/**
 * The NPC that automatically attacks nearby players.
 *
 * <p>Players can be whitelisted or blacklisted from attacks.</p>
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:Aggressive"</p>
 */
public class AggressiveTrait extends NpcTraitType {

    private static final String NAME = "Aggressive";

    /**
     * Constructor.
     */
    public AggressiveTrait() {
        super(NpcTraitPack.getPlugin(), NAME);
    }

    @Override
    protected Aggressive createTrait(INpc npc) {
        return new Aggressive(this);
    }

    public static class Aggressive extends NpcRunnableTrait {

        private boolean _isWhitelist;
        private Set<Player> _filter;
        private LivingEntity _target;

        /**
         * Constructor.
         *
         * @param type  The parent type that instantiated the trait.
         */
        Aggressive(NpcTraitType type) {
            super(type);
        }

        @Override
        public Aggressive setEnabled(boolean isEnabled) {
            super.setEnabled(isEnabled);

            if (_target != null) {
                getNpc().getNavigator().cancel();
                _target = null;
            }

            return this;
        }

        /**
         * Set the target filter policy to whitelist mode.
         */
        public Aggressive whiteList() {
            _isWhitelist = true;
            return this;
        }

        /**
         * Set the target filter policy to blacklist mode.
         */
        public Aggressive blackList() {
            _isWhitelist = false;
            return this;
        }

        /**
         * Whitelist or blacklist a player as a valid target of aggression.
         *
         * @param player  The player.
         *
         * @return  Self for chaining.
         */
        public Aggressive addFilter(Player player) {
            if (_filter == null) {
                _filter = new PlayerSet(getType().getPlugin(), 10);
            }

            _filter.add(player);

            return this;
        }

        /**
         * Remove a player from the pool of valid/invalid target players.
         *
         * @param player  The player.
         *
         * @return  Self for chaining.
         */
        public Aggressive removeFilter(Player player) {
            if (_filter != null)
                _filter.remove(player);

            return this;
        }

        /**
         * Clear all target filters.
         */
        public Aggressive clearFilters() {
            if (_filter != null)
                _filter.clear();

            return this;
        }

        /**
         * Determine if the current target filter and filter policy would allow
         * the specified player to be attacked.
         *
         * @param player  The player to check.
         */
        public boolean canAttack(Player player) {
            PreCon.notNull(player);

            return _isWhitelist
                    ? _filter != null && _filter.contains(player)
                    : _filter == null || !_filter.contains(player);
        }

        /**
         * Manually set a target.
         *
         * @param target  The target of aggression.
         *
         * @return  Self for chaining.
         */
        public Aggressive setTarget(@Nullable Entity target) {

            if (!getNpc().isSpawned())
                return this;

            Entity currentTarget = getTarget();

            if (target == currentTarget)
                return this;

            if (target instanceof Player && !canAttack((Player)target))
                return this;

            INpcNav navigator = getNpc().getNavigator();
            INpcGoals goals = getNpc().getGoals();

            goals.pause();

            INpc vehicle = getNpc().getNPCVehicle();
            if (vehicle != null && vehicle.isSpawned()) {
                vehicle.getNavigator()
                        .setHostile(true)
                        .setTarget(target);
            }

            navigator.setHostile(true).setTarget(target);

            goals.resume();

            return this;
        }

        /**
         * Get the current target of aggression.
         *
         * @return  The {@link org.bukkit.entity.Entity} or null if there is no target.
         */
        @Nullable
        public Entity getTarget() {

            if (!getNpc().isSpawned())
                return null;

            return getNpc().getNavigator().getTargetEntity();
        }

        @Override
        protected void onAttach(INpc npc) {
            setInterval(3);
        }

        @Override
        protected void onDetach() {
            // prep for reuse
            _filter = null;
            _target = null;
        }

        @Override
        protected void onRun() {

            if (_target == null || _target.isDead() || !_target.isValid()) {

                final INpcProvider provider = Nucleus.getProviders().getNpcs();
                assert provider != null;

                _target = EntityUtils.getClosestLivingEntity(getNpc().getEntity(), 16,
                        new IValidator<LivingEntity>() {
                            @Override
                            public boolean isValid(LivingEntity entity) {
                                return entity instanceof Player &&
                                        canAttack((Player) entity) &&
                                        !provider.isNpc(entity);
                            }
                        });

                if (_target == null)
                    return;

                setTarget(_target);
            }

            getNpc().lookEntity(_target);

            INpc vehicle = getNpc().getNPCVehicle();
            if (vehicle != null) {
                vehicle.lookEntity(_target);
            }

            if (!getNpc().getNavigator().isRunning()) {
                setTarget(_target);
            }
        }
    }
}
