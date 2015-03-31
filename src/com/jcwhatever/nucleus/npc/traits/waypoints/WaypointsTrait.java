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

package com.jcwhatever.nucleus.npc.traits.waypoints;

import com.jcwhatever.nucleus.npc.traits.waypoints.provider.IWaypointProvider;
import com.jcwhatever.nucleus.providers.npc.INpc;
import com.jcwhatever.nucleus.providers.npc.ai.INpcState;
import com.jcwhatever.nucleus.providers.npc.ai.goals.INpcGoal;
import com.jcwhatever.nucleus.providers.npc.ai.goals.INpcGoalAgent;
import com.jcwhatever.nucleus.providers.npc.ai.goals.INpcGoalPriority;
import com.jcwhatever.nucleus.providers.npc.traits.NpcRunnableTrait;
import com.jcwhatever.nucleus.providers.npc.traits.NpcTraitType;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.script.IScriptUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.script.ScriptUpdateSubscriber;
import com.jcwhatever.nucleus.utils.observer.update.NamedUpdateAgents;

import org.bukkit.Location;

/*
 * 
 */
public abstract class WaypointsTrait extends NpcRunnableTrait {

    private final NamedUpdateAgents _subscriberAgents = new NamedUpdateAgents();
    private static final Location CURRENT = new Location(null, 0, 0, 0);

    private INpcGoal _waypointGoal;
    private final INpcGoalPriority _waypointGoalPriority;
    private int _priority = 1;

    /**
     * Constructor.
     *
     * @param type The parent type that instantiated the trait.
     */
    protected WaypointsTrait(NpcTraitType type) {
        super(type);

        setInterval(20);

        _waypointGoalPriority = new INpcGoalPriority() {
            @Override
            public int getPriority(INpcState state) {
                return _priority;
            }
        };
    }

    /**
     * Add a one time callback that is run when the NPC has finished
     * pathing to all of the way points.
     *
     * @param subscriber  The subscriber.
     *
     * @return  Self for chaining.
     */
    public WaypointsTrait onFinish(IScriptUpdateSubscriber<INpc> subscriber) {
        PreCon.notNull(subscriber);

        _subscriberAgents.getAgent("onFinish").register(new ScriptUpdateSubscriber<>(subscriber));

        return this;
    }

    /**
     * Start pathing to the added waypoints.
     *
     * @return  Self for chaining.
     */
    public WaypointsTrait start() {
        if (_waypointGoal == null)
            _waypointGoal = createGoal();

        getNpc().getGoals().add(_waypointGoalPriority, _waypointGoal);

        return this;
    }

    /**
     * Stop pathing.
     *
     * @return  Self for chaining.
     */
    public WaypointsTrait stop() {
        if (_waypointGoal == null)
            return this;

        getNpc().getGoals().remove(_waypointGoal);

        return this;
    }

    /**
     * Clear all way points.
     */
    public void clear() {
        getWaypointProvider().reset();
    }

    /**
     * Get the waypoint goal priority.
     *
     * <p>Default value is 1.</p>
     */
    public int getPriority() {
        return _priority;
    }

    /**
     * Set the waypoint goal priority.
     *
     * @param priority  The priority. Must be greater than 0.
     *
     * @return  Self for chaining.
     */
    public WaypointsTrait setPriority(int priority) {
        PreCon.greaterThanZero(priority, "priority");

        _priority = priority;

        return this;
    }

    @Override
    protected void onRemove() {
        stop();
        clear();
        _subscriberAgents.disposeAgents();
    }

    @Override
    protected void onRun() {
        // do nothing
    }

    /**
     * Get the {@link NamedUpdateAgents} collection.
     */
    protected NamedUpdateAgents getUpdateAgents() {
        return _subscriberAgents;
    }

    /**
     * Invoked to get the waypoint provider.
     */
    protected abstract IWaypointProvider getWaypointProvider();

    /**
     * Invoked to create the waypoint goal instance.
     */
    protected WaypointGoal createGoal() {
        return new WaypointGoal();
    }

    /**
     * NPC Way point goal
     */
    protected class WaypointGoal implements INpcGoal {

        @Override
        public String getName() {
            return WaypointsTrait.this.getName();
        }

        @Override
        public void reset(INpcState state) {
            // do nothing
        }

        @Override
        public boolean canRun(INpcState state) {
            return getWaypointProvider().hasNext();
        }

        @Override
        public float getCost(INpcState state) {
            return 1.0f;
        }

        @Override
        public void pause(INpcState state) {
            // do nothing
        }

        @Override
        public void firstRun(INpcGoalAgent agent) {
            getNpc().getNavigator().cancel();
        }

        @Override
        public void run(INpcGoalAgent goalAgent) {
            if (!getNpc().getNavigator().isRunning()) {

                if (getWaypointProvider().hasNext()) {
                    next();
                } else {
                    _subscriberAgents.update("onFinish", getNpc());

                    // check waypoints again in case more were added
                    // by onFinish subscriber
                    if (getWaypointProvider().hasNext())
                        next();
                    else
                        goalAgent.finish();
                }
            }
        }

        private void next() {

            Location current = getWaypointProvider().next(CURRENT);

            getNpc().getNavigator().setTarget(current);
            getNpc().lookLocation(current);
        }
    }
}
