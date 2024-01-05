package com.whitehallplugins.infinitygauntlet.items;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class Gauntlet extends BowItem {

    public Gauntlet(Settings settings) {
        super(settings);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (world.isClient()) {
            System.out.println("usageTick called: " + remainingUseTicks + " ticks remaining");
            int charge = getMaxUseTime(stack) - remainingUseTicks;
            stack.setDamage(stack.getMaxDamage() - Math.min(charge*5, stack.getMaxDamage()-1));
        }
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return state.getBlock().getHardness() <= 50f;
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return 150.0f;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (world.isClient()) {
            int charge = getMaxUseTime(stack) - remainingUseTicks;
            if (charge >= 22) {
                System.out.println("onStoppedUsing called: " + charge + " ticks charged, " + getMaxUseTime(stack) + " ticks max, " + remainingUseTicks + " ticks remaining");
                world.playSound((PlayerEntity) user, user.getX(), user.getY(), user.getZ(), SoundEvents.BLOCK_ANVIL_PLACE, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
        }
        else {
            setHideDurabilityBar(stack, true);
        }
        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    public static void setCustomModelData(ItemStack stack, int customModelData) {
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putInt("CustomModelData", customModelData);
        stack.setNbt(tag);
    }

    public static int getCustomModelData(ItemStack stack) {
        NbtCompound tag = stack.getOrCreateNbt();
        return tag != null && tag.contains("CustomModelData") ? tag.getInt("CustomModelData") : 0;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        System.out.println("getUseAction called");
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.setCurrentHand(hand);
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient()) {
            stack.setDamage(100);
            System.out.println("Gauntlet used by " + player.getName().toString());
        }
        else {
            setHideDurabilityBar(stack, false);
        }
        return TypedActionResult.consume(stack);
    }

    public static void setHideDurabilityBar(ItemStack stack, boolean hide) {
        System.out.println("setHideDurabilityBar called: " + hide);
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean("Unbreakable", hide);
        nbt.putInt("HideFlags", 4);
        stack.setNbt(nbt);
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.infinitygauntlet.gauntlet.tooltip1", Text.translatable("item.infinitygauntlet.gauntlet.power" + getCustomModelData(stack))).formatted(Formatting.GOLD));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        System.out.println("postMine called " + getCustomModelData(stack));
        switch(getCustomModelData(stack)){
            case 1:
                appendTooltip(stack, world, stack.getTooltip((PlayerEntity) miner, TooltipContext.BASIC), TooltipContext.BASIC);
                setCustomModelData(stack, 2);
                break;
            case 2:
                setCustomModelData(stack, 3);
                break;
            case 3:
                setCustomModelData(stack, 4);
                break;
            case 4:
                setCustomModelData(stack, 5);
                break;
            case 5:
                setCustomModelData(stack, 0);
                break;
            default:
                setCustomModelData(stack, 1);
                break;
        }
        return super.postMine(stack, world, state, pos, miner);
    }

    @Override
    public boolean isSuitableFor(ItemStack stack, BlockState state) {
        return state.getBlock().getHardness() <= 50f;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        if (world.isClient()) {
            world.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        else {
            setHideDurabilityBar(stack, true);
            setCustomModelData(stack, 0);
        }
        super.onCraftByPlayer(stack, world, player);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> (stack.getItem() == ItemStack.EMPTY.getItem());
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
    public boolean isUsedOnRelease(ItemStack stack) {
        return false;
    }

    @Override
    public boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        return true;
    }

}
