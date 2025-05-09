package com.whitehallplugins.infinitygauntlet.items.gauntlets;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import com.whitehallplugins.infinitygauntlet.files.config.DefaultModConfig;
import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.kyrptonaught.customportalapi.portal.PortalIgnitionSource;
import net.kyrptonaught.customportalapi.portal.PortalPlacer;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.Unit;

import java.util.*;
import java.util.logging.Logger;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.MOD_ID;
import static com.whitehallplugins.infinitygauntlet.items.gems.SharedGemFunctions.*;

public final class Gauntlet extends BowItem{

    public Gauntlet(Settings settings) {
        super(settings);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (user instanceof PlayerEntity) {
            int charge = (getMaxUseTime(stack, user) - remainingUseTicks);
            if (world.isClient()) {
                if (charge >= 0 && charge < getChargeTime(stack)) {
                    double chargingIncrement = (double) stack.getMaxDamage() / getChargeTime(stack);
                    stack.setDamage(stack.getMaxDamage() - (int) Math.min(charge * chargingIncrement, stack.getMaxDamage() - 1));
                } else if (charge == getChargeTime(stack)) {
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
            case 0 ->
                    InfinityGauntlet.CONFIG.getOrDefault("powerGauntletChargeTime", DefaultModConfig.POWER_GAUNTLET_CHARGE_TIME); // POWER
            case 1 ->
                    InfinityGauntlet.CONFIG.getOrDefault("spaceGauntletChargeTime", DefaultModConfig.SPACE_GAUNTLET_CHARGE_TIME);  // SPACE
            case 2 ->
                    InfinityGauntlet.CONFIG.getOrDefault("timeGauntletChargeTime", DefaultModConfig.TIME_GAUNTLET_CHARGE_TIME);  // TIME
            case 3 ->
                    InfinityGauntlet.CONFIG.getOrDefault("mindGauntletChargeTime", DefaultModConfig.MIND_GAUNTLET_CHARGE_TIME);  // MIND
            case 4 ->
                    InfinityGauntlet.CONFIG.getOrDefault("realityGauntletChargeTime", DefaultModConfig.REALITY_GAUNTLET_CHARGE_TIME);  // REALITY
            case 5 ->
                    InfinityGauntlet.CONFIG.getOrDefault("soulGauntletChargeTime", DefaultModConfig.SOUL_GAUNTLET_CHARGE_TIME);  // SOUL
            default -> 0;
        };
    }

    @Override
    public boolean canMine(ItemStack stack, BlockState state, World world, BlockPos pos, LivingEntity user) {
        return state.getBlock().getHardness() <= 50f;
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        return InfinityGauntlet.CONFIG.getOrDefault("infinityGauntletMineSpeed", DefaultModConfig.INFINITY_GAUNTLET_MINE_SPEED);
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (world.isClient()) {
            setHideDurabilityBar(stack, true);
        } else {
            int charge = getMaxUseTime(stack, user) - remainingUseTicks;
            boolean charged = charge >= getChargeTime(stack);
            switch (getCustomModelData(stack)) {
                case 0: // POWER
                    powerGemUse((ServerWorld) world, (PlayerEntity) user, charged);
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
        return true;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient()) {
            stack.setDamage(100);
            setHideDurabilityBar(stack, false);
        }
        return ActionResult.CONSUME.noIncrementStat();
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient()) {
            if (getCustomModelData(context.getStack()) == 5 && context.getWorld().getBlockState(context.getBlockPos()).isOf(InfinityGauntlet.SOUL_DIMENSION_PORTAL_FRAME_BLOCK_INFUSED)) {
                HitResult hit = Objects.requireNonNull(context.getPlayer()).raycast(6.0F, 1.0F, false);
                if (hit.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHit = (BlockHitResult)hit;
                    BlockPos usedBlockPos = blockHit.getBlockPos();
                    if (PortalPlacer.attemptPortalLight(context.getWorld(), usedBlockPos.offset(blockHit.getSide()), PortalIgnitionSource.ItemUseSource(InfinityGauntlet.SOUL_GEM).withPlayer(context.getPlayer()))) {
                        return ActionResult.SUCCESS_SERVER;
                    }
                }
            }
        }
        return super.useOnBlock(context);
    }

    public static void setHideDurabilityBar(ItemStack stack, boolean hide) {
        if (hide) {
            stack.set(DataComponentTypes.UNBREAKABLE, Unit.INSTANCE);
        } else {
            stack.remove(DataComponentTypes.UNBREAKABLE);
        }
    }

    public static void setCustomModelData(ItemStack stack, float customModelData) {
        stack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(List.of(customModelData), Collections.emptyList(), Collections.emptyList(), Collections.emptyList()));
        System.out.println(stack.getHolder());
        if (stack.getHolder() instanceof PlayerEntity player) {
            player.clearActiveItem();
        }
    }

    public static int getCustomModelData(ItemStack stack) {
        CustomModelDataComponent component = stack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        return component != null ? component.floats().getFirst().intValue() : 0;
    }

    @Override
    public boolean isCorrectForDrops(ItemStack stack, BlockState state) {
        return state.getBlock().getHardness() <= 50f;
    }

    @Override
    public void onCraft(ItemStack stack, World world) {
        if (!world.isClient()) {
            initStack(stack);
        }
        super.onCraft(stack, world);
    }

    @Override
    public void onCraftByPlayer(ItemStack stack, PlayerEntity player) {
        initStack(stack);
        super.onCraftByPlayer(stack, player);
    }

    private void initStack(ItemStack stack) {
        setHideDurabilityBar(stack, true);
        setCustomModelData(stack, 0);
    }

    @Override
    public boolean canBeEnchantedWith(ItemStack stack, RegistryEntry<Enchantment> enchantment, EnchantingContext context) {
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

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        return this.asItem().getDefaultStack();
    }

    private static void sendCurrentMode(PlayerEntity player, int mode) {
        player.sendMessage(Text.translatable("item.infinitygauntlet.gauntlet.gauntlet.tooltip1", Text.translatable("item.infinitygauntlet.gauntlet.gauntlet.power" + mode)).formatted(Formatting.WHITE), false);
    }

    public static void swapPower(PlayerEntity player, ItemStack stack) {
        stack.setHolder(player);
        switch (getCustomModelData(stack)) {
            case 1: // FROM SPACE TO TIME
                setCustomModelData(stack, 2);
                sendCurrentMode(player, 2);
                break;
            case 2: // FROM TIME TO MIND
                timeToMind(stack);
                setCustomModelData(stack, 3);
                sendCurrentMode(player, 3);
                break;
            case 3: // FROM MIND TO REALITY
                setStackGlowing(stack, false);
                setCustomModelData(stack, 4);
                sendCurrentMode(player, 4);
                break;
            case 4: // FROM REALITY TO SOUL
                realityToSoul(stack);
                setCustomModelData(stack, 5);
                sendCurrentMode(player, 5);
                break;
            case 5: // FROM SOUL TO POWER
                setStackGlowing(stack, false);
                setCustomModelData(stack, 0);
                sendCurrentMode(player, 0);
                break;
            default: // FROM POWER TO SPACE
                setCustomModelData(stack, 1);
                sendCurrentMode(player, 1);
                break;
        }
    }

    private static void timeToMind(ItemStack stack) {
        NbtCompound compound = getNbtFromItem(stack);
        try {
            if (compound.contains(MIND_GEM_NBT_ID)) {
                setStackGlowing(stack, true);
            }
        } catch (IllegalArgumentException exception) {
            Logger.getLogger(MOD_ID).warning(Text.translatable("infinitygauntlet.error.mindgemuuid").getString());
        }
    }

    private static void realityToSoul(ItemStack stack) {
        NbtCompound compound = getNbtFromItem(stack);
        try {
            if (compound.contains(SOUL_GEM_NBT_ID) &&
                    Objects.requireNonNull(compound.getList(SOUL_GEM_NBT_ID)).isPresent()) {
                setStackGlowing(stack, true);
            }
        } catch (IllegalArgumentException exception) {
            Logger.getLogger(MOD_ID).warning(Text.translatable("infinitygauntlet.error.soulgemlist").getString());
        }
    }
}
