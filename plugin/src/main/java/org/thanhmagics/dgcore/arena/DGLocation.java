package org.thanhmagics.dgcore.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DGLocation implements Serializable {

    public static List<DGLocation> locations = new ArrayList<>();

    public String w;

    public Integer x, y, z;

    public DGLocation(String w, int x, int y, int z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static DGLocation valueOf(String w, int x, int y, int z) {
        for (DGLocation location : locations) {
            if (location.w.equals(w) &&
                    location.x.equals(x) &&
                    location.y.equals(y) &&
                    location.z.equals(z))
                return location;
        }
        DGLocation dgLocation = new DGLocation(w, x, y, z);
        locations.add(dgLocation);
        return dgLocation;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DGLocation dl)) return false;
        if (!dl.w.equals(w)) return false;
        if (!dl.x.equals(x)) return false;
        if (!dl.y.equals(y)) return false;
        if (!dl.z.equals(z)) return false;
        return true;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(w), x, y, z);
    }

    public DGLocation setX(Integer x) {
        this.x = x;
        return this;
    }

    public DGLocation setY(Integer x) {
        this.y = x;
        return this;
    }

    public DGLocation setZ(Integer x) {
        this.z = x;
        return this;
    }


    public String toString() {
        return w + "," + x + "," + y + "," + z;
    }
}
