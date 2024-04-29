package bo.gob.ine.naci.epc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bo.gob.ine.naci.epc.adaptadores.AdapterEvents;
import bo.gob.ine.naci.epc.adaptadores.AdapterMove;
import bo.gob.ine.naci.epc.adaptadores.ListadoViviendasAdapterRecycler;
import bo.gob.ine.naci.epc.entidades.Asignacion;
import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.EncuestaAnterior;
import bo.gob.ine.naci.epc.entidades.Estado;
import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.InformanteAnterior;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.entidades.RolPermiso;
import bo.gob.ine.naci.epc.entidades.Seleccion;
import bo.gob.ine.naci.epc.entidades.Upm;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityProcess;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class ListadoViviendasActivity extends ActionBarActivityProcess implements AdapterEvents, AdapterMove {
//    , CallBackItemTouch

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ImageView imagen;
    private RecyclerView list;
    private LinearLayout layoutCabecera;
    private int idAsignacion;
    private int codigo;
    private String seleccionable = null;
    private IdInformante idTemp = null;
    private ImageView boletaVacia;
    private ActionBarDrawerToggle toogle;
    private FloatingActionsMenu fabMenuMain;
    private FloatingActionButton cabecera, nuevoLv;
    private Animation animation;
    private View hView;
    private GuideView mGuideView;
    private GuideView.Builder builder;
    View view4;
    CoordinatorLayout layout;
    ListadoViviendasAdapterRecycler adapterRecyclerViewListadoViviendas;
    //    private int nroRevisita;
//    private int reciclada;
    private ArrayList<Map<String, Object>> valores = new ArrayList<>();
    //posicion actual
    private int mCurrentItemPosition;
    public static String CREAR_CABECERA = "crear_cabecera";
    private int idUpmHijo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_viviendas);

        Bundle bundle = getIntent().getExtras();
        idUpm = bundle.getInt("IdUpm");
        idAsignacion = Asignacion.get_asignacion(idUpm, Usuario.getUsuario());

        view4 = findViewById(R.id.view4);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layout = findViewById(R.id.layout_main_activity);
        imagen = findViewById(R.id.logo);
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        list = (RecyclerView) findViewById(R.id.lv_list_view);

        fabMenuMain = findViewById(R.id.fabMenuLv);
        cabecera = findViewById(R.id.fabCabecera);
        nuevoLv = findViewById(R.id.fabNuevoLv);
            nuevoLv.setTitle(Seleccion.hasSelected(idUpm)?"Añadir LV omitida":"Añadir LV");

//    if (RolPermiso.tienePermiso(Usuario.getRol(), "activity_listado_viviendas")) {
//        cabecera.setVisibility(View.VISIBLE);
//    } else {
        cabecera.setVisibility(View.GONE);
//    }

        if (Informante.listadoViviendasInicializado(idAsignacion)) {
            cabecera.setTitle("Editar Cabecera");
        }
        cabecera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Informante.listadoViviendasInicializado(idAsignacion)) {
                    Integer id = Asignacion.get_asignacion(idUpm, Usuario.getUsuario());
                    if (id == null) {
                        errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("UPM no asignada"), Parametros.FONT_OBS);
                    } else {
                        irEncuesta(new IdInformante(id, 0), new IdEncuesta(0, 0, 0, 1), 1, 1, 0, new IdInformante(0, 0), 1, idUpmHijo);

                        finish();
                    }
                } else {
                    irEncuesta(Asignacion.getInformanteCabeceraViviendas(idAsignacion), new IdEncuesta(0, 0, 0, 1), 1, 1, 0, new IdInformante(0, 0), 1, idUpmHijo);
                    finish();
                }
            }
        });

        nuevoLv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RolPermiso.tienePermiso(Usuario.getRol(), "activity_listado_viviendas")) {
//                if (!Informante.listadoViviendasInicializado(idAsignacion)) {
//                    errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No ha iniciado el LV"), Parametros.FONT_OBS);
//                } else {
                    if (Seleccion.hasSelected(idUpm)) {
//                        if( !(nroRevisita>0 &&nroRevisita<8))
                        decisionMessage(ListadoViviendasActivity.this, "agregarListado", null, "Confirmar", Html.fromHtml("Usted ya realizó la selección de viviendas. ¿Está seguro de añadir una nueva vivienda?"));
//                        else
//                            errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("Debe deshacer el sorteo de viviendas"), Parametros.FONT_OBS);
                    } else {
                        agregarListado();
                    }
//                }
                } else {
//                agregarListado();
                }
            }
        });

        fabMenuMain.expand();

        boletaVacia = findViewById(R.id.boletaVacia);
        drawerLayout = findViewById(R.id.lv_navigation_drawer);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }
        hView = navigationView.getHeaderView(0);

        setupNavigationDrawerContent(navigationView);

        toogle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {

//            verificaVivienda();
            cargarCabecera();


            cargarListado();
            registerForContextMenu(list);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
//    private void verificaVivienda() {
//
//        int flag = 0;
//        Asignacion asignacion = new Asignacion();
//        if (asignacion.abrir(idAsignacion)) {
//            nroRevisita = asignacion.get_revisita();
//            //reciclada = movimiento.get_re;
//            if (nroRevisita > 0 && !Asignacion.verificaViviendas(idAsignacion)) {
//                flag = 1;
//            }
//            asignacion.free();
//        } else {
//            errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se encontro la asignacion "), Parametros.FONT_OBS);
//        }
//
//        Upm upm = new Upm();
//        if (upm.abrir(idUpm)) {
//            reciclada = upm.get_reciclada();
//            if (reciclada == 1 && !Asignacion.verificaViviendas(idAsignacion)) {
//                flag = 2;
//            }
//            upm.free();
//        } else {
//            errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se encontro la UPM "), Parametros.FONT_OBS);
//        }
//
//        //HPMF
//        if(flag == 1&& (!Asignacion.tieneViviendasAnteriores(idAsignacion)||!Asignacion.tieneViviendasAnteriores1(idAsignacion))) {
//            informationMessage(ListadoViviendasActivity.this,"cargar_viviendas_anteriores","INFORMACIÓN", Html.fromHtml("Se detectaron viviendas anteriores (UPM reciclada)."), Parametros.FONT_OBS);
//        } else if (flag == 2&& (!Asignacion.tieneViviendasAnteriores(idAsignacion)||!Asignacion.tieneViviendasAnteriores1(idAsignacion))) {
//            informationMessage(ListadoViviendasActivity.this,"cargar_viviendas_anteriores", "INFORMACIÓN", Html.fromHtml("Se detectaron viviendas de la anterior visita."), Parametros.FONT_OBS);
//        }
//
//        invalidateOptionsMenu();
//    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item;


        item = menu.findItem(R.id.action_settings);
        item.setTitle("Versión " + Parametros.VERSION);

        MenuItem borrarMenu = menu.findItem(R.id.action_borrar_todo);

//            if(!(nroRevisita>0 && nroRevisita<10) ||(reciclada==1 &&nroRevisita==0)){
//                borrarMenu.setVisible(false);
//            }


        return true;


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toogle.onOptionsItemSelected(item)) {
            return true;
        }
//
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                drawerLayout.openDrawer(GravityCompat.START);
//                return true;
//        }
        switch (item.getItemId()) {
            case R.id.action_settings:
                cargarListadoPrueba();
                registerForContextMenu(list);
                IniciaTutorial();

                return true;
            case R.id.action_borrar_todo:
                if (Seleccion.hasSelected(idUpm)) {
                    informationMessageObs(null, "Voes", Seleccion.hasSelected(idUpm)?Html.fromHtml(Informante.getResumenVoe(idUpm)):Html.fromHtml(Seleccion.seleccionViviendas(Upm.getCodigoUpm(idUpm).endsWith("A")?1:0,idUpm)), Parametros.FONT_OBS);

//                    errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("Debe deshacer el sorteo de viviendas "), Parametros.FONT_OBS);
                } else {
                    errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("Debe realizar la selección de viviendas, en el menú de la izquierda"), Parametros.FONT_OBS);
//                    successMethod = "procesoExitoso";
//                    errorMethod = null;
//                    decisionMessage(ListadoViviendasActivity.this, "borrar", null, "Confirmar", Html.fromHtml("Se eliminarán todas las viviendas"));

                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void IniciaTutorial() {


        NavigationView navigationView = findViewById(R.id.action_cartografia);
//        navigationView.getHeaderView(0);


//        Map<String, Object> val = valores.get(list.getChildAdapterPosition(0));

        builder = new GuideView.Builder(this)
                .setTitle("Despliega")
                .setContentText("Opciones")
                .setGravity(Gravity.center)
                .setDismissType(DismissType.anywhere)
                .setTargetView(fabMenuMain)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        switch (view.getId()) {
                            case R.id.fabMenuLv:
                                if (cabecera.getVisibility() == View.VISIBLE) {
                                    builder.setTargetView(cabecera).build();
                                    builder.setTitle("Editar cabecera");
                                    builder.setTitleTextSize(25);
                                    builder.setContentText("Presione boton para editar la cabecera");
                                } else {
                                    builder.setTargetView(nuevoLv).build();
                                    builder.setTitle(Seleccion.hasSelected(idUpm)?"Añadir LV omitida":"Añadir LV");
                                    builder.setTitleTextSize(25);
                                    builder.setContentText("Presione boton para añadir LV");
                                }
                                break;

                            case R.id.fabCabecera:
                                builder.setTargetView(nuevoLv).build();
                                builder.setTitle(Seleccion.hasSelected(idUpm)?"Añadir LV omitida":"Añadir LV");
                                builder.setTitleTextSize(25);
                                builder.setContentText("Presione boton para añadir LV");
                                break;

                            case R.id.fabNuevoLv:
                                builder.setTargetView(list.getChildAt(0)).build();
                                builder.setTitle("Opciones");
                                builder.setTitleTextSize(25);
                                builder.setContentText("Seleccione una fila");

                                break;

                            case -1:

                                builder.setTargetView(drawerLayout).build();
                                builder.setTitle("Consolida");
                                builder.setTitleTextSize(25);
                                builder.setContentText("Consolida");
                                drawerLayout.openDrawer(GravityCompat.START);
                                //return;
                                break;

                            case R.id.lv_navigation_drawer:
                                drawerLayout.openDrawer(GravityCompat.START);
                                builder.setTargetView(hView.findViewById(R.id.consolidaLv)).build();
//                                builder.setTargetView(hView).build();
//                                builder.setTitle("Expandir");
//                                builder.setTitleTextSize(25);
//                                builder.setContentText("Selecciona opciones");
                                //return;
                                break;

                            case R.id.consolidaLv:

                                if (hView.findViewById(R.id.btnDeshacerSorteo).getVisibility() == View.VISIBLE) {
                                    builder.setTargetView(hView.findViewById(R.id.btnDeshacerSorteo)).build();
                                    builder.setTitle("Sortear/Deshacer Sorteo");
                                    builder.setTitleTextSize(25);
                                    builder.setContentText("Sortear");
                                } else {
//                                    builder.setTargetView(toolbar).build();
//                                    builder.setTitle("Colapsa");
//                                    builder.setTitleTextSize(25);
//                                    builder.setContentText("Colapsa");
//                                    drawerLayout.closeDrawer(GravityCompat.START);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putInt("IdUpm", (Integer) idUpm);
//                                    finish();
//                                    startActivity(getIntent());
                                    builder.setTargetView(hView.findViewById(R.id.btnDeshacerSorteo)).build();
                                    builder.setTitle("Sortear/Deshacer Sorteo");
                                    builder.setTitleTextSize(25);
                                    builder.setContentText("Selecciona Viviendas");
                                }

                                //return;
                                break;
                            case R.id.reordenaLv:
                                if (hView.findViewById(R.id.reordenaLv).getVisibility() == View.VISIBLE) {
                                    builder.setTargetView(hView.findViewById(R.id.reordenaLv)).build();
                                    builder.setTitle("Borrar");
                                    builder.setTitleTextSize(25);
                                    builder.setContentText("Borra LV");

                                } else {
                                    builder.setTargetView(toolbar).build();
                                    builder.setTitle("Colapsa");
                                    builder.setTitleTextSize(25);
                                    builder.setContentText("Colapsa");
//                                    drawerLayout.closeDrawer(GravityCompat.START);

                                }
                                break;
                            case R.id.btnDeshacerSorteo:

//                                if (hView.findViewById(R.id.borrarLv).getVisibility() == View.VISIBLE) {
//                                    builder.setTargetView(hView.findViewById(R.id.borrarLv)).build();
//                                    builder.setTitle("Borrar");
//                                    builder.setTitleTextSize(25);
//                                    builder.setContentText("Borrar");
//
//                                } else {
//                                    builder.setTargetView(toolbar).build();
//                                    builder.setTitle("Colapsa");
//                                    builder.setTitleTextSize(25);
//                                    builder.setContentText("Colapsa");
//                                    drawerLayout.closeDrawer(GravityCompat.START);
                                Bundle bundle = new Bundle();
                                bundle.putInt("IdUpm", (Integer) idUpm);
                                finish();
                                startActivity(getIntent());
                                return;
//                                }

                            case R.id.borrarLv:

                                if (hView.findViewById(R.id.borrarLv).getVisibility() == View.VISIBLE) {
                                    builder.setTargetView(hView.findViewById(R.id.borrarLv)).build();
                                    builder.setTitle("Borrar");
                                    builder.setTitleTextSize(25);
                                    builder.setContentText("Borrar");

                                } else {
                                    builder.setTargetView(toolbar).build();
                                    builder.setTitle("Colapsa");
                                    builder.setTitleTextSize(25);
                                    builder.setContentText("Colapsa");
                                    drawerLayout.closeDrawer(GravityCompat.START);
                                    Bundle bbundle = new Bundle();
                                    bbundle.putInt("IdUpm", (Integer) idUpm);
                                    finish();
                                    startActivity(getIntent());
                                }
//                                cargarCabecera();
//
//                                //return;
//                                break;


                            case R.id.descargaLv:
                                builder.setTargetView(toolbar).build();
                                builder.setTitle("Colapsa");
                                builder.setTitleTextSize(25);
                                builder.setContentText("Colapsa");
                                drawerLayout.closeDrawer(GravityCompat.START);
                                Bundle buundle = new Bundle();
                                buundle.putInt("IdUpm", (Integer) idUpm);
                                finish();
                                startActivity(getIntent());


//                                Intent listado = new Intent(this., ListadoViviendasActivity.class);
//                                listado.putExtras(bundle);
//                                activity.startActivity(listado);
//                                cargarListado();
//                                registerForContextMenu(list);
                                return;
                            //break;


//                            case R.id.main_navigation_drawer:
//                                return;
//                            default:
//
//
//                                //onCreate(bundle);
//
//
//                                return;
//                            default:
//                                Bundle bundlee = new Bundle();
//                                bundlee.putInt("IdUpm", (Integer) idUpm);
//                                finish();
//                                startActivity(getIntent());
//                                return;
                        }

                        mGuideView = builder.build();
                        mGuideView.show();
                    }

                });

        mGuideView = builder.build();
        mGuideView.show();
        updatingForDynamicLocationViews();


    }

    private void updatingForDynamicLocationViews() {
        view4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                mGuideView.updateGuideViewLocation();
            }
        });
    }

    private void cargarCabecera() {
        if(Informante.existeCabecera(new IdInformante(idAsignacion, 0))) {
            if(!Seleccion.hasSelected(idUpm)) {
                Informante.Sumatotales(idAsignacion, new String[]{"AA_02;" + Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA, "AA_03;" + Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA, "AA_04;" + Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA, "AA_05;" + Parametros.CODIGO_PREGUNTA_NRO_HOGARES, "AA_06;" + Parametros.CODIGO_PREGUNTA_NRO_DE_HOMBRES, "AA_07;" + Parametros.CODIGO_PREGUNTA_NRO_DE_MUJERES, "AA_08;" + Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA});
                Log.d("SUMA", "SI");
            } else {
                Informante.Sumatotales(idAsignacion, new String[]{"AA_08;" + Parametros.CODIGO_PREGUNTA_VIVIENDA_OMITIDA});
                Log.d("SUMA", "NO");
            }
        }
        TextView txtLVcabeceraValor = (TextView) hView.findViewById(R.id.vivienda_cabecera_valor);
        txtLVcabeceraValor.setTextSize(Parametros.FONT_LIST_BIG);
        TextView txtLVcabeceraDescripcion = (TextView) hView.findViewById(R.id.vivienda_cabecera_descripcion);
        txtLVcabeceraDescripcion.setTextSize(Parametros.FONT_LIST_SMALL);
        TextView txtLVcabeceraDatosActuales = (TextView) hView.findViewById(R.id.vivienda_cabecera_datos_actuales);
        txtLVcabeceraDatosActuales.setTextSize(Parametros.FONT_LIST_SMALL);
        Button btnSorteo = (Button) hView.findViewById(R.id.btnSorteo);
        Button btnDeshacerSorteo = (Button) hView.findViewById(R.id.btnDeshacerSorteo);
        Button consolidaLv = (Button) hView.findViewById(R.id.consolidaLv);
        Button descargaLv = (Button) hView.findViewById(R.id.descargaLv);
        Button reordenaLv = (Button) hView.findViewById(R.id.reordenaLv);
        Button borrarLv = (Button) hView.findViewById(R.id.borrarLv);
        if (RolPermiso.tienePermiso(Usuario.getRol(), "activity_listado_viviendas")) {
//            if (Seleccion.hasSelected(idUpm)) {
            btnDeshacerSorteo.setVisibility(View.GONE);
            btnSorteo.setVisibility(View.VISIBLE);
            descargaLv.setVisibility(View.VISIBLE);
//            }
//            else {
//                btnSorteo.setVisibility(View.VISIBLE);
//                btnDeshacerSorteo.setVisibility(View.GONE);
//            }
            SharedPreferences preferences = this.getSharedPreferences(Parametros.SIGLA_PROYECTO + ".xml", Context.MODE_PRIVATE);
            if (preferences.getBoolean("lv" + idUpm, false)) {
//                descargaLv.setVisibility(View.GONE);
            }
        } else {
            txtLVcabeceraDatosActuales.setText("SOLAMENTE EL SUPERVISOR PUEDE VER ESTE APARTADO");
            btnDeshacerSorteo.setVisibility(View.GONE);
            btnSorteo.setVisibility(View.GONE);
//            descargaLv.setVisibility(View.GONE);
        }

        btnSorteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Seleccion.hasSelected(idUpm)&&Informante.getObservacionUPM(idUpm).length()<3) {
                    errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("Agregar observación UPM"), Parametros.FONT_OBS);
                    return;
                } else {
                    int voes = Seleccion.obtenerNroViviendasNoOmitidas(idUpm);
                    String message=Informante.revisarDatosOrdenUpm(idUpm);
                    if(message!=null){
                        errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("Revise " +message), Parametros.FONT_OBS);
                        return;
                    }
                    if (voes >= 12) {
                        decisionMessage(ListadoViviendasActivity.this, "seleccionar", null, "Confirmar", Html.fromHtml("Usted solo tiene <b>" + voes + "</b> viviendas objeto de estudio. ¿Realmente desea proceder con la seleccion?"));

                    } else if(Upm.isSorteoEspecial(idUpm) && voes >= 8){
                        decisionMessage(ListadoViviendasActivity.this, "seleccionar", null, "Confirmar", Html.fromHtml("Usted solo tiene <b>" + voes + "</b> viviendas objeto de estudio. ¿Realmente desea proceder con la seleccion?"));
                    }
                    else if(Seleccion.hasSelected(idUpm)){
                        decisionMessage(ListadoViviendasActivity.this, "seleccionar", null, "Confirmar", Html.fromHtml("Quiere volver a sortear?"));
                    }else {
                        errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("Revise nro de viviendas para sorteo"), Parametros.FONT_OBS);
                        return;
                    }
                }

            }
        });
        btnDeshacerSorteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Seleccion.hasSelected(idUpm)) {
//                    Seleccion.deshacer(idUpm);
//                    cargarCabecera();
//                    cargarListado();
                } else {
                    errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("Aún no realizó la selección de viviendas"), Parametros.FONT_OBS);
                }
            }
        });
        consolidaLv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RolPermiso.tienePermiso(Usuario.getRol(), "activity_listado_viviendas")) {

//                    if (Informante.listadoViviendasInicializado(idAsignacion)) {
                    if (Movil.isNetworkAvailable()) {
                        try {
                            startThree();
                            successMethod = "success";
                            errorMethod = null;
                            new bo.gob.ine.naci.epc.herramientas.ActionBarActivityProcess.enviaJson().execute(Parametros.URL_DOWNLOAD, String.valueOf(Usuario.getUsuario()));
                        } catch (Exception ex) {
                            errorMessage(ListadoViviendasActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
                        }
                    } else {
                        errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                    }
//                    } else {
//                        errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("Debe iniciar el LV"), Parametros.FONT_OBS);
//                    }
                } else {
                    if (Movil.isNetworkAvailable()) {
                        try {
                            startThree();
                            successMethod = "success";
                            errorMethod = null;
                            new enviaJson().execute(Parametros.URL_DOWNLOAD, String.valueOf(Usuario.getUsuario()));
                        } catch (Exception ex) {
                            errorMessage(ListadoViviendasActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
                        }
                    } else {
                        errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                    }

                }
            }
        });

        descargaLv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Informante.listadoViviendasInicializado(idAsignacion)) {
                if (!Informante.existeCabecera(new IdInformante(idAsignacion, 0))) {
                    errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("Agregar Registros antes"), Parametros.FONT_OBS);

                } else {
                    String obs = Informante.getObservacionUPM(idUpm);
//                        observation=obs;
                    observationMessageLV(ListadoViviendasActivity.this, "registrar_obs_upm", "Observación UPM", Html.fromHtml("Anote la observación"), obs, false);
                }
            }
        });

        reordenaLv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Seleccion.hasSelected(idUpm)) {
                    Log.d("seleccionado", "1");
                    Seleccion.deshacer(idUpm);
                }
                if (Movil.isNetworkAvailable()) {
                    successMethod = "descarga_lv";
                    errorMethod = null;
//                                    if (Observacion.countObs("1,6,8,17,18,19,22,24") > 0) {
//                                        decisionMessage("download_asignacion", null, "ALERTA", Html.fromHtml("Tiene observaciones en sus boletas. Se le recomienda corregirlas. ¿Desea descargar la carga de trabajo de todos modos?"));
//                                    } else {
                    decisionMessage(ListadoViviendasActivity.this, "consolida_lv", null, "Confirmar", Html.fromHtml("Utilice este boton en caso de que la selección de LV's no este correcta"));
//                                    }
                } else {
                    errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                }

            }
        });

        borrarLv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                successMethod = "procesoExitoso";
//                errorMethod = null;
//                    decisionMessage(ListadoViviendasActivity.this, "borrar", null, "Confirmar", Html.fromHtml("Utilice este boton en caso de que la selección de LV's no este correcta"));
                informationMessageObs(null, "Voes",Seleccion.hasSelected(idUpm)?Html.fromHtml(Informante.getResumenVoe(idUpm)):Html.fromHtml(Seleccion.seleccionViviendas(Upm.getCodigoUpm(idUpm).endsWith("A")?1:0,idUpm)), Parametros.FONT_OBS);
            }
        });
        reordenaLv.setVisibility(View.GONE);
//        descargaLv.setVisibility(View.GONE);
//    if((nroRevisita>0 && nroRevisita<8) || (reciclada==1 && nroRevisita==0)){
//        borrarLv.setVisibility(View.GONE);
//        btnSorteo.setVisibility(View.GONE);
////        btnDeshacerSorteo.setVisibility(View.GONE);
//    }
//    if(nroRevisita==0)
//        borrarLv.setVisibility(View.GONE);
//        if (Informante.listadoViviendasInicializado(idAsignacion)) {
        IdInformante idInformante = Asignacion.getInformanteCabeceraViviendas(idAsignacion);
        txtLVcabeceraValor.setText(Html.fromHtml("<b>UPM:</b> " + Upm.getCodigo(idAsignacion)));
//            txtLVcabeceraDescripcion.setText(Html.fromHtml(Informante.getPreguntasRespuestasInicialesLv(1, idInformante, "", false, 0)));

        StringBuilder message = new StringBuilder();
        int totalViviendasNoOmitidas = Seleccion.obtenerNroViviendasNoOmitidas(idUpm);
        if(Upm.isSorteoEspecial(idUpm)){
            message.append("<b>SELECCIONADAS: " + Seleccion.obtenerSeleccionViviendasNoOmitidas(idUpm)+ "</b><br><br>");
        }else {
            message.append("<b>SELECCIONADAS: " + Seleccion.seleccionRemplazo(Upm.getCodigo(idAsignacion).endsWith("-A") ? 1 : 0, Seleccion.obtenerNroViviendasNoOmitidas(idUpm), Seleccion.getVoeReemplazo(idUpm)) + "</b><br><br>");
        }

//            String [] list=Seleccion.obtenerNroViviendasOmitidas(idUpm).split("\\s*,\\s*");
        message.append("<b>OMITIDAS: [" + Seleccion.obtenerNroViviendasOmitidas(idUpm) + "]</b><br><br>");
        message.append("<b>VOES DE REMPLAZO: [" + Seleccion.getVoeReemplazo(idUpm).toString().replace(",", "),(").replace("{","(").replace("}",")").replace("=","-") + "]</b><br><br>");
        message.append("<b>REGISTRADOS:</b><br>");
        int val = Informante.getInformantesRespuesta(idUpm, Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA, true, 1);
        ///Datos directo de las consultas generadas... en la UPM de referencia
        Map<String, Object> datosLv = Informante.obtenerCabaceraListadoViviendas(idAsignacion);
//
//            message.append("<font color=#DDEBBE><b>VIVIENDAS OCUPADAS: </b>" + val + "</font><br>");
//            val = Informante.getInformantesRespuesta(idUpm, Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA, true, 2);
//            message.append("<font color=#DDEBBE><b>VIVIENDAS DESOCUPADAS: </b>" + val + "</font><br>");
//            val = Informante.getInformantesRespuesta(idUpm, Parametros.CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA, true, 3);
//
//            message.append("<font color=#DDEBBE><b>OTROS(MERCADO, PARQUES,ETC…): </b>" + val + "</font><br>");
//            val = Informante.getInformantesRespuesta(idUpm, Parametros.CODIGO_PREGUNTA_NRO_HOGARES, false, null);
//            message.append("<font color=#DDEBBE><b>Nº TOTAL DE HOGARES: </b>" + val + "</font><br>");
//            val = Informante.getInformantesRespuesta(idUpm, Parametros.CODIGO_PREGUNTA_NRO_DE_HOMBRES, false, null);
//            message.append("<font color=#DDEBBE><b>Nº TOTAL DE HOMBRES: </b>" + val + "</font><br>");
//            val = Informante.getInformantesRespuesta(idUpm, Parametros.CODIGO_PREGUNTA_NRO_DE_MUJERES, false, null);
//            message.append("<font color=#DDEBBE><b>Nº TOTAL DE MUJERES: </b>" + val + "</font><br>");


        message.append("<font color=#DDEBBE><b>VIVIENDAS OCUPADAS: </b>" + String.valueOf(datosLv.get(Informante.PARAM_VIVIENDAS_OCUPADAS)) + "</font><br>");
        message.append("<font color=#DDEBBE><b>VIVIENDAS DESOCUPADAS: </b>" + String.valueOf(datosLv.get(Informante.PARAM_VIVIENDAS_DESOCUPADAS)) + "</font><br>");
        message.append("<font color=#DDEBBE><b>OTROS(MERCADO, PARQUES,ETC…): </b>" + String.valueOf(datosLv.get(Informante.PARAM_VIVIENDAS_OTROS)) + "</font><br><br>");
        message.append("<font color=#DDEBBE><b>Nº TOTAL DE HOGARES: </b>" + String.valueOf(datosLv.get(Informante.PARAM_TOTAL_HOGARES)) + "</font><br>");
        message.append("<font color=#DDEBBE><b>Nº TOTAL DE HOMBRES: </b>" + String.valueOf(datosLv.get(Informante.PARAM_TOTAL_HOMBRRES)) + "</font><br>");
        message.append("<font color=#DDEBBE><b>Nº TOTAL DE MUJERES: </b>" + String.valueOf(datosLv.get(Informante.PARAM_TOTAL_MUJERES)) + "</font><br><br>");

        message.append("<font color=#DDEBBE><b>MARCO MUESTRAL </b></font><br>");
        message.append("<font color=#DDEBBE><b>TOTAL VIVIENDAS OCUPADAS MARCO: </b>" + String.valueOf(datosLv.get(Informante.PARAM_VIVIENDAS_OCUPADAS_MARCO)) + "</font><br>");
        message.append("<font color=#DDEBBE><b>TOTAL VIVIENDAS DESOCUPADAS MARCO: </b>" + String.valueOf(datosLv.get(Informante.PARAM_VIVIENDAS_DESOCUPADAS_MARCO)) + "</font><br>");

        txtLVcabeceraDatosActuales.setText(Html.fromHtml(message.toString()));
//        } else {
//            txtLVcabeceraValor.setText("No ha iniciado la cabecera del LV");
//            txtLVcabeceraDescripcion.setText("(Haga click en el botón de abajo para iniciar el LV)");
//            txtLVcabeceraDatosActuales.setVisibility(View.GONE);
//        }
    }

    public void seleccionar() {
//        Seleccion.preSorteo(idUpm);
        if (Upm.getCodigo(idAsignacion).endsWith("A")) {
            Seleccion.sortearOmitidas(1, idUpm);
        } else {
            Seleccion.sortearOmitidas(0, idUpm);
        }
//        drawerLayout.openDrawer(GravityCompat.END);
        cargarCabecera();
        cargarListado();
    }

    public void descargaExitosa() {
        SharedPreferences preferences = this.getSharedPreferences(Parametros.SIGLA_PROYECTO + ".xml", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("lv" + idUpm, true);
        editor.apply();

        cargarCabecera();
        cargarListado();
        drawerLayout.openDrawer(GravityCompat.END);
    }

    public void procesoExitoso() {
        cargarCabecera();
        cargarListado();
        drawerLayout.openDrawer(GravityCompat.END);
    }

    public void agregarListado() {
        Integer id = Asignacion.get_asignacion(idUpm, Usuario.getUsuario());
        if (id == null) {
            errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("UPM no asignada"), Parametros.FONT_OBS);
        } else {
            if (RolPermiso.tienePermiso(Usuario.getRol(), "activity_listado_viviendas")) {
                if (!Informante.existeCabecera(new IdInformante(id, 0))) {
                    creaNuevaCabecera(idAsignacion, idUpm);
                }

                irEncuesta(new IdInformante(id, 0), new IdEncuesta(0, 0, 0, 1), 1, 2, 0, Asignacion.getInformanteCabeceraViviendas(idAsignacion), 1, idUpmHijo);
            } else {
//                irEncuesta(new IdInformante(id, 0), new IdEncuesta(0,0,0), 1, 2, 0, new IdInformante(0, 0),1);
            }
            finish();
        }
    }

    public void cargarListado() {
        animation = AnimationUtils.loadAnimation(this, R.anim.item_anim_from_right);
        LayoutAnimationController controller = new LayoutAnimationController(animation, 0.3f);
        try {
            Informante informante = new Informante();
            valores = informante.obtenerListadoViviendas(idUpm, Seleccion.hasSelected(idUpm), 0);
            adapterRecyclerViewListadoViviendas = new ListadoViviendasAdapterRecycler(this, valores);
            adapterRecyclerViewListadoViviendas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String a = valores.get(list.getChildAdapterPosition(v)).get("id_asignacion").toString();
                    try {
                        Map<String, Object> val = valores.get(list.getChildAdapterPosition(v));
                        irEncuesta(new IdInformante((Integer) val.get("id_asignacion"), (Integer) val.get("correlativo")), new IdEncuesta(0, 0, 0, 1), 1, 2, 0, new IdInformante((Integer) val.get("id_asignacion_padre"), (Integer) val.get("correlativo_padre")), 1, idUpmHijo);
                        finish();
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            });
            //longClick
            registerForContextMenu(list);
            adapterRecyclerViewListadoViviendas.setOnLongItemClickListener(new ListadoViviendasAdapterRecycler.onLongItemClickListener() {
                @Override
                public void ItemLongClicked(View v, int position) {
                    mCurrentItemPosition = position;
                    v.showContextMenu();
                }
            });
            if (valores.size() > 0) {
                boletaVacia.setVisibility(View.GONE);
                list.setAdapter(adapterRecyclerViewListadoViviendas);
            }

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(list);
//            ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(this);
//            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
//            itemTouchHelper.attachToRecyclerView(list);

            invalidateOptionsMenu();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public void cargarListadoPrueba() {
        animation = AnimationUtils.loadAnimation(this, R.anim.item_anim_from_right);
        LayoutAnimationController controller = new LayoutAnimationController(animation, 0.3f);
        try {
            Informante informante = new Informante();
            valores = informante.obtenerListadoViviendasPrueba(idUpm, Seleccion.hasSelected(idUpm));
            adapterRecyclerViewListadoViviendas = new ListadoViviendasAdapterRecycler(this, valores);
            adapterRecyclerViewListadoViviendas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String a = valores.get(list.getChildAdapterPosition(v)).get("id_asignacion").toString();
                    try {
                        Map<String, Object> val = valores.get(list.getChildAdapterPosition(v));
                        irEncuesta(new IdInformante((Integer) val.get("id_asignacion"), (Integer) val.get("correlativo")), new IdEncuesta(0, 0, 0, 1), 1, 2, 0, new IdInformante((Integer) val.get("id_asignacion_padre"), (Integer) val.get("correlativo_padre")), 1, idUpmHijo);
                        finish();
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            });
            if (valores.size() > 0) {
                boletaVacia.setVisibility(View.GONE);
                list.setAdapter(adapterRecyclerViewListadoViviendas);
            }

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(list);
//
//            ItemTouchHelper.Callback callback = new MyItemTouchHelperCallback(this);
//            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
//            itemTouchHelper.attachToRecyclerView(list);

            invalidateOptionsMenu();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public void download_lv() {
        try {
            successMethod = "descargaExitosa";
            errorMethod = null;
            startThree();
            new DownloadFileJson().execute(Parametros.URL_DOWNLOAD, Parametros.TABLAS_DESCARGA_LV, Usuario.getUsuario() + "/" + idUpm, "Los datos se importaron correctamente.",Usuario.getPlainToken());
        } catch (Exception ex) {
            errorMessage(ListadoViviendasActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
        }
    }

    @SuppressWarnings("unused")
    public void descarga_lv() {
        try {
            successMethod = "procesoExitoso";
            errorMethod = null;
            startThree();
            new DownloadFileJson().execute(Parametros.URL_DOWNLOAD, Parametros.TABLAS_REORDENA_LV, Usuario.getUsuario() + "/" + idUpm, "Los datos se importaron correctamente.",Usuario.getPlainToken());
        } catch (Exception ex) {
            errorMessage(ListadoViviendasActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
        }
    }

    @SuppressWarnings("unused")
    public void consolida_lv() {
        try {
            startThree();
            new enviaJson().execute(Parametros.URL_DOWNLOAD, String.valueOf(Usuario.getUsuario()));
        } catch (Exception ex) {
            errorMessage(ListadoViviendasActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
        }
    }

    @SuppressWarnings("unused")
    public void consolidar_lv() {
        try {
            startThree();
            new enviaJson().execute(Parametros.URL_DOWNLOAD, String.valueOf(Usuario.getUsuario()));
        } catch (Exception ex) {
            errorMessage(ListadoViviendasActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
        }
    }

//    @SuppressWarnings("unused")
//    public void enviar() {
//        IdInformante boleta = new IdInformante(0, 0);
//        String file = Parametros.DIR_BAK + backup(boleta, 0);
//        File login = new File(Parametros.DIR_BAK + Usuario.getLogin() + "_" + backup(boleta, 0));
//        if (new File(file).renameTo(login)) {
//            startThree();
//            new BluetoothClient().execute(String.valueOf(id), String.valueOf(login));
//        } else {
//            new BluetoothClient().execute(String.valueOf(id), file);
//        }
//    }

    @Override
    public void onItemClick(int mPosition) {
        Map<String, Object> values = valores.get(mPosition);
        if (seleccionable == null) {
            Informante informante = new Informante();
            if (informante.abrir(new IdInformante((Integer) values.get("id_asignacion"), (Integer) values.get("correlativo")))) {
                irEncuestaInicial(informante.get_id_informante(), 0, new IdInformante(0, 0), idUpmHijo);
                finish();
            } else {
                errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
            }
        } else {
            String currentState = (String) values.get("estado");
            switch (seleccionable) {
                case "DESELECCIONAR":
                    if (currentState.startsWith("SELECCIONADO")) {
                        idTemp = new IdInformante((Integer) values.get("id_asignacion"), (Integer) values.get("correlativo"));
                        values.put("estado", "DESELECCIONADO");
                        seleccionable = "SELECCIONAR";
                        informationMessage(ListadoViviendasActivity.this, null, "INFORMACIÓN", Html.fromHtml("Seleccione la nueva vivienda SELECCIONADA."), Parametros.FONT_OBS);
                    } else {
                        errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("Debe elegir una vivienda SELECCIONADA."), Parametros.FONT_OBS);
                    }
                    break;
                case "SELECCIONAR":
                    if (idTemp.id_asignacion == (Integer) values.get("id_asignacion") && idTemp.correlativo == (Integer) values.get("correlativo")) {
                        errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No puede elegir el mismo predio"), Parametros.FONT_OBS);
                    } else {
                        if (currentState.startsWith("ELABORADO")) {
                            Informante informante = new Informante();
                            if (informante.abrir(idTemp)) {
                                String tempState = informante.get_apiestado().toString();
                                String part[] = tempState.split(";");
                                if (informante.editar()) {
                                    String nuevoEstado;
                                    if (part.length > 1) {
                                        nuevoEstado = tempState + "-D";
                                    } else {
                                        nuevoEstado = tempState + ";S-D";
                                    }
                                    nuevoEstado = nuevoEstado.replace("SELECCIONADO", "ELABORADO");
                                    informante.set_apiestado(nuevoEstado);
                                    informante.guardar();
                                    idTemp = null;
                                } else {
                                    informante.cerrar();
                                    errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se pudo editar."), Parametros.FONT_OBS);
                                }
                            } else {
                                errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
                            }
                            if (informante.abrir(new IdInformante((Integer) values.get("id_asignacion"), (Integer) values.get("correlativo")))) {
                                String part[] = currentState.split(";");
                                if (informante.editar()) {
                                    String nuevoEstado;
                                    if (part.length > 1) {
                                        nuevoEstado = currentState + "-R" + values.get("codigo");
                                    } else {
                                        nuevoEstado = currentState + ";E-R" + values.get("codigo");
                                    }
                                    nuevoEstado = nuevoEstado.replace("ELABORADO", "SELECCIONADO");
                                    informante.set_apiestado(nuevoEstado);
                                    informante.guardar();
                                } else {
                                    informante.cerrar();
                                    errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se pudo editar."), Parametros.FONT_OBS);
                                }
                            } else {
                                errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
                            }
                            seleccionable = null;
                            cargarListado();
                        } else {
                            errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No puede elegir esa vivienda. Elija una vivienda en estado ELABORADO."), Parametros.FONT_OBS);
                        }
                    }
                    break;
            }

            /*String currentState = (String)values.get("estado");
            switch (seleccionable) {
                case "DESELECCIONAR":
                    if (currentState.startsWith("SELECCIONADO")) {
                        idTemp = new IdInformante((Integer) values.get("id_asignacion"), (Integer) values.get("correlativo"));
                        values.put("estado", "DESELECCIONADO");
                        seleccionable = "SELECCIONAR";
                        informationMessage(null, "INFORMACIÓN", Html.fromHtml("Seleccione la nueva vivienda SELECCIONADA."), Parametros.FONT_OBS);
                    } else {
                        errorMessage(null, "Error!", Html.fromHtml("Debe elegir una vivienda SELECCIONADA."), Parametros.FONT_OBS);
                    }
                    break;
                case "SELECCIONAR":
                    if (idTemp.id_asignacion == (Integer) values.get("id_asignacion") && idTemp.correlativo == (Integer) values.get("correlativo")) {
                        errorMessage(null, "Error!", Html.fromHtml("No puede elegir el mismo predio"), Parametros.FONT_OBS);
                    } else {
                        if (currentState.startsWith("SELECCIONADO") || currentState.startsWith("DESELECCIONADO")) {
                            errorMessage(null, "Error!", Html.fromHtml("No puede elegir esa vivienda. Elija una vivienda en estado ELABORADO."), Parametros.FONT_OBS);
                        } else {
                            Informante informante = new Informante();
                            if (informante.abrir(idTemp)) {
                                if (informante.editar()) {
                                    informante.set_apiestado(Estado.DESELECCIONADO);
                                    informante.guardar();
                                    idTemp = null;
                                    cargarListado();
                                } else {
                                    informante.cerrar();
                                    errorMessage(null, "Error!", Html.fromHtml("No se pudo editar."), Parametros.FONT_OBS);
                                }
                            } else {
                                errorMessage(null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
                            }
                            if (informante.abrir(new IdInformante((Integer) values.get("id_asignacion"), (Integer) values.get("correlativo")))) {
                                if (informante.editar()) {
                                    informante.set_apiestado(Estado.SELECCIONADO2);
                                    informante.guardar();
                                    seleccionable = null;
                                    cargarListado();
                                } else {
                                    informante.cerrar();
                                    errorMessage(null, "Error!", Html.fromHtml("No se pudo editar."), Parametros.FONT_OBS);
                                }
                            } else {
                                errorMessage(null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
                            }
                        }
                    }
                    break;
            }*/
        }
    }


    @Override
    public void onLongItemClick(int mPosition) {
        Log.d("OnLOngClick", "Onlong");
//        openContextMenu(this.getViewByPosition(mPosition,list));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cargarListado();
    }

    public void ordenManual(int mPosition) {
        Map<String, Object> values = valores.get(mPosition);
        codigo = Integer.parseInt(values.get("codigo").toString());
        numeroMessage(ListadoViviendasActivity.this, "mover", null, Html.fromHtml("Orden:"), codigo);
    }

    @SuppressWarnings("unused")
    public void mover() {
        try {
            int codigoDestino = Integer.parseInt(observation);
            if (codigoDestino > 0 && codigoDestino <= valores.size()) {
                if (codigo > codigoDestino) {
                    Informante informante = new Informante();
                    if (informante.abrir("id_nivel = 2 AND id_upm = " + idUpm, "CAST(codigo AS Int)")) {
                        int i = 1;
                        while (i < codigoDestino) {
                            informante.siguiente();
                            i++;
                        }
                        while (i < codigo) {
                            if (informante.editar()) {
                                informante.set_codigo(String.valueOf(i + 1));
                                informante.guardar2();
                                informante.siguiente();
                                i++;
                            } else {
                                errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se ha podido editar."), Parametros.FONT_OBS);
                                break;
                            }
                        }
                        if (informante.editar()) {
                            informante.set_codigo(String.valueOf(codigoDestino));
                            informante.guardar2();
                        } else {
                            errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se ha podido editar."), Parametros.FONT_OBS);
                        }
                        cargarListado();
                    } else {
                        errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se encontraron informantes."), Parametros.FONT_OBS);
                    }
                    informante.free();
                } else {
                    Informante informante = new Informante();
                    if (informante.abrir("id_nivel = 2 AND id_upm = " + idUpm, "CAST(codigo AS Int)")) {
                        int i = 1;
                        while (i < codigo) {
                            informante.siguiente();
                            i++;
                        }
                        if (informante.editar()) {
                            informante.set_codigo(String.valueOf(codigoDestino));
                            informante.guardar2();
                            informante.siguiente();
                        } else {
                            errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se ha podido editar."), Parametros.FONT_OBS);
                        }
                        while (i < codigoDestino) {
                            if (informante.editar()) {
                                informante.set_codigo(String.valueOf(i));
                                informante.guardar2();
                                informante.siguiente();
                                i++;
                            } else {
                                errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se ha podido editar."), Parametros.FONT_OBS);
                                break;
                            }
                        }
                        cargarListado();
                    } else {
                        errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se encontraron informantes."), Parametros.FONT_OBS);
                    }
                    informante.free();
                }
            } else {
                errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("Fuera de rango."), Parametros.FONT_OBS);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unused")
    public void borrar() {

//        successMethod="cargarListado";
//        Upm.borrar(idUpm, Usuario.getUsuario());
//        Upm.borrarTodo(idUpm,idAsignacion);
////        cargarCabecera();
////        cargarListado();
//        Bundle bundle=new Bundle();
//        bundle.putInt("idUpm",idUpm);
//        Intent intent=getIntent();
//        intent.putExtras(bundle);
//        finish();
//        startActivity(intent);
        informationMessage(this, null, "Voes", Seleccion.hasSelected(idUpm)?Html.fromHtml(Informante.getResumenVoe(idUpm)):Html.fromHtml(Seleccion.seleccionViviendas(Upm.getCodigoUpm(idUpm).endsWith("A")?1:0,idUpm)), Parametros.FONT_OBS);

    }

    @SuppressWarnings("unused")
    public void anular() {
        Informante informante = new Informante();
        informante.abrir(idTemp);
        informante.editar();
        informante.set_apiestado(Estado.ANULADO);
        informante.guardar();
        Informante.actualizaCodigoInformante(idAsignacion,2);
//        cargarListado();
        Bundle bundle = new Bundle();
        bundle.putInt("idUpm", idUpm);
        Intent intent = getIntent();
        intent.putExtras(bundle);
        finish();
        startActivity(intent);
    }


    @Override
    public void onLeft(int mPosition) {
        Map<String, Object> values = valores.get(mPosition);

        int codigo = Integer.parseInt(values.get("codigo").toString());
        if (codigo > 1) {
            Informante informante = new Informante();
            if (informante.abrir("id_nivel = 2 AND estado <> 'ANULADO' AND id_upm = " + idUpm + " AND codigo = " + codigo, null)) {
                Informante informante2 = new Informante();
                if (informante2.abrir("id_nivel = 2 AND estado <> 'ANULADO' AND id_upm = " + idUpm + " AND codigo = " + (codigo - 1), null)) {
                    informante.editar();
                    informante.set_codigo(String.valueOf(codigo - 1));
                    informante.guardar();

                    informante2.editar();
                    informante2.set_codigo(String.valueOf(codigo));
                    informante2.guardar();

                    cargarListado();
                } else {
                    errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
                }
            } else {
                errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
            }
        }
    }

    @Override
    public void onRight(int mPosition) {
        Map<String, Object> values = valores.get(mPosition);

        int codigo = Integer.parseInt(values.get("codigo").toString());
        if (codigo < Informante.contar2(idAsignacion)) {
            Informante informante = new Informante();
            if (informante.abrir("id_nivel = 2 AND estado <> 'ANULADO' AND id_upm = " + idUpm + " AND codigo = " + codigo, null)) {
                Informante informante2 = new Informante();
                if (informante2.abrir("id_nivel = 2 AND estado <> 'ANULADO' AND id_upm = " + idUpm + " AND codigo = " + (codigo + 1), null)) {
                    informante.editar();
                    informante.set_codigo(String.valueOf(codigo + 1));
                    informante.guardar();

                    informante2.editar();
                    informante2.set_codigo(String.valueOf(codigo));
                    informante2.guardar();

                    cargarListado();
                } else {
                    errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
                }
            } else {
                errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_contextual_listado, menu);
        MenuItem item = menu.findItem(R.id.action_change);
        item.setVisible(false);
        item = menu.findItem(R.id.action_marcar);
        item.setVisible(false);
//        if (Seleccion.hasSelected(idUpm)) {
            item = menu.findItem(R.id.action_orden_manual);
            item.setVisible(false);
            item = menu.findItem(R.id.action_subir);
            item.setVisible(false);
            item = menu.findItem(R.id.action_bajar);
            item.setVisible(false);
        if (Seleccion.hasSelected(idUpm)) {
            item = menu.findItem(R.id.action_eliminar);
            item.setVisible(false);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info;
        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Map<String, Object> values = valores.get(mCurrentItemPosition);
        switch (item.getItemId()) {
            case R.id.action_marcar:
                ordenManual(mCurrentItemPosition);
                return false;
            case R.id.action_orden_manual:
                ordenManual(mCurrentItemPosition);
                return true;
            case R.id.action_eliminar:
                decisionMessage(ListadoViviendasActivity.this, "anular", null, "Confirmar", Html.fromHtml("Se perdera la información."));
                idTemp = new IdInformante((Integer) values.get("id_asignacion"), (Integer) values.get("correlativo"));
                return true;
            case R.id.action_subir:
                onLeft(mCurrentItemPosition);
                return true;
            case R.id.action_bajar:
                onRight(mCurrentItemPosition);
                return true;
            case R.id.action_detalles:
                StringBuilder message = new StringBuilder();
                if (!RolPermiso.tienePermiso(Usuario.getRol(), "indices")) {
                    message.append("<b>ASIG-CORR: </b>" + values.get("id_asignacion") + "-" + values.get("correlativo") + "<br>");
                    message.append("<b>ASIG-CORR (PADRE): </b>" + values.get("id_asignacion_padre") + "-" + values.get("correlativo_padre") + "<br>");
                }
                message.append("<b>CODIGO: </b>" + values.get("codigo") + "<br>");
                message.append("<b>DESC: </b>" + values.get("descripcion") + "<br>");
                message.append("<b>ID_USUARIO: </b>" + values.get("id_usuario") + "<br>");
                message.append("<b>ID_UPM: </b>" + values.get("id_upm") + "<br>");
                message.append("<b>ESTADO: </b>" + values.get("estado") + "<br>");
                message.append("<b>CREACION: </b>" + values.get("usucre") + " - " + Movil.dateExtractor(Long.parseLong(values.get("feccre").toString())) + "<br>");
                informationMessage(ListadoViviendasActivity.this, null, "Detalles", Html.fromHtml(message.toString()), Parametros.FONT_OBS);
                return false;
            case R.id.action_change:
                idTemp = new IdInformante((Integer) values.get("id_asignacion"), (Integer) values.get("correlativo"));
                cambiaUPM(idTemp);
                return false;
        }
        ;
        return false;
    }


    public void cambiaUPM(final IdInformante idInformante) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("UPM");
        final Spinner upmSpinner = new Spinner(this);
        ArrayList<String> spinnerArray = new ArrayList<>();
        Upm upm = new Upm();
        final ArrayList<Map<String, Object>> values = upm.obtenerListado(Usuario.getUsuario());
        for (Map<String, Object> o : values) {
            spinnerArray.add(o.get("codigo").toString());
        }
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        upmSpinner.setAdapter(spinnerArrayAdapter);
        dialog.setView(upmSpinner);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Upm upm = new Upm();
                //idTemp = new IdInformante((Integer) values.get("id_asignacion"), (Integer) values.get("correlativo"));
                if (upm.abrir("codigo = '" + upmSpinner.getSelectedItem() + "'", null)) {
                    Informante informante = new Informante();
                    if (informante.abrir(idInformante)) {
                        int id = informante.get_id_nivel();
                        IdInformante idP = informante.get_id_informante_padre();
                        informante.editar();
                        informante.set_id_upm(upm.get_id_upm());
                        if (informante.get_id_nivel() == 1) {
                            String cod = informante.get_codigo();
                            cod = upm.get_codigo() + cod.substring(cod.length() - 5, cod.length());
                            informante.set_codigo(cod);
                        }
                        informante.guardar();
                        irListadoViviendas(upm.get_id_upm());
                        ListadoViviendasActivity.this.finish();
                    } else {
                        errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se encontró el informante."), Parametros.FONT_OBS);
                    }
                } else {
                    errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se encontró la UPM."), Parametros.FONT_OBS);
                }
                upm.free();
            }
        });
        dialog.setNegativeButton("Cancelar", null);
        AlertDialog alert = dialog.create();
        alert.show();
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

//    @Override
//    public void itemTouchMode(int oldPosition, int newPosition) {
//
//        valores.add(newPosition,valores.remove(oldPosition));
//        adapterRecyclerViewListadoViviendas.notifyItemMoved(oldPosition,newPosition);
//    }
//
//    @Override
//    public void onSwiped(RecyclerView.ViewHolder viewHolder, int position) {
//
//        final Map<String, Object> deletedItem = valores .get(viewHolder.getAdapterPosition());
//        final  int deletedIndex = viewHolder.getAdapterPosition();
//        adapterRecyclerViewListadoViviendas.removeItem(viewHolder.getAdapterPosition());
//        Snackbar snackbar = Snackbar.make(layout,"listado de vivienda " + "=> Eliminado", Snackbar.LENGTH_LONG);
//        snackbar.setAction("CANCELAR/UNDO", new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                adapterRecyclerViewListadoViviendas.restoredItem(deletedItem,deletedIndex);
//
//            }
//        });
//        snackbar.setActionTextColor(Color.GREEN);
//        snackbar.show();
//    }

//    public void auto() {
//        ArrayList<Map<String, Integer>> pregs = Pregunta.preguntas(0);
//        int n = Integer.valueOf(observation);
//        IdInformante idInformante = new IdInformante(idAsignacion, 0);
//        for (int i = 0; i < n; i++) {
//            Informante informante = new Informante();
//            if (informante.nuevo()) {
//                informante.set_id_informante(idInformante);
//                informante.set_id_usuario(Usuario.getUsuario());
//                informante.set_id_upm(idUpm);
//                informante.set_id_nivel(0);
//                informante.set_codigo(String.valueOf(i));
//                informante.set_descripcion("Prueba");
//                informante.set_apiestado(Estado.ELABORADO);
//                informante.set_usucre(Usuario.getLogin());
//                idInformante = informante.guardarLV(idAsignacion);
//                for (int j = 0; j < pregs.size(); j++) {
//                    Encuesta encuesta = new Encuesta();
//                    if (encuesta.nuevo()) {
//                        switch (pregs.get(j).get("id_tipo_pregunta")) {
//                            case 0: {
//                                encuesta.set_id_encuesta(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, pregs.get(j).get("id_pregunta"), 1));
//                                encuesta.set_id_respuesta(0l);
//                                encuesta.set_codigo_respuesta("0");
//                                encuesta.set_respuesta("abcd");
//                                encuesta.set_apiestado(Estado.ELABORADO);
//                                encuesta.set_usucre(Usuario.getLogin());
//                                break;
//                            }
//                            case 1: {
//                                encuesta.set_id_encuesta(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, pregs.get(j).get("id_pregunta"), 1));
//                                Map<Integer, String> resp = Respuesta.getRespuestas(pregs.get(j).get("respuestas"));
//                                Map<Integer, String> resp = Respuesta.getRespuestas(pregs.get(j).get("id_pregunta"));
//                                int val = new Random().nextInt(resp.size());
//                                int key = (Integer)resp.keySet().toArray()[val];
//                                encuesta.set_id_respuesta((long)key);
//                                encuesta.set_codigo_respuesta(resp.get(key).split("|")[0]);
//                                encuesta.set_respuesta(resp.get(key).split("|")[1]);
//                                encuesta.set_apiestado(Estado.ELABORADO);
//                                encuesta.set_usucre(Usuario.getLogin());
//                                break;
//                            }
//                            case 2: {
//                                encuesta.set_id_encuesta(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, pregs.get(j).get("id_pregunta"), 1));
//                                encuesta.set_id_respuesta(0l);
//                                String val;
//                                if (pregs.get(j).get("maximo") > 0) {
//                                    val = String.valueOf(new Random().nextInt(pregs.get(j).get("maximo")));
//                                } else {
//                                    val = String.valueOf(new Random().nextInt(255));
//                                }
//                                encuesta.set_codigo_respuesta(val);
//                                encuesta.set_respuesta(val);
//                                encuesta.set_apiestado(Estado.ELABORADO);
//                                encuesta.set_usucre(Usuario.getLogin());
//                                break;
//                            }
//                            case 19: {
//                                encuesta.set_id_encuesta(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, pregs.get(j).get("id_pregunta"), 1));
//                                encuesta.set_id_respuesta(0l);
//                                String val;
//                                if (new Random().nextInt() >= 0) {
//                                    val = "1";
//                                } else {
//                                    val = "0";
//                                }
//                                encuesta.set_codigo_respuesta(val);
//                                encuesta.set_respuesta(val);
//                                encuesta.set_apiestado(Estado.ELABORADO);
//                                encuesta.set_usucre(Usuario.getLogin());
//                                break;
//                            }
//                            case 20: {
//                                encuesta.set_id_encuesta(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, pregs.get(j).get("id_pregunta"), 1));
//                                encuesta.set_id_respuesta(0l);
//                                encuesta.set_codigo_respuesta("0");
//                                encuesta.set_respuesta("abcd");
//                                encuesta.set_apiestado(Estado.ELABORADO);
//                                encuesta.set_usucre(Usuario.getLogin());
//                                break;
//                            }case 21: {
//                                encuesta.set_id_encuesta(new IdEncuesta(idInformante.id_asignacion, idInformante.correlativo, pregs.get(j).get("id_pregunta"), 1));
//                                encuesta.set_id_respuesta(0l);
//                                encuesta.set_codigo_respuesta("0");
//                                encuesta.set_respuesta("abcd");
//                                encuesta.set_apiestado(Estado.ELABORADO);
//                                encuesta.set_usucre(Usuario.getLogin());
//                                break;
//                            }
//                        }
//                        encuesta.guardar();
//                    }
//                }
//            }
//        }
//    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

//        int fromPosition = viewHolder.getAdapterPosition();
//        int toPosition = target.getAdapterPosition();
//        Collections.swap(valores, fromPosition, toPosition);
//        recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//        int position = viewHolder.getAdapterPosition();
//        valores.remove(position);
//        decisionMessage(ListadoViviendasActivity.this, "anular", null, "Confirmar", Html.fromHtml("Se perdera la información."));
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            if (viewHolder != null && viewHolder.getAdapterPosition() > -1) return 0;
            return super.getMovementFlags(recyclerView, viewHolder);
        }
    };

    @SuppressWarnings("unused")
    public void cargar_viviendas_anteriores() {
        new InsertaViviendasAnteriores().execute();
    }

    // EN CASO DE SER REVISITA, INSERTA VIVIENDAS DEL ANTERIOR TRIMESTRE
//    public class InsertaViviendasAnteriores1 extends AsyncTask<String, String, String> {
//        ProgressDialog dialog;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog = new ProgressDialog(ListadoViviendasActivity.this);
//            dialog.setMessage("Procesando...");
//            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//            dialog.setTitle(getString(R.string.app_name));
//            dialog.show();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                StringBuilder textCodigos=new StringBuilder();
//                // CONTROL PARA LAS VIVIENDAS RECICLADAS O REVISITAS
//                ArrayList<Map<String, Object>> informantePadreAnterior = null;
//                ArrayList<Map<String, Object>> informanteAnterior;
//                int id_base=0;
//                if (nroRevisita > 0&&nroRevisita<8) {
//                    informantePadreAnterior = new InformanteAnterior().obtenerListado("id_nivel = 1 AND id_upm = " + idUpm+" AND id_base=2 ", "CAST(codigo AS Int)");
//                }
//                if(informantePadreAnterior != null && informantePadreAnterior.size()>0){
//                    Informante informantePadre = new Informante();
//                    Map<String,Object> mapInformantePadreAnterior=informantePadreAnterior.get(0);
//                    if(informantePadre.nuevo()){
//                        informantePadre.set_id_nivel((Integer) mapInformantePadreAnterior.get("id_nivel"));
//                        informantePadre.set_id_upm((Integer) mapInformantePadreAnterior.get("id_upm"));
//                        informantePadre.set_id_informante(new IdInformante(idAsignacion,0));
//                        informantePadre.set_codigo((String) mapInformantePadreAnterior.get("codigo"));
//                        informantePadre.set_descripcion((String) mapInformantePadreAnterior.get("descripcion"));
//                        informantePadre.set_usucre(Usuario.getLogin());
//                        informantePadre.set_id_informante_anterior(new IdInformante((Integer) mapInformantePadreAnterior.get("id_asignacion"),(Integer) mapInformantePadreAnterior.get("correlativo")));
//                        informantePadre.set_codigo_anterior((String) mapInformantePadreAnterior.get("codigo"));
//                        informantePadre.set_id_usuario(Usuario.getUsuario());
//
//                        IdInformante idInformantePadreNuevo=null;
//                        switch ((Integer) mapInformantePadreAnterior.get("id_nivel")) {
//                            case 1:
//                                idInformantePadreNuevo =  (IdInformante)informantePadre.guardar();
//                                break;
//
//                        }
//                        ///inserta encuesta de padre
//                        ArrayList<Map<String, Object>> encuestaAnteriorPadre = new EncuestaAnterior().obtenerListado("id_asignacion = " + mapInformantePadreAnterior.get("id_asignacion")+ " AND correlativo ="+ mapInformantePadreAnterior.get("correlativo"),"");
//                        Encuesta encuestaPadre = new Encuesta();
//                        for (Map<String, Object> r : encuestaAnteriorPadre) {
//                            if (encuestaPadre.nuevo()) {
//                                if(idInformantePadreNuevo!=null)
//                                    encuestaPadre.set_id_encuesta(new IdEncuesta(idInformantePadreNuevo.id_asignacion,idInformantePadreNuevo.correlativo,(Integer)r.get("id_pregunta")));
//                                encuestaPadre.set_codigo_respuesta((String) r.get("codigo_respuesta"));
//                                encuestaPadre.set_respuesta((String) r.get("respuesta"));
//                                encuestaPadre.set_observacion((String) r.get("observacion"));
//                                encuestaPadre.set_usucre(Usuario.getLogin());
//                                encuestaPadre.set_visible("true");
//                                switch ((String) r.get("codigo_respuesta")) {
////                                    case "997":
////                                        encuestaPadre.set_estado(Estado.NOAPLICA);
////                                        break;
////                                    case "998":
////                                        encuestaPadre.set_estado(Estado.SENIEGA);
////                                        break;
////                                    case "999":
////                                        encuestaPadre.set_estado(Estado.NOSABE);
////                                        break;
//                                    default:
//                                        encuestaPadre.set_estado(Estado.INSERTADO);
//                                        break;
//                                }
//                                encuestaPadre.guardar();
//                            } else {
//                                return "No se ha podido crear la respuesta.";
//                            }
//                        }
//
////                        informanteAnterior = new InformanteAnterior().obtenerListado("id_nivel = 2 AND id_upm = " + idUpm+" AND id_base=2 AND id_asignacion_padre= "+(Integer)mapInformantePadreAnterior.get("id_asignacion")+" AND correlativo_padre= "+(Integer)mapInformantePadreAnterior.get("correlativo"), "CAST(codigo AS Int)");
//                        informanteAnterior = new InformanteAnterior().obtenerListado("id_nivel = 2 AND id_upm = " + idUpm+" AND id_base=2 ", "CAST(codigo AS Int)");
//                        //informantes Hijos
//
//                        Informante informante = new Informante();
//
//                        for (Map<String, Object> i : informanteAnterior) {
//                            textCodigos.append(",").append((String) i.get("codigo"));
//                            if (informante.nuevo()) {
//
//                                informante.set_id_nivel((Integer) i.get("id_nivel"));
//                                informante.set_id_upm((Integer) i.get("id_upm"));
//                                informante.set_id_upm_hijo((Integer) i.get("id_upm_hijo"));
//                                informante.set_id_informante(new IdInformante(idAsignacion,0));
//                                informante.set_codigo((String) i.get("codigo"));
//                                informante.set_descripcion((String) i.get("descripcion"));
//
//                                informante.set_usucre(Usuario.getLogin());
//                                informante.set_id_informante_anterior(new IdInformante((Integer) i.get("id_asignacion"),(Integer) i.get("correlativo")));
//                                informante.set_codigo_anterior((String) i.get("codigo"));
//                                informante.set_id_usuario(Usuario.getUsuario());
////                        informante.set_estado((Estado) i.get("estado"));
//                                informante.set_apiestado(((String) i.get("estado")).equals("SELECCIONADO")?Estado.SELECCIONADO:Estado.ELABORADO);
////                                if(idInformantePadreNuevo!=null){
////                                    informante.set_id_informante_padre(idInformantePadreNuevo);
////                                }
//                                IdInformante idInformanteNuevo;
////                                switch ((Integer) i.get("id_nivel")) {
////                                    case 2:
//                                        idInformanteNuevo = (IdInformante) informante.guardar();
////                                        break;
////                                    case 3:
////                                        idInformanteNuevo = (IdInformante)informante.guardar();
////                                        break;
////                                    default:
////                                        idInformanteNuevo = informante.guardar(new IdInformante(idAsignacionPadre,correlativoPadre));
////                                        break;
////                                }
//
//                                ArrayList<Map<String, Object>> encuestaAnterior = new EncuestaAnterior().obtenerListado("id_asignacion = " + i.get("id_asignacion")+ " AND correlativo="+ i.get("correlativo"),"");
//                                Encuesta encuesta = new Encuesta();
//                                for (Map<String, Object> r : encuestaAnterior) {
//                                    if (encuesta.nuevo()) {
//                                        if(idInformanteNuevo!=null)
//                                            encuesta.set_id_encuesta(new IdEncuesta(idInformanteNuevo.id_asignacion,idInformanteNuevo.correlativo,(Integer)r.get("id_pregunta")));
//                                        encuesta.set_codigo_respuesta((String) r.get("codigo_respuesta"));
//                                        encuesta.set_respuesta((String) r.get("respuesta"));
//                                        encuesta.set_observacion((String) r.get("observacion"));
//                                        encuesta.set_usucre(Usuario.getLogin());
//                                        encuesta.set_visible("true");
//                                        switch ((String) r.get("codigo_respuesta")) {
////                                            case "997":
////                                                encuesta.set_estado(Estado.NOAPLICA);
////                                                break;
////                                            case "998":
////                                                encuesta.set_estado(Estado.SENIEGA);
////                                                break;
////                                            case "999":
////                                                encuesta.set_estado(Estado.NOSABE);
////                                                break;
//                                            default:
////                                                encuesta.set_estado(Estado.ELABORADO);
//                                                encuesta.set_estado(Estado.INSERTADO);
//                                                break;
//                                        }
//                                        encuesta.guardar();
//                                    } else {
//                                        return "No se ha podido crear la respuesta.";
//                                    }
//                                }
//                                Log.d("generados",textCodigos.toString());
//                            } else {
//                                return "No se ha podido crear el informante.";
//                            }
//                        }
//                    }
//
//
//                }else if(nroRevisita>0&&nroRevisita<8) {
//                    if(!Informante.listadoViviendasInicializado(idAsignacion)){
//                        creaNuevaCabecera(idAsignacion,idUpm);
//                    }
////                        Informante informantePadre = new Informante();
////                        if (informantePadre.nuevo()) {
////                            informantePadre.set_id_nivel(1);
////                            informantePadre.set_id_upm(idUpm);
////                            informantePadre.set_id_informante(new IdInformante(idAsignacion, 0));
//////                            informantePadre.set_codigo((String) mapInformantePadreAnterior.get("codigo"));
//////                            informantePadre.set_descripcion((String) mapInformantePadreAnterior.get("descripcion"));
////                            informantePadre.set_usucre(Usuario.getLogin());
////                            informantePadre.set_id_informante_anterior(null);
//////                            informantePadre.set_id_id_informante_anterior((Long) mapInformantePadreAnterior.get("id_inforamnte_anterior"));
//////                            informantePadre.set_codigo_anterior((String) mapInformantePadreAnterior.get("codigo"));
////                            informantePadre.set_id_usuario(Usuario.getUsuario());
//
//                            Informante informantePadreNuevo = new Informante();
//                            IdInformante idInformantePadreNuevo = null;
//                    if(informantePadreNuevo.abrir("id_asignacion="+idAsignacion+" AND id_nivel= 1","")) {
//                        idInformantePadreNuevo=informantePadreNuevo.get_id_informante();
//                    }
//
////                            switch ((Integer) mapInformantePadreAnterior.get("id_nivel")) {
////                                case 1:
////                                    idInformantePadreNuevo = (IdInformante) informantePadre.guardar();
////                                    break;
////
////                            }
//                            ///inserta encuesta de padre
//                            //para el caso de la migracion establecer encuesta default
////                            Pregunta p=new Pregunta();
////
////                            ArrayList<Map<String, Object>> encuestaAnteriorPadre = new EncuestaAnterior().obtenerListado("id_asignacion = " + mapInformantePadreAnterior.get("id_asignacion") + " AND correlativo =" + mapInformantePadreAnterior.get("correlativo"), "");
////                            Encuesta encuestaPadre = new Encuesta();
////                            for (Map<String, Object> r : encuestaAnteriorPadre) {
////                                if (encuestaPadre.nuevo()) {
////                                    if (idInformantePadreNuevo != null)
////                                        encuestaPadre.set_id_encuesta(new IdEncuesta(idInformantePadreNuevo.id_asignacion, idInformantePadreNuevo.correlativo, (Integer) r.get("id_pregunta")));
////                                    encuestaPadre.set_codigo_respuesta((String) r.get("codigo_respuesta"));
////                                    encuestaPadre.set_respuesta((String) r.get("respuesta"));
////                                    encuestaPadre.set_observacion((String) r.get("observacion"));
////                                    encuestaPadre.set_usucre(Usuario.getLogin());
////                                    switch ((String) r.get("codigo_respuesta")) {
////                                        case "997":
////                                            encuestaPadre.set_estado(Estado.NOAPLICA);
////                                            break;
////                                        case "998":
////                                            encuestaPadre.set_estado(Estado.SENIEGA);
////                                            break;
////                                        case "999":
////                                            encuestaPadre.set_estado(Estado.NOSABE);
////                                            break;
////                                        default:
////                                            encuestaPadre.set_estado(Estado.ELABORADO);
////                                            break;
////                                    }
////                                    encuestaPadre.guardar();
////                                } else {
////                                    return "No se ha podido crear la respuesta.";
////                                }
////                            }
//
//                            informanteAnterior = new InformanteAnterior().obtenerListado("id_nivel = 1 AND id_upm = " + idUpm + " AND id_base=1 ", "CAST(codigo AS Int)");
//                            //informantes Hijos
//
//                            Informante informante = new Informante();
//
//                            for (Map<String, Object> i : informanteAnterior) {
//                                textCodigos.append(",").append((String) i.get("codigo"));
//                                if (informante.nuevo()) {
//
//                                    informante.set_id_nivel((Integer) i.get("id_nivel"));
//                                    informante.set_id_upm((Integer) i.get("id_upm"));
//                                    informante.set_id_informante(new IdInformante(0, 0));
//                                    informante.set_codigo((String) i.get("codigo"));
//                                    informante.set_descripcion((String) i.get("descripcion"));
//
//                                    informante.set_usucre(Usuario.getLogin());
//                                    informante.set_id_informante_anterior(new IdInformante((Integer) i.get("id_asignacion"), (Integer) i.get("correlativo")));
//                                    informante.set_id_id_informante_anterior((Long) i.get("id_informante_anterior"));
//                                    informante.set_codigo_anterior((String) i.get("codigo"));
//                                    informante.set_id_usuario(Usuario.getUsuario());
////                        informante.set_estado((Estado) i.get("estado"));
//                                    informante.set_apiestado(((String) i.get("estado")).equals("SELECCIONADO") ? Estado.SELECCIONADO : Estado.ELABORADO);
//                                    if (idInformantePadreNuevo != null) {
//                                        informante.set_id_informante_padre(idInformantePadreNuevo);
//                                    }
//                                    IdInformante idInformanteNuevo;
////                                switch ((Integer) i.get("id_nivel")) {
////                                    case 2:
//                                    idInformanteNuevo = (IdInformante) informante.guardar();
////                                        break;
////                                    case 3:
////                                        idInformanteNuevo = (IdInformante)informante.guardar();
////                                        break;
////                                    default:
////                                        idInformanteNuevo = informante.guardar(new IdInformante(idAsignacionPadre,correlativoPadre));
////                                        break;
////                                }
//
//                                    ArrayList<Map<String, Object>> encuestaAnterior = new EncuestaAnterior().obtenerListado("id_asignacion = " + i.get("id_asignacion") + " AND correlativo=" + i.get("correlativo"), "");
//                                    Encuesta encuesta = new Encuesta();
//                                    for (Map<String, Object> r : encuestaAnterior) {
//                                        if (encuesta.nuevo()) {
//                                            if (idInformanteNuevo != null)
//                                                encuesta.set_id_encuesta(new IdEncuesta(idInformanteNuevo.id_asignacion, idInformanteNuevo.correlativo, (Integer) r.get("id_pregunta")));
//                                            encuesta.set_codigo_respuesta((String) r.get("codigo_respuesta"));
//                                            encuesta.set_respuesta((String) r.get("respuesta"));
//                                            encuesta.set_observacion((String) r.get("observacion"));
//                                            encuesta.set_usucre(Usuario.getLogin());
//                                            encuesta.set_visible("true");
//                                            switch ((String) r.get("codigo_respuesta")) {
////                                                case "997":
////                                                    encuesta.set_estado(Estado.NOAPLICA);
////                                                    break;
////                                                case "998":
////                                                    encuesta.set_estado(Estado.SENIEGA);
////                                                    break;
////                                                case "999":
////                                                    encuesta.set_estado(Estado.NOSABE);
////                                                    break;
//                                                default:
//                                                    encuesta.set_estado(Estado.INSERTADO);
//                                                    break;
//                                            }
//                                            encuesta.guardar();
//                                        } else {
//                                            return "No se ha podido crear la respuesta.";
//                                        }
//                                    }
//                                    Log.d("generados", textCodigos.toString());
//                                } else {
//                                    return "No se ha podido crear el informante.";
//                                }
//                            }
//                        }
//
//
//                return "Ok";
//            } catch (Exception e) {
//                return e.getMessage();
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            dialog.dismiss();
//            if( result.equals("Ok") ) {
//
//                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//                finish();
//                startActivity(getIntent());
//            } else if(result.equals(CREAR_CABECERA)){
////                irEncuesta(new IdInformante(id, 0), new IdEncuesta(0,0,0), 1, 1, 0, new IdInformante(0, 0),1);
////                finish();
//            }else{
//                errorMessage(ListadoViviendasActivity.this,null, "Error!", Html.fromHtml("Datos erroneos de visitas pasadas"), Parametros.FONT_OBS);
//            }
//        }
//    }

    public class InsertaViviendasAnteriores extends AsyncTask<String, String, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ListadoViviendasActivity.this);
            dialog.setMessage("Procesando...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle(getString(R.string.app_name));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                StringBuilder textCodigos = new StringBuilder();
                // CONTROL PARA LAS VIVIENDAS RECICLADAS O REVISITAS
                ArrayList<Map<String, Object>> informanteAnterior;
//                        informanteAnterior = new InformanteAnterior().obtenerListado("id_nivel = 2 AND id_upm = " + idUpm+" AND id_base=2 AND id_asignacion_padre= "+(Integer)mapInformantePadreAnterior.get("id_asignacion")+" AND correlativo_padre= "+(Integer)mapInformantePadreAnterior.get("correlativo"), "CAST(codigo AS Int)");
                informanteAnterior = new InformanteAnterior().obtenerListado("id_nivel = 2 AND id_upm = " + idUpm + " AND id_base=2 ", "CAST(codigo AS Int)");
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
                        informante.set_apiestado(((String) i.get("estado")).equals("SELECCIONADO") ? Estado.SELECCIONADO : Estado.ELABORADO);
//                                if(idInformantePadreNuevo!=null){
//                                    informante.set_id_informante_padre(idInformantePadreNuevo);
//                                }
                        IdInformante idInformanteNuevo;
//                                switch ((Integer) i.get("id_nivel")) {
//                                    case 2:
                        idInformanteNuevo = (IdInformante) informante.guardar();
//                                        break;
//                                    case 3:
//                                        idInformanteNuevo = (IdInformante)informante.guardar();
//                                        break;
//                                    default:
//                                        idInformanteNuevo = informante.guardar(new IdInformante(idAsignacionPadre,correlativoPadre));
//                                        break;
//                                }

                        ArrayList<Map<String, Object>> encuestaAnterior = new EncuestaAnterior().obtenerListado("id_asignacion = " + i.get("id_asignacion") + " AND correlativo=" + i.get("correlativo"), "");
                        Encuesta encuesta = new Encuesta();
                        for (Map<String, Object> r : encuestaAnterior) {
                            if (encuesta.nuevo()) {
                                if (idInformanteNuevo != null)
                                    encuesta.set_id_encuesta(new IdEncuesta(idInformanteNuevo.id_asignacion, idInformanteNuevo.correlativo, (Integer) r.get("id_pregunta"), 1));
                                encuesta.set_codigo_respuesta((String) r.get("codigo_respuesta"));
                                encuesta.set_respuesta((String) r.get("respuesta"));
                                encuesta.set_observacion((String) r.get("observacion"));
                                encuesta.set_usucre(Usuario.getLogin());
                                encuesta.set_visible("true");
                                switch ((String) r.get("codigo_respuesta")) {
//                                            case "997":
//                                                encuesta.set_estado(Estado.NOAPLICA);
//                                                break;
//                                            case "998":
//                                                encuesta.set_estado(Estado.SENIEGA);
//                                                break;
//                                            case "999":
//                                                encuesta.set_estado(Estado.NOSABE);
//                                                break;
                                    default:
//                                                encuesta.set_estado(Estado.ELABORADO);
                                        encuesta.set_estado(Estado.INSERTADO);
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


                return "Ok";
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result.equals("Ok")) {

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                finish();
                startActivity(getIntent());
            } else if (result.equals(CREAR_CABECERA)) {
//                irEncuesta(new IdInformante(id, 0), new IdEncuesta(0,0,0), 1, 1, 0, new IdInformante(0, 0),1);
//                finish();
            } else {
                errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("Datos erroneos de visitas pasadas"), Parametros.FONT_OBS);
            }
        }
    }

    ///crea una nueva cabecera
    public void creaNuevaCabecera(int idAsignacion, int idUpm) {
        /////////
        Informante informantePadre = new Informante();
        ///crear informante cabecera
        Map<String, Object> mapInformantePadreAnterior = new HashMap<>();
        mapInformantePadreAnterior.put("id_nivel", 1);
        mapInformantePadreAnterior.put("id_upm", idUpm);
        mapInformantePadreAnterior.put("codigo", "0");
        mapInformantePadreAnterior.put("descripcion", String.valueOf(Upm.getCodigoComunidad(idAsignacion)).toUpperCase());
        try {

            if (informantePadre.nuevo()) {
                informantePadre.set_id_nivel((Integer) mapInformantePadreAnterior.get("id_nivel"));
                informantePadre.set_id_upm((Integer) mapInformantePadreAnterior.get("id_upm"));
                informantePadre.set_id_informante(new IdInformante(idAsignacion, 0));
                informantePadre.set_codigo((String) mapInformantePadreAnterior.get("codigo"));
                informantePadre.set_descripcion((String) mapInformantePadreAnterior.get("descripcion"));
                informantePadre.set_usucre(Usuario.getLogin());
//            informantePadre.set_id_informante_anterior(new IdInformante((Integer) mapInformantePadreAnterior.get("id_asignacion"), (Integer) mapInformantePadreAnterior.get("correlativo")));
//            informantePadre.set_codigo_anterior((String) mapInformantePadreAnterior.get("codigo"));
                informantePadre.set_id_usuario(Usuario.getUsuario());

                IdInformante idInformantePadreNuevo = null;
                switch ((Integer) mapInformantePadreAnterior.get("id_nivel")) {
                    case 1:
                        idInformantePadreNuevo = (IdInformante) informantePadre.guardar();
                        break;

                }
                ///inserta encuesta de padre

                ArrayList<Map<String, Object>> encuestaAnteriorPadre = new ArrayList<>();
//            AA_01
                Map<String, Object> m1 = new HashMap<>();
                m1.put("id_pregunta", 20458);
                m1.put("codigo_respuesta", Upm.getIdUpm(idAsignacion));
                m1.put("respuesta", Upm.getCodigoComunidad(idAsignacion).toUpperCase());
//                    AA_02
                Map<String, Object> m2 = new HashMap<>();
                m2.put("id_pregunta", 36265);
                m2.put("codigo_respuesta", "0");
                m2.put("respuesta", "0");
//            AA_03
                Map<String, Object> m3 = new HashMap<>();
                m3.put("id_pregunta", 36266);
                m3.put("codigo_respuesta", "0");
                m3.put("respuesta", "0");

//                    AA_04
                Map<String, Object> m4 = new HashMap<>();
                m4.put("id_pregunta", 36267);
                m4.put("codigo_respuesta", "0");
                m4.put("respuesta", "0");

//            AA_05
                Map<String, Object> m5 = new HashMap<>();
                m5.put("id_pregunta", 36313);
                m5.put("codigo_respuesta", "0");
                m5.put("respuesta", "0");
//                    AA_06
                Map<String, Object> m6 = new HashMap<>();
                m6.put("id_pregunta", 36314);
                m6.put("codigo_respuesta", "0");
                m6.put("respuesta", "0");
//            AA_07
                Map<String, Object> m7 = new HashMap<>();
                m7.put("id_pregunta", 36315);
                m7.put("codigo_respuesta", "0");
                m7.put("respuesta", "0");
//                    AA_08
                Map<String, Object> m8 = new HashMap<>();
                m8.put("id_pregunta", 36423);
                m8.put("codigo_respuesta", "0");
                m8.put("respuesta", "0");
                //                    AA_09

                Map<String, Object> m9 = new HashMap<>();
                m9.put("id_pregunta", 18607);
                m9.put("codigo_respuesta", "-1");
                m9.put("respuesta", "");

                encuestaAnteriorPadre.add(m1);
                encuestaAnteriorPadre.add(m2);
                encuestaAnteriorPadre.add(m3);
                encuestaAnteriorPadre.add(m4);
                encuestaAnteriorPadre.add(m5);
                encuestaAnteriorPadre.add(m6);
                encuestaAnteriorPadre.add(m7);
                encuestaAnteriorPadre.add(m8);
                encuestaAnteriorPadre.add(m9);

                Encuesta encuestaPadre = new Encuesta();
                for (Map<String, Object> r : encuestaAnteriorPadre) {
                    if (encuestaPadre.nuevo()) {
                        if (idInformantePadreNuevo != null) {
                            encuestaPadre.set_id_encuesta(new IdEncuesta(idInformantePadreNuevo.id_asignacion, idInformantePadreNuevo.correlativo, Integer.parseInt(String.valueOf(r.get("id_pregunta"))), 1));
                        }
                        encuestaPadre.set_codigo_respuesta( String.valueOf(r.get("codigo_respuesta")));
                        encuestaPadre.set_respuesta((String) String.valueOf(r.get("respuesta")));
//                    encuestaPadre.set_observacion((String) r.get("observacion"));
                        encuestaPadre.set_usucre(Usuario.getLogin());
                        encuestaPadre.set_visible("true");
//                        switch ((String) r.get("codigo_respuesta")) {
////                                    case "997":
////                                        encuestaPadre.set_estado(Estado.NOAPLICA);
////                                        break;
////                                    case "998":
////                                        encuestaPadre.set_estado(Estado.SENIEGA);
////                                        break;
////                                    case "999":
////                                        encuestaPadre.set_estado(Estado.NOSABE);
////                                        break;
//                            default:
                                encuestaPadre.set_estado(Estado.INSERTADO);
//                                break;
//                        }
                        encuestaPadre.guardar();
                        encuestaPadre.free();
                        informantePadre.free();
                    } else {
//                    return "No se ha podido crear la respuesta.";
                        errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se ha podido crear la respuesta"), Parametros.FONT_OBS);
                    }
                }

                ////////
            } else {
                errorMessage(ListadoViviendasActivity.this, null, "Error!", Html.fromHtml("No se ha podido crear la cabecera"), Parametros.FONT_OBS);
            }
        } catch (Exception ee) {
            ee.printStackTrace();
        }


    }

    ///mensajes personalizados
    public void informationMessageSeleccionados(String method, String titulo, Map<Integer, String> datos, float tamanoLetra) {
        this.methodAceptar = method;
        this.methodCancelar = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(titulo);
        Context context = this;
        TableLayout tableLayoutKish = new TableLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = android.view.Gravity.CENTER;
        tableLayoutKish.setLayoutParams(params);
        for (int i = 0; i < datos.size(); i++) {
            TableRow tableRow = new TableRow(context);

            tableLayoutKish.addView(tableRow);
        }
        alertDialog.setView(tableLayoutKish);

        alertDialog.setIcon(R.drawable.info);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                buttonPressed = "Aceptar";
            }
        });
        buttonPressed = "";
        AlertDialog dialog = alertDialog.create();
        dialog.setOnDismissListener(this);
        dialog.show();

        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(tamanoLetra);
    }

    public void registrar_obs_upm() {
        Informante informanteTemp = new Informante();
        if (informanteTemp.abrir("id_upm=" + idUpm + " AND id_nivel=1 AND estado <> 'ANULADO'", "")) {
            Encuesta encuestaTemp = new Encuesta();
            IdInformante i = informanteTemp.get_id_informante();
            if (encuestaTemp.abrir("id_asignacion=" + i.id_asignacion + " AND correlativo=" + i.correlativo + " AND id_pregunta=" + Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_OBSERVACION_UPM), "")) {

                if (observation != null) {
                    encuestaTemp.editar();
                    encuestaTemp.set_usumod(Usuario.getLogin());
                    encuestaTemp.set_fecmod(System.currentTimeMillis() / 1000);
//                 encuestaTemp.guardar();
                }
//             encuestaTemp.free();

            } else {
                encuestaTemp.nuevo();
                encuestaTemp.set_id_encuesta(new IdEncuesta(i.id_asignacion, i.correlativo, Pregunta.getIdPregunta(Parametros.CODIGO_PREGUNTA_OBSERVACION_UPM), 1));
                encuestaTemp.set_usucre(Usuario.getLogin());
            }
            encuestaTemp.set_codigo_respuesta(observation == null ? "0" : String.valueOf(observation.length()));
            encuestaTemp.set_respuesta(observation);
            encuestaTemp.set_estado(Estado.INSERTADO);
            encuestaTemp.set_latitud(Movil.getGPS().split(";")[0].toString());
            encuestaTemp.set_longitud(Movil.getGPS().split(";")[1].toString());
            encuestaTemp.set_visible("t");
            encuestaTemp.guardar();
            encuestaTemp.free();
            informanteTemp.free();
        }
    }
}