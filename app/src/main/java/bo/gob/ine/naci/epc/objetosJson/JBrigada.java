package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JBrigada {

    public int id_brigada;
    public int id_departamento;
    public String codigo_brigada;
    public String estado;
    public String usucre;
    public int feccre;
    public String usumod;
    public String fecmod;
    public ArrayList<ContentValues> datos;

    public JBrigada(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_brigada = objetoJSON.getInt("id_brigada");
            id_departamento = objetoJSON.getInt("id_departamento");
            codigo_brigada = objetoJSON.getString("codigo_brigada");
            estado = objetoJSON.getString("estado");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getInt("feccre");
            usumod = objetoJSON.getString("usumod");
            fecmod = objetoJSON.getString("fecmod");


            ContentValues paquete = new ContentValues();
            paquete.put("id_brigada", id_brigada);
            paquete.put("id_departamento", id_departamento);
            paquete.put("codigo_brigada", codigo_brigada);
            paquete.put("estado", estado);
            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("usumod", usumod);
            paquete.put("fecmod", fecmod);

            datos.add(paquete);
        }


    }

}
