package bo.gob.ine.naci.epc.herramientas;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import bo.gob.ine.naci.epc.MyApplication;
import android.provider.Settings;
public class Movil {
    protected static LocationManager LOCATION_MANAGER;
    protected static LocationListener LOCATION_LISTENER;
    public static double LATITUDE;
    public static double LONGITUDE;
    public static double ALTITUDE;
    public static double ACCURACY;

    public static int getApiVersion(){
        return Build.VERSION.SDK_INT;
    }

    public static String getHost(){
        return Build.HOST;
    }

    public static String getFingerPrint(){
        return Build.FINGERPRINT;
    }

    public static String getHardware(){
        return Build.HARDWARE;
    }

    public static String getBoard(){
        return Build.BOARD;
    }

    public static String getBrand(){
        return Build.BRAND;
    }

    public static String getRelease(){
        return Build.VERSION.RELEASE;
    }

    public static String getDevice(){
        return Build.DEVICE;
    }

    public static String getModel(){
        return Build.MODEL;
    }

    public static String getProduct(){
        return Build.PRODUCT;
    }

    public static String getUser(){
        return Build.USER;
    }

    public static String getTime(){
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date(Build.TIME));
    }

    public static double getBatteryCapacity() {
        Object mPowerProfile_ = null;
        double batteryCapacity;
        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";
        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS).getConstructor(Context.class).newInstance(MyApplication.getContext());
            batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
        } catch (Exception e) {
            e.printStackTrace();
            batteryCapacity = -1;
        }
        return batteryCapacity;
    }

    public static void initGPS() {
        /*if(!Movil.isActiveGps()) {
            ((MainActivity)activity).informationMessage("activaGPS", "Sin GPS", Html.fromHtml("Active el sensor de GPS"));
        }*/
        try {
            LOCATION_MANAGER = (LocationManager)MyApplication.getContext().getSystemService(Context.LOCATION_SERVICE);
            Log.i("Movil", "GPS Activo");
            LOCATION_LISTENER = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location.getLatitude() != 0 && location.getLongitude() != 0) {
                        LATITUDE = location.getLatitude();
                        LONGITUDE = location.getLongitude();
                        ALTITUDE = location.getAltitude();
                        ACCURACY = location.getAccuracy();
                        if (location.getAccuracy() <= 15) {
                            LOCATION_MANAGER.removeUpdates(this);
                        }
                        /*Toast.makeText(MyApplication.getContext(), "GPS actualizado: " + getGPS(), Toast.LENGTH_LONG);*/
                        Log.i("Movil", "actualizado "+location.getProvider().toString()+" : "+getGPS());
                    }
                }
                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {}
                @Override
                public void onProviderEnabled(String s) {}
                @Override
                public void onProviderDisabled(String s) {}
            };
            requestLocation();

        } catch (Exception ex) {
            Toast.makeText(MyApplication.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void requestLocation()    {
        LOCATION_MANAGER.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, LOCATION_LISTENER);
        LOCATION_MANAGER.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, LOCATION_LISTENER);
    }

    public static String getGPS()    {
        Log.i("Movil", "Recuperando LAT-LON: "+ LATITUDE + ";" + LONGITUDE + ";" + ALTITUDE + ";" + ACCURACY);
        return LATITUDE + ";" + LONGITUDE + ";" + ALTITUDE + ";" + ACCURACY;
    }

    public static boolean isActiveGps() {
        return LOCATION_MANAGER != null && (LOCATION_MANAGER.isProviderEnabled(LocationManager.GPS_PROVIDER));
    }

    public static String dateExtractor(long longDate) {
        Calendar fe = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        // EN POSTGRESQL EL DATO FECHA ES: TIPO TIMESTAMP, AL DESCARGAR EL CSV, SE REALIZA UN select extract (epoch from timestamp), PARA QUE LA APP INTERPRETE DICHO NUMERO, SE SUMA 14400000 (4hrs.) (ANTERIORMENTE 86400000)
        fe.setTimeInMillis(longDate * 1000 + 14400000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm", new Locale("es"));
        //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss", new Locale("es"));
        return simpleDateFormat.format(fe.getTime());
    }

    public static String zippea(String zipDirectorioDestino, String filePathOrigen, String zipFileName, String zippedFileName) {
        String zipPath = zipDirectorioDestino + zipFileName;
        try {
            File directory = new File(Parametros.DIR_BAK);
            if ( !directory.exists() ) {
                if (!directory.mkdirs()) {
                    return "Error:No se pudo crear el directorio.";
                }
            }
            FileOutputStream fos = new FileOutputStream(zipPath);
            ZipOutputStream zos = new ZipOutputStream(fos);
            ZipEntry ze;
            if (!zippedFileName.equals("")) {
                ze = new ZipEntry(zippedFileName);
            } else {
                ze = new ZipEntry(new File(filePathOrigen).getName());
            }
            zos.putNextEntry(ze);

            FileInputStream fis = new FileInputStream(filePathOrigen);
            byte[] buf = new byte[1024];
            int len;
            while ((len = fis.read(buf)) > 0) {
                zos.write(buf, 0, len);
            }
            fis.close();
            zos.close();
            return zipFileName;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String[] unZippea(String zipDirectorioOrigen, String zipDirectorioDestino, String fileNameOrigen) throws Exception {
        ArrayList<String> files2 = new ArrayList<>();

        InputStream is;
        ZipInputStream zis;
        is = new FileInputStream(zipDirectorioOrigen + fileNameOrigen);
        zis = new ZipInputStream(new BufferedInputStream(is));
        ZipEntry ze;
        byte[] buffer = new byte[1024];
        int count;

        while ((ze = zis.getNextEntry()) != null) {
            if (ze.isDirectory()) {
                File fmd = new File(zipDirectorioDestino + ze.getName());
                if (fmd.mkdirs()) {
                    continue;
                } else {
                    throw new Exception("No se ha podido crear el directorio.");
                }
            }
            files2.add(ze.getName());
            FileOutputStream fout = new FileOutputStream(zipDirectorioDestino + ze.getName());
            while ((count = zis.read(buffer)) != -1) {
                fout.write(buffer, 0, count);
            }
            fout.close();
            zis.closeEntry();
        }
        zis.close();
        return files2.toArray(new String[files2.size()]);
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void vibrate() {
        Vibrator vibrate= (Vibrator)MyApplication.getContext().getSystemService(MyApplication.getContext().VIBRATOR_SERVICE);
        vibrate.vibrate(50);
    }

    public static void setAutoOrientationEnabled(Context context, boolean enabled)
    {
        Settings.System.putInt( context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }
    public static String getImei() {
        String identifier = null;
        TelephonyManager tm;
        try{
            tm = (TelephonyManager)MyApplication.getContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null)
                identifier = tm.getDeviceId();

        }catch (Exception e){
            e.printStackTrace();
        }
        if (identifier == null || identifier .length() == 0)
            identifier = Settings.Secure.getString(MyApplication.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        return identifier;
    }
    public static String getMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);

            while (md5.length() < 32)
                md5 = "0" + md5;

            return md5;
        } catch ( NoSuchAlgorithmException e) {
            Log.e("MD5", e.getLocalizedMessage());
            return null;
        }
    }
}
