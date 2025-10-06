package net.bored.veil.client.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.Collections;
import java.util.List;

public class CursedEnergyHud {
    private static String clan = "None";
    private static int cursedEnergy = 0;
    private static String cursedTechnique = "None";
    private static List<String> abilitySlots = Collections.nCopies(9, "None");


    public static void updateHudInfo(String newClan, int newCursedEnergy, String newTechnique, List<String> newAbilitySlots) {
        clan = newClan;
        cursedEnergy = newCursedEnergy;
        cursedTechnique = newTechnique;
        abilitySlots = newAbilitySlots;
    }

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && !"None".equals(clan)) { // Only render when player has a clan
                TextRenderer textRenderer = client.textRenderer;
                String clanText = "Clan: " + clan;
                String techniqueText = "Technique: " + cursedTechnique;
                String energyText = "Cursed Energy: " + cursedEnergy;

                int screenHeight = drawContext.getScaledWindowHeight();

                // Position the text above the hotbar in the bottom-left corner.
                int yPos = screenHeight - 50;
                drawContext.drawTextWithShadow(textRenderer, clanText, 10, yPos, 0xFFFFFF);
                yPos += 10;
                drawContext.drawTextWithShadow(textRenderer, techniqueText, 10, yPos, 0xFFFFFF);
                yPos += 10;
                drawContext.drawTextWithShadow(textRenderer, energyText, 10, yPos, 0xFFFFFF);

                // Display the currently selected ability
                int selectedSlot = client.player.getInventory().selectedSlot;
                if (abilitySlots != null && abilitySlots.size() > selectedSlot) {
                    String selectedAbility = abilitySlots.get(selectedSlot);
                    if (!"None".equals(selectedAbility)) {
                        int screenWidth = drawContext.getScaledWindowWidth();
                        int textWidth = textRenderer.getWidth(selectedAbility);
                        drawContext.drawTextWithShadow(textRenderer, selectedAbility, (screenWidth - textWidth) / 2, screenHeight - 55, 0xAAAAFF);
                    }
                }
            }
        });
    }
}