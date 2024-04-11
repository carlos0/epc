package bo.gob.ine.naci.epc.preguntas2;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;
import java.util.Map;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class ValorTipoGastos extends PreguntaView {
    protected EditText valor;

    protected Context context;
    protected int posicion;
    protected int idSeccion;
    protected String cod;
    protected Boolean evaluar;
    protected ImageButton btnNext;

    protected Map<Integer, String> opciones;
//    protected List<String> opciones;
    protected TextView txtConvertido, txt1, txt2;
    protected RadioGroup radioGroup;
    protected Spinner spinner;
    protected Button resp, noAplica, seNiega, noSabe;
    protected int buttonsActive;
    protected String codEsp;
    protected Float resultado = 0f;
    protected Float conversor = 0f;
    protected String medida;
    protected boolean hayRespuesta = false;

    public ValorTipoGastos(final Context context, final int posicion, final int id, int idSeccion, String cod, String preg, final Map<Integer, String> opciones, int maxLength, int buttonsActive, String ayuda, final String codEsp, final Boolean evaluar, final Boolean mostrarSeccion, String observacion) {
//    public ValorTipoGastos(final Context context, final int posicion, final int id, int idSeccion, String cod, String preg, final List<String> opciones, int maxLength, int buttonsActive, String ayuda, final String codEsp, final Boolean evaluar, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.opciones = opciones;
        this.buttonsActive = buttonsActive;
        this.codEsp = codEsp;

        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;
        btnNext = new ImageButton(getContext());
        btnNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_next_preg));
        btnNext.setBackground(null);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (evaluar) {
//                    hayRespuesta = false;
                    try {
                        String codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
                        String factor = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[1].trim();

                        Float dato;
                        String cantidad = valor.getText().toString();
                        if (cantidad.equals("")) {
                            dato = 0.0f;
                            txt1.setText("DEBE COLOCAR UNA CANTIDAD");
                        } else {
                            txt1.setText("El valor convertido es:");
                            dato = Float.parseFloat(valor.getText().toString());
                        }
                        Log.d("medida", medida);
                        switch (medida) {
                            case "Kilo":
                                switch (factor) {
                                    case "Quintal":
                                        conversor = 45.360f;
                                        break;
                                    case "Arroba":
                                        conversor = 11.340f;
                                        break;
                                    case "Cuartilla":
                                        conversor = 2.835f;
                                        break;
                                    case "Kilo":
                                        conversor = 1f;
                                        break;
                                    case "Libra":
                                        conversor = 0.454f;
                                        break;
                                    case "Onza":
                                        conversor = 0.028f;
                                        break;
                                    case "Gramo":
                                        conversor = 0.001f;
                                        break;
                                }
                                break;
                            case "Litro":
                                switch (factor) {
                                    case "Litro":
                                        conversor = 1f;
                                        break;
                                    case "Cc":
                                    case "Mililitro":
                                        conversor = 0.001f;
                                        break;
                                }
                                break;
                            case "Libra":
                                switch (factor) {
                                    case "Quintal":
                                        conversor = 100f;
                                        break;
                                    case "Arroba":
                                        conversor = 25f;
                                        break;
                                    case "Cuartilla":
                                        conversor = 6.250f;
                                        break;
                                    case "Kilo":
                                        conversor = 2.205f;
                                        break;
                                    case "Libra":
                                        conversor = 1f;
                                        break;
                                    case "Onza":
                                        conversor = 0.062f;
                                        break;
                                    case "Gramo":
                                        conversor = 0.002f;
                                        break;
                                }
                                break;
                            case "Gramo":
                                switch (factor) {
                                    case "Quintal":
                                        conversor = 46909.30f;
                                        break;
                                    case "Arroba":
                                        conversor = 11340f;
                                        break;
                                    case "Cuartilla":
                                        conversor = 2835.96f;
                                        break;
                                    case "Kilo":
                                        conversor = 1000f;
                                        break;
                                    case "Libra":
                                        conversor = 453.59f;
                                        break;
                                    case "Onza":
                                        conversor = 28.35f;
                                        break;
                                    case "Gramo":
                                        conversor = 1f;
                                        break;
                                }
                                break;

                        }
                        resultado = dato * conversor;
                        String val = String.format(Locale.US, "%.3f", resultado);
                        txtConvertido.setText(val);
                        txt2.setText(medida + "s");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (!valor.getText().toString().trim().equals("") && radioGroup.getCheckedRadioButtonId() != -1) {
                        FragmentEncuesta.actualiza(id);
                    }

                }
            }
        });
        LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsButton.gravity = Gravity.RIGHT;
        btnNext.setLayoutParams(layoutParamsButton);

        valor = new EditText(context);
        valor.setSingleLine();
        valor.setTextSize(Parametros.FONT_RESP);
        valor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        if (maxLength > 0) {
            InputFilter[] fArray = new InputFilter[2];
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            fArray[1] = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                    if (source != null && Parametros.BLOCK_CHARACTER_SET.contains("" + source)) {
                        return "";
                    }
                    return null;
                }
            };
            valor.setFilters(fArray);
        }
        valor.setText("");
        valor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(evaluar) {
//                    FragmentEncuesta.actualiza(id);
//                }
            }
        });
        valor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(evaluar) {
//                    FragmentEncuesta.actualiza(id);
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        valor.setEnabled(false);

//        addView(valor);
        txtConvertido = new TextView(context);
        txtConvertido.setTextSize(Parametros.FONT_RESP);
        txtConvertido.setTextColor(Color.BLUE);
        txtConvertido.setText("");
        txt1 = new TextView(context);
        txt1.setTextSize(Parametros.FONT_RESP);
        txt1.setTextColor(Color.BLUE);
        txt1.setText("El valor convertido es:");
        txt2 = new TextView(context);
        txt2.setTextSize(Parametros.FONT_RESP);
        txt2.setTextColor(Color.BLUE);
        txt2.setText("");

//        spinner = new Spinner(context);
//
//// Crea un ArrayAdapter y enlázalo al Spinner
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, (List) opciones);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//
//// Configura un listener de selección (similar a un OnClickListener)
//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
////                String opcionSeleccionada = opciones[position];
//
//                hayRespuesta = false;
//
//                String codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
//                String factor = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[1].trim();
//
//                Float dato;
//                String cantidad = valor.getText().toString();
//                if (cantidad.equals("")) {
//                    dato = 0.0f;
//                    txt1.setText("DEBE COLOCAR UNA CANTIDAD");
//                } else {
//                    txt1.setText("El valor convertido es:");
//                    dato = Float.parseFloat(valor.getText().toString());
//                }
//
//                switch (medida) {
//                    case "Kilo":
//                        switch (factor) {
//                            case "Quintal":
//                                conversor = 45.360f;
//                                break;
//                            case "Arroba":
//                                conversor = 11.340f;
//                                break;
//                            case "Cuartilla":
//                                conversor = 2.835f;
//                                break;
//                            case "Kilo":
//                                conversor = 1f;
//                                break;
//                            case "Libra":
//                                conversor = 0.454f;
//                                break;
//                            case "Onza":
//                                conversor = 0.028f;
//                                break;
//                            case "Gramo":
//                                conversor = 0.001f;
//                                break;
//                        }
//                        break;
//                    case "Litro":
//                        switch (factor) {
//                            case "Litro":
//                                conversor = 1f;
//                                break;
//                            case "Cc":
//                            case "Mililitro":
//                                conversor = 0.001f;
//                                break;
//                        }
//                        break;
//                    case "Libra":
//                        switch (factor) {
//                            case "Quintal":
//                                conversor = 100f;
//                                break;
//                            case "Arroba":
//                                conversor = 25f;
//                                break;
//                            case "Cuartilla":
//                                conversor = 6.250f;
//                                break;
//                            case "Kilo":
//                                conversor = 2.205f;
//                                break;
//                            case "Libra":
//                                conversor = 1f;
//                                break;
//                            case "Onza":
//                                conversor = 0.062f;
//                                break;
//                            case "Gramo":
//                                conversor = 0.002f;
//                                break;
//                        }
//                        break;
//                    case "Gramo":
//                        switch (factor) {
//                            case "Quintal":
//                                conversor = 46909.30f;
//                                break;
//                            case "Arroba":
//                                conversor = 11340f;
//                                break;
//                            case "Cuartilla":
//                                conversor = 2835.96f;
//                                break;
//                            case "Kilo":
//                                conversor = 1000f;
//                                break;
//                            case "Libra":
//                                conversor = 453.59f;
//                                break;
//                            case "Onza":
//                                conversor = 28.35f;
//                                break;
//                            case "Gramo":
//                                conversor = 1f;
//                                break;
//                        }
//                        break;
//
//                }
//                resultado = dato * conversor;
//                String val = String.format(Locale.US, "%.3f", resultado);
//                txtConvertido.setText(val);
//                txt2.setText(medida + "s");
//
//                if (evaluar && !valor.getText().toString().trim().equals("")) {
//                    FragmentEncuesta.actualiza(id);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parentView) {
//                // Si no se selecciona nada
//                parentView.getid
//            }
//        });

        radioGroup = new RadioGroup(context);
        radioGroup.setOrientation(RadioGroup.VERTICAL);

        medida = opciones.get(1).split("\\|")[1].trim();

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
                    if (((RadioButton) v).isChecked()) {
                        hayRespuesta = false;

                        String codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
                        String factor = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[1].trim();

                        Float dato;
                        String cantidad = valor.getText().toString();
                        if (cantidad.equals("")) {
                            dato = 0.0f;
                            txt1.setText("DEBE COLOCAR UNA CANTIDAD");
                        } else {
                            txt1.setText("El valor convertido es:");
                            dato = Float.parseFloat(valor.getText().toString());
                        }

                        switch (medida) {
                            case "Kilo":
                                switch (factor) {
                                    case "Quintal":
                                        conversor = 45.360f;
                                        break;
                                    case "Arroba":
                                        conversor = 11.340f;
                                        break;
                                    case "Cuartilla":
                                        conversor = 2.835f;
                                        break;
                                    case "Kilo":
                                        conversor = 1f;
                                        break;
                                    case "Libra":
                                        conversor = 0.454f;
                                        break;
                                    case "Onza":
                                        conversor = 0.028f;
                                        break;
                                    case "Gramo":
                                        conversor = 0.001f;
                                        break;
                                }
                                break;
                            case "Litro":
                                switch (factor) {
                                    case "Litro":
                                        conversor = 1f;
                                        break;
                                    case "Cc":
                                    case "Mililitro":
                                        conversor = 0.001f;
                                        break;
                                }
                                break;
                            case "Libra":
                                switch (factor) {
                                    case "Quintal":
                                        conversor = 100f;
                                        break;
                                    case "Arroba":
                                        conversor = 25f;
                                        break;
                                    case "Cuartilla":
                                        conversor = 6.250f;
                                        break;
                                    case "Kilo":
                                        conversor = 2.205f;
                                        break;
                                    case "Libra":
                                        conversor = 1f;
                                        break;
                                    case "Onza":
                                        conversor = 0.062f;
                                        break;
                                    case "Gramo":
                                        conversor = 0.002f;
                                        break;
                                }
                                break;
                            case "Gramo":
                                switch (factor) {
                                    case "Quintal":
                                        conversor = 46909.30f;
                                        break;
                                    case "Arroba":
                                        conversor = 11340f;
                                        break;
                                    case "Cuartilla":
                                        conversor = 2835.96f;
                                        break;
                                    case "Kilo":
                                        conversor = 1000f;
                                        break;
                                    case "Libra":
                                        conversor = 453.59f;
                                        break;
                                    case "Onza":
                                        conversor = 28.35f;
                                        break;
                                    case "Gramo":
                                        conversor = 1f;
                                        break;
                                }
                                break;

                        }
                        resultado = dato * conversor;
                        String val = String.format(Locale.US, "%.3f", resultado);
                        txtConvertido.setText(val);
                        txt2.setText(medida + "s");
                        if (evaluar && !valor.getText().toString().trim().equals("")) {
                            FragmentEncuesta.actualiza(id);
                        }
                    }
                }
            });
        }
        if (opciones.size() == 1) {
            ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
        }
        if (idSeccion == 166) {
            botones.addView(btnNext);
            respuesta.addView(valor);
            respuesta.addView(radioGroup);
//            respuesta.addView(spinner);
            respuesta.addView(txt1);
            respuesta.addView(txtConvertido);
            respuesta.addView(txt2);
        } else {
            addView(btnNext);
            addView(valor);
            addView(radioGroup);
            addView(txt1);
            addView(txtConvertido);
            addView(txt2);
        }

        requestFocus();
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

                if (radioGroup.getCheckedRadioButtonId() == -1 || valor.getText().toString().trim().length() == 0) {
                    return "-1";
                } else {
//                    String val = String.format(Locale.US, "%.2f", Float.parseFloat(valor.getText().toString()) * Float.parseFloat(opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[2]));
//                    String val = String.format(Locale.US, "%.3f", Float.parseFloat(valor.getText().toString()) * Float.parseFloat(opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[2]));
//                    String val = String.format(Locale.US, "%.3f",Float.parseFloat(txtConvertido.getText().toString()));
//                    if(!hayRespuesta){
                    try{
                        String codigo = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0];
                        String factor = opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[1].trim();

                        Float dato;
                        String cantidad = valor.getText().toString();
                        if(cantidad.equals("")){
                            dato = 0.0f;
//                            txt1.setText("DEBE COLOCAR UNA CANTIDAD");
                        }else {
//                            txt1.setText("El valor convertido es:");
                            dato = Float.parseFloat(valor.getText().toString());
                        }

                        switch (medida){
                            case "Kilo":
                                switch (factor){
                                    case "Quintal":
                                        conversor = 45.360f;
                                        break;
                                    case "Arroba":
                                        conversor = 11.340f;
                                        break;
                                    case "Cuartilla":
                                        conversor = 2.835f;
                                        break;
                                    case "Kilo":
                                        conversor = 1f;
                                        break;
                                    case "Libra":
                                        conversor = 0.454f;
                                        break;
                                    case "Onza":
                                        conversor = 0.028f;
                                        break;
                                    case "Gramo":
                                        conversor = 0.001f;
                                        break;
                                }
                                break;
                            case "Litro":
                                switch (factor) {
                                    case "Litro":
                                        conversor = 1f;
                                        break;
                                    case "Cc":
                                    case "Mililitro":
                                        conversor = 0.001f;
                                        break;
                                }
                                break;
                            case "Libra":
                                switch (factor){
                                    case "Quintal":
                                        conversor = 100f;
                                        break;
                                    case "Arroba":
                                        conversor = 25f;
                                        break;
                                    case "Cuartilla":
                                        conversor = 6.250f;
                                        break;
                                    case "Kilo":
                                        conversor = 2.205f;
                                        break;
                                    case "Libra":
                                        conversor = 1f;
                                        break;
                                    case "Onza":
                                        conversor = 0.062f;
                                        break;
                                    case "Gramo":
                                        conversor = 0.002f;
                                        break;
                                }
                                break;
                            case "Gramo":
                                switch (factor){
                                    case "Quintal":
                                        conversor = 46909.30f;
                                        break;
                                    case "Arroba":
                                        conversor = 11340f;
                                        break;
                                    case "Cuartilla":
                                        conversor = 2835.96f;
                                        break;
                                    case "Kilo":
                                        conversor = 1000f;
                                        break;
                                    case "Libra":
                                        conversor = 453.59f;
                                        break;
                                    case "Onza":
                                        conversor = 28.35f;
                                        break;
                                    case "Gramo":
                                        conversor = 1f;
                                        break;
                                }
                                break;

                        }
                        resultado = dato * conversor;
                        String val = String.format(Locale.US, "%.3f",resultado);
                        try{
                            txtConvertido.setText(val);
                            txt2.setText(medida + "s");
                        }catch (Exception e){

                        }

                    }catch(Exception e){
//                        resultado=0f;
                        e.printStackTrace();
                    }

//                    }
                    return String.valueOf(resultado);
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
//                    return valor.getText() + ":" + opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[1];
                    String val = String.format(Locale.US, "%.3f", Float.parseFloat(valor.getText().toString()) * Float.parseFloat(opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[2]));
                    return valor.getText() + ":" + opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[0] + ":" + opciones.get(radioGroup.getCheckedRadioButtonId()).split("\\|")[1];
                }
    }

    @Override
    public void setResp(String value) {
        String[] val = value.split(":");
        if (val.length == 3) {
            this.valor.setText(val[0]);
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                RadioButton child = (RadioButton) radioGroup.getChildAt(i);
                if (child.getId() == Integer.valueOf(val[1])){
                    child.setChecked(true);
                    hayRespuesta=true;
                    break;
                }
            }
            txt2.setText(medida + "s");
        }
    }

    @Override
    public void setCodResp(String value) {
        this.resultado = Float.valueOf(value);
        this.txtConvertido.setText(value);
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
