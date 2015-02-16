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

package com.jcwhatever.nucleus.npc.traits.living;

import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;

import javax.annotation.Nullable;

/**
 * An implementation of {@link LivingEntityTrait} specific to
 * {@link org.bukkit.entity.Horse} entities.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:LivingEntity"</p>
 */
public class EntityHorseTrait extends LivingEntityTrait {

    private Horse.Color _color = Horse.Color.WHITE;
    private Horse.Style _style = Style.NONE;
    private Horse.Variant _variant = Variant.HORSE;
    private int _domestication = 20;
    private int _maxDomestication = 20;
    private double _jumpStrength;
    private boolean _isCarryingChest;

    /**
     * Constructor.
     *
     * @param npc  The NPC the trait is for.
     * @param type The parent type that instantiated the trait.
     */
    EntityHorseTrait(INpc npc, NpcTraitType type) {
        super(npc, type, EntityType.HORSE);
    }

    /**
     * Get the color the horse spawns with.
     */
    public Horse.Color getColor() {
        return _color;
    }

    /**
     * Set the color the horse spawns with. If the horse is spawned,
     * its current color is also updated.
     *
     * @param color  The {@link Horse.Color} or color name.
     *
     * @return  Self for chaining.
     */
    public EntityHorseTrait setColor(Object color) {

        _color = getEnum(color, Horse.Color.class);

        Horse horse = getHorse();
        if (horse != null)
            horse.setColor(_color);

        return this;
    }

    /**
     * Get the style the horse spawns with.
     */
    public Horse.Style getStyle() {
        return _style;
    }

    /**
     * Set the style the horse spawns with. If the horse is spawned,
     * its current color is also updated.
     *
     * @param style  The {@link Horse.Style} or style name.
     *
     * @return  Self for chaining.
     */
    public EntityHorseTrait setStyle(Object style) {
        PreCon.notNull(style);

        _style = getEnum(style, Horse.Style.class);

        Horse horse = getHorse();
        if (horse != null)
            horse.setStyle(_style);

        return this;
    }

    /**
     * Get the variant the horse is spawned with.
     */
    public Horse.Variant getVariant() {
        return _variant;
    }

    /**
     * Set the variant the horse is spawned with. If the horse is spawned,
     * its current variant is also updated.
     *
     * @param variant  The {@link Horse.Variant} or variant name.
     *
     * @return  Self for chaining.
     */
    public EntityHorseTrait setVariant(Object variant) {
        PreCon.notNull(variant);

        _variant = getEnum(variant, Horse.Variant.class);

        Horse horse = getHorse();
        if (horse != null)
            horse.setStyle(_style);

        return this;
    }

    /**
     * Get the domestication level the horse is spawned with.
     */
    public int getDomestication() {
        return _domestication;
    }

    /**
     * Set the domestication level the horse is spawned with. If the horse
     * is spawned, its current domestication level is also updated.
     *
     * @param domestication  The domestication level.
     *
     * @return  Self for chaining.
     */
    public EntityHorseTrait setDomestication(int domestication) {
        _domestication = domestication;

        Horse horse = getHorse();
        if (horse != null)
            horse.setDomestication(domestication);

        return this;
    }

    /**
     * Get the max domestication level the horse is spawned with.
     */
    public int getMaxDomestication() {
        return _maxDomestication;
    }

    /**
     * Set the max domestication level the horse is spawned with. If the
     * horse is already spawned, its current max domestication level
     * is also updated.
     *
     * @param maxDomestication  The max domestication level.
     *
     * @return  Self for chaining.
     */
    public EntityHorseTrait setMaxDomestication(int maxDomestication) {
        _maxDomestication = maxDomestication;

        Horse horse = getHorse();
        if (horse != null)
            horse.setMaxDomestication(maxDomestication);

        return this;
    }

    /**
     * Get the jump strength the horse is spawned with.
     */
    public double getJumpStrength() {
        return _jumpStrength;
    }

    /**
     * Set the jump strength the horse is spawned with. If the horse is
     * already spawned, its current max jump strength is also updated.
     *
     * @param strength  The jump strength.
     *
     * @return  Self for chaining.
     */
    public EntityHorseTrait setJumpStrength(double strength) {

        _jumpStrength = strength;

        Horse horse = getHorse();
        if (horse != null)
            horse.setJumpStrength(strength);

        return this;
    }

    /**
     * Determine if the horse is spawned with a chest.
     */
    public boolean isCarryingChest() {
        return _isCarryingChest;
    }

    /**
     * Set the if the horse is spawned with a chest. If the horse is
     * already spawned, its current chest flag is also updated.
     *
     * @param isCarryingChest  True to carry a chest, otherwise false.
     *
     * @return  Self for chaining.
     */
    public EntityHorseTrait setCarryingChest(boolean isCarryingChest) {
        _isCarryingChest = isCarryingChest;

        Horse horse = getHorse();
        if (horse != null)
            horse.setCarryingChest(isCarryingChest);

        return this;
    }

    @Override
    public void onSpawn() {

        super.onSpawn();

        if (isDisposed())
            return;

        Horse horse = getHorse();
        if (horse == null)
            return;

        horse.setCarryingChest(_isCarryingChest);
        horse.setVariant(_variant);
        horse.setStyle(_style);
        horse.setJumpStrength(_jumpStrength);
        horse.setColor(_color);
        horse.setMaxDomestication(_maxDomestication);
        horse.setDomestication(_domestication);
    }

    @Nullable
    private Horse getHorse() {
        return (Horse)getLivingEntity();
    }
}

