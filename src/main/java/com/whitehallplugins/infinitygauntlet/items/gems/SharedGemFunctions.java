package com.whitehallplugins.infinitygauntlet.items.gems;

import com.whitehallplugins.infinitygauntlet.InfinityGauntlet;
import com.whitehallplugins.infinitygauntlet.files.config.DefaultModConfig;
import com.whitehallplugins.infinitygauntlet.files.teleport.OfflineTeleportManager;
import com.whitehallplugins.infinitygauntlet.items.gauntlets.Gauntlet;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.hit.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.MOD_ID;
import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.CONFIG;
import static com.whitehallplugins.infinitygauntlet.InfinityGauntlet.SOUL_DIMENSION;

public class SharedGemFunctions {
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
            EntityType.FIREBALL, EntityType.TNT, EntityType.DRAGON_FIREBALL, EntityType.EXPERIENCE_ORB);
    private static final List<Block> disallowedBlocks = List.of(Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK,
            Blocks.REPEATING_COMMAND_BLOCK, Blocks.BARRIER, Blocks.STRUCTURE_BLOCK, Blocks.STRUCTURE_VOID,
            Blocks.BEDROCK, Blocks.END_PORTAL_FRAME, Blocks.END_PORTAL, Blocks.END_GATEWAY, Blocks.NETHER_PORTAL,
            Blocks.LIGHT);
    private static final List<Block> ALLOWED_BLOCKS = List.of(Blocks.GLASS, Blocks.WHITE_STAINED_GLASS,
            Blocks.ORANGE_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS,
            Blocks.YELLOW_STAINED_GLASS, Blocks.LIME_STAINED_GLASS, Blocks.PINK_STAINED_GLASS,
            Blocks.GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS,
            Blocks.PURPLE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS,
            Blocks.GREEN_STAINED_GLASS, Blocks.RED_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS);
    private static final int ENTITY_RAYCAST_DISTANCE = CONFIG.getOrDefault("raycastEntityDistance", DefaultModConfig.raycastEntityDistance);
    private static final int BLOCK_RAYCAST_DISTANCE = CONFIG.getOrDefault("raycastBlocksDistance", DefaultModConfig.raycastBlocksDistance);
    private static final int COMBINED_RAYCAST_DISTANCE = CONFIG.getOrDefault("raycastCombinedDistance", DefaultModConfig.raycastCombinedDistance);
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(CONFIG.getOrDefault(
            "realityGauntletConcurrentThreads", DefaultModConfig.realityGauntletConcurrentThreads));
    private static final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
    public static final String SOUL_GEM_NBT_ID = "SoulGemEntities";
    public static final String MIND_GEM_NBT_ID = "HostileEntity";
    private static final String SOUL_PLAYER_NBT_ID = "minecraft:player";

    public SharedGemFunctions(){
        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));
    }

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
                (!entity.isSpectator() && !disallowedEntities.contains(entity.getType())))) {
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
        int numParticles = (int) (distance * 0.85);

        Vec3d step = end.subtract(start).multiply(1.0 / numParticles);

        for (int i = 0; i < numParticles; i++) {
            Vec3d particlePos = start.add(step.multiply(i));

            BlockPos blockPos = new BlockPos((int) particlePos.x, (int) particlePos.y, (int) particlePos.z);
            BlockState blockState = world.getBlockState(blockPos);
            if ((!blockState.isAir() && !blockState.isOf(Blocks.WATER) && !blockState.isOf(Blocks.LAVA)) || ((numParticles - i) == 1)) {
                if (explosion) {
                    double radius = 1.5;
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
            world.spawnParticles(ParticleTypes.FLAME, particlePos.x, particlePos.y, particlePos.z, 1, 0.25, 0.25, 0.25, 0.4);
        }
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

    private static void addToNbtList(LivingEntity targetEntity, NbtList entityList, NbtCompound glowingItem) {
        NbtCompound entityDataForList = new NbtCompound();
        if (targetEntity instanceof PlayerEntity){
            entityDataForList.putString("id", SOUL_PLAYER_NBT_ID);
            entityDataForList.putUuid("UUID", targetEntity.getUuid());
        }
        else {
            targetEntity.saveNbt(entityDataForList);
        }
        entityList.add(entityDataForList);
        glowingItem.put(SOUL_GEM_NBT_ID, entityList);
    }

    private static void despawnEntity(World world, Entity entity) {
        ServerWorld serverWorld = (ServerWorld) world;
        Objects.requireNonNull(serverWorld.getEntity(entity.getUuid())).remove(Entity.RemovalReason.DISCARDED);
    }

    private static void resummonEntity(World world, PlayerEntity summoner, NbtList entityList, ItemStack stack, boolean gauntlet) {
        NbtCompound lastDespawnedEntity = (NbtCompound) entityList.get(entityList.size() - 1);
        if (lastDespawnedEntity != null) {
            try {
                BlockHitResult result = (BlockHitResult) raycast(summoner, BLOCK_RAYCAST_DISTANCE, 1, false, false);
                Vec3d targetPos = result.getPos();
                if (lastDespawnedEntity.getString("id").equals(SOUL_PLAYER_NBT_ID) && gauntlet) {
                    UUID targetUUID = lastDespawnedEntity.getUuid("UUID");
                    if (Objects.requireNonNull(world.getServer()).getPlayerManager().getPlayer(targetUUID) != null) {
                        ServerPlayerEntity player = Objects.requireNonNull(world.getServer()).getPlayerManager().getPlayer(Objects.requireNonNull(lastDespawnedEntity.getUuid("UUID")));
                        assert player != null;
                        player.setSpawnPoint(summoner.getWorld().getRegistryKey(), new BlockPos((int) targetPos.getX(), (int) targetPos.getY(), (int) targetPos.getZ()), 0, true, false);
                        spawnPortalParticles((ServerWorld) world, targetPos, true);
                        TeleportTarget target = new TeleportTarget(targetPos, player.getVelocity(), player.getYaw(), player.getPitch());
                        FabricDimensions.teleport(player, (ServerWorld) summoner.getWorld(), target);
                    }
                    else {
                        NbtCompound teleportData = new NbtCompound();
                        teleportData.putDouble("TargetX", targetPos.getX());
                        teleportData.putDouble("TargetY", targetPos.getY());
                        teleportData.putDouble("TargetZ", targetPos.getZ());
                        teleportData.putString("World", summoner.getWorld().getRegistryKey().getValue().toString());

                        OfflineTeleportManager.setTeleportData(targetUUID, teleportData);
                        summoner.sendMessage(Text.literal("The stored player is offline. They will spawn" +
                                " at the targeted location when they log in"));
                    }
                    entityList.remove(entityList.size() - 1);
                }
                else {
                    Optional<EntityType<?>> nbtType = EntityType.fromNbt(lastDespawnedEntity);
                    Entity newEntity = nbtType.orElseThrow().create(world);
                    if (newEntity != null) {
                        newEntity.readNbt(lastDespawnedEntity);
                        Vec3d position = result.getPos();
                        newEntity.refreshPositionAndAngles(position.getX(), position.getY() + 0.5, position.getZ(), summoner.getYaw(), summoner.getPitch());
                        world.spawnEntity(newEntity);
                        entityList.remove(entityList.size() - 1);
                    }
                }
                if (entityList.isEmpty()) {
                    resetSoulGem(stack);
                }
            }
            catch (NoSuchElementException ignored) {}
        }
    }

    private static void resetSoulGem(ItemStack item){
        setStackGlowing(item, false);
        assert item.getNbt() != null;
        item.getNbt().remove(SOUL_GEM_NBT_ID);
    }

    private static boolean isThreadPoolBusy() {
        return threadPoolExecutor.getActiveCount() >= CONFIG.getOrDefault("realityGauntletConcurrentThreads",
                DefaultModConfig.realityGauntletConcurrentThreads);
    }

    private static void changeBlocksInSphereRecursive(PlayerEntity user, World world, BlockPos centerPos, BlockState targetBlock, Block changeTo) {
        if (isThreadPoolBusy()) {
            user.sendMessage(Text.literal("All threads are busy. The next reality gem task will have to wait."));
        }

        executorService.submit(() -> {
            Set<BlockPos> visited = new HashSet<>();
            Queue<BlockPos> queue = new LinkedList<>();
            queue.add(centerPos);

            long lastIterationTime = System.currentTimeMillis();

            while (!queue.isEmpty()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastIterationTime < CONFIG.getOrDefault("realityGauntletBlockChangeThreadTime",
                        DefaultModConfig.realityGauntletBlockChangeThreadTime)) {
                    continue;
                }
                BlockPos currentPos = queue.poll();
                visited.add(currentPos);
                synchronized (world) {
                    if (world.getBlockState(currentPos).getBlock() == targetBlock.getBlock()) {
                        world.breakBlock(currentPos, false);
                        world.setBlockState(currentPos, changeTo.getDefaultState());
                        for (int xOffset = -1; xOffset <= 1; xOffset++) {
                            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                                    assert currentPos != null;
                                    BlockPos neighborPos = currentPos.add(xOffset, yOffset, zOffset);
                                    if (!visited.contains(neighborPos) && diamondDistance(centerPos, neighborPos) <=
                                            CONFIG.getOrDefault("realityGemBlockRadius", DefaultModConfig.realityGauntletBlockRadius)) {
                                        queue.add(neighborPos);
                                    }
                                    // CIRCLE MODE: centerPos.getSquaredDistance(neighborPos) <= BLOCK_CHANGE_RADIUS * BLOCK_CHANGE_RADIUS
                                }
                            }
                        }
                        lastIterationTime = System.currentTimeMillis();
                    }
                }
            }
        });
    }

    private static int diamondDistance(BlockPos pos1, BlockPos pos2) {
        return Math.abs(pos1.getX() - pos2.getX()) + Math.abs(pos1.getY() - pos2.getY()) + Math.abs(pos1.getZ() - pos2.getZ());
    }

    public static boolean isAcceptableBlock(ItemStack stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            BlockState state = block.getDefaultState();
            if (ALLOWED_BLOCKS.contains(block)) {
                return true;
            }
            return state.isOpaque() || state.isFullCube(null, null);
        }
        return false;
    }

    public static void spawnPortalParticles(ServerWorld world, Vec3d pos, boolean plusOne) {
        if (plusOne) {
            pos.add(0,1,0);
        }
        world.spawnParticles(ParticleTypes.PORTAL, pos.getX(), pos.getY(), pos.getZ(), 60, 0.5, 0.5, 0.5, 0.0);
    }

    public static void mindGemUse(World world, PlayerEntity user, boolean gauntlet) {
        if (!gauntlet) {
            if (CONFIG.getOrDefault("isMindGemEnabled", DefaultModConfig.isMindGemEnabled)) {
                try {
                    ItemStack stackInHand = user.getStackInHand(user.getActiveHand());
                    if (stackInHand.getItem() instanceof MindGem || stackInHand.getItem() instanceof Gauntlet) {
                        NbtCompound glowingItem = stackInHand.getOrCreateNbt();
                        EntityHitResult entityHitResult;
                        try {
                            entityHitResult = (EntityHitResult) raycast(user, ENTITY_RAYCAST_DISTANCE, 2, false, false);
                            if (!entityHitResult.getType().equals(HitResult.Type.MISS)) {
                                Entity targetEntity = entityHitResult.getEntity();
                                if (!glowingItem.contains(MIND_GEM_NBT_ID) && targetEntity instanceof HostileEntity) {
                                    setStackGlowing(stackInHand, true);
                                    glowingItem.putUuid(MIND_GEM_NBT_ID, targetEntity.getUuid());
                                } else {
                                    if (targetEntity instanceof LivingEntity) { // && !(targetEntity instanceof PlayerEntity) - Remove players from the list (If wanted)
                                        if (targetEntity instanceof PlayerEntity && ((PlayerEntity) targetEntity).isCreative() || targetEntity.isSpectator()) {
                                            return;
                                        }
                                        if (!targetEntity.getUuid().equals(glowingItem.getUuid(MIND_GEM_NBT_ID))) {
                                            ServerWorld serverWorld = (ServerWorld) world;
                                            if (Objects.requireNonNull(serverWorld.getEntity(glowingItem.getUuid(MIND_GEM_NBT_ID))).isAlive()) {
                                                HostileEntity entity = (HostileEntity) serverWorld.getEntity(glowingItem.getUuid(MIND_GEM_NBT_ID));
                                                assert entity != null;
                                                for (String tag : entity.getCommandTags()) {
                                                    if (tag.startsWith("MindGemControlled")) {
                                                        entity.removeCommandTag(tag);
                                                    }
                                                }
                                                entity.setPersistent();
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
                        } catch (NullPointerException ignored) {
                            glowingItem.remove("Enchantments");
                            glowingItem.remove(MIND_GEM_NBT_ID);
                            return;
                        }
                        stackInHand.setNbt(glowingItem);
                    }
                } catch (NullPointerException e) {
                    Logger.getLogger(MOD_ID).warning("Raycast error in mind gem action: " + e);
                }
            }
        }
        else {
            if (CONFIG.getOrDefault("isMindGemGauntletEnabled", DefaultModConfig.isMindGemGauntletEnabled)) {
                try {
                    EntityHitResult entityHitResult = (EntityHitResult) raycast(user, ENTITY_RAYCAST_DISTANCE, 2, false, false);
                    if (!entityHitResult.getType().equals(HitResult.Type.MISS) && entityHitResult.getEntity() instanceof PlayerEntity targetEntity) {
                        StatusEffectInstance weakness = new StatusEffectInstance(StatusEffects.WEAKNESS, 3600, 255, false, true);
                        StatusEffectInstance nausea = new StatusEffectInstance(StatusEffects.NAUSEA, 3600, 255, false, true);
                        StatusEffectInstance blindness = new StatusEffectInstance(StatusEffects.BLINDNESS, 1200, 255, false, true);
                        targetEntity.removeStatusEffect(StatusEffects.WEAKNESS);
                        targetEntity.removeStatusEffect(StatusEffects.NAUSEA);
                        targetEntity.removeStatusEffect(StatusEffects.BLINDNESS);
                        targetEntity.addStatusEffect(weakness);
                        targetEntity.addStatusEffect(nausea);
                        targetEntity.addStatusEffect(blindness);
                        targetEntity.addExperienceLevels(-Integer.MAX_VALUE);
                    }
                } catch (NullPointerException e) {
                    Logger.getLogger(MOD_ID).warning("Raycast error in mind gem action: " + e);
                }
            }
        }
        /* GEM SHOULD BE FINISHED
         * right click = control hostile mob to attack another mob, resets target's xp levels (WORKS)
         * (after command given, no more agro from that specific mob) (NOT WORKING BUT MIGHT NOT BE NECESSARY)
         *
         * With Gauntlet: Gives 3 minutes of weakness and nausea and 1 minute of blindness (WORKS)
         */
    }

    public static void powerGemUse(World world, PlayerEntity user, boolean gauntlet) {
        StatusEffectInstance strength = new StatusEffectInstance(StatusEffects.STRENGTH, 9600, 4, false, true);
        StatusEffectInstance resistance = new StatusEffectInstance(StatusEffects.RESISTANCE, 9600, 255, false, true);
        if (!gauntlet) {
            if (CONFIG.getOrDefault("isPowerGemEnabled", DefaultModConfig.isPowerGemEnabled)) {
                if (user.isSneaking()) {
                    user.removeStatusEffect(StatusEffects.STRENGTH);
                    user.removeStatusEffect(StatusEffects.RESISTANCE);
                    user.addStatusEffect(strength);
                    user.addStatusEffect(resistance);
                } else {
                    HitResult target = raycast(user, COMBINED_RAYCAST_DISTANCE, 3, true, true);
                    if (!target.getType().equals(HitResult.Type.MISS)) {
                        if (target.getType().equals(HitResult.Type.BLOCK)) {
                            Vec3d targetPos = target.getPos();
                            for (BlockPos blockPos : BlockPos.iterateOutwards(((BlockHitResult) target).getBlockPos(), 1, 1, 1)) {
                                if (world.getBlockState(blockPos).getBlock().equals(Blocks.WATER)) {
                                    targetPos = ((BlockHitResult) target).getBlockPos().toCenterPos();
                                    break;
                                }
                            }
                            DamageSource damageSource = new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(DamageTypes.EXPLOSION), user);
                            user.setInvulnerable(true);
                            world.createExplosion(null, damageSource, new ExplosionBehavior(), targetPos,
                                    CONFIG.getOrDefault("powerGemExplosionPower", DefaultModConfig.powerGemExplosionPower), false, World.ExplosionSourceType.BLOCK);
                            user.setInvulnerable(false);
                        } else if (target.getType().equals(HitResult.Type.ENTITY)) {
                            EntityHitResult entityTarget = (EntityHitResult) target;
                            DamageSource damageSource = new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(InfinityGauntlet.POWER_GEM_DAMAGE_TYPE), user);
                            entityTarget.getEntity().damage(damageSource, Float.MAX_VALUE);
                        }
                        world.playSound(null, user.getBlockPos(), SoundEvents.ENTITY_GHAST_SHOOT, SoundCategory.HOSTILE);
                    }
                }
            }
        }
        else {
            if (CONFIG.getOrDefault("isPowerGemGauntletEnabled", DefaultModConfig.isPowerGemGauntletEnabled)) {
                Box box = new Box(user.getBlockPos()).expand(64);
                Predicate<Entity> predicate = entity -> {
                    if (entity instanceof PlayerEntity) {
                        if (entity == user) {
                            return false;
                        }
                        return (!((PlayerEntity) entity).isCreative() && !entity.isSpectator());
                    }
                    return true;
                };
                world.getEntitiesByClass(LivingEntity.class, box, predicate).forEach(targetEntity -> {
                    LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
                    assert lightning != null;
                    lightning.refreshPositionAfterTeleport(targetEntity.getX(), targetEntity.getY(), targetEntity.getZ());
                    world.spawnEntity(lightning);
                    if (targetEntity.isAlive()) {
                        DamageSource damageSource = new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(InfinityGauntlet.POWER_GEM_DAMAGE_TYPE), user);
                        targetEntity.damage(damageSource, Float.MAX_VALUE);
                    }
                });
            }
        }
        /* GEM SHOULD BE FINISHED
         * Right click gem = explosion/instakill (max 64 blocks distance) (traced particles) (WORKING)
         * Shift right click gem = strength + resistance, 8 minutes (invincible) (kills enderman in 3 hits) (WORKING)
         *
         * With Gauntlet: long right click for lightning to strike entities within 64 blocks of the player (WORKING)
         */
    }

    public static void realityGemUse(World world, PlayerEntity user, boolean gauntlet) {
        if (user.isSneaking() && user.hasPermissionLevel(4) &&
                CONFIG.getOrDefault("isRealityGemEnabled", DefaultModConfig.isRealityGemEnabled)) {
            ((ServerPlayerEntity) user).changeGameMode(user.isCreative() ? GameMode.SURVIVAL : GameMode.CREATIVE);
        } else {
            BlockHitResult target = (BlockHitResult) raycast(user, BLOCK_RAYCAST_DISTANCE, 1, false, false);
            BlockPos targetPos = target.getBlockPos();
            if (!world.getBlockState(targetPos).isAir()) {
                int currentSlot = user.getInventory().selectedSlot;
                int nextSlot = (currentSlot + 1) % user.getInventory().size();
                ItemStack nextStack = user.getInventory().getStack(nextSlot);
                Block changeToBlock;
                if (isAcceptableBlock(nextStack)) {
                    changeToBlock = ((BlockItem) nextStack.getItem()).getBlock();
                }
                else {
                    changeToBlock = Blocks.AIR;
                }
                BlockState targetBlock = world.getBlockState(targetPos);
                if (!targetBlock.isOf(changeToBlock) && !disallowedBlocks.contains(targetBlock.getBlock())){
                    if (gauntlet && CONFIG.getOrDefault("isRealityGemGauntletEnabled", DefaultModConfig.isRealityGemGauntletEnabled)){
                        changeBlocksInSphereRecursive(user, world, targetPos, targetBlock, changeToBlock);
                    }
                    else if (CONFIG.getOrDefault("isRealityGemEnabled", DefaultModConfig.isRealityGemEnabled)){
                        world.breakBlock(targetPos, false);
                        world.setBlockState(targetPos, changeToBlock.getDefaultState());
                    }
                }
            }
        }
        /* GEM SHOULD BE FINISHED
         * Shift right click = creative/survival (if op) (WORKS)
         * right click = change targeted block to the right block in hotbar. Plays breaking sound of block being changed (WORKS)
         *
         * With gauntlet: hold right click to change blocks to block to the right in radius 32 around the player
         * only changes the same type of block, only on the surface (WORKS)
         */
    }

    public static void soulGemUse(World world, PlayerEntity user, boolean gauntlet) {
        ItemStack stackInHand = user.getStackInHand(user.getActiveHand());
        if (stackInHand.getItem() instanceof SoulGem || stackInHand.getItem() instanceof Gauntlet) {
            NbtCompound glowingItem = stackInHand.getOrCreateNbt();
            NbtList entityList = new NbtList();
            if (glowingItem.contains(SOUL_GEM_NBT_ID, NbtCompound.LIST_TYPE)) {
                entityList = glowingItem.getList(SOUL_GEM_NBT_ID, NbtElement.COMPOUND_TYPE);
            }
            if (!user.isSneaking()) {
                if (entityList.size() < CONFIG.getOrDefault("maxNumberofEntitesInSoulGem", DefaultModConfig.maxNumberofEntitesInSoulGem)) {
                    EntityHitResult entityHitResult;
                    try {
                        entityHitResult = (EntityHitResult) raycast(user, ENTITY_RAYCAST_DISTANCE, 2, false, false);
                        if (entityHitResult != null && !entityHitResult.getType().equals(HitResult.Type.MISS)) {
                            Entity targetEntity = entityHitResult.getEntity();
                            if (targetEntity instanceof LivingEntity && !disallowedEntities.contains(targetEntity.getType())) {
                                if (!(targetEntity instanceof PlayerEntity) && CONFIG.getOrDefault("isSoulGemEnabled", DefaultModConfig.isSoulGemEnabled)) {
                                    addToNbtList((LivingEntity) targetEntity, entityList, glowingItem);
                                    setStackGlowing(stackInHand, true);
                                    despawnEntity(world, targetEntity);
                                } else if (gauntlet && CONFIG.getOrDefault("isSoulGemGauntletEnabled", DefaultModConfig.isSoulGemGauntletEnabled)){
                                    addToNbtList((LivingEntity) targetEntity, entityList, glowingItem);
                                    setStackGlowing(stackInHand, true);
                                    spawnPortalParticles((ServerWorld) world, targetEntity.getPos(), true);
                                    ((PlayerEntity) targetEntity).getInventory().dropAll();
                                    ServerWorld soulDimension = Objects.requireNonNull(world.getServer()).getWorld(RegistryKey.of(RegistryKeys.WORLD, SOUL_DIMENSION));
                                    assert soulDimension != null;
                                    Vec3d spawnPos = soulDimension.getSpawnPos().toCenterPos();
                                    if (!world.getBlockState(soulDimension.getSpawnPos()).isAir()){
                                        for (BlockPos p : BlockPos.iterateOutwards(soulDimension.getSpawnPos(), 12, 200, 12)){
                                            if (world.getBlockState(p).isAir() && world.getBlockState(p.up(1)).isAir()){
                                                spawnPos = p.toCenterPos();
                                                break;
                                            }
                                        }
                                    }
                                    Objects.requireNonNull(world.getServer().getPlayerManager().getPlayer(targetEntity.getUuid())).setSpawnPoint(
                                            RegistryKey.of(RegistryKeys.WORLD, SOUL_DIMENSION), new BlockPos((int) spawnPos.getX(), (int) spawnPos.getY(), (int) spawnPos.getZ()), 0, true, false);
                                    TeleportTarget target = new TeleportTarget(spawnPos, targetEntity.getVelocity(), targetEntity.getYaw(), targetEntity.getPitch());
                                    FabricDimensions.teleport(targetEntity, soulDimension, target);
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        Logger.getLogger(MOD_ID).warning("Raycast error in soul gem action: " + e);
                    }
                }
            }
            else {
                if (!entityList.isEmpty() && CONFIG.getOrDefault("isSoulGemEnabled", DefaultModConfig.isSoulGemEnabled)) {
                    try {
                        if (((NbtCompound) entityList.get(entityList.size() - 1)).get("id") != null) {
                            resummonEntity(world, user, entityList, stackInHand, gauntlet);
                        }
                        else {
                            entityList.remove(entityList.size() - 1);
                            if (entityList.isEmpty()) {
                                resetSoulGem(stackInHand);
                            }
                        }
                    }
                    catch (NullPointerException e) {
                        Logger.getLogger(MOD_ID).warning("Raycast error in soul gem action: " + e);
                    }
                }
            }
        }

        /* GEM SHOULD BE FINISHED
          Right click gem = check if mob then suck (not player) (64 blocks) (max 25) (WORKS)
          Shift right click = place sucked mob (not player) (64 blocks) (WORKS)

          With gauntlet: long right click for player to soul dimension (WORKS)
          long shift right click bring player back from soul dimension
         */
    }

    public static void spaceGemUse(World world, PlayerEntity user, boolean gauntlet) {
        if (!gauntlet) {
            if (CONFIG.getOrDefault("isSpaceGemEnabled", DefaultModConfig.isSpaceGemEnabled)) {
                if (user.isSneaking()) {
                    user.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                            (syncId, inventory, playerEntity) -> GenericContainerScreenHandler.createGeneric9x3(
                                    syncId, inventory, playerEntity.getEnderChestInventory()
                            ),
                            Text.translatable("item.infinitygauntlet.space.enderchest")
                    ));
                    user.incrementStat(Stats.OPEN_ENDERCHEST);
                } else {
                    try {
                        Vec3d targetPos = raycast(user, BLOCK_RAYCAST_DISTANCE, 1, false, false).getPos();
                        BlockPos blockPos = new BlockPos((int) targetPos.getX(), (int) targetPos.getY(), (int) targetPos.getZ());
                        boolean validTeleport = false;
                        for (BlockPos pos : BlockPos.iterateOutwards(blockPos, 1, 1, 1)) {
                            if (!world.getBlockState(pos).isAir()) {
                                validTeleport = true;
                                break;
                            }
                        }
                        if (validTeleport) {
                            spawnPortalParticles((ServerWorld) world, user.getPos(), true);
                            user.requestTeleport(targetPos.getX(), targetPos.getY() + 1, targetPos.getZ());
                            spawnPortalParticles((ServerWorld) world, targetPos, true);
                            user.playSound(SoundEvents.ENTITY_PLAYER_TELEPORT, SoundCategory.BLOCKS, 1, 1);
                        }
                    } catch (NullPointerException e) {
                        Logger.getLogger(MOD_ID).warning("Raycast error in space gem action: " + e);
                    }
                }
            }
        }
        else {
            if (CONFIG.getOrDefault("isSpaceGemGauntletEnabled", DefaultModConfig.isSpaceGemGauntletEnabled)) {
                if (world.getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
                    ServerWorld nether = Objects.requireNonNull(user.getServer()).getWorld(World.NETHER);
                    assert nether != null;
                    double x = user.getX();
                    double y = user.getY();
                    double z = user.getZ();
                    for (BlockPos pos : BlockPos.iterateOutwards(user.getBlockPos(), 1, 128, 1)) {
                        if (nether.getBlockState(pos).isAir() && nether.getBlockState(pos.up()).isAir() && nether.getBlockState(pos.down()).isSolidBlock(nether, pos.down()) && pos.getY() < 128) {
                            x = pos.getX();
                            y = pos.getY();
                            z = pos.getZ();
                        }
                    }
                    user.teleport(nether, x, y, z, new HashSet<>(), 0, 0);
                } else if (world.getDimensionKey().equals(DimensionTypes.THE_NETHER)) {
                    user.moveToWorld(Objects.requireNonNull(user.getServer()).getWorld(World.END));
                } else if (world.getDimensionKey().equals(DimensionTypes.THE_END)) {
                    user.teleport(Objects.requireNonNull(user.getServer()).getWorld(World.OVERWORLD), user.getX(), user.getY(), user.getZ(), new HashSet<>(), 0, 0);
                }
            }
        }
        /* GEM SHOULD BE FINISHED
         * right click = teleport to target block (within 64 blocks) (WORKS)
         * shift right click = open enderchest (WORKS)
         *
         * With gauntlet: (WORKS)
         * short right click and shift right click = same as gem
         * long right click = change dimension (world, nether, end)
         * (When changing to end, goes to end spawn) (When changing to nether, Finds closest y coordinate first)
         * (When changing to overworld, teleport to exact coordinates)
         */
    }

    public static void timeGemUse(World world, PlayerEntity user, boolean gauntlet) {
        StatusEffectInstance speed = new StatusEffectInstance(StatusEffects.SPEED, 9000, 9, false, true);
        StatusEffectInstance haste = new StatusEffectInstance(StatusEffects.HASTE, 9000, 2, false, true);
        if (!gauntlet) {
            if (CONFIG.getOrDefault("isTimeGemEnabled", DefaultModConfig.isTimeGemEnabled)) {
                if (user.isSneaking()) {
                    user.removeStatusEffect(StatusEffects.SPEED);
                    user.removeStatusEffect(StatusEffects.HASTE);
                    user.addStatusEffect(speed);
                    user.addStatusEffect(haste);
                }
                try {
                    HitResult target = raycast(user, COMBINED_RAYCAST_DISTANCE, 3, false, true);
                    Vec3d targetPos = target.getPos();
                    if (target.getType().equals(HitResult.Type.BLOCK)) {
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
                        } else {
                            validBlock = true;
                        }
                        if (validBlock) {
                            BlockState nearbyBlockState = world.getBlockState(blockTarget);
                            ((Fertilizable) nearbyBlockState.getBlock()).grow((ServerWorld) world, world.random, blockTarget, nearbyBlockState);
                            ((ServerWorld) world).spawnParticles(ParticleTypes.HAPPY_VILLAGER, targetPos.getX(), targetPos.getY() + 0.5, targetPos.getZ(), 8, 0.35, 0.15, 0.35, 0.0);
                        }
                    } else if (target.getType().equals(HitResult.Type.ENTITY)) {
                        EntityHitResult entityHitResult = (EntityHitResult) target;
                        if (entityHitResult.getEntity() instanceof PassiveEntity passiveEntity) {
                            if (passiveEntity.isBaby()) {
                                passiveEntity.setBaby(false);
                                return;
                            }
                            passiveEntity.setBaby(true);
                        }
                    }
                } catch (NullPointerException e) {
                    Logger.getLogger(MOD_ID).warning("Raycast error in time gem action: " + e);
                }
            }
        }
        else {
            if (CONFIG.getOrDefault("isTimeGemGauntletEnabled", DefaultModConfig.isTimeGemGauntletEnabled)) {
                try {
                    EntityHitResult entityHitResult = (EntityHitResult) raycast(user, ENTITY_RAYCAST_DISTANCE, 2, false, false);
                    if (!entityHitResult.getType().equals(HitResult.Type.MISS)) {
                        Vec3d targetPos = entityHitResult.getPos();
                        spawnPortalParticles((ServerWorld) world, targetPos, true);
                        BlockPos spawnPos = Objects.requireNonNull(user.getServer()).getOverworld().getSpawnPos();
                        entityHitResult.getEntity().teleport(Objects.requireNonNull(user.getServer()).getOverworld(), spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), new HashSet<>(), 0, 0);
                        spawnPortalParticles((ServerWorld) world, spawnPos.toCenterPos(), true);
                    }
                } catch (NullPointerException e) {
                    Logger.getLogger(MOD_ID).warning("Raycast error in time gem action: " + e);
                }
            }
        }
        /*   GEM SHOULD BE FINISHED
         * right click = if blocks - bone meal, if entity - baby/adult (up to 32 blocks) (WORKS)
         * shift right click = speed 10 (7:30) Haste 3 (WORKS)
         *
         * With gauntlet: hold right click to send player to spawn or remove mob(WORKS)
         */
    }
}
