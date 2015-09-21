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

import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.events.NpcSpawnEvent;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.nms.INmsEntityHandler;
import com.jcwhatever.nucleus.utils.nms.NmsUtils;

/**
 * Allows setting the NPC visibility.
 */
public class VisibilityTrait extends NpcTraitType {

    private static final String NAME = "Visibility";
    private static final INmsEntityHandler HANDLER = NmsUtils.getEntityHandler();

    /**
     * Constructor.
     */
    public VisibilityTrait() {
        super(NpcTraitPack.getPlugin(), NAME);
    }

    @Override
    protected NpcTrait createTrait(INpc npc) {
        return new Visibility(this);
    }

    public static class Visibility extends NpcTrait {

        private boolean _isVisible = true;

        /**
         * Constructor.
         *
         * @param type  The parent type that instantiated the trait.
         */
        Visibility(NpcTraitType type) {
            super(type);
        }

        /**
         * Determine if the entity is set to be visible.
         */
        public boolean isVisible() {
            return _isVisible;
        }

        public Visibility setVisible(boolean isVisible) {
            _isVisible = isVisible;

            if (getNpc().isSpawned()) {
                HANDLER.setVisible(getNpc().getEntity(), isVisible);
            }
            return this;
        }

        @Override
        protected void onSpawn(NpcSpawnEvent.NpcSpawnReason reason) {
            if (!_isVisible)
                HANDLER.setVisible(getNpc().getEntity(), false);
        }
    }
}

