package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//TODO:BRP
public class JMDisperso {

    public int gid;
    public String num_upm;
    public String ciudad_com;
    public String id_com;
    public String data_json;
    public String tipo;

    public ArrayList<ContentValues> datos;

    public JMDisperso(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            gid = objetoJSON.getInt("gid");
            num_upm = objetoJSON.getString("num_upm");
            ciudad_com = objetoJSON.getString("ciudad_com");
            id_com = objetoJSON.getString("id_com");
            data_json = objetoJSON.getString("data_json");
            tipo = objetoJSON.getString("tipo");

            ContentValues paquete = new ContentValues();
            paquete.put("gid", gid);
            paquete.put("num_upm", num_upm);
            paquete.put("ciudad_com", ciudad_com);
            paquete.put("id_com", id_com);
            paquete.put("data_json", data_json);
            paquete.put("tipo", tipo);

            datos.add(paquete);
        }


    }

}
