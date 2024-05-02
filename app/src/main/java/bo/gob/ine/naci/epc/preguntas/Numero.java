package bo.gob.ine.naci.epc.preguntas;

import android.content.Context;
import android.text.InputType;

import java.util.Map;


public class Numero extends Abierta {
    public Numero(Context context, final int posicion, int id, int idSeccion, String cod, String preg, int maxLength, final Map<Integer, String> buttonsActive, String ayuda, final Boolean evaluar) {
        super(context, posicion, id, idSeccion, cod, preg, maxLength, buttonsActive, ayuda, false, evaluar);
        textbox.setInputType(InputType.TYPE_CLASS_NUMBER);
//        contenedor.setHint(ayuda == null || ayuda.equals("") ? "respuesta" : ayuda);
    }

    @Override
    public String getCodResp() {
        if (idOpciones.contains(seleccion)) {
            return seleccion;
        } else {
            String val = textbox.getText().toString().trim();
            if (val.length() > 0) {
                return val;
            } else {
                return "-1";
            }
        }
    }
}
