package com.whitehallplugins.infinitygauntlet.items.gems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class BaseGem extends Item {
    public BaseGem(Settings settings) {
        super(settings);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.NONE;
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
        return null;
    }
}
