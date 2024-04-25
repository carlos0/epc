package bo.gob.ine.naci.epc.preguntas2;

import static com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_FILLED;
import static com.google.android.material.textfield.TextInputLayout.END_ICON_NONE;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bo.gob.ine.naci.epc.BottomSheet;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.adaptadores.BoletaAdapterRecycler;
import bo.gob.ine.naci.epc.adaptadores.TablaMatrizAdapterRecycler;
import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class TablaMatriz extends PreguntaView implements View.OnClickListener {
    protected TextInputLayout contenedor;
    protected TextInputEditText textbox;
    protected ImageButton btnNext;
    protected ImageButton btnAdd;

    protected Context context;
    protected int posicion;
    protected int idSeccion;
    protected String cod;
    protected Boolean evaluar;

    protected ToggleButton resp;
    protected Map<Integer, String> buttonsActive;
    protected List<String> btnOpciones = new ArrayList<String>();
    protected List<String> idOpciones = new ArrayList<String>();
    protected String seleccion = "";
    protected String seleccionText = "";
    protected ToggleButton boton;
    protected Map<String, View> guardaBotones = new HashMap<>();
    protected Map<Integer, String> guardaIdBotones = new HashMap<>();
    protected int maxLength;
    protected LinearLayout buttons;
    protected InputFilter[] fArray = new InputFilter[3];

    private TablaMatrizAdapterRecycler adapter;
    private ArrayList<Map<String, Object>> valores = new ArrayList<>();
    private int idAsignacion;
    private int correlativo;
    RecyclerView recyclerView;
    LinearLayout linearLayout;

    BottomSheet bottomSheetDialog;


    public TablaMatriz(final Context context, final int posicion, final int id, int idSeccion, final String cod, String preg, int maxLength, final Map<Integer, String> buttonsActive, String ayuda, boolean multiple, final Boolean evaluar, final Boolean mostrarSeccion, String observacion, int idAsignacion, int correlativo) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);
        this.buttonsActive = buttonsActive;
        this.maxLength = maxLength;

        this.cod = cod;
        this.context = context;
        this.posicion = posicion;
        this.idSeccion = idSeccion;
        this.evaluar = evaluar;

        this.idAsignacion = idAsignacion;
        this.correlativo = correlativo;

        btnNext = new ImageButton(getContext());
        btnNext.setImageDrawable(getResources().getDrawable(R.drawable.ic_next_preg));
        btnNext.setBackground(null);
        btnNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(evaluar) {
                    FragmentEncuesta.actualiza(id);
                }
            }
        });
        LayoutParams layoutParamsButton = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParamsButton.gravity = Gravity.RIGHT;
        btnNext.setLayoutParams(layoutParamsButton);


        textbox = new TextInputEditText(getContext());
        textbox.setHintTextColor(getResources().getColor(R.color.colorSecondary_text));
        textbox.setTextColor(getResources().getColor(R.color.colorPrimary_text));
        textbox.setImeOptions(EditorInfo.IME_ACTION_GO);

        textbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if(evaluar) {
                        FragmentEncuesta.actualiza(id);
                    }
                    return true;
                }
                return false;
            }
        });
        addView(textbox);

        linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        ));

        cargarListado(recyclerView);

        btnAdd = new ImageButton(context);
        btnAdd.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_person));
        btnAdd.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        btnAdd.setBackground(null);
        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                    FragmentEncuesta.actualiza(id);

            }
        });
        addView(btnNext);
        linearLayout.addView(recyclerView);
        linearLayout.addView(btnAdd);


        addView(linearLayout);


        buttons = new LinearLayout(context);
        buttons.setFocusable(true);
        buttons.setFocusableInTouchMode(true);
        buttons.setOrientation(HORIZONTAL);
        buttons.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;

        if (buttonsActive != null) {
            for (Integer key : buttonsActive.keySet()) {
                resp = new ToggleButton(context);
                resp.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
                resp.setLayoutParams(layoutParams);
                buttons.addView(resp);
                String opcion = buttonsActive.get(key);
                String[] a = opcion.split("\\|");
                resp.setText(a[1]);
                resp.setTextOff(a[1]);
                resp.setTextOn(a[1]);
                resp.setTextSize(Parametros.FONT_RESP);
                resp.setId(key);
                resp.setOnClickListener(this);
                btnOpciones.add(a[1]);
                idOpciones.add(a[0]);
                guardaBotones.put(a[0],resp);
                guardaIdBotones.put(key, a[0]);
//                if(Usuario.getRol() == Parametros.SUPERVISOR){
//                    if(idSeccion != 449){
//                        resp.setFocusable(false);
//                        resp.setEnabled(false);
//                        resp.setCursorVisible(false);
//                        resp.setKeyListener(null);
//                    }
//                }
            }
            if (idSeccion == 166) {
                respuesta.addView(buttons);
            } else {
                addView(buttons);
            }

        }
    }

    public void cargarListado(RecyclerView list) {

        try {
            Encuesta encuesta = new Encuesta();
            valores = encuesta.obtenerListadoTablaMatriz(idAsignacion, correlativo, id);

            adapter = new TablaMatrizAdapterRecycler(context, valores);

            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String a = valores.get(list.getChildAdapterPosition(v)).get("id_asignacion").toString();
                    try {
                        Map<String, Object> val = valores.get(list.getChildAdapterPosition(v));
//                        irEncuesta2(new IdEncuesta((Integer) val.get("id_asignacion"), (Integer) val.get("correlativo"), 0), 0, false);
//                        listarPreguntas();
//                        finish();
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            });
            if (valores.size() > 0) {
                list.setAdapter(adapter);
            }
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @Override
    public String getCodResp() {
        if (idOpciones.contains(seleccion)) {
            return seleccion;
        } else if (textbox.getText().toString().trim().length() > 0) {
            return String.valueOf(textbox.getText().toString().trim().length());
        } else {
            return "-1";
        }
    }

    @Override
    public String getResp() {
        return (textbox.getText().toString().trim()).replace("\n", " ");
    }

    @Override
    public void setCodResp(String value) {
        ToggleButton botonActive;
        if(idOpciones.contains(value)){
            seleccion = value;
            botonActive = (ToggleButton) guardaBotones.get(value);
            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            contenedor.setEndIconVisible(false);
            boton = botonActive;

            textbox.setEnabled(false);
            contenedor.setEndIconActivated(false);
            contenedor.setEndIconActivated(false);
            contenedor.setEndIconVisible(false);
            contenedor.setBoxBackgroundMode (BOX_BACKGROUND_FILLED);

        } else {
            textbox.setEnabled(true);
            for(String opc : idOpciones) {
                botonActive = (ToggleButton) guardaBotones.get(opc);
                botonActive.setChecked(false);
                botonActive.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    @Override
    public void setResp(String value) {
        if(value.length() > this.maxLength) {
            contenedor.setCounterMaxLength(value.length());
            fArray[0] = new InputFilter.LengthFilter(value.length());
            textbox.setFilters(fArray);
        }
        textbox.setText(value);
    }

    @Override
    public void setFocus() {
        if (buttonsActive != null) {
            buttons.requestFocus();
        } else {
//            textbox.requestFocus();
        }
    }

    @Override
    public void setEstado(Estado value) {
        super.setEstado(value);
    }

    @Override
    public void onClick(View v) {
        Log.d("REVISION501", String.valueOf(seleccion));
        String respuesta = buttonsActive.get(v.getId());
        String[] a = respuesta.split("\\|");
        Log.d("REVISION500", respuesta);
        String botonPresionado = guardaIdBotones.get(v.getId());
        ToggleButton botonActive = (ToggleButton) guardaBotones.get(botonPresionado);

        if(seleccion.equals(a[0])){
            Log.d("REVISION200", "ACTS");
            textbox.setEnabled(true);
            textbox.setText("");
            seleccion = "";
            seleccionText = "";
            botonActive.setChecked(false);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            textbox.setFilters(fArray);
            contenedor.setCounterMaxLength(maxLength);
            contenedor.setEndIconVisible(true);
            boton = null;
            Log.d("REVISION300", seleccion);
            if(evaluar) {
                FragmentEncuesta.actualiza(id);
            }
        } else if(idOpciones.contains(seleccion)){
            Log.d("REVISION200", "ACTS2");
            boton.setChecked(false);
            boton.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            if(a[1].length() > maxLength) {
                contenedor.setCounterMaxLength(a[1].length());
                fArray[0] = new InputFilter.LengthFilter(a[1].length());
                textbox.setFilters(fArray);
            }
            textbox.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            contenedor.setEndIconVisible(false);
            contenedor.setEndIconActivated(false);
            boton = botonActive;
            Log.d("REVISION400", seleccion);
            if(evaluar) {
                FragmentEncuesta.actualiza(id);
            }
        } else {//SI NO EXISTIERA NADA en @seleccion
            Log.d("REVISION200", "ACTS3");
            if(a[1].length() > maxLength) {
                contenedor.setCounterMaxLength(a[1].length());
                fArray[0] = new InputFilter.LengthFilter(a[1].length());
                textbox.setFilters(fArray);
            }
            textbox.setText(a[1]);
            seleccion = a[0];
            seleccionText = a[1];
            textbox.setEnabled(false);
            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));
            contenedor.setEndIconVisible(false);
            boton = botonActive;
            Log.d("REVISION400", seleccion);
            if(evaluar) {
                FragmentEncuesta.actualiza(id);
            }
        }
    }


}
