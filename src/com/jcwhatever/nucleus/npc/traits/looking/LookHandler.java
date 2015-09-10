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

import com.jcwhatever.nucleus.npc.traits.looking.LookingTrait.Looking;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;

import org.bukkit.Location;

import javax.annotation.Nullable;

/**
 * Abstract look handler.
 */
public abstract class LookHandler implements Runnable {

    private static final Location NPC_LOCATION = new Location(null, 0, 0, 0);

    private final Location _adjustedLocation = new Location(null, 0, 0, 0);
    private final Location _currentLook = new Location(null, 0, 0, 0);
    private final Location _talkNodLocation = new Location(null, 0, 0, 0);
    private final Location _lookLocation = new Location(null, 0, 0, 0);

    private final INpc _npc;
    private final Looking _trait;

    private boolean _isTalkNod;
    private int _lookSteps = 5;

    /**
     * Constructor.
     *
     * @param trait  The parent {@link Looking} trait.
     */
    public LookHandler(Looking trait) {
        _npc = trait.getNpc();
        _trait = trait;
    }

    /**
     * Get the owning {@link com.jcwhatever.nucleus.providers.npc.INpc}.
     */
    public INpc getNpc() {
        return _npc;
    }

    /**
     * Get the parent {@link LookingTrait}.
     */
    public Looking getTrait() {
        return _trait;
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
    public LookHandler setTalkNod(boolean isEnabled) {
        _isTalkNod = isEnabled;

        return this;
    }

    @Override
    public void run() {

        Location look = getLookLocation(_lookLocation);
        if (look == null)
            return;

        Location location = getNpc().getLocation(NPC_LOCATION);
        if (location == null)
            return;

        // make sure look location is in same world as NPC
        if (!location.getWorld().equals(look.getWorld()))
            return;

        // add talk nod variation
        if (_isTalkNod)
            look = getTalkNodLocation(look, _currentLook, _talkNodLocation);

        // don't look to the location all at once, take steps
        look = getNextLook(look, _currentLook, getLookSteps());

        // tell NP where to look
        getNpc().lookLocation(look);
    }

    /**
     * Invoked once per tick while the trait is running to get the location to
     * look at.
     *
     * @param output  The {@link org.bukkit.Location} to output the results into.
     *
     * @return  The output {@link org.bukkit.Location} or null to not look.
     */
    @Nullable
    protected abstract Location getLookLocation(Location output);

    /**
     * Get the number of steps to take to look at the specified target
     * location returned by {@link #getLookLocation}.
     */
    protected int getLookSteps() {
        return _lookSteps;
    }

    /**
     * Set the number of steps to take to look at the specified target
     * location returned by {@link #getLookLocation}.
     *
     * @param steps  The number of steps.
     */
    protected void setLookSteps(int steps) {
        _lookSteps = steps;
    }

    /**
     * Get the distance squared from the NPC's current location to
     * the specified {@link org.bukkit.Location}.
     *
     * @param location  The location to check.
     */
    protected double distanceSquared(Location location) {
        Location npcLocation = getNpc().getLocation(NPC_LOCATION);
        if (npcLocation == null || !npcLocation.getWorld().equals(location.getWorld()))
            return -1;
        return npcLocation.distanceSquared(location);
    }

    // get next look towards target location, return result in output location,
    private Location getNextLook(Location target, Location output, int steps) {

        if (_currentLook.getWorld() == null) {
            Location npcLocation = getNpc().getLocation(NPC_LOCATION);
            if (npcLocation == null)
                return null;

            LocationUtils.getYawLocation(npcLocation, 3.0D, npcLocation.getYaw(), _currentLook);
        }

        Location current = _currentLook;
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

    private Location getTalkNodLocation(Location targetLook, Location currentLook, Location output) {

        // normalize location for consistent head movement regardless
        // of player distance.

        double x = currentLook.getX() - targetLook.getX();
        double y = currentLook.getY() - targetLook.getY();
        double z = currentLook.getZ() - targetLook.getZ();

        double magnitude = Math.sqrt((x * x) + (y * y) + (z * z));

        if (magnitude > 1) {

            x = targetLook.getX() + (x / magnitude);
            y = targetLook.getY() + (y / magnitude);
            z = targetLook.getZ() + (z / magnitude);

            _adjustedLocation.setWorld(targetLook.getWorld());
            _adjustedLocation.setX(x);
            _adjustedLocation.setY(y);
            _adjustedLocation.setZ(z);
        }

        return LocationUtils.addNoise(_adjustedLocation, 0.2D, 0.4D, 0.2D, output);
    }
}
