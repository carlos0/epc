package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Map;

import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class CerradaConsulta extends PreguntaView {
    protected Map<Integer, String> opciones, opcionesConsulta;
    protected RadioGroup radioGroup;
    protected ArrayList<Integer> filtro;
    protected Button resp, noAplica, seNiega, noSabe;
    protected String codEsp;
    protected int buttonsActive;

    public CerradaConsulta(Context context, int id, int idSeccion, String cod, String preg, Map<Integer, String> opciones, Map<Integer, String> opcionesConsulta, int buttonsActive, ArrayList<Integer> filtro, String ayuda, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.opciones = opciones;
        this.opcionesConsulta = opcionesConsulta;
        this.buttonsActive = buttonsActive;
        this.filtro = filtro;
        radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        // CREAMOS EL GRUPO DE RADIO BUTTONS
        RadioButton rb;
        for (Integer key : opcionesConsulta.keySet()) {
            rb = new RadioButton(context);
            radioGroup.addView(rb);
            String opcion = opcionesConsulta.get(key);
            String[] a = opcion.split("\\|");
            rb.setText(Html.fromHtml(a[1]));
            rb.setTextSize(Parametros.FONT_RESP);
            rb.setId(key);
        }
        for (Integer key : opciones.keySet()) {
            rb = new RadioButton(context);
            radioGroup.addView(rb);
            String opcion = opciones.get(key);
            String[] a = opcion.split("\\|");
            rb.setText(Html.fromHtml(a[1]));
            rb.setTextSize(Parametros.FONT_RESP);
            rb.setId(key);

        }
        addView(radioGroup);

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
                CerradaConsulta.this.estado = Estado.INSERTADO;
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
                CerradaConsulta.this.estado = Estado.NOAPLICA;
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
                CerradaConsulta.this.estado = Estado.SENIEGA;
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
                CerradaConsulta.this.estado = Estado.NOSABE;
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
    }

    public void setEnabledRB(boolean flag) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton child = (RadioButton)radioGroup.getChildAt(i);
            // CONTROL QUE UNICAMENTE FUNCIONARA PARA LA PREGUNTA BA03_09
            if (filtro == null) {
                child.setEnabled(flag);
            }
            if (child.isChecked() && !flag ) {
                child.setChecked(false);
            }
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
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    return "-1";
                } else {
                    String codigo;
                    if (opciones.get(radioGroup.getCheckedRadioButtonId()) == null) {
                        codigo= opcionesConsulta.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
                    }else{
                        codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
                    }
                    return codigo.trim();
                }
        }
    }

//    @Override
//    public void setIdResp(long id) {
//        for (int i = 0; i < radioGroup.getChildCount(); i++) {
//            RadioButton child = (RadioButton)radioGroup.getChildAt(i);
//            if (child.getId() == id) {
//                if (child.isEnabled()) {
//                    child.setChecked(true);
//                }
//                break;
//            }
//        }
//    }

    @Override
    public void setResp(String value) {

    }

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
                    String[] sel;
                    if (opciones.get(radioGroup.getCheckedRadioButtonId()) == null) {
                        sel = opcionesConsulta.get(radioGroup.getCheckedRadioButtonId()).split("\\|");
                    }else{
                        sel = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|");
                    }
                    return sel[1];
                }
        }
    }

    @Override
    public void setFocus() { }

    @Override
    public void setEstado(Estado value) {
        super.setEstado(value);
        switch (estado) {
            case NOAPLICA:
                setEnabledRB(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLUE);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
                break;
            case SENIEGA:
                setEnabledRB(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLUE);
                noSabe.setTextColor(Color.BLACK);
                break;
            case NOSABE:
                setEnabledRB(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLUE);
                break;
            default:
                setEnabledRB(true);
                resp.setTextColor(Color.BLUE);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLACK);
                break;
        }
    }
}