package com.whitehallplugins.infinitygauntlet.items.gems.replicas;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MindGemReplica extends Item {
    public MindGemReplica(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient() && user.isSneaking() && user.getName().toString().equals("TropicalElf")) {
            user.sendMessage(Text.literal("I am inevitable.").formatted(Formatting.RED));
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.infinitygauntlet.mind.gemreplica.tooltip").formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }

}
