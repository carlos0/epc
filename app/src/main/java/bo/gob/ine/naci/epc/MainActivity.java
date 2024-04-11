package bo.gob.ine.naci.epc;

import static com.tozny.crypto.android.AesCbcWithIntegrity.generateKeyFromPassword;
import static com.tozny.crypto.android.AesCbcWithIntegrity.generateSalt;
import static com.tozny.crypto.android.AesCbcWithIntegrity.saltString;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.material.navigation.NavigationView;
import com.tozny.crypto.android.AesCbcWithIntegrity;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import bo.gob.ine.naci.epc.adaptadores.AdapterEvents;
import bo.gob.ine.naci.epc.adaptadores.MainAdapterRecycler;
import bo.gob.ine.naci.epc.entidades.DataBase;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.Upm;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityProcess;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;
import smartdevelop.ir.eram.showcaseviewlib.GuideView;
import smartdevelop.ir.eram.showcaseviewlib.config.DismissType;
import smartdevelop.ir.eram.showcaseviewlib.config.Gravity;
import smartdevelop.ir.eram.showcaseviewlib.listener.GuideListener;

public class MainActivity extends ActionBarActivityProcess implements AdapterEvents {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private ImageView imagen;
    private MainAdapterRecycler adapter;
    private ArrayList<Map<String, Object>> valores = new ArrayList<>();
    private RecyclerView list;
    public static Activity self;
    private ActionBarDrawerToggle toogle;
    private ImageView boletaVacia;
    private String proyectoNombre;
    private FloatingActionsMenu fabMenuMain;
    private FloatingActionButton descarga, actualiza, consolida;
    private Animation animation;
    private View hView;
    private static long back_pressed;
    private GuideView mGuideView;
    private GuideView.Builder builder;
    View view4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        self = this;
        setContentView(R.layout.activity_main);

//        Proyecto proyecto = new Proyecto();
//
//        proyectoNombre = proyecto.get_nombre();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imagen = findViewById(R.id.logo);
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        fabMenuMain = findViewById(R.id.fabMenuMain);
        descarga = findViewById(R.id.fabDescarga);
        actualiza = findViewById(R.id.fabActualiza);
        consolida = findViewById(R.id.fabConsolida);
        view4 = findViewById(R.id.view4);

        descarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Movil.isNetworkAvailable()) {
                    successMethod = "cargarListado";
                    errorMethod = null;
//                                    if (Observacion.countObs("1,6,8,17,18,19,22,24") > 0) {
//                                        decisionMessage("download_asignacion", null, "ALERTA", Html.fromHtml("Tiene observaciones en sus boletas. Se le recomienda corregirlas. ¿Desea descargar la carga de trabajo de todos modos?"));
//                                    } else {
                    download_asignacion();
//                                    }
                } else {
                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                }
                fabMenuMain.collapse();
            }
        });

        actualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Movil.isNetworkAvailable()) {
                    try {
                        startThree();
                        successMethod = "success";
                        errorMethod = null;
                        new DownloadFileJson().execute(Parametros.URL_DOWNLOAD, Parametros.TABLAS_DESCARGA_BOLETA, Usuario.getLogin(), "Los datos se importaron correctamente.",Usuario.getPlainToken());
                    } catch (Exception ex) {
                        errorMessage(MainActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
                    }
                } else {
                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                }
                fabMenuMain.collapse();
            }
        });

        consolida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Movil.isNetworkAvailable()) {
                    successMethod = null;
                    errorMethod = null;
                    finalMethod = null;
                    decisionMessage(MainActivity.this, "consolida", null, "¿Consolidar?", Html.fromHtml("Se subirán los datos"));
                } else {
                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                }
                fabMenuMain.collapse();
            }
        });

        boletaVacia = findViewById(R.id.boletaVacia);
        drawerLayout = findViewById(R.id.main_navigation_drawer);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        if (navigationView != null) {
            setupNavigationDrawerContent(navigationView);
        }
        hView = navigationView.getHeaderView(0);
        TextView inicialRol = hView.findViewById(R.id.inicialRol);
        TextView rolEncuestador = hView.findViewById(R.id.rolEncuestador);
        TextView nombreEncuestador = hView.findViewById(R.id.nombreEncuestador);

        inicialRol.setText(Usuario.getRolDescripcion().substring(0,1));
        rolEncuestador.setText(Usuario.getRolDescripcion());
        nombreEncuestador.setText(Usuario.getNombreUsuario());

        setupNavigationDrawerContent(navigationView);

        toogle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        actionBar = getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.googleg_standard_color_18);
//        actionBar.setDisplayHomeAsUpEnabled(true);
        String accessToken = BuildConfig.ACCESS_TOKEN;
        String secret = BuildConfig.SECRET;
        try {
            AesCbcWithIntegrity.SecretKeys keys = AesCbcWithIntegrity.generateKey();
                    String salt = saltString(generateSalt());
            // You can store the salt, it's not secret. Don't store the key. Derive from password every time
            AesCbcWithIntegrity.SecretKeys key = generateKeyFromPassword(BuildConfig.SECRET, salt);
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac = AesCbcWithIntegrity.encrypt("some  :; test", keys);
            //store or send to server
            String ciphertextString = cipherTextIvMac.toString();

            //Use the constructor to re-create the CipherTextIvMac class from the string:
            AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMacc = new AesCbcWithIntegrity.CipherTextIvMac(ciphertextString);
            String plainText = AesCbcWithIntegrity.decryptString(cipherTextIvMacc, keys);
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if ( checkPermission() ) {
            inicializa();
        } else {
            requestPermissions();
        }
    }

    public void inicializa () {
        if(!Movil.isActiveGps()) {
            Movil.initGPS();
        }
        if (Parametros.FORZAR_ACTIVACION_GPS) {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    if(!Movil.isActiveGps()) {
                        self.runOnUiThread(new Runnable() {
                            public void run() {
                                informationMessage(MainActivity.this, "activaGPS", "Sin GPS", Html.fromHtml("Debe activar el sensor de GPS."), Parametros.FONT_OBS);
                            }
                        });
                    }
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 500);
        }
        list = findViewById(R.id.list_view);
        cargarListado();
    }

    public void inicializaPrueba (){
        try{
            if(!Movil.isActiveGps()) {
                Movil.initGPS();
            }
            if (Parametros.FORZAR_ACTIVACION_GPS) {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if(!Movil.isActiveGps()) {
                            self.runOnUiThread(new Runnable() {
                                public void run() {
                                    informationMessage(MainActivity.this, "activaGPS", "Sin GPS", Html.fromHtml("Debe activar el sensor de GPS."), Parametros.FONT_OBS);
                                }
                            });
                        }
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 500);
            }
            list = findViewById(R.id.list_view);

            cargaListadoPrueba();
            fabMenuMain.expand();
        }catch (Exception e){
            e.printStackTrace();
            errorMessage(MainActivity.this,null, "Error!", Html.fromHtml("No se pudo cargar el listado"), Parametros.FONT_OBS);
        }

    }
    public void cargaListadoPrueba(){
        animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.item_anim_from_right);
        LayoutAnimationController controller = new LayoutAnimationController(animation,0.3f);
        try {
            Upm upm = new Upm();

            valores = upm.obtenerListadoPrueba();
            adapter = new MainAdapterRecycler(this, valores);
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String a = valores.get(list.getChildAdapterPosition(v)).get("id_asignacion").toString();
                    try {
                        Map<String, Object> val = valores.get(list.getChildAdapterPosition(v));
                        irInformante((Integer) val.get("id_upm"),0);
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
    private void IniciaTutorial(){



        //NavigationView navigationView = findViewById(R.id.action_cartografia);
        //navigationView.getHeaderView(0);


//        Map<String, Object> val = valores.get(list.getChildAdapterPosition());

        builder = new GuideView.Builder(this)
                .setTitle("Despliega")
                .setContentText("Despliega Opciones")
                .setGravity(Gravity.center)
                .setDismissType(DismissType.anywhere)
                .setTargetView(fabMenuMain)
                .setGuideListener(new GuideListener() {
                    @Override
                    public void onDismiss(View view) {
                        switch (view.getId()) {
                            case R.id.fabMenuMain:
                                builder.setTargetView(descarga).build();
                                builder.setTitle("Descarga");
                                builder.setTitleTextSize(25);
                                builder.setContentText("Descargar y actualizar la asignación de carga de trabajo");
                                break;
                            case R.id.fabDescarga:
                                builder.setTargetView(actualiza).build();
                                builder.setTitle("Actualiza");
                                builder.setTitleTextSize(25);
                                builder.setContentText("actualizar la base de datos de preguntas");
                                break;
                            case R.id.fabActualiza:
                                builder.setTargetView(consolida).build();
                                builder.setTitle("Consolida");
                                builder.setTitleTextSize(25);
                                builder.setContentText("Enviar la información capturada del dispositivo al servidor del INE");
                                break;
                            case R.id.fabConsolida:
                                builder.setTargetView(list.getChildAt(0)).build();
                                builder.setTitle("Opciones");
                                builder.setTitleTextSize(25);
                                builder.setContentText("Para mostrar detalle Presionar imagen (Información) \n Para mostrar los LV's presionar la imagen de la derecha (LV) \n Para mostrar las boletas seleccionar la fila \n Para mostrar las observaciones presionar el ícono de observaciones");


                                break;
                            case R.id.list_it:
                                builder.setTargetView(hView).build();
                                builder.setTitle("Cartografía");
                                builder.setTitleTextSize(25);
                                builder.setContentText("Seleccionar cartografía");
                                finish();
                                startActivity(getIntent());

                                return;
                            case R.id.main_navigation_drawer:

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toogle.onOptionsItemSelected(item)){
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


                inicializaPrueba ();
                IniciaTutorial();

                //inicializa();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            //DESCARGA LA ASIGNACION DE UPM AL USUARIO
                            case R.id.action_asignacion: {
                                if (Movil.isNetworkAvailable()) {
                                    successMethod = "asignacion";
                                    errorMethod = null;
//                                    if (Observacion.countObs("1,6,8,17,18,19,22,24") > 0) {
//                                        decisionMessage("download_asignacion", null, "ALERTA", Html.fromHtml("Tiene observaciones en sus boletas. Se le recomienda corregirlas. ¿Desea descargar la carga de trabajo de todos modos?"));
//                                    } else {
                                    decisionMessage(MainActivity.this, "consolida", null, "¿SINCRONIZAR?", Html.fromHtml("Se sincronizara su información antes de descargar su asignación"));

//                                    }
                                } else {
                                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                                }
                                return true;
                            }
                            //DESCARGA LA ULTIMA VERSION DE LA BOLETA Y SUS COMPONENTES
                            case R.id.action_preguntas: {
                                if (Movil.isNetworkAvailable()) {
                                    try {
                                        startThree();
                                        successMethod = "success";
                                        errorMethod = null;
                                        new DownloadFileJson().execute(Parametros.URL_DOWNLOAD, Parametros.TABLAS_DESCARGA_BOLETA, Usuario.getLogin(), "Los datos se importaron correctamente.",Usuario.getPlainToken());
                                    } catch (Exception ex) {
                                        errorMessage(MainActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
                                    }
                                } else {
                                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                                }
                                return true;
                            }
                            //DESCARGA LA ULTIMA VERSION DE LA APK
                            case R.id.action_apk: {
                                if (Movil.isNetworkAvailable()) {
                                    decisionMessage(MainActivity.this,"action_apk", null, "ACTUALIZAR", Html.fromHtml("¿Desea actualizar la aplicación?"));
                                } else {
                                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                                }
                                return true;
                            }
                            //DESCARGA ARCHIVOS PARA CARTODROID
                            case R.id.action_cartografia: {
//                                Map<Integer, String> map = new LinkedHashMap<>();
//                                map.put(1, "DESCARGAR ARCHIVOS");
////                                //map.put(2,"CONSOLIDAR CARTOGRAFIA");
//                                selectionMessage(MainActivity.this, "action_cartografia", "Selección", Html.fromHtml("ANTES DE DESCARGAR LA CARTOGRAFÍA POR FAVOR HAGA LA DESCARGA DE ASIGNACIÓN."), map, 1);
                                //TODO:BRP{
                                if (Movil.isNetworkAvailable()) {
                                    successMethod = "cargarListado";
                                    errorMethod = null;
                                    ArrayList<String> pathFileList = Upm.getCodigosUpm(Usuario.getUsuario());
                                    if(pathFileList.size() == 0){
                                        errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No tiene información de mapas. Descargue su carga de trabajo con la opción: 'Descargar asignación'"), Parametros.FONT_OBS);
                                    } else {
                                        File directory = new File(Parametros.DIR_CARTO);
                                        if (!directory.exists()) {
                                            if (!directory.mkdirs()) {
                                                errorMessage(MainActivity.this,null, "Error!", Html.fromHtml("No se pudo crear el directorio."), Parametros.FONT_OBS);
                                            }
                                        }
                                        new DownloadFtp().execute(pathFileList);
                                    }
//                                    }
                                } else {
                                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                                }
                                //TODO:BRP}
                                return true;
                            }
                            //RESTAURA ENC_INFORMANTES Y ENC_ENCUESTAS
//                            case R.id.action_restaurar_datos: {
////                                if (Movil.isNetworkAvailable()) {
////                                    successMethod = "cargarListado";
////                                    errorMethod = null;
////                                    observationMessage("action_restaurar", "RESTAURAR DATOS", Html.fromHtml("Esta opción le permite restaurar los últimos datos que consolidó. <b><font color='red'>La información que no haya sido consolidada se perderá</font></b>. Ingrese el código de permiso."), "", false);
////                                } else {
////                                    errorMessage(null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
////                                }
//                                return true;
//                            }
                            //REALIZA UN BACKUP
                            case R.id.action_backup: {
                                if (DataBase.backup("") == null) {
                                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No se pudo generar la copia de seguridad"), Parametros.FONT_OBS);
                                } else {
                                    exitoMessage(MainActivity.this, null, "INE", Html.fromHtml("La copia de seguridad se guardó correctamente en:\n" + Parametros.DIR_BAK), Parametros.FONT_OBS);
                                }
                                return true;
                            }
                            //CONSOLIDAR DATOS
                            case R.id.action_consolida: {
                                if (Movil.isNetworkAvailable()) {
                                    successMethod = "ocultaConcluido";
//                                    successMethod = null;
                                    errorMethod = null;
                                    finalMethod = null;
                                    decisionMessage(MainActivity.this, "consolida", null, "¿Consolidar?", Html.fromHtml("Se subirán los datos"));
//                                    new UploadHttpFile().execute(Parametros.URL_UPLOAD_BACKUP, Parametros.DIR_BAK, cartodroidFile, filePath.getPath(), "Se subió la capa exitosamente");
                                } else {
                                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                                }
                                return true;
                            }
                            //DESCARGAR OBSERVACIONES
                            case R.id.action_descargar_observaciones: {
                                if (Movil.isNetworkAvailable()) {
                                    descarga_observaciones();
                                } else {
                                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                                }
                                return true;
                            }
                            //CONSOLIDA FOTOS
//                            case R.id.action_consolida_foto: {
////                                if (Movil.isNetworkAvailable()) {
////                                    successMethod = null;
////                                    errorMethod = null;
////                                    decisionMessage("descarga_reporte_fotos", null, "¿Consolidar?", Html.fromHtml("Se subirán las fotos capturadas"));
////                                } else {
////                                    errorMessage(null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
////                                }
//                                return true;
//                            }
//                            //VERIFICAR DATOS
//                            case R.id.action_verificar_establecimiento: {
////                                if (Movil.isNetworkAvailable()) {
////                                    Map<Integer, String> map = new LinkedHashMap<>();
////                                    map.put(1, "CÓDIGO QR");
////                                    map.put(2, "POR CÓDIGO");
////                                    selectionMessage("action_verificar_seleccion", "Selección", Html.fromHtml("Elija el método de verificación"), map, 0);
////                                } else {
////                                    errorMessage(null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
////                                }
//                                return true;
//                            }
//                            //REPORTES
//                            case R.id.action_reporte: {
////                                selectionMessage("action_ir_reporte", "Selección", Html.fromHtml("Elija el reporte"), Reporte.getReportes(), 0);
//                                return true;
//                            }
//                            //DESCARGAS
//                            case R.id.action_descargas: {
////                                selectionMessage("action_descarga", "Selección", Html.fromHtml("Elija la descarga"), Documento.getDocumentos(), 0);
//                                return true;
//                            }
//                            //CONFIGURACIONES
//                            case R.id.action_ajustes: {
////                                irConfiguracion();
////                                finish();
//                                //numeroMessage("pruebas", Html.fromHtml("Prueba"), 0);
//                                return true;
//                            }
                            //ACERCA DE...
                            case R.id.action_about: {
                                aboutMessage(MainActivity.this, getResources().getString(R.string.app_name), "https://www.ine.gob.bo"+Movil.getImei());
                                return true;
                            }
                            //CERRAR SESION
                            case R.id.action_cerrar: {
                                if (Movil.isNetworkAvailable()) {
                                    if (Usuario.getLogin() == null) {
                                        DataBase.backup("");
//                                        Usuario.cerrarSesion();
                                        finish();
                                    } else {
//                                        observationMessage(MainActivity.this, "cerrar_sesion", "CERRAR SESIÓN", Html.fromHtml("Ingrese su CONTRASEÑA para cerrar sesión. <b><font color = 'blue'>Su información se consolidará automáticamente.</font></b>"), "", true);
                                        decisionMessage(MainActivity.this,"cerrar_sesion", null, "ALERTA", Html.fromHtml("Esta seguro de cerrar sesión?"));
                                    }
                                } else {
                                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                                }
                                return true;
                            }
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onItemClick(int mPosition) {
//        try {
//            Map<String, Object> val = (Map<String, Object>) adapter.getItem(mPosition);
//            irInformante((Integer) val.get("id_upm"));
//        } catch (Exception exp) {
//            exp.printStackTrace();
//        }
    }

    @Override
    public void onLongItemClick(int mPosition) {

    }

    @SuppressWarnings("unused")
    public void download_asignacion() {
        try {
            startThree();
            new DownloadFileJson().execute(Parametros.URL_DOWNLOAD,Parametros.TABLAS_DESCARGA_ASIGNACION, Usuario.getLogin(),"Los datos se importaron correctamente.",Usuario.getPlainToken());
        } catch (Exception ex) {
            errorMessage(MainActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
        }
    }

    @SuppressWarnings("unused")
    public void asignacion() {
        try {
            successMethod = "cargarListado";
            download_asignacion();
        } catch (Exception ex) {
            errorMessage(MainActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
        }
    }

    public void cargarListado() {
        animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.item_anim_from_right);
        LayoutAnimationController controller = new LayoutAnimationController(animation,0.3f);
        try {
            Upm upm = new Upm();
            valores = upm.obtenerListado(Usuario.getUsuario());
            adapter = new MainAdapterRecycler(this, valores);
            adapter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    String a = valores.get(list.getChildAdapterPosition(v)).get("id_asignacion").toString();
//                    if (Usuario.getRol() != Parametros.SUPERVISOR) {
                        try {
                            Map<String, Object> val = valores.get(list.getChildAdapterPosition(v));
                            irInformante((Integer) val.get("id_upm"), 0);
                        } catch (Exception exp) {
                            exp.printStackTrace();
                        }
//                    } else {
//                        errorMessage(MainActivity.this, null, "ADVERTENCIA", Html.fromHtml("El supervisor no puede llenar boletas"), Parametros.FONT_OBS);
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

    @SuppressWarnings("unused")
    public void consolida(){
        String file = DataBase.backup("");
        if (file == null) {
            errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No se pudo generar la copia de seguridad."), Parametros.FONT_OBS);
        } else {
            try {
//                informationMessage(MainActivity.this, null, "Error!", Html.fromHtml("EN ESTA ETAPA SOLO PUEDE CONSOLIDAR EL MENU DEL LV."), Parametros.FONT_OBS);
                startThree();
                new bo.gob.ine.naci.epc.herramientas.ActionBarActivityProcess.enviaJson().execute(Parametros.URL_DOWNLOAD, String.valueOf(Usuario.getUsuario()),String.valueOf(Usuario.getPlainToken()));
//                new UploadHttpFile().execute(Parametros.URL_UPLOAD, Parametros.DIR_BAK, file, "La información de "+Usuario.getLogin()+" se subió correctamente (se enviaron las boletas CONCLUIDAS).");
            } catch (Exception ex) {
                errorMessage(MainActivity.this, null, "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
            }
        }
    }

    @SuppressWarnings("unused")
    public void action_apk() {
        try {
            startThree();
            new DownloadHttpFile().execute(Parametros.DIR_TEMP, Parametros.URL_APK, Parametros.SIGLA_PROYECTO.toLowerCase()+".apk", null);
        } catch (Exception ex) {
            errorMessage(MainActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
        }
    }

    @SuppressWarnings("unused")
    public void action_cartografia() {
        // MUESTRA OPCIONES PARA INTERACTUAR CON CARTODROID (BAJADA Y SUBIDA DE DATOS)
        switch (id) {
            case 1:
                // PREPARA FICHEROS PARA DESCARGA DESDE UN SERVIDOR FTP
                ArrayList<String> pathFileList = Upm.getUrlPdf(Usuario.getUsuario());
                String pathNewFile = "";
                String nameNewFile = "";
                if( pathFileList.size() == 0 ){
                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("No tiene información de mapas. Descargue su carga de trabajo con la opción: 'Descargar asignación'"), Parametros.FONT_OBS);
                } else {
                    for ( String path : pathFileList ){
                        String[] part = path.split("/");
                        String fileName = part[part.length-1];
                        File file = new File(Parametros.DIR_CARTO + fileName+".zip");
                        Log.d("path: ",file.getPath());
                        Log.d("path: ",file.getAbsolutePath());
//                        file
                        Log.d("fff ","__________");
                        if( file.exists() ){
//                            if ( file.length() == 0 ){
////                                if (!file.delete()) {
////                                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("Error eliminando archivo fallido."), Parametros.FONT_OBS);
////                                } else {
////                                    pathNewFile = pathNewFile +path+";";
////                                    nameNewFile = nameNewFile +fileName+";";
////                                }
//
//                                if (file.exists()) {
////                                    errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("Error eliminando archivo fallido."), Parametros.FONT_OBS);
//                                } else {
//                                    pathNewFile = pathNewFile +path+";";
//                                    nameNewFile = nameNewFile +fileName+";";
//                                }
//
//                            }
                        } else {
                            pathNewFile = pathNewFile +path+";";
                            nameNewFile = nameNewFile +fileName+";";
                        }
                    }
                    if(!pathNewFile.equals("")) {
                        try {
                            startThree();
                            new DownloadHttpFile().execute(Parametros.DIR_CARTO, pathNewFile.substring(0, pathNewFile.length()-1), nameNewFile, "Los mapas se descargaron correctamente en la siguiente ruta: INE/"+Parametros.SIGLA_PROYECTO.toUpperCase()+"/cartografia/");
                        } catch (Exception ex) {
                            errorMessage(MainActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
                        }
                    } else {
                    }
                    informationMessage(MainActivity.this, null, "INFORMACIÓN", Html.fromHtml("Sus archivos están al día. Revise la siguiente ruta: INE/"+Parametros.SIGLA_PROYECTO.toUpperCase()+"cartografia/"), Parametros.FONT_OBS);
                }
                break;
        }
    }

    @SuppressWarnings("unused")
    public void descarga_observaciones() {
        try {
            //successMethod = "cargarListado";
            successMethod = "habilitaConcluido";
            errorMethod = null;
            startThree();
            new DownloadFileJson().execute(Parametros.URL_OBSERVACION, Parametros.TABLAS_DESCARGA_OBSERVACION, Usuario.getLogin() , "Observaciones descargadas exitosamente.<br>(Las boletas con observaciones pendientes, serán visibles nuevamente.)",Usuario.getPlainToken());
        } catch (Exception ex) {
            errorMessage(MainActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
        }
    }

    @SuppressWarnings("unused")
    public void ocultaConcluido() {
        Informante.inhabilitaBoletas();
        cargarListado();
    }

    @SuppressWarnings("unused")
    public void habilitaConcluido() {
        Informante.habilitaBoletas();
        cargarListado();
    }

    @SuppressWarnings("unused")
    public void cerrar_sesion() {
//        if(Usuario.verifyPass(observation)){
            successMethod = "logout";
            errorMethod = null;
            finalMethod = null;
            consolida();
//        } else {
//            errorMessage(MainActivity.this, null, "Error!", Html.fromHtml("Contraseña incorrecta"), Parametros.FONT_OBS);
//        }
    }

    @SuppressWarnings("unused")
    public void logout() throws MalformedURLException {
        successMethod = null;
        errorMethod = null;
        finalMethod = "exit";
            new CerrarSesion().execute(new URL(Parametros.URL_CERRAR_SESION));
    }
    @SuppressWarnings("unused")
    public void exit()  {
        finish();
    }

    @Override
    public DragAndDropPermissions requestDragAndDropPermissions(DragEvent event) {
        return super.requestDragAndDropPermissions(event);
    }
        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem versionItem = menu.findItem(R.id.action_settings);
        MenuItem borrarMenu=menu.findItem(R.id.action_borrar_todo);
        borrarMenu.setVisible(false);
//        searchView = (SearchView) searchItem.getActionView();
//        searchView.setQueryHint(getString(R.string.search_view_hint));
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                searchView.clearFocus();
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String searchText) {
//                adapter.refreshData(valores);
//                adapter.getFilter().filter(searchText);
//                return true;
//            }
//        });
//        cargarMenuItems(menu);
            versionItem.setTitle("Versión "+Parametros.VERSION);
        return true;
    }


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

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            this.finish();
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Presione de nuevo para salir!", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }
}
