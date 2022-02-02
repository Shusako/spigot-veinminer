package info.shusako.veinminer;

import info.shusako.veinminer.enums.ActivationMode;
import info.shusako.veinminer.enums.RadiusType;
import info.shusako.veinminer.enums.VeinSearchType;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Shusako on 12/13/2018.
 * For project Minecraft Veinminer Plugin, 2018
 */
public class MinerListener implements Listener {

    // holds the VeinList (the list that contains all the marked blocks) per player
    private final HashMap<Player, VeinList> playerMarkedBlocks = new HashMap<>();

    // list of blocks veinminer breaks so we don't act on them in BlockBreakEvent methods
    private List<Block> veinBreakList = new ArrayList<>();

    // defined in static block
    private int minParticlesPerEdge;
    private int maxParticlesPerEdge;

    // the offsets for the immediate neighbors, this should not be changed as it is used for detecting visibility
    private final int[][] immediateNeighborOffsets =
            {{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1}, {0, 0, 1}};

    // TODO: make this a data structure of it's own, too many dimensions
    // "ParticleMaster" hold n "ParticleType" holds 6 "ParticleFaces" holds n "ParticlePositions"
    // ParticleMaster.getParticleType(T).getWithParticleCount(N).getFace(F) => float[particle index][x, y, z]
    // [number of particles - min (min particles => 0)][face][particles][particle x, y, z]
    private float[][][][] particleOffsets = {{{}}};

    MinerListener() {
        setupParticleOffsets();
    }

    void setupParticleOffsets() {
        minParticlesPerEdge = Utils.getServerSettings(Integer.class, "particles_per_edge.min");
        maxParticlesPerEdge = Utils.getServerSettings(Integer.class, "particles_per_edge.max");

        if (minParticlesPerEdge < 2) {
            System.out.println("server.particles_per_edge.min is less than 2! Overriding...");
            minParticlesPerEdge = 2;
        }
        if (maxParticlesPerEdge <= minParticlesPerEdge) {
            System.out.println("server.particles_per_edge.max is not greater than server.particles_per_edge.min! " +
                    "Overriding...");
            maxParticlesPerEdge = minParticlesPerEdge + 1;
        }

        int[] innerIndices = new int[6];
        this.particleOffsets = new float[maxParticlesPerEdge - minParticlesPerEdge + 1][6][maxParticlesPerEdge *
                maxParticlesPerEdge][3];

        // TODO: "ParticleDrawer" TYPES
        float upperLimit = 1 - 0.0001F;
        // So this is basically generating all the particle offsets to cover a block, and storing them in such a way
        // that particleOffsets[x][0] refers to all the particles that should be visible when neighborOffsets[0] is
        // not a block, when there are x particles per edge
        for (int n = minParticlesPerEdge; n <= maxParticlesPerEdge; n++) {
            final int particleIndex = n - minParticlesPerEdge;
            float particleMicroOffset = 1.0F / (n - 1);
            Arrays.fill(innerIndices, 0);
            for (float i = 0; i <= 1; i += particleMicroOffset) {
                for (float j = 0; j <= 1; j += particleMicroOffset) {
                    for (float k = 0; k <= 1; k += particleMicroOffset) {
                        if (i == 0 || i > upperLimit || j == 0 || j > upperLimit || k == 0 || k > upperLimit) {
                            if (i == 0) {
                                this.particleOffsets[particleIndex][0][innerIndices[0]++] = new float[]{i, j, k};
                            }
                            if (i > upperLimit) {
                                this.particleOffsets[particleIndex][1][innerIndices[1]++] = new float[]{i, j, k};
                            }
                            if (j == 0) {
                                this.particleOffsets[particleIndex][2][innerIndices[2]++] = new float[]{i, j, k};
                            }
                            if (j > upperLimit) {
                                this.particleOffsets[particleIndex][3][innerIndices[3]++] = new float[]{i, j, k};
                            }
                            if (k == 0) {
                                this.particleOffsets[particleIndex][4][innerIndices[4]++] = new float[]{i, j, k};
                            }
                            if (k > upperLimit) {
                                this.particleOffsets[particleIndex][5][innerIndices[5]++] = new float[]{i, j, k};
                            }
                        }
                    }
                }
            }
        }
    }

    // TODO: Allow customizable block selection for things that shouldn't need tools

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        ActivationMode mode = Utils.getPlayerSetting(ActivationMode.class, player, "activation_mode");

        if (mode == ActivationMode.SNEAK_MARK && event.isSneaking() &&
                Utils.isTool(player.getInventory().getItemInMainHand())) {
            Block block = player.getTargetBlockExact(6, FluidCollisionMode.NEVER);
            if (block != null) {
                playSound(player);

                VeinList veinList = getVeinList(block, player);
                registerVeinList(player, veinList);

                showParticles(player, veinList);
            }
        }
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // if we're right-clicking a block with something that isn't a block
        ActivationMode mode = Utils.getPlayerSetting(ActivationMode.class, player, "activation_mode");

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && mode == ActivationMode.RIGHT_CLICK &&
                Utils.isTool(player.getInventory().getItemInMainHand())) {

            playSound(player);

            VeinList veinList = getVeinList(event.getClickedBlock(), player);
            registerVeinList(player, veinList);

            showParticles(player, veinList);
        }
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (this.veinBreakList.contains(event.getBlock())) {
            this.veinBreakList.remove(event.getBlock());
            return;
        }

        if (!this.playerMarkedBlocks.containsKey(player)) {
            // If it's ALWAYS or SNEAK_ALWAYS, create a list on break
            ActivationMode mode = Utils.getPlayerSetting(ActivationMode.class, player, "activation_mode");

            if ((mode == ActivationMode.ALWAYS || (mode == ActivationMode.SNEAK_ALWAYS && player.isSneaking())) &&
                    Utils.isTool(player.getInventory().getItemInMainHand())) {
                VeinList veinList = getVeinList(event.getBlock(), player);
                this.playerMarkedBlocks.put(player, veinList);
            }
        }

        if (this.playerMarkedBlocks.containsKey(player)) {
            VeinList veinList = this.playerMarkedBlocks.get(player);

            int playerOpenDuration = Utils.getPlayerSetting(Integer.class, player, "open_duration");

            // remove it, as if a player breaks a block not in the vein list, they probably want the vein canceled
            this.playerMarkedBlocks.remove(player);

            // the time has expired
            if (veinList.startTime + playerOpenDuration < System.currentTimeMillis()) return;

            // break all blocks in the list if the broken block is apart of the list
            if (veinList.blockList.contains(event.getBlock())) {
                createDestroyTask(player, veinList);
            }
        }
    }

    private void createDestroyTask(Player player, VeinList veinList) {
        RadiusType radiusType = Utils.getPlayerSetting(RadiusType.class, player, "radius_type");
        int maxRadius = Utils.getPlayerSetting(Integer.class, player, "max_radius");

        veinList.taskID = player.getServer().getScheduler().scheduleSyncRepeatingTask(Veinminer.instance, () -> {
            if (veinList.blockList.isEmpty() || !player.isOnline()) {
                // cancel our self
                player.getServer().getScheduler().cancelTask(veinList.taskID);
            } else {
                int blocksPerTick = Utils.getPlayerSetting(Integer.class, player, "blocks_per_tick");
                int blocksThisTick = 0;
                // TODO: Cancel veinmine if player switches hands / isn't holding a tool?
                while (blocksThisTick++ < blocksPerTick && !veinList.blockList.isEmpty()) {
                    Block block = veinList.blockList.remove(0);

                    if (radiusType.distance(veinList.source.getLocation(), block.getLocation()) <= maxRadius) {
                        playerBreak(player, block);
                    }
                }
            }
        }, 0L, Utils.getPlayerSetting(Integer.class, player, "tick_skip"));
    }

    private void playSound(Player player) {
        // confirmation sound
        if (Utils.getPlayerSetting(Boolean.class, player, "sound_enabled")) {
            Sound playerSound = Utils.getPlayerSetting(Sound.class, player, "sound");
            player.playSound(player.getLocation(), playerSound, SoundCategory.BLOCKS,
                    Utils.getPlayerSetting(Integer.class, player, "sound_volume"), 1.0F);
        }
    }

    private void showParticles(Player player, VeinList veinList) {
        if (Utils.getPlayerSetting(Boolean.class, player, "particles_enabled")) {
            // TODO: SEPARATE THIS LOGIC INTO A "ParticleDrawer" CLASS
            int playerParticlesPerEdge = Utils.getPlayerSetting(Integer.class, player, "particles_per_edge");

            float particleMicroOffset = 1.0F / (playerParticlesPerEdge - 1);
            if (particleMicroOffset > 0.15F) particleMicroOffset = 0.15F;

            Particle playerParticle = Utils.getPlayerSetting(Particle.class, player, "particle");

            for (Block block : veinList.blockList) {
                for (int i = 0; i < this.immediateNeighborOffsets.length; i++) {
                    int[] offset = this.immediateNeighborOffsets[i];

                    // this side is empty, lets get some particles going
                    if (block.getRelative(offset[0], offset[1], offset[2]).isPassable()) {
                        int limit = playerParticlesPerEdge * playerParticlesPerEdge;
                        int current = 0;
                        for (float[] particleOffset : this.particleOffsets[playerParticlesPerEdge -
                                minParticlesPerEdge][i]) {
                            if (current++ > limit) break;

                            player.spawnParticle(playerParticle,
                                    block.getLocation().add(particleOffset[0], particleOffset[1], particleOffset[2]), 1,
                                    (Math.random() - 0.5D) * particleMicroOffset,
                                    (Math.random() - 0.5D) * particleMicroOffset,
                                    (Math.random() - 0.5D) * particleMicroOffset, 0);
                        }
                    }
                }
            }
        }
    }

    private void registerVeinList(Player player, VeinList veinList) {
        // only have one list per player, put will replace it but I like this for clarity
        if (this.playerMarkedBlocks.containsKey(player)) {
            this.playerMarkedBlocks.remove(player);
        }
        this.playerMarkedBlocks.put(player, veinList);
    }

    private VeinList getVeinList(Block block, Player player) {
        return Utils.getPlayerSetting(VeinSearchType.class, player, "vein_search_type").getVeinList(block, player);
    }

    private void playerBreak(Player player, Block block) {
        // This list is to keep track of blocks that veinminer breaks so we don't act on them when getting
        // a BlockBreakEvent
        veinBreakList.add(block);

        // have the player break the block, doing it this way helps with item enchantments
        player.breakBlock(block);
//        ((CraftPlayer) player).getHandle().playerInteractManager.breakBlock(Utils.locationToBlockPosition(block
//                .getLocation()));
    }
}
