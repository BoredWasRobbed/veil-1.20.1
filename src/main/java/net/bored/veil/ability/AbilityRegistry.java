package net.bored.veil.ability;

import net.bored.veil.ability.impl.technique.BloodManipulation;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A central registry for all Cursed Techniques and their abilities.
 * This class also maps techniques to specific clans.
 */
public class AbilityRegistry {

    private static final Map<String, CursedTechnique> TECHNIQUES = new HashMap<>();
    private static final Map<String, String> CLAN_TECHNIQUES = new HashMap<>();

    // A default "None" technique for players without a special one.
    private static final CursedTechnique NONE = new CursedTechnique("None", Collections.singletonList(
            new Ability("Basic Strike", 5) {
                @Override
                public void activate(ServerPlayerEntity player) {
                    player.sendMessage(Text.literal("You performed a basic cursed strike!"), true);
                }
            }
    ));

    public static void initializeAbilities() {
        // Register all techniques here
        register(NONE);
        register(new BloodManipulation());

        // Map clans to their techniques
        CLAN_TECHNIQUES.put("Kamo", "Blood Manipulation");
        // Add more clan-technique mappings here
        // e.g., CLAN_TECHNIQUES.put("Gojo", "Limitless");
    }

    private static void register(CursedTechnique technique) {
        TECHNIQUES.put(technique.getName(), technique);
    }

    public static CursedTechnique getTechnique(String name) {
        return TECHNIQUES.get(name);
    }

    public static Set<String> getAllTechniqueNames() {
        return TECHNIQUES.keySet();
    }

    public static List<String> getAbilityNamesForTechnique(String techniqueName) {
        CursedTechnique technique = getTechnique(techniqueName);
        if (technique != null) {
            return technique.getAbilities().stream()
                    .map(Ability::getName)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    public static CursedTechnique getTechniqueForClan(String clan) {
        String techniqueName = CLAN_TECHNIQUES.get(clan);
        if (techniqueName != null) {
            return TECHNIQUES.getOrDefault(techniqueName, NONE);
        }
        return NONE;
    }
}