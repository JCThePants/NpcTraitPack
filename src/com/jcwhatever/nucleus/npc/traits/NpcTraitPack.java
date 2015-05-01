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
import com.jcwhatever.nucleus.NucleusPlugin;
import com.jcwhatever.nucleus.npc.traits.flock.FlockingTrait;
import com.jcwhatever.nucleus.npc.traits.living.LivingEntityTraitType;
import com.jcwhatever.nucleus.npc.traits.looking.LookingTrait;
import com.jcwhatever.nucleus.npc.traits.nms.TraitNmsManager;
import com.jcwhatever.nucleus.npc.traits.particles.EnderParticlesTrait;
import com.jcwhatever.nucleus.npc.traits.particles.ExplosionParticlesTrait;
import com.jcwhatever.nucleus.npc.traits.particles.FireParticlesTrait;
import com.jcwhatever.nucleus.npc.traits.particles.GlyphParticlesTrait;
import com.jcwhatever.nucleus.npc.traits.particles.HeartParticlesTrait;
import com.jcwhatever.nucleus.npc.traits.particles.RainbowDustParticlesTrait;
import com.jcwhatever.nucleus.npc.traits.particles.SmokeParticlesTrait;
import com.jcwhatever.nucleus.npc.traits.particles.SprintDustParticlesTrait;
import com.jcwhatever.nucleus.npc.traits.waypoints.PlannedWaypointsTrait;
import com.jcwhatever.nucleus.npc.traits.waypoints.SimpleWaypointsTrait;
import com.jcwhatever.nucleus.npc.traits.waypoints.plan.WaypointPairFactory;
import com.jcwhatever.nucleus.providers.npc.INpcProvider;
import com.jcwhatever.nucleus.providers.npc.events.NpcCreateEvent;
import com.jcwhatever.nucleus.providers.npc.events.NpcEntityTypeChangeEvent;
import com.jcwhatever.nucleus.providers.npc.traits.INpcTraits;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * NPC Trait Pack Bukkit plugin for NucleusFramework.
 *
 * <p>Adds collection of NPC traits.</p>
 */
public class NpcTraitPack extends NucleusPlugin implements Listener {

    private static NpcTraitPack _instance;

    private TraitNmsManager _nmsManager;
    private WaypointPairFactory _waypointFactory;

    public static NpcTraitPack getPlugin() {
        return _instance;
    }

    public static String getLookup(String name) {
        return getPlugin().getName() + ':' + name;
    }

    public static TraitNmsManager getNmsManager() {
        return _instance._nmsManager;
    }

    public static WaypointPairFactory getWaypointPairFactory() {
        return _instance._waypointFactory;
    }

    @Override
    public String getChatPrefix() {
        return "[NPCTraitPack] ";
    }

    @Override
    public String getConsolePrefix() {
        return "[NPCTraitPack] ";
    }

    @Override
    protected void onInit() {
        _instance = this;
    }

    @Override
    protected void onEnablePlugin() {
        _instance = this;

        INpcProvider provider = Nucleus.getProviders().getNpcs();

        if (provider == null) {
            getMessenger().warning("Nucleus NPC provider not detected. Disabling.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        _nmsManager = new TraitNmsManager();
        _waypointFactory = new WaypointPairFactory();

        provider
                .registerTrait(new AggressiveTrait())
                .registerTrait(new ArcherTrait())
                .registerTrait(new FreezeHeightTrait())
                .registerTrait(new LookingTrait())
                .registerTrait(new NoDropsTrait())
                .registerTrait(new PickupVictimDropsTrait())
                .registerTrait(new ProtectPassengerTrait())
                .registerTrait(new RiderTrait())
                .registerTrait(new UnbreakingArmorTrait())
                .registerTrait(new UnbreakingWeaponTrait())
                .registerTrait(new FlockingTrait())
                .registerTrait(new SpigotActivatedTrait())
                .registerTrait(new ChunkLoaderTrait())

                .registerTrait(new SimpleWaypointsTrait())
                .registerTrait(new PlannedWaypointsTrait())

                        // particle traits
                .registerTrait(new EnderParticlesTrait())
                .registerTrait(new ExplosionParticlesTrait())
                .registerTrait(new FireParticlesTrait())
                .registerTrait(new GlyphParticlesTrait())
                .registerTrait(new HeartParticlesTrait())
                .registerTrait(new RainbowDustParticlesTrait())
                .registerTrait(new SmokeParticlesTrait())
                .registerTrait(new SprintDustParticlesTrait())

                        // auto added traits
                .registerTrait(new LivingEntityTraitType());

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    protected void onDisablePlugin() {
        _instance = null;
    }

    @EventHandler
    private void onNpcCreate(NpcCreateEvent event) {

        INpcTraits traits = event.getNpc().getTraits();

        EntityType type = traits.getType();

        if (type.isAlive()) {
            traits.add(getLookup("LivingEntity"));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onNpcEntityTypeChange(NpcEntityTypeChangeEvent event) {

        if (event.getOldType().isAlive() == event.getNewType().isAlive())
            return;

        INpcTraits traits = event.getNpc().getTraits();

        if (event.getNewType().isAlive()) {
            traits.add(getLookup("LivingEntity"));
        }
        else {
            traits.remove(getLookup("LivingEntity"));
        }
    }
}
