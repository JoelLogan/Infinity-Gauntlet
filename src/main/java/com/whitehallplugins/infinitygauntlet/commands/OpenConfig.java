package com.whitehallplugins.infinitygauntlet.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.MOD_ID;
import static net.minecraft.server.command.CommandManager.literal;

public class OpenConfig {

    private OpenConfig() {}

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("infinitygauntlet:openconfig")
                .requires(source -> source.hasPermissionLevel(4))
                .executes(ctx -> sendMessage(ctx.getSource())));
    }

    private static int sendMessage(ServerCommandSource ctx) {
        if (!ctx.getServer().isDedicated()) {
            String filePath = String.valueOf(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID)
                    .resolve(InfinityGauntlet.CONFIG_FILE_NAME + ".properties").toAbsolutePath());
            ctx.sendMessage(Text.translatable("infinitygauntlet.info.openconfig")
                    .setStyle(Style.EMPTY
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("infinitygauntlet.info.openconfig.hover")))
                            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, filePath)))
                    .formatted(Formatting.UNDERLINE, Formatting.GOLD));
        }
        else {
            ctx.sendMessage(Text.translatable("infinitygauntlet.info.openconfig.inaccessible").formatted(Formatting.YELLOW));
        }
        return 1;
    }
}