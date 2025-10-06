package net.bored.veil.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.bored.veil.ability.AbilityRegistry;
import net.bored.veil.manager.PlayerDataManager;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.stream.Collectors;

public class SetAbilityCommand {

    // Suggests abilities from the player's current cursed technique
    private static final SuggestionProvider<ServerCommandSource> ABILITY_SUGGESTIONS = (context, builder) -> {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player != null) {
            PlayerDataManager.PlayerData data = PlayerDataManager.getPlayerData(player);
            return CommandSource.suggestMatching(
                    AbilityRegistry.getAbilityNamesForTechnique(data.cursedTechnique()).stream()
                            .map(name -> name.contains(" ") ? "\"" + name + "\"" : name) // Handle spaces
                            .collect(Collectors.toList()),
                    builder
            );
        }
        return builder.buildFuture();
    };

    public static LiteralArgumentBuilder<ServerCommandSource> register() {
        return CommandManager.literal("setability")
                .requires(source -> source.hasPermissionLevel(0)) // Usable by all players
                .then(CommandManager.argument("slot", IntegerArgumentType.integer(1, 9))
                        .then(CommandManager.argument("ability_name", StringArgumentType.greedyString())
                                .suggests(ABILITY_SUGGESTIONS)
                                .executes(SetAbilityCommand::execute)
                        )
                );
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) return 0;

        int slot = IntegerArgumentType.getInteger(context, "slot") - 1; // Convert to 0-8 index
        String abilityName = StringArgumentType.getString(context, "ability_name");

        boolean success = PlayerDataManager.setAbilityInSlot(player, slot, abilityName);

        if (success) {
            context.getSource().sendFeedback(() -> Text.literal("Set ability '" + abilityName + "' to slot " + (slot + 1)), false);
        } else {
            context.getSource().sendError(Text.literal("Ability '" + abilityName + "' is not part of your Cursed Technique."));
        }

        return success ? 1 : 0;
    }
}
