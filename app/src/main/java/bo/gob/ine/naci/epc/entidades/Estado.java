package bo.gob.ine.naci.epc.entidades;

/**
 * Created by INE.
 */
public enum Estado {
    INSERTADO(0),
    ELABORADO(1), //INFORMANTE CREADO, DISPONIBLE PARA USO Y MODIFICACIONES
    CONCLUIDO(2), //INFORMANTE NIVEL 2, FINALIZÓ SATISFACTORIAMENTE LA BOLETA DE HOGAR (-2)
    ANULADO(3),
    INCOMPLETO(4),
    SELECCIONADO(5),
    SELECCIONADO2(6),
    SELECCIONADO3(7),
    SELECCIONADO4(8),
    DESELECCIONADO(9),
    ENVIADO(10),
    EDITADO(11),
    PRECONCLUIDO(12),
    NOAPLICA(997),
    SENIEGA(998),
    NOSABE(999);

    private int value;

    Estado(int value) {
        this.value = value;
    }
}
