package info.shusako.veinminer;

import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shusako on 12/14/2018.
 * For project Minecraft Veinminer Plugin, 2018
 */
public class VeinList {

    public long startTime;
    public List<Block> blockList = new ArrayList<>();
    public Block source;
    public int taskID;

    public VeinList() {}
}
