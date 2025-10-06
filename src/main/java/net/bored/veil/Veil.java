package net.bored.veil;

import net.bored.veil.ability.AbilityRegistry;
import net.bored.veil.command.VeilCommand;
import net.bored.veil.manager.PlayerDataManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.util.Identifier;

public class Veil implements ModInitializer {
	public static final String MOD_ID = "veil";
	public static final Identifier CURSED_ENERGY_SYNC_ID = new Identifier(MOD_ID, "cursed_energy_sync");

	@Override
	public void onInitialize() {
		// Initialize abilities and techniques
		AbilityRegistry.initializeAbilities();

		// Register events and commands
		registerEvents();
		CommandRegistrationCallback.EVENT.register(VeilCommand::register);
	}

	private void registerEvents() {
		// This event ensures data is sent to the client as soon as they join.
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			// Get or create the player's data
			PlayerDataManager.PlayerData data = PlayerDataManager.getPlayerData(handler.player);
			// ALWAYS sync the data to the client upon joining to ensure the HUD appears.
			PlayerDataManager.syncPlayerData(handler.player, data);
		});

		// Keep data when a player respawns
		ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
			PlayerDataManager.PlayerData data = PlayerDataManager.getExistingPlayerData(oldPlayer.getUuid());
			if (data != null) {
				PlayerDataManager.updatePlayerData(newPlayer.getUuid(), data);
				PlayerDataManager.syncPlayerData(newPlayer, data);
			}
		});
	}
}