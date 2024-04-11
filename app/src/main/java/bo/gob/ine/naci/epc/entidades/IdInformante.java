package bo.gob.ine.naci.epc.entidades;

/**
 * Created by INE.
 */
//EN DB
public class IdInformante extends Identificador {
    public int id_asignacion;
    public int correlativo;

    public IdInformante(int id_asignacion, int correlativo) {
        this.id_asignacion = id_asignacion;
        this.correlativo = correlativo;
    }

    public IdInformante(String id) {
        String[] ids = id.split(",");
        this.id_asignacion = Integer.parseInt(ids[0]);
        this.correlativo = Integer.parseInt(ids[1]);
    }

    public IdInformante(long id) {
        this.id_asignacion = (int)(id / 10000);
        this.correlativo = (int)(id % 10000);
    }

    public IdInformante(int[] id) {
        this.id_asignacion = id[0];
        this.correlativo = id[1];
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof IdInformante) {
            IdInformante i = (IdInformante)o;
            return id_asignacion == i.id_asignacion && correlativo == i.correlativo;
        } else {
            return false;
        }
    }

    @Override
    public int[] toArray() {
        return new int[]{id_asignacion, correlativo};
    }

    @Override
    public String where() {
        return "id_asignacion = " + id_asignacion + " AND correlativo = " + correlativo;
    }
}
