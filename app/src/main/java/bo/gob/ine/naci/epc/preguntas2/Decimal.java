package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.util.Map;

import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;

/**
 * Created by INE.
 */
public class Decimal extends Abierta {
    public Decimal(Context context, final int posicion, int id, int idSeccion, String cod, String preg, int maxLength, final Map<Integer, String> buttonsActive, String ayuda, final Boolean evaluar, final Boolean mostrarSeccion, String observacion) {
        super(context, posicion, id, idSeccion, cod, preg, maxLength, buttonsActive, ayuda, false, evaluar, mostrarSeccion, observacion);

        final int ids = id;

        textbox.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        textbox.setImeOptions(EditorInfo.IME_ACTION_GO);

        textbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    FragmentEncuesta.actualiza(ids);
                    return true;
                }
                return false;
            }
        });
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
