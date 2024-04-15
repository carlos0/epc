package bo.gob.ine.naci.epc.herramientas;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import java.util.Arrays;
import java.util.List;

import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.entidades.Configuracion;


public class Parametros {

    public static final String PROTOCOLO = "https://";

    public static final String HOST = "servicioswm.ine.gob.bo"; //servidor 10.1.25.71
    public static final String HOST2 = "enc.ine.gob.bo";
    public static final String SERVICIO_PROYECTO = "postcensal";

    public static final String SIGLA_PROYECTO = "epc";
    public static final String VERSION = "1.00";

    public static final String BLOCK_CHARACTER_SET = "~^|$'´¨[]{}°%*!";
    public static boolean FORZAR_ACTIVACION_GPS = true;
    public static final String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    public static final String DIR_RAIZ = Environment.getExternalStorageDirectory().toString() + "/INE/"+SIGLA_PROYECTO.toUpperCase()+"/";
    public static final String DIR_TEMP = Environment.getExternalStorageDirectory().toString() + "/INE/"+SIGLA_PROYECTO.toUpperCase()+"/temp/";
    public static final String DIR_BAK = Environment.getExternalStorageDirectory().toString() + "/INE/"+SIGLA_PROYECTO.toUpperCase()+"/bak/";
    public static final String DIR_FOTO = Environment.getExternalStorageDirectory().toString() + "/INE/"+SIGLA_PROYECTO.toUpperCase()+"/foto/";
    public static final String DIR_CARTO = Environment.getExternalStorageDirectory().toString() + "/INE/"+SIGLA_PROYECTO.toUpperCase()+"/cartografia/";
    public static final String DIR_EX = Environment.getExternalStorageDirectory().toString() + "/INE/"+SIGLA_PROYECTO.toUpperCase()+"/ex/";
    public static final String DIR_PDF = Environment.getExternalStorageDirectory().toString() + "/INE/"+SIGLA_PROYECTO.toUpperCase()+"/pdf/";

    public static final String URL_DOWNLOAD = PROTOCOLO + HOST + "/" + SERVICIO_PROYECTO + "/movil/"; //enlace para descarga de datos
    public static final String URL_AUTENTICAR = PROTOCOLO + HOST + "/" + SERVICIO_PROYECTO + "/login/signin"; //enlace para inicio de sesion
    public static final String URL_CERRAR_SESION = PROTOCOLO + HOST + "/" + SERVICIO_PROYECTO + "/login/logout"; //enlace para cerrar sesion
    public static final String URL_APK = PROTOCOLO + HOST + "/" + SERVICIO_PROYECTO + "/download/apk2"; //enlace para actualizacion de la APk
    public static final String URL_OBSERVACION = PROTOCOLO + HOST + "/" + SERVICIO_PROYECTO + "/movil/"; //enlace para descarga observaciones
    public static final String URL_UPLOAD = PROTOCOLO + HOST + "/" + SERVICIO_PROYECTO + "/asignacion/consolidarMovil2"; //enlace para consolidar datos
    public static final String URL_UPLOAD_CARTODROID = "http://" + HOST2 + "/"+SERVICIO_PROYECTO+"/index.php/c_sqlite/zip_carto"; //enlace de la carpeta para subir CSV de cartodroid (consolida_carto)

    public static final String HOST_FTP = "mapace.ine.gob.bo";
    public static final String USUARIO_FTP = "umapas";
    public static final String PASSWORD_FTP = "um4p4s";

//    public static final String TABLAS_DESCARGA = "enc_pregunta, cat_catalogo, cat_seleccion, cat_tipo_pregunta, cat_upm_hijo, enc_informante, enc_encuesta, enc_nivel, enc_seccion, ope_asignacion, ope_brigada, ope_documento, ope_reporte, seg_rol, seg_rolpermiso, seg_usuario, seg_proyecto, cat_upm, cat_manzanas_comunidad, cat_comunidad_upm, perimetro, predio, disperso";
//public static final String TABLAS_DESCARGA_ASIGNACION = "ope_asignacion, cat_upm, cat_upm_hijo, enc_observacion, cat_tipo_obs, ope_reporte, ope_voes_remplazo, cat_manzanas_comunidad, perimetro, predio, disperso";
    public static final String TABLAS_DESCARGA = "enc_pregunta, cat_catalogo, cat_seleccion, cat_tipo_pregunta, cat_upm_hijo, enc_informante, enc_encuesta, enc_nivel, enc_seccion, ope_asignacion, ope_brigada, ope_documento, ope_reporte, seg_rol, seg_rolpermiso, seg_usuario, seg_proyecto, cat_upm";
    public static final String TABLAS_DESCARGA_ASIGNACION = "ope_asignacion, cat_upm, cat_upm_hijo, enc_observacion, cat_tipo_obs, ope_reporte";

    public static final String TABLAS_DESCARGA_LV = "f_reordena_lv, enc_informante_lv, enc_encuesta_lv";
    public static final String TABLAS_REORDENA_LV = "f_arregla_lv, enc_informante_lv, enc_encuesta_lv";
    public static final String TABLAS_DESCARGA_OBSERVACION = "enc_observacion, cat_tipo_obs";
    public static final String TABLAS_DESCARGA_BOLETA = "enc_pregunta, cat_catalogo, cat_seleccion, cat_tipo_pregunta, enc_nivel, enc_seccion, ope_documento, ope_reporte, seg_proyecto, cat_manzanas_comunidad, cat_comunidad_upm" ;

//TODO: MODIFICAR TODO ESTO

    public static String CODIGO_PREGUNTA_CIUDAD_COMUNIDAD = "A_01";
    public static String CODIGO_PREGUNTA_UPM_SELECCIONADA = "A_02";
    public static String CODIGO_PREGUNTA_RECORRIDO_MANZANA = "A_07";
    public static String CODIGO_PREGUNTA_NRO_ORDEN_PREDIO = "A_10";
    public static String CODIGO_PREGUNTA_NRO_ORDEN_VIV = "A_12";
    public static String CODIGO_PREGUNTA_USO_DE_LA_VIVIENDA = "A_15";
    public static String CODIGO_PREGUNTA_MANZANA_COMUNIDAD = "A_03";
    public static String CODIGO_PREGUNTA_UNION_DIVISION = "A_04";
    public static String CODIGO_PREGUNTA_MANZANA_UNION = "A_05";

    public static String CODIGO_PREGUNTA_NRO_HOGARES = "A_16";
    public static String CODIGO_PREGUNTA_NRO_DE_HOMBRES = "A_17";
    public static String CODIGO_PREGUNTA_NRO_DE_MUJERES = "A_18";
    public static String CODIGO_PREGUNTA_TOTAL_PERSONAS = "A_18_a";
    public static String CODIGO_PREGUNTA_NOMBRE_JEFE_HOGAR = "A_19";
    public static String CODIGO_PREGUNTA_VIVIENDA_OMITIDA = "A_28";
    public static String CODIGO_PREGUNTA_OBSERVACION_UPM = "AA_09";
    public static String RESPUESTA_PERSONA_NOMBRE = "S1A_01";

    public static String CODIGO_PREGUNTA_NRO_ORDEN_VIVIENDA = "A_27";
    public static String CODIGO_INCIDENCIA_FINAL = "ZZ_04";
//    public static int ID_INCIDENCIA_FINAL = 2173;


    public static int ID_INCIDENCIA_FINAL = 99999;
    public static int ID_NOMBRE_PERSONAS  = 18581;
    public static int ID_EDAD_PERSONAS  = 18173;
    public static int ID_PREGUNTA_INCIDENCIA_LV  = 14328;
    //varialbles id_pregunta
    public static int ID_MANZANA_COMUNIDAD= 18600;
    public static int ID_PREGUNTA_AVENIDA_CALLE= 18603;
    public static int ID_PREGUNTA_COMUNIDAD_MANZANA= 2177;

    //VARIABLES RESERVADAS
    public static final int ID_SECCION_RESERVADA = 0;
    public static final int LV_CABECERA = 1;
    public static final int LV_VIVIENDAS = 2;
    public static final int BOLETA_HOGARES = 3;
    public static final int BOLETA_PERSONAS = 4;
    public static final int ENCUESTADOR = 9;
    public static final int SUPERVISOR = 8;

    public static int ID_PREG_DATOS_VIVIENDA  = 18596;
    //nombres de Miembro de hogar
    public static int ID_PREG_PERSONAS  = 20475;

    public static int ID_PREG_HOGAR  = 18580;
    public static int ID_PREG_INCIDENCIA  = 99999;

    public static Fragment FRAGMENTO_ACTUAL = null;

    public static int PID_TOTAL_HM  = 17;
    public static int PID_VOE  = 26;
    public static int PID_VIVIENDA_OMITIDA  = 27;
    public static int PID_INCIDENCIA_VOE  = 22;
    public static int PID_REFERENCIA_VOE  = 24;

    public static boolean personaActiva = false;

    public static List<Integer> LIST_PREG_DATOS_VIVIENDA = Arrays.asList(181);
    public static List<Integer> LIST_PREG_PERSONAS = Arrays.asList(202, 203, 204, 205);
    public static List<Integer> LIST_PREG_HOGAR = Arrays.asList(201);
    public static List<Integer> LIST_PREG_INCIDENCIA = Arrays.asList(180);

    public static Boolean FRAGMENT_ACTIVO = false;

    public int[] COLOR_USUARIOS = new int[]{Color.RED, Color.YELLOW, Color.BLUE, Color.CYAN, Color.MAGENTA};

    public static final int RC_BARCODE_CAPTURE = 9001;

    //TAMAÑOS DE LETRA
    public static float DENSITY = 1;
    public static float EXTRA_LETTER_SIZE = 0f;

    public static float FONT_LIST_BIG_BASE = 15.5f;
    public static float FONT_LIST_SMALL_BASE = 12.5f;
    public static float FONT_PREG_BASE = 13f;
    public static float FONT_RESP_BASE = 13f;
    public static float FONT_OBS_BASE = 12f;
    public static float FONT_AYUD_BASE = 11.5f;
    public static float FONT_AYUD_BASE_SMALL = 5.5f;

    public static float FONT_LIST_BIG;
    public static float FONT_LIST_SMALL;
    public static float FONT_LIST_SMALL_SMALL;
    public static float FONT_PREG;
    public static float FONT_RESP;
    public static float FONT_OBS;
    public static float FONT_AYUD;


    static {
        DENSITY = MyApplication.getContext().getResources().getDisplayMetrics().density;
        refreshLetterSize();
    }

    public static void refreshLetterSize () {
        Configuracion.inicializa(MyApplication.getContext().getSharedPreferences(Parametros.SIGLA_PROYECTO + ".xml", Context.MODE_PRIVATE));
        switch (Configuracion.getDatoConfiguracion(1)) {
            case 0:
                EXTRA_LETTER_SIZE = -2f;
                break;
            case 1:
                EXTRA_LETTER_SIZE = 0f;
                break;
            case 2:
                EXTRA_LETTER_SIZE = 2f;
                break;
        }
        WindowManager wm = (WindowManager) MyApplication.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x > size.y ? size.y : size.x;
        FONT_LIST_BIG = (float) ((FONT_LIST_BIG_BASE + EXTRA_LETTER_SIZE) * Math.pow(width / 300f / DENSITY, 0.5f));
        FONT_LIST_SMALL = (float) ((FONT_LIST_SMALL_BASE + EXTRA_LETTER_SIZE) * Math.pow(width / 300f / DENSITY, 0.5f));
        FONT_LIST_SMALL_SMALL = (float) ((FONT_AYUD_BASE_SMALL + EXTRA_LETTER_SIZE) * Math.pow(width / 300f / DENSITY, 0.5f));
        FONT_PREG = (float) ((FONT_PREG_BASE + EXTRA_LETTER_SIZE) * Math.pow(width / 300f / DENSITY, 0.5f));
        FONT_RESP = (float) ((FONT_RESP_BASE + EXTRA_LETTER_SIZE) * Math.pow(width / 300f / DENSITY, 0.5f));
        FONT_OBS = (float) ((FONT_OBS_BASE + EXTRA_LETTER_SIZE) * Math.pow(width / 300f / DENSITY, 0.5f));
        FONT_AYUD = (float) ((FONT_AYUD_BASE + EXTRA_LETTER_SIZE) * Math.pow(width / 300f / DENSITY, 0.5f));
    }

    ////tablas de resultado de la encuesta
    public static final String TABLA_INFORMANTE="enc_informante";
    public static  final String TABLA_ENCUESTA="enc_encuesta";
}
