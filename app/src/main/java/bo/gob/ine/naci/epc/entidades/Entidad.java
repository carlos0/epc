package bo.gob.ine.naci.epc.entidades;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
//import android.database.SQLException;
//import android.database.sqlite.SQLiteDatabase;
//import net.zetetic.database.SQLException;
//import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import bo.gob.ine.naci.epc.BuildConfig;
import bo.gob.ine.naci.epc.MyApplication;

//EN DB
public class Entidad {
    protected static Context context = null;
    protected static DataBase db = null;
    protected static SQLiteDatabase conn = null;
    protected String nombreTabla = null;
    protected String[] fields;
    protected String[] types;
    protected String line;

    protected Cursor filaActual = null;
    protected ContentValues filaNueva = null;

    public Entidad(String nombreTabla) {
        if (context == null) {
            context = MyApplication.getContext();
        }
        this.nombreTabla = nombreTabla;
        if (db == null) {
//            System.loadLibrary("sqlcipher");
            db = new DataBase(context);
            conn = db.getWritableDatabase(BuildConfig.DB_SECRET);
        }else  if(conn==null){
            conn=db.getWritableDatabase(BuildConfig.DB_SECRET);
        }

        Cursor cursor = conn.rawQuery("PRAGMA table_info(" + nombreTabla + ")", null);
        int n = cursor.getCount();
        fields = new String[n];
        types = new String[n];
        int i = 0;
        while (cursor.moveToNext()) {
            fields[i] = cursor.getString(1);
            types[i] = cursor.getString(2);
            if (types[i].contains("(")) {
                types[i] = types[i].substring(0, types[i].indexOf("("));
            }
            i++;
        }
        cursor.close();
    }

    public boolean abrir(String condicion, String orden) {
        if (filaNueva == null) {
            if (filaActual == null) {
                filaActual = conn.query(nombreTabla, fields, condicion, null, null, null, orden);
                if (filaActual.moveToFirst()) {
                    return true;
                } else {
                    filaActual.close();
                    filaActual = null;
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    public boolean abrirLV(String condicion, String orden) {
        if (filaNueva == null) {
            filaActual = conn.query(nombreTabla, fields, condicion, null, null, null, orden);
            if (filaActual.moveToFirst()) {
                return true;
            } else {
                filaActual.close();
                filaActual = null;
                return false;
            }
        } else {
            return false;
        }
    }

    public void actualizar(Map<String, Object> ent, String... omitir) {
        conn.beginTransaction();
        ContentValues paquete = new ContentValues();
        try {
            for (int i = 1; i < fields.length; i++) {
                if (Arrays.binarySearch(omitir, fields[i]) < 0) {
                    paquete.put(fields[i], ent.get(fields[i]).toString());
                }
            }
            String where = fields[0] + " = " + ent.get(fields[0]);
            conn.update(nombreTabla, paquete, where, null);
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
        paquete.clear();
    }

    public boolean anterior() {
        if (filaActual == null) {
            return false;
        } else {
            if (filaNueva == null) {
                return filaActual.moveToPrevious();
            } else {
                return false;
            }
        }
    }

    public int count() {
        if (filaActual == null) {
            return 0;
        } else {
            return filaActual.getCount();
        }
    }

    public void deleteAll() {
        conn.beginTransaction();
        try {
            conn.delete(nombreTabla, null, null);
            conn.setTransactionSuccessful();
        } finally {
            conn.endTransaction();
        }
    }

    public static boolean cerrar() {
        if (conn != null) {
            conn.close();
            conn = null;
            if (db != null) {
                db.close();
                db = null;
                return true;
            }
        }
        return false;
    }

    public boolean editar() {
        if (filaNueva == null) {
            if (filaActual == null) {
                return false;
            } else {
                filaNueva = new ContentValues();
                for (int i = 0; i < filaActual.getColumnCount(); i++) {
                    if (filaActual.isNull(i)){
                        filaNueva.put(filaActual.getColumnName(i), (String)null);
                    } else {
                        filaNueva.put(filaActual.getColumnName(i), filaActual.getString(i));
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    public String insertDataJson(ArrayList<ContentValues> paquete, boolean encuesta, boolean actualizarNoBorrar) throws Exception {
        String message = null;
        String error = null;
        if (!actualizarNoBorrar) {
            deleteAll();
        }
        conn.beginTransaction();
        try {
            for (int i = 0; i < paquete.size(); i++) {
                error = paquete.get(i).toString();//

                if (actualizarNoBorrar) {
                    if(!encuesta){
                        String query = "SELECT * FROM enc_encuesta WHERE id_asignacion = " + paquete.get(i).get("id_asignacion").toString() + " AND correlativo = " + paquete.get(i).get("correlativo").toString() + " AND id_pregunta = " + paquete.get(i).get("id_pregunta").toString();
                        Cursor cursor = conn.rawQuery(query, null);

                        int c = cursor.getCount();
                        cursor.close();
                        if (c == 1) {
                            conn.update(nombreTabla, paquete.get(i), "id_asignacion = ? and correlativo = ? and id_pregunta = ? ", new String[]{paquete.get(i).get("id_asignacion").toString(), paquete.get(i).get("correlativo").toString(), paquete.get(i).get("id_pregunta").toString()});
                        } else {
                            conn.insertOrThrow(nombreTabla, null, paquete.get(i));
                        }
                    } else {
                        String query = "SELECT * FROM enc_informante WHERE id_asignacion = " + paquete.get(i).get("id_asignacion").toString() + " AND correlativo = " + paquete.get(i).get("correlativo").toString();
                        Cursor cursor = conn.rawQuery(query, null);
//                    Cursor cursor = conn.query(nombreTabla, new String[]{paquete.get(i).get("id_asignacion").toString()}, fields[0] + " = " + tokens[0], null, null, null, null);
                        int c = cursor.getCount();
                        cursor.close();
                        if (c == 1) {
                            conn.update(nombreTabla, paquete.get(i), "id_asignacion = ? and correlativo = ?", new String[]{paquete.get(i).get("id_asignacion").toString()});
                        } else {
                            conn.insertOrThrow(nombreTabla, null, paquete.get(i));
                        }
                    }
//                    cant++;
                } else {
                    conn.insertOrThrow(nombreTabla, null, paquete.get(i));
                }

//                conn.insertOrThrow(nombreTabla, null, paquete.get(i));
            }
            conn.setTransactionSuccessful();
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
            message = ex.getMessage()+ "<br><br>Error en la línea:<br>" +line+ " <br><br>" + nombreTabla + "@ " +error;
        } catch (SQLException ex) {
            ex.printStackTrace();
            message = ex.getMessage()+ "<br><br>Error en la línea:<br>" +line+ "<br><br>" + nombreTabla + "@ " +error;
        } finally {
            conn.endTransaction();
        }

        if (message == null) {
            return "ok";
        } else {
            throw new Exception(message);
        }

    }

    public int insertData(String path, boolean tieneCabecera, boolean actualizarNoBorrar) throws Exception {
        String message = null;
        if (!actualizarNoBorrar) {
            deleteAll();
        }

        File externFile = new File(path, nombreTabla);

        int cant = 0;
        conn.beginTransaction();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(externFile);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
            String[] tokens;

            ContentValues paquete;

            String[] fieldsPG = new String[0];
            while ((line = reader.readLine()) != null) {
                if (tieneCabecera) {
                    fieldsPG = line.split("\\|", -1);
                    tieneCabecera = false;
                } else {
                    paquete = new ContentValues();
                    tokens = line.split("\\|", -1);

                    int j = 0;
                    for (int i = 0; i < fieldsPG.length; i++) {
                        if (fields[i - j].equals(fieldsPG[i])) {
                            paquete.put(fields[i - j], tokens[i].equals("") ? null : tokens[i].replace("\"", ""));
                        } else {
                            j++;
                        }
                    }
                    if (actualizarNoBorrar) {
                        Cursor cursor = conn.query(nombreTabla, new String[]{fields[0]}, fields[0] + " = " + tokens[0], null, null, null, null);
                        int c = cursor.getCount();
                        cursor.close();
                        if (c == 1) {
                            conn.update(nombreTabla, paquete, fields[0] + " = " + tokens[0], null);
                        } else {
                            conn.insertOrThrow(nombreTabla, null, paquete);
                        }
                        cant++;
                    } else {
                        conn.insertOrThrow(nombreTabla, null, paquete);
                        cant++;
                    }

                    paquete.clear();
                }
            }
            conn.setTransactionSuccessful();
            reader.close();
            dataInputStream.close();
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
            message = ex.getMessage()+ "<br><br>Error en la línea:<br>" +line+ "<br><br>" + nombreTabla;
        } catch (IOException ex) {
            message = ex.getMessage();
        } catch (SQLException ex) {
            ex.printStackTrace();
            message = ex.getMessage()+ "<br><br>Error en la línea:<br>" +line+ "<br><br>" + nombreTabla;
        } finally {
            conn.endTransaction();
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ex) {
                    message = ex.getMessage();
                }
            }
        }
        if (message == null) {
            return cant;
        } else {
            throw new Exception(message);
        }
    }

    public int insertData(String path, boolean tieneCabecera) throws Exception {
        String message = null;
        int mov = 0;

        File externFile = new File(path, nombreTabla);

        int cant = 0;
        conn.beginTransaction();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(externFile);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader reader = new BufferedReader(new InputStreamReader(dataInputStream));
            String line;
            String[] tokens;

            ContentValues paquete;

            String[] fieldsPG = new String[0];
            while ((line = reader.readLine()) != null) {
                if (tieneCabecera) {
                    fieldsPG = line.split("\\|", -1);
                    tieneCabecera = false;
                } else {
                    paquete = new ContentValues();
                    tokens = line.split("\\|", -1);

                    int j = 0;
                    for (int i = 1; i < fieldsPG.length; i++) {
                        if (fields[i - j].equals(fieldsPG[i])) {
                            paquete.put(fields[i - j], tokens[i].equals("") ? null : tokens[i].replace("\"", ""));
                            if (fields[i - j].equals("id_movimiento")) {
                                mov = i;
                            }
                        } else {
                            j++;
                        }
                    }
                    Cursor cursor = conn.query(nombreTabla, new String[]{fields[0]}, fields[1] + " = " + tokens[1] + " AND id_movimiento = " + tokens[mov], null, null, null, null);
                    if (cursor.moveToNext()) {
                        conn.update(nombreTabla, paquete, fields[0] + " = " + cursor.getInt(0), null);
                    } else {
                        conn.insertOrThrow(nombreTabla, null, paquete);
                    }
                    cursor.close();
                    cant++;

                    paquete.clear();
                }
            }
            conn.setTransactionSuccessful();
            reader.close();
            dataInputStream.close();
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
            message = ex.getMessage() + "<br/>" + nombreTabla;
        } catch (IOException ex) {
            message = ex.getMessage();
        } finally {
            conn.endTransaction();
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ex) {
                    message = ex.getMessage();
                }
            }
        }
        if (message == null) {
            return cant;
        } else {
            throw new Exception(message);
        }
    }

    public long insert(Map<String, Object> ent, String... omitir) throws Exception {
        String message = null;
        long id = 0;
        conn.beginTransaction();
        ContentValues paquete = new ContentValues();
        try {
            for (int i = 1; i < fields.length; i++) {
                if (Arrays.binarySearch(omitir, fields[i]) < 0) {
                    paquete.put(fields[i], ent.get(fields[i]).toString());
                }
            }
            id = conn.insertOrThrow(nombreTabla, null, paquete);
            conn.setTransactionSuccessful();
        } catch (SQLException ex) {
            message = ex.getMessage();
        } finally {
            conn.endTransaction();
        }
        paquete.clear();
        if (message == null) {
            return id;
        } else {
            throw new Exception(message);
        }
    }

    public boolean isOpened() {
        return filaActual != null;
    }

    public void free() {
        if (filaActual != null) {
            filaActual.close();
            filaActual = null;
        }
        filaNueva = null;
    }

    public boolean nuevo() {
        if (filaNueva == null) {
            if (filaActual == null) {
                filaNueva = new ContentValues();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public ArrayList<Map<String, Object>> obtenerListado(String filtro, String orden) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        String s = nombreTabla;
        String q = fields.toString();
        String qw = filtro;
        Cursor cursor = conn.query(nombreTabla, fields, filtro, null, null, null, orden);
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> map = new LinkedHashMap<>();
                for (int i = 0; i < fields.length; i++) {
                    if (cursor.isNull(i)) {
                        map.put(fields[i], null);
                    } else {
                        switch (types[i]) {
                            case "integer":
                                map.put(fields[i], cursor.getInt(i));
                                break;
                            case "bigint":
                                map.put(fields[i], cursor.getLong(i));
                                break;
                            case "nvarchar":
                                map.put(fields[i], cursor.getString(i));
                                break;
                            case "text":
                                map.put(fields[i], cursor.getString(i));
                                break;
                            case "long":
                                map.put(fields[i], cursor.getLong(i));
                                break;
                            case "numeric":
                                map.put(fields[i], cursor.getDouble(i));
                                break;
                            default:
                                map.put(fields[i], cursor.getString(i));
                                break;
                        }
                    }
                }
                list.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }





    public ArrayList<Map<String, Object>> obtenerListado(String filtro) {
        return obtenerListado(filtro, null);
    }

    public Map<String, Object> obtenerRegistro(int id) {
        Map<String, Object> map = null;
        Cursor cursor = conn.query(nombreTabla, fields, fields[0] + " = " + id, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                map = new LinkedHashMap<>();
                for (int i = 0; i < fields.length; i++) {
                    if (cursor.isNull(i)) {
                        map.put(fields[i], null);
                    } else {
                        switch (types[i]) {
                            case "integer":
                                map.put(fields[i], cursor.getInt(i));
                                break;
                            case "bigint":
                                map.put(fields[i], cursor.getLong(i));
                                break;
                            case "nvarchar":
                                map.put(fields[i], cursor.getString(i));
                                break;
                            case "text":
                                map.put(fields[i], cursor.getString(i));
                                break;
                            case "long":
                                map.put(fields[i], cursor.getLong(i));
                            case "numeric":
                                map.put(fields[i], cursor.getDouble(i));
                                break;
                            default:
                                map.put(fields[i], cursor.getString(i));
                                break;
                        }
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return map;
    }

    public boolean siguiente() {
        if (filaActual == null) {
            return false;
        } else {
            if (filaNueva == null) {
                return filaActual.moveToNext();
            } else {
                return false;
            }
        }
    }
    public JSONArray obtenerListado(String nombreTablaExterna, String filtro, String orden) {
        ArrayList<Map<String, Object>> list = new ArrayList<>();

        String q = fields.toString();
        String qw = filtro;
        Cursor cursor = conn.query(nombreTablaExterna, fields, filtro, null, null, null, orden);
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> map = new LinkedHashMap<>();
                for (int i = 0; i < fields.length; i++) {
                    if (cursor.isNull(i)) {
                        map.put(fields[i], null);
                    } else {
                        switch (types[i]) {
                            case "integer":
                                map.put(fields[i], cursor.getInt(i));
                                break;
                            case "bigint":
                                map.put(fields[i], cursor.getLong(i));
                                break;
                            case "nvarchar":
                                map.put(fields[i], cursor.getString(i));
                                break;
                            case "text":
                                map.put(fields[i], cursor.getString(i));
                                break;
                            case "long":
                                map.put(fields[i], cursor.getLong(i));
                                break;
                            case "numeric":
                                map.put(fields[i], cursor.getDouble(i));
                                break;
                            default:
                                map.put(fields[i], cursor.getString(i));
                                break;
                        }
                    }
                }
                list.add(map);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return new JSONArray(list);
    }

    public static void eliminaDatos() {
        conn.beginTransaction();
        try {
            conn.execSQL("DELETE FROM enc_informante");
            conn.execSQL("DELETE FROM enc_encuesta");
            conn.execSQL("DELETE FROM enc_observacion");
            conn.execSQL("DELETE FROM ope_asignacion");
            conn.execSQL("DELETE FROM cat_upm");
            conn.execSQL("DELETE FROM cat_upm_hijo");
            conn.execSQL("DELETE FROM seg_usuario");
            conn.setTransactionSuccessful();
        }
        catch (Exception e){
            e.printStackTrace();
        } finally {
            conn.endTransaction();
        }
    }
    public static DataBase getDataBase(){
        return db;
    }
}
