package in.tosc.trickle.api;

/**
 * Created by championswimmer on 22/3/15.
 */
public class DistStatsObject {
    public String name;
    public double latitude;
    public double longitude;
    public int sexRatio;
    public long population;
    public float literacy;
    public float growth;

    public DistStatsObject(String name, double latitude, double longitude, int sexRatio, long population, float literacy, float growth) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sexRatio = sexRatio;
        this.population = population;
        this.literacy = literacy;
        this.growth = growth;
    }
}
