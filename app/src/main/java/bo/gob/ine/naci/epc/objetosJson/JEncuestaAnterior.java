package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JEncuestaAnterior {

    public int id_asignacion;
    public int correlativo;
    public int id_pregunta;
    public String codigo_respuesta;
    public String respuesta;
    public String observacion;
    public String visible;
//    public String id_informante;
//    public String id_encuesta;
    public ArrayList<ContentValues> datos;

    public JEncuestaAnterior(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_asignacion = objetoJSON.getInt("id_asignacion");
            correlativo = objetoJSON.getInt("correlativo");
            id_pregunta = objetoJSON.getInt("id_pregunta");
            codigo_respuesta = objetoJSON.getString("codigo_respuesta");
            respuesta = objetoJSON.getString("respuesta");
            observacion = objetoJSON.getString("observacion");
            visible = objetoJSON.getString("visible");
//            id_informante = objetoJSON.getString("id_informante");
//            id_encuesta = objetoJSON.getString("id_encuesta");


            ContentValues paquete = new ContentValues();
            paquete.put("id_asignacion", id_asignacion);
            paquete.put("correlativo", correlativo);
            paquete.put("id_pregunta", id_pregunta);
            paquete.put("codigo_respuesta", codigo_respuesta);
            paquete.put("respuesta", respuesta);
            paquete.put("observacion", observacion);
            paquete.put("visible", visible);
//            paquete.put("id_informante", id_informante);
//            paquete.put("id_encuesta", id_encuesta);


            datos.add(paquete);
        }


    }

}
