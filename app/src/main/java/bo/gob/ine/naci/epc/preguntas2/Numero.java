package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import java.util.Map;

import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;


public class Numero extends Abierta {
    public Numero(Context context, final int posicion, int id, int idSeccion, String cod, String preg, int maxLength, final Map<Integer, String> buttonsActive, String ayuda, final Boolean evaluar, final Boolean mostrarSeccion, String observacion) {
        super(context, posicion, id, idSeccion, cod, preg, maxLength, buttonsActive, ayuda, false, evaluar, mostrarSeccion, observacion);

        final int ids = id;

        textbox.setInputType(InputType.TYPE_CLASS_NUMBER);

        textbox.setImeOptions(EditorInfo.IME_ACTION_GO);

        textbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if(evaluar) {
                        FragmentEncuesta.actualiza(ids);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public String getCodResp() {
        Log.d("REVISION201", seleccion);
        Log.d("REVISION201", textbox.getText().toString());

        if (idOpciones.contains(seleccion)) {
            Log.d("REVISION601", seleccion);
            return seleccion;
        } else {
            Log.d("REVISION601", seleccion);
            String val = textbox.getText().toString().trim();
            if (val.length() > 0) {
                return val;
            } else {
                return "-1";
            }
        }
    }
}
