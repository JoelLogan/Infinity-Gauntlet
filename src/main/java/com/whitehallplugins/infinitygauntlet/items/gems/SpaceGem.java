package com.whitehallplugins.infinitygauntlet.items.gems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static com.whitehallplugins.infinitygauntlet.items.gems.SharedGemFunctions.spaceGemUse;

public class SpaceGem extends Item {
    public SpaceGem(Settings settings) {
        super(settings);
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return false;
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
            spaceGemUse(world, user, hand);
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}
