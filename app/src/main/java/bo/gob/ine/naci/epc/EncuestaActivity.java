package bo.gob.ine.naci.epc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bo.gob.ine.naci.epc.adaptadores.CustomExpandableListAdapter;
import bo.gob.ine.naci.epc.adaptadores.IComunicaFragments;
import bo.gob.ine.naci.epc.entidades.Asignacion;
import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.entidades.Siguiente;
import bo.gob.ine.naci.epc.entidades.Upm;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.fragments.FragmentInicial;
import bo.gob.ine.naci.epc.fragments.FragmentList;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityProcess;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import bo.gob.ine.naci.epc.preguntas2.PreguntaView;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class EncuestaActivity extends ActionBarActivityProcess implements View.OnClickListener, IComunicaFragments, FragmentInicial.OnFragmentInteractionListener, FragmentEncuesta.OnFragmentInteractionListener
        ,FragmentList.OnFragmentInteractionListener{

    private DrawerLayout drawerLayout;

    private IdEncuesta idEncuesta;

    private LinearLayout layout;
    private ExtendedFloatingActionButton botonSiguiente;
    private ExtendedFloatingActionButton botonAnterior;

    private IdInformante idInformante;
    private IdInformante idPadre;
    private int nivel;
    private int idNivel;
    private int idSeccion;
    private int idUpmHijo;

    private int seccionId;
    private Siguiente idSiguiente;
    private PreguntaView preg;
    private Encuesta encuesta;
    private Informante informante;
    private Pregunta pregunta;

    private PreguntaView[] pregs;
    private Button[] buttons;
    private int indice = -1;

    private FragmentEncuesta fragmentEncuesta;
    private FragmentInicial fragmentInicial;
    private FragmentList fragmentList;

    private FragmentTransaction transaction;

    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> lstTitle;
    private Map<String,List<String>> lstChild;
    private String[] items;
    private ActionBarDrawerToggle toogle;

    private RingProgressBar ringProgressBar;
    int progress=0;
    private final  static  String TAG_FRAGMET_ENCUESTA="TAG_FRAGMET_ENCUESTA";
    private final  static  String TAG_FRAGMET_ENCUESTA_INICIAL="TAG_FRAGMET_ENCUESTA_INICIAL";
    private final  static  String TAG_FRAGMET_LISTA="TAG_FRAGMET_LISTA";
    //Variables de escritura de archivos
    private FileOutputStream fos=null;
    private static TextView textViewEncuesta;
//    Handler myHandler=new Handler(){
//        @Override
//        public void handleMessage(Message msg){
//            if(msg.what == 0)
//            {
//                if(progress<100){
//                    progress++;
//                    ringProgressBar.setProgress(progress);
//                }
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta);

//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

//        layout = (LinearLayout) findViewById(R.id.layout_encuesta);

        drawerLayout = findViewById(R.id.encuesta_navigation_drawer);
        expandableListView=(ExpandableListView)findViewById(R.id.navigation_view_encuesta);
//        View listHeaderView = getLayoutInflater().inflate(R.layout.nav_header,null,false);
//        expandableListView.addHeaderView(listHeaderView);
        initItems();
        genData();
        addDrawersItem();
        setupDrawer();
        toogle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        ringProgressBar=(RingProgressBar)findViewById(R.id.progress_bar);

        fragmentInicial = new FragmentInicial();
        fragmentList = new FragmentList();
        fragmentEncuesta = new FragmentEncuesta();

        Bundle bundle = getIntent().getExtras();
        idInformante = new IdInformante(bundle.getIntArray("IdInformante"));
        idEncuesta = new IdEncuesta(bundle.getIntArray("IdEncuesta"));
        nivel = bundle.getInt("Nivel");
        idNivel = bundle.getInt("idNivel");
        idSeccion = bundle.getInt("IdSeccion");
        idPadre = new IdInformante(bundle.getIntArray("IdPadre"));
        idUpmHijo = bundle.getInt("idUpmHijo");
        encuesta = new Encuesta();
        textViewEncuesta=(TextView) findViewById(R.id.textViewEncuesta);
        textViewEncuesta.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textViewEncuesta.setMarqueeRepeatLimit(-1);
        textViewEncuesta.setSingleLine(true);
        textViewEncuesta.setSelected(true);

        startThree();

        if (checkPermission()) {
//            fragmentInicial = new FragmentInicial();
//            fragmentInicial.setArguments(bundle);
//            getSupportFragmentManager().beginTransaction().add(R.id.contenedor_fragments, fragmentInicial).commit();
            evaluador(bundle);
        } else {
            requestPermissions();
        }
        Thread.setDefaultUncaughtExceptionHandler(handleAppCrash);
    }
    private Thread.UncaughtExceptionHandler handleAppCrash =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable ex) {
                    Log.e("error", ex.toString());
                    //send email here
                    try {
                        StringBuilder text= new StringBuilder();
                        text.append(String.valueOf(getClass().getName()));

                        text.append("\n");
                        text.append(ex);
                        File directory = new File(Parametros.DIR_EX);
                        if ( !directory.exists() ) {
                            if (!directory.mkdirs()) {
                            }
                        }
                        File myExternalFile = new File(Parametros.DIR_EX+"ex_"+ String.valueOf(new Date().toString().replace(" ","-").replace(":","-")));
                        fos=new FileOutputStream(myExternalFile);
                        fos.write(text.toString().getBytes("UTF-8"));
                        fos.close();

                    }catch (Exception e){
                        e.printStackTrace();
                    }finally {
                        if(fos!=null){
                            try {
                                fos.close();
                            }catch (IOException ie){
                                ie.printStackTrace();
                            }
                        }
                    }

                }
            };

    private void evaluador(Bundle bundleFragments) {
        IdInformante informante = new IdInformante((int[]) bundleFragments.getIntArray("IdInformante"));
        if (bundleFragments.getInt("idNivel") == Parametros.LV_CABECERA || bundleFragments.getInt("idNivel") == Parametros.LV_VIVIENDAS) {
            if(bundleFragments.getInt("idNivel")==Parametros.LV_CABECERA){
                textViewEncuesta.setText("Cabecera LV");
            }else {
                textViewEncuesta.setText("Boleta LV");
            }
            fragmentInicial.setArguments(bundleFragments);
            getSupportFragmentManager().beginTransaction().add(R.id.contenedor_fragments, fragmentInicial).commit();
        } else if (informante.correlativo == 0) {
            textViewEncuesta.setText("Boleta Nueva");
            fragmentInicial.setArguments(bundleFragments);
            getSupportFragmentManager().beginTransaction().add(R.id.contenedor_fragments, fragmentInicial).commit();
        } else {
            switch (bundleFragments.getInt("tipoFragment")) {
                case 1:
                    textViewEncuesta.setText(String.valueOf(Informante.getCodigoString(idInformante)));
                    transaction = getSupportFragmentManager().beginTransaction();
                    fragmentInicial.setArguments(bundleFragments);
                    transaction.replace(R.id.contenedor_fragments, fragmentInicial, TAG_FRAGMET_ENCUESTA_INICIAL).commit();
                    break;
                case 2:
                    textViewEncuesta.setText("Listado de Personas - "+Informante.getCodigoString(idInformante));
                    transaction = getSupportFragmentManager().beginTransaction();
                    fragmentList.setArguments(bundleFragments);
                    transaction.replace(R.id.contenedor_fragments, fragmentList,TAG_FRAGMET_LISTA).commit();
                    break;
                case 3:
                    textViewEncuesta.setText(""+Informante.getCodigoString(idInformante));
                    transaction = getSupportFragmentManager().beginTransaction();
                    fragmentEncuesta.setArguments(bundleFragments);
                    transaction.replace(R.id.contenedor_fragments, fragmentEncuesta,TAG_FRAGMET_ENCUESTA).commit();
                    break;
                default:
                    textViewEncuesta.setText("Boleta");
                    irInformante(Asignacion.getUpm(informante.id_asignacion),idUpmHijo);
                    break;
            }
        }
//        if (bundleFragments.getInt("idNivel") == Parametros.LV_CABECERA || bundleFragments.getInt("idNivel") == Parametros.LV_VIVIENDAS) {
//            fragmentInicial.setArguments(bundleFragments);
//            getSupportFragmentManager().beginTransaction().add(R.id.contenedor_fragments, fragmentInicial).commit();
//        } else if (bundleFragments.getInt("IdSeccion") == 0) {
//            fragmentInicial.setArguments(bundleFragments);
//            getSupportFragmentManager().beginTransaction().add(R.id.contenedor_fragments, fragmentInicial).commit();
//        } else if(bundleFragments.getInt("IdSeccion") == 1000){
//            transaction = getSupportFragmentManager().beginTransaction();
//            fragmentList.setArguments(bundleFragments);
//            transaction.replace(R.id.contenedor_fragments, fragmentList).commit();
//        }else{
//            Nivel nuevoNivel = new Nivel();
//            Seccion nuevaSeccion = new Seccion();
//            String tipoNivel = nuevoNivel.tipo_nivel(bundleFragments.getInt("IdSeccion"));
//            int seccionInicial = nuevaSeccion.seccion_inicial(bundleFragments.getInt("idNivel"));
//            if (bundleFragments.getInt("IdSeccion") == seccionInicial) {
//                IdEncuesta encuesta = new IdEncuesta((int[]) bundleFragments.getIntArray("IdEncuesta"));
//                if(encuesta.id_pregunta != nuevaSeccion.getPrimeraPregunta(bundleFragments.getInt("IdSeccion"))){
//                    transaction = getSupportFragmentManager().beginTransaction();
//                    fragmentEncuesta.setArguments(bundleFragments);
//                    transaction.replace(R.id.contenedor_fragments, fragmentEncuesta).commit();
//                }else{
//                    if (tipoNivel.equals("BUCLE")) {
//                        transaction = getSupportFragmentManager().beginTransaction();
//                        fragmentList.setArguments(bundleFragments);
//                        transaction.replace(R.id.contenedor_fragments, fragmentList).commit();
//                    } else {
//                        transaction = getSupportFragmentManager().beginTransaction();
//                        fragmentInicial.setArguments(bundleFragments);
//                        transaction.replace(R.id.contenedor_fragments, fragmentInicial).commit();
//                    }
//                }
//            } else {
//                transaction = getSupportFragmentManager().beginTransaction();
//                fragmentEncuesta.setArguments(bundleFragments);
//                transaction.replace(R.id.contenedor_fragments, fragmentEncuesta).commit();
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

//    @Override
//    public void onClicknNavigation(View v) {
//        switch (v.getId()){
//            Nivel nivel = new Nivel();
//            if(nivel.tipo_nivel(idSeccion).equals("LINEAL")){
//                fragmentEncuesta = new FragmentEncuesta();
//                fragmentEncuesta.setArguments(bundleFragments);
//                transaction.replace(R.id.contenedor_fragments, fragmentEncuesta).addToBackStack(null);
//            }else{//BUCLE
//                fragmentList = new FragmentList();
//                fragmentList.setArguments(bundleFragments);
//                transaction.replace(R.id.contenedor_fragments, fragmentList).addToBackStack(null);
//            }
//        }
//    }

    @Override
    public void enviarDatos(IdInformante informante, IdEncuesta encuesta, int nivel, int IdNivel, int seccion, IdInformante informantePadre, int tipoFragment) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("IdInformante", informante.toArray());
        bundle.putSerializable("IdEncuesta", encuesta.toArray());
        bundle.putSerializable("Nivel", nivel);
        bundle.putSerializable("idNivel", IdNivel);
        bundle.putSerializable("IdSeccion", seccion);
        bundle.putSerializable("IdPadre", informantePadre.toArray());
        bundle.putSerializable("tipoFragment", tipoFragment);

        evaluador(bundle);

    }

    @Override
    public void mensaje(int tipo, Context context, String methodAceptar, String methodCancelar, String titulo, Spanned mensaje) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", (Serializable) context);
        bundle.putSerializable("methodAceptar", methodAceptar);
        bundle.putSerializable("methodCancelar", methodCancelar);
        bundle.putSerializable("titulo", titulo);
        bundle.putString("mensaje", mensaje.toString());
        switch (tipo){
            case 1:
                //information
                informationMessage(context, methodAceptar, titulo, mensaje, Parametros.FONT_OBS);

                break;
            case 2:
                //error
                errorMessage(context, methodAceptar, titulo, mensaje, Parametros.FONT_OBS);
                break;
            default:
                decisionMessage(context, methodAceptar, methodCancelar, titulo, mensaje);
                break;
        }


        Log.d("sa", buttonPressed);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for(int i=0;i<100;i++)
//                {
//                    try{
//                        Thread.sleep(5);
//                        myHandler.sendEmptyMessage(0);
//                    }catch (InterruptedException e){
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }).start();
//    }
    }

    private void setupDrawer() {
        toogle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
        {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //getSupportActionBar().setTitle("EDMTDev");
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };
        toogle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(toogle);

    }

    private void addDrawersItem(){
        adapter = new CustomExpandableListAdapter(this,lstTitle,lstChild);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Toast.makeText(EncuestaActivity.this, items[i],Toast.LENGTH_LONG).show();
                return false;
            }
        });
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                getSupportActionBar().setTitle(lstTitle.get(groupPosition).toString());
                drawerLayout.closeDrawer(GravityCompat.START);
            }

        });
        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                getSupportActionBar().setTitle("EDMTDev");
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//            getSupportActionBar().setHomeButtonEnabled(false);
        }
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String selectedItem = ((List) (lstChild.get(lstTitle.get(groupPosition))))
                        .get(childPosition).toString();
                getSupportActionBar().setTitle(selectedItem);

//                if(items[0].equals((lstTitle.get(groupPosition))))
//                    navigationManager.showFragment(selectedItem);
//                else
//                    throw new IllegalArgumentException("Fragmento no soportado");
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;



            }
        });
    }
    public  static void setTitleBoleta( String title){
        textViewEncuesta.setText(String.valueOf(textViewEncuesta.getText().toString())+" "+String.valueOf(title));
    }

    private void genData(){

        List<String> title = Arrays.asList();
        List<String> childitem = Arrays.asList();


        lstChild = new TreeMap<>();
//        lstChild.put(title.get(0),childitem);
//        lstChild.put(title.get(1),childitem);
//        lstChild.put(title.get(2),childitem);

        lstTitle = new ArrayList<>(lstChild.keySet());

    }

    private void initItems(){
//        items = new String[]{"Android Programing","Xamarin Programing","IOS Programing"};
        items = new String[]{};

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(toogle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
    public int getIdUpmHijo(){
        return idUpmHijo;
    }

//    @Override
//    public void onBackPressed() {
//        Asignacion asignacion = new Asignacion();
//        asignacion.abrir(idInformante.id_asignacion);
//        if (idNivel == Parametros.LV_VIVIENDAS || idNivel == Parametros.LV_CABECERA) {
//            irListadoViviendas(asignacion.get_id_upm());
//        }
//        asignacion.free();
//        finish();
//    }
@Override
public void onBackPressed() {
//    Asignacion asignacion = new Asignacion();
//    asignacion.abrir(idInformante.id_asignacion);
//    FragmentEncuesta fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentEncuesta);
//    if (!(fragment instanceof IOnBackPressed) || !((IOnBackPressed) fragment).onBackPressed()) {
//        transaction = getSupportFragmentManager().beginTransaction();
//        fragmentList.setArguments(bundleFragments);
//        transaction.replace(R.id.contenedor_fragments, fragmentList).commit();
//    }
//    else {
//        super.onBackPressed();
//    }
//    finish();
    if (getFragmentManager().getBackStackEntryCount() > 0) {
        getFragmentManager().popBackStack();
    } else {
        super.onBackPressed();
        Log.d("NivelBack",""+idNivel);
                if (idNivel == Parametros.LV_VIVIENDAS || idNivel == Parametros.LV_CABECERA) {
            irListadoViviendas(Upm.getIdUpm(idInformante.id_asignacion));
                }else{
                    irInformante(Upm.getIdUpm(idInformante.id_asignacion),idUpmHijo);

                }
        finish();

    }

}

}