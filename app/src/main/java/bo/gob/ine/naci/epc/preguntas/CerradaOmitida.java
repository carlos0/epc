package bo.gob.ine.naci.epc.preguntas;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Map;

import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.Seleccion;
import bo.gob.ine.naci.epc.herramientas.Parametros;


public class CerradaOmitida extends PreguntaView {
    protected Map<Integer, String> opciones;
    protected RadioGroup radioGroup;
    protected Button resp, noAplica, seNiega, noSabe;
    protected EditText editText;
    protected String codEsp;
    protected int idUpm;
    protected int buttonsActive;

    public CerradaOmitida(Context context, int id, int idSeccion, String cod, String preg, Map<Integer, String> opciones, String codEsp, int buttonsActive, String ayuda, int idUpm) {
        super(context, id, idSeccion, cod, preg, ayuda);
        this.opciones = opciones;
        this.codEsp = codEsp;
        this.idUpm = idUpm;
        this.buttonsActive = buttonsActive;
        radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setFocusable(true);

        // CREAMOS EL GRUPO DE RADIO BUTTONS
        RadioButton rb;
        for (Integer key : opciones.keySet()) {
            rb = new RadioButton(context);
            radioGroup.addView(rb);
            String opcion = opciones.get(key);
            String[] a = opcion.split("\\|");
            rb.setText(Html.fromHtml(a[1]));
            rb.setTextSize(Parametros.FONT_RESP);
            rb.setId(key);
            if ( a[0].equals(codEsp) ) {
                editText = new EditText(context);
                editText.setSingleLine();
                editText.setTextSize(Parametros.FONT_RESP);
                editText.setTextSize(Parametros.FONT_RESP);
            }
        }
        if (opciones.size() == 2) {
//            ((RadioButton)radioGroup.getChildAt(1)).setChecked(true);
            if(Seleccion.hasSelected(idUpm)==false){
                ((RadioButton)radioGroup.getChildAt(1)).setChecked(true);
            }else{
                ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
            }
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                ((RadioButton) radioGroup.getChildAt(i)).setEnabled(false);
            }

//            if(Seleccion.hasSelected(idUpm)){
//                ((RadioButton)radioGroup.getChildAt(0)).setChecked(true);
//                for (int i = 0; i < radioGroup.getChildCount(); i++) {
//                    ((RadioButton) radioGroup.getChildAt(i)).setEnabled(false);
//                }
//            }else{
//                ((RadioButton)radioGroup.getChildAt(1)).setChecked(true);
//                for (int i = 0; i < radioGroup.getChildCount(); i++) {
//                    ((RadioButton) radioGroup.getChildAt(i)).setEnabled(false);
//                }
//            }
        }
        addView(radioGroup);
        if (editText != null) {
            addView(editText);
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
                CerradaOmitida.this.estado = Estado.INSERTADO;
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
                CerradaOmitida.this.estado = Estado.NOAPLICA;
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
                CerradaOmitida.this.estado = Estado.SENIEGA;
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
                CerradaOmitida.this.estado = Estado.NOSABE;
                setEnabledRB(false);
                resp.setTextColor(Color.BLACK);
                noAplica.setTextColor(Color.BLACK);
                seNiega.setTextColor(Color.BLACK);
                noSabe.setTextColor(Color.BLUE);
            }
        });

        if ( buttonsActive != 0 ) {
//            addView(buttons);
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
            if (child.isChecked() && !flag) {
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
                    String codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
                    if (codigo.equals(codEsp)) {
                        if (editText.getText().toString().trim().length() == 0) {
                            return "-1";
                        } else {
                            return codigo.trim();
                        }
                    } else {
                        return codigo.trim();
                    }
                }
        }
    }

//    @Override
//    public void setIdResp(long id) {
//        for (int i = 0; i < radioGroup.getChildCount(); i++) {
//            RadioButton child = (RadioButton) radioGroup.getChildAt(i);
//            if (child.getId() == id) {
//                child.setChecked(true);
//            }
//            ((RadioButton) radioGroup.getChildAt(i)).setEnabled(false);
//        }
//    }



//    @Override
//    public void setIdResp(long id) {
//        for (int i = 0; i < radioGroup.getChildCount(); i++) {
//            RadioButton child = (RadioButton)radioGroup.getChildAt(i);
//            if (child.getId() == id) {
////                if (child.isEnabled()) {
//                    child.setChecked(true);
////                }
//                for (int j = 0; i < radioGroup.getChildCount(); j++) {
//                    ((RadioButton) radioGroup.getChildAt(j)).setEnabled(false);
//                }
//                break;
//            }
//        }
//    }

    @Override
    public void setResp(String value) {
        if (editText != null) {
            String codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
            if (codigo.equals(codEsp)) {
                editText.setText(value);
            }
        }
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
                    String[] sel = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|");
                    if (sel[0].equals(codEsp)) {
                        return editText.getText().toString();
                    } else {
                        return sel[1];
                    }
                }
        }
    }

    @Override
    public void setFocus() { radioGroup.requestFocus();}

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