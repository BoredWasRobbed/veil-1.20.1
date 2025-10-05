package net.bored.veil.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

public class CursedEnergyHud {
    private static String clan = "None";
    private static int cursedEnergy = 0;

    public static void setCursedEnergy(String newClan, int newCursedEnergy) {
        clan = newClan;
        cursedEnergy = newCursedEnergy;
    }

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && !"None".equals(clan)) { // Only render when player has a clan
                TextRenderer textRenderer = client.textRenderer;
                String clanText = "Clan: " + clan;
                String energyText = "Cursed Energy: " + cursedEnergy;

                int screenHeight = drawContext.getScaledWindowHeight();

                // Position the text above the hotbar in the bottom-left corner.
                drawContext.drawTextWithShadow(textRenderer, clanText, 10, screenHeight + 20, 0xFFFFFF);
                drawContext.drawTextWithShadow(textRenderer, energyText, 10, screenHeight + 10, 0xFFFFFF);
            }
        });
    }
}
