package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JInformante {

    public int id_asignacion;
    public int correlativo;
    public String id_asignacion_padre;
    public String correlativo_padre;
    public int id_usuario;
    public int id_upm;
    public Integer id_upm_hijo;
    public int id_nivel;
    public String codigo;
    public String estado;
    public String usucre;
    public int feccre;
    public String usumod;
    public String fecmod;
    public String descripcion;
//    public  Integer id_asignacion_anterior;
//    public  Integer correlativo_anterior;
//    public  String codigo_anterior;
    public ArrayList<ContentValues> datos;

    public JInformante(JSONArray jsonArray) throws JSONException {

        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_asignacion = objetoJSON.getInt("id_asignacion");
            correlativo = objetoJSON.getInt("correlativo");
            id_asignacion_padre = objetoJSON.getString("id_asignacion_padre").equals("")? null : objetoJSON.getString("id_asignacion_padre");
            correlativo_padre = objetoJSON.getString("correlativo_padre").equals("")? null : objetoJSON.getString("correlativo_padre");
            id_usuario = objetoJSON.getInt("id_usuario");
            id_upm = objetoJSON.getInt("id_upm");
            id_upm_hijo = objetoJSON.getString("id_upm_hijo").equals("")?null:objetoJSON.getInt("id_upm_hijo");
            id_nivel = objetoJSON.getInt("id_nivel");
            codigo = objetoJSON.getString("codigo");
            estado = objetoJSON.getString("estado");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getInt("feccre");
            usumod = objetoJSON.getString("usumod");
            fecmod = objetoJSON.getString("fecmod");
            descripcion = objetoJSON.getString("descripcion").equals(null)? null : objetoJSON.getString("descripcion");
//            id_asignacion_anterior = objetoJSON.getString("id_asignacion_anterior").equals("")?null:objetoJSON.getInt("id_asignacion_anterior");
//            correlativo_anterior = objetoJSON.getString("correlativo_anterior").equals("")?null:objetoJSON.getInt("correlativo_anterior");
//            codigo_anterior = objetoJSON.getString("codigo_anterior").equals("")?null:objetoJSON.getString("codigo_anterior");


            ContentValues paquete = new ContentValues();
            paquete.put("id_asignacion", id_asignacion);
            paquete.put("correlativo", correlativo);
            paquete.put("id_asignacion_padre", id_asignacion_padre);
            paquete.put("correlativo_padre", correlativo_padre);
            paquete.put("id_usuario", id_usuario);
            paquete.put("id_upm", id_upm);
            paquete.put("id_upm_hijo", id_upm_hijo);
            paquete.put("id_nivel", id_nivel);
            paquete.put("codigo", codigo);
            paquete.put("estado", estado);
            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("usumod", usumod);
            paquete.put("fecmod", fecmod);
            paquete.put("descripcion", descripcion);
//            paquete.put("id_asignacion_anterior", id_asignacion_anterior);
//            paquete.put("correlativo_anterior", correlativo_anterior);
//            paquete.put("codigo_anterior", codigo_anterior);

            datos.add(paquete);
        }


    }

}
