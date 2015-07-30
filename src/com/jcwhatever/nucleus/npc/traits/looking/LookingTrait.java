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

package com.jcwhatever.nucleus.npc.traits.looking;

import com.jcwhatever.nucleus.npc.traits.NpcTraitPack;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.traits.NpcRunnableTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

/**
 * Provides 3 different ways to position the NPC. The NPC can be commanded to look
 * at a specific entity, a specific location, or any player that gets within range.
 *
 * <p>Custom look handlers can also be used.</p>
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:Looking"</p>
 */
public class LookingTrait extends NpcTraitType {

    /**
     * Constructor.
     */
    public LookingTrait() {
        super(NpcTraitPack.getPlugin(), "Looking");
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new Looking(this);
    }

    public static class Looking extends NpcRunnableTrait {

        private LookHandler _handler;

        /**
         * Constructor.
         *
         * @param type     The parent type that instantiated the trait.
         */
        Looking(NpcTraitType type) {
            super(type);
        }

        /**
         * Get the current {@link LookHandler}, if any.
         */
        @Nullable
        public LookHandler getHandler() {
            return _handler;
        }

        /**
         * Set a custom {@link LookHandler}.
         *
         * @param handler  The handler to set. Null to remove handler.
         *
         * @return  Self for chaining.
         */
        public Looking setHandler(@Nullable LookHandler handler) {
            _handler = handler;

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

            if (_handler instanceof LookEntity) {
                ((LookEntity) _handler).setLookEntity(entity);
            }
            else {
                _handler = new LookEntity(this)
                        .setLookEntity(entity);
            }

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

            if (_handler instanceof LookLocation) {
                ((LookLocation) _handler).setLookLocation(location);
            }
            else {
                _handler = new LookLocation(this)
                        .setLookLocation(location);
            }

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

            if (!(_handler instanceof LookClose))
                _handler = new LookClose(this);

            setEnabled(true);

            return this;
        }

        /**
         * Look casually at living entities that get within range.
         *
         * <p>Enables trait.</p>
         *
         * @return  Self for chaining.
         */
        public Looking lookCasual() {

            if (!(_handler instanceof LookCasual))
                _handler = new LookCasual(this);

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

            _handler = null;

            setEnabled(false);

            return this;
        }

        @Override
        protected void onAttach(INpc npc) {
            _handler = null;
        }

        @Override
        protected void onRun() {

            if (_handler == null)
                return;

            // don't perform look if npc is currently navigating
            if (getNpc().getNavigator().isRunning())
                return;

            _handler.run();
        }
    }
}
