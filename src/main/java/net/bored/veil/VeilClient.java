package net.bored.veil;

import net.bored.veil.client.hud.CursedEnergyHud;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;

public class VeilClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Register the HUD
        CursedEnergyHud.register();

        // Register the network receiver to update the HUD
        ClientPlayNetworking.registerGlobalReceiver(Veil.CURSED_ENERGY_SYNC_ID, (client, handler, buf, responseSender) -> {
            NbtCompound nbt = buf.readNbt();
            if (nbt != null) {
                final String receivedClan = nbt.getString("clan");
                final int receivedEnergy = nbt.getInt("cursedEnergy");

                // Schedule the HUD update on the main game thread
                client.execute(() -> CursedEnergyHud.setCursedEnergy(receivedClan, receivedEnergy));
            }
        });
    }
}

