package es.udc.fic.muei.atopate.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.util.List;

public class RouteFinder {
    private static final String apiKey = "AIzaSyCeMcDjaJsg2gDGfXGOn3GRFv1ippD6Pqw";

    private static final GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();

    private static void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private static String getEndLocationTitle(DirectionsResult results) {
        return results.routes[0].legs[0].distance.humanReadable + " - " + results.routes[0].legs[0].duration.humanReadable;
    }

    public static DirectionsResult drawRoute(LatLng from, LatLng to, GoogleMap mMap) {
        com.google.maps.model.LatLng gLocStart = new com.google.maps.model.LatLng(
                from.latitude, from.longitude);
        com.google.maps.model.LatLng gLocEnd = new com.google.maps.model.LatLng(
                to.latitude, to.longitude);
        try {
            DirectionsResult result = DirectionsApi.newRequest(context)
                    .origin(gLocStart)
                    .destination(gLocEnd)
                    .mode(TravelMode.WALKING)
                    .language("es")
                    .await();

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(result.routes[0].legs[0].endLocation.lat,
                            result.routes[0].legs[0].endLocation.lng))
                    .title(result.routes[0].legs[0].endAddress)
                    .snippet(getEndLocationTitle(result)));

            addPolyline(result, mMap);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

}