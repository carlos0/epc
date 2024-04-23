package bo.gob.ine.naci.epc;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bo.gob.ine.naci.epc.entidades.Upm;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityNavigator;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import bo.gob.ine.naci.epc.herramientas.ToolMaps;

public final class MapActivity2 extends ActionBarActivityNavigator implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveCanceledListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnPolygonClickListener {
    private GoogleMap mMap;

    private int idUpm;
    private String codUpm;
    private ArrayList<String> gCodUpm;

    private ArrayList<Map<String, Object>> valoresPerimetro = new ArrayList<>();
    private ArrayList<Map<String, Object>> valoresPredio = new ArrayList<>();
    private ArrayList<Map<String, Object>> valoresDisperso = new ArrayList<>();

    public static final int PATTERN_DASH_LENGTH_PX = 5;
    public static final int PATTERN_GAP_LENGTH_PX = 5;
    public static final PatternItem DOT = new Dot();
    public static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    public static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    public static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    public static final int PATTERN_DASH_LENGTH_PX1 = 5;
    public static final int PATTERN_GAP_LENGTH_PX1 = 15;
    public static final PatternItem DASH1 = new Dash(PATTERN_DASH_LENGTH_PX1);
    public static final PatternItem GAP1 = new Gap(PATTERN_GAP_LENGTH_PX1);
    public static final PatternItem DOT1 = new Dot();
    public static final List<PatternItem> PATTERN_POLYGON_BETA = Arrays.asList(GAP1, DASH1);

    public static final int PATTERN_DASH_LENGTH_PX2 = 20;
    public static final int PATTERN_GAP_LENGTH_PX2 = 10;
    public static final PatternItem DASH2 = new Dash(PATTERN_DASH_LENGTH_PX2);
    public static final PatternItem GAP2 = new Gap(PATTERN_GAP_LENGTH_PX2);
    public static final PatternItem DOT2 = new Dot();
    public static final List<PatternItem> PATTERN_POLYGON_GAMA = Arrays.asList(DOT2, GAP2, DASH2, GAP2);

    private ArrayList<Marker> gMarkerPerimetro = new ArrayList<>();
    private ArrayList<Marker> gMarkerPredio = new ArrayList<>();
    private Map<Marker, Polygon> gMarkerPredioPolygon = new HashMap<>();
    private ArrayList<Marker> gMarkerRecorrido = new ArrayList<>();
    private Map<Marker, Polygon> gMarkerDispersoPolygon = new HashMap<>();
    private ArrayList<Marker> gMarkerDisperso = new ArrayList<>();

    private ArrayList<LatLng> puntosZoom = new ArrayList<>();

    private ArrayList<Polygon> polygonPerimetros = new ArrayList<>();
    private Map<Polygon, Polygon> polygonBorderPerimetros = new HashMap<>();
    private ArrayList<Polygon> polygonPredios = new ArrayList<>();
    private Map<Polygon, Polygon> polygonBorderPredios = new HashMap<>();
    private ArrayList<Polygon> polygonDisperso = new ArrayList<>();
    private Map<Polygon, Polygon> polygonBorderDisperso = new HashMap<>();

    public int contPerimetros = 0;
    public int contPredios = 0;
    public int contDisperso = 0;

    private Map<String, TileOverlay> valoresOverlay = new HashMap<>();

    private MaterialButton perimetro, predio, disperso, map, google, recorrido, ubicacion;

    private MaterialButtonToggleGroup toogleButtons;

    private Polygon poligonoSeleccionado = null;

    int colorBordeSeleccion = MyApplication.getContext().getResources().getColor(R.color.color_seleccion_borde);

    int colorBordeSegmento = Color.RED;
    int colorRellenoSegmento = MyApplication.getContext().getResources().getColor(R.color.color_relleno_segmento);

    private TextView mensaje;
    private LinearLayout content_mensaje;
    private int tipo;

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Bundle bundle = getIntent().getExtras();
        idUpm = bundle.getInt("idUpm");

//        correlativo = bundle.getInt("correlativo");
//        sectorSegmento = bundle.getBoolean("sectorSegmento");
//
//        init();
//
//        actionButons();
//
//        actionCapas();

        Upm upm = new Upm();

        codUpm = Upm.getCodUpm(idUpm);
        valoresPerimetro = upm.obtenerPerimetro(idUpm);
        valoresPredio = upm.obtenerPredio(idUpm);
        valoresDisperso = upm.obtenerDisperso(idUpm);

        toogleButtons = findViewById(R.id.fabMenuMain2);

        perimetro = findViewById(R.id.fabPerimetro);
        predio = findViewById(R.id.fabPredio);
        recorrido = findViewById(R.id.fabRecorrido);
        disperso = findViewById(R.id.fabDisperso);
        map = findViewById(R.id.fabMap);
        google = findViewById(R.id.fabGoogle);
        ubicacion = findViewById(R.id.fabUbicacion);

        mensaje = findViewById(R.id.mensaje);
        content_mensaje = findViewById(R.id.content_mensaje);

//        perimetro.setBackgroundColor(getResources().getColor(R.color.color_transparent_black));
//        predio.setBackgroundColor(getResources().getColor(R.color.color_transparent_black));
//        disperso.setBackgroundColor(getResources().getColor(R.color.color_transparent_black));
//        map.setBackgroundColor(getResources().getColor(R.color.color_transparent_black));
//        google.setBackgroundColor(getResources().getColor(R.color.color_transparent_black));
        if (codUpm.endsWith("D")) {
            tipo = 2;
            disperso.setVisibility(View.VISIBLE);
        } else {
            tipo = 1;
            perimetro.setVisibility(View.VISIBLE);
            predio.setVisibility(View.VISIBLE);
            recorrido.setVisibility(View.VISIBLE);
//            map.setVisibility(View.VISIBLE);
        }

        perimetro.setChecked(true);
        predio.setChecked(true);
//        recorrido.setChecked(true);
        disperso.setChecked(true);
        map.setChecked(true);
        google.setChecked(false);
        ubicacion.setChecked(true);

        perimetro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (perimetro.isChecked()) {
                    polygonPerimetro(1);
                    marcadorPerimetro(1);
                } else {
                    polygonPerimetro(2);
                    marcadorPerimetro(2);
                }
            }
        });

        predio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (predio.isChecked()) {
                    polygonPredio(1);
                    marcadorPredio(1);
                } else {
                    polygonPredio(2);
                    marcadorPredio(2);
                    mostrarMensaje(null);
                }
            }
        });

        recorrido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recorrido.isChecked()) {
                    marcadorRecorrido(1);
                } else {
                    marcadorRecorrido(2);
                }
            }
        });

        disperso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (disperso.isChecked()) {
                    polygonDisperso(1);
                    marcadorDisperso(1);
                } else {
                    polygonDisperso(2);
                    marcadorDisperso(2);
                }
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (google.isChecked()) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                }
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.setChecked(true);
                areaZoom(puntosZoom);
            }
        });

        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ubicacion.setChecked(true);
                obtenerUbicacionActual();
            }
        });

        startThree();
    }

    public void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mostrarUbicacion();
        }
    }

    private void mostrarUbicacion() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 15));
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.setPadding(0, 100, 0, 0);
        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        LatLng bolivia = new LatLng(-17.239516, -64.644281);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bolivia,5.5f));

        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnPolygonClickListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
        mMap.setOnCameraMoveListener(this);

        //TileProvider wmsTileProvider = TileProviderFactory.getOsgeoWmsTileProvider();
        //mMap.addTileOverlay(new TileOverlayOptions().tileProvider(wmsTileProvider));

//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bolivia, 5.5f));
//
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);
//        uiSettings.setZoomControlsEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
                }
            }
        });

        cargarPerimetros();
        cargarPredios();
        cargarDisperso();

        if (!puntosZoom.isEmpty()) {
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    areaZoom(puntosZoom);
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    cargarCapaIne();
                }
            });
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (poligonoSeleccionado != null) {
            poligonoSeleccionado.setStrokeColor(colorBordeSegmento);
            poligonoSeleccionado.setZIndex(3.1f);
            poligonoSeleccionado = null;
            mostrarMensaje(null);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(gMarkerPredioPolygon.containsKey(marker) || gMarkerDispersoPolygon.containsKey(marker)){
            ArrayList<LatLng> poligonZoom = new ArrayList<>();
            Polygon polygon = null;
            switch (tipo){
                case 1:
                    polygon = gMarkerPredioPolygon.get(marker);
                    break;
                case 2:
                    polygon = gMarkerDispersoPolygon.get(marker);
                    break;
            }
            if (poligonoSeleccionado != null) {
                Log.d("polygon", poligonoSeleccionado.getId());
                if (!poligonoSeleccionado.equals(polygon)) {
                    poligonoSeleccionado.setStrokeColor(colorBordeSegmento);
                    poligonoSeleccionado.setZIndex(3.1f);
                    polygon.setStrokeColor(colorBordeSeleccion);
                    polygon.setZIndex(3.2f);
                    poligonoSeleccionado = polygon;
                    mostrarMensaje((Map<String, Object>) poligonoSeleccionado.getTag());
                    poligonZoom.addAll(poligonoSeleccionado.getPoints());
                    areaZoom(poligonZoom);
                } else {
                    poligonoSeleccionado = null;
                    polygon.setStrokeColor(colorBordeSegmento);
                    polygon.setZIndex(3.1f);
                    mostrarMensaje(null);
                }
            } else {
                polygon.setStrokeColor(colorBordeSeleccion);
                polygon.setZIndex(3.2f);
                poligonoSeleccionado = polygon;
                mostrarMensaje((Map<String, Object>) poligonoSeleccionado.getTag());
                poligonZoom.addAll(poligonoSeleccionado.getPoints());
                areaZoom(poligonZoom);
            }
        }
        return true;
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
        Log.d("polygon", polygon.getId());

        Log.d("polygon", String.valueOf(polygon.getTag()));

        ArrayList<LatLng> poligonZoom = new ArrayList<>();
        if (poligonoSeleccionado != null) {
            Log.d("polygon", poligonoSeleccionado.getId());
            if (!poligonoSeleccionado.equals(polygon)) {
                poligonoSeleccionado.setStrokeColor(colorBordeSegmento);
                poligonoSeleccionado.setZIndex(3.1f);
                polygon.setStrokeColor(colorBordeSeleccion);
                polygon.setZIndex(3.2f);
                poligonoSeleccionado = polygon;
                mostrarMensaje((Map<String, Object>) poligonoSeleccionado.getTag());
                poligonZoom.addAll(poligonoSeleccionado.getPoints());
                areaZoom(poligonZoom);
            } else {
                poligonoSeleccionado = null;
                polygon.setStrokeColor(colorBordeSegmento);
                mostrarMensaje(null);
                polygon.setZIndex(3.1f);
            }
        } else {
            polygon.setStrokeColor(colorBordeSeleccion);
            polygon.setZIndex(3.2f);
            poligonoSeleccionado = polygon;
            mostrarMensaje((Map<String, Object>) poligonoSeleccionado.getTag());
            poligonZoom.addAll(poligonoSeleccionado.getPoints());
            areaZoom(poligonZoom);
        }
    }

    @Override
    public void onCameraMove() {
//        // Cuando la cÃ¡mara se estÃ© moviendo, agregue su objetivo a la ruta actual que dibujaremos en el mapa.
        float zoom = mMap.getCameraPosition().zoom;
        Log.d("castzoom", String.valueOf((int)zoom));
        Log.d("zoom", String.valueOf(zoom));

//        switch ((int) zoom){
//            case 5:
//                break;
//            case 6:
//                break;
//            case 7:
//                break;
//            case 8:
//                break;
//            case 9:
//                break;
//            case 10:
//                break;
//            case 11:
//                break;
//            case 12:
//                break;
//            case 13:
//                break;
//            case 14:
//                break;
//            case 15:
//                break;
//            case 16:
//                break;
//            case 17:
//                break;
//            case 18:
//                break;
//            case 19:
//                break;
//            case 20:
//                break;
//            case 21:
//                break;
//            default:
//                break;
//        }
//        if (tipoArea.equals("1")) {
        if (zoom >= 22 || zoom <= 12) {
            marcadorPerimetro(2);
            marcadorPredio(2);
            marcadorRecorrido(2);
            marcadorDisperso(2);
        } else {
            marcadorPerimetro(1);
            marcadorPredio(1);
            marcadorRecorrido(1);
            marcadorDisperso(1);
        }
//        }

//        addCameraTargetToPath();
    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onCameraMoveStarted(int i) {

    }

    @Override
    public void onCameraMoveCanceled() {

    }

    private void cargarPerimetros() {
        if (!valoresPerimetro.isEmpty()) {
            for (Map<String, Object> pos : valoresPerimetro) {
                String upm = pos.get("upm").toString();
                String ciucom = pos.get("ciucom").toString();
                String geojson = pos.get("geojson").toString();
                Log.d("geojson", geojson);
                Log.d("ciucom", ciucom);
                Log.d("geojson", geojson);

                try {
                    JSONArray geoJsonData = new JSONArray(geojson).getJSONArray(0);
                    ArrayList<LatLng> latLngs = new ArrayList<>();

                    for (int i = 0; i < geoJsonData.length(); i++) {
                        JSONArray geoJsonConvert = geoJsonData.getJSONArray(i);
                        latLngs.add(new LatLng(Double.valueOf(geoJsonConvert.getString(1)), Double.valueOf(geoJsonConvert.getString(0))));
                    }

                    PolygonOptions pc = new PolygonOptions()
                            .clickable(false)
                            .strokeColor(Color.YELLOW)
                            .strokeWidth(8f)
                            .fillColor(this.getResources().getColor(R.color.color_blanco_transparente));
                    pc.addAll(latLngs);

                    PolygonOptions pcB = new PolygonOptions()
                            .clickable(false)
                            .strokeColor(Color.BLACK)
                            .strokeWidth(4f)
                            .fillColor(this.getResources().getColor(R.color.color_blanco_transparente));
                    pcB.addAll(latLngs);
                    pcB.strokePattern(PATTERN_POLYGON_ALPHA);

                    Polygon polygon1 = mMap.addPolygon(pc);
                    polygon1.setZIndex(2f);
                    polygon1.setTag(upm);

                    Polygon polygon2 = mMap.addPolygon(pcB);
                    polygon2.setZIndex(2.1f);
                    polygon2.setTag(upm);

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (LatLng latLng : polygon1.getPoints()) {
                        builder.include(latLng);
                    }

                    LatLngBounds bounds = builder.build();

                    LatLng b2 = bounds.getCenter();

                    Marker markerManz = mMap.addMarker(new MarkerOptions()
                            .position(b2)
                            .draggable(false)
                            .anchor(0.5f, 0.5f)
                            .snippet("MANZANO")
                            .zIndex(2.2f)
                            .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmapManzana(upm, 40.0f, 0xFF44F105, 1))));
                    markerManz.setTag("CAPA");
                    gMarkerPerimetro.add(markerManz);
                    Log.d("polygon1.getPoints()", String.valueOf(polygon1.getPoints()));

                    puntosZoom.addAll(polygon1.getPoints());

                    polygonPerimetros.add(contPerimetros, polygon2);
                    polygonBorderPerimetros.put(polygon2, polygon1);
                    contPerimetros++;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cargarPredios() {
        if (!valoresPredio.isEmpty()) {
            for (Map<String, Object> pos : valoresPredio) {
                String upm = pos.get("upm").toString();
                String orden = pos.get("orden").toString();
                String recorrido = pos.get("recorrido").toString();
                String geojson = pos.get("geojson").toString();
                String tipo = pos.get("tipo").toString();

                Log.d("upm", upm);
                Log.d("orden", orden);
                Log.d("recorrido", recorrido);
                Log.d("geojson", geojson);

                try {
                    JSONArray geoJsonData = new JSONArray(geojson).getJSONArray(0);
                    ArrayList<LatLng> latLngs = new ArrayList<>();

                    for (int i = 0; i < geoJsonData.length(); i++) {
                        JSONArray geoJsonConvert = geoJsonData.getJSONArray(i);
                        latLngs.add(new LatLng(Double.valueOf(geoJsonConvert.getString(1)), Double.valueOf(geoJsonConvert.getString(0))));
                    }

                    PolygonOptions pc = new PolygonOptions()
                            .clickable(false)
                            .strokeColor(Color.YELLOW)
                            .strokeWidth(8f)
                            .fillColor(this.getResources().getColor(R.color.color_blanco_transparente));
                    pc.addAll(latLngs);

                    Polygon polygon1 = mMap.addPolygon(pc);
                    polygon1.setZIndex(3f);
                    polygon1.setTag(pos);

                    PolygonOptions pcB = new PolygonOptions()
                            .clickable(true)
                            .strokeColor(Color.RED)
                            .strokeWidth(5f)
                            .fillColor(this.getResources().getColor(R.color.color_blanco_transparente));
                    pcB.addAll(latLngs);

                    Polygon polygon2 = mMap.addPolygon(pcB);
                    polygon2.setZIndex(3.1f);
                    polygon2.setTag(pos);

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (LatLng latLng : polygon1.getPoints()) {
                        builder.include(latLng);
                    }

                    LatLngBounds bounds = builder.build();

                    LatLng b2 = bounds.getCenter();

                    Marker markerManz = mMap.addMarker(new MarkerOptions()
                            .position(b2)
                            .draggable(false)
                            .anchor(0.8f, 0.5f)
                            .snippet("MANZANO")
                            .zIndex(3.2f)
                            .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmapManzana(orden, 40.0f, 0xFFFB0202, 2))));
                    markerManz.setTag("CAPA");
                    gMarkerPredio.add(markerManz);
                    gMarkerPredioPolygon.put(markerManz, polygon2);
                    Log.d("polygon1.getPoints()", String.valueOf(polygon1.getPoints()));

                    Marker markerRecorrido = mMap.addMarker(new MarkerOptions()
                            .position(b2)
                            .draggable(false)
                            .anchor(0.7f, 0.35f)
                            .snippet("MANZANO")
                            .zIndex(3.3f)
                            .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmapManzana(recorrido, 40.0f, 0xFFFB0202, 2))));
                    markerRecorrido.setTag("CAPA");
                    markerRecorrido.setVisible(false);
                    gMarkerRecorrido.add(markerRecorrido);

                    Log.d("polygon1.getPoints()", String.valueOf(polygon1.getPoints()));

                    puntosZoom.addAll(polygon1.getPoints());

                    polygonPredios.add(contPredios, polygon1);
                    polygonBorderPredios.put(polygon1, polygon2);
                    contPredios++;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cargarDisperso() {
        if (!valoresDisperso.isEmpty()) {
            for (Map<String, Object> pos : valoresDisperso) {
                String upm = pos.get("upm").toString();
                String ciucom = pos.get("ciucom").toString();
                String geojson = pos.get("geojson").toString();
                Log.d("geojson", geojson);
                Log.d("ciucom", ciucom);
                Log.d("geojson", geojson);

                try {
                    JSONArray geoJsonData = new JSONArray(geojson).getJSONArray(0);
                    ArrayList<LatLng> latLngs = new ArrayList<>();

                    for (int i = 0; i < geoJsonData.length(); i++) {
                        JSONArray geoJsonConvert = geoJsonData.getJSONArray(i);
                        latLngs.add(new LatLng(Double.valueOf(geoJsonConvert.getString(1)), Double.valueOf(geoJsonConvert.getString(0))));
                    }

                    PolygonOptions pc = new PolygonOptions()
                            .clickable(false)
                            .strokeColor(Color.YELLOW)
                            .strokeWidth(8f)
                            .fillColor(this.getResources().getColor(R.color.color_blanco_transparente));
                    pc.addAll(latLngs);

                    Polygon polygon1 = mMap.addPolygon(pc);
                    polygon1.setZIndex(2f);
                    polygon1.setTag(pos);

                    PolygonOptions pcB = new PolygonOptions()
                            .clickable(true)
                            .strokeColor(Color.RED)
                            .strokeWidth(5f)
                            .fillColor(this.getResources().getColor(R.color.color_blanco_transparente));
                    pcB.addAll(latLngs);

                    Polygon polygon2 = mMap.addPolygon(pcB);
                    polygon2.setZIndex(2.1f);
                    polygon2.setTag(pos);

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (LatLng latLng : polygon1.getPoints()) {
                        builder.include(latLng);
                    }

                    LatLngBounds bounds = builder.build();

                    LatLng b2 = bounds.getCenter();

                    Marker markerManz = mMap.addMarker(new MarkerOptions()
                            .position(b2)
                            .draggable(false)
                            .anchor(0.5f, 0.5f)
                            .snippet("MANZANO")
                            .zIndex(2.2f)
                            .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmapManzana(upm, 40.0f, 0xFFFB0202, 2))));
                    markerManz.setTag("CAPA");
                    gMarkerDisperso.add(markerManz);
                    gMarkerDispersoPolygon.put(markerManz, polygon2);
                    Log.d("polygon1.getPoints()", String.valueOf(polygon1.getPoints()));

                    puntosZoom.addAll(polygon1.getPoints());

                    polygonDisperso.add(contDisperso, polygon1);
                    polygonBorderDisperso.put(polygon1, polygon2);
                    contDisperso++;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cargarCapaIne() {
//        capasActivas.add("1");

        /***CAPA INE OLD**/
        float index;
        Upm u = new Upm();
        gCodUpm = u.getCodigos(idUpm);

        for (String cod : gCodUpm) {
            String pathname = Parametros.DIR_CARTO + cod + ".mbtiles";
            Log.d("archivo_mbtiles", pathname);
//                TileProvider tileProvider1 = new ExpandedMBTilesTileProvider(new File(Parametros.DIR_RAIZ + "0201010100114.mbtiles"), 1024, 1024);
            if (new File(pathname).exists()) {
                Log.d("archivo", "existe");
                TileOverlay tileOverlay = mMap.addTileOverlay(ToolMaps.graficaMBTiles(pathname));
                tileOverlay.setZIndex(1);
                valoresOverlay.put(cod, tileOverlay);

            } else {
                Toast.makeText(this, "Descargue CARTOGRAFIA desde el menu principal", Toast.LENGTH_SHORT).show();
//                    toastMessage("Descargue CARTOGRAFIA desde el menu principal", null);
                Log.d("archivo", "Descargue CARTOGRAFIA desde el menu principal");
            }
        }
    }

    public void marcadorPerimetro(int d) {
        switch (d) {
            case 1:
                if(perimetro.isChecked()) {
                    if (!gMarkerPerimetro.isEmpty()) {
                        for (Marker m : gMarkerPerimetro) {
                            m.setVisible(true);
                        }
                    }
                }
                break;
            case 2:
                if (!gMarkerPerimetro.isEmpty()) {
                    for (Marker m : gMarkerPerimetro) {
                        m.setVisible(false);
                    }
                    break;
                }
        }

    }

    public void marcadorPredio(int d) {
        switch (d) {
            case 1:
                if(predio.isChecked()) {
                    if (!gMarkerPredio.isEmpty()) {
                        for (Marker m : gMarkerPredio) {
                            m.setVisible(true);
                        }
                    }
                }
                break;
            case 2:
                if (!gMarkerPredio.isEmpty()) {
                    for (Marker m : gMarkerPredio) {
                        m.setVisible(false);
                    }
                    break;
                }
        }

    }

    public void marcadorRecorrido(int d) {
        switch (d) {
            case 1:
                if(recorrido.isChecked()) {
                    if (!gMarkerRecorrido.isEmpty()) {
                        for (Marker m : gMarkerRecorrido) {
                            m.setVisible(true);
                        }
                    }
                }
                break;
            case 2:
                if (!gMarkerRecorrido.isEmpty()) {
                    for (Marker m : gMarkerRecorrido) {
                        m.setVisible(false);
                    }
                    break;
                }
        }

    }


    public void marcadorDisperso(int d) {
        switch (d) {
            case 1:
                if(disperso.isChecked()) {
                    if (!gMarkerDisperso.isEmpty()) {
                        for (Marker m : gMarkerDisperso) {
                            m.setVisible(true);
                        }
                    }
                }
                break;
            case 2:
                if (!gMarkerDisperso.isEmpty()) {
                    for (Marker m : gMarkerDisperso) {
                        m.setVisible(false);
                    }
                    break;
                }
        }
    }

    public void polygonPerimetro(int d) {
        switch (d) {
            case 1:
                if (!polygonPerimetros.isEmpty()) {
                    for (Polygon p : polygonPerimetros) {
                        p.setVisible(true);
                        polygonBorderPerimetros.get(p).setVisible(true);
                    }
                }
                break;
            case 2:
                if (!polygonPerimetros.isEmpty()) {
                    for (Polygon p : polygonPerimetros) {
                        p.setVisible(false);
                        polygonBorderPerimetros.get(p).setVisible(false);
                    }
                    break;
                }
        }
    }

    public void polygonPredio(int d) {
        switch (d) {
            case 1:
                if (!polygonPredios.isEmpty()) {
                    for (Polygon p : polygonPredios) {
                        p.setVisible(true);
                        polygonBorderPredios.get(p).setVisible(true);
                    }
                }
                break;
            case 2:
                if (!polygonPredios.isEmpty()) {
                    for (Polygon p : polygonPredios) {
                        p.setVisible(false);
                        polygonBorderPredios.get(p).setVisible(false);
                    }
                    break;
                }
        }
    }

    public void polygonDisperso(int d) {
        switch (d) {
            case 1:
                if (!polygonDisperso.isEmpty()) {
                    for (Polygon p : polygonDisperso) {
                        p.setVisible(true);
                        polygonBorderDisperso.get(p).setVisible(true);
                    }
                }
                break;
            case 2:
                if (!polygonDisperso.isEmpty()) {
                    for (Polygon p : polygonDisperso) {
                        p.setVisible(false);
                        polygonBorderDisperso.get(p).setVisible(false);
                    }
                    break;
                }
        }
    }

    public Bitmap textAsBitmapManzana(String text, float textSize, int textColor, int tipo) {
        Drawable drawable1;
        drawable1 = getResources().getDrawable(R.drawable.bg_manzana);
        Paint paint = new Paint();
        paint.setTextSize(textSize);


        paint.setTextAlign(Paint.Align.LEFT);

        int width = (int) (paint.measureText(text) + 5); // round
        float baseline = (int) (-paint.ascent() + 125); // ascent() is negative
        int height = (int) (baseline + paint.descent());

        Bitmap image = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);

        if(tipo == 1){
            paint.setStrokeWidth(textSize/5);
            paint.setStyle(Paint.Style.STROKE);
            Canvas canvas = new Canvas(image);
            canvas.drawText(text, 0, baseline, paint);
            paint.setColor(Color.WHITE);
        } else {
            paint.setShadowLayer(15, 0, 0, Color.YELLOW);
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(textColor);

        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);

        Drawable drawable2 = new BitmapDrawable(getResources(), image);
//        Drawable drawable1 = getResources().getDrawable(R.drawable.bg_default);

        Drawable[] layers = new Drawable[2];
        layers[0] = drawable1;
        layers[1] = drawable2;

        LayerDrawable layerDrawable = new LayerDrawable(layers);
        layerDrawable.setLayerInset(1, 0, 0, 0, 70);
        Bitmap bitmap;

        bitmap = Bitmap.createBitmap((width), (height),
                Bitmap.Config.ARGB_8888);

        Canvas can = new Canvas(bitmap);
        layerDrawable.setBounds(0, 0, width, height);
        layerDrawable.draw(can);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(this.getResources(),
                bitmap);
        bitmapDrawable.setBounds(0, 0, 25, 25);

        return bitmapDrawable.getBitmap().copy(
                Bitmap.Config.ARGB_8888, true);
    }

    private void areaZoom(ArrayList<LatLng> latLng) {

        double puntosX = 0f;
        double puntosY = 0f;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        int contador = 0;
        int numeroPuntos = latLng.size();
//        List<LatLng> puntos = poligonoSeleccionado.getPoints();

        Log.d("numeroPuntos", String.valueOf(numeroPuntos));

        for (int j = 0; j < numeroPuntos; j++) {
            Log.d("marcador_poligono" + j, String.valueOf(latLng.get(j)));

            puntosX = puntosX + latLng.get(j).latitude;
            puntosY = puntosY + latLng.get(j).longitude;
            builder.include(latLng.get(j));
        }

//        LatLng mainLocation = latLng;
//        LatLngBounds.Builder builder = new LatLngBounds.Builder();
//        builder.include(mainLocation);
        LatLngBounds bounds=new LatLngBounds(new LatLng(-17.239516, -64.644281),new LatLng(-17.239516, -64.644281));
        try {
            bounds = builder.build();
        }catch (Exception e){
            e.printStackTrace();

        }


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//            int width = metrics.widthPixels; // ancho absoluto en pixels
        int width = getResources().getDisplayMetrics().widthPixels;
        ; // ancho absoluto en pixels
        int height = metrics.heightPixels;

        int padding = ((width * 10) / 100); // offset from edges of the map
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                padding);
        mMap.animateCamera(cu);
    }

    public void mostrarMensaje(Map<String, Object> tag){
        if(tag != null){
            StringBuilder builderMensaje = new StringBuilder();
            Log.d("ciu_com", String.valueOf(tag.get("ciucom")));
            String ciu_com = tag.get("ciucom")==null?"S/D":tag.get("ciucom").toString();
            switch (tipo){
                case 1:
                    builderMensaje.append("<strong><span style=\"color:#1565C0;\">NUM_UPM: </span></strong>").append(tag.get("upm") + "<br/>");
                    builderMensaje.append("<strong><span style=\"color:#1565C0;\">ID_MANZ: </span></strong>").append(tag.get("id_manz") + "<br/>");
                    builderMensaje.append("<strong><span style=\"color:#1565C0;\">CIUDAD_COM: </span></strong>").append(ciu_com + "<br/>");
                    builderMensaje.append("<strong><span style=\"color:#1565C0;\">ORDEN_PRED: </span></strong>").append(tag.get("orden") + "<br/>");
                    builderMensaje.append("<strong><span style=\"color:#1565C0;\">RECORRIDO: </span></strong>").append(tag.get("recorrido") + "<br/>");
                    builderMensaje.append("<strong><span style=\"color:#1565C0;\">TIPO: </span></strong>").append(tag.get("tipo") + "<br/>");
                    break;
                case 2:
                    builderMensaje.append("<strong><span style=\"color:#1565C0;\">NUM_UPM: </span></strong>").append(tag.get("upm") + "<br/>");
                    builderMensaje.append("<strong><span style=\"color:#1565C0;\">CIUDAD_COM: </span></strong>").append(ciu_com + "<br/>");
                    builderMensaje.append("<strong><span style=\"color:#1565C0;\">ID_COM: </span></strong>").append(tag.get("id_com") + "<br/>");
                    break;
            }
            content_mensaje.setVisibility(View.VISIBLE);
            mensaje.setText(Html.fromHtml(builderMensaje.toString()));
        } else {
            content_mensaje.setVisibility(View.GONE);
            mensaje.setText("");
        }

    }
}