package bo.gob.ine.naci.epc.preguntas;

import android.content.Context;
import android.text.InputType;

import java.util.Map;

/**
 * Created by INE.
 */
public class Decimal extends Abierta {
    public Decimal(Context context, final int posicion, int id, int idSeccion, String cod, String preg, int maxLength, final Map<Integer, String> buttonsActive, String ayuda, final Boolean evaluar) {
        super(context, posicion, id, idSeccion, cod, preg, maxLength, buttonsActive, ayuda, false, evaluar);

        textbox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }

    @Override
    public String getCodResp() {
        if (idOpciones.contains(seleccion)) {
            return seleccion;
        } else {
            String val = textbox.getText().toString().trim().replace("\n", " ");
            if (val.length() > 0) {
                return Float.parseFloat(val) + "";
            } else {
                return "-1";
            }
        }
    }

    @Override
    public String getResp() {
        String val = textbox.getText().toString().trim().replace("\n", " ");
        if (val.length() > 0) {
            return Float.parseFloat(val) + "";
        } else {
            return textbox.getText().toString();
        }
    }
}
