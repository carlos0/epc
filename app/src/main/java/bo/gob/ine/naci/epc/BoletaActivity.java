package bo.gob.ine.naci.epc;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import bo.gob.ine.naci.epc.adaptadores.AdapterEvents;
import bo.gob.ine.naci.epc.adaptadores.BoletaAdapterRecycler;
import bo.gob.ine.naci.epc.entidades.Asignacion;
import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.EncuestaAnterior;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.InformanteAnterior;
import bo.gob.ine.naci.epc.entidades.Nivel;
import bo.gob.ine.naci.epc.entidades.Proyecto;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityProcess;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class BoletaActivity extends ActionBarActivityProcess implements AdapterEvents, View.OnTouchListener {

    private Toolbar toolbar;
    private ImageView imagen;
    private int idNivel;
    private IdInformante idTemp;
    private FloatingActionButton botonBoleta;
    private RecyclerView list;
    private BoletaAdapterRecycler adapter;
    private ArrayList<Map<String, Object>> valores = new ArrayList<>();
    private ImageView boletaVacia;
    private int nivel;

    private boolean seleccionable = false;

    float dX;
    float dY;
    int lastAction;
    private  int idAsignacion;
    private  Integer nroRevisita;
//    private int reciclada;
    private int idUpmHijo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boleta);

        cargarOpciones();

        Bundle bundle = getIntent().getExtras();
        idUpm = bundle.getInt("IdUpm");
        idUpmHijo = bundle.getInt("idUpmHijo");

        // TODO: implementar diferentes boletas en el floating action button y enviar el nivel q corresponda segun el ENC_NIVEL
        Nivel nivelInicial = new Nivel();
        nivel = nivelInicial.nivel_inicial();
        verificaBoletas();
        cargarListado();

    }

    public void cargarOpciones(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imagen = findViewById(R.id.logo);
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        botonBoleta = findViewById(R.id.botonBoleta);
        botonBoleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                agregarInformante();
            }
        });
//        botonBoleta.setOnTouchListener(this);

        boletaVacia = findViewById(R.id.boletaVacia);
        list = findViewById(R.id.list_Boleta);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void verificaBoletas(){
        int flag = 0;
        Log.d("REVISION_VIP",idUpm + " - " + Usuario.getUsuario());
        Integer idasignacion = Asignacion.get_asignacion(idUpm, Usuario.getUsuario());
        idAsignacion=idasignacion;
        Asignacion a=new Asignacion();
        if(a.abrir(idAsignacion)){
            nroRevisita = a.get_revisita();
            //reciclada = movimiento.get_re;
            if (nroRevisita > 0 && !Asignacion.verificaBoletas(idAsignacion)) {
                flag = 1;
            }
            a.free();
        }else {
            errorMessage(BoletaActivity.this, null, "Error!", Html.fromHtml("No se encontro la asignacion "), Parametros.FONT_OBS);
        }

//        Upm upm = new Upm();
//        if (upm.abrir(idUpm)) {
//            reciclada = upm.get_reciclada();
//            if (reciclada == 1 && !Asignacion.verificaBoletas(idAsignacion)) {
//                flag = 2;
//            }
//            upm.free();
//        } else {
//            errorMessage(BoletaActivity.this, null, "Error!", Html.fromHtml("No se encontro la UPM "), Parametros.FONT_OBS);
//        }

        //HPMF
//        if(flag == 1&& !Asignacion.tieneBoletasAnteriores(idAsignacion)) {
//            informationMessage(BoletaActivity.this,"cargar_boletas_anteriores","INFORMACIÓN", Html.fromHtml("Se detectaron boletas anteriores (UPM reciclada)."), Parametros.FONT_OBS);
//        } else if (flag == 2&& !Asignacion.tieneBoletasAnteriores(idAsignacion)) {
//            informationMessage(BoletaActivity.this,"cargar_boletas_anteriores", "INFORMACIÓN", Html.fromHtml("Se detectaron boletas de la anterior visita."), Parametros.FONT_OBS);
//        }

        invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void agregarInformante() {
        Integer id = Asignacion.get_asignacion(idUpm, Usuario.getUsuario());
        if (id == null) {
            errorMessage(this, null, "Error!", Html.fromHtml("Manzano no asignado."), Parametros.FONT_OBS);
        } else {
            Asignacion asignacion = new Asignacion();
            if (asignacion.abrir(id)) {
                irEncuesta2(new IdEncuesta(id, 0, 0), 0, false);
                finish();
            } else {
                errorMessage(this, null, "Error!", Html.fromHtml("No se pudo abrir la asignación."), Parametros.FONT_OBS);
            }
            asignacion.free();
        }
    }

    @Override
    public void onItemClick(int mPosition) {
        boolean flag = true;
        Map<String, Object> values = valores.get(mPosition);
//        if (values.get("estado").equals("ENVIADO") && RolPermiso.tienePermiso(Usuario.getRol(), "reasignar")) {
//            flag = false;
//            UpmEnviada upm = new UpmEnviada();
//            if (upm.abrir(idUpm)) {
//                if ((Integer) values.get("id_usuario") == Usuario.getUsuario()) {
//                    flag = true;
//                } else {
//                    errorMessage(null, "Error!", Html.fromHtml("La UPM ya fue enviada."), Parametros.FONT_OBS);
//                }
//            } else {
//                idTemp = new IdInformante((Integer) values.get("id_asignacion"), (Integer) values.get("correlativo"));
//                selectionMessage("cambiar", "Selección", Html.fromHtml("Usuario"), Usuario.partners(0), (Integer) values.get("id_usuario"));
//            }
//        }
        if (flag) {
            try {
                if (seleccionable) {
//                    if (values.get("estado").equals("SELECCIONADO")) {
//                        values.put("estado", "ELABORADO");
//                    } else {
//                        if ((Integer) values.get("id_usuario") == Usuario.getUsuario()) {
//                            values.put("estado", "SELECCIONADO");
//                        } else {
//                            Usuario usuario = new Usuario();
//                            if (usuario.abrir((Integer) values.get("id_usuario"))) {
//                                errorMessage(null, "Error!", Html.fromHtml("Solo " + usuario.get_nombre() + " " + usuario.get_paterno() + " puede editar."), Parametros.FONT_OBS);
//                            } else {
//                                errorMessage(null, "Error!", Html.fromHtml("No se encontró el usuario."), Parametros.FONT_OBS);
//                            }
//                            usuario.free();
//                        }
//                    }
//                    InformanteAdapter adapter = new InformanteAdapter(this, valores, idNivel);
//                    list.setAdapter(adapter);
//                } else {
//                    IdInformante idInformante = new IdInformante((Integer) values.get("id_asignacion"), (Integer) values.get("correlativo"));
//                    int idPregunta = Encuesta.ultima(idInformante);
//                    Siguiente idSiguiente = new Flujo().siguiente(idInformante, idPregunta);
//
//                    if (idSiguiente.idSiguiente > 0) {
//                        Pregunta pregunta = new Pregunta();
//                        if (pregunta.abrir(idSiguiente.idSiguiente)) {
//                            if (pregunta.get_id_nivel() == idNivel && (Integer) values.get("id_usuario") == Usuario.getUsuario()) {
//                                irEncuesta(new IdInformante(idInformante.id_asignacion, idInformante.correlativo), new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, idSiguiente.idSiguiente, idSiguiente.fila), nivel, 0, new IdInformante(Asignacion.get_asignacion(idUpm, Usuario.getUsuario()), 0));
////                                irEncuesta(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, idSiguiente.idSiguiente, idSiguiente.fila), idNivel);
//                                finish();
//                            } else {
//                                if (idNivel == 1) {
//                                    irEncuesta(new IdInformante(Asignacion.get_asignacion(idUpm, Usuario.getUsuario()), 0), new IdEncuesta(0,0,0,0), nivel, 0, new IdInformante(Asignacion.get_asignacion(idUpm, Usuario.getUsuario()), 0));
////                                    irInformante(Informante.getUpm(idInformante), idNivel + 1, idInformante);
//                                    finish();
//                                } else {
//                                    Toast.makeText(this,"Seccion concluida",Toast.LENGTH_SHORT).show();
////                                    irResumen(idInformante, 0);
//                                    finish();
//                                }
//                            }
//                        } else {
//                            errorMessage(this,null, "Error!", Html.fromHtml("No se encontró la pregunta."), Parametros.FONT_OBS);
//                        }
//                        pregunta.free();
//                    } else {
//                        irEncuesta(new IdInformante(Asignacion.get_asignacion(idUpm, Usuario.getUsuario()), 0), new IdEncuesta(0,0,0,0), nivel, 0, new IdInformante(Asignacion.get_asignacion(idUpm, Usuario.getUsuario()), 0));
////                        irResumen(idInformante, 0);
//                        finish();
//                    }
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
    }

    @Override
    public void onLongItemClick(int mPosition) {

    }

    public void cargarListado() {

        try {
            Informante informante = new Informante();
            valores = informante.obtenerListadoBoleta(idUpm, 0,3);

            adapter = new BoletaAdapterRecycler(this, valores, idNivel);

            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String a = valores.get(list.getChildAdapterPosition(v)).get("id_asignacion").toString();
                    try {
                        Map<String, Object> val = valores.get(list.getChildAdapterPosition(v));
//                        irEncuesta2(new IdInformante((Integer) val.get("id_asignacion"), (Integer) val.get("correlativo")), new IdEncuesta(0,0,0), 2, 3, Parametros.ID_SECCION_RESERVADA, new IdInformante((Integer) val.get("id_asignacion_padre"), (Integer) val.get("correlativo_padre")),1,idUpmHijo);
                        irEncuesta2(new IdEncuesta((Integer) val.get("id_asignacion"), (Integer) val.get("correlativo"), 0), 0, false);
                    finish();
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            });
            if (valores.size() > 0) {
                boletaVacia.setVisibility(View.GONE);
                list.setAdapter(adapter);
            }

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(list);

            String duplicados = Informante.duplicados(idUpm);
            if (duplicados != null) {
                toastMessage(duplicados, Color.RED);
            }
            invalidateOptionsMenu();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    //PERMITE MANIPULAR ELEMENTOS DEL RECYCLER VIEW
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
            Collections.swap(valores, fromPosition, toPosition);
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    public void continuarConcluido() {
        if (Informante.isClosed(idTemp)) {
            informationMessage(BoletaActivity.this, null, "Error!", Html.fromHtml("La boleta está cerrada por el monitor."), Parametros.FONT_OBS);
        } else {
            AlertDialog.Builder alertDialog = new  AlertDialog.Builder(this);
            alertDialog.setTitle("Codigo");
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);

            TextView textMensaje = new TextView(this);
            textMensaje.setText("Introduzca código de acceso:");
            linearLayout.addView(textMensaje);

            final EditText cod = new EditText(this);
            cod.setSingleLine();
            linearLayout.addView(cod);

            alertDialog.setView(linearLayout);

            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    if (cod.getText().toString().equals(Proyecto.getCodigoDesbloqueo())) {
                        continuar();
                    } else {
                        errorMessage(BoletaActivity.this, null, "Error!", Html.fromHtml("Código incorrecto."), Parametros.FONT_OBS);
                    }
                }
            });
            alertDialog.setNegativeButton("Cancelar", null);

            AlertDialog dialog = alertDialog.create();
            dialog.show();
        }
    }

    public void continuar() {
        Informante informante = new Informante();
        if (informante.abrir(idTemp)) {
            irEncuestaInicial(informante.get_id_informante(), informante.get_id_nivel(), informante.get_id_informante_padre(),idUpmHijo);
            finish();
        } else {
            errorMessage(BoletaActivity.this, null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
        }
        informante.free();
    }

    public static void habilitaSupervisor(IdInformante idInformante){
        Log.d("supervisor2", "aqui");
            Informante.supervisor(idInformante);

    }

    //PARA MOVER EL FLOATING BUTTON POR CUALQUIER PARTE DE LA PANTALLA
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                view.setY(event.getRawY() + dY);
                view.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_DOWN)
                    agregarInformante();
                break;

                case MotionEvent.ACTION_BUTTON_PRESS:
                    agregarInformante();
                    break;
            default:
                return false;
        }
        return true;
    }
    @SuppressWarnings("unused")
    public void cargar_boletas_anteriores(){
        new BoletaActivity.InsertaBoletasAnteriores().execute();
    }

    // EN CASO DE SER REVISITA, INSERTA VIVIENDAS DEL ANTERIOR TRIMESTRE
    public class InsertaBoletasAnteriores extends AsyncTask<String, String, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(BoletaActivity.this);
            dialog.setMessage("Procesando...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle(getString(R.string.app_name));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                StringBuilder textCodigos=new StringBuilder();
                // CONTROL PARA LAS VIVIENDAS RECICLADAS O REVISITAS
                ArrayList<Map<String, Object>> informantePadreAnterior = null;
                ArrayList<Map<String, Object>> informanteAnterior;
                int id_base=0;
                if (nroRevisita > 0) {
                    informantePadreAnterior = new InformanteAnterior().obtenerListado("id_nivel = 3 AND id_upm = " + idUpm+" AND id_base=2 ", "CAST(codigo AS Int)");
                }
                if(informantePadreAnterior != null && informantePadreAnterior.size()>0) {
                for(Map<String, Object> t : informantePadreAnterior){
                    Informante informantePadre = new Informante();
//                    Map<String, Object> mapInformantePadreAnterior = informantePadreAnterior.get(0);
                    if (informantePadre.nuevo()) {
                        informantePadre.set_id_nivel((Integer) t.get("id_nivel"));
                        informantePadre.set_id_upm((Integer) t.get("id_upm"));
                        informantePadre.set_id_upm_hijo((Integer) t.get("id_upm_hijo"));
                        informantePadre.set_codigo((String) t.get("codigo"));
                        informantePadre.set_descripcion((String) t.get("descripcion"));
                        informantePadre.set_usucre(Usuario.getLogin());
                        informantePadre.set_id_informante_anterior(new IdInformante((Integer) t.get("id_asignacion"), (Integer) t.get("correlativo")));
                        informantePadre.set_codigo_anterior((String) t.get("codigo"));
                        informantePadre.set_id_usuario(Usuario.getUsuario());

                        IdInformante idInformantePadreNuevo = null;
                        switch ((Integer) t.get("id_nivel")) {
                            case 3:
                                idInformantePadreNuevo = (IdInformante) informantePadre.guardar();
                                break;

                        }
                        ///inserta encuesta de padre
                        ArrayList<Map<String, Object>> encuestaAnteriorPadre = new EncuestaAnterior().obtenerListado("id_asignacion = " + t.get("id_asignacion") + " AND correlativo =" + t.get("correlativo")+" AND id_pregunta NOT IN (2036,2037,2038,2151)", "");
                        Encuesta encuestaPadre = new Encuesta();
                        for (Map<String, Object> r : encuestaAnteriorPadre) {
                            if (encuestaPadre.nuevo()) {
                                if (idInformantePadreNuevo != null)
                                    encuestaPadre.set_id_encuesta(new IdEncuesta(idInformantePadreNuevo.id_asignacion, idInformantePadreNuevo.correlativo, (Integer) r.get("id_pregunta")));
                                encuestaPadre.set_codigo_respuesta((String) r.get("codigo_respuesta"));
                                encuestaPadre.set_respuesta((String) r.get("respuesta"));
                                encuestaPadre.set_observacion((String) r.get("observacion"));
                                encuestaPadre.set_usucre(Usuario.getLogin());
                                switch ((String) r.get("codigo_respuesta")) {
//                                    case "997":
//                                        encuestaPadre.set_estado(Estado.NOAPLICA);
//                                        break;
//                                    case "998":
//                                        encuestaPadre.set_estado(Estado.SENIEGA);
//                                        break;
//                                    case "999":
//                                        encuestaPadre.set_estado(Estado.NOSABE);
//                                        break;
                                    default:
                                        encuestaPadre.set_estado(Estado.ELABORADO);
                                        break;
                                }
                                encuestaPadre.guardar();
                            } else {
                                return "No se ha podido crear la respuesta.";
                            }
                        }

                        informanteAnterior = new InformanteAnterior().obtenerListado("id_nivel = 4 AND id_upm = " + idUpm + " AND id_base=2 AND id_asignacion_padre= " + (Integer) t.get("id_asignacion") + " AND correlativo_padre= " + (Integer) t.get("correlativo"), "CAST(codigo AS Int)");
                        //informantes Hijos

                        Informante informante = new Informante();

                        for (Map<String, Object> i : informanteAnterior) {
                            textCodigos.append(",").append((String) i.get("codigo"));
                            if (informante.nuevo()) {

                                informante.set_id_nivel((Integer) i.get("id_nivel"));
                                informante.set_id_upm((Integer) i.get("id_upm"));
                                informante.set_id_upm_hijo((Integer) i.get("id_upm_hijo"));
                                informante.set_id_informante(new IdInformante(idAsignacion, 0));
                                informante.set_codigo((String) i.get("codigo"));
                                informante.set_descripcion((String) i.get("descripcion"));
                                informante.set_usucre(Usuario.getLogin());
                                informante.set_id_informante_anterior(new IdInformante((Integer) i.get("id_asignacion"), (Integer) i.get("correlativo")));
                                informante.set_codigo_anterior((String) i.get("codigo"));
                                informante.set_id_usuario(Usuario.getUsuario());
//                        informante.set_estado((Estado) i.get("estado"));
                                if (idInformantePadreNuevo != null) {
                                    informante.set_id_informante_padre(idInformantePadreNuevo);
                                }
                                IdInformante idInformanteNuevo;
//                                switch ((Integer) i.get("id_nivel")) {
//                                    case 2:
//                                idInformanteNuevo = (IdInformante) informante.guardar();
//                                        break;
//                                    case 3:
//                                        idInformanteNuevo = (IdInformante)informante.guardar();
//                                        break;
//                                    default:
                                idInformanteNuevo = informante.guardar(idInformantePadreNuevo);
//                                        break;
//                                }

                                ArrayList<Map<String, Object>> encuestaAnterior = new EncuestaAnterior().obtenerListado("id_asignacion = " + i.get("id_asignacion") + " AND correlativo=" + i.get("correlativo")+" AND id_pregunta NOT IN (2036,2037,2038,2151)", "");
                                Encuesta encuesta = new Encuesta();
                                for (Map<String, Object> r : encuestaAnterior) {
                                    if (encuesta.nuevo()) {
                                        if (idInformanteNuevo != null)
                                            encuesta.set_id_encuesta(new IdEncuesta(idInformanteNuevo.id_asignacion, idInformanteNuevo.correlativo, (Integer) r.get("id_pregunta")));
                                        encuesta.set_codigo_respuesta((String) r.get("codigo_respuesta"));
                                        encuesta.set_respuesta((String) r.get("respuesta"));
                                        encuesta.set_observacion((String) r.get("observacion"));
                                        encuesta.set_usucre(Usuario.getLogin());
                                        switch ((String) r.get("codigo_respuesta")) {
                                            case "997":
                                                encuesta.set_estado(Estado.NOAPLICA);
                                                break;
                                            case "998":
                                                encuesta.set_estado(Estado.SENIEGA);
                                                break;
                                            case "999":
                                                encuesta.set_estado(Estado.NOSABE);
                                                break;
                                            default:
                                                encuesta.set_estado(Estado.ELABORADO);
                                                break;
                                        }
                                        encuesta.guardar();
                                    } else {
                                        return "No se ha podido crear la respuesta.";
                                    }
                                }
                                Log.d("generados", textCodigos.toString());
                            } else {
                                return "No se ha podido crear el informante.";
                            }
                        }
                    }
////////
                }

                }else {

                }
//                Informante informante = new Informante();
//                for (Map<String, Object> i : informanteAnterior) {
//                    textCodigos.append(",").append((String) i.get("codigo"));
//                    if (informante.nuevo()) {
//                        int idAsignacionPadre=(Integer) i.get("id_asignacion_padre");
//                        int correlativoPadre=(Integer) i.get("correlativo_padre");
//
//                        informante.set_id_nivel((Integer) i.get("id_nivel"));
//                        informante.set_id_upm((Integer) i.get("id_upm"));
//                        informante.set_id_informante(new IdInformante(idAsignacion,0));
//                        informante.set_codigo((String) i.get("codigo"));
//                        informante.set_descripcion((String) i.get("descripcion"));
//                        informante.set_usucre(Usuario.getLogin());
//                        informante.set_id_informante_anterior(new IdInformante((Integer) i.get("id_asignacion"),(Integer) i.get("correlativo")));
//                        informante.set_codigo_anterior((String) i.get("codigo"));
//                        informante.set_id_usuario(Usuario.getUsuario());
////                        informante.set_estado((Estado) i.get("estado"));
//                        if(idAsignacionPadre!=0){
//                            informante.set_id_informante_padre(new IdInformante(idAsignacionPadre,correlativoPadre));
//                        }
//                        IdInformante idInformanteNuevo;
//                        switch ((Integer) i.get("id_nivel")) {
//                            case 2:
//                                idInformanteNuevo = informante.guardarLV(idAsignacion);
//                                break;
//                            case 3:
//                                idInformanteNuevo = (IdInformante)informante.guardar();
//                                break;
//                            default:
//                                idInformanteNuevo = informante.guardar(new IdInformante(idAsignacionPadre,correlativoPadre));
//                                break;
//                        }
//
//
//
//                        ArrayList<Map<String, Object>> encuestaAnterior = new EncuestaAnterior().obtenerListado("id_asignacion = " + i.get("id_asignacion")+ " AND correlativo="+ i.get("correlativo"),"");
//                        Encuesta encuesta = new Encuesta();
//                        for (Map<String, Object> r : encuestaAnterior) {
//                            if (encuesta.nuevo()) {
//                                if(idInformanteNuevo!=null)
//                                encuesta.set_id_encuesta(new IdEncuesta(idInformanteNuevo.id_asignacion,idInformanteNuevo.correlativo,(Integer)r.get("id_pregunta")));
//                                encuesta.set_codigo_respuesta((String) r.get("codigo_respuesta"));
//                                encuesta.set_respuesta((String) r.get("respuesta"));
//                                encuesta.set_observacion((String) r.get("observacion"));
//                                encuesta.set_usucre(Usuario.getLogin());
//                                switch ((String) r.get("codigo_respuesta")) {
//                                    case "997":
//                                        encuesta.set_estado(Estado.NOAPLICA);
//                                        break;
//                                    case "998":
//                                        encuesta.set_estado(Estado.SENIEGA);
//                                        break;
//                                    case "999":
//                                        encuesta.set_estado(Estado.NOSABE);
//                                        break;
//                                    default:
//                                        encuesta.set_estado(Estado.ELABORADO);
//                                        break;
//                                }
//                                encuesta.guardar();
//                            } else {
//                                return "No se ha podido crear la respuesta.";
//                            }
//                        }
//                        Log.d("generados",textCodigos.toString());
//                    } else {
//                        return "No se ha podido crear el informante.";
//                    }
//                }
                return "Ok";
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if( result.equals("Ok") ) {
                cargarListado();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            } else {
                errorMessage(BoletaActivity.this,null, "Error!", Html.fromHtml("Datos erroneos de visitas pasadas"), Parametros.FONT_OBS);
            }
        }
    }

}
