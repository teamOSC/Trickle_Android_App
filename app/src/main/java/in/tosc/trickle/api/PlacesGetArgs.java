package in.tosc.trickle.api;

/**
 * Created by championswimmer on 22/3/15.
 */
public class PlacesGetArgs {
    public enum Type {
        TYPE_ATM("atm"),
        TYPE_RESTAURANT("restaurant,food,cafe"),
        TYPE_GAS_STATION("gas_station"),
        TYPE_HOSPITAL("hospital,doctor");

        private final String text;

        /**
         * @param text
         */
        private Type(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }

    public PlacesGetArgs (double lat, double lng, Type t) {
        latitude = lat;
        longitude = lng;
        placetype = t;
    }


    public double latitude;
    public double longitude;
    public Type placetype;

}
