package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JAsignacion {

    public int id_asignacion;
    public int id_usuario;
    public int id_upm;
    public String estado;
    public String usucre;
    public int feccre;
    public String usumod;
    public String fecmod;
    public int gestion;
    public int mes;
    public int revisita;

    public ArrayList<ContentValues> datos;

    public JAsignacion(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_asignacion = objetoJSON.getInt("id_asignacion");
            id_usuario = objetoJSON.getInt("id_usuario");
            id_upm = objetoJSON.getInt("id_upm");
            estado = objetoJSON.getString("estado");
            usucre = objetoJSON.getString("usucre");
            feccre = objetoJSON.getInt("feccre");
            usumod = objetoJSON.getString("usumod");
            fecmod = objetoJSON.getString("fecmod");
            gestion = objetoJSON.getInt("gestion");
            mes = objetoJSON.getInt("mes");
            revisita = objetoJSON.getInt("revisita");


            ContentValues paquete = new ContentValues();
            paquete.put("id_asignacion", id_asignacion);
            paquete.put("id_usuario", id_usuario);
            paquete.put("id_upm", id_upm);
            paquete.put("estado", estado);
            paquete.put("usucre", usucre);
            paquete.put("feccre", feccre);
            paquete.put("usumod", usumod);
            paquete.put("fecmod", fecmod);
            paquete.put("gestion", gestion);
            paquete.put("mes", mes);
            paquete.put("revisita", revisita);

            datos.add(paquete);
        }

    }

}
