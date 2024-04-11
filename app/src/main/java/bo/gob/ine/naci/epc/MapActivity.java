package bo.gob.ine.naci.epc;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Map;

import bo.gob.ine.naci.epc.entidades.Upm;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityNavigator;


public final class MapActivity extends ActionBarActivityNavigator implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ArrayList<Map<String, Object>> valores = new ArrayList<>();
    private int upmActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Bundle bundle = getIntent().getExtras();
        upmActual = bundle.getInt("idUpm");

        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_map));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.background_lvs);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        Upm upm = new Upm();
        valores = upm.obtenerListado(Usuario.getUsuario());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setCompassEnabled(false);
        uiSettings.setMapToolbarEnabled(false);

        LatLng mainLocation = new LatLng(-16.499308, -68.121568);
        for( Map<String, Object> obj : valores ) {
            double currentLat = (double)obj.get("latitud") == 0 ? -16.499308d : (double)obj.get("latitud");
            double currentLon = (double)obj.get("longitud") == 0 ? -68.121568d : (double)obj.get("longitud");
            if((Integer)obj.get("id_upm") == upmActual) {
                mainLocation = new LatLng(currentLat, currentLon);
                mMap.addMarker(new MarkerOptions()
                        .position(mainLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_rojo))
                        .title(String.valueOf(obj.get("codigo")) + " - " + String.valueOf(obj.get("nombre"))));
            } else {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(currentLat, currentLon))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_azul))
                        .title(String.valueOf(obj.get("codigo")) + " - " + String.valueOf(obj.get("nombre"))));
            }
        }

        CameraPosition cameraPosition = CameraPosition.builder()
                .target(mainLocation)
                .zoom(18)
                .build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        /*irPrincipal();
        finish();*/
    }
}
