package bo.gob.ine.naci.epc.entidades;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class InformanteAnteriorPrimero extends EntidadCorr {
    public InformanteAnteriorPrimero() {
        super("enc_informante_anterior_primero");
    }

    public ArrayList<Map<String, Object>> obtenerListado(int idUpm) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        String query = "SELECT id_informante, codigo, descripcion, 'REVISITA' apiestado\n" +
                "FROM enc_informante_anterior\n" +
                "WHERE id_upm = " + idUpm + "\n" +
                "AND id_nivel = 1\n" +
                "ORDER BY codigo";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id_informante", cursor.getInt(0));
                row.put("codigo", cursor.getString(1));
                row.put("descripcion", cursor.getString(2));
                row.put("apiestado", cursor.getString(3));
                res.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return res;
    }

    public ArrayList<Map<String, Object>> obtenerResidente(IdInformante idPadre) {
        ArrayList<Map<String, Object>> res = new ArrayList<>();
        String query = "SELECT id_asignacion, correlativo, codigo, descripcion, 'REVISITA' apiestado\n" +
                "FROM enc_informante_anterior\n" +
                "WHERE id_asignacion_padre = " + idPadre.id_asignacion + "\n" +
                "AND correlativo_padre = " + idPadre.correlativo + "\n" +
                "AND id_nivel = 2\n" +
                "ORDER BY CAST(codigo AS Int)";

        Cursor cursor = conn.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id_asignacion", cursor.getInt(0));
                row.put("correlativo", cursor.getInt(1));
                row.put("codigo", cursor.getString(2));
                row.put("descripcion", cursor.getString(3));
                row.put("apiestado", cursor.getString(4));
                res.add(row);
            } while (cursor.moveToNext());
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id_asignacion", idPadre.id_asignacion);
            row.put("correlativo", 0);
            row.put("codigo", "NUEVO");
            row.put("descripcion", "Residente que no estaba en la anterior visita.");
            row.put("apiestado", "ELABORADO");
            res.add(row);
        }
        cursor.close();
        return res;
    }

    @SuppressWarnings("unused")
    public IdInformante get_id_informante() {
        return new IdInformante(filaActual.getInt(filaActual.getColumnIndex("id_asignacion")), filaActual.getInt(filaActual.getColumnIndex("correlativo")));
    }

    @SuppressWarnings("unused")
    public void set_id_informante(IdInformante value) {
        filaNueva.put("id_asignacion", value.id_asignacion);
        filaNueva.put("correlativo", value.correlativo);
    }
    @SuppressWarnings("unused")
    public Integer get_id_informante_padre() {
        return filaActual.getInt(filaActual.getColumnIndex("id_informante_padre"));
    }
    @SuppressWarnings("unused")
    public void set_id_informante_padre(Integer value) {
        filaNueva.put("id_informante_padre", value);
    }

    @SuppressWarnings("unused")
    public Integer get_id_upm() {
        if (filaActual.isNull(filaActual.getColumnIndex("id_upm"))) {
            return null;
        } else {
            return filaActual.getInt(filaActual.getColumnIndex("id_upm"));
        }
    }
    @SuppressWarnings("unused")
    public void set_id_upm(Integer value) {
        filaNueva.put("id_upm", value);
    }

    @SuppressWarnings("unused")
    public Integer get_id_upm_hijo() {
        if (filaActual.isNull(filaActual.getColumnIndex("id_upm_hijo"))) {
            return 0;
        } else {
            return filaActual.getInt(filaActual.getColumnIndex("id_upm_hijo"));
        }
    }

    @SuppressWarnings("unused")
    public void set_id_upm_hijo(Integer value) {
        filaNueva.put("id_upm_hijo", value);
    }

    @SuppressWarnings("unused")
    public Integer get_id_nivel() {
        return filaActual.getInt(filaActual.getColumnIndex("id_nivel"));
    }
    @SuppressWarnings("unused")
    public void set_id_nivel(Integer value) {
        filaNueva.put("id_nivel", value);
    }

    @SuppressWarnings("unused")
    public String get_codigo() {
        return filaActual.getString(filaActual.getColumnIndex("codigo"));
    }
    @SuppressWarnings("unused")
    public void set_codigo(String value) {
        filaNueva.put("codigo", value);
    }

    @SuppressWarnings("unused")
    public String get_descripcion() {
        return filaActual.getString(filaActual.getColumnIndex("descripcion"));
    }
    @SuppressWarnings("unused")
    public void set_descripcion(String value) {
        filaNueva.put("descripcion", value);
    }
    //crear Informante Anterior Default
    public ArrayList<Map<String, Object>> obtenerInformanteDefault() {
        ArrayList<Map<String, Object>> res = new ArrayList<>();

                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id_informante", 0);
                row.put("codigo", "0");
                row.put("descripcion", "Cabecera LV");
                row.put("apiestado", "REVISITA");
                res.add(row);
;
        return res;
    }

}