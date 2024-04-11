package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JUpm {

    public int id_upm;
    public int id_proyecto;
    public int id_departamento;
    public String codigo;
    public String nombre;
    public int fecinicio;
    public int latitud;
    public int longitud;
    public String url_pdf;

    public String estado;
    public String usucre;
    public int feccre;
    public String usumod;
    public String fecmod;

    public ArrayList<ContentValues> datos;

    public JUpm(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_upm = objetoJSON.getInt("id_upm");
            id_proyecto = objetoJSON.getInt("id_proyecto");
            id_departamento = objetoJSON.getInt("id_departamento");
            codigo = objetoJSON.getString("codigo");
            nombre = objetoJSON.getString("nombre");
            fecinicio = objetoJSON.getInt("fecinicio");
            latitud = objetoJSON.getInt("latitud");
            longitud = objetoJSON.getInt("longitud");
            estado = objetoJSON.getString("estado");

            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getInt("feccre");
            usumod = objetoJSON.getString("usumod");
            fecmod = objetoJSON.getString("fecmod");
            url_pdf=objetoJSON.getString("url_pdf");


            ContentValues paquete = new ContentValues();
            paquete.put("id_upm", id_upm);
            paquete.put("id_proyecto", id_proyecto);
            paquete.put("id_departamento", id_departamento);
            paquete.put("codigo", codigo);
            paquete.put("nombre", nombre);
            paquete.put("fecinicio", fecinicio);
            paquete.put("latitud", latitud);
            paquete.put("longitud", longitud);
            paquete.put("estado", estado);

            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("usumod", usumod);
            paquete.put("fecmod", fecmod);
            paquete.put("url_pdf", url_pdf);

            datos.add(paquete);
        }


    }

}
