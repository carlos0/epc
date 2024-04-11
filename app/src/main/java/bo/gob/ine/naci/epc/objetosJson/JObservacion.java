package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JObservacion {

    public int id_observacion;
    public int id_tipo_obs;
    public int id_usuario;
    public int id_asignacion;
    public int correlativo;
    public String observacion;
    public String respuesta;
    public String justificacion;
    public String usucre;
    public int feccre;
    public String usumod;
    public String fecmod;
    public String estado;
    public int id_pregunta;
    public int id_asignacion_hijo;
    public int correlativo_hijo;
    public ArrayList<ContentValues> datos;

    public JObservacion(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_observacion = objetoJSON.getInt("id_observacion");
            id_tipo_obs = objetoJSON.getInt("id_tipo_obs");
            id_usuario = objetoJSON.getInt("id_usuario");
            id_asignacion = objetoJSON.getInt("id_asignacion");
            correlativo = objetoJSON.getInt("correlativo");
            observacion = objetoJSON.getString("observacion");
            respuesta = objetoJSON.getString("respuesta");
            justificacion = objetoJSON.getString("justificacion");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getInt("feccre");
            usumod = objetoJSON.getString("usumod");
            fecmod = objetoJSON.getString("fecmod");
            estado = objetoJSON.getString("estado");
            id_pregunta = objetoJSON.getInt("id_pregunta");
            id_asignacion_hijo = objetoJSON.getInt("id_asignacion_hijo");
            correlativo_hijo = objetoJSON.getInt("correlativo_hijo");


            ContentValues paquete = new ContentValues();
            paquete.put("id_observacion", id_observacion);
            paquete.put("id_tipo_obs", id_tipo_obs);
            paquete.put("id_usuario", id_usuario);
            paquete.put("id_asignacion", id_asignacion);
            paquete.put("correlativo", correlativo);
            paquete.put("observacion", observacion);
            paquete.put("respuesta", respuesta);
            paquete.put("justificacion", justificacion);
            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("usumod", usumod);
            paquete.put("fecmod", fecmod);
            paquete.put("estado", estado);
            paquete.put("id_pregunta", id_pregunta);
            paquete.put("id_asignacion_hijo", id_asignacion_hijo);
            paquete.put("correlativo_hijo", correlativo_hijo);

            datos.add(paquete);
        }


    }

}
