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

public class Gauntlet extends BowItem {

    public Gauntlet(Settings settings) {
        super(settings);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        int charge = (getMaxUseTime(stack) - remainingUseTicks) - 5; // -5 For LAG
        if (world.isClient()) {
            System.out.println("usageTick called: " + remainingUseTicks + " ticks remaining");
            if (charge >= 0) {
                stack.setDamage(stack.getMaxDamage() - Math.min(charge * 5, stack.getMaxDamage() - 1));
            }
        }
        else {
            if (charge == 20) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.75f, 2.0f);
            }
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
        if (!world.isClient()) {
            int charge = getMaxUseTime(stack) - remainingUseTicks;
            setHideDurabilityBar(stack, true);
            if (charge >= 22) {
                System.out.println("onStoppedUsing called: " + charge + " ticks charged, " + getMaxUseTime(stack) + " ticks max, " + remainingUseTicks + " ticks remaining");
                user.sendMessage(Text.literal("You drew 22"));
            }
        }
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
    public boolean isSuitableFor(ItemStack stack, BlockState state) {
        return state.getBlock().getHardness() <= 50f;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isClient()) {
            setHideDurabilityBar(stack, true);
            setCustomModelData(stack, 0);
        }
        super.onCraftByPlayer(stack, world, player);
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
    public int getRange() {
        return 0;
    }

    @Override
    public boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        return true;
    }

    public static void swapPower(ItemStack stack) {
        switch(getCustomModelData(stack)){
            case 1:
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
    }

}
