package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JFlujo {

    public int id_pregunta;
    public String saltos;

    public int id_pregunta_destino;
    public String orden;
    public String regla;
    public String rpn;
    public ArrayList<ContentValues> datos;

    public JFlujo(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);

            objetoJSON = objetoJSON.equals("") ? null : objetoJSON;

            id_pregunta = objetoJSON.getInt("id_pregunta");
            saltos = objetoJSON.getString("saltos");

            if(!saltos.equals("null")) {

                JSONArray jsonArrayFlujo = new JSONArray(saltos);
                for (int i = 0; i < jsonArrayFlujo.length(); i++) {

                    JSONObject objetoJSONFlujo = jsonArrayFlujo.getJSONObject(i);

                    id_pregunta_destino = objetoJSONFlujo.getInt("destino");
                    orden = objetoJSONFlujo.getString("orden");
                    regla = objetoJSONFlujo.getString("regla");

                    ContentValues paquete = new ContentValues();
                    paquete.put("id_pregunta", id_pregunta);
                    paquete.put("id_pregunta_destino", id_pregunta_destino);
                    paquete.put("orden", orden);
                    paquete.put("regla", regla);
                    paquete.put("rpn", regla);

                    datos.add(paquete);
                }
            }
        }
    }

}
