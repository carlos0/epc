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

import java.util.ArrayList;
import java.util.Map;

import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class CerradaMatriz extends PreguntaView {
    protected Map<Integer, String> opciones;
    protected RadioGroup radioGroup;
    protected ArrayList<Integer> filtro;
    protected Button resp, noAplica, seNiega, noSabe;
    protected EditText editText, editText2;
    protected String codEsp;
    protected int buttonsActive;

    public CerradaMatriz(Context context, int id, int idSeccion, final String cod, String preg, final Map<Integer, String> opciones, final String codEsp, int maxLength, int buttonsActive, String ayuda) {
        super(context, id, idSeccion, cod, preg, ayuda);
        this.opciones = opciones;
        this.codEsp = codEsp;
        this.buttonsActive = buttonsActive;
        radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        editText = new EditText(context);
        editText.setSingleLine();
        editText.setTextSize(Parametros.FONT_RESP);
        editText.setTextSize(Parametros.FONT_RESP);
        editText.setHint("Especifique");

        if (maxLength > 0) {
            InputFilter[] fArray = new InputFilter[2] ;
            fArray[0] = new InputFilter.LengthFilter(512);
            fArray[1] = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                    if(source != null && Parametros.BLOCK_CHARACTER_SET.contains(""+source)){
                        return "";
                    }
                    return null;
                }
            };
            editText.setFilters(fArray);
        }

        editText2 = new EditText(context);
        editText2.setSingleLine();
        editText2.setTextSize(Parametros.FONT_RESP);
        editText2.setTextSize(Parametros.FONT_RESP);
        editText2.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText2.setHint("Monto");

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
            editText2.setFilters(fArray);
        }
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
            rb.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(((RadioButton)v).isChecked()){
                        String codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
                        if(codEsp.startsWith("-")){
                            int cod = Integer.parseInt(String.valueOf(codigo.charAt(1)));
                            int codes = Integer.parseInt(String.valueOf(codEsp.charAt(1)));
                            if(cod != codes){
                                editText.setVisibility(View.VISIBLE);
                                editText2.setVisibility(View.GONE);
                            }else {
                                editText.setVisibility(View.GONE);
                                editText2.setVisibility(View.GONE);
                            }
                        }else {
                            if (Integer.parseInt(codEsp) > opciones.size()) {
                                String primero = String.valueOf(codEsp.charAt(0));
                                String segundo = String.valueOf(codEsp.charAt(1));
                                if(primero == segundo){
                                    if(String.valueOf(codigo.charAt(1)).equals(primero)){
                                        editText.setVisibility(View.VISIBLE);
                                        editText2.setVisibility(View.GONE);
                                    }else{
                                        editText.setVisibility(View.GONE);
                                        editText2.setVisibility(View.GONE);
                                    }
                                }else{
                                    if (String.valueOf(codigo.charAt(1)).equals(primero) || String.valueOf(codigo.charAt(1)).equals(segundo)) {
                                        editText.setVisibility(View.VISIBLE);
                                        editText2.setVisibility(View.GONE);
                                    } else {
                                        editText.setVisibility(View.GONE);
                                        editText2.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                if (codigo.equals(codEsp)) {
                                    editText.setVisibility(View.VISIBLE);
                                    editText2.setVisibility(View.VISIBLE);
                                } else {
                                    editText.setVisibility(View.GONE);
                                    editText2.setVisibility(View.GONE);
                                }
                            }
                        }
                    }else {
                        editText.setVisibility(View.GONE);
                        editText2.setVisibility(View.GONE);
                    }

                }
            });
        }
        addView(radioGroup);

        if (editText != null) {
            addView(editText);
            addView(editText2);
            editText.setVisibility(View.GONE);
            editText2.setVisibility(View.GONE);


//            editText.setVisibility(View.VISIBLE);
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
                CerradaMatriz.this.estado = Estado.INSERTADO;
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
                CerradaMatriz.this.estado = Estado.NOAPLICA;
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
                CerradaMatriz.this.estado = Estado.SENIEGA;
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
                CerradaMatriz.this.estado = Estado.NOSABE;
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
                    if (codEsp.startsWith("-")) {
                        int cod = Integer.parseInt(String.valueOf(codigo.charAt(1)));
                        int codes = Integer.parseInt(String.valueOf(codEsp.charAt(1)));
                        if(cod != codes){
                            if (editText.getText().toString().trim().length() == 0) {
                                return "-1";
                            } else {
                                return codigo.trim();
                            }
                        } else {
                            return codigo.trim();
                        }
                    } else {
                        if (Integer.parseInt(codEsp) > opciones.size()) {
                            String primero = String.valueOf(codEsp.charAt(0));
                            String segundo = String.valueOf(codEsp.charAt(1));
                            if (String.valueOf(codigo.charAt(1)).equals(primero) || String.valueOf(codigo.charAt(1)).equals(segundo)) {
                                if (editText.getText().toString().trim().length() == 0) {
                                    return "-1";
                                } else {
                                    return codigo.trim();
                                }
                            } else {
                                return codigo.trim();
                            }
                        } else {
                            if (codigo.equals(codEsp)) {
                                if (editText.getText().toString().trim().length() == 0 || editText2.getText().toString().trim().length() == 0) {
                                    return "-1";
                                } else {
                                    return codigo.trim() + "," + editText2.getText().toString().trim();
                                }
                            } else {
                                if(editText2.getVisibility()== View.GONE){
                                    return codigo.trim() + "," + 0;
                                }else {
                                    return codigo.trim();
                                }
                            }
                        }
                    }

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
        if (editText != null) {
            String codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
            if (codEsp.startsWith("-")) {
                int cod = Integer.parseInt(String.valueOf(codigo.charAt(1)));
                int codes = Integer.parseInt(String.valueOf(codEsp.charAt(1)));
                if(cod != codes){
                    editText.setText(value);
                    editText.setVisibility(View.VISIBLE);
                    editText2.setVisibility(View.GONE);
                } else {
                    editText.setVisibility(View.GONE);
                    editText2.setVisibility(View.GONE);
                }
            } else {
                if (Integer.parseInt(codEsp) > opciones.size()) {
                    String primero = String.valueOf(codEsp.charAt(0));
                    String segundo = String.valueOf(codEsp.charAt(1));
                    if (String.valueOf(codigo.charAt(1)).equals(primero) || String.valueOf(codigo.charAt(1)).equals(segundo)) {
                        editText.setText(value);
                        editText.setVisibility(View.VISIBLE);
                        editText2.setVisibility(View.GONE);
                    }else {
                        editText.setVisibility(View.GONE);
                        editText2.setVisibility(View.GONE);
                    }
                } else {
                    if (codigo.equals(codEsp)) {
                        editText.setVisibility(View.VISIBLE);
                        editText2.setVisibility(View.VISIBLE);
                        if(value.contains(":")){
                            String[] val = value.split(":");
                            editText.setText(val[1]);
                            editText2.setText(val[0]);
                            editText.setVisibility(View.VISIBLE);
                            editText2.setVisibility(View.VISIBLE);
                        }else{
                            editText.setText(value);
                            editText.setVisibility(View.VISIBLE);
                            editText2.setVisibility(View.GONE);
                        }
                    }else{
                        editText.setVisibility(View.GONE);
                        editText2.setVisibility(View.GONE);
                    }
                }
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
                    String respuesta;
                    String[] sel = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|");
                    if (codEsp.startsWith("-")) {
                        int cod = Integer.parseInt(String.valueOf(sel[0].charAt(1)));
                        int codes = Integer.parseInt(String.valueOf(codEsp.charAt(1)));
                        if(cod != codes){
                            respuesta = editText.getText().toString();
                            return respuesta;
                        } else {
                            return sel[1];
                        }
                    } else {
                        if (Integer.parseInt(codEsp) > opciones.size()) {
                            String primero = String.valueOf(codEsp.charAt(0));
                            String segundo = String.valueOf(codEsp.charAt(1));
                            if (String.valueOf(sel[0].charAt(1)).equals(primero) || String.valueOf(sel[0].charAt(1)).equals(segundo)) {
                                respuesta = editText.getText().toString();
                                return respuesta;
                            } else {
                                return sel[1];
                            }
                        } else {
                            if (sel[0].equals(codEsp)) {
                                if(editText2.getVisibility() == View.GONE){
                                    respuesta = editText.getText().toString();
                                }else {
                                    //editText = Especifique
                                    //editText2 = Monto
                                    respuesta =  editText2.getText().toString() + ":" + editText.getText().toString();
                                }
                                return respuesta;
//                        return editText.getText().toString();
                            } else {
                                return 0 + ":" + sel[1];
                            }
                        }
                    }

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