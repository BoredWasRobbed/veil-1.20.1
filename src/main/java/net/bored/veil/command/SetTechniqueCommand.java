package net.bored.veil.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.bored.veil.ability.AbilityRegistry;
import net.bored.veil.manager.PlayerDataManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class SetTechniqueCommand {

    // Provides suggestions for the technique name based on what's registered
    private static final SuggestionProvider<ServerCommandSource> TECHNIQUE_SUGGESTIONS = (context, builder) ->
            CommandSource.suggestMatching(AbilityRegistry.getAllTechniqueNames(), builder);

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("settechnique")
                .requires(source -> source.hasPermissionLevel(2)) // Operator-only command
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .then(CommandManager.argument("technique", StringArgumentType.greedyString()) // Changed to greedyString
                                .suggests(TECHNIQUE_SUGGESTIONS)
                                .executes(SetTechniqueCommand::execute)
                        )
                );
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        String techniqueName = StringArgumentType.getString(context, "technique");

        // Attempt to set the player's technique using the PlayerDataManager
        boolean success = PlayerDataManager.setPlayerTechnique(player, techniqueName);

        if (success) {
            context.getSource().sendFeedback(() -> Text.literal("Set " + player.getName().getString() + "'s technique to " + techniqueName), true);
            player.sendMessage(Text.literal("Your cursed technique has been set to " + techniqueName + " by an admin."), false);
        } else {
            context.getSource().sendError(Text.literal("Error: Technique '" + techniqueName + "' not found."));
        }

        return success ? 1 : 0;
    }
}

