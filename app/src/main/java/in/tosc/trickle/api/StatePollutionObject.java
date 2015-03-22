package in.tosc.trickle.api;

/**
 * Created by championswimmer on 22/3/15.
 */
public class StatePollutionObject {
    public String state;
    public double latitude;
    public double longitude;
    public int level;

    public StatePollutionObject(String state, double latitude, double longitude, int level) {
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
        this.level = level;
    }
}
