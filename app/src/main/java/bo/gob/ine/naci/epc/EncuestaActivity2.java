package bo.gob.ine.naci.epc;

import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import bo.gob.ine.naci.epc.adaptadores.CustomExpandableListAdapter;
import bo.gob.ine.naci.epc.adaptadores.IComunicaFragments2;
import bo.gob.ine.naci.epc.entidades.Encuesta;
import bo.gob.ine.naci.epc.entidades.IdEncuesta;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.Pregunta;
import bo.gob.ine.naci.epc.entidades.Siguiente;
import bo.gob.ine.naci.epc.entidades.Upm;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.fragments.FragmentInicial2;
import bo.gob.ine.naci.epc.fragments.FragmentList;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityProcess;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import bo.gob.ine.naci.epc.preguntas2.PreguntaView;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class EncuestaActivity2 extends ActionBarActivityProcess implements View.OnClickListener, IComunicaFragments2, FragmentInicial2.OnFragmentInteractionListener, FragmentEncuesta.OnFragmentInteractionListener
        ,FragmentList.OnFragmentInteractionListener {

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

    private int idAsignacion;
    private int correlativo;
    private int idPregunta;

    private int seccionId;
    private Siguiente idSiguiente;
    private PreguntaView preg;
    private Encuesta encuesta;
    private Informante informante;
    private Pregunta pregunta;

    private PreguntaView[] pregs;
    private Button[] buttons;
    private int indice = -1;

    //    private FragmentEncuesta fragmentEncuesta;
//    private FragmentInicial2 fragmentInicial;
//    private FragmentList fragmentList;
    private Fragment fragmentEncuesta, fragmentInicial, fragmentList;

    private FragmentTransaction transaction;

    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> lstTitle;
    private Map<String, List<String>> lstChild;
    private String[] items;
    private ActionBarDrawerToggle toogle;

    private RingProgressBar ringProgressBar;
    int progress = 0;
    private final static String TAG_FRAGMET_ENCUESTA = "TAG_FRAGMET_ENCUESTA";
    private final static String TAG_FRAGMET_ENCUESTA_INICIAL = "TAG_FRAGMET_ENCUESTA_INICIAL";
    private final static String TAG_FRAGMET_LISTA = "TAG_FRAGMET_LISTA";
    //Variables de escritura de archivos
    private FileOutputStream fos = null;
    private static TextView textViewEncuesta;

    public int idAsignacionMsj;
    public int correlativoMsj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuesta2);

        drawerLayout = findViewById(R.id.encuesta_navigation_drawer);
        expandableListView = (ExpandableListView) findViewById(R.id.navigation_view_encuesta);

        toogle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        fragmentInicial = new FragmentInicial2();
        fragmentList = new FragmentList();
        fragmentEncuesta = new FragmentEncuesta();

        Bundle bundle = getIntent().getExtras();

        Log.d("BRP", String.valueOf(bundle.getIntArray("IdEncuesta")));
        idEncuesta = new IdEncuesta(bundle.getIntArray("IdEncuesta"));

        encuesta = new Encuesta();
        textViewEncuesta = (TextView) findViewById(R.id.textViewEncuesta);
        textViewEncuesta.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textViewEncuesta.setMarqueeRepeatLimit(-1);
        textViewEncuesta.setSingleLine(true);
        textViewEncuesta.setSelected(true);

        startThree();

        if (checkPermission()) {
            evaluador(bundle);
        } else {
            requestPermissions();
        }
    }

    private void evaluador(Bundle bundleFragments) {
        Log.d("onback", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            try {
                getSupportFragmentManager().getFragments().remove(this);
                getSupportFragmentManager().popBackStack();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        IdEncuesta idE = new IdEncuesta((int[]) bundleFragments.getIntArray("IdEncuesta"));

        idPregunta = idE.id_pregunta;
        IdEncuesta nIdEncuesta;
        Bundle nBundle;

        if (bundleFragments.getBoolean("directo")) {
            idAsignacion = idAsignacionMsj;
            correlativo = correlativoMsj;
            Pregunta preg = new Pregunta();
            preg.abrir(idPregunta);
            idSeccion = preg.get_id_seccion();
            preg.free();
            int tipoFragment = 0;
            tipoFragment = Parametros.LIST_PREG_DATOS_VIVIENDA.contains(idSeccion) ? 1 : tipoFragment;
            tipoFragment = Parametros.LIST_PREG_PERSONAS.contains(idSeccion) ? 2 : tipoFragment;
            tipoFragment = Parametros.LIST_PREG_HOGAR.contains(idSeccion) ? 3 : tipoFragment;
            tipoFragment = Parametros.LIST_PREG_INCIDENCIA.contains(idSeccion) ? 4 : tipoFragment;

            switch (tipoFragment) {
                case 0:
                    textViewEncuesta.setText(String.valueOf(Informante.getCodigoString(idInformante)));
                    fragmentList.setArguments(bundleFragments);
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedor_fragments, fragmentList).commit();
                    break;
                case 1:
                    textViewEncuesta.setText(String.valueOf(Informante.getCodigoString(idInformante)));
                    transaction = getSupportFragmentManager().beginTransaction();
                    fragmentInicial.setArguments(bundleFragments);
                    if (Parametros.FRAGMENT_ACTIVO) {
                        transaction.replace(R.id.contenedor_fragments, fragmentInicial, TAG_FRAGMET_ENCUESTA_INICIAL).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().add(R.id.contenedor_fragments, fragmentInicial).commit();
                    }
                    break;
                case 2:
                    textViewEncuesta.setText("Personas - " + Informante.getCodigoString(idInformante));
                    transaction = getSupportFragmentManager().beginTransaction();
                    fragmentEncuesta.setArguments(bundleFragments);
                    if (Parametros.FRAGMENT_ACTIVO) {
                        transaction.replace(R.id.contenedor_fragments, fragmentEncuesta, TAG_FRAGMET_ENCUESTA).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().add(R.id.contenedor_fragments, fragmentEncuesta).commit();
                    }
                    break;
                case 3:
                    textViewEncuesta.setText("Hogar - " + Informante.getCodigoString(idInformante));
                    transaction = getSupportFragmentManager().beginTransaction();
                    fragmentEncuesta.setArguments(bundleFragments);
                    if (Parametros.FRAGMENT_ACTIVO) {
                        transaction.replace(R.id.contenedor_fragments, fragmentEncuesta, TAG_FRAGMET_ENCUESTA).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().add(R.id.contenedor_fragments, fragmentEncuesta).commit();
                    }
                    break;
                case 4:
                    textViewEncuesta.setText("Incidencia - " + Informante.getCodigoString(idInformante));
                    transaction = getSupportFragmentManager().beginTransaction();
                    fragmentEncuesta.setArguments(bundleFragments);
                    if (Parametros.FRAGMENT_ACTIVO) {
                        transaction.replace(R.id.contenedor_fragments, fragmentEncuesta, TAG_FRAGMET_ENCUESTA).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().add(R.id.contenedor_fragments, fragmentEncuesta).commit();
                    }
                    break;
            }
        } else {
            idAsignacion = idE.id_asignacion;
            correlativo = idE.correlativo;
            if (correlativo == 0) {
                textViewEncuesta.setText("Boleta Nueva");
                nIdEncuesta = new IdEncuesta(idAsignacion, correlativo, Parametros.ID_PREG_DATOS_VIVIENDA);
                nBundle = new Bundle();
                nBundle.putIntArray("IdEncuesta", nIdEncuesta.toArray());
                fragmentInicial.setArguments(nBundle);
                getSupportFragmentManager().beginTransaction().add(R.id.contenedor_fragments, fragmentInicial).commit();
            } else {
                switch (bundleFragments.getInt("tipoFragment")) {
                    case 0:
                        textViewEncuesta.setText(String.valueOf(Informante.getCodigoString(idE.getIdInformante())));
                        nIdEncuesta = new IdEncuesta(idAsignacion, correlativo, Parametros.ID_PREG_PERSONAS);
                        nBundle = new Bundle();
                        nBundle.putIntArray("IdEncuesta", nIdEncuesta.toArray());
                        fragmentList.setArguments(nBundle);
                        getSupportFragmentManager().beginTransaction().add(R.id.contenedor_fragments, fragmentList).commit();
                        break;
                    case 1:
                        textViewEncuesta.setText(String.valueOf(Informante.getCodigoString(idE.getIdInformante())));
                        nIdEncuesta = new IdEncuesta(idAsignacion, correlativo, Parametros.ID_PREG_DATOS_VIVIENDA);
                        nBundle = new Bundle();
                        nBundle.putIntArray("IdEncuesta", nIdEncuesta.toArray());
                        transaction = getSupportFragmentManager().beginTransaction();
                        fragmentInicial.setArguments(nBundle);
                        transaction.replace(R.id.contenedor_fragments, fragmentInicial, TAG_FRAGMET_ENCUESTA_INICIAL).addToBackStack(null).commit();
                        Parametros.FRAGMENTO_ACTUAL = fragmentInicial;
                        break;
                    case 2:
                        textViewEncuesta.setText("Personas - " + Informante.getCodigoString(idE.getIdInformante())+" - "+Encuesta.getNombre(idE.getIdInformante())+" - "+Encuesta.getEdad(idE.getIdInformante())+" - "+Informante.getCodigoFechaCreacionBoletaFormat(idE.getIdInformante()));
                        nIdEncuesta = new IdEncuesta(idAsignacion, correlativo, Parametros.ID_PREG_PERSONAS);
                        nBundle = new Bundle();
                        nBundle.putIntArray("IdEncuesta", nIdEncuesta.toArray());
                        transaction = getSupportFragmentManager().beginTransaction();
                        fragmentEncuesta.setArguments(nBundle);
                        transaction.replace(R.id.contenedor_fragments, fragmentEncuesta, TAG_FRAGMET_LISTA).addToBackStack(null).commit();
                        Parametros.FRAGMENTO_ACTUAL = fragmentEncuesta;
                        break;
                    case 3:
                        textViewEncuesta.setText("Hogar - " + Informante.getCodigoString(idE.getIdInformante()));
                        nIdEncuesta = new IdEncuesta(idAsignacion, correlativo, idPregunta);
                        nBundle = new Bundle();
                        nBundle.putIntArray("IdEncuesta", nIdEncuesta.toArray());
                        transaction = getSupportFragmentManager().beginTransaction();
                        fragmentEncuesta.setArguments(nBundle);
                        transaction.replace(R.id.contenedor_fragments, fragmentEncuesta, TAG_FRAGMET_ENCUESTA).addToBackStack(null).commit();
                        Parametros.FRAGMENTO_ACTUAL = fragmentEncuesta;
                        break;
                    case 4:
                        textViewEncuesta.setText("Incidencia - " + Informante.getCodigoString(idE.getIdInformante()));
                        nIdEncuesta = new IdEncuesta(idAsignacion, correlativo, Parametros.ID_PREG_INCIDENCIA);
                        nBundle = new Bundle();
                        nBundle.putIntArray("IdEncuesta", nIdEncuesta.toArray());
                        transaction = getSupportFragmentManager().beginTransaction();
                        fragmentEncuesta.setArguments(nBundle);
                        transaction.replace(R.id.contenedor_fragments, fragmentEncuesta, TAG_FRAGMET_ENCUESTA).addToBackStack(null).commit();
                        Parametros.FRAGMENTO_ACTUAL = fragmentEncuesta;
                        break;
                    default:
                        textViewEncuesta.setText(String.valueOf(Informante.getCodigoString(idE.getIdInformante())));
                        transaction = getSupportFragmentManager().beginTransaction();
                        fragmentList.setArguments(bundleFragments);
                        transaction.replace(R.id.contenedor_fragments, fragmentList, TAG_FRAGMET_LISTA).addToBackStack(null).commit();
                        Parametros.FRAGMENTO_ACTUAL = fragmentList;
                        break;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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
    public void enviarDatos(IdEncuesta encuesta, int tipoFragment, boolean directo) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("IdEncuesta", encuesta.toArray());
        bundle.putSerializable("tipoFragment", tipoFragment);
        bundle.putSerializable("directo", directo);

        evaluador(bundle);

    }

    @Override
    public void mensaje(int tipo, Context context, String methodAceptar, String methodCancelar, String titulo, Spanned mensaje, IdEncuesta idEncuestaMsj) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("context", (Serializable) context);
        bundle.putSerializable("methodAceptar", methodAceptar);
        bundle.putSerializable("methodCancelar", methodCancelar);
        bundle.putSerializable("titulo", titulo);
        bundle.putString("mensaje", mensaje.toString());
        idAsignacionMsj = idEncuestaMsj.id_asignacion;
        correlativoMsj = idEncuestaMsj.correlativo;

        switch (tipo) {
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

    public void volverInicio(){
        if(idPadre!=null &&idPadre.correlativo!=0){
            idInformante=idPadre;
            idPadre=new IdInformante(0,0);
        }
        Log.d("brp", "aqui");
        getSupportFragmentManager().getFragments().remove(this);
        getSupportFragmentManager().popBackStack();
        enviarDatos(new IdEncuesta(idInformante.id_asignacion,idInformante.correlativo, Parametros.ID_PREG_PERSONAS), 5, false);
    }

    public void terminar(){
        getSupportFragmentManager().getFragments().remove(this);
        getSupportFragmentManager().popBackStack();
        Bundle bundle = new Bundle();
        bundle.putInt("IdUpm", Informante.getUpm(idInformante));

        Intent informante = new Intent(this, BoletaActivity.class);
        informante.putExtras(bundle);
        informante.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(informante);
        this.finish();
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
        toogle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
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

    private void addDrawersItem() {
        adapter = new CustomExpandableListAdapter(this, lstTitle, lstChild);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                Toast.makeText(EncuestaActivity2.this, items[i], Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(), (CharSequence) lstTitle,Toast.LENGTH_LONG).show();

//                if(items[0].equals((lstTitle.get(groupPosition))))
//                    navigationManager.showFragment(selectedItem);
//                else
//                    throw new IllegalArgumentException("Fragmento no soportado");
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;


            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("onStopP","err");
//        finish();
    }

    public static void setTitleBoleta(String title) {
        textViewEncuesta.setText(String.valueOf(textViewEncuesta.getText().toString()) + " " + String.valueOf(title));
    }

    private void genData() {

        List<String> title = Arrays.asList();
        List<String> childitem = Arrays.asList();


        lstChild = new TreeMap<>();
//        lstChild.put(title.get(0),childitem);
//        lstChild.put(title.get(1),childitem);
//        lstChild.put(title.get(2),childitem);

        lstTitle = new ArrayList<>(lstChild.keySet());

    }

    private void initItems() {
//        items = new String[]{"Android Programing","Xamarin Programing","IOS Programing"};
        items = new String[]{};

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        if (toogle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    public int getIdUpmHijo() {
        return idUpmHijo;
    }

    @Override
    public void onBackPressed() {

        Log.d("onbackM", String.valueOf(getSupportFragmentManager().getFragments()));
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            try {
                getSupportFragmentManager().getFragments().remove(this);
                getSupportFragmentManager().popBackStack();
            }catch (Exception e){
                e.printStackTrace();
            }


//        getSupportFragmentManager().beginTransaction().remove(this);
//        getSupportFragmentManager().getFragments();
//        FragmentManager fragmentManager = getFragmentManager(); fragmentManager.beginTransaction() .remove(this) // "this" refers to current instance of Fragment2 .commit(); fragmentManager.popBackStack();

        } else {
//
////        Log.d("NivelBack",""+idNivel);
////                if (idNivel == Parametros.LV_VIVIENDAS || idNivel == Parametros.LV_CABECERA) {
////            irListadoViviendas(Upm.getIdUpm(idInformante.id_asignacion));
////                }else{
            Log.d("REVISION_VIP2", String.valueOf(Upm.getIdUpm(idEncuesta.id_asignacion)));
            irInformante(Upm.getIdUpm(idEncuesta.id_asignacion), 0);
////
////                }
            finish();
        }
    }
    public void removeFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

}