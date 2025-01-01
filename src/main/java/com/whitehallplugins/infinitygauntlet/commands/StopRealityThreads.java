package com.whitehallplugins.infinitygauntlet.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.whitehallplugins.infinitygauntlet.items.gems.SharedGemFunctions;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class StopRealityThreads {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("infinitygauntlet:stoprealitythreads")
                .requires(source -> source.hasPermissionLevel(4))
                .executes(ctx -> stopTheads(ctx.getSource())));
    }

    private static int stopTheads(ServerCommandSource ctx) {
        SharedGemFunctions.stopAllCurrentThreads();
        ctx.sendMessage(Text.translatable("infinitygauntlet.warning.realitythreadsstopped").formatted(Formatting.GREEN));
        return 1;
    }
}
