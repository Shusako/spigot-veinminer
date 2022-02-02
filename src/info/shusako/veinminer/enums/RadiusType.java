package info.shusako.veinminer.enums;

import org.bukkit.Location;

/**
 * Created by Shusako on 12/19/2018.
 * For project Minecraft Veinminer Plugin, 2018
 */
public enum RadiusType {

    MANHATTAN {
        @Override
        public double distance(Location loc1, Location loc2) {
            return Math.abs(loc2.getX() - loc1.getX()) + Math.abs(loc2.getY() - loc1.getY()) + Math.abs(loc2.getZ() -
                    loc1.getZ());
        }
    }, EUCLIDEAN {
        @Override
        public double distance(Location loc1, Location loc2) {
            return loc2.distance(loc1);
        }
    }, CHEBYSHEV {
        @Override
        public double distance(Location loc1, Location loc2) {
            return Math.max(Math.abs(loc2.getZ() - loc1.getZ()), Math.max(Math.abs(loc2.getX() - loc1.getX()), Math
                    .abs(loc2.getY() - loc1.getY())));
        }
    };

    public abstract double distance(Location loc1, Location loc2);
}
