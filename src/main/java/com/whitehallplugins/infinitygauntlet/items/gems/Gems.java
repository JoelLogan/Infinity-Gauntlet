package com.whitehallplugins.infinitygauntlet.items.gems;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import static com.whitehallplugins.infinitygauntlet.items.gems.SharedGemFunctions.*;

public final class Gems {
    public static final class MindGem extends BaseGem {
        public MindGem(Settings settings) {
            super(settings);
        }
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient()) {
                mindGemUse(world, user, false);
            }
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
    }
    public static final class PowerGem extends BaseGem {
        public PowerGem(Settings settings) {
            super(settings);
        }
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient()) {
                powerGemUse(world, user, false);
            }
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
    }
    public static final class RealityGem extends BaseGem {
        public RealityGem(Settings settings) {
            super(settings);
        }
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient()) {
                realityGemUse(world, user, false);
            }
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
    }
    public static final class SoulGem extends BaseGem {
        public SoulGem(Settings settings) {
            super(settings);
        }
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient()) {
                soulGemUse(world, user, false);
            }
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
    }
    public static final class SpaceGem extends BaseGem {
        public SpaceGem(Settings settings) {
            super(settings);
        }
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient()) {
                spaceGemUse(world, user, false);
            }
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
    }
    public static final class TimeGem extends BaseGem {
        public TimeGem(Settings settings) {
            super(settings);
        }
        @Override
        public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient()) {
                timeGemUse(world, user, false);
            }
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
    }
}
