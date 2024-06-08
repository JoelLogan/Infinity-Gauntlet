package com.whitehallplugins.infinitygauntlet.mixins;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FurnaceBlockEntityMixin {

    private FurnaceBlockEntityMixin() {
        throw new IllegalStateException("Utility class");
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V", shift = At.Shift.BEFORE), cancellable = true)
    private static void removeCustomItemConsume(World world, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity blockEntity, CallbackInfo ci){
        Item fuel = blockEntity.getStack(1).getItem();
        if (fuel == InfinityGauntlet.POWER_GEM || fuel == InfinityGauntlet.GAUNTLET_ITEM) {
            if (!state.get(AbstractFurnaceBlock.LIT)) {
                state = state.with(AbstractFurnaceBlock.LIT, true);
                world.setBlockState(pos, state, Block.NOTIFY_ALL);
            }
            ci.cancel();
        }
    }
}