package bo.gob.ine.naci.epc.preguntas2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.herramientas.Parametros;


/**
 * Created by INE.
 */
public class Gps extends PreguntaView implements LocationListener {
    protected Context context;
    protected int posicion;
    protected int idSeccion;
    protected String cod;
    protected Boolean evaluar;

    protected LocationManager locationManager;
    protected double latitude;
    protected TextView latitudeTextView;
    protected double longitude;
    protected TextView longitudeTextView;
    protected double altitude;
    protected TextView altitudeTextView;
    protected double accuracy;
    protected TextView accuracyTextView;
    protected Button refreshButton;
    protected Boolean activado = false;

    public Gps(final Context context, final int posicion, final int id, int idSeccion, String cod, String preg, String ayuda, final Boolean evaluar, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);

        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        latitudeTextView = new TextView(context);
        latitudeTextView.setTextSize(Parametros.FONT_RESP);
        addView(latitudeTextView);
        longitudeTextView = new TextView(context);
        longitudeTextView.setTextSize(Parametros.FONT_RESP);
        addView(longitudeTextView);
        altitudeTextView = new TextView(context);
        altitudeTextView.setTextSize(Parametros.FONT_RESP);
        addView(altitudeTextView);
        accuracyTextView = new TextView(context);
        accuracyTextView.setTextSize(Parametros.FONT_OBS);
        addView(accuracyTextView);
        refreshButton = new Button(context);
        refreshButton.setText("Actualizar");
        addView(refreshButton);
        refreshButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    activado = true;
                    if (isActive()) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Gps.this);
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Gps.this);
                    } else {
                        activate();
                    }
                    if(evaluar) {
                        FragmentEncuesta.actualiza(id);
                    }

                } catch (Exception ex) {
                    Toast.makeText(Gps.this.context, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public String getCodResp() {
        if(activado) {
            return "0";
        }else{
            return "-1";
        }
    }

    @Override
    public String getResp() {
        return latitude + ";" + longitude + ";" + altitude + ";" + accuracy;
    }

    @Override
    public void setCodResp(String value) {
        if(value.equals("0")){
            activado = true;
        }
    }

    @Override
    public void setResp(String value) {
        String[] arr = value.split(";");
        if (arr.length >= 3) {
            latitude = Double.parseDouble(arr[0]);
            latitudeTextView.setText(Html.fromHtml("<b>Latitud:</b> " + latitude));
            longitude = Double.parseDouble(arr[1]);
            longitudeTextView.setText(Html.fromHtml("<b>Longitud:</b> " + longitude));
            altitude = Double.parseDouble(arr[2]);
            altitudeTextView.setText(Html.fromHtml("<b>Altitud:</b> " + altitude));
            if (arr.length == 4) {
                accuracy = Double.parseDouble(arr[3]);
                accuracyTextView.setText(Html.fromHtml("<b>Exactitud:</b> " + accuracy));
            } else {
                accuracyTextView.setText(Html.fromHtml("<b>Exactitud:</b> Sin información."));
            }
        }
    }

    @Override
    public void setFocus() {

    }

    public boolean isActive() {
        return locationManager != null &&
                (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    public void activate() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location.getLatitude() != 0 && location.getLongitude() != 0) {
            latitude = location.getLatitude();
            latitudeTextView.setText(Html.fromHtml("<b>Latitud:</b> " + latitude));
            longitude = location.getLongitude();
            longitudeTextView.setText(Html.fromHtml("<b>Longitud:</b> " + longitude));
            altitude = location.getAltitude();
            altitudeTextView.setText(Html.fromHtml("<b>Altitud:</b> " + altitude));
            accuracy = location.getAccuracy();
            accuracyTextView.setText(Html.fromHtml("<b>Exactitud:</b> " + accuracy));
            if (location.getAccuracy() <= 15) {
                locationManager.removeUpdates(this);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
