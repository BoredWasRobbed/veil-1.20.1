package net.bored.veil.ability;

import java.util.List;

/**
 * Represents a Cursed Technique, which is a collection of related abilities.
 */
public class CursedTechnique {
    private final String name;
    private final List<Ability> abilities;

    public CursedTechnique(String name, List<Ability> abilities) {
        this.name = name;
        this.abilities = abilities;
    }

    public String getName() {
        return name;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }
}
