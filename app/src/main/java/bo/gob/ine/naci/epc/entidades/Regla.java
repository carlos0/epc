package bo.gob.ine.naci.epc.entidades;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Regla {

    private int orden;
    private String regla;
    private String mensaje;
    private String codigo;

    public Regla(int orden, String regla, String mensaje, String codigo) {
        this.orden = orden;
        this.regla = regla;
        this.mensaje = mensaje;
        this.codigo = codigo;
    }

    public int getOrden() {
        return orden;
    }

    public String getRegla() {
        return regla;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getCodigo() {
        return codigo;
    }

    public static List<Regla> fromJson(String json) throws JSONException {
        List<Regla> reglas = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            int orden = i + 1; // Calculamos el orden correlativo
            String regla = jsonObject.getString("regla");
            String mensaje = jsonObject.getString("mensaje");
            String codigo = jsonObject.getString("codigo");
            Regla nuevaRegla = new Regla(orden, regla, mensaje, codigo);
            reglas.add(nuevaRegla);
        }
        return reglas;
    }

    @Override
    public String toString() {
        return "Orden: " + orden + ", Regla: " + regla + ", Mensaje: " + mensaje + ", Código: " + codigo;
    }
}
