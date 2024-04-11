package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JUpmHijo {

    public int id_upm_hijo;
    public int id_upm_padre;
    public String codigo;
    public String nombre;
    public int id_incidencia;
    public String estado;
    public String latitud;
    public String longitud;
    public String url_pdf;
    public ArrayList<ContentValues> datos;

    public JUpmHijo(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_upm_hijo = objetoJSON.getInt("id_upm_hijo");
            id_upm_padre = objetoJSON.getInt("id_upm_padre");
            codigo = objetoJSON.getString("codigo");
            nombre = objetoJSON.getString("nombre");
            id_incidencia = objetoJSON.getInt("id_incidencia");
            estado = objetoJSON.getString("estado");
            latitud = objetoJSON.getString("latitud");
            longitud = objetoJSON.getString("longitud");
            url_pdf = objetoJSON.getString("url_pdf");


            ContentValues paquete = new ContentValues();
            paquete.put("id_upm_hijo", id_upm_hijo);
            paquete.put("id_upm_padre", id_upm_padre);
            paquete.put("codigo", codigo);
            paquete.put("nombre", nombre);
            paquete.put("id_incidencia", id_incidencia);
            paquete.put("estado", estado);
            paquete.put("latitud", latitud);
            paquete.put("longitud", longitud);
            paquete.put("url_pdf", url_pdf);

            datos.add(paquete);
        }


    }

}
