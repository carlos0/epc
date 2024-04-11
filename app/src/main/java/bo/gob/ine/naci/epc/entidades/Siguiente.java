package bo.gob.ine.naci.epc.entidades;

/**
 * Created by INE.
 */
public class Siguiente {
    public IdInformante idInformante;
    public int idSiguiente;
    public int seccion;

    public Siguiente() {
        this.idInformante = null;
        this.idSiguiente = 0;
        this.seccion = 0;
    }

    public Siguiente(int idSiguiente, int seccion) {
        idInformante = null;
        this.idSiguiente = idSiguiente;
        this.seccion = seccion;
    }

    public Siguiente(IdInformante idInformante, int idSiguiente, int seccion) {
        this.idInformante = idInformante;
        this.idSiguiente = idSiguiente;
        this.seccion = seccion;
    }

    public Siguiente(int[] values) {
        if (values[0] == 0) {
            idInformante = null;
        } else {
            idInformante = new IdInformante(values[0], values[1]);
        }
        idSiguiente = values[2];
        seccion = values[3];
    }

    public int[] toArray() {
        if (idInformante == null) {
            return new int[]{0, 0, idSiguiente, seccion};
        } else {
            return new int[]{idInformante.id_asignacion, idInformante.correlativo, idSiguiente, seccion};
        }
    }
}
