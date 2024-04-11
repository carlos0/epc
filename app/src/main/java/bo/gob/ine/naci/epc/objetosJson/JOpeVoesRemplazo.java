package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JOpeVoesRemplazo {

    public int id_upm;
    public int nro_total;
    public int voe_seleccionada;
    public int voe_remplazo;

    public ArrayList<ContentValues> datos;

    public JOpeVoesRemplazo(JSONArray jsonArray) throws JSONException {

        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_upm = objetoJSON.getInt("id_upm");
            nro_total = objetoJSON.getInt("nro_total");
            voe_seleccionada = objetoJSON.getInt("voe_seleccionada");
            voe_remplazo = objetoJSON.getInt("voe_remplazo");


            ContentValues paquete = new ContentValues();
            paquete.put("id_upm", id_upm);
            paquete.put("nro_total", nro_total);
            paquete.put("voe_seleccionada", voe_seleccionada);
            paquete.put("voe_remplazo", voe_remplazo);
            datos.add(paquete);
        }
    }

}
