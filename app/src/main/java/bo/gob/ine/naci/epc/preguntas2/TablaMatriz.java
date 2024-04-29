package bo.gob.ine.naci.epc.preguntas2;

import static com.google.android.material.textfield.TextInputLayout.BOX_BACKGROUND_FILLED;
import static com.google.android.material.textfield.TextInputLayout.END_ICON_NONE;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.adaptadores.BoletaAdapterRecycler;
import bo.gob.ine.naci.epc.adaptadores.TablaMatrizAdapterRecycler;
import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class TablaMatriz extends PreguntaView implements View.OnClickListener {
//    protected TextInputLayout contenedor;
//    protected TextInputEditText textbox;
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
    LinearLayout linearLayout;

    int fila;
    RecyclerView list;

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

        linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        list = new RecyclerView(getContext());
        list.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        ));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        Log.d("RECYCLER", "-----------");

        cargarListado();

        btnAdd = new ImageButton(getContext());
        btnAdd.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_person));
        btnAdd.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        btnAdd.setBackground(null);
        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                FragmentEncuesta fragmentEncuesta = new FragmentEncuesta();
                fila = Encuesta.ultimaFilaBucle(new IdInformante(idAsignacion,correlativo), Parametros.ID_PREG_BUCLE);
                Log.d("FILA", String.valueOf(fila));
                FragmentEncuesta.dibujaTablaMatriz(fila + 1);
            }
        });

        linearLayout.addView(list);
        linearLayout.addView(btnAdd);

        addView(linearLayout);
        setBotones(btnNext, false);

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

    public void cargarListado() {

        try {
            Encuesta encuesta = new Encuesta();
            valores = encuesta.obtenerListadoTablaMatriz(idAsignacion, correlativo, id);

            adapter = new TablaMatrizAdapterRecycler(context, valores);

            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Map<String, Object> val = valores.get(list.getChildAdapterPosition(v));
                        Log.d("FILA_CLICK", val.get("fila").toString());
                        FragmentEncuesta.dibujaTablaMatriz(Integer.parseInt(val.get("fila").toString()));
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
        int countFila = Encuesta.cuentaFilaBucle(new IdInformante(idAsignacion,correlativo), Parametros.ID_PREG_BUCLE);
        Log.d("FILA", String.valueOf(countFila));
        return String.valueOf(countFila);
    }

    @Override
    public String getResp() {
        int countFila = Encuesta.cuentaFilaBucle(new IdInformante(idAsignacion,correlativo), Parametros.ID_PREG_BUCLE);
        Log.d("FILA", String.valueOf(countFila));
        return String.valueOf(countFila);
    }

    @Override
    public void setCodResp(String value) {
        ToggleButton botonActive;
        if(idOpciones.contains(value)){
            seleccion = value;
            botonActive = (ToggleButton) guardaBotones.get(value);
            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));

            boton = botonActive;

        } else {

            for(String opc : idOpciones) {
                botonActive = (ToggleButton) guardaBotones.get(opc);
                botonActive.setChecked(false);
                botonActive.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            }
        }
    }

    @Override
    public void setResp(String value) {

    }

    @Override
    public void setFocus() {
        if (buttonsActive != null) {
            buttons.requestFocus();
        } else {
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

            seleccion = "";
            seleccionText = "";
            botonActive.setChecked(false);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            fArray[0] = new InputFilter.LengthFilter(maxLength);

            boton = null;
            Log.d("REVISION300", seleccion);
            if(evaluar) {
                FragmentEncuesta.actualiza(id);
            }
        } else if(idOpciones.contains(seleccion)){
            Log.d("REVISION200", "ACTS2");
            boton.setChecked(false);
            boton.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));

            seleccion = a[0];
            seleccionText = a[1];

            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));

            boton = botonActive;
            Log.d("REVISION400", seleccion);
            if(evaluar) {
                FragmentEncuesta.actualiza(id);
            }
        } else {//SI NO EXISTIERA NADA en @seleccion
            Log.d("REVISION200", "ACTS3");
            if(a[1].length() > maxLength) {

            }

            seleccion = a[0];
            seleccionText = a[1];

            botonActive.setChecked(true);
            botonActive.setTextColor(getContext().getResources().getColor(R.color.colorAccent));

            boton = botonActive;
            Log.d("REVISION400", seleccion);
            if(evaluar) {
                FragmentEncuesta.actualiza(id);
            }
        }
    }


}
