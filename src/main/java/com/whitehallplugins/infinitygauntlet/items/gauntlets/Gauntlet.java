package com.whitehallplugins.infinitygauntlet.items.gauntlets;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import com.whitehallplugins.infinitygauntlet.files.config.DefaultModConfig;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.MOD_ID;
import static com.whitehallplugins.infinitygauntlet.items.gems.SharedGemFunctions.*;

public final class Gauntlet extends BowItem {

    private static final String CUSTOM_MODEL_DATA_NBT = "CustomModelData";

    public Gauntlet(Settings settings) {
        super(settings);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user instanceof PlayerEntity) {
            int charge = (getMaxUseTime(stack) - remainingUseTicks);
            if (world.isClient()) {
                if (charge >= 0 && charge < getChargeTime(stack)) {
                    double chargingIncrement = (double) stack.getMaxDamage() / getChargeTime(stack);
                    stack.setDamage(stack.getMaxDamage() - (int) Math.min(charge * chargingIncrement, stack.getMaxDamage() - 1));
                }
                else if (charge == getChargeTime(stack)){
                    setHideDurabilityBar(stack, true);
                }
            } else {
                if (charge == getChargeTime(stack)) {
                    world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.75f, 0.0f);
                }
            }
        }
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    private int getChargeTime(ItemStack stack) {
        return switch (getCustomModelData(stack)) {
            case 0 -> InfinityGauntlet.CONFIG.getOrDefault("powerGauntletChargeTime", DefaultModConfig.POWER_GAUNTLET_CHARGE_TIME); // POWER
            case 1 -> InfinityGauntlet.CONFIG.getOrDefault("spaceGauntletChargeTime", DefaultModConfig.SPACE_GAUNTLET_CHARGE_TIME);  // SPACE
            case 2 -> InfinityGauntlet.CONFIG.getOrDefault("timeGauntletChargeTime", DefaultModConfig.TIME_GAUNTLET_CHARGE_TIME);  // TIME
            case 3 -> InfinityGauntlet.CONFIG.getOrDefault("mindGauntletChargeTime", DefaultModConfig.MIND_GAUNTLET_CHARGE_TIME);  // MIND
            case 4 -> InfinityGauntlet.CONFIG.getOrDefault("realityGauntletChargeTime", DefaultModConfig.REALITY_GAUNTLET_CHARGE_TIME);  // REALITY
            case 5 -> InfinityGauntlet.CONFIG.getOrDefault("soulGauntletChargeTime", DefaultModConfig.SOUL_GAUNTLET_CHARGE_TIME);  // SOUL
            default -> 0;
        };
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return state.getBlock().getHardness() <= 50f;
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return InfinityGauntlet.CONFIG.getOrDefault("infinityGauntletMineSpeed", DefaultModConfig.INFINITY_GAUNTLET_MINE_SPEED);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (world.isClient()){
            setHideDurabilityBar(stack, true);
        }
        else {
            int charge = getMaxUseTime(stack) - remainingUseTicks;
            boolean charged = charge >= getChargeTime(stack);
            switch (getCustomModelData(stack)) {
                case 0: // POWER
                    powerGemUse(world, (PlayerEntity) user, charged);
                    break;
                case 1: // SPACE
                    spaceGemUse(world, (PlayerEntity) user, charged);
                    break;
                case 2: // TIME
                    timeGemUse(world, (PlayerEntity) user, charged);
                    break;
                case 3: // MIND
                    mindGemUse(world, (PlayerEntity) user, charged);
                    break;
                case 4: // REALITY
                    realityGemUse(world, (PlayerEntity) user, charged);
                    break;
                case 5: // SOUL
                    soulGemUse(world, (PlayerEntity) user, charged);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        player.setCurrentHand(hand);
        ItemStack stack = player.getStackInHand(hand);
        if (world.isClient()) {
            stack.setDamage(100);
            setHideDurabilityBar(stack, false);
        }
        return TypedActionResult.consume(stack);
    }

    public static void setHideDurabilityBar(ItemStack stack, boolean hide) {
        NbtCompound nbt = stack.getOrCreateNbt();
        nbt.putBoolean("Unbreakable", hide);
        nbt.putInt("HideFlags", 4);
        stack.setNbt(nbt);
    }

    public static void setCustomModelData(PlayerEntity player, ItemStack stack, int customModelData) {
        NbtCompound tag = stack.getOrCreateNbt();
        tag.putInt(CUSTOM_MODEL_DATA_NBT, customModelData);
        if (stack.getItem() instanceof BowItem){
            player.clearActiveItem();
        }
        stack.setNbt(tag);
    }

    public static int getCustomModelData(ItemStack stack) {
        NbtCompound tag = stack.getOrCreateNbt();
        return tag != null && tag.contains(CUSTOM_MODEL_DATA_NBT) ? tag.getInt(CUSTOM_MODEL_DATA_NBT) : 0;
    }

    @Override
    public boolean isDamageable() {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.infinitygauntlet.gauntlet.gauntlet.tooltip1", Text.translatable("item.infinitygauntlet.gauntlet.gauntlet.power" + getCustomModelData(stack))).formatted(Formatting.GOLD));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public boolean isSuitableFor(ItemStack stack, BlockState state) {
        return state.getBlock().getHardness() <= 50f;
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, World world, PlayerEntity player) {
        if (!world.isClient()) {
            setHideDurabilityBar(stack, true);
            setCustomModelData(player, stack, 0);
        }
        super.onCraftByPlayer(stack, world, player);
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Override
    public int getRange() {
        return 0;
    }

    @Override
    public boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        return true;
    }

    private static void sendCurrentMode(PlayerEntity player, int mode){
        player.sendMessage(Text.translatable("item.infinitygauntlet.gauntlet.gauntlet.tooltip1", Text.translatable("item.infinitygauntlet.gauntlet.gauntlet.power" + mode)).formatted(Formatting.WHITE));
    }

    public static void swapPower(PlayerEntity player, ItemStack stack) {
        switch(getCustomModelData(stack)){
            case 1: // FROM SPACE TO TIME
                setCustomModelData(player, stack, 2);
                sendCurrentMode(player, 2);
                break;
            case 2: // FROM TIME TO MIND
                timeToMind(stack);
                setCustomModelData(player, stack, 3);
                sendCurrentMode(player, 3);
                break;
            case 3: // FROM MIND TO REALITY
                setStackGlowing(stack, false);
                setCustomModelData(player, stack, 4);
                sendCurrentMode(player, 4);
                break;
            case 4: // FROM REALITY TO SOUL
                realityToSoul(stack);
                setCustomModelData(player, stack, 5);
                sendCurrentMode(player, 5);
                break;
            case 5: // FROM SOUL TO POWER
                setStackGlowing(stack, false);
                setCustomModelData(player, stack, 0);
                sendCurrentMode(player, 0);
                break;
            default: // FROM POWER TO SPACE
                setCustomModelData(player, stack, 1);
                sendCurrentMode(player, 1);
                break;
        }
    }

    private static void timeToMind(ItemStack stack) {
        if (stack.hasNbt()){
            try {
                if (stack.getNbt() == null){
                    throw new NullPointerException(stack.getName() + ": NBT is null");
                }
                if (stack.getNbt().containsUuid(MIND_GEM_NBT_ID)) {
                    Objects.requireNonNull(stack.getNbt().getUuid(MIND_GEM_NBT_ID));
                    setStackGlowing(stack, true);
                }
            }
            catch (IllegalArgumentException exception) {
                Logger.getLogger(MOD_ID).warning(Text.translatable("infinitygauntlet.error.mindgemuuid").getString());
            }
        }
    }

    private static void realityToSoul(ItemStack stack) {
        if (stack.hasNbt()){
            if (stack.getNbt() == null){
                throw new NullPointerException(stack.getName() + ": NBT is null");
            }
            if (stack.getNbt().contains(SOUL_GEM_NBT_ID, NbtElement.LIST_TYPE) &&
                    !Objects.requireNonNull(stack.getNbt().getList(SOUL_GEM_NBT_ID, NbtElement.COMPOUND_TYPE)).isEmpty()) {
                setStackGlowing(stack, true);
            }
        }
    }
}
