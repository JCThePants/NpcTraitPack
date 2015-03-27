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
import com.jcwhatever.nucleus.providers.npc.events.NpcSpawnEvent.NpcSpawnReason;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

import javax.annotation.Nullable;

/**
 * An implementation of {@link LivingEntityTrait} specific to
 * {@link org.bukkit.entity.Villager} entities.
 *
 * <p>Trait is registered with the lookup name "NpcTraitPack:LivingEntity"</p>
 */
public class EntityVillagerTrait extends EntityAgeableTrait {

    private Villager.Profession _profession;

    /**
     * Constructor.
     *
     * @param type The parent type that instantiated the trait.
     */
    EntityVillagerTrait(NpcTraitType type) {
        super(type, EntityType.VILLAGER);
    }

    /**
     * Get the profession the villager is spawned with.
     */
    public Villager.Profession getProfession() {
        return _profession;
    }

    /**
     * Set the profession the villager is spawned with. If the villager is
     * already spawned, the current entity is also updated.
     *
     * @param profession  The {@link org.bukkit.entity.Villager.Profession}
     *                    or profession name.
     *
     * @return  Self for chaining.
     */
    public EntityVillagerTrait setProfession(Object profession) {
        PreCon.notNull(profession);

        Villager.Profession pro = getEnum(profession, Villager.Profession.class);

        _profession = pro;

        Villager villager = getVillager();
        if (villager != null)
            villager.setProfession(pro);

        return this;
    }

    @Override
    protected void onAdd(INpc npc) {
        super.onAdd(npc);

        _profession = Profession.FARMER;
    }

    @Override
    protected void onSpawn(NpcSpawnReason reason) {

        super.onSpawn(reason);

        if (isDisposed())
            return;

        Villager villager = getVillager();
        if (villager == null)
            return;

        villager.setProfession(_profession);
    }

    @Nullable
    private Villager getVillager() {
        return (Villager) getLivingEntity();
    }
}