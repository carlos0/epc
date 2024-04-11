package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JNivel {

    public int id_nivel;
    public int id_nivel_padre;
    public int nivel;
    public String descripcion;
    public String tipo;
    public int id_rol;

    public ArrayList<ContentValues> datos;

    public JNivel(JSONArray jsonArray) throws JSONException {

        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_nivel = objetoJSON.getInt("id_nivel");
            id_nivel_padre = objetoJSON.getInt("id_nivel_padre");
            nivel = objetoJSON.getInt("nivel");
            descripcion = objetoJSON.getString("descripcion");
            tipo = objetoJSON.getString("tipo");
            id_rol = objetoJSON.getInt("id_rol");


            ContentValues paquete = new ContentValues();
            paquete.put("id_nivel", id_nivel);
            paquete.put("id_nivel_padre", id_nivel_padre);
            paquete.put("nivel", nivel);
            paquete.put("descripcion", descripcion);
            paquete.put("tipo", tipo);
            paquete.put("id_rol", id_rol);

            datos.add(paquete);
        }


    }

}
