package bo.gob.ine.naci.epc.entidades;

import static com.tozny.crypto.android.AesCbcWithIntegrity.generateKeyFromPassword;


import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.password4j.Password;
import com.tozny.crypto.android.AesCbcWithIntegrity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import bo.gob.ine.naci.epc.BuildConfig;
import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.herramientas.JWTUtils;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class Usuario extends EntidadId {
    protected static String login = null;
    protected static String nombreUsuario = null;
    protected static Integer depto = null;
    protected static Integer usuario = null;
    protected static Integer brigada = null;
    protected static Integer rol = null;
//    protected static Integer proy = null;
    protected static String rolDescripcion = null;
    protected static String serie = null;
    protected static String movilToken = null;

    public Usuario() {
        super("seg_usuario");
    }

    public static String getLogin() {
        if (login == null) {
            if (context == null) {
                context = MyApplication.getContext();
            }
            SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO+".xml", Context.MODE_PRIVATE);
            login = preferences.getString("login", null);
        }
        return login;
    }

    public static String getLogin(int idUsuario) {
        String res = null;
        String query = "SELECT login\n" +
                "FROM seg_usuario\n" +
                "WHERE id_usuario = " + idUsuario;

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToNext()) {
            res = cursor.getString(0);
        }
        cursor.close();
        return res;
    }

    public static String getNombreUsuario() {
        if (nombreUsuario == null) {
            if (context == null) {
                context = MyApplication.getContext();
            }
            SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO+".xml", Context.MODE_PRIVATE);
            nombreUsuario = preferences.getString("nombreUsuario", null);
        }
        return nombreUsuario;
    }

    public static int getUsuario() {
        if (usuario == null) {
            if (context == null) {
                context = MyApplication.getContext();
            }
            SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO+".xml", Context.MODE_PRIVATE);
            usuario = preferences.getInt("usuario", -1);
        }
        return usuario;
    }

    public static int getRol() {
        if (rol == null) {
            if (context == null) {
                context = MyApplication.getContext();
            }
            SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO+".xml", Context.MODE_PRIVATE);
            rol = preferences.getInt("rol", -1);
        }
        return rol;
    }

    public static String getRolDescripcion() {
        if (rolDescripcion == null) {
            if (context == null) {
                context = MyApplication.getContext();
            }
            SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO+".xml", Context.MODE_PRIVATE);
            rolDescripcion = preferences.getString("rolDescripcion", null);
        }
        return rolDescripcion;
    }
    public static String getMovilToken() {
        if (movilToken == null) {
            if (context == null) {
                context = MyApplication.getContext();
            }
            SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO+".xml", Context.MODE_PRIVATE);
            movilToken = preferences.getString("movilToken", null);
        }
        return movilToken;
    }
    public static int getBrigada() {
        if (brigada == null) {
            if (context == null) {
                context = MyApplication.getContext();
            }
            SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO+".xml", Context.MODE_PRIVATE);
            brigada = preferences.getInt("brigada", -1);
        }
        return brigada;
    }

//    public static int getProyecto() {
//        if (proy == null) {
//            if (context == null) {
//                context = MyApplication.getContext();
//            }
//            SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO+".xml", Context.MODE_PRIVATE);
//            proy = preferences.getInt("proy", -1);
//        }
//        return proy;
//    }

    public static String getSerie() {
        if (serie == null) {
            if (context == null) {
                context = MyApplication.getContext();
            }
            SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO+".xml", Context.MODE_PRIVATE);
            serie = preferences.getString("serie", null);
        }
        return serie;
    }

    public static ArrayList<Map<String, Object>> getUsuariosLV(int idUpm) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        String strQuery = "SELECT u.id_usuario, u.login, u.nombre || coalesce(' ' || u.paterno, '') usuario, estado\n" +
                "FROM seg_usuario u, (SELECT id_usuario, min(CAST(codigo AS Int)) orden\n" +
                "    FROM enc_informante\n" +
                "    WHERE id_nivel = 0\n" +
                "    AND id_upm = " + idUpm + "\n" +
                "    AND estado <> 'ANULADO'\n" +
                "    GROUP BY usuario) i\n" +
                "WHERE u.id_usuario = i.id_usuario\n" +
                "ORDER BY i.orden";

        Cursor cursor = conn.rawQuery(strQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> row = new TreeMap<>();
                row.put("id_usuario", cursor.getInt(0));
                row.put("codigo", cursor.getString(1));
                row.put("descripcion", cursor.getString(2));
                row.put("estado", cursor.getString(3));
                res.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public static String autenticar(String user, String pass) {
        String consulta = "SELECT u.id_usuario, u.id_rol, login, nombre, u.id_departamento, r.descripcion, u.id_brigada\n" +
                "FROM seg_usuario u, seg_rol r\n" +
                "WHERE u.id_rol = r.id_rol\n" +
                "AND u.login = '" + user + "'";
        Cursor cursor = conn.rawQuery(consulta, null);
        String result;
        if (cursor.getCount() == 0) {
            result = "Usuario o contraseña incorrectos.";
        } else {
            if (cursor.moveToFirst()) {
//                serie = cursor.getString(cursor.getColumnIndex("serie"));
//                if (serie.equals(Movil.getMacBluetooth()) || Movil.esEmuladorNox()) {
//                if (serie.equals(Movil.getImei()) || Movil.esEmuladorNox()) {
                    SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO + ".xml", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    login = cursor.getString(cursor.getColumnIndex("login"));
                    String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
//                    String paterno = cursor.getString(cursor.getColumnIndex("paterno"));
//                    nombreUsuario = nombre + (paterno == null ? "":" "+paterno);
                    nombreUsuario = nombre;
                    depto = cursor.getInt(cursor.getColumnIndex("id_departamento"));
                    usuario = cursor.getInt(cursor.getColumnIndex("id_usuario"));
                    brigada = cursor.getInt(cursor.getColumnIndex("id_brigada"));
                    rol = cursor.getInt(cursor.getColumnIndex("id_rol"));
                    rolDescripcion = cursor.getString(cursor.getColumnIndex("descripcion"));
                    editor.putString("login", login);
                    editor.putString("nombreUsuario", nombreUsuario);
                    editor.putInt("depto", depto);
                    editor.putInt("usuario", usuario);
                    editor.putInt("brigada", brigada);
                    editor.putInt("rol", rol);
                    editor.putString("rolDescripcion", rolDescripcion);
//                    editor.putString("serie", serie);
                    editor.apply();
                    result = "Ok";
//                } else {
////                    result = "El dispositivo con ID:  '"+ Movil.getMacBluetooth()+"'. no fue asignado al usuario.";
//                    result = "El dispositivo con ID:  '"+ Movil.getImei()+"'. no fue asignado al usuario.";
//                }
            } else {
                result = "Final inesperado.";
            }
        }
        cursor.close();
        return result;
    }


    public static String autenticarStore(String user, String token, JSONObject data) throws JSONException {

        String result;
        SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO + ".xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        login=user;
        editor.putString("login", login);
        String nombre = data.getString("nombre");
//                    String paterno = cursor.getString(cursor.getColumnIndex("paterno"));
//                    nombreUsuario = nombre + (paterno == null ? "":" "+paterno);
        nombreUsuario = nombre;
        depto = data.getInt("id_departamento");
        usuario = data.getInt("id_usuario");
        brigada = data.getInt("id_brigada");
        rol = data.getInt("id_rol");
        rolDescripcion = data.getString("descripcion");
        editor.putString("login", login);
        editor.putString("nombreUsuario", nombreUsuario);
        editor.putInt("depto", depto);
        editor.putInt("usuario", usuario);
        editor.putInt("brigada", brigada);
        editor.putInt("rol", rol);
        editor.putString("rolDescripcion", rolDescripcion);
        movilToken=getEncriptToken(token,user);
        editor.putString("movilToken", movilToken);
        editor.commit();

        editor.apply();
        result = "Ok";
        return result;
    }
    public static String autenticarStore(String token,String user) throws Exception {
        JSONObject data= JWTUtils.decodedPayload(token).getJSONObject("usuario");

        String result;
        SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO + ".xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        login=user;
        editor.putString("login", login);
        String nombre = data.getString("nombre");
//                    String paterno = cursor.getString(cursor.getColumnIndex("paterno"));
//                    nombreUsuario = nombre + (paterno == null ? "":" "+paterno);
        nombreUsuario = nombre;
        depto = data.getInt("id_departamento");
        usuario = data.getInt("id_usuario");
        brigada = data.getInt("id_brigada");
        rol = data.getInt("id_rol");
        rolDescripcion = data.getString("descripcion");
        editor.putString("login", login);
        editor.putString("nombreUsuario", nombreUsuario);
        editor.putInt("depto", depto);
        editor.putInt("usuario", usuario);
        editor.putInt("brigada", brigada);
        editor.putInt("rol", rol);
        editor.putString("rolDescripcion", rolDescripcion);
        movilToken=getEncriptToken(token,user);
        editor.putString("movilToken", movilToken);
        editor.commit();

        editor.apply();
        result = "Ok";
        return result;
    }
    public  static  String getPlainToken(){
        String result=null;
        try{
            AesCbcWithIntegrity.SecretKeys keys = generateKeyFromPassword(BuildConfig.SECRET, Usuario.getLogin().getBytes("UTF-8"));
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMacc = new AesCbcWithIntegrity.CipherTextIvMac(Usuario.getMovilToken());
            result = AesCbcWithIntegrity.decryptString(cipherTextIvMacc, keys);
        }catch (Exception e){

        }finally {
            return result;
        }


    }
    public  static  String getPlainToken(String token,String user){
        String result=null;
        if(Usuario.getMovilToken()!=null){
            return null;
        }
        try{
            AesCbcWithIntegrity.SecretKeys keys = generateKeyFromPassword(BuildConfig.SECRET, user.getBytes("UTF-8"));
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMacc = new AesCbcWithIntegrity.CipherTextIvMac(token);
            result = AesCbcWithIntegrity.decryptString(cipherTextIvMacc, keys);
        }catch (Exception e){

        }finally {
            return result;
        }

    }

    public static String getEncriptToken(String t,String user){
        String ciphertextString=null;
        try {

            AesCbcWithIntegrity.SecretKeys keys = generateKeyFromPassword(BuildConfig.SECRET, user.getBytes("UTF-8"));
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt(t, keys);
            ciphertextString = cipherTextIvMac.toString();

        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return ciphertextString;
        }

    }
    public static void cerrarSesion() {
        Configuracion.clear();
        login = null;
        nombreUsuario = null;
        depto = null;
        usuario = null;
        brigada = null;
        rol = null;
        rolDescripcion = null;
        movilToken=null;
        if (context == null) {
            context = MyApplication.getContext();
        }
        SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO +".xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static int getDepartamento() {
        int res = 0;
        String query = "SELECT id_departamento\n" +
                "FROM seg_usuario\n" +
                "WHERE id_usuario = " + getUsuario();

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            res = cursor.getInt(0);
        }
        cursor.close();

        return res;
    }

    public static String getFoto() {
        String res = null;
        String query = "SELECT foto\n" +
                "FROM seg_usuario\n" +
                "WHERE id_usuario = " + getUsuario();

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            res = cursor.getString(0);
        }
        cursor.close();
        return res;
    }

    public static TreeMap<Integer, String> partners(int idUsuario) {
        TreeMap<Integer, String> usuarios = new TreeMap<>();
        String query = "SELECT id_usuario, login\n" +
                "FROM seg_usuario\n" +
                "WHERE (id_brigada = 0\n" +
                "OR id_brigada = " + getBrigada() + ")\n" +
                "AND id_usuario <> " + idUsuario + "\n" +
                "AND id_departamento = " + getDepartamento() + "\n" +
                "ORDER BY id_brigada DESC";

        Cursor cursor = conn.rawQuery(query, null);
        while (cursor.moveToNext()) {
            usuarios.put(cursor.getInt(0), cursor.getString(1));
        }
        cursor.close();

        return usuarios;
    }

    /*public static void updateSerie(String usr, String serie) {
        try {
            conn.beginTransaction();
            try {
                conn.execSQL("UPDATE seg_usuario SET serie = '"+serie+"' WHERE login = '" + usr+"'");
                conn.setTransactionSuccessful();
            } finally {
                conn.endTransaction();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }*/

    public static boolean verifyPass(String pass) {
        boolean result = false;
        String consulta = "SELECT u.password\n" +
                "FROM seg_usuario u\n" +
                "WHERE u.login = '" + Usuario.getLogin() + "'";
        Cursor cursor = conn.rawQuery(consulta, null);
        if (cursor.moveToFirst()) {
            try {
                return Password.check(pass, cursor.getString(0)).withBCrypt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return result;
    }

    public static boolean verifyClave(String pass) {
        boolean result = false;
        String consulta = "SELECT u.clave\n" +
                "FROM seg_usuario u\n" +
                "WHERE u.login = '" + Usuario.getLogin() + "'";
        Cursor cursor = conn.rawQuery(consulta, null);
        if (cursor.moveToFirst()) {
            try {
                result=Password.check(pass, cursor.getString(0)).withBCrypt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return result;
    }

    public static String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.v("MD5", e.getMessage());
        }
        return "";
    }

//    public static String autenticar_bak(String user) {
//        String consulta = "SELECT u.id_usuario, ur.id_rol, login, nombre, paterno, u.id_departamento, u.id_brigada, u.serie, ur.id_proyecto, r.descripcion\n" +
//                "FROM seg_usuario u, seg_usuariorestriccion ur, seg_rol r\n" +
//                "WHERE u.id_usuario = ur.id_usuario AND ur.id_rol = r.id_rol\n" +
//                "AND u.login = '" + user + "'";
//        Cursor cursor = conn.rawQuery(consulta, null);
//        String result;
//        if (cursor.getCount() == 0) {
//            result = "Usuario o contraseña incorrectos.";
//        } else {
//            if (cursor.moveToFirst()) {
//                serie = cursor.getString(cursor.getColumnIndex("serie"));
////                if (serie.equals(Movil.getMacBluetooth())) {
//                if (serie.equals(Movil.getImei())) {
//                    SharedPreferences preferences = context.getSharedPreferences(Parametros.SIGLA_PROYECTO + ".xml", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = preferences.edit();
//                    login = cursor.getString(cursor.getColumnIndex("login"));
//                    String nombre = cursor.getString(cursor.getColumnIndex("nombre"));
//                    String paterno = cursor.getString(cursor.getColumnIndex("paterno"));
//                    nombreUsuario = nombre + (paterno == null ? "":" "+paterno);
//                    depto = cursor.getInt(cursor.getColumnIndex("id_departamento"));
//                    usuario = cursor.getInt(cursor.getColumnIndex("id_usuario"));
//                    brigada = cursor.getInt(cursor.getColumnIndex("id_brigada"));
//                    rol = cursor.getInt(cursor.getColumnIndex("id_rol"));
//                    proy = cursor.getInt(cursor.getColumnIndex("id_proyecto"));
//                    rolDescripcion = cursor.getString(cursor.getColumnIndex("descripcion"));
//                    editor.putString("login", login);
//                    editor.putString("nombreUsuario", nombreUsuario);
//                    editor.putInt("depto", depto);
//                    editor.putInt("usuario", usuario);
//                    editor.putInt("brigada", brigada);
//                    editor.putInt("rol", rol);
//                    editor.putInt("proy", proy);
//                    editor.putString("rolDescripcion", rolDescripcion);
//                    editor.putString("serie", serie);
//                    editor.apply();
//                    result = "Ok";
//                } else {
////                    result = "El dispositivo con ID:  '"+ Movil.getMacBluetooth()+"'. no fue asignado al usuario.";
//                    result = "El dispositivo con ID:  '"+ Movil.getImei()+"'. no fue asignado al usuario.";
//                }
//            } else {
//                result = "Final inesperado.";
//            }
//        }
//        cursor.close();
//        return result;
//    }

    @SuppressWarnings("unused")
    public Integer get_id_usuario() {
        return filaActual.getInt(filaActual.getColumnIndex("id_usuario"));
    }
    @SuppressWarnings("unused")
    public void set_id_usuario(Integer value) {
        filaNueva.put("id_usuario", value);
    }

    @SuppressWarnings("unused")
    public String get_nombre() {
        return filaActual.getString(filaActual.getColumnIndex("nombre"));
    }
    @SuppressWarnings("unused")
    public void set_nombre(String value) {
        filaNueva.put("nombre", value);
    }

    @SuppressWarnings("unused")
    public String get_paterno() {
        return filaActual.getString(filaActual.getColumnIndex("paterno"));
    }
    @SuppressWarnings("unused")
    public void set_paterno(String value) {
        filaNueva.put("paterno", value);
    }

    @SuppressWarnings("unused")
    public String get_serie() {
        return filaActual.getString(filaActual.getColumnIndex("serie"));
    }
    @SuppressWarnings("unused")
    public void set_serie(String value) {
        filaNueva.put("serie", value);
    }
}
