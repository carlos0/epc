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
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mapsforge.core.model.LatLong;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.Upm;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityNavigator;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import bo.gob.ine.naci.epc.herramientas.ToolMaps;

public final class MapActivity2 extends ActionBarActivityNavigator implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnCameraIdleListener,
        GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveCanceledListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnPolygonClickListener,
        GoogleMap.OnPolylineClickListener {
    private GoogleMap mMap;

    private int idUpm;
    private int tipoMapa;
    private String codUpm;
    private ArrayList<String> gCodUpm;

    private ArrayList<Map<String, Object>> valoresComunidad = new ArrayList<>();
    private ArrayList<Map<String, Object>> valoresPredio = new ArrayList<>();
    private ArrayList<Map<String, Object>> valoresManzana = new ArrayList<>();
    private ArrayList<Map<String, Object>> valoresSegmento = new ArrayList<>();
    private ArrayList<Map<String, Object>> valoresSegmentoD = new ArrayList<>();

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

    private ArrayList<Marker> gMarkerComunidad = new ArrayList<>();
    private ArrayList<Marker> gMarkerPredio = new ArrayList<>();
    private ArrayList<Polyline> gPolylineSegmento = new ArrayList<>();

    private ArrayList<Polygon> gPolygonSegmentoD = new ArrayList<>();
    private ArrayList<Marker> gMarkerSegmentoD = new ArrayList<>();

    private ArrayList<Polygon> gPolygonManzana = new ArrayList<>();
    private ArrayList<Marker> gMarkerManzana = new ArrayList<>();

    private ArrayList<LatLng> puntosZoom = new ArrayList<>();

    private Map<String, TileOverlay> valoresOverlay = new HashMap<>();

    private MaterialButton map, google, ubicacion, comunidad, predio, manzana, segmento, segmentoD;

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

    private int idAsignacion;
    private int correlativo;
    private LatLng punto;
    private Map<String, String> datos;
    private boolean todo = false;

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
        /**TIPO MAPA**/
        /**1. TODAS LAS CARGAS**/
        /**2. UNA CARGA**/
        /**3. UN PREDIO**/
        tipoMapa = bundle.getInt("tipo");
        idAsignacion = bundle.getInt("idAsignacion");
        correlativo = bundle.getInt("correlativo");

        toogleButtons = findViewById(R.id.fabMenuMain2);

        comunidad = findViewById(R.id.fabComunidad);
        predio = findViewById(R.id.fabPredio);
        manzana = findViewById(R.id.fabManzana);
        segmento = findViewById(R.id.fabSegmento);
        segmentoD = findViewById(R.id.fabSegmentoD);

        map = findViewById(R.id.fabMap);
        google = findViewById(R.id.fabGoogle);
        ubicacion = findViewById(R.id.fabUbicacion);

        mensaje = findViewById(R.id.mensaje);
        content_mensaje = findViewById(R.id.content_mensaje);

        switch (tipoMapa) {
            case 1:
                segmento.setText("SEGMENTO_AMANZ");
                segmentoD.setText("SEGMENTO_DISP");
                todo = true;
                break;
            case 2:
                codUpm = Upm.getCodUpm(idUpm);
                switch (codUpm){
                    case "1":
                        segmentoD.setVisibility(View.GONE);
                        comunidad.setVisibility(View.GONE);
                        break;
                    case "2":
                        segmento.setVisibility(View.GONE);
                        manzana.setVisibility(View.GONE);
                        break;
                }
                todo = false;
                break;
            case 3:
                codUpm = Upm.getCodUpm(idUpm);
                switch (codUpm){
                    case "1":
                        segmentoD.setVisibility(View.GONE);
                        comunidad.setVisibility(View.GONE);
                        break;
                    case "2":
                        segmento.setVisibility(View.GONE);
                        manzana.setVisibility(View.GONE);
                        break;
                }
                todo = false;
                datos = Encuesta.getPunto(idAsignacion, correlativo);
                punto = new LatLng(Double.parseDouble(datos.get("latitud")), Double.parseDouble(datos.get("longitud")));
                break;
        }

        Upm upm = new Upm();

        valoresComunidad = upm.obtenerComunidad(idUpm, todo);
        valoresPredio = upm.obtenerPredio(idUpm, todo);
        valoresManzana = upm.obtenerManzana(idUpm, todo);
        valoresSegmento = upm.obtenerSegmento(idUpm, todo);
        valoresSegmentoD = upm.obtenerSegmentoD(idUpm, todo);

//        if (codUpm.endsWith("D")) {
//            tipo = 2;
//            disperso.setVisibility(View.VISIBLE);
//        } else {
//            tipo = 1;
//            perimetro.setVisibility(View.VISIBLE);
//            predio.setVisibility(View.VISIBLE);
//            recorrido.setVisibility(View.VISIBLE);
////            map.setVisibility(View.VISIBLE);
//        }


        comunidad.setChecked(true);
        comunidad.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
        comunidad.setTextColor(getResources().getColor(R.color.color_list));
        predio.setChecked(true);
        predio.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
        predio.setTextColor(getResources().getColor(R.color.color_list));
        manzana.setChecked(true);
        manzana.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
        manzana.setTextColor(getResources().getColor(R.color.color_list));
        segmento.setChecked(true);
        segmento.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
        segmento.setTextColor(getResources().getColor(R.color.color_list));
        segmentoD.setChecked(true);
        segmentoD.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
        segmentoD.setTextColor(getResources().getColor(R.color.color_list));
        map.setChecked(false);
        google.setChecked(false);
        ubicacion.setChecked(false);

        comunidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comunidad.isChecked()) {
                    comunidad.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
                    comunidad.setTextColor(getResources().getColor(R.color.color_list));
                    marcadorComunidad(1);
                } else {
                    comunidad.setBackgroundColor(getResources().getColor(R.color.color_blanco_button));
                    comunidad.setTextColor(getResources().getColor(R.color.colorPrimary));
                    marcadorComunidad(2);
                }
            }
        });

        predio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (predio.isChecked()) {
                    predio.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
                    predio.setTextColor(getResources().getColor(R.color.color_list));
                    marcadorPredio(1);
                } else {
                    predio.setBackgroundColor(getResources().getColor(R.color.color_blanco_button));
                    predio.setTextColor(getResources().getColor(R.color.colorPrimary));
                    marcadorPredio(2);
                }
            }
        });

        manzana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manzana.isChecked()) {
                    manzana.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
                    manzana.setTextColor(getResources().getColor(R.color.color_list));
                    marcadorManzana(1);
                    polygonManzana(1);
                } else {
                    manzana.setBackgroundColor(getResources().getColor(R.color.color_blanco_button));
                    manzana.setTextColor(getResources().getColor(R.color.colorPrimary));
                    marcadorManzana(2);
                    polygonManzana(2);
                }
            }
        });

        segmento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (segmento.isChecked()) {
                    segmento.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
                    segmento.setTextColor(getResources().getColor(R.color.color_list));
                    polilineaSegmento(1);
                } else {
                    segmento.setBackgroundColor(getResources().getColor(R.color.color_blanco_button));
                    segmento.setTextColor(getResources().getColor(R.color.colorPrimary));
                    polilineaSegmento(2);
                }
            }
        });

        segmentoD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (segmentoD.isChecked()) {
                    segmentoD.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
                    segmentoD.setTextColor(getResources().getColor(R.color.color_list));
                    marcadorSegmentoD(1);
                    polygonSegmentoD(1);
                } else {
                    segmentoD.setBackgroundColor(getResources().getColor(R.color.color_blanco_button));
                    segmentoD.setTextColor(getResources().getColor(R.color.colorPrimary));
                    marcadorSegmentoD(2);
                    polygonSegmentoD(2);
                }
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (google.isChecked()) {
                    google.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
                    google.setTextColor(getResources().getColor(R.color.color_list));
                    google.setIconTintResource(R.color.color_list);
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                } else {
                    google.setBackgroundColor(getResources().getColor(R.color.color_blanco_button));
                    google.setTextColor(getResources().getColor(R.color.colorPrimary));
                    google.setIconTintResource(R.color.colorPrimary);
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
        map.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    map.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
                    map.setTextColor(getResources().getColor(R.color.color_list));
                    map.setIconTintResource(R.color.color_list);
                    break;
                case MotionEvent.ACTION_UP:
                    map.setBackgroundColor(getResources().getColor(R.color.color_blanco_button));
                    map.setTextColor(getResources().getColor(R.color.colorPrimary));
                    map.setIconTintResource(R.color.colorPrimary);
                    break;
            }
            return false;
        });

        ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ubicacion.setChecked(true);
                obtenerUbicacionActual();
            }
        });
        ubicacion.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ubicacion.setBackgroundColor(getResources().getColor(R.color.colorPrimaryOpaco));
                    ubicacion.setTextColor(getResources().getColor(R.color.color_list));
                    ubicacion.setIconTintResource(R.color.color_list);
                    break;
                case MotionEvent.ACTION_UP:
                    ubicacion.setBackgroundColor(getResources().getColor(R.color.color_blanco_button));
                    ubicacion.setTextColor(getResources().getColor(R.color.colorPrimary));
                    ubicacion.setIconTintResource(R.color.colorPrimary);
                    break;
            }
            return false;
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

        cargarComunidad();
        cargarPredio();
        cargarManzana();
        cargarSegmento();
        cargarSegmentoD();

        if(tipoMapa == 3)
            cargarPunto();

        if (!puntosZoom.isEmpty()) {
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    areaZoom(puntosZoom);
//                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
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
//        if(gMarkerPredioPolygon.containsKey(marker) || gMarkerDispersoPolygon.containsKey(marker)){
//            ArrayList<LatLng> poligonZoom = new ArrayList<>();
//            Polygon polygon = null;
//            switch (tipo){
//                case 1:
//                    polygon = gMarkerPredioPolygon.get(marker);
//                    break;
//                case 2:
//                    polygon = gMarkerDispersoPolygon.get(marker);
//                    break;
//            }
//            if (poligonoSeleccionado != null) {
//                Log.d("polygon", poligonoSeleccionado.getId());
//                if (!poligonoSeleccionado.equals(polygon)) {
//                    poligonoSeleccionado.setStrokeColor(colorBordeSegmento);
//                    poligonoSeleccionado.setZIndex(3.1f);
//                    polygon.setStrokeColor(colorBordeSeleccion);
//                    polygon.setZIndex(3.2f);
//                    poligonoSeleccionado = polygon;
//                    mostrarMensaje((Map<String, Object>) poligonoSeleccionado.getTag());
//                    poligonZoom.addAll(poligonoSeleccionado.getPoints());
//                    areaZoom(poligonZoom);
//                } else {
//                    poligonoSeleccionado = null;
//                    polygon.setStrokeColor(colorBordeSegmento);
//                    polygon.setZIndex(3.1f);
//                    mostrarMensaje(null);
//                }
//            } else {
//                polygon.setStrokeColor(colorBordeSeleccion);
//                polygon.setZIndex(3.2f);
//                poligonoSeleccionado = polygon;
//                mostrarMensaje((Map<String, Object>) poligonoSeleccionado.getTag());
//                poligonZoom.addAll(poligonoSeleccionado.getPoints());
//                areaZoom(poligonZoom);
//            }
//        }
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
            marcadorPredio(2);
        } else {
            marcadorPredio(1);
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

    private void cargarPunto() {
        Log.d("MAPS", "*-------------cargarPunto-------------*");
        Log.d("MAPS", String.valueOf(punto));
        if (punto.latitude != 0 || punto.longitude != 0) {

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(punto)
                            .draggable(false)
                            .anchor(0.5f, 0.5f)
                            .icon(bitmapDescriptorFromVector(this, R.drawable.ic_home_lv)));
                    marker.setTag("PUNTO");
                    marker.setZIndex(10);

                    Marker markerRecorrido = mMap.addMarker(new MarkerOptions()
                            .position(punto)
                            .draggable(false)
                            .anchor(0.5f, 0.8f)
                            .snippet("VIVIENDA")
                            .zIndex(3.3f)
                            .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmap(datos.get("codigo"), 50.0f, Color.RED, Color.WHITE))));
                    markerRecorrido.setTag("CAPA");

                    puntosZoom.add(punto);

        }
    }

    private void cargarSegmentoD() {
        Log.d("MAPS", "*-------------cargarSegmentoD-------------*");
        Log.d("MAPS", String.valueOf(valoresSegmentoD));
        if (!valoresSegmentoD.isEmpty()) {
            for (Map<String, Object> pos : valoresSegmentoD) {
                String seg_unico = pos.get("seg_unico").toString();
                String segmento = pos.get("segmento").toString();
                String geo = pos.get("geo").toString();
                try {
                    JSONObject jsonObject = new JSONObject(geo);
                    JSONArray coordinatesArray = jsonObject.getJSONArray("coordinates");

                    JSONArray geoJsonData2 = coordinatesArray.getJSONArray(0);
                    JSONArray geoJsonData = geoJsonData2.getJSONArray(0);

                    ArrayList<LatLng> latLngs = new ArrayList<>();

                    for (int i = 0; i < geoJsonData.length(); i++) {
                        JSONArray geoJsonConvert = geoJsonData.getJSONArray(i);
                        latLngs.add(new LatLng(Double.valueOf(geoJsonConvert.getString(1)), Double.valueOf(geoJsonConvert.getString(0))));
                    }

                    PolygonOptions pc = new PolygonOptions()
                            .clickable(false)
                            .strokeColor(Color.YELLOW)
                            .strokeWidth(20f)
                            .fillColor(this.getResources().getColor(R.color.color_transparent));
                    pc.addAll(latLngs);

                    Polygon polygon1 = mMap.addPolygon(pc);
                    polygon1.setZIndex(3f);
                    polygon1.setTag(pos);

                    PolygonOptions pcB = new PolygonOptions()
                            .clickable(true)
                            .strokeColor(Color.RED)
                            .strokeWidth(8f)
                            .fillColor(this.getResources().getColor(R.color.color_transparent));
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
                            .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmap(segmento, 40.0f, Color.RED, Color.WHITE))));
                    markerManz.setTag("CAPA");

                    puntosZoom.addAll(polygon1.getPoints());

                    gPolygonSegmentoD.add(polygon1);
                    gPolygonSegmentoD.add(polygon2);
                    gMarkerSegmentoD.add(markerManz);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void cargarSegmento() {
        Log.d("MAPS", "*-------------cargarSegmento-------------*");
        Log.d("MAPS", String.valueOf(valoresSegmento));
        if (!valoresSegmento.isEmpty()) {
            for (Map<String, Object> pos : valoresSegmento) {
                String seg_unico = pos.get("seg_unico").toString();
                String geo = pos.get("geo").toString();

                try {
                    JSONObject jsonObject = new JSONObject(geo);
                    JSONArray coordinatesArray = jsonObject.getJSONArray("coordinates");

                    JSONArray geoJsonData = coordinatesArray.getJSONArray(0);

                    ArrayList<LatLng> latLngs = new ArrayList<>();

                    for (int i = 0; i < geoJsonData.length(); i++) {
                        JSONArray geoJsonConvert = geoJsonData.getJSONArray(i);
                        latLngs.add(new LatLng(Double.valueOf(geoJsonConvert.getString(1)), Double.valueOf(geoJsonConvert.getString(0))));
                    }

                    PolylineOptions pLinea = new PolylineOptions()
                            .clickable(false)
                            .width(8)
                            .color(Color.RED)
                            .zIndex(4.1f);
//                            .jointType(JointType.ROUND)
//                            .endCap(new RoundCap());
                    pLinea.addAll(latLngs);
                    Polyline mapLinea = mMap.addPolyline(pLinea);

                    PolylineOptions pLineaS = new PolylineOptions()
                            .clickable(false)
                            .width(20)
                            .color(Color.YELLOW)
                            .zIndex(4f);
//                            .jointType(JointType.ROUND)
//                            .endCap(new RoundCap());
                    pLineaS.addAll(latLngs);
                    Polyline mapLineaS = mMap.addPolyline(pLineaS);

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    for (LatLng latLng : pLinea.getPoints()) {
                        builder.include(latLng);
                    }

                    puntosZoom.addAll(pLinea.getPoints());

                    gPolylineSegmento.add(mapLinea);
                    gPolylineSegmento.add(mapLineaS);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void cargarManzana() {
        Log.d("MAPS", "*-------------cargarManzana-------------*");
        Log.d("MAPS", String.valueOf(valoresManzana));
        if (!valoresManzana.isEmpty()) {
            for (Map<String, Object> pos : valoresManzana) {
                String id_manz = pos.get("id_manz").toString();
                String orden_manz = pos.get("orden_manz").toString();
                String seg_unico = pos.get("seg_unico").toString();
                String geo = pos.get("geo").toString();

                try {
                    JSONObject jsonObject = new JSONObject(geo);
                    JSONArray coordinatesArray = jsonObject.getJSONArray("coordinates");

                    JSONArray geoJsonData2 = coordinatesArray.getJSONArray(0);
                    JSONArray geoJsonData = geoJsonData2.getJSONArray(0);

                    ArrayList<LatLng> latLngs = new ArrayList<>();

                    for (int i = 0; i < geoJsonData.length(); i++) {
                        JSONArray geoJsonConvert = geoJsonData.getJSONArray(i);
                        latLngs.add(new LatLng(Double.valueOf(geoJsonConvert.getString(1)), Double.valueOf(geoJsonConvert.getString(0))));
                    }

                    PolygonOptions pc = new PolygonOptions()
                            .clickable(false)
                            .strokeColor(Color.RED)
                            .strokeWidth(5f)
                            .fillColor(this.getResources().getColor(R.color.color_blanco_transparente));
                    pc.addAll(latLngs);

                    Polygon polygon1 = mMap.addPolygon(pc);
                    polygon1.setZIndex(3f);
                    polygon1.setTag(pos);

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
                            .snippet("MANZANA")
                            .zIndex(3.2f)
                            .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmap(orden_manz, 40.0f, Color.BLACK, Color.WHITE))));
                    markerManz.setTag("CAPA");

                    puntosZoom.addAll(polygon1.getPoints());

                    gPolygonManzana.add(polygon1);
                    gMarkerManzana.add(markerManz);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void cargarPredio() {
        Log.d("MAPS", "*-------------cargarPredio-------------*");
        Log.d("MAPS", String.valueOf(valoresPredio));
        if (!valoresPredio.isEmpty()) {

            for (Map<String, Object> pos : valoresPredio) {
                String seg_unico = pos.get("seg_unico").toString();
                String orden = pos.get("orden").toString();
                int cod_if = Integer.parseInt(pos.get("cod_if").toString());
                String tipo = pos.get("tipo").toString();
                String ciu_com = pos.get("ciu_com").toString();
                String geo = pos.get("geo").toString();

                try {
                    JSONObject jsonObject = new JSONObject(geo);
                    JSONArray coordinatesArray = jsonObject.getJSONArray("coordinates");

                    double longitude = coordinatesArray.getDouble(0);
                    double latitude = coordinatesArray.getDouble(1);

                    LatLng latLng = new LatLng(latitude, longitude);
                    BitmapDescriptor colorPunto;
                    switch (cod_if){
                        case 1:
                        case 4:
                             colorPunto = bitmapDescriptorFromVector(this, R.drawable.marker_predio_inicial);
                            break;
                        case 2:
                            colorPunto = bitmapDescriptorFromVector(this, R.drawable.marker_predio_final);
                            break;
                        case 0:
                        default:
                            colorPunto = bitmapDescriptorFromVector(this, R.drawable.marker_predio);
                            break;
                    }

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .draggable(false)
                            .anchor(0.5f, 0.5f)
                            .icon(colorPunto));
                    marker.setTag("Total");
                    marker.setZIndex(10);

                    BitmapDescriptor colorTexto = null;
                    switch (tipo){
                        case "1":
                            colorTexto = BitmapDescriptorFactory.fromBitmap(textAsBitmap(orden, 50.0f, Color.BLACK, Color.YELLOW));
                            break;
                        case "2":
                            colorTexto = BitmapDescriptorFactory.fromBitmap(textAsBitmap(orden, 50.0f, getResources().getColor(R.color.s_marker_predio_disperso), Color.WHITE));

                            Marker markerRecorrido2 = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .draggable(false)
                                    .anchor(0.6f, 0.35f)
                                    .snippet("MANZANO")
                                    .zIndex(3.3f)
                                    .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmap(ciu_com, 50.0f, getResources().getColor(R.color.s_marker_predio_disperso), Color.WHITE))));
                            markerRecorrido2.setTag("CAPA");
                            gMarkerPredio.add(markerRecorrido2);
                            break;
                    }

                    Marker markerRecorrido = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .draggable(false)
                            .anchor(0.5f, 0.8f)
                            .snippet("MANZANO")
                            .zIndex(3.3f)
                            .icon(colorTexto));
                    markerRecorrido.setTag("CAPA");

                    gMarkerPredio.add(marker);
                    gMarkerPredio.add(markerRecorrido);

                    puntosZoom.add(latLng);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void cargarComunidad() {
        Log.d("MAPS", "*-------------cargarComunidad-------------*");
        Log.d("MAPS", String.valueOf(valoresComunidad));
        if (!valoresComunidad.isEmpty()) {
            BitmapDescriptor colorPunto;
            colorPunto = bitmapDescriptorFromVector(this, R.drawable.marker_comunidad);

            for (Map<String, Object> pos : valoresComunidad) {
                String ciu_com = pos.get("ciu_com").toString();
                String id_com = pos.get("id_com").toString();
                String seg_unico = pos.get("seg_unico").toString();
                String geo = pos.get("geo").toString();

                try {
                    JSONObject jsonObject = new JSONObject(geo);
                    JSONArray coordinatesArray = jsonObject.getJSONArray("coordinates");

                    double longitude = coordinatesArray.getDouble(0);
                    double latitude = coordinatesArray.getDouble(1);

                    LatLng latLng = new LatLng(latitude, longitude);

                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .draggable(false)
                            .anchor(0.5f, 0.5f)
                            .icon(colorPunto));
                    marker.setTag("Total");
                    marker.setZIndex(10);

                    Marker markerRecorrido = mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .draggable(false)
                            .anchor(0.6f, 0.35f)
                            .snippet("MANZANO")
                            .zIndex(10)
                            .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmap(ciu_com, 60.0f, getResources().getColor(R.color.marker_comunidad), Color.WHITE))));
                    markerRecorrido.setTag("COMUNIDAD");

                    puntosZoom.add(latLng);

                    gMarkerComunidad.add(marker);
                    gMarkerComunidad.add(markerRecorrido);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int icono) {
        Drawable background = ContextCompat.getDrawable(context, icono);
//        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());

        background.setBounds(0, 0, 50, 50);
        IconGenerator factory = new IconGenerator(context);
        factory.setBackground(null);
        factory.setTextAppearance(R.style.textGenerator1);

        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
//        d.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

//    private void cargarPerimetros() {
//        if (!valoresPerimetro.isEmpty()) {
//            for (Map<String, Object> pos : valoresPerimetro) {
//                String upm = pos.get("upm").toString();
//                String ciucom = pos.get("ciucom").toString();
//                String geojson = pos.get("geojson").toString();
//                Log.d("geojson", geojson);
//                Log.d("ciucom", ciucom);
//                Log.d("geojson", geojson);
//
//                try {
//                    JSONArray geoJsonData = new JSONArray(geojson).getJSONArray(0);
//                    ArrayList<LatLng> latLngs = new ArrayList<>();
//
//                    for (int i = 0; i < geoJsonData.length(); i++) {
//                        JSONArray geoJsonConvert = geoJsonData.getJSONArray(i);
//                        latLngs.add(new LatLng(Double.valueOf(geoJsonConvert.getString(1)), Double.valueOf(geoJsonConvert.getString(0))));
//                    }
//
//                    PolygonOptions pc = new PolygonOptions()
//                            .clickable(false)
//                            .strokeColor(Color.YELLOW)
//                            .strokeWidth(8f)
//                            .fillColor(this.getResources().getColor(R.color.color_blanco_transparente));
//                    pc.addAll(latLngs);
//
//                    PolygonOptions pcB = new PolygonOptions()
//                            .clickable(false)
//                            .strokeColor(Color.BLACK)
//                            .strokeWidth(4f)
//                            .fillColor(this.getResources().getColor(R.color.color_blanco_transparente));
//                    pcB.addAll(latLngs);
//                    pcB.strokePattern(PATTERN_POLYGON_ALPHA);
//
//                    Polygon polygon1 = mMap.addPolygon(pc);
//                    polygon1.setZIndex(2f);
//                    polygon1.setTag(upm);
//
//                    Polygon polygon2 = mMap.addPolygon(pcB);
//                    polygon2.setZIndex(2.1f);
//                    polygon2.setTag(upm);
//
//                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//                    for (LatLng latLng : polygon1.getPoints()) {
//                        builder.include(latLng);
//                    }
//
//                    LatLngBounds bounds = builder.build();
//
//                    LatLng b2 = bounds.getCenter();
//
//                    Marker markerManz = mMap.addMarker(new MarkerOptions()
//                            .position(b2)
//                            .draggable(false)
//                            .anchor(0.5f, 0.5f)
//                            .snippet("MANZANO")
//                            .zIndex(2.2f)
//                            .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmapManzana(upm, 40.0f, 0xFF44F105, 1))));
//                    markerManz.setTag("CAPA");
//                    gMarkerPerimetro.add(markerManz);
//                    Log.d("polygon1.getPoints()", String.valueOf(polygon1.getPoints()));
//
//                    puntosZoom.addAll(polygon1.getPoints());
//
//                    polygonPerimetros.add(contPerimetros, polygon2);
//                    polygonBorderPerimetros.put(polygon2, polygon1);
//                    contPerimetros++;
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private void cargarPredios() {
//        if (!valoresPredio.isEmpty()) {
//            for (Map<String, Object> pos : valoresPredio) {
//                String upm = pos.get("upm").toString();
//                String orden = pos.get("orden").toString();
//                String recorrido = pos.get("recorrido").toString();
//                String geojson = pos.get("geojson").toString();
//                String tipo = pos.get("tipo").toString();
//
//                Log.d("upm", upm);
//                Log.d("orden", orden);
//                Log.d("recorrido", recorrido);
//                Log.d("geojson", geojson);
//
//                try {
//                    JSONArray geoJsonData = new JSONArray(geojson).getJSONArray(0);
//                    ArrayList<LatLng> latLngs = new ArrayList<>();
//
//                    for (int i = 0; i < geoJsonData.length(); i++) {
//                        JSONArray geoJsonConvert = geoJsonData.getJSONArray(i);
//                        latLngs.add(new LatLng(Double.valueOf(geoJsonConvert.getString(1)), Double.valueOf(geoJsonConvert.getString(0))));
//                    }
//
//                    PolygonOptions pc = new PolygonOptions()
//                            .clickable(false)
//                            .strokeColor(Color.YELLOW)
//                            .strokeWidth(8f)
//                            .fillColor(this.getResources().getColor(R.color.color_blanco_transparente));
//                    pc.addAll(latLngs);
//
//                    Polygon polygon1 = mMap.addPolygon(pc);
//                    polygon1.setZIndex(3f);
//                    polygon1.setTag(pos);
//
//                    PolygonOptions pcB = new PolygonOptions()
//                            .clickable(true)
//                            .strokeColor(Color.RED)
//                            .strokeWidth(5f)
//                            .fillColor(this.getResources().getColor(R.color.color_blanco_transparente));
//                    pcB.addAll(latLngs);
//
//                    Polygon polygon2 = mMap.addPolygon(pcB);
//                    polygon2.setZIndex(3.1f);
//                    polygon2.setTag(pos);
//
//                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//                    for (LatLng latLng : polygon1.getPoints()) {
//                        builder.include(latLng);
//                    }
//
//                    LatLngBounds bounds = builder.build();
//
//                    LatLng b2 = bounds.getCenter();
//
//                    Marker markerManz = mMap.addMarker(new MarkerOptions()
//                            .position(b2)
//                            .draggable(false)
//                            .anchor(0.8f, 0.5f)
//                            .snippet("MANZANO")
//                            .zIndex(3.2f)
//                            .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmapManzana(orden, 40.0f, 0xFFFB0202, 2))));
//                    markerManz.setTag("CAPA");
//                    gMarkerPredio.add(markerManz);
//                    gMarkerPredioPolygon.put(markerManz, polygon2);
//                    Log.d("polygon1.getPoints()", String.valueOf(polygon1.getPoints()));
//
//                    Marker markerRecorrido = mMap.addMarker(new MarkerOptions()
//                            .position(b2)
//                            .draggable(false)
//                            .anchor(0.7f, 0.35f)
//                            .snippet("MANZANO")
//                            .zIndex(3.3f)
//                            .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmapManzana(recorrido, 40.0f, 0xFFFB0202, 2))));
//                    markerRecorrido.setTag("CAPA");
//                    markerRecorrido.setVisible(false);
//                    gMarkerRecorrido.add(markerRecorrido);
//
//                    Log.d("polygon1.getPoints()", String.valueOf(polygon1.getPoints()));
//
//                    puntosZoom.addAll(polygon1.getPoints());
//
//                    polygonPredios.add(contPredios, polygon1);
//                    polygonBorderPredios.put(polygon1, polygon2);
//                    contPredios++;
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private void cargarDisperso() {
//        if (!valoresDisperso.isEmpty()) {
//            for (Map<String, Object> pos : valoresDisperso) {
//                String upm = pos.get("upm").toString();
//                String ciucom = pos.get("ciucom").toString();
//                String geojson = pos.get("geojson").toString();
//                Log.d("geojson", geojson);
//                Log.d("ciucom", ciucom);
//                Log.d("geojson", geojson);
//
//                try {
//                    JSONArray geoJsonData = new JSONArray(geojson).getJSONArray(0);
//                    ArrayList<LatLng> latLngs = new ArrayList<>();
//
//                    for (int i = 0; i < geoJsonData.length(); i++) {
//                        JSONArray geoJsonConvert = geoJsonData.getJSONArray(i);
//                        latLngs.add(new LatLng(Double.valueOf(geoJsonConvert.getString(1)), Double.valueOf(geoJsonConvert.getString(0))));
//                    }
//
//                    PolygonOptions pc = new PolygonOptions()
//                            .clickable(false)
//                            .strokeColor(Color.YELLOW)
//                            .strokeWidth(8f)
//                            .fillColor(this.getResources().getColor(R.color.color_blanco_transparente));
//                    pc.addAll(latLngs);
//
//                    Polygon polygon1 = mMap.addPolygon(pc);
//                    polygon1.setZIndex(2f);
//                    polygon1.setTag(pos);
//
//                    PolygonOptions pcB = new PolygonOptions()
//                            .clickable(true)
//                            .strokeColor(Color.RED)
//                            .strokeWidth(5f)
//                            .fillColor(this.getResources().getColor(R.color.color_blanco_transparente));
//                    pcB.addAll(latLngs);
//
//                    Polygon polygon2 = mMap.addPolygon(pcB);
//                    polygon2.setZIndex(2.1f);
//                    polygon2.setTag(pos);
//
//                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
//
//                    for (LatLng latLng : polygon1.getPoints()) {
//                        builder.include(latLng);
//                    }
//
//                    LatLngBounds bounds = builder.build();
//
//                    LatLng b2 = bounds.getCenter();
//
//                    Marker markerManz = mMap.addMarker(new MarkerOptions()
//                            .position(b2)
//                            .draggable(false)
//                            .anchor(0.5f, 0.5f)
//                            .snippet("MANZANO")
//                            .zIndex(2.2f)
//                            .icon(BitmapDescriptorFactory.fromBitmap(textAsBitmapManzana(upm, 40.0f, 0xFFFB0202, 2))));
//                    markerManz.setTag("CAPA");
//                    gMarkerDisperso.add(markerManz);
//                    gMarkerDispersoPolygon.put(markerManz, polygon2);
//                    Log.d("polygon1.getPoints()", String.valueOf(polygon1.getPoints()));
//
//                    puntosZoom.addAll(polygon1.getPoints());
//
//                    polygonDisperso.add(contDisperso, polygon1);
//                    polygonBorderDisperso.put(polygon1, polygon2);
//                    contDisperso++;
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

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

    public void marcadorComunidad(int d) {
        switch (d) {
            case 1:
                if(comunidad.isChecked()) {
                    if (!gMarkerComunidad.isEmpty()) {
                        for (Marker m : gMarkerComunidad) {
                            m.setVisible(true);
                        }
                    }
                }
                break;
            case 2:
                if (!gMarkerComunidad.isEmpty()) {
                    for (Marker m : gMarkerComunidad) {
                        m.setVisible(false);
                    }
                    break;
                }
        }

    }

    public void marcadorPredio(int d) {
        switch (d) {
            case 1:
//                if(predio.isChecked()) {
                    if (!gMarkerPredio.isEmpty()) {
                        for (Marker m : gMarkerPredio) {
                            m.setVisible(true);
                        }
                    }
//                }
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

    public void polilineaSegmento(int d) {
        switch (d) {
            case 1:
//                if(disperso.isChecked()) {
                if (!gPolylineSegmento.isEmpty()) {
                    for (Polyline m : gPolylineSegmento) {
                        m.setVisible(true);
                    }
                }
//                }
                break;
            case 2:
                if (!gPolylineSegmento.isEmpty()) {
                    for (Polyline m : gPolylineSegmento) {
                        m.setVisible(false);
                    }
                    break;
                }
        }
    }

    public void marcadorSegmentoD(int d) {
        switch (d) {
            case 1:
//                if(recorrido.isChecked()) {
                if (!gMarkerSegmentoD.isEmpty()) {
                    for (Marker m : gMarkerSegmentoD) {
                        m.setVisible(true);
                    }
                }
//                }
                break;
            case 2:
                if (!gMarkerSegmentoD.isEmpty()) {
                    for (Marker m : gMarkerSegmentoD) {
                        m.setVisible(false);
                    }
                    break;
                }
        }
    }

    public void polygonSegmentoD(int d) {
        switch (d) {
            case 1:
                if (!gPolygonSegmentoD.isEmpty()) {
                    for (Polygon p : gPolygonSegmentoD) {
                        p.setVisible(true);
                    }
                }
                break;
            case 2:
                if (!gPolygonSegmentoD.isEmpty()) {
                    for (Polygon p : gPolygonSegmentoD) {
                        p.setVisible(false);
                    }
                    break;
                }
        }
    }

    public void marcadorManzana(int d) {
        switch (d) {
            case 1:
//                if(recorrido.isChecked()) {
                    if (!gMarkerManzana.isEmpty()) {
                        for (Marker m : gMarkerManzana) {
                            m.setVisible(true);
                        }
                    }
//                }
                break;
            case 2:
                if (!gMarkerManzana.isEmpty()) {
                    for (Marker m : gMarkerManzana) {
                        m.setVisible(false);
                    }
                    break;
                }
        }
    }

    public void polygonManzana(int d) {
        switch (d) {
            case 1:
                if (!gPolygonManzana.isEmpty()) {
                    for (Polygon p : gPolygonManzana) {
                        p.setVisible(true);
                    }
                }
                break;
            case 2:
                if (!gPolygonManzana.isEmpty()) {
                    for (Polygon p : gPolygonManzana) {
                        p.setVisible(false);
                    }
                    break;
                }
        }
    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor, int sombraText) {
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

        //SOMBRA
        paint.setStrokeWidth(textSize/5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(sombraText);
        Canvas canvas1 = new Canvas(image);
        canvas1.drawText(text, 0, baseline, paint);

        //TEXT
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

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {
        Toast.makeText(this, polyline.getTag().toString(), Toast.LENGTH_LONG).show();
    }
}