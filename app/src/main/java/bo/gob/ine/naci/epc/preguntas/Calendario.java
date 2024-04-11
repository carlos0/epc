package bo.gob.ine.naci.epc.preguntas;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import bo.gob.ine.naci.epc.herramientas.Parametros;

/**
 * Created by INE.
 */
public class Calendario extends PreguntaView {
    protected TableLayout calendario;
    protected EditText[] cells;
    protected boolean validar = false;

    public Calendario(Context context, int id, int idSeccion, String cod, String preg, String[] prev, GregorianCalendar fechaContacto, String ayuda) {
        super(context, id, idSeccion, cod, preg, ayuda);

        calendario = new TableLayout(context);
        Calendar cal = new GregorianCalendar(2016, 11, 1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy MMM", new Locale("es", "ES"));
        cells = new EditText[72];
        boolean flag = true;
        for (int i = 0; i < 72; i++) {
            TableRow tr = new TableRow(context);
            TextView label = new TextView(context);
            label.setText(format.format(cal.getTime()));
            label.setTextSize(Parametros.FONT_RESP);
            tr.addView(label);
            cells[i] = new EditText(context);
            if (cal.compareTo(fechaContacto) > 0) {
                cells[i].setEnabled(false);
            } else {
                if (flag) {
                    flag = false;
                    cells[i].requestFocus();
                }
            }
            cells[i].setWidth(120);
            cells[i].setSingleLine();
            cells[i].setTextSize(Parametros.FONT_RESP);
            cells[i].setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
            InputFilter[] fArray = new InputFilter[]{new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                    if (source.equals("")) {
                        return source;
                    }
                    String res = "";
                    for (int i = 0; i < source.length(); i++) {
                        if (source.charAt(i) == 'N' || source.charAt(i) == 'E' || source.charAt(i) == 'T' || source.charAt(i) == 'U' || source.charAt(i) == 'V' || source.charAt(i) == 'W' || source.charAt(i) == 'X' || source.charAt(i) == 'Y' || (source.charAt(i) > 47 && source.charAt(i) < 58)) {
                            res = res + source.charAt(i);
                        }
                    }
                    return res;
                }
            }, new InputFilter.LengthFilter(1)};
            cells[i].setFilters(fArray);
            if (prev[i] != null && !prev[i].equals("")) {
                cells[i].setText(prev[i]);
                cells[i].setEnabled(false);
                validar = true;
            }
            //cells[i].setHint(prev[i]);
            tr.addView(cells[i]);
            calendario.addView(tr);
            cal.add(Calendar.MONTH, -1);
        }
        addView(calendario);
    }

    @Override
    public String getCodResp() {
        String cod = "";
        for (int i = 0; i < 72; i++) {
            if (validar && cells[i].isEnabled() && cells[i].getText().toString().trim().length() == 0) {
                cells[i].requestFocus();
                return "-1";
            } else {
                cod += cells[i].getText().toString() + ",";
            }
        }
        cod = cod.substring(0, cod.length() - 1);
        return cod;
    }

    @Override
    public void setCodResp(String cod) {
        String[] a = cod.split(",");
        for (int i = 0; i < 72; i++) {
            if (cells[i].isEnabled()) {
                cells[i].setText(a[i]);
            }
        }
    }

    @Override
    public String getResp() {
        Calendar cal = new GregorianCalendar(2016, 11, 1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy MMM", new Locale("es", "ES"));
        String resp = "";
        for (int i = 0; i < 72; i++) {
            resp += format.format(cal.getTime()) + ": " + cells[i].getText().toString() + "<br/>";
            cal.add(Calendar.MONTH, -1);
        }
        resp = resp.substring(0, resp.length() - 5);
        return resp;
    }

    @Override
    public void setResp(String value) { }

    @Override
    public void setFocus() { }
}
