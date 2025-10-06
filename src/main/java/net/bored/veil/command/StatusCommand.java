package net.bored.veil.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.bored.veil.manager.PlayerDataManager;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class StatusCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("status")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    PlayerDataManager.PlayerData data = PlayerDataManager.getPlayerData(player);
                    player.sendMessage(Text.literal("Clan: " + data.clan() + ", Technique: " + data.cursedTechnique() + ", Cursed Energy: " + data.cursedEnergy()), false);
                    return 1;
                })
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(context -> {
                            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                            PlayerDataManager.PlayerData data = PlayerDataManager.getPlayerData(player);
                            context.getSource().sendFeedback(() -> Text.literal("Player " + player.getName().getString() + " - Clan: " + data.clan() + ", Technique: " + data.cursedTechnique() + ", Cursed Energy: " + data.cursedEnergy()), false);
                            return 1;
                        })
                );
    }
}
