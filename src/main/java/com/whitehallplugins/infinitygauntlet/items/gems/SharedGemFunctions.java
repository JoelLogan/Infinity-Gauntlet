package com.whitehallplugins.infinitygauntlet.items.gems;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.List;
import java.util.Objects;

public class SharedGemFunctions { // Add Gauntlet as a parameter to the functions
    private static final List<EntityType<?>> disallowedEntities = List.of(EntityType.ITEM,
            EntityType.EXPERIENCE_BOTTLE, EntityType.ITEM_FRAME, EntityType.GLOW_ITEM_FRAME,
            EntityType.ARMOR_STAND, EntityType.AREA_EFFECT_CLOUD, EntityType.ARROW,
            EntityType.WIND_CHARGE, EntityType.BLOCK_DISPLAY, EntityType.TEXT_DISPLAY,
            EntityType.ITEM_DISPLAY, EntityType.FALLING_BLOCK, EntityType.FIREWORK_ROCKET,
            EntityType.FISHING_BOBBER, EntityType.LEASH_KNOT, EntityType.LIGHTNING_BOLT,
            EntityType.PAINTING, EntityType.SMALL_FIREBALL, EntityType.SNOWBALL,
            EntityType.SPECTRAL_ARROW, EntityType.TRIDENT, EntityType.WITHER_SKULL,
            EntityType.BOAT, EntityType.CHEST_MINECART, EntityType.COMMAND_BLOCK_MINECART,
            EntityType.FURNACE_MINECART, EntityType.HOPPER_MINECART, EntityType.TNT_MINECART,
            EntityType.EGG, EntityType.ENDER_PEARL, EntityType.POTION, EntityType.EVOKER_FANGS,
            EntityType.FIREBALL, EntityType.TNT, EntityType.DRAGON_FIREBALL);

    /**
     * Get the target of the player's crosshair
     * @param player The player
     * @param distance The maximum distance to check
     * @param mode 1 for blocks, 2 for entities, 3 for both
     * @param particles Whether to show particles
     * @return The target of the player's crosshair
     */
    public static HitResult raycast(PlayerEntity player, double distance, int mode, boolean particles) {
        Vec3d playerEyePos = player.getCameraPosVec(1.0f);

        ServerWorld world = ((ServerPlayerEntity) player).getServerWorld();

        float yaw = player.getYaw();
        float pitch = player.getPitch();

        Vec3d lookDirection = calculateLookDirection(yaw, pitch);
        Vec3d endPoint = playerEyePos.add(lookDirection.multiply(distance));

        BlockHitResult blockHitResult = raycastBlocks(world, playerEyePos, endPoint);
        EntityHitResult entityHitResult = raycastEntities(player, world, playerEyePos, endPoint);

        int returnMode = 1;

        if (blockHitResult != null && (entityHitResult == null || blockHitResult.getPos().squaredDistanceTo(playerEyePos) < entityHitResult.getPos().squaredDistanceTo(playerEyePos))) {
            endPoint = blockHitResult.getPos();
        }
        else if (entityHitResult != null) {
            endPoint = entityHitResult.getPos();
            returnMode = 2;
        }

        if (particles) {
            createExplosionParticleTrail(world, playerEyePos, endPoint);
        }

        if (mode == 1) {
            return blockHitResult;
        }
        else if (mode == 2) {
            return entityHitResult;
        }
        else {
            if (returnMode == 1) {
                return blockHitResult;
            }
            else {
                return entityHitResult;
            }
        }
    }

    private static Vec3d calculateLookDirection(float yaw, float pitch) {
        double x = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        double y = -Math.sin(Math.toRadians(pitch));
        double z = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        return new Vec3d(x, y, z);
    }

    private static BlockHitResult raycastBlocks(ServerWorld world, Vec3d start, Vec3d end) {
        RaycastContext raycastContext = new RaycastContext(start, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, ShapeContext.absent());
        return world.raycast(raycastContext);
    }

    private static EntityHitResult raycastEntities(PlayerEntity player, ServerWorld world, Vec3d start, Vec3d end) {
        Box box = new Box(start, end).expand(1.0, 1.0, 1.0); // Extend the bounding box for more accurate intersection
        EntityHitResult result = null;
        double closestDistanceSq = Double.MAX_VALUE;
        Vec3d lookDirection = end.subtract(start).normalize(); // Calculate look direction

        for (Entity entity : world.getOtherEntities(player, box, (entity) ->
                (!entity.isSpectator() || !disallowedEntities.contains(entity.getType())))) {
            Box entityBox = entity.getBoundingBox();

            // Check each face of the hitbox
            Vec3d[] vertices = {
                    new Vec3d(entityBox.minX, entityBox.minY, entityBox.minZ), // Bottom southwest
                    new Vec3d(entityBox.minX, entityBox.minY, entityBox.maxZ), // Bottom northwest
                    new Vec3d(entityBox.maxX, entityBox.minY, entityBox.minZ), // Bottom southeast
                    new Vec3d(entityBox.maxX, entityBox.minY, entityBox.maxZ), // Bottom northeast
                    new Vec3d(entityBox.minX, entityBox.maxY, entityBox.minZ), // Top southwest
                    new Vec3d(entityBox.minX, entityBox.maxY, entityBox.maxZ), // Top northwest
                    new Vec3d(entityBox.maxX, entityBox.maxY, entityBox.minZ), // Top southeast
                    new Vec3d(entityBox.maxX, entityBox.maxY, entityBox.maxZ)  // Top northeast
            };

            for (int i = 0; i < vertices.length; i++) {
                Vec3d vertex = vertices[i];
                Vec3d nextVertex = vertices[(i + 1) % vertices.length]; // Get the next vertex to form an edge

                // Calculate normal of the face
                Vec3d edge = nextVertex.subtract(vertex);
                Vec3d faceNormal = new Vec3d(-edge.z, 0.0, edge.x).normalize();

                // Calculate intersection point with the face
                Vec3d startToVertex = vertex.subtract(start);
                double dot = faceNormal.dotProduct(lookDirection);

                // Check if ray and face are parallel
                if (Math.abs(dot) < 1e-6) {
                    continue; // Ray and face are parallel, no intersection
                }

                double t = faceNormal.dotProduct(startToVertex) / dot;

                // Check if intersection is behind the ray origin
                if (t < 0) {
                    continue; // Intersection point is behind the ray origin
                }

                Vec3d intersection = start.add(lookDirection.multiply(t));

                // Check if the intersection point is within the face
                if (isPointInsideFace(intersection, vertex, nextVertex, faceNormal)) {
                    // Check if the intersection point is within the hitbox
                    if (entityBox.contains(intersection)) {
                        // Calculate squared distance to intersection point
                        double distanceSq = start.squaredDistanceTo(intersection);
                        if (distanceSq < closestDistanceSq) {
                            closestDistanceSq = distanceSq;
                            result = new EntityHitResult(entity, intersection);
                        }
                    }
                }
            }
        }
        return result;
    }

    private static boolean isPointInsideFace(Vec3d point, Vec3d vertex1, Vec3d vertex2, Vec3d normal) {
        Vec3d edge1 = vertex1.subtract(point);
        Vec3d edge2 = vertex2.subtract(point);

        return normal.dotProduct(edge1.crossProduct(edge2)) >= 0;
    }

    private static void createExplosionParticleTrail(ServerWorld world, Vec3d start, Vec3d end) {
        double distance = start.distanceTo(end);
        int numParticles = (int) distance;

        Vec3d step = end.subtract(start).multiply(1.0 / numParticles);

        for (int i = 0; i < numParticles; i++) {
            Vec3d particlePos = start.add(step.multiply(i));

            BlockPos blockPos = new BlockPos((int) particlePos.x, (int) particlePos.y, (int) particlePos.z);
            BlockState blockState = world.getBlockState(blockPos);
            if (!blockState.isAir()) {
                break;
            }
            world.spawnParticles(ParticleTypes.FLAME, particlePos.x, particlePos.y, particlePos.z, 2, 0.5, 0.5, 0.5, 0.5);
        }
    }

    public static void mindGemUse(World world, PlayerEntity user, Hand hand) {
        try {
            EntityHitResult entityHitResult = (EntityHitResult) raycast(user, 32, 2, false);
            assert entityHitResult != null;
            System.out.println("Right Clicked Mind Gem " + entityHitResult.getEntity().getType());
        }
        catch (NullPointerException ignored){

        }
        /**
         * right click = control hostile mob to attack another mob
         * (after command given, no more agro from that specific mob)
         *
         */
    }

    public static void powerGemUse(World world, PlayerEntity user, Hand hand) {
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
            HitResult target = raycast(user, 64, 3, true);
            Vec3d targetPos = target.getPos();
            if (target.getType().equals(HitResult.Type.BLOCK)) {
                System.out.println("BLOCK");
                world.createExplosion(null, null, new ExplosionBehavior(), targetPos, 5.0F, false, World.ExplosionSourceType.BLOCK);
            }
            else if (target.getType().equals(HitResult.Type.ENTITY)) {
                System.out.println("ENTITY");
                EntityHitResult entityTarget = (EntityHitResult) target;
                entityTarget.getEntity().damage(entityTarget.getEntity().getDamageSources().playerAttack(user),1000.0F);
            }
        }
        /**
         * Right click gem = explosion/instakill (max 64 blocks distance) (traced particles)
         * Shift right click gem = strength + resistance, 8 minutes (invincible) (kills enderman in 3 hits)
         *
         * With Gauntlet: long right click for lightning
         */
    }

    public static void realityGemUse(World world, PlayerEntity user, Hand hand) {
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

    public static void soulGemUse(World world, PlayerEntity user, Hand hand) {
        System.out.println("Right Clicked Soul Gem");
        /**
         * Right click gem = check if mob then suck (not player) (64 blocks) (max 25)
         * Shift right click = place sucked mob (not player) (64 blocks)
         *
         * With gauntlet: long right click for player to soul dimension
         * long shift right click bring player back from soul dimension
         */
    }

    public static void spaceGemUse(World world, PlayerEntity user, Hand hand) {
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
            try {
                Vec3d targetPos = raycast(user, 32, 1, false).getPos();
                BlockPos blockPos = new BlockPos((int) targetPos.getX(), (int) targetPos.getY(), (int) targetPos.getZ());
                boolean validTeleport = false;
                for (BlockPos pos : BlockPos.iterateOutwards(blockPos, 1, 1, 1)) {
                    if (!world.getBlockState(pos).isAir()) {
                        validTeleport = true;
                        break;
                    }
                }
                if (validTeleport) {
                    user.requestTeleport(targetPos.getX(), targetPos.getY()+1, targetPos.getZ());
                }
            } catch (NullPointerException e) {
                System.out.println("Raycast error in space gem action: " + e);
            }
        }
        /**
         * right click = teleport to target block (within 32 blocks) (WORKS)
         * shift right click = open enderchest (WORKS)
         *
         * With gauntlet: long right click = change dimension (world, nether, end)
         */
    }

    public static void timeGemUse(World world, PlayerEntity user, Hand hand) {
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
            try {
                Vec3d targetPos = raycast(user, 32, 1, false).getPos();
                BlockPos blockTarget = new BlockPos((int) targetPos.getX(), (int) targetPos.getY(), (int) targetPos.getZ());
                boolean validBlock = false;
                if (!(world.getBlockState(blockTarget).getBlock() instanceof Fertilizable)) {
                    for (BlockPos pos : BlockPos.iterateOutwards(blockTarget, 1, 1, 1)) {
                        if (pos.equals(blockTarget)) {
                            continue;
                        }
                        if (world.getBlockState(pos).getBlock() instanceof Fertilizable) {
                            blockTarget = pos;
                            validBlock = true;
                            break;
                        }
                    }
                }
                else {
                    validBlock = true;
                }
                if (validBlock) {
                    BlockState nearbyBlockState = world.getBlockState(blockTarget);
                    ((Fertilizable) nearbyBlockState.getBlock()).grow((ServerWorld) world, world.random, blockTarget, nearbyBlockState);
                }
            }
            catch (NullPointerException e){
                System.out.println("Raycast error in time gem action: " + e);
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
