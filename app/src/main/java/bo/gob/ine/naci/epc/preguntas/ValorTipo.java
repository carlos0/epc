package bo.gob.ine.naci.epc.preguntas;

import android.content.Context;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Map;

import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.herramientas.Parametros;


/**
 * Created by INE.
 */
public class ValorTipo extends PreguntaView {
    protected EditText valor;

    protected Context context;
    protected int posicion;
    protected int idSeccion;
    protected String cod;
    protected Boolean evaluar;

    protected Map<Integer, String> opciones;
    protected TextView txtFrecuencia;
    protected RadioGroup radioGroup;
    protected Button resp, noAplica, seNiega, noSabe;
    protected int buttonsActive;

    public ValorTipo(final Context context, final int posicion, final int id, int idSeccion, String cod, String preg, Map<Integer, String> opciones, int maxLength, int buttonsActive, String ayuda, final Boolean evaluar) {
        super(context, id, idSeccion, cod, preg, ayuda);
        this.opciones = opciones;
        this.buttonsActive = buttonsActive;

        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;

        valor = new EditText(context);
        valor.setSingleLine();
        valor.setTextSize(Parametros.FONT_RESP);
        valor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (maxLength > 0) {
            InputFilter[] fArray = new InputFilter[2] ;
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            fArray[1] = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                    if(source != null && Parametros.BLOCK_CHARACTER_SET.contains(""+source)){
                        return "";
                    }
                    return null;
                }
            };
            valor.setFilters(fArray);
        }
        valor.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(evaluar) {
                    FragmentEncuesta.ejecucion(context, id, posicion, "");
                }
                return false;
            }
        });

        addView(valor);

        radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        RadioButton rb;
        for (Integer key : opciones.keySet()) {
            rb = new RadioButton(context);
            radioGroup.addView(rb);
            String opcion = opciones.get(key);
            String[] a = opcion.split("\\|");
            rb.setText(Html.fromHtml(a[1]));
            rb.setTextSize(Parametros.FONT_RESP);
            rb.setId(key);
        }
        if (opciones.size() == 1) {
            ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
        }

        txtFrecuencia = new TextView(context);
        txtFrecuencia.setText("");
        addView(txtFrecuencia);

        radioGroup.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(evaluar) {
                    FragmentEncuesta.ejecucion(context, id, posicion, "");
                }
                return true;
            }
        });
        addView(radioGroup);


        valor.requestFocus();
    }

    public void setEnabledRB(boolean flag) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton child = (RadioButton)radioGroup.getChildAt(i);
            child.setEnabled(flag);
            if (child.isChecked() && !flag) {
                child.setChecked(false);
            }
        }
        valor.setEnabled(flag);
    }

//    @Override
//    public long getIdResp() {
//        return radioGroup.getCheckedRadioButtonId();
//    }

    @Override
    public String getCodResp() {

                if (radioGroup.getCheckedRadioButtonId() == -1 || valor.getText().toString().trim().length() == 0) {
                    return "-1";
                } else {
                    return String.valueOf(valor.getText()).trim();
                }

    }

//    @Override
//    public void setIdResp(long id) {
//        for (int i = 0; i < radioGroup.getChildCount(); i++) {
//            RadioButton child = (RadioButton)radioGroup.getChildAt(i);
//            if (child.getId() == id) {
//                child.setChecked(true);
//                break;
//            }
//        }
//    }

    @Override
    public String getResp() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            return "";
        } else {
            return opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0] + ":" + opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[1];
        }
    }

    @Override
    public void setCodResp(String value) {
            this.valor.setText(value);
    }

    @Override
    public void setResp(String value) {
        String[] val = value.split(":");
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton child = (RadioButton) radioGroup.getChildAt(i);
            if (child.getId() == Integer.valueOf(val[0])) {
                child.setChecked(true);
                break;
            }
        }
    }

    @Override
    public void setFocus() { valor.requestFocus(); }

    @Override
    public void setEstado(Estado value) {
        super.setEstado(value);
    }

    public void setVisible (View v, boolean sw) {
        if(sw) {
            v.setVisibility(VISIBLE);
        } else {
            v.setVisibility(INVISIBLE);
        }
    }
}
