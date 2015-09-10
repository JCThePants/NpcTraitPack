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

import com.jcwhatever.nucleus.npc.traits.nms.INmsAnimation;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import javax.annotation.Nullable;

/*
 * Trait to add methods off animating NPC.
 */
public class AnimationTrait  extends NpcTraitType {

    private static final String NAME = "Animation";
    private static INmsAnimation HANDLER;

    /**
     * Constructor.
     */
    public AnimationTrait() {
        super(NpcTraitPack.getPlugin(), NAME);
    }

    @Override
    protected Animation createTrait(INpc npc) {
        return new Animation(this);
    }

    public static class Animation extends NpcTrait {

        private static Location CACHE_LOCATION = new Location(null, 0, 0, 0);

        /**
         * Constructor.
         *
         * @param type  The parent type that instantiated the trait.
         */
        Animation(NpcTraitType type) {
            super(type);

            if (HANDLER == null) {
                HANDLER = NpcTraitPack.getNmsManager().getHandler("ENTITY_ANIMATION");
            }
        }

        /**
         * Make the Npc swing it's arm.
         */
        public Animation swingArm() {
            sendAnimation(INmsAnimation.AnimationType.ARM_SWING);
            return this;
        }

        /**
         * Animate Npc with damage effect.
         */
        public Animation takeDamage() {
            sendAnimation(INmsAnimation.AnimationType.TAKE_DAMAGE);
            return this;
        }

        /**
         * Animate Npc with food eating effect.
         */
        public Animation eatFood() {
            sendAnimation(INmsAnimation.AnimationType.EAT_FOOD);
            return this;
        }

        /**
         * Animate Npc with critical hit effect.
         */
        public Animation critEffect() {
            sendAnimation(INmsAnimation.AnimationType.CRIT_EFFECT);
            return this;
        }

        /**
         * Animate Npc with magic critical hit effect.
         */
        public Animation magicCritEffect() {
            sendAnimation(INmsAnimation.AnimationType.MAGIC_CRIT_EFFECT);
            return this;
        }

        private void sendAnimation(INmsAnimation.AnimationType type) {
            Entity entity = getEntity();
            if (entity == null)
                return;

            Collection<Player> nearby = getNearby();
            HANDLER.sendAnimation(nearby, entity, type);
        }

        private Collection<Player> getNearby() {
            Entity entity = getEntity();
            assert entity != null;

            Location location = entity.getLocation(CACHE_LOCATION);
            return PlayerUtils.getNearbyPlayers(location, 32);
        }

        @Nullable
        private Entity getEntity() {
            if (HANDLER == null)
                return null;

            return getNpc().getEntity();
        }
    }
}
