package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import java.util.Locale;

import bo.gob.ine.naci.epc.herramientas.Parametros;

/**
 * Created by INE.
 */
public class Formula extends PreguntaView {
    protected TextView valor;
    protected int nroDecimales;

    public Formula(Context context, int id, int idSeccion, String cod, String preg, Float val, String ayuda, String variable, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        if (variable == null) {
            this.nroDecimales = 2;
        } else {
            this.nroDecimales = Integer.parseInt(variable);
        }
        valor = new TextView(context);
        valor.setTextSize(Parametros.FONT_RESP);
        valor.setTextColor(Color.BLUE);
        //valor.setText(String.format(Locale.US, "%.2f", val));
        valor.setText(String.format(Locale.US, "%."+nroDecimales+"f", val));
        addView(valor);
    }

    @Override
    public String getCodResp() {
        if (valor.getText().length() == 0) {
            return "-1";
        } else {
            return valor.getText().toString();
        }
    }

    @Override
    public String getResp() {
        return valor.getText().toString();
    }

    @Override
    public void setResp(String value) { }

    @Override
    public void setFocus() { }
}
