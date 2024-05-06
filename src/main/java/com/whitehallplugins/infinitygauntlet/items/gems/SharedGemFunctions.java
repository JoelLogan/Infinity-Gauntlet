package com.whitehallplugins.infinitygauntlet.items.gems;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import com.whitehallplugins.infinitygauntlet.items.gauntlets.Gauntlet;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
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
import org.joml.Vector3f;

import java.util.*;

public class SharedGemFunctions { // TODO: Add Gauntlet functionality (and sounds) from the gems
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
    private static final int MAX_DESPAWNED_ENTITIES = 25;
    public static final String SOUL_GEM_NBT_ID = "SoulGemEntities";
    public static final String MIND_GEM_NBT_ID = "HostileEntity";

    /**
     * Get the target of the player's crosshair
     * @param player The player
     * @param distance The maximum distance to check
     * @param mode 1 for blocks, 2 for entities, 3 for both
     * @param particles Whether to show particles
     * @param explosion Whether to show explosion particles
     * @return The target of the player's crosshair
     */
    public static HitResult raycast(PlayerEntity player, double distance, int mode, boolean particles, boolean explosion) {
        Vec3d playerEyePos = player.getCameraPosVec(1.0f);

        ServerWorld world = ((ServerPlayerEntity) player).getServerWorld();

        float yaw = player.getYaw();
        float pitch = player.getPitch();

        Vec3d lookDirection = calculateLookDirection(yaw, pitch);
        Vec3d endPoint = playerEyePos.add(lookDirection.multiply(distance));

        BlockHitResult blockHitResult = raycastBlocks(world, playerEyePos, endPoint);
        EntityHitResult entityHitResult = raycastEntities(player, world, playerEyePos, endPoint);

        int returnMode = 1;
        boolean runParticles = false;
        boolean runExplosion = false;

        if (!blockHitResult.getType().equals(HitResult.Type.MISS) && (entityHitResult == null || blockHitResult.getPos().squaredDistanceTo(playerEyePos) < entityHitResult.getPos().squaredDistanceTo(playerEyePos))) {
            endPoint = blockHitResult.getPos();
            if (particles) {
                runParticles = true;
            }
            if (explosion) {
                runExplosion = true;
            }
        }
        else if (entityHitResult != null) {
            endPoint = entityHitResult.getPos();
            if (particles) {
                runParticles = true;
            }
            returnMode = 2;
        }

        if (runParticles) {
            createExplosionParticles(world, playerEyePos, endPoint, runExplosion);
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
        Box box = new Box(start, end).expand(1.0, 1.0, 1.0);
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

    private static void createExplosionParticles(ServerWorld world, Vec3d start, Vec3d end, boolean explosion) {
        double distance = start.distanceTo(end);
        int numParticles = (int) distance * 2;

        Vec3d step = end.subtract(start).multiply(1.0 / numParticles);

        for (int i = 0; i < numParticles; i++) {
            Vec3d particlePos = start.add(step.multiply(i));

            BlockPos blockPos = new BlockPos((int) particlePos.x, (int) particlePos.y, (int) particlePos.z);
            BlockState blockState = world.getBlockState(blockPos);
            if (!blockState.isAir() || ((numParticles - i) == 1)) {
                if (explosion) {
                    double radius = 2;
                    DustParticleEffect dustParticle = new DustParticleEffect(new Vector3f(1.0f, 1.0f, 1.0f), 1.0f);
                    for (int j = 0; j < 400; j++) {
                        double offsetX = world.random.nextGaussian() * radius;
                        double offsetY = world.random.nextGaussian() * radius;
                        double offsetZ = world.random.nextGaussian() * radius;
                        world.spawnParticles(dustParticle, particlePos.x + 0.5 + offsetX, particlePos.y + 0.5 + offsetY, particlePos.z + 0.5 + offsetZ, 1, 0.0, 0.0, 0.0, 0.0);
                    }
                }
                break;
            }
            world.spawnParticles(ParticleTypes.FLAME, particlePos.x, particlePos.y, particlePos.z, 1, 0.25, 0.25, 0.25, 0.5);
        }
    }

    public static void mindGemUse(World world, PlayerEntity user) {
        try {
            System.out.println("Right Clicked Mind Gem");
            ItemStack stackInHand = user.getStackInHand(user.getActiveHand());
            if (stackInHand.getItem() instanceof MindGem || stackInHand.getItem() instanceof Gauntlet) {
                NbtCompound glowingItem = stackInHand.getOrCreateNbt();
                EntityHitResult entityHitResult;
                try {
                    entityHitResult = (EntityHitResult) raycast(user, 32, 2, false, false);
                    if (!entityHitResult.getType().equals(HitResult.Type.MISS)) {
                        Entity targetEntity = entityHitResult.getEntity();
                        if (!glowingItem.contains(MIND_GEM_NBT_ID)) {
                            if (targetEntity instanceof HostileEntity) {
                                setStackGlowing(stackInHand, true);
                                glowingItem.putUuid(MIND_GEM_NBT_ID, targetEntity.getUuid());
                            }
                        } else {
                            if (targetEntity instanceof LivingEntity && !(targetEntity instanceof PlayerEntity)) {
                                if (!targetEntity.getUuid().equals(glowingItem.getUuid(MIND_GEM_NBT_ID))) {
                                    user.sendMessage(Text.literal("You have targeted " + targetEntity.getUuidAsString() + "with " + glowingItem.getUuid(MIND_GEM_NBT_ID).toString()));
                                    ServerWorld serverWorld = (ServerWorld) world;
                                    if (Objects.requireNonNull(serverWorld.getEntity(glowingItem.getUuid(MIND_GEM_NBT_ID))).isAlive()) {
                                        HostileEntity entity = (HostileEntity) serverWorld.getEntity(glowingItem.getUuid(MIND_GEM_NBT_ID));
                                        assert entity != null;
                                        entity.addCommandTag("MindGemControlled." + targetEntity.getUuidAsString());
                                        entity.addStatusEffect(new StatusEffectInstance(InfinityGauntlet.targetEntityEffect, StatusEffectInstance.INFINITE));
                                        entity.setTarget((LivingEntity) targetEntity);
                                    }
                                    setStackGlowing(stackInHand, false);
                                    glowingItem.remove(MIND_GEM_NBT_ID);
                                }
                            }
                        }
                    }
                }
                catch (NullPointerException ignored){
                    glowingItem.remove("Enchantments");
                    glowingItem.remove(MIND_GEM_NBT_ID);
                    return;
                }
                stackInHand.setNbt(glowingItem);
            }
        }
        catch (NullPointerException ignored){

        }
        /*
         * right click = control hostile mob to attack another mob (WORKS)
         * (after command given, no more agro from that specific mob) (NOT WORKING BUT MIGHT NOT BE NECESSARY)
         *
         */
    }

    public static void setStackGlowing(ItemStack stack, boolean glowing) {
        NbtList glowingTag = new NbtList();
        glowingTag.add(new NbtCompound());
        if (glowing) {
            stack.getOrCreateNbt().put("Enchantments", glowingTag);
        }
        else {
            stack.getOrCreateNbt().remove("Enchantments");
        }
    }

    public static boolean isStackGlowing(ItemStack stack) {
        return stack.getOrCreateNbt().contains("Enchantments");
    }

    public static void powerGemUse(World world, PlayerEntity user) {
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
            HitResult target = raycast(user, 64, 3, true, true);
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
        /*
         * Right click gem = explosion/instakill (max 64 blocks distance) (traced particles) (WORKING)
         * Shift right click gem = strength + resistance, 8 minutes (invincible) (kills enderman in 3 hits) (WORKING)
         *
         * With Gauntlet: long right click for lightning
         */
    }

    public static void realityGemUse(World world, PlayerEntity user) {
        if (user.isSneaking()){
            System.out.println("Shift Right Clicked Reality Gem");
            if (user.hasPermissionLevel(4)) {
                ((ServerPlayerEntity) user).changeGameMode(user.isCreative() ? GameMode.SURVIVAL : GameMode.CREATIVE);
            }
        }
        else {
            System.out.println("Right Clicked Reality Gem");
            BlockHitResult target = (BlockHitResult) raycast(user, 32, 1, false, false);
            BlockPos targetPos = target.getBlockPos();
            int currentSlot = user.getInventory().selectedSlot;
            int nextSlot = (currentSlot + 1) % user.getInventory().size();
            ItemStack nextStack = user.getInventory().getStack(nextSlot);
            Block targetBlock = null;
            if (nextStack.getItem() instanceof BlockItem) {
                targetBlock = ((BlockItem) nextStack.getItem()).getBlock();
            }
            if (targetBlock == null) {
                targetBlock = Blocks.AIR;
            }
            world.setBlockState(targetPos, targetBlock.getDefaultState());
        }
        /*
         * Shift right click = creative/survival (if op) (WORKS)
         * right click = change targeted block to the right block in hotbar (WORKS)
         *
         * With gauntlet: hold right click to change blocks to block to the right in radius 32 around the player
         * only changes the same type of block
         */
    }

    public static void soulGemUse(World world, PlayerEntity user) {
        ItemStack stackInHand = user.getStackInHand(user.getActiveHand());
        if (stackInHand.getItem() instanceof SoulGem || stackInHand.getItem() instanceof Gauntlet) {
            NbtCompound glowingItem = stackInHand.getOrCreateNbt();
            NbtList entityList = new NbtList();
            if (glowingItem.contains(SOUL_GEM_NBT_ID, NbtCompound.LIST_TYPE)) {
                entityList = glowingItem.getList(SOUL_GEM_NBT_ID, NbtElement.COMPOUND_TYPE);
            }
            if (!user.isSneaking()) {
                System.out.println("Right Clicked Soul Gem");
                if (entityList.size() < MAX_DESPAWNED_ENTITIES) {
                    EntityHitResult entityHitResult;
                    try {
                        entityHitResult = (EntityHitResult) raycast(user, 64, 2, false, false);
                        if (entityHitResult != null && !entityHitResult.getType().equals(HitResult.Type.MISS)) {
                            Entity targetEntity = entityHitResult.getEntity();
                            if (!(targetEntity instanceof PlayerEntity)) {
                                NbtCompound entityDataForList = new NbtCompound();
                                targetEntity.saveNbt(entityDataForList);
                                System.out.println("Despawned Entity: " + targetEntity.getType().getName());
                                entityList.add(entityDataForList);
                                glowingItem.put(SOUL_GEM_NBT_ID, entityList);
                                setStackGlowing(stackInHand, true);
                                despawnEntity(world, targetEntity);
                            }
                        }
                    } catch (NullPointerException ignored) {

                    }
                }
            }
            else {
                System.out.println("Soul Gem Shift Right Clicked");
                if (!entityList.isEmpty()) {
                    System.out.println("Resummoned Entity: " + Objects.requireNonNull(((NbtCompound) entityList.get(entityList.size() - 1)).get("id")));
                    resummonEntity(world, user, entityList, stackInHand);
                }
            }
        }

        /*
          Right click gem = check if mob then suck (not player) (64 blocks) (max 25) (WORKS)
          Shift right click = place sucked mob (not player) (64 blocks) (WORKS)

          With gauntlet: long right click for player to soul dimension
          long shift right click bring player back from soul dimension
         */
    }

    private static void despawnEntity(World world, Entity entity) {
        ServerWorld serverWorld = (ServerWorld) world;
        Objects.requireNonNull(serverWorld.getEntity(entity.getUuid())).remove(Entity.RemovalReason.DISCARDED);
    }

    private static void resummonEntity(World world, PlayerEntity summoner, NbtList entityList, ItemStack stack) {
        NbtCompound lastDespawnedEntity = (NbtCompound) entityList.remove(entityList.size() - 1);
        if (lastDespawnedEntity != null) {
            try {
                Optional<EntityType<?>> type = EntityType.fromNbt(lastDespawnedEntity);
                Entity newEntity = type.orElseThrow().create(world);
                if (newEntity != null) {
                    newEntity.readNbt(lastDespawnedEntity);
                    BlockHitResult result = (BlockHitResult) raycast(summoner, 64, 1, false, false);
                    Vec3d position = result.getPos();
                    newEntity.refreshPositionAndAngles(position.getX(), position.getY() + 0.5, position.getZ(), summoner.getYaw(), summoner.getPitch());
                    world.spawnEntity(newEntity);
                    if (entityList.isEmpty()) {
                        setStackGlowing(stack, false);
                        assert stack.getNbt() != null;
                        stack.getNbt().remove(SOUL_GEM_NBT_ID);
                    }
                }
            }
            catch (NoSuchElementException ignored) {

            }
        }
    }

    public static void spaceGemUse(World world, PlayerEntity user) {
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
                Vec3d targetPos = raycast(user, 64, 1, false, false).getPos();
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
        /*
         * right click = teleport to target block (within 64 blocks) (WORKS)
         * shift right click = open enderchest (WORKS)
         *
         * With gauntlet:
         * short right click and shift right click = same as gem
         * long right click = change dimension (world, nether, end)
         * (When changing to end, goes to end spawn) (When changing to nether, Finds closest y coordinate first)
         * (When changing to overworld, teleport to exact coordinates)
         */
    }

    public static void timeGemUse(World world, PlayerEntity user) {
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
                Vec3d targetPos = raycast(user, 32, 1, false, false).getPos();
                BlockPos blockTarget = new BlockPos((int) targetPos.getX(), (int) targetPos.getY(), (int) targetPos.getZ());
                boolean validBlock = false;
                if (!(world.getBlockState(blockTarget).getBlock() instanceof Fertilizable)) {
                    for (BlockPos pos : BlockPos.iterateOutwards(blockTarget, 0, 1, 0)) {
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
        /*
         * right click = bone meal (up to 32 blocks) (WORKS)
         * shift right click = speed 10 (7:30) Haste 3 (WORKS)
         *
         * With gauntlet: hold right click to send player or mob to spawn
         */
    }
}
