package com.whitehallplugins.infinitygauntlet.items.gems;

import com.whitehallplugins.infinitygauntlet.client.InfinityGauntletClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import static com.whitehallplugins.infinitygauntlet.items.gems.SharedGemFunctions.*;

public final class Gems {
    private Gems(){}
    public static final class MindGem extends BaseGem {
        public MindGem(Settings settings) {
            super(settings);
        }
        @Override
        public ActionResult use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient()) {
                mindGemUse(world, user, false);
            }
            else {
                InfinityGauntletClient.triggerAnimation(0x0072d5);
            }
            return ActionResult.PASS;
        }
    }
    public static final class PowerGem extends BaseGem {
        public PowerGem(Settings settings) {
            super(settings);
        }
        @Override
        public ActionResult use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient()) {
                powerGemUse((ServerWorld) world, user, false);
            }
            else {
                InfinityGauntletClient.triggerAnimation(0xff0000);
            }
            return ActionResult.PASS;
        }
    }
    public static final class RealityGem extends BaseGem {
        public RealityGem(Settings settings) {
            super(settings);
        }
        @Override
        public ActionResult use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient()) {
                realityGemUse(world, user, false);
            }
            else {
                InfinityGauntletClient.triggerAnimation(0xffea4d);
            }
            return ActionResult.PASS;
        }
    }
    public static final class SoulGem extends BaseGem {
        public SoulGem(Settings settings) {
            super(settings);
        }
        @Override
        public ActionResult use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient()) {
                soulGemUse(world, user, false);
            }
            else {
                InfinityGauntletClient.triggerAnimation(0xffaa18);
            }
            return ActionResult.PASS;
        }
    }
    public static final class SpaceGem extends BaseGem {
        public SpaceGem(Settings settings) {
            super(settings);
        }
        @Override
        public ActionResult use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient()) {
                spaceGemUse(world, user, false);
            }
            else {
                InfinityGauntletClient.triggerAnimation(0xd500ca);
            }
            return ActionResult.PASS;
        }
    }
    public static final class TimeGem extends BaseGem {
        public TimeGem(Settings settings) {
            super(settings);
        }
        @Override
        public ActionResult use(World world, PlayerEntity user, Hand hand) {
            if (!world.isClient()) {
                timeGemUse(world, user, false);
            }
            else {
                InfinityGauntletClient.triggerAnimation(0x03d97e);
            }
            return ActionResult.PASS;
        }
    }
}
