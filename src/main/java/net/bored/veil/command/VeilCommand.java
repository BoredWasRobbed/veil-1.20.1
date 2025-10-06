package net.bored.veil.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class VeilCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        LiteralArgumentBuilder<ServerCommandSource> veilCommand = CommandManager.literal("veil")
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal("Veil Mod - Use subcommands like /veil status"), false);
                    return 1;
                })
                .then(StatusCommand.register())
                .then(SetTechniqueCommand.register())
                .then(SetAbilityCommand.register()); // Register the new command

        dispatcher.register(veilCommand);
    }
}