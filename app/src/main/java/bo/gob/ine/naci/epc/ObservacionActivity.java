package bo.gob.ine.naci.epc;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import bo.gob.ine.naci.epc.adaptadores.AdapterEvents;
import bo.gob.ine.naci.epc.adaptadores.AdapterMove;
import bo.gob.ine.naci.epc.adaptadores.ObservacionAdapter;
import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Observacion;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityProcess;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;


public class ObservacionActivity extends ActionBarActivityProcess implements AdapterEvents, AdapterMove {
    private RecyclerView list;
    private Toolbar toolbar;
    private ImageView imagen;
    private ImageView boletaVacia;
    private DrawerLayout drawerLayout;
    private View hView;
    private ActionBarDrawerToggle toogle;
    private Animation animation;
    private String seleccionable = null;
    private GuideView mGuideView;
    private GuideView.Builder builder;
    private int idUpmHijo;
    View view4;


    private ArrayList<Map<String, Object>> valores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observacion);
        //list = (ListView) findViewById(R.id.list_view);
        Bundle bundle = getIntent().getExtras();
        idUpm = bundle.getInt("IdUpm");
        idUpmHijo = bundle.getInt("idUpmHijo");

        view4 = findViewById(R.id.view4);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imagen = findViewById(R.id.logo);
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        imagen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                drawerLayout.openDrawer(GravityCompat.START);
//            }
//        });

        list = (RecyclerView) findViewById(R.id.observaciones_list_view);

        boletaVacia = findViewById(R.id.boletaVacia);


        //drawerLayout = findViewById(R.id.lv_navigation_drawer);
//        NavigationView navigationView = findViewById(R.id.navigation_view);
//        if (navigationView != null) {
//            setupNavigationDrawerContent(navigationView);
//        }
//        hView = navigationView.getHeaderView(0);

        //setupNavigationDrawerContent(navigationView);



//        toogle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
//        drawerLayout.addDrawerListener(toogle);
//        toogle.syncState();
//        EditText edit_text_query ;
//        edit_text_query = findViewById(R.id.edit_text_query);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        String query =  "SELECT o.id_observacion, eto.id_tipo_obs, eto.descripcion, i.id_asignacion, i.correlativo, i.codigo, o.observacion, o.usucre, o.feccre\n" +
//                "FROM enc_observacion o, cat_tipo_obs eto, enc_informante i\n" +
//                "WHERE o.id_tipo_obs = eto.id_tipo_obs\n" +
//                "AND i.id_asignacion = o.id_asignacion\n" +
//                "AND i.correlativo = o.correlativo\n" +
//                "AND i.id_nivel = 1 AND i.estado <> 'ANULADO' AND i.id_upm IN (SELECT id_upm FROM cat_upm)\n" +
//                "AND eto.id_tipo_obs NOT IN ('')\n" +
//                "AND o.feccre = (SELECT MAX(feccre) FROM enc_observacion WHERE id_asignacion = i.id_asignacion AND correlativo = i.correlativo)\n" +
//                "ORDER BY i.codigo";
//
////
//        edit_text_query.setText(query);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setLogo(R.mipmap.ic_activity);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
        cargarListado();
        registerForContextMenu(list);
    }
    private void setupNavigationDrawerContent(NavigationView navigationView) {

    }

    public void cargarListadoPrueba() {
//        try {
//            valores = Observacion.obtenerObservacionesRecientes("1,6,8,17,18,19,22,24");
//            ObservacionAdapter adapter = new ObservacionAdapter(this, valores);
//            list.setAdapter(adapter);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }


        animation = AnimationUtils.loadAnimation(this, R.anim.item_anim_from_right);
        LayoutAnimationController controller = new LayoutAnimationController(animation,0.3f);
        try {

            //valores = informante.obtenerListadoViviendas(idUpm, Seleccion.hasSelected(idUpm));

            //valores = Observacion.obtenerObservacionesRecientesPrueba(getString(idUpm));
            Observacion observacion = new Observacion();

            valores = observacion.obtenerObservacionesRecientesPrueba("");
//            Toast.makeText(this, "This is my Toast message!" + getString(valores.size()),
//                    Toast.LENGTH_LONG).show();
            ObservacionAdapter adapter = new ObservacionAdapter(this, valores);
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String a = valores.get(list.getChildAdapterPosition(v)).get("id_asignacion").toString();
                    try {
                        Map<String, Object> val = valores.get(list.getChildAdapterPosition(v));
                        irEncuesta(new IdInformante((Integer) val.get("id_asignacion"), (Integer) val.get("correlativo")), new IdEncuesta(0,0,0,1), 1, 2, 0, new IdInformante((Integer) val.get("id_asignacion_padre"), (Integer) val.get("correlativo_padre")),1,idUpmHijo);
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

            invalidateOptionsMenu();
        } catch (Exception exp) {
            exp.printStackTrace();
        }

    }

    public void cargarListado() {
//        try {
//            valores = Observacion.obtenerObservacionesRecientes("1,6,8,17,18,19,22,24");
//            ObservacionAdapter adapter = new ObservacionAdapter(this, valores);
//            list.setAdapter(adapter);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }


        animation = AnimationUtils.loadAnimation(this, R.anim.item_anim_from_right);
        LayoutAnimationController controller = new LayoutAnimationController(animation,0.3f);
        try {
            Observacion observacion = new Observacion();
            //valores = informante.obtenerListadoViviendas(idUpm, Seleccion.hasSelected(idUpm));
            Bundle bundle = getIntent().getExtras();
            idUpm = bundle.getInt("IdUpm");
            valores = observacion.obtenerObservacionesRecientes("1,6,8,17,18,19,22,24", idUpm);
            ObservacionAdapter adapter = new ObservacionAdapter(this, valores);
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String a = valores.get(list.getChildAdapterPosition(v)).get("id_asignacion").toString();
//                    try {
//                        Map<String, Object> val = valores.get(list.getChildAdapterPosition(v));
//                        irEncuesta(new IdInformante((Integer) val.get("id_asignacion"), (Integer) val.get("correlativo")), new IdEncuesta(0,0,0), 1, 2, 0, new IdInformante((Integer) val.get("id_asignacion_padre"), (Integer) val.get("correlativo_padre")),1, idUpmHijo);
//                    } catch (Exception exp) {
//                        exp.printStackTrace();
//                    }
                }
            });
            if (valores.size() > 0) {
                boletaVacia.setVisibility(View.GONE);
                list.setAdapter(adapter);
            }

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(list);

            invalidateOptionsMenu();
        } catch (Exception exp) {
            exp.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(toogle.onOptionsItemSelected(item)){
//            return true;
//        }
//        switch (item.getItemId()) {
//            case R.id.action_settings:
//
//                //cargarCabeceraPrueba();
//                //cargarListadoPrueba();
//                registerForContextMenu(list);
//                //IniciaTutorial();
//
////                Bundle bundle = new Bundle();
////                bundle.putInt("IdUpm", (Integer)  idUpm);
////                onCreate(bundle);
//
////                cargarCabecera();
////                cargarListado();
////                registerForContextMenu(list);
//                //registerForContextMenu(list);
//
//
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    /*

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_descargar_observaciones:
                try {
                    startThree();
                    successMethod = "cargarListado";
                    new ActionBarActivityProcess.DownloadFile().execute(new URL(Parametros.URL_OBSERVACION + Movil.getMacBluetooth() + "&version=" + Parametros.VERSION));
                } catch (Exception ex) {
                    errorMessage("finish", "Error!", Html.fromHtml(ex.getMessage()));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

//        if(toogle.onOptionsItemSelected(item)){
//            return true;
//        }

        switch (item.getItemId()) {
            case R.id.action_settings:


                //cargarCabeceraPrueba();
                cargarListadoPrueba();
                registerForContextMenu(list);
                IniciaTutorial();

                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
//
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
    }
    private void IniciaTutorial(){




        //navigationView.getHeaderView(0);


//        Map<String, Object> val = valores.get(list.getChildAdapterPosition());

        builder = new GuideView.Builder(this)
                .setTitle("Tutorial")
                .setContentText("")
                .setGravity(Gravity.center)
                .setDismissType(DismissType.anywhere)
                .setTargetView(toolbar )//list.getChildAt(0)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        switch (view.getId()) {
                            case R.id.toolbar:

                                builder.setTargetView(list.getChildAt(0)).build();
                                builder.setTitle("Lista");
                                builder.setTitleTextSize(25);
                                builder.setContentText("Lista de Observaciones");

                                //return;
                                break;
                            case R.id.list_it:

                                finish();
                                startActivity(getIntent());

//                                Bundle bundle = new Bundle();
//                                bundle.putInt("IdUpm", (Integer)  idUpm);
//                                finish();
//                                startActivity(getIntent());

                                return;


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
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
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
//
//        @Override
//    public void onItemClick(int mPosition) {
//        Map<String, Object> values = valores.get(mPosition);
//        informationMessage(null, values.get("codigo").toString(), Html.fromHtml(values.get("observacion_historial").toString()), Parametros.FONT_OBS);
//    }
//
//    @Override
//    public void onLongItemClick(int mPosition) {
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        irPrincipal();
//        finish();
//    }
    };


    @Override
    public void onItemClick(int mPosition) {

    }

    @Override
    public void onLongItemClick(int mPosition) {

    }

    @Override
    public void onLeft(int mPosition) {

    }

    @Override
    public void onRight(int mPosition) {

    }
}
