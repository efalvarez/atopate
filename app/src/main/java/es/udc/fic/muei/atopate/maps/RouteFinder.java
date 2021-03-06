package es.udc.fic.muei.atopate.maps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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

    private static void draw(List<LatLng> coordenadas, GoogleMap mMap, String markerTitle, String markerSnippet, int width, int height) {
        if (coordenadas.size() > 1) {
            mMap.addPolyline(new PolylineOptions().addAll(coordenadas));

            if (markerTitle != null && markerSnippet != null) {
                mMap.addMarker(new MarkerOptions().position(coordenadas.get(coordenadas.size() - 1)).title(markerTitle).snippet(markerSnippet));
            } else {
                mMap.addMarker(new MarkerOptions().position(coordenadas.get(coordenadas.size() - 1)));
            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng pos : coordenadas) {
                builder.include(pos);
            }
            LatLngBounds bounds = builder.build();
            int padding = (int) (height * 0.25); // offset from edges of the map in pixels (25%)
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);
        }
    }

    private static String getEndLocationTitle(DirectionsResult results) {
        return results.routes[0].legs[0].distance.humanReadable + " - " + results.routes[0].legs[0].duration.humanReadable;
    }

    public static void drawRoute(List<LatLng> coordenadas, GoogleMap mMap, int width, int height) {
        draw(coordenadas, mMap, null, null, width, height);
    }

    public static DirectionsResult drawRoute(LatLng from, LatLng to, GoogleMap mMap, int width, int height) {
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

            draw(PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath()), mMap, result.routes[0].legs[0].endAddress, getEndLocationTitle(result), width, height);

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public static List<LatLng> getRoute(String from, String to) {
        try {
            DirectionsResult result = DirectionsApi.newRequest(context)
                    .origin(from)
                    .destination(to)
                    .mode(TravelMode.DRIVING)
                    .language("es")
                    .await();

            return PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath());

        } catch (Exception e) {
            return null;
        }
    }

}