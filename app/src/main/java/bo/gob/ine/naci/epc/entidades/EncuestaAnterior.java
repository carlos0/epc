package bo.gob.ine.naci.epc.entidades;


/**
 * Created by INE.
 */
public class EncuestaAnterior extends EntidadCorr {
    public EncuestaAnterior() {
        super("enc_encuesta_anterior");
    }

    @SuppressWarnings("unused")
    public Long get_id_asignacion() {
        return filaActual.getLong(filaActual.getColumnIndex("id_asignacion"));
    }
    @SuppressWarnings("unused")
    public void set_id_asigancion(Long value) {
        filaNueva.put("id_asignacion", value);
    }

    @SuppressWarnings("unused")
    public Integer get_correlativo() {
        return filaActual.getInt(filaActual.getColumnIndex("correlativo"));
    }
    @SuppressWarnings("unused")
    public void set_correlativo(Integer value) {
        filaNueva.put("correlativo", value);
    }

    @SuppressWarnings("unused")
    public Integer get_id_pregunta() {
        return filaActual.getInt(filaActual.getColumnIndex("id_pregunta"));
    }
    @SuppressWarnings("unused")
    public void set_id_pregunta(Integer value) {
        filaNueva.put("id_pregunta", value);
    }
    @SuppressWarnings("unused")
    public String get_codigo_respuesta() {
        return filaActual.getString(filaActual.getColumnIndex("codigo_respuesta"));
    }
    @SuppressWarnings("unused")
    public void set_codigo_respuesta(String value) {
        filaNueva.put("codigo_respuesta", value);
    }

    @SuppressWarnings("unused")
    public String get_respuesta() {
        return filaActual.getString(filaActual.getColumnIndex("respuesta"));
    }
    @SuppressWarnings("unused")
    public void set_respuesta(String value) {
        filaNueva.put("respuesta", value);
    }

    @SuppressWarnings("unused")
    public int get_fila() {
        return filaActual.getInt(filaActual.getColumnIndex("fila"));
    }
    @SuppressWarnings("unused")
    public void set_fila(int value) {
        filaNueva.put("fila", value);
    }

    @SuppressWarnings("unused")
    public String get_observacion() {
        return filaActual.getString(filaActual.getColumnIndex("observacion"));
    }
    @SuppressWarnings("unused")
    public void set_observacion(String value) {
        filaNueva.put("observacion", value);
    }
}

