package bo.gob.ine.naci.epc.entidades;

import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//import net.zetetic.database.sqlcipher.SQLiteDatabase;
//import net.zetetic.database.sqlcipher.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import bo.gob.ine.naci.epc.BuildConfig;
import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class DataBase extends SQLiteOpenHelper {
    protected static String filePathDB = null;
    public static int versionDB = 1;

    static {
        filePathDB = MyApplication.getContext().getDatabasePath(Parametros.SIGLA_PROYECTO.toLowerCase()).getPath();
        SQLiteDatabase.loadLibs(MyApplication.getContext());
    }

    public static String getFilePathDB() {
        return filePathDB;
    }

    public DataBase(Context context) {
        super(context, filePathDB, null, versionDB);
//        super(context, filePathDB, BuildConfig.DB_SECRET, null,
//                versionDB,0,null, null,false);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE seg_proyecto (\n" +
                "id_proyecto integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "nombre text,\n" +
                "codigo nvarchar(4),\n" +
                "descripcion text,\n" +
                "fecinicio long NOT NULL,\n" +
                "fecfin long NOT NULL,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ACTIVO',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL,\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long,\n" +
                "color nvarchar(10),\n" +
                "codigo_desbloqueo nvarchar(10),\n" +
                "version_boleta nvarchar(10))");

        db.execSQL("CREATE TABLE seg_usuario (\n" +
                "id_usuario integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "login nvarchar(20),\n" +
                "password nvarchar(100),\n" +
                "nombre text,\n" +
                "telefono integer,\n" +
                "foto nvarchar(50),\n" +
                "id_departamento integer NOT NULL,\n" +
                "estado text NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre text NOT NULL,\n" +
                "feccre long NOT NULL,\n" +
                "id_rol integer,\n" +
                "id_brigada integer)");

        db.execSQL("CREATE TABLE seg_rol (\n" +
                "id_rol integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "sigla nvarchar(30) NOT NULL,\n" +
                "descripcion nvarchar(60) NOT NULL,\n" +
                "estado nvarchar(15) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(15) NOT NULL,\n" +
                "feccre long NOT NULL ,\n" +
                "usumod nvarchar(15),\n" +
                "fecmod long)");

        db.execSQL("CREATE TABLE seg_rolpermiso (\n" +
                "id_rolpermiso integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "id_rol integer NOT NULL,\n" +
                "descripcion text NOT NULL,\n" +
                "estado character(100) NOT NULL DEFAULT 'INICIO',\n" +
                "usucre character(100) NOT NULL,\n" +
                "feccre long NOT NULL ,\n" +
                "usumod character(100),\n" +
                "fecmod long with time zone)");

        db.execSQL("CREATE TABLE ope_brigada (\n" +
                "id_brigada integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "id_departamento integer NOT NULL,\n" +
                "codigo_brigada nvarchar(20) NOT NULL,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL,\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long)");

        db.execSQL("CREATE TABLE cat_upm (\n" +
                "id_upm integer PRIMARY KEY AUTOINCREMENT,\n" +
                "id_proyecto integer NOT NULL,\n" +
                "id_departamento integer,\n" +
                "codigo text,\n" +
                "nombre text,\n" +
                "fecinicio long,\n" +
                "latitud nvarchar(14) NOT NULL DEFAULT '0',\n" +
                "longitud nvarchar(14) NOT NULL DEFAULT '0',\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL,\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long,\n" +
                "incidencia integer NOT NULL DEFAULT 1,\n" +
                "url_pdf text" +
                ")");

        db.execSQL("CREATE TABLE cat_upmenviada (\n" +
                "id_upm integer PRIMARY KEY)");

        db.execSQL("CREATE TABLE cat_seleccion (\n" +
                "id_seleccion integer PRIMARY KEY AUTOINCREMENT,\n" +
                "urbano integer NOT NULL DEFAULT 1,\n" +
                "nro_viviendas integer NOT NULL,\n" +
                "viv01 integer NOT NULL,\n" +
                "viv02 integer NOT NULL,\n" +
                "viv03 integer NOT NULL,\n" +
                "viv04 integer NOT NULL,\n" +
                "viv05 integer NOT NULL,\n" +
                "viv06 integer NOT NULL,\n" +
                "viv07 integer NOT NULL,\n" +
                "viv08 integer NOT NULL,\n" +
                "viv09 integer NOT NULL,\n" +
                "viv10 integer NOT NULL,\n" +
                "viv11 integer NOT NULL,\n" +
                "viv12 integer NOT NULL,\n" +
                "UNIQUE(urbano, nro_viviendas))");

        db.execSQL("CREATE TABLE cat_tipo_pregunta (\n" +
                "id_tipo_pregunta integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "tipo_pregunta text,\n" +
                "descripcion text,\n" +
                "respuesta_valor text,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL,\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long)");

        db.execSQL("CREATE TABLE cat_tipo_obs (\n" +
                "id_tipo_obs integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "cod_tipo nvarchar(3) NOT NULL,\n" +
                "descripcion nvarchar(200),\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL,\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long,\n" +
                "estado nvarchar(60))");

        db.execSQL("CREATE TABLE ope_asignacion (\n" +
                "id_asignacion integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "id_usuario integer,\n" +
                "id_upm integer,\n" +
                "gestion integer NOT NULL,\n" +
                "mes integer NOT NULL,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL,\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long,\n" +
                "revisita integer NOT NULL DEFAULT 0,\n" +
                "trimestre_ope integer,\n" +
                "gestion_ope integer" +
                ")");

        db.execSQL("CREATE TABLE enc_nivel (\n" +
                "id_nivel integer NOT NULL PRIMARY KEY,\n" +
                "id_nivel_padre integer NOT NULL,\n" +
                "nivel integer NOT NULL,\n" +
                "descripcion text,\n" +
                "tipo text,\n" +
                "id_rol integer NOT NULL)");

        db.execSQL("CREATE TABLE enc_seccion (\n" +
                "id_seccion integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "id_nivel integer NOT NULL," +
                "codigo text,\n" +
                "seccion text,\n" +
                "abierta integer NOT NULL DEFAULT 0,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL,\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long)");
        db.execSQL("CREATE INDEX fk_enc_seccion_id_nivel ON enc_seccion(id_nivel ASC)");
        db.execSQL("CREATE INDEX fk_enc_seccion_codigo ON enc_seccion(codigo ASC)");

        db.execSQL("CREATE TABLE enc_pregunta (" +
                "id_pregunta integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "id_seccion integer,\n" +
                "ayuda text,\n" +
                "id_tipo_pregunta integer NOT NULL,\n" +
                "minimo integer NOT NULL DEFAULT 0,\n" +
                "maximo integer NOT NULL DEFAULT 0,\n" +
                "catalogo nvarchar(60) NOT NULL DEFAULT '--',\n" +
                "longitud integer,\n" +
                "codigo_especifique nvarchar(12) NOT NULL DEFAULT 0,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL,\n" +
                "codigo_pregunta text,\n" +
                "pregunta text,\n" +
                "respuesta text,\n" +
                "saltos text,\n" +
                "regla text,\n" +
                "saltos_rpn text,\n" +
                "regla_rpn text,\n" +
                "apoyo text,\n" +
                "inicial integer NOT NULL DEFAULT 0,\n" +
                "omision text,\n"  +
                "variable text" +
                ")");

        db.execSQL("CREATE INDEX fk_seccion ON enc_pregunta(id_seccion ASC)");
        db.execSQL("CREATE INDEX fk_codigo_pregunta ON enc_pregunta(codigo_pregunta ASC)");
        db.execSQL("CREATE INDEX fk_inicial_pregunta ON enc_pregunta(inicial ASC)");
        db.execSQL("CREATE INDEX fk_inicial_estado ON enc_pregunta(estado ASC)");
        db.execSQL("CREATE INDEX fk_inicial_omision ON enc_pregunta(omision ASC)");
        db.execSQL("CREATE INDEX fk_inicial_catalogo ON enc_pregunta(catalogo ASC)");

        db.execSQL("CREATE TABLE enc_respuesta (\n" +
                "id_respuesta integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "id_pregunta long,\n" +
                "codigo nvarchar(10) NOT NULL,\n" +
                "respuesta text NOT NULL,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL,\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long,\n" +
                "factor numeric(30,15) NOT NULL DEFAULT 1)");
        db.execSQL("CREATE INDEX fk_enc_respuesta_id_pregunta ON enc_respuesta(id_pregunta ASC)");
        db.execSQL("CREATE INDEX fk_enc_respuesta_codigo ON enc_respuesta(codigo ASC)");

        db.execSQL("CREATE TABLE enc_flujo (\n" +
                "id_flujo integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "id_pregunta integer NOT NULL,\n" +
                "id_pregunta_destino integer NOT NULL,\n" +
                "orden integer NOT NULL,\n" +
                "regla text NOT NULL DEFAULT '',\n" +
                "rpn text NOT NULL DEFAULT '',\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO')");

        db.execSQL("CREATE INDEX fk_preguntaflujo ON enc_flujo(id_pregunta ASC)");
        db.execSQL("CREATE INDEX fk_preguntadestinoflujo ON enc_flujo(id_pregunta_destino ASC)");
        db.execSQL("CREATE INDEX index_orden ON enc_flujo(orden ASC)");
        db.execSQL("CREATE INDEX index_enc_flujo_estado ON enc_flujo(estado ASC)");


        db.execSQL("CREATE TABLE enc_informante (\n" +
                "id_asignacion integer,\n" +
                "correlativo integer,\n" +
                "id_asignacion_padre integer,\n" +
                "correlativo_padre integer,\n" +
                "id_usuario integer NOT NULL,\n" +
                "id_upm integer,\n" +
                "id_upm_hijo integer DEFAULT 0,\n" +
                "id_nivel integer NOT NULL,\n" +
                "latitud nvarchar(14) NOT NULL DEFAULT '0',\n" +
                "longitud nvarchar(14) NOT NULL DEFAULT '0',\n" +
                "codigo nvarchar(50) NOT NULL,\n" +
                "descripcion text NOT NULL,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL DEFAULT (strftime('%s', 'now', 'localtime')),\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long,\n" +
                "PRIMARY KEY(id_asignacion, correlativo))");
        db.execSQL("CREATE INDEX index_enc_informante_id_asignacion_padre ON enc_informante(id_asignacion_padre ASC)");
        db.execSQL("CREATE INDEX index_enc_informante_correlativo_padre ON enc_informante(correlativo_padre ASC)");
        db.execSQL("CREATE INDEX index_enc_informante_id_upm ON enc_informante(id_upm ASC)");
        db.execSQL("CREATE INDEX index_enc_informante_id_upm_hijo ON enc_informante(id_upm_hijo ASC)");
        db.execSQL("CREATE INDEX index_enc_informante_id_nivel ON enc_informante(id_nivel ASC)");
        db.execSQL("CREATE INDEX index_enc_informante_id_usuario ON enc_informante(id_usuario ASC)");
        db.execSQL("CREATE INDEX index_enc_informante_id_codigo ON enc_informante(codigo ASC)");
        db.execSQL("CREATE INDEX index_enc_informante_id_estado ON enc_informante(estado ASC)");

        db.execSQL("CREATE TABLE enc_encuesta (\n" +
                "id_asignacion integer,\n" +
                "correlativo integer,\n" +
                "id_pregunta integer NOT NULL,\n" +
                "codigo_respuesta text NOT NULL DEFAULT '',\n" +
                "respuesta text NOT NULL,\n" +
                "observacion text,\n" +
                "latitud nvarchar(14) NOT NULL DEFAULT '0',\n" +
                "longitud nvarchar(14) NOT NULL DEFAULT '0',\n" +
                "visible text NOT NULL DEFAULT 'f',\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL DEFAULT (strftime('%s', 'now', 'localtime')),\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long,\n" +
                "fila integer NOT NULL DEFAULT 0,\n" +
                "PRIMARY KEY (id_asignacion, correlativo, id_pregunta))");
        db.execSQL("CREATE INDEX index_encuesta_visible ON enc_encuesta(visible ASC)");
        db.execSQL("CREATE INDEX index_encuesta_codigo_respuesta ON enc_encuesta(codigo_respuesta ASC)");
        db.execSQL("CREATE INDEX index_encuesta_estado ON enc_encuesta(estado ASC)");

        db.execSQL("CREATE TABLE cat_catalogo (\n" +
                "id_catalogo integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "catalogo text NOT NULL,\n" +
                "codigo text NOT NULL,\n" +
                "descripcion text NOT NULL,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL DEFAULT 'postgres',\n" +
                "feccre long NOT NULL DEFAULT (strftime('%s', 'now', 'localtime')),\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long)");

        db.execSQL("CREATE TABLE enc_regla (\n" +
                "id_regla integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "id_proyecto integer NOT NULL,\n" +
                "regla text,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL DEFAULT 'postgres',\n" +
                "feccre long NOT NULL DEFAULT (strftime('%s', 'now', 'localtime')),\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long)");

        db.execSQL("CREATE TABLE cat_upm_hijo(\n" +
                "id_upm_hijo Int PRIMARY KEY,\n" +
                "id_upm_padre Int NOT NULL,\n" +
                "codigo text NOT NULL,\n" +
                "id_incidencia Int NOT NULL,\n" +
                "nombre text,\n" +
                "latitud nvarchar(14) NOT NULL DEFAULT '0',\n" +
                "longitud nvarchar(14) NOT NULL DEFAULT '0',\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "url_pdf text" +
                ")");

        db.execSQL("CREATE TABLE enc_observacion(\n" +
                "id_observacion integer NOT NULL PRIMARY KEY,\n" +
                "id_tipo_obs integer NOT NULL,\n" +
                "id_usuario integer NOT NULL,\n" +
                "id_asignacion integer NOT NULL,\n" +
                "correlativo integer NOT NULL,\n" +
                "observacion text NOT NULL,\n" +
                "respuesta text NOT NULL DEFAULT '',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL DEFAULT (strftime('%s', 'now', 'localtime')),\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "id_pregunta bigint NOT NULL DEFAULT 0\n," +
                "id_asignacion_hijo integer NOT NULL,\n" +
                "correlativo_hijo integer NOT NULL, " +
                "justificacion text)");

        db.execSQL("CREATE TABLE ope_reporte(\n" +
                "id_reporte integer NOT NULL PRIMARY KEY,\n" +
                "id_proyecto integer NOT NULL,\n" +
                "nombre text,\n" +
                "id_tiporeporte integer,\n" +
                "vista_funcion text,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL DEFAULT (strftime('%s', 'now', 'localtime')),\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long)");

        db.execSQL("CREATE TABLE ope_documento(\n" +
                "id_documento integer NOT NULL PRIMARY KEY,\n" +
                "id_proyecto integer NOT NULL,\n" +
                "nombre text,\n" +
                "path text,\n" +
                "descripcion text,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL DEFAULT (strftime('%s', 'now', 'localtime')),\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long)");

        db.execSQL("CREATE TABLE enc_log(\n" +
                "id_log integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "id_usuario integer NOT NULL,\n" +
                "id_tipo integer NOT NULL,\n" +
                "descripcion text,\n" +
                "id_asignacion integer,\n" +
                "correlativo integer,\n" +
                "id_pregunta integer,\n" +
                "valor1 text,\n" +
                "valor2 text,\n" +
                "estado nvarchar(60) NOT NULL DEFAULT 'ELABORADO',\n" +
                "usucre nvarchar(60) NOT NULL,\n" +
                "feccre long NOT NULL DEFAULT (strftime('%s', 'now', 'localtime')),\n" +
                "usumod nvarchar(60),\n" +
                "fecmod long)");
        db.execSQL("CREATE TABLE cat_manzanas_comunidad (\n" +
                "id_cat_manzana_comunidad integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "num_upm text NOT NULL,\n" +
                "manzano text NOT NULL)");
        db.execSQL("CREATE TABLE cat_comunidad_upm (\n" +
                "id_cat_comunidad_upm integer NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "num_upm text NOT NULL,\n" +
                "comunidad text NOT NULL)");
        db.execSQL("CREATE TABLE ope_voes_remplazo (\n" +
                "id_upm integer NOT NULL,\n" +
                "nro_total integer NOT NULL,\n" +
                "voe_seleccionada integer NOT NULL,\n" +
                "voe_remplazo integer NOT NULL, " +
                "PRIMARY KEY (id_upm, voe_seleccionada, voe_remplazo))");

        //TODO:BRP
        db.execSQL("CREATE TABLE a_epc_segmento(\n" +
                "gid integer NOT NULL,\n" +
                "seg_unico nvarchar(254),\n" +
                "geo text)");
        db.execSQL("CREATE TABLE a_epc_manzana(\n" +
                "gid integer NOT NULL,\n" +
                "id_manz nvarchar(13),\n" +
                "orden_manz nvarchar(10),\n" +
                "seg_unico nvarchar(254),\n" +
                "geo text)");
        db.execSQL("CREATE TABLE a_epc_predio(\n" +
                "gid integer NOT NULL,\n" +
                "seg_unico nvarchar(254),\n" +
                "orden nvarchar(20),\n" +
                "cod_if nvarchar(20),\n" +
                "tipo nvarchar(20),\n" +
                "ciu_com nvarchar(20),\n" +
                "geo text)");
        db.execSQL("CREATE TABLE d_epc_comunidad(\n" +
                "gid integer NOT NULL,\n" +
                "ciu_com nvarchar(254),\n" +
                "id_com nvarchar(13),\n" +
                "seg_unico nvarchar(254),\n" +
                "geo text)");
        db.execSQL("CREATE TABLE d_epc_segmento(\n" +
                "gid integer NOT NULL,\n" +
                "seg_unico nvarchar(254),\n" +
                "segmento nvarchar(254),\n" +
                "geo text)");

 }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 0:
                db.execSQL("DROP TABLE cat_catalogo;");
                db.execSQL("DROP TABLE cat_comunidad_upm;");
                db.execSQL("DROP TABLE cat_manzanas_comunidad;");
                db.execSQL("DROP TABLE cat_seleccion;");
                db.execSQL("DROP TABLE cat_tipo_obs;");
                db.execSQL("DROP TABLE cat_tipo_pregunta;");
                db.execSQL("DROP TABLE cat_upm;");
                db.execSQL("DROP TABLE cat_upm_hijo;");
                db.execSQL("DROP TABLE cat_upm_enviada;");
                db.execSQL("DROP TABLE enc_encuesta;");
                db.execSQL("DROP TABLE enc_encuesta_anterior;");
                db.execSQL("DROP TABLE enc_flujo;");
                db.execSQL("DROP TABLE enc_informante;");
                db.execSQL("DROP TABLE enc_informante_anterior;");
                db.execSQL("DROP TABLE enc_log;");
                db.execSQL("DROP TABLE enc_nivel;");
                db.execSQL("DROP TABLE enc_observacion;");
                db.execSQL("DROP TABLE enc_pregunta;");
                db.execSQL("DROP TABLE enc_regla;");
                db.execSQL("DROP TABLE enc_respuesta;");
                db.execSQL("DROP TABLE enc_seccion;");
                db.execSQL("DROP TABLE ope_asignacion;");
                db.execSQL("DROP TABLE ope_brigada;");
                db.execSQL("DROP TABLE ope_documento;");
                db.execSQL("DROP TABLE ope_reporte;");
                db.execSQL("DROP TABLE seg_proyecto;");
                db.execSQL("DROP TABLE seg_rol;");
                db.execSQL("DROP TABLE seg_rolpermiso;");
                db.execSQL("DROP TABLE seg_usuario;");
                db.execSQL("DROP TABLE enc_informante_anterior_primero;");
                db.execSQL("DROP TABLE enc_encuesta_anterior_primero;");
                onCreate(db);
                break;
        }

        }


    public static boolean existsDataBase() {
        File file = new File(filePathDB);
        return file.exists();
    }

    // FUNCION PARA ELIMINAR LA BASE DE DATOS
    public void deleteDB () {
        getWritableDatabase(BuildConfig.DB_SECRET).beginTransaction();
        MyApplication.getContext().deleteDatabase(getFilePathDB());
        getWritableDatabase(BuildConfig.DB_SECRET).setTransactionSuccessful();
        getWritableDatabase(BuildConfig.DB_SECRET).endTransaction();
    }

    //CREA UN BACKUP DE LA BASE DE DATOS SQLITE
    public static String backup(String concatenar) {
        // FORMA EL NOMBRE DEL ARCHIVO, JALANDO LA FECHA Y EL LOGIN DEL USUARIO
        Format formatter = new SimpleDateFormat("yyyyMMdd_HHmm", Locale.ENGLISH);
        String fileName = Usuario.getLogin()+"_"+formatter.format(new Date()) + "_" +concatenar +".zip";
        String dbPath = getFilePathDB();
        return Movil.zippea (Parametros.DIR_BAK, dbPath, fileName, "");
    }
}
