package bo.gob.ine.naci.epc.objetosJson;

import android.content.ContentValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSeleccion {

    public int id_seleccion;
    public int urbano;
    public int nro_viviendas;
    public int viv01;
    public int viv02;
    public int viv03;
    public int viv04;
    public int viv05;
    public int viv06;
    public int viv07;
    public int viv08;
    public int viv09;
    public int viv10;
    public int viv11;
    public int viv12;
    public ArrayList<ContentValues> datos;

    public JSeleccion(JSONArray jsonArray) throws JSONException {
        datos = new ArrayList<>();
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject objetoJSON = jsonArray.getJSONObject(j);
            id_seleccion = objetoJSON.getInt("id_seleccion");
            urbano = objetoJSON.getInt("urbano");
            nro_viviendas = objetoJSON.getInt("nro_viviendas");
            viv01 = objetoJSON.getInt("viv01");
            viv02 = objetoJSON.getInt("viv02");
            viv03 = objetoJSON.getInt("viv03");
            viv04 = objetoJSON.getInt("viv04");
            viv05 = objetoJSON.getInt("viv05");
            viv06 = objetoJSON.getInt("viv06");
            viv07 = objetoJSON.getInt("viv07");
            viv08 = objetoJSON.getInt("viv08");
            viv09 = objetoJSON.getInt("viv09");
            viv10 = objetoJSON.getInt("viv10");
            viv11 = objetoJSON.getInt("viv11");
            viv12 = objetoJSON.getInt("viv12");


            ContentValues paquete = new ContentValues();
            paquete.put("id_seleccion", id_seleccion);
            paquete.put("urbano", urbano);
            paquete.put("nro_viviendas", nro_viviendas);
            paquete.put("viv01", viv01);
            paquete.put("viv02", viv02);
            paquete.put("viv03", viv03);
            paquete.put("viv04", viv04);
            paquete.put("viv05", viv05);
            paquete.put("viv06", viv06);
            paquete.put("viv07", viv07);
            paquete.put("viv08", viv08);
            paquete.put("viv09", viv09);
            paquete.put("viv10", viv10);
            paquete.put("viv11", viv11);
            paquete.put("viv12", viv12);

            datos.add(paquete);
        }


    }

}
