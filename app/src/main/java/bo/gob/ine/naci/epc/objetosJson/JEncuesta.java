package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JEncuesta {

    public int id_asignacion;
    public int correlativo;
    public int id_pregunta;
    public String codigo_respuesta;
    public String respuesta;
    public String observacion;
    public int latitud;
    public int longitud;
    public String visible;
    public String estado;
    public String usucre;
    public int feccre;
    public String usumod;
    public String fecmod;
    public ArrayList<ContentValues> datos;

    public JEncuesta(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_asignacion = objetoJSON.getInt("id_asignacion");
            correlativo = objetoJSON.getInt("correlativo");
            id_pregunta = objetoJSON.getInt("id_pregunta");
            codigo_respuesta = objetoJSON.getString("codigo_respuesta");
            respuesta = objetoJSON.getString("respuesta");
            observacion = objetoJSON.getString("observacion");
            latitud = objetoJSON.getInt("latitud");
            longitud = objetoJSON.getInt("longitud");
            visible = objetoJSON.getString("visible");
            estado = objetoJSON.getString("estado");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getInt("feccre");
            usumod = objetoJSON.getString("usumod");
            fecmod = objetoJSON.getString("fecmod");


            ContentValues paquete = new ContentValues();
            paquete.put("id_asignacion", id_asignacion);
            paquete.put("correlativo", correlativo);
            paquete.put("id_pregunta", id_pregunta);
            paquete.put("codigo_respuesta", codigo_respuesta);
            paquete.put("respuesta", respuesta);
            paquete.put("observacion", observacion);
            paquete.put("latitud", latitud);
            paquete.put("longitud", longitud);
            paquete.put("visible", visible);
            paquete.put("estado", estado);
            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("usumod", usumod);
            paquete.put("fecmod", fecmod);

            datos.add(paquete);
        }


    }

}
