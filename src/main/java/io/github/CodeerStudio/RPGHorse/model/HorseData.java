package io.github.CodeerStudio.RPGHorse.model;
import org.bukkit.Location;

public class HorseData {
    private double distanceTraveled;
    private Location lastPosition;

    // Constructor to initialize the data
    public HorseData(Location lastPosition) {
        this.distanceTraveled = 0.0;
        this.lastPosition = lastPosition;
    }

    // Getters and setters
    public double getDistanceTraveled() {
        return distanceTraveled;
    }

    public void setDistanceTraveled(double distanceTraveled) {
        this.distanceTraveled = distanceTraveled;
    }

    public Location getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Location lastPosition) {
        this.lastPosition = lastPosition;
    }
}
