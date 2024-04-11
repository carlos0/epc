package bo.gob.ine.naci.epc.preguntas;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;
import java.util.Map;

import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class ValorTipoMatriz extends PreguntaView {
    protected EditText valor;
    protected Map<Integer, String> opciones;
    protected TextView txtFrecuencia;
    protected RadioGroup radioGroup;
    protected Button resp, noAplica, seNiega, noSabe;
    protected int buttonsActive;

    public ValorTipoMatriz(Context context, int id, int idSeccion, String cod, String preg, final Map<Integer, String> opciones, int maxLength, int buttonsActive, String ayuda, final String codEsp) {
        super(context, id, idSeccion, cod, preg, ayuda);
        this.opciones = opciones;
        this.buttonsActive = buttonsActive;

        valor = new EditText(context);
        valor.setSingleLine();
        valor.setTextSize(Parametros.FONT_RESP);
        valor.setInputType(InputType.TYPE_CLASS_NUMBER);
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
//        addView(valor);
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
            rb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((RadioButton)v).isChecked()) {
                        String codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
                        if (codigo.equals(codEsp)) {
                            valor.setText("0");
                            valor.setEnabled(false);
                        } else {
                            valor.setText("");
                            valor.setEnabled(true);
                        }
                    }
                }
            });
        }
        if (opciones.size() == 1) {
            ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
        }

        txtFrecuencia = new TextView(context);
        txtFrecuencia.setText("");
        addView(txtFrecuencia);
        int a = radioGroup.getChildCount();
        if(radioGroup.getChildCount() > 2){
            addView(valor);
            addView(radioGroup);
        }else {
            addView(radioGroup);
            addView(valor);
        }

        LinearLayout buttons = new LinearLayout(context);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        resp = new Button(context);
        resp.setText("Resp");
        resp.setLayoutParams(layoutParams);
        resp.setTextColor(Color.BLUE);
        buttons.addView(resp);
        noAplica = new Button(context);
        noAplica.setText("No aplica");
        noAplica.setLayoutParams(layoutParams);
        buttons.addView(noAplica);
        seNiega = new Button(context);
        seNiega.setText("Se niega");
        seNiega.setLayoutParams(layoutParams);
        buttons.addView(seNiega);
        noSabe = new Button(context);
        noSabe.setText("No sabe");
        noSabe.setLayoutParams(layoutParams);
        buttons.addView(noSabe);
        resp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ValorTipoMatriz.this.estado = Estado.INSERTADO;
                setEnabledRB(true);
                resp.setTextColor(Color.BLUE);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        noAplica.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ValorTipoMatriz.this.estado = Estado.NOAPLICA;
                setEnabledRB(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLUE);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        seNiega.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ValorTipoMatriz.this.estado = Estado.SENIEGA;
                setEnabledRB(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLUE);
                noSabe.setTextColor(Color.BLACK);
            }
        });
        noSabe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ValorTipoMatriz.this.estado = Estado.NOSABE;
                setEnabledRB(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLUE);
            }
        });

        if ( buttonsActive != 0 ) {
            addView(buttons);
            //setVisible(resp, (buttonsActive & 3) == 3);
            setVisible(noAplica, (buttonsActive & 4) == 4);
            setVisible(seNiega, (buttonsActive & 2) == 2);
            setVisible(noSabe, (buttonsActive & 1) == 1);
        }
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

    public void setEnabledET() {
        Integer dato = Integer.valueOf(String.valueOf(valor.getText()));
        Integer base = 0;
        EditText child = (EditText) valor;
        if(dato == base){
            child.setEnabled(false);
        }else {
            child.setEnabled(true);
        }
    }

//    @Override
//    public long getIdResp() {
//        return radioGroup.getCheckedRadioButtonId();
//    }

    @Override
    public String getCodResp() {
        switch (estado) {
            case NOAPLICA:
                return "997";
            case SENIEGA:
                return "998";
            case NOSABE:
                return "999";
            default:
                if (radioGroup.getCheckedRadioButtonId() == -1 || valor.getText().toString().trim().length() == 0) {
                    return "-1";
                } else {
                    String val = String.format(Locale.US, "%.2f", Float.parseFloat(valor.getText().toString()) * Float.parseFloat(opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[2]));
                    return opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0] + "," + val;
                }
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
        switch (estado) {
            case NOAPLICA:
                return "No aplica";
            case SENIEGA:
                return "Se niega";
            case NOSABE:
                return "No sabe";
            default:
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    return "";
                } else {
                    return valor.getText() + ":" + opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[1];
                }
        }
    }

    @Override
    public void setResp(String value) {
        String[] val = value.split(":");
        if (val.length == 2) {
            this.valor.setText(val[0]);
            if (val[0].equals("0")) {
                this.valor.setEnabled(false);
            } else {
                this.valor.setEnabled(true);
            }
        }
    }

    @Override
    public void setFocus() { valor.requestFocus(); }

    @Override
    public void setEstado(Estado value) {
        super.setEstado(value);
        switch (estado) {
            case NOAPLICA:
                setEnabledRB(false);
                valor.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLUE);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
                break;
            case SENIEGA:
                setEnabledRB(false);
                valor.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLUE);
                noSabe.setTextColor(Color.BLACK);
                break;
            case NOSABE:
                setEnabledRB(false);
                valor.setEnabled(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLUE);
                break;
            default:
                setEnabledRB(true);
                setEnabledET();
                resp.setTextColor(Color.BLUE);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
                break;
        }
    }

    public void setVisible (View v, boolean sw) {
        if(sw) {
            v.setVisibility(VISIBLE);
        } else {
            v.setVisibility(INVISIBLE);
        }
    }
}
