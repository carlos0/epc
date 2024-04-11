package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JTipoPregunta {

    public int id_tipo_pregunta;
    public String tipo_pregunta;
    public String descripcion;
    public String respuesta_valor;
    public String estado;
    public String usucre;
    public int feccre;
    public String usumod;
    public String fecmod;
    public ArrayList<ContentValues> datos;

    public JTipoPregunta(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);

            objetoJSON = objetoJSON.equals("") ? null : objetoJSON;

            id_tipo_pregunta = objetoJSON.getInt("id_tipo_pregunta");
            tipo_pregunta = objetoJSON.getString("tipo_pregunta");
            descripcion = objetoJSON.getString("descripcion");
            respuesta_valor = objetoJSON.getString("respuesta_valor");
            estado = objetoJSON.getString("estado");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getInt("feccre");
            usumod = objetoJSON.getString("usumod");
            fecmod = objetoJSON.getString("fecmod");


            ContentValues paquete = new ContentValues();
            paquete.put("id_tipo_pregunta", id_tipo_pregunta);
            paquete.put("tipo_pregunta", tipo_pregunta);
            paquete.put("descripcion", descripcion);
            paquete.put("respuesta_valor", respuesta_valor);
            paquete.put("estado", estado);
            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("usumod", usumod);
            paquete.put("fecmod", fecmod);

            datos.add(paquete);
        }


    }

}
