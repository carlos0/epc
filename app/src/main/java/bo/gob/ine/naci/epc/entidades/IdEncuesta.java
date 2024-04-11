package bo.gob.ine.naci.epc.entidades;

import java.io.Serializable;

/**
 * Created by alberto on 09/01/2016.
 */
//EN DB
public class IdEncuesta extends Identificador implements Serializable {
    public int id_asignacion;
    public int correlativo;
    public int id_pregunta;

    public IdEncuesta(int id_asignacion, int correlativo, int id_pregunta) {
        this.id_asignacion = id_asignacion;
        this.correlativo = correlativo;
        this.id_pregunta = id_pregunta;
    }

    public IdEncuesta(int[] id) {
        this.id_asignacion = id[0];
        this.correlativo = id[1];
        this.id_pregunta = id[2];
    }

    public IdInformante getIdInformante() {
        return new IdInformante(id_asignacion, correlativo);
    }

    public void setIdInformante(IdInformante value) {
        id_asignacion = value.id_asignacion;
        correlativo = value.correlativo;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IdEncuesta) {
            IdEncuesta i = (IdEncuesta)o;
            return id_asignacion == i.id_asignacion && correlativo == i.correlativo &&
                    id_pregunta == i.id_pregunta;
        } else {
            return false;
        }
    }

    @Override
    public int[] toArray() {
        return new int[]{id_asignacion, correlativo, id_pregunta
        };
    }

    @Override

    public String where() {
        return "id_asignacion = " + id_asignacion + " AND correlativo = " + correlativo +
                " AND id_pregunta = " + id_pregunta;
    }
}
