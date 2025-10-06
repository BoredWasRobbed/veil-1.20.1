package net.bored.veil.manager;

import net.bored.veil.Veil;
import net.bored.veil.ability.Ability;
import net.bored.veil.ability.AbilityRegistry;
import net.bored.veil.ability.CursedTechnique;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlayerDataManager {
    public record PlayerData(String clan, int cursedEnergy, String cursedTechnique, List<String> abilitySlots) {}

    private static final ConcurrentHashMap<UUID, PlayerData> PLAYER_DATA = new ConcurrentHashMap<>();
    private static final List<String> CLANS = Arrays.asList("Zenin", "Gojo", "Kamo", "Inumaki", "Fushiguro");
    private static final Random RANDOM = new Random();

    public static PlayerData getPlayerData(ServerPlayerEntity player) {
        return PLAYER_DATA.computeIfAbsent(player.getUuid(), uuid -> {
            String clan = CLANS.get(RANDOM.nextInt(CLANS.size()));
            int cursedEnergy = 25 + RANDOM.nextInt(76) + getClanBuff(clan);
            CursedTechnique technique = AbilityRegistry.getTechniqueForClan(clan);
            List<String> abilitySlots = new ArrayList<>(Collections.nCopies(9, "None"));

            PlayerData data = new PlayerData(clan, cursedEnergy, technique.getName(), abilitySlots);
            syncPlayerData(player, data);
            return data;
        });
    }

    public static boolean setAbilityInSlot(ServerPlayerEntity player, int slot, String abilityName) {
        PlayerData currentData = getPlayerData(player);
        CursedTechnique currentTechnique = AbilityRegistry.getTechnique(currentData.cursedTechnique());

        // Check if the ability is valid for the player's technique
        boolean isValidAbility = currentTechnique.getAbilities().stream()
                .anyMatch(ability -> ability.getName().equalsIgnoreCase(abilityName));

        if (!isValidAbility && !abilityName.equalsIgnoreCase("None")) {
            return false; // Ability does not belong to the technique
        }

        List<String> newAbilitySlots = new ArrayList<>(currentData.abilitySlots());
        newAbilitySlots.set(slot, abilityName);

        PlayerData newData = new PlayerData(currentData.clan(), currentData.cursedEnergy(), currentData.cursedTechnique(), newAbilitySlots);
        updatePlayerData(player.getUuid(), newData);
        syncPlayerData(player, newData);
        return true;
    }


    public static boolean setPlayerTechnique(ServerPlayerEntity player, String techniqueName) {
        PlayerData currentData = getPlayerData(player);

        if (AbilityRegistry.getTechnique(techniqueName) == null) {
            return false; // Technique does not exist
        }

        // Reset ability slots when technique changes
        List<String> newAbilitySlots = new ArrayList<>(Collections.nCopies(9, "None"));
        PlayerData newData = new PlayerData(currentData.clan(), currentData.cursedEnergy(), techniqueName, newAbilitySlots);

        updatePlayerData(player.getUuid(), newData);
        syncPlayerData(player, newData);
        return true;
    }


    public static void updatePlayerData(UUID uuid, PlayerData data) {
        PLAYER_DATA.put(uuid, data);
    }

    public static PlayerData getExistingPlayerData(UUID uuid) {
        return PLAYER_DATA.get(uuid);
    }

    private static int getClanBuff(String clan) {
        return switch (clan) {
            case "Zenin" -> 10;
            case "Gojo" -> 20;
            case "Kamo" -> 15;
            case "Inumaki" -> 5;
            case "Fushiguro" -> 12;
            default -> 0;
        };
    }

    public static void syncPlayerData(ServerPlayerEntity player, PlayerData data) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("clan", data.clan);
        nbt.putInt("cursedEnergy", data.cursedEnergy);
        nbt.putString("cursedTechnique", data.cursedTechnique);

        NbtList abilitySlotsNbt = new NbtList();
        for (String slot : data.abilitySlots()) {
            abilitySlotsNbt.add(NbtString.of(slot));
        }
        nbt.put("abilitySlots", abilitySlotsNbt);

        ServerPlayNetworking.send(player, Veil.CURSED_ENERGY_SYNC_ID, PacketByteBufs.create().writeNbt(nbt));
    }
}