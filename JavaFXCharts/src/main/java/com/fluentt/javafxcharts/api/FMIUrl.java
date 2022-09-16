package com.fluentt.javafxcharts.api;

/**
 * This class is used to generate the urls to fetch data
 * from the Finnish Meteorological Institute (FMI).
 * @author J.L.
 */
public class FMIUrl {

    static String ROOT = "https://opendata.fmi.fi/wfs?request=getFeature&version=2.0.0";


    /**
     * This method returns an url formatted according to the parameters
     * to fetch observed data from the FMI api.
     * @param temp does user want temperature data
     * @param wind does user want observed wind data
     * @param cloudiness does user want observed cloudiness data
     * @param x_min minimum x coordinate
     * @param y_min minimum y coordinate
     * @param x_max maximum x coordinate
     * @param y_max maximum y coordinate
     * @param start_time the starting time
     * @param end_time the ending time
     * @param timestep the timestep that seperates the data
     * @return the formatted url as a String
     */
    public static String getObservedURL(double x_min, double y_min, double x_max, double y_max,
                                 String start_time, String end_time, int timestep,
                                 boolean temp, boolean wind, boolean cloudiness) {

        String url = ROOT;

        // Configure query
        url += "&storedquery_id=fmi::observations::weather::simple";

        // Add coordinates
        url += "&bbox="+x_min+","+y_min+","+x_max+","+y_max;

        // Add time interval
        url += "&starttime="+start_time+"&endtime="+end_time;

        // Add timestep
        url += "&timestep="+timestep;

        // Add parameters
        url += "&parameters=";

        if (temp)
            url += "t2m,";

        if (wind)
            url += "ws_10min,";

        if (cloudiness)
            url += "n_man,";

        return url.substring(0, url.length() - 1);  // Remove extra comma
    }

    public static String getForecastURL(double lat, double lon,
                                 String start_time, String end_time, int timestep,
                                 boolean temp, boolean wind) {

        String url = ROOT;

        // Configure query
        url += "&storedquery_id=fmi::forecast::harmonie::surface::point::simple";

        // Add latitude and longitude
        url += "&latlon="+lat+","+lon;

        url += "&starttime="+start_time+"&endtime="+end_time;
        url += "&timestep="+timestep;

        // Add parameters
        url += "&parameters=";

        if (temp)
            url += "temperature,";

        if (wind)
            url += "windspeedms,";

        return url.substring(0, url.length() - 1);  // Remove extra comma
    }

    public static String getStatURL(double x_min, double y_min, double x_max, double y_max,
                             String start_time, String end_time,
                             boolean avg, boolean min, boolean max) {

        String url = ROOT;

        // Configure query
        url += "&storedquery_id=fmi::observations::weather::hourly::simple";
        url += "&bbox="+x_min+","+y_min+","+x_max+","+y_max;
        url += "&starttime="+start_time+"&endtime="+end_time;

        // Add parameters
        url += "&parameters=";
        if (avg)
            url += "TA_PT1H_AVG,";

        if (min)
            url += "TA_PT1H_MIN,";

        if (max)
            url += "TA_PT1H_MAX,";


        return url.substring(0, url.length() - 1);
    }

}