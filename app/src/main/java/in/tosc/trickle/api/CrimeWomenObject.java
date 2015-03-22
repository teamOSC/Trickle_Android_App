package in.tosc.trickle.api;

/**
 * Created by championswimmer on 22/3/15.
 */
public class CrimeWomenObject {
    public String address;
    public double latitude;
    public double longitude;
    public int crimes;

    public CrimeWomenObject(String address, double latitude, double longitude, int crimes) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.crimes = crimes;
    }
}
