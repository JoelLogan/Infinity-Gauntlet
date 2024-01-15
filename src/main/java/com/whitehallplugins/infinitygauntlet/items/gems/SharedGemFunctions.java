package com.whitehallplugins.infinitygauntlet.items.gems;

import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.Objects;

public class SharedGemFunctions { // Add Gauntlet as a parameter to the functions
    public static HitResult getTarget(PlayerEntity user, double distance) {
        return user.raycast(distance, 0.0F, false);
    }

    public static void mindGemAction(World world, PlayerEntity user, Hand hand) {
        System.out.println("Right Clicked Mind Gem");
        /**
         * right click = control hostile mob to attack another mob
         * (after command given, no more agro from that specific mob)
         *
         */
    }

    public static void powerGemAction(World world, PlayerEntity user, Hand hand) {
        StatusEffectInstance strength = new StatusEffectInstance(StatusEffects.STRENGTH, 9600, 4, false, true);
        StatusEffectInstance resistance = new StatusEffectInstance(StatusEffects.RESISTANCE, 9600, 254, false, true);
        if (user.isSneaking()) {
            System.out.println("Shift Right Clicked Power Gem");
            user.removeStatusEffect(StatusEffects.STRENGTH);
            user.removeStatusEffect(StatusEffects.RESISTANCE);
            user.addStatusEffect(strength);
            user.addStatusEffect(resistance);
        }
        else {
            System.out.println("Right Clicked Power Gem");
            // TODO: Explosion
            HitResult target = getTarget(user, 64.0D);
            Vec3d targetPos = target.getPos();
            if (target.getType().equals(HitResult.Type.BLOCK)) {
                System.out.println("BLOCK");
                world.createExplosion(null, null, new ExplosionBehavior(), targetPos, 5.0F, false, World.ExplosionSourceType.BLOCK);
            }
            else if (target.getType().equals(HitResult.Type.ENTITY)) { // BUGGED
                System.out.println("ENTITY");
                EntityHitResult entityTarget = (EntityHitResult) target;
                entityTarget.getEntity().damage(null,1000.0F);
            }
        }
        /**
         * Right click gem = explosion/instakill (max 64 blocks distance) (traced particles)
         * Shift right click gem = strength + resistance, 8 minutes (invincible) (kills enderman in 3 hits)
         *
         * With Gauntlet: long right click for lightning
         */
    }

    public static void realityGemAction(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()){
            System.out.println("Shift Right Clicked Reality Gem");
            if (user.hasPermissionLevel(4)) {
                ((ServerPlayerEntity) user).changeGameMode(user.isCreative() ? GameMode.SURVIVAL : GameMode.CREATIVE);
            }
        }
        else {
            System.out.println("Right Clicked Reality Gem");
        }
        /**
         * Shift right click = creative/survival (if op) (WORKS)
         * right click = change targeted block to the right block in hotbar
         *
         * With gauntlet: hold right click to change blocks to block to the right in radius 32 around the player
         * only changes the same type of block
         */
    }

    public static void soulGemAction(World world, PlayerEntity user, Hand hand) {
        System.out.println("Right Clicked Soul Gem");
        /**
         * Right click gem = check if mob then suck (not player) (64 blocks) (max 25)
         * Shift right click = place sucked mob (not player) (64 blocks)
         *
         * With gauntlet: long right click for player to soul dimension
         * long shift right click bring player back from soul dimension
         */
    }

    public static void spaceGemAction(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()) {
            System.out.println("Shift Right Clicked Space Gem");
            user.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (syncId, inventory, playerEntity) -> GenericContainerScreenHandler.createGeneric9x3(
                            syncId, inventory, playerEntity.getEnderChestInventory()
                    ),
                    Text.translatable("item.infinitygauntlet.space.enderchest")
            ));
            user.incrementStat(Stats.OPEN_ENDERCHEST);
        }
        else {
            System.out.println("Right Clicked Space Gem");
            if (getTarget(user, 32.0D).getType().equals(HitResult.Type.BLOCK)) {
                try {
                    Vec3d targetPos = getTarget(user, 32.0D).getPos();
                    user.teleport(targetPos.getX(), targetPos.getY()+1, targetPos.getZ());
                } catch (NullPointerException e) {
                    System.out.println("Raycast error in space gem action: " + e);
                }
            }
        }
        /**
         * right click = teleport to target block (within 32 blocks) (WORKS)
         * shift right click = open enderchest (WORKS)
         *
         * With gauntlet: long right click = change dimension (world, nether, end)
         */
    }

    public static void timeGemAction(World world, PlayerEntity user, Hand hand) {
        StatusEffectInstance speed = new StatusEffectInstance(StatusEffects.SPEED, 9000, 9, false, true);
        StatusEffectInstance haste = new StatusEffectInstance(StatusEffects.HASTE, 9000, 2, false, true);
        if (user.isSneaking()) {
            System.out.println("Shift Right Clicked Time Gem");
            user.removeStatusEffect(StatusEffects.SPEED);
            user.removeStatusEffect(StatusEffects.HASTE);
            user.addStatusEffect(speed);
            user.addStatusEffect(haste);
        }
        else {
            System.out.println("Right Clicked Time Gem");
            if (getTarget(user, 32.0D).getType().equals(HitResult.Type.BLOCK)) {
                try {
                    Vec3d targetPos = getTarget(user, 32.0D).getPos();
                    BlockPos blockTarget = new BlockPos((int) targetPos.getX(), (int) targetPos.getY()-1, (int) targetPos.getZ());
                    BlockState blockState = world.getBlockState(blockTarget);
                    if (blockState.getBlock() instanceof Fertilizable) {
                        if (((Fertilizable) blockState.getBlock()).isFertilizable(world, blockTarget, blockState)) {
                            ((Fertilizable) blockState.getBlock()).grow(Objects.requireNonNull(world.getServer()).getWorld(world.getRegistryKey()), world.random, blockTarget,blockState);
                        }
                    }
                }
                catch (NullPointerException e){
                    System.out.println("Raycast error in time gem action: " + e);
                }
            }
        }
        /**
         * right click = bone meal (up to 32 blocks) (WORKS)
         * shift right click = speed 10 (7:30) Haste 3 (WORKS)
         *
         * With gauntlet: hold right click to send player or mob to spawn
         */
    }
}
