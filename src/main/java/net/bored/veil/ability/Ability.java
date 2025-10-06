package net.bored.veil.ability;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * An abstract representation of a single ability.
 * Each specific ability (like Cursed Strike) will extend this class.
 */
public abstract class Ability {
    protected final String name;
    protected final int cost;

    public Ability(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    /**
     * The action to perform when the ability is activated.
     * @param player The player activating the ability.
     */
    public abstract void activate(ServerPlayerEntity player);
}
