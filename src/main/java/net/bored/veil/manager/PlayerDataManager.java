package net.bored.veil.manager;

import net.bored.veil.Veil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerDataManager {
    public record PlayerData(String clan, int cursedEnergy) {}

    private static final ConcurrentHashMap<UUID, PlayerData> PLAYER_DATA = new ConcurrentHashMap<>();
    private static final List<String> CLANS = Arrays.asList("Zenin", "Gojo", "Kamo", "Inumaki", "Fushiguro");
    private static final Random RANDOM = new Random();

    public static PlayerData getPlayerData(ServerPlayerEntity player) {
        return PLAYER_DATA.computeIfAbsent(player.getUuid(), uuid -> {
            String clan = CLANS.get(RANDOM.nextInt(CLANS.size()));
            int cursedEnergy = 25 + RANDOM.nextInt(76) + getClanBuff(clan);
            PlayerData data = new PlayerData(clan, cursedEnergy);
            syncCursedEnergy(player, data);
            return data;
        });
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

    public static void syncCursedEnergy(ServerPlayerEntity player, PlayerData data) {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("clan", data.clan);
        nbt.putInt("cursedEnergy", data.cursedEnergy);
        ServerPlayNetworking.send(player, Veil.CURSED_ENERGY_SYNC_ID, PacketByteBufs.create().writeNbt(nbt));
    }
}