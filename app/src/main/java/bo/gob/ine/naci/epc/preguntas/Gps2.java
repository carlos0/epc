package bo.gob.ine.naci.epc.preguntas;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import org.mapsforge.map.util.MapViewProjection;

import java.io.File;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityMessage;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;


/**
 * Created by INE.
 */
public class Gps2 extends PreguntaView {
    protected Context context;
    protected double latitude;
    protected double longitude;
    protected double altitude;
    protected double accuracy;
    protected TextView coordenadaTextView;
    protected Button editarButton;
    protected Button centrarButton;
    protected Button geoposButton;

    private MapView mapView;
    private Marker marker;
    private boolean GPSactivo;
    private boolean flag;

    public Gps2(final Context context, int id, int idSeccion, String cod, String preg, String ayuda) {
        super(context, id, idSeccion, cod, preg, ayuda);
        this.context = context;
        flag = false;
        GPSactivo = false;

        coordenadaTextView = new TextView(context);
        coordenadaTextView.setTextSize(Parametros.FONT_RESP);
        addView(coordenadaTextView);

        File map = new File(Parametros.DIR_RAIZ, "bolivia.map");
        if (map.exists()) {
            /*if ( Double.parseDouble(Movil.getGPS().split(";")[0]) == 0 || Double.parseDouble(Movil.getGPS().split(";")[1]) == 0 ) {
                coordenadaTextView.setText("No tiene un punto estable de Georeferenciación, Habilite el sensor GPS");
            } else {*/
            dibujaMapa();
            //}
        } else {
            coordenadaTextView.setText("No tiene el mapa descargado. Descárguelo desde el menú principal");
        }
    }

    private void dibujaMapa() {
        mapView = new MapView(context);
        mapView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        mapView.setMinimumHeight(550);
        /*mapView.setMinimumWidth(400);*/
        mapView.setZoomLevelMin((byte) 10);
        mapView.setZoomLevelMax((byte) 20);
        mapView.setClickable(false);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setBuiltInZoomControls(true);
        TileCache tileCache = AndroidUtil.createTileCache(context, "mapcache", mapView.getModel().displayModel.getTileSize(), 1f, this.mapView.getModel().frameBufferModel.getOverdrawFactor());
        File map = new File(Parametros.DIR_RAIZ, "bolivia.map");
        MapDataStore mapDataStore = new MapFile(map);
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore, this.mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);
        mapView.getLayerManager().getLayers().add(tileRendererLayer);

        mapView.setZoomLevel((byte) 15);
        if (Double.parseDouble(Movil.getGPS().split(";")[0]) == 0 || Double.parseDouble(Movil.getGPS().split(";")[1]) == 0) {
            mapView.setCenter(new LatLong(-16.49565673, -68.13358799));
        } else {
            GPSactivo = true;
            mapView.setCenter(new LatLong(Double.parseDouble(Movil.getGPS().split(";")[0]), Double.parseDouble(Movil.getGPS().split(";")[1])));
        }

        mapView.setOnTouchListener(new OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                if (flag) {
                    int actionType = ev.getAction();
                    switch (actionType) {
                        case MotionEvent.ACTION_DOWN:
                            return false;
                        case MotionEvent.ACTION_UP:
                            MapViewProjection pr = mapView.getMapViewProjection();
                            LatLong point = pr.fromPixels((int)ev.getX(), (int)ev.getY());
                            latitude = point.getLatitude();
                            longitude = point.getLongitude();
                            altitude = Double.parseDouble(Movil.getGPS().split(";")[2]);
                            accuracy = Double.parseDouble(Movil.getGPS().split(";")[3]);
                            dibujaMarcador(point);
                            return true;
                        case MotionEvent.ACTION_MOVE:
                            return false;
                    }
                } else {
                    /*Toast.makeText(context, "Mapa bloqueado", Toast.LENGTH_SHORT).show();*/
                    (((ActionBarActivityMessage)((Activity) context))).informationMessage(context, null, "Información",Html.fromHtml("Mapa bloqueado. Utilice el botón 'Desbloquear' para utilizarlo"), Parametros.FONT_OBS);
                }
                return false;
            }
        });

        // CREA LOS BOTONES
        LayoutParams buttonParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        buttonParams.weight = 0.33f;
        editarButton = new Button(context);
        editarButton.setTextSize(Parametros.FONT_LIST_SMALL);
        editarButton.setLayoutParams(buttonParams);
        editarButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!flag) {
                    editarButton.setText("Bloquear");
                    flag = true;
                    mapView.setClickable(true);
                } else {
                    editarButton.setText("Desbloquear");
                    flag = false;
                    mapView.setClickable(false);
                }
            }
        });
        if(!flag) {
            editarButton.setText("Desbloquear");
        } else {
            editarButton.setText("Bloquear");
        }

        centrarButton = new Button(context);
        centrarButton.setTextSize(Parametros.FONT_LIST_SMALL);
        centrarButton.setText("Centrar");
        centrarButton.setLayoutParams(buttonParams);
        centrarButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(latitude == 0 || longitude == 0) {
                    Toast.makeText(context, "No ha seleccionado ningún punto", Toast.LENGTH_SHORT).show();
                } else {
                    mapView.setCenter(new LatLong(latitude, longitude));
                }
            }
        });

        geoposButton = new Button(context);
        geoposButton.setTextSize(Parametros.FONT_LIST_SMALL);
        geoposButton.setText("Geoposicionar");
        geoposButton.setLayoutParams(buttonParams);
        geoposButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (Movil.isActiveGps()) {
                        Movil.requestLocation();
                        double currentLat =  Double.parseDouble(Movil.getGPS().split(";")[0]);
                        double currentLon =  Double.parseDouble(Movil.getGPS().split(";")[1]);
                        if ( currentLat == 0 || currentLon == 0) {
                            Toast.makeText(context, "No se encontró ningún punto", Toast.LENGTH_SHORT).show();
                        } else {
                            mapView.setCenter(new LatLong(currentLat, currentLon));
                            Toast.makeText(context, currentLat +", "+currentLon, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Movil.initGPS();
                        Toast.makeText(context, "Active el sensor GPS", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams linearParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        buttonLayout.setLayoutParams(linearParams);
        buttonLayout.setWeightSum(1f);

        buttonLayout.addView(editarButton);
        buttonLayout.addView(centrarButton);
        buttonLayout.addView(geoposButton);
        addView(mapView);
        addView(buttonLayout);
    }

    private void dibujaMarcador(LatLong point) {
        coordenadaTextView.setText(latitude+", "+ longitude);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_marker);
        if (marker == null) {
            Drawable d = new BitmapDrawable(getResources(), bitmap);
            marker = new Marker(point, AndroidGraphicFactory.convertToBitmap(d), 0, -1 * bitmap.getHeight() / 2);
            mapView.getLayerManager().getLayers().add(marker);
        } else {
            mapView.getLayerManager().getLayers().remove(marker);
            marker.setLatLong(point);
            marker.setVerticalOffset(-1 * bitmap.getHeight() / 2);
            mapView.getLayerManager().getLayers().add(marker);
        }
    }

    @Override
    public String getCodResp() {
        if ( latitude == 0 || longitude == 0) {
            return "-1";
        } else {
            return "0";
        }
    }

    @Override
    public String getResp() {
        return latitude + ";" + longitude + ";" + altitude + ";" + accuracy;
    }

    @Override
    public void setResp(String value) {
        String[] arr = value.split(";");
        if (arr.length >= 3) {
            latitude = Double.parseDouble(arr[0]);
            longitude = Double.parseDouble(arr[1]);
            altitude = Double.parseDouble(arr[2]);
            if (arr.length == 4) {
                accuracy = Double.parseDouble(arr[3]);
            }
        }
        if ( latitude == 0 || longitude == 0 ) {
            if(GPSactivo) {
                mapView.setCenter(new LatLong(Double.parseDouble(Movil.getGPS().split(";")[0]), Double.parseDouble(Movil.getGPS().split(";")[1])));
            } else {
                (((ActionBarActivityMessage)((Activity) context))).errorMessage(context, null, "Error!", Html.fromHtml("Su GPS no está activo, no es posible geoposicionar el mapa"), Parametros.FONT_OBS);
            }
        } else {
            mapView.setCenter(new LatLong(latitude, longitude));
            dibujaMarcador(new LatLong(latitude, longitude));
        }
    }

    @Override
    public void setFocus() {

    }

    @Override
    public void destroyAll() {
        if(mapView != null) {
            mapView.destroyAll();
        }
        /*AndroidGraphicFactory.clearResourceMemoryCache();*/
    }
}