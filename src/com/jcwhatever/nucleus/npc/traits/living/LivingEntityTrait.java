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

import com.jcwhatever.nucleus.npc.traits.NpcTraitPack;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.events.NpcSpawnEvent.NpcSpawnReason;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import javax.annotation.Nullable;

/**
 * Trait for an NPC whose entity type is an implementation of a {@link org.bukkit.entity.LivingEntity}.
 *
 * <p>Extended with more specific implementations where available, used when a more specific
 * living entity trait is not available.</p>
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:LivingEntity"</p>
 */
public class LivingEntityTrait extends NpcTrait {

    private final EntityType _entityType;
    private final LinkedList<PotionEffect> _potions = new LinkedList<>();
    private boolean _canPickupItems;
    private int _maxAir;
    private double _maxHealth;
    private double _health;

    /**
     * Constructor.
     *
     * @param type The parent type that instantiated the trait.
     */
    LivingEntityTrait(NpcTraitType type, EntityType entityType) {
        super(type);

        PreCon.notNull(entityType);

        _entityType = entityType;
    }

    @Override
    public boolean isReusable() {
        return false;
    }

    /**
     * Get the NPC {@link org.bukkit.entity.EntityType} the trait was
     * instantiated for.
     */
    public EntityType getEntityType() {
        return _entityType;
    }

    /**
     * Add a potion effect to the NPC. If the NPC is not spawned,
     * the effect will be added when it is next spawned.
     *
     * @param effect  The effect to add.
     *
     * @return  Self for chaining.
     */
    public LivingEntityTrait addPotionEffect(PotionEffect effect) {
        PreCon.notNull(effect);

        LivingEntity entity = getLivingEntity();
        if (entity != null)
            entity.addPotionEffect(effect, true);
        else
            _potions.add(effect);

        return this;
    }

    /**
     * Remove a potion effect from the NPC.
     *
     * @param potionEffectType  The {@link org.bukkit.potion.PotionEffectType} or name.
     *
     * @return  Self for chaining.
     */
    public LivingEntityTrait removePotionEffect(Object potionEffectType) {
        PreCon.notNull(potionEffectType);

        PotionEffectType type = getPotionEffectType(potionEffectType);

        LivingEntity entity = getLivingEntity();
        if (entity != null) {
            entity.removePotionEffect(type);
        }

        Iterator<PotionEffect> iterator = _potions.iterator();
        while (iterator.hasNext()) {
            PotionEffect effect = iterator.next();

            if (effect.getType().equals(type))
                iterator.remove();
        }

        return this;
    }

    /**
     * Clear all potion effects from the NPC.
     */
    public void clearPotionEffects() {

        _potions.clear();

        LivingEntity entity = getLivingEntity();
        if (entity != null) {
            Collection<PotionEffect> effects = new ArrayList<>(entity.getActivePotionEffects());
            for (PotionEffect effect : effects) {
                entity.removePotionEffect(effect.getType());
            }
        }
    }

    /**
     * Determine if the NPC can pickup items.
     */
    public boolean canPickupItems() {
        return _canPickupItems;
    }

    /**
     * Set the NPC's capability to pickup items.
     *
     * @param canPickup  True to allow pickup, otherwise false.
     *
     * @return  Self for chaining.
     */
    public LivingEntityTrait setCanPickupItems(boolean canPickup) {

        _canPickupItems = canPickup;

        LivingEntity entity = getLivingEntity();
        if (entity != null)
            entity.setCanPickupItems(canPickup);

        return this;
    }

    /**
     * Get the maximum air the entity spawns with.
     */
    public int getMaximumAir() {
        return _maxAir;
    }

    /**
     * Set the maximum air the entity spawns with. If the entity is spawned,
     * its current max air is also updated.
     *
     * @param ticks  The number of ticks.
     *
     * @return  Self for chaining.
     */
    public LivingEntityTrait setMaximumAir(int ticks) {
        _maxAir = ticks;

        LivingEntity entity = getLivingEntity();
        if (entity != null)
            entity.setMaximumAir(ticks);

        return this;
    }

    /**
     * Get the max health the entity spawns with.
     */
    public double getMaxHealth() {
        return _maxHealth;
    }

    /**
     * Set the max health the entity spawns with. If the entity is spawned,
     * its current max health is also updated.
     *
     * @param health  The entities health.
     *
     * @return  Self for chaining.
     */
    public LivingEntityTrait setMaxHealth(double health) {

        _maxHealth = health;

        LivingEntity entity = getLivingEntity();
        if (entity != null)
            entity.setMaxHealth(health);

        return this;
    }

    /**
     * Get the health the entity spawns with.
     */
    public double getHealth() {
        return _health;
    }

    /**
     * Set the health the entity spawns with. If the entity is spawned,
     * its current health is also updated.
     *
     * @param health  The entities health.
     *
     * @return  Self for chaining.
     */
    public LivingEntityTrait setHealth(double health) {
        _health = health;

        LivingEntity entity = getLivingEntity();
        if (entity != null)
            entity.setHealth(health);

        return this;
    }

    @Override
    protected void onAdd(INpc npc) {
        super.onAdd(npc);

        _potions.clear();
        _canPickupItems = false;
        _maxAir = 20;
        _maxHealth = 20.0D;
        _health = 20.0D;
    }

    @Override
    protected void onSpawn(NpcSpawnReason reason) {

        EntityType type = getNpc().getTraits().getType();
        if (type != getEntityType()) {

            String traitName = NpcTraitPack.getLookup("LivingEntity");

            // re-add trait so the correct one is used.
            getNpc().getTraits().remove(traitName);
            getNpc().getTraits().add(traitName);
            return;
        }

        super.onSpawn(reason);

        LivingEntity entity = getLivingEntity();
        assert entity != null;

        // add potions
        while (!_potions.isEmpty()) {
            entity.addPotionEffect(_potions.remove(), true);
        }

        entity.setCanPickupItems(_canPickupItems);
        entity.setMaximumAir(_maxAir);
    }

    /**
     * Get the NPC living entity.
     *
     * @return  The {@link org.bukkit.entity.LivingEntity} or null if not spawned.
     */
    @Nullable
    protected LivingEntity getLivingEntity() {
        if (!getNpc().isSpawned())
            return null;

        return (LivingEntity)getNpc().getEntity();
    }
}
