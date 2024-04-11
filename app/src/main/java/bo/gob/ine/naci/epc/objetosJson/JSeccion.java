package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSeccion {

    public int id_seccion;
    public int id_nivel;
    public String codigo;
    public String seccion;
    public String estado;
    public String usucre;
    public int feccre;
    public int abierta;
    public ArrayList<ContentValues> datos;

    public JSeccion(JSONArray jsonArray) throws JSONException {

        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_seccion = objetoJSON.getInt("id_seccion");
            id_nivel = objetoJSON.getInt("id_nivel");
            codigo = objetoJSON.getString("codigo");
            seccion = objetoJSON.getString("seccion");
            estado = objetoJSON.getString("estado");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getInt("feccre");
            abierta = objetoJSON.getInt("abierta");


            ContentValues paquete = new ContentValues();
            paquete.put("id_seccion", id_seccion);
            paquete.put("id_nivel", id_nivel);
            paquete.put("codigo", codigo);
            paquete.put("seccion", seccion);
            paquete.put("estado", estado);
            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("abierta", abierta);

            datos.add(paquete);
        }


    }

}
