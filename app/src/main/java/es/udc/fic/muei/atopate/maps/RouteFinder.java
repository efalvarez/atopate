package es.udc.fic.muei.atopate.maps;

import android.util.Log;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class RouteFinder {
    public static final boolean WITHOUT_MARKERS = false;
    public static final boolean WITH_MARKERS = true;

    private static final String apiKey = "AIzaSyCeMcDjaJsg2gDGfXGOn3GRFv1ippD6Pqw";

    private static final GeoApiContext context = new GeoApiContext.Builder().apiKey(apiKey).build();

    private static final int MAX_NUMBER_OF_WAYPOINTS = 20;

    private static void draw(List<LatLng> coordenadas, GoogleMap mMap, String markerTitle, String markerSnippet, int width, int height, boolean markers) {
        if (!CollectionUtils.isEmpty(coordenadas)) {
            mMap.addPolyline(new PolylineOptions().addAll(coordenadas));

            if (markers) {
                if (markerTitle != null && markerSnippet != null) {
                    mMap.addMarker(new MarkerOptions().position(coordenadas.get(coordenadas.size() - 1)).title(markerTitle).snippet(markerSnippet));
                } else {
                    mMap.addMarker(new MarkerOptions().position(coordenadas.get(coordenadas.size() - 1)));
                }
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

    public static void drawRoute(List<LatLng> coordenadas, GoogleMap mMap, int width, int height, boolean markers) {
        draw(coordenadas, mMap, null, null, width, height, markers);
    }

    public static void drawingRoute(ArrayList<LatLng> caminoRecorrido, GoogleMap mMap, int width, int height) {
        if (caminoRecorrido.size() > 1) {
            mMap.clear();

            List<LatLng> caminoGenerado = getTotalRoute(caminoRecorrido);

            try {
                assert caminoGenerado != null;
                mMap.addPolyline(new PolylineOptions().addAll(caminoGenerado));
            } catch (NullPointerException npe) {
                Log.e(TAG, "drawingRoute: No se genera un camino correcto con el API de google", npe);
                mMap.addPolyline(new PolylineOptions().addAll(caminoRecorrido));
            }

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng pos : caminoRecorrido) {
                builder.include(pos);
            }
            LatLngBounds bounds = builder.build();
            int padding = (int) (height * 0.25); // offset from edges of the map in pixels (25%)
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);
        }
    }

    public static DirectionsResult drawRoute(LatLng from, LatLng to, GoogleMap mMap, int width, int height, boolean markers) {
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

            draw(PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath()), mMap, result.routes[0].legs[0].endAddress, getEndLocationTitle(result), width, height, markers);

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

    private static List<LatLng> getTotalRoute(ArrayList<LatLng> caminoRecorrido) {
        List<com.google.maps.model.LatLng> caminoMaps = new ArrayList<>();
        List<LatLng> resultado = new ArrayList<>();

        if (!CollectionUtils.isEmpty(caminoRecorrido)) {

            LatLng posicionInicial = caminoRecorrido.get(0);
            LatLng posicionFinal = caminoRecorrido.get(caminoRecorrido.size() - 1);

            com.google.maps.model.LatLng posicionInicialMaps =
                    new com.google.maps.model.LatLng(posicionInicial.latitude, posicionInicial.longitude);
            com.google.maps.model.LatLng posicionFinalMaps =
                    new com.google.maps.model.LatLng(posicionFinal.latitude, posicionFinal.longitude);

            // hacemos un calculo para reducir la cantidad de puntos que le mandamos a maps para
            // calcular la ruta
            float divisionValue = (float) caminoRecorrido.size() / MAX_NUMBER_OF_WAYPOINTS;
            int amountOfLocationsToSKip = Math.round(divisionValue);
            amountOfLocationsToSKip = Math.max(amountOfLocationsToSKip, 1);


            for (int i = 0; i < caminoRecorrido.size(); i+=amountOfLocationsToSKip) {
                LatLng posicionRecorrido = caminoRecorrido.get(i);
                caminoMaps.add(new com.google.maps.model.LatLng(posicionRecorrido.latitude, posicionRecorrido.longitude));
            }


            try {

            DirectionsResult result = null;
                result = DirectionsApi.newRequest(context)
                        .origin(posicionInicialMaps)
                        .destination(posicionFinalMaps)
                        .mode(TravelMode.DRIVING)
                        .language("es")
                        .waypoints(caminoMaps.toArray(new com.google.maps.model.LatLng[0]))
                        .await();

                resultado.addAll(PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath()));

            } catch (ApiException e) {
                Log.d(TAG, "Ha habido un problema a la hora de recuperar los valores del camino recorrido", e);

            } catch (InterruptedException e) {
                Log.d(TAG, "Ha habido un problema a la hora de recuperar los valores del camino recorrido", e);

            } catch (IOException e) {
                Log.d(TAG, "Ha habido un problema a la hora de recuperar los valores del camino recorrido", e);

            }


        }


//        try {
//            if(caminoRecorrido.size() > 1) {
//
//
//
//                for (LatLng latLng : caminoRecorrido) {
//                    caminoMaps.add(new com.google.maps.model.LatLng(latLng.latitude, latLng.longitude));
//                }
//
//                for (int i=1; i<caminoRecorrido.size();i++){
//                    com.google.maps.model.LatLng from = caminoMaps.get(i-1);
//                    com.google.maps.model.LatLng to = caminoMaps.get(i);
//                    DirectionsResult result = DirectionsApi.newRequest(context)
//                            .origin(from)
//                            .destination(to)
//                            .mode(TravelMode.DRIVING)
//                            .language("es")
//                            .await();
//                    resultado.addAll(PolyUtil.decode(result.routes[0].overviewPolyline.getEncodedPath()));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return resultado;
    }

}