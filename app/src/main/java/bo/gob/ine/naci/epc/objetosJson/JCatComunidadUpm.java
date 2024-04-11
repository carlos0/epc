package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JCatComunidadUpm {

    public String num_upm;
    public String comunidad;

    public ArrayList<ContentValues> datos;

    public JCatComunidadUpm(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);

            num_upm = objetoJSON.getString("num_upm");
            comunidad = objetoJSON.getString("comunidad");



            ContentValues paquete = new ContentValues();

            paquete.put("num_upm", num_upm);
            paquete.put("comunidad", comunidad);

            datos.add(paquete);
        }


    }

}
