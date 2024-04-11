package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//TODO:BRP
public class JMPredio {

    public int gid;
    public String num_upm;
    public String id_manz;
    public String ciudad_com;
    public int orden_pred;
    public String recorrido;
    public String data_json;
    public String tipo;

    public ArrayList<ContentValues> datos;

    public JMPredio(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            gid = objetoJSON.getInt("gid");
            num_upm = objetoJSON.getString("num_upm");
            id_manz = objetoJSON.getString("id_manz");
            ciudad_com = objetoJSON.getString("ciudad_com");
            orden_pred = objetoJSON.getInt("orden_pred");
            recorrido = objetoJSON.getString("recorrido");
            data_json = objetoJSON.getString("data_json");
            tipo = objetoJSON.getString("tipo");

            ContentValues paquete = new ContentValues();
            paquete.put("gid", gid);
            paquete.put("num_upm", num_upm);
            paquete.put("id_manz", id_manz);
            paquete.put("ciudad_com", ciudad_com);
            paquete.put("orden_pred", orden_pred);
            paquete.put("recorrido", recorrido);
            paquete.put("data_json", data_json);
            paquete.put("tipo", tipo);

            datos.add(paquete);
        }


    }

}
