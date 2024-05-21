package com.whitehallplugins.infinitygauntlet.items.gems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static com.whitehallplugins.infinitygauntlet.items.gems.SharedGemFunctions.mindGemUse;

public class MindGem extends Item {
    public MindGem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean isDamageable() {
        return false;
    }


    @Override
    public boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient()) {
            mindGemUse(world, user, false);
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
