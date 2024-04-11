package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JCatalogo {

    public int id_catalogo;
    public String catalogo;
    public String codigo;
    public String descripcion;
    public ArrayList<ContentValues> datos;

    public JCatalogo(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_catalogo = objetoJSON.getInt("id_catalogo");
            catalogo = objetoJSON.getString("catalogo");
            codigo = objetoJSON.getString("codigo");
            descripcion = objetoJSON.getString("descripcion");


            ContentValues paquete = new ContentValues();
            paquete.put("id_catalogo", id_catalogo);
            paquete.put("catalogo", catalogo);
            paquete.put("codigo", codigo);
            paquete.put("descripcion", descripcion);

            datos.add(paquete);
        }


    }

}
