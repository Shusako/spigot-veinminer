package info.shusako.veinminer.enums;

import info.shusako.veinminer.Utils;
import info.shusako.veinminer.VeinList;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Shusako on 12/17/2018.
 * For project Minecraft Veinminer Plugin, 2018
 */
public enum VeinSearchType {

    // TODO: multiple active search types, attach to activation modes
    // will have to enable and disable activation modes, and attach search types to them
    // e.g. BFS on right click, PLANE on always

    CARDINAL {
        // the offsets for the neighbors
        private final int[][] neighborOffsets = {{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1},
                {0, 0, 1}};

        @Override
        public VeinList getVeinList(Block source, Player player) {
            return getVeinListBFS(source, player, neighborOffsets);
        }
    }, CUBE {
        private final int[][] neighborOffsets = {{-1, 0, 0}, {1, 0, 0}, {0, -1, 0}, {0, 1, 0}, {0, 0, -1},
                {0, 0, 1}, {-1, -1, 0}, {1, -1, 0}, {0, -1, -1}, {0, -1, 1}, {-1, 1, 0}, {1, 1, 0}, {
                0, 1, -1}, {0, 1, 1}, {-1, 0, -1}, {-1, 0, 1}, {1, 0, -1}, {1, 0, 1}, {-1, -1, -1}, {-1,
                -1, 1}, {1, -1, -1}, {1, -1, 1}, {-1, 1, -1}, {-1, 1, 1}, {1, 1, -1}, {1, 1, 1}};

        @Override
        public VeinList getVeinList(Block source, Player player) {
            // could be done "faster" with a triple nested loop, but this allowed it to adhere to max_blocks
            // in a way I would describe as "better
            return getVeinListBFS(source, player, neighborOffsets);
        }
    }, HAMMER {
        // These are just rotations of each other, but it's easier to do it this way
        private final int[][] xExcluded = {{0, 1, 0}, {0, 1, 1}, {0, 0, 1}, {0, -1, 1}, {0, -1, 0}, {0,
                -1, -1}, {0, 0, -1}, {0, 1, -1}};
        private final int[][] yExcluded = {{1, 0, 0}, {1, 0, 1}, {0, 0, 1}, {-1, 0, 1}, {-1, 0, 0}, {-1,
                0, -1}, {0, 0, -1}, {1, 0, -1}};
        private final int[][] zExcluded = {{0, 1, 0}, {1, 1, 0}, {1, 0, 0}, {1, -1, 0}, {0, -1, 0}, {-1,
                -1, 0}, {-1, 0, 0}, {-1, 1, 0}};

        @Override
        public VeinList getVeinList(Block source, Player player) {
            BlockIterator blockIterator = new BlockIterator(player, 30);
            Block previousBlock = null;
            while (blockIterator.hasNext()) {
                Block block = blockIterator.next();
                if (block.equals(source))
                    break;
                else {
                    previousBlock = block;
                }
            }
            if (previousBlock == null) {
                return new VeinList();
            } else {
                BlockFace blockFace = source.getFace(previousBlock);
                if (blockFace == null)
                    return new VeinList();

                switch (blockFace) {
                    case WEST:
                    case EAST:
                        return VeinSearchType.getVeinListBFS(source, player, xExcluded);
                    case NORTH:
                    case SOUTH:
                        return VeinSearchType.getVeinListBFS(source, player, zExcluded);
                    case UP:
                    case DOWN:
                        return VeinSearchType.getVeinListBFS(source, player, yExcluded);
                    default:
                        player.sendMessage("Invalid blockface. Report this.");
                        return new VeinList();
                }
            }
        }
    };

    public abstract VeinList getVeinList(Block source, Player player);

    private static VeinList getVeinListBFS(Block source, Player player, int[][] neighborOffsets) {
        boolean checkType = Utils.getPlayerSetting(Boolean.class, player, "check_type");

        VeinList veinList = new VeinList();
        veinList.startTime = System.currentTimeMillis();
        veinList.source = source;
        veinList.blockList.add(source);

        int playerMaxBlocks = Utils.getPlayerSetting(Integer.class, player, "max_blocks");
        RadiusType radiusType = Utils.getPlayerSetting(RadiusType.class, player, "radius_type");
        int maxRadius = Utils.getPlayerSetting(Integer.class, player, "max_radius");

        // Find nearby blocks using BFS
        Queue<Block> bfsQueue = new LinkedList<>();
        bfsQueue.add(source);

        while (!bfsQueue.isEmpty()) {
            Block front = bfsQueue.remove();
            for (int[] offset : neighborOffsets) {
                // get the neighbor and see if its the same block before continuing
                Block neighbor = front.getRelative(offset[0], offset[1], offset[2]);

                // TODO: Should checkType be player set?
                if (checkType && neighbor.getType() != source.getType() || (neighbor.isLiquid() || neighbor.getType()
                        == Material.AIR))
                    continue;

                // enforce max radius
                if (radiusType.distance(source.getLocation(), neighbor.getLocation()) > maxRadius) continue;

                // don't bother with blocks that the list already has
                if (veinList.blockList.contains(neighbor)) continue;

                // we can only break up to playerMaxBlocks blocks
                if (veinList.blockList.size() >= playerMaxBlocks) break;

                // passed all conditions, add it to the list
                bfsQueue.add(neighbor);
                veinList.blockList.add(neighbor);
            }
        }
        return veinList;
    }
}
