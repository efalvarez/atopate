package es.udc.fic.muei.atopate.maps;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsConfigurer {

    private static GoogleMap mMap;

    public static void initializeMap(SupportMapFragment mapFragment, OnMapReadyCallback callback) {
        mapFragment.getMapAsync(callback);
    }

    public static void onMapsReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(43.333024, -8.410868);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in FIC"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

}
