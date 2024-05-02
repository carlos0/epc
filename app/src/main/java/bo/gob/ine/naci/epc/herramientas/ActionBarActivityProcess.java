package bo.gob.ine.naci.epc.herramientas;

import static com.tozny.crypto.android.AesCbcWithIntegrity.generateKeyFromPassword;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.tozny.crypto.android.AesCbcWithIntegrity;

import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**implementando metricas de cambio de seguridad**/
import java.security.cert.CertificateException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;

import bo.gob.ine.naci.epc.BuildConfig;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.DataBase;
import bo.gob.ine.naci.epc.entidades.Entidad;
import bo.gob.ine.naci.epc.entidades.Proyecto;
import bo.gob.ine.naci.epc.entidades.UpmHijo;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.objetosJson.JAsignacion;
import bo.gob.ine.naci.epc.objetosJson.JBrigada;
import bo.gob.ine.naci.epc.objetosJson.JCatComunidadUpm;
import bo.gob.ine.naci.epc.objetosJson.JCatManzanasComunidad;
import bo.gob.ine.naci.epc.objetosJson.JCatalogo;
import bo.gob.ine.naci.epc.objetosJson.JDocumento;
import bo.gob.ine.naci.epc.objetosJson.JEncuesta;
import bo.gob.ine.naci.epc.objetosJson.JEncuestaAnterior;
import bo.gob.ine.naci.epc.objetosJson.JEncuestaAnteriorPrimero;
import bo.gob.ine.naci.epc.objetosJson.JFlujo;
import bo.gob.ine.naci.epc.objetosJson.JInformante;
import bo.gob.ine.naci.epc.objetosJson.JInformanteAnterior;
import bo.gob.ine.naci.epc.objetosJson.JInformanteAnteriorPrimero;
import bo.gob.ine.naci.epc.objetosJson.JMComunidad;
import bo.gob.ine.naci.epc.objetosJson.JMPredio;
import bo.gob.ine.naci.epc.objetosJson.JMSegmento;
import bo.gob.ine.naci.epc.objetosJson.JMSegmentoD;
import bo.gob.ine.naci.epc.objetosJson.JMmanzana;
import bo.gob.ine.naci.epc.objetosJson.JNivel;
import bo.gob.ine.naci.epc.objetosJson.JObservacion;
import bo.gob.ine.naci.epc.objetosJson.JOpeVoesRemplazo;
import bo.gob.ine.naci.epc.objetosJson.JPregunta;
import bo.gob.ine.naci.epc.objetosJson.JProyecto;
import bo.gob.ine.naci.epc.objetosJson.JReporte;
import bo.gob.ine.naci.epc.objetosJson.JRol;
import bo.gob.ine.naci.epc.objetosJson.JRolPermiso;
import bo.gob.ine.naci.epc.objetosJson.JSeccion;
import bo.gob.ine.naci.epc.objetosJson.JSeleccion;
import bo.gob.ine.naci.epc.objetosJson.JTipoObservacion;
import bo.gob.ine.naci.epc.objetosJson.JTipoPregunta;
import bo.gob.ine.naci.epc.objetosJson.JUpm;
import bo.gob.ine.naci.epc.objetosJson.JUpmHijo;
import bo.gob.ine.naci.epc.objetosJson.JUsuario;
import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ActionBarActivityProcess extends ActionBarActivityNavigator {

    protected String[] files;
    protected String fileName;
    protected String errorMethod = null;
    protected String successMethod = null;
    protected String finalMethod = null;
    protected String finalMessage = null;
    protected String[] tablasDescarga = null;
    protected URL url;
    protected ArrayList tablasData = new ArrayList();
    protected String downloadedReport;
    protected String condicion;
    protected String tokenCripto=null;
    protected int idUpm;
    protected SweetAlertDialog SweetDialog;


    // DESCARGA DATOS EN FORMATO JSON DEL INE
    public class DownloadFileJson extends AsyncTask<String, Integer, String> {
//        SweetAlertDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            SweetDialog = new SweetAlertDialog(ActionBarActivityProcess.this, SweetAlertDialog.PROGRESS_TYPE);
            SweetDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            SweetDialog.setTitleText("SINCRONIZANDO");
            SweetDialog.setCancelable(false);
            SweetDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            int count = 0;
            String url_fail = "";
            String preUrl = "";
            try {
//                tablasData.clear();
                tablasDescarga = params[1].split(", ");

                condicion = params[2];
                finalMessage = params[3];
                String plainText = params[4] == null ? Usuario.getPlainToken() : params[4];
                //TODO:BRP{
                List<String> tablas = Arrays.asList("seg_usuario", "seg_usuariorestriccion", "ope_asignacion",
                        "enc_informante", "enc_encuesta", "enc_observacion", "cat_upm", "enc_informante_lv",
                        "enc_encuesta_lv", "f_reordena_lv", "f_arregla_lv", "cat_manzanas_comunidad",
                        "cat_comunidad_upm", "enc_informante_anterior", "enc_encuesta_anterior",
                        "enc_informante_anterior_primero", "enc_encuesta_anterior_primero", "cat_upm_hijo",
                        "ope_brigada", "ope_voes_remplazo", "perimetro", "predio", "disperso", "a_epc_segmento",
                        "a_epc_manzana", "a_epc_predio", "d_epc_comunidad", "d_epc_segmento");

                for (String tb : tablasDescarga) {
                    if(tablas.contains(tb)) {
//                    if (tb.equals("seg_usuario") || tb.equals("seg_usuariorestriccion") || tb.equals("ope_asignacion") || tb.equals("enc_informante") || tb.equals("enc_encuesta") || tb.equals("enc_observacion") || tb.equals("cat_upm") || tb.equals("enc_informante_lv") || tb.equals("enc_encuesta_lv") || tb.equals("f_reordena_lv") || tb.equals("f_arregla_lv") || tb.equals("cat_manzanas_comunidad") || tb.equals("cat_comunidad_upm") || tb.equals("enc_informante_anterior") || tb.equals("enc_encuesta_anterior") || tb.equals("enc_informante_anterior_primero") || tb.equals("enc_encuesta_anterior_primero") || tb.equals("cat_upm_hijo") || tb.equals("ope_brigada") || tb.equals("ope_voes_remplazo") || tb.equals("perimetro") || tb.equals("predio") || tb.equals("disperso")) {
                        //TODO:BRP}
                        Log.d("tablas ", tb);
                        if (condicion.split("/").length > 1) {
                            String[] doubleCondicion = condicion.split("/");
                            preUrl = params[0] + tb + "/" + doubleCondicion[0] + "/" + doubleCondicion[1];
                        } else {
                            preUrl = params[0] + tb + "/" + condicion;
                        }
                    } else {
                        preUrl = params[0] + tb;
                    }
                    Log.d("","-------------------url----------------------");
                    Log.d("",preUrl);

                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                            // Validación básica del certificado del servidor
                            if (chain == null || chain.length == 0) {
                                throw new CertificateException("No se encontraron certificados de servidor");
                            }

                            X509Certificate serverCert = chain[0];
                            // Aquí puedes implementar lógica para validar el certificado del servidor
                            // Por ejemplo, verificar el nombre del host, la cadena de certificación, etc.
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }}, new SecureRandom());

                    url = new URL(preUrl);

                    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
//                    connection.setSSLSocketFactory(sslContext.getSocketFactory());
//                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("Authorization", "Bearer " + plainText);
                    int responseCode = connection.getResponseCode();
                    int lenghtOfFile = connection.getContentLength();

                    File downloadDir = new File(Parametros.DIR_TEMP);
                    if (!downloadDir.exists()) {
                        if (!downloadDir.mkdirs()) {
                            return "No se ha podido crear el directorio";
                        }
                    }

                    BufferedReader reader;
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    } else {
                        reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                        url_fail = url + "\n" + url_fail;
                        count++;
                    }

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    Log.i("mensaje", stringBuilder.toString());
                    tablasData.add(stringBuilder);
                }

                if (count > 0) {
                    return "No se puede acceder a '" + url_fail + "'";
                } else {
                    return "Ok";
                }
            } catch (Exception e) {
                if (e.getMessage().startsWith("failed to connect")) {
                    return "No se pudo establecer la conexion, si el problema persiste porfavor reportelo";
                } else {
                    return e.getMessage();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equalsIgnoreCase("Ok")) {
                new InsertJson().execute(tablasData);
            } else {
                SweetDialog.dismiss();
                if (result.equals("Movil no registrado.\n")) {
                    errorMessage(ActionBarActivityProcess.this, "finish", "Error!", Html.fromHtml(result), Parametros.FONT_OBS);
                } else {
                    errorMessage(ActionBarActivityProcess.this, null, "Error!", Html.fromHtml(result), Parametros.FONT_OBS);
                }
                endThree();
            }
        }
    }

    // DESCOMPRIME EL ARCHIVO ZIPEADO
    private class InsertJson extends AsyncTask<ArrayList, Void, String> {
        SweetAlertDialog dialog;
        String respuesta = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SweetDialog.setTitleText("SINCRONIZANDO");
        }

        @Override
        protected String doInBackground(ArrayList... params) {
            String error="";
            int inicio = 0;
            try {
                if(tablasData.contains("f_reordena_lv")){
                    inicio = 1;
                }
                for (int i = inicio; i < tablasData.size(); i++) {
                    JSONArray jsonArray = new JSONArray(tablasData.get(i).toString());
                    Entidad ent;
                    error = tablasDescarga[i];
                    switch(tablasDescarga[i]) {
                        case "ope_asignacion":
                            JAsignacion jAsignacion =  new JAsignacion(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jAsignacion.datos, true, false);
                            break;
                        case "ope_brigada":
                            JBrigada jBrigada =  new JBrigada(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jBrigada.datos, true, false);
                            break;
                        case "cat_catalogo":
                            JCatalogo jCatalogo =  new JCatalogo(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jCatalogo.datos, true, false);
                            break;
                        case "ope_documento":
                            JDocumento jDocumento =  new JDocumento(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jDocumento.datos, true, false);
                            break;
                        case "enc_encuesta":
                            JEncuesta jEncuesta =  new JEncuesta(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jEncuesta.datos, false, true);
                            break;
                        case "enc_informante":
                            JInformante jInformante =  new JInformante(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jInformante.datos, true, true);
                            break;
                        case "enc_encuesta_lv":
                            JEncuesta jEncuestaLv =  new JEncuesta(jsonArray);
                            ent = new Entidad("enc_encuesta");
                            respuesta = ent.insertDataJson(jEncuestaLv.datos, true, false);
                            break;
                        case "enc_informante_lv":
                            JInformante jInformanteLv =  new JInformante(jsonArray);
                            ent = new Entidad("enc_informante");
                            respuesta = ent.insertDataJson(jInformanteLv.datos, true, false);
                            break;
                        case "enc_observacion":
                            JObservacion jObservacion =  new JObservacion(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jObservacion.datos, true, false);
                            break;
                        case "enc_pregunta":
                            JPregunta jPregunta =  new JPregunta(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jPregunta.datos, true, false);
                            JFlujo jFlujo =  new JFlujo(jsonArray);
                            ent = new Entidad("enc_flujo");
                            respuesta = ent.insertDataJson(jFlujo.datos, true, false);
                            break;
                        case "seg_proyecto":
                            JProyecto jProyecto =  new JProyecto(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jProyecto.datos, true, false);
                            break;
                        case "ope_reporte":
                            JReporte jReporte =  new JReporte(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jReporte.datos, true, false);
                            break;
                        case "seg_rol":
                            JRol jRol =  new JRol(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jRol.datos, true, false);
                            break;
                        case "seg_rolpermiso":
                            JRolPermiso jRolPermiso =  new JRolPermiso(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jRolPermiso.datos, true, false);
                            break;
                        case "enc_nivel":
                            JNivel jNivel =  new JNivel(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jNivel.datos, true, false);
                            break;
                        case "enc_seccion":
                            JSeccion jSeccion =  new JSeccion(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jSeccion.datos, true, false);
                            break;
                        case "cat_seleccion":
                            JSeleccion jSeleccion =  new JSeleccion(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jSeleccion.datos, true, false);
                            break;
                        case "cat_tipo_obs":
                            JTipoObservacion jTipoObservacion =  new JTipoObservacion(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jTipoObservacion.datos, true, false);
                            break;
                        case "ope_voes_remplazo":
                            JOpeVoesRemplazo jOpeVoesRemplazo =  new JOpeVoesRemplazo(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jOpeVoesRemplazo.datos, true, false);
                            break;
                        case "cat_tipo_pregunta":
                            JTipoPregunta jTipoPregunta =  new JTipoPregunta(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jTipoPregunta.datos, true, false);
                            break;
                        case "cat_upm":
                            JUpm jUpm =  new JUpm(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jUpm.datos, true, false);
                            break;
                        case "cat_upm_hijo":
                            JUpmHijo jUpmHijo =  new JUpmHijo(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jUpmHijo.datos, true, false);
                            break;
                        case "cat_manzanas_comunidad":
                            JCatManzanasComunidad jCatManzanasComunidad =  new JCatManzanasComunidad(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jCatManzanasComunidad.datos, true, false);
                            break;
                        case "cat_comunidad_upm":
                            JCatComunidadUpm jCatComunidadUpm =  new JCatComunidadUpm(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jCatComunidadUpm.datos, true, false);
                            break;
                        case "seg_usuario":
                            JUsuario jUsuario =  new JUsuario(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jUsuario.datos, true, false);
                            break;
                        case "enc_encuesta_anterior":
                            JEncuestaAnterior jEncuestaAnterior =  new JEncuestaAnterior(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jEncuestaAnterior.datos, true, false);
                            break;
                        case "enc_informante_anterior":
                            JInformanteAnterior jInformanteAnterior =  new JInformanteAnterior(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jInformanteAnterior.datos, true, false);
                            break;
                        case "enc_encuesta_anterior_primero":
                            JEncuestaAnteriorPrimero jEncuestaAnteriorPrimero =  new JEncuestaAnteriorPrimero(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jEncuestaAnteriorPrimero.datos, true, false);
                            break;
                        case "enc_informante_anterior_primero":
                            JInformanteAnteriorPrimero jInformanteAnteriorPrimero =  new JInformanteAnteriorPrimero(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jInformanteAnteriorPrimero.datos, true, false);
                            break;
                        //TODO:BRP{
                        case "a_epc_segmento":
                            JMSegmento jmSegmento =  new JMSegmento(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jmSegmento.datos, true, false);
                            break;
                        case "a_epc_manzana":
                            JMmanzana jMmanzana =  new JMmanzana(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jMmanzana.datos, true, false);
                            break;
                        case "a_epc_predio":
                            JMPredio jmPredio = new JMPredio(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jmPredio.datos, true, false);
                            break;
                        case "d_epc_comunidad":
                            JMComunidad jmComunidad = new JMComunidad(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jmComunidad.datos, true, false);
                            break;
                        case "d_epc_segmento":
                            JMSegmentoD jmSegmentoD = new JMSegmentoD(jsonArray);
                            ent = new Entidad(tablasDescarga[i]);
                            respuesta = ent.insertDataJson(jmSegmentoD.datos, true, false);
                            break;
                        //TODO:BRP}
                    }

                }

                //Elimina viviendas antes de un reemplazo LV
                UpmHijo.eliminaViviendasPorReemplazo();

                if (!(url.toString().contains("asignacion") || url.toString().contains("boletas"))) {
                    SharedPreferences preferences = getApplicationContext().getSharedPreferences("sice.xml", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
                    editor.putString("fechabd", sdf.format(new Date()));
                    editor.apply();
                }

            } catch (JSONException e){
                return e.getMessage() + " @1 en la tabla:" + error;
            } catch (Exception e) {
                return e.getMessage() + " @2 en la tabla:" + error;
            }
            return "Ok";
        }

        @Override
        protected void onPostExecute(String result) {
            if (SweetDialog != null && SweetDialog.isShowing()) {
                SweetDialog.dismiss();
            }
            if (result.equalsIgnoreCase("Ok")) {
                if(Usuario.getMovilToken()==null){
//                    try {
//                        Usuario.autenticarStore(Usuario.getPlainToken(tokenCripto,condicion),condicion);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                }
                exitoMessage(ActionBarActivityProcess.this ,successMethod, "Concluído", Html.fromHtml(finalMessage), Parametros.FONT_OBS);
            } else {
                errorMessage(ActionBarActivityProcess.this, errorMethod, "Error!", Html.fromHtml(result), Parametros.FONT_OBS);
            }
            tablasData.clear();
            endThree();
        }
    }

    public class enviaJson extends AsyncTask<String, Void, String> {
        String mensajeRequest;
        String datodato;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SweetDialog = new SweetAlertDialog(ActionBarActivityProcess.this, SweetAlertDialog.PROGRESS_TYPE);
            SweetDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            SweetDialog.setTitleText("SINCRONIZANDO SUS DATOS");
            SweetDialog.setCancelable(false);
            SweetDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String error = "";
            try {
                String plainText = Usuario.getPlainToken();
//                ArrayList<Map<String, Object>> contenedor = new ArrayList<>();
//                ArrayList<Map<String, Object>> contenedor_enc = new ArrayList<>();
//                Map<String, Object> elementos = null;
////
//                Map<Integer, String> contenedor = new LinkedHashMap<>();
//
////                JSONArray conjunto = new JSONArray();
//
////                JSONArray conjunto_total = new JSONArray();
//
////                Entidad ent;
////                Entidad ent2;
////
//                int completado = 0;
//                String conjunto_total = "";
////
////                ent = new Entidad("enc_informante");
////                ent2 = new Entidad("enc_encuesta");
//
////                contenedor = ent.obtenerListado("id_usuario = " + params[1]);
//                contenedor = Informante.obtenerIndexConsolidacion(Integer.parseInt(params[1]));
//                for(int n = 0; n < contenedor.size(); n++){
//                    if(n==0){
//                        conjunto_total += "[";
//                    }
//                    conjunto_total += Informante.obtenerDatosConsolidacion(contenedor.get(n));
//                    if(n == contenedor.size()-1){
//                        conjunto_total += "]";
//                    } else {
//                        conjunto_total += ",";
//                    }
//                }

//                }
                //datos de informante

//                String result = conjunto_total;
                Entidad inf=new Entidad(Parametros.TABLA_INFORMANTE);
                JSONArray jsonInformante=inf.obtenerListado(Parametros.TABLA_INFORMANTE,"","id_asignacion, correlativo");

                //get respuesta
                JSONArray jsonInformantePeticion = new JSONArray();
                Entidad respuestas=new Entidad(Parametros.TABLA_ENCUESTA);
                JSONArray jsonEncuesta;

                for (int i = 0; i < jsonInformante.length(); i++) {
                    JSONObject row = jsonInformante.getJSONObject(i);
                    jsonEncuesta=respuestas.obtenerListado(Parametros.TABLA_ENCUESTA,"id_asignacion="+row.getInt("id_asignacion")+" AND  correlativo="+row.getInt("correlativo"),null);
//                    jsonInformante.put(i,jsonEncuesta);
                    JSONArray jsonEncuestaItem=new JSONArray();
                    jsonEncuestaItem.put(0,row);
                    jsonEncuestaItem.put(1,jsonEncuesta);
//                    jsonInformantePeticion=new JSONArray();
                    jsonInformantePeticion.put(i,jsonEncuestaItem);

                }
                //Respuestad de la encuesta

//                Log.d("BASEE", "onPostExecute: "+String.valueOf(jsonInformantePeticion.toString()));

                datodato=String.valueOf(jsonInformantePeticion);
                URL urlAux = new URL(Parametros.URL_UPLOAD);
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
//                RequestBody body = RequestBody.create(JSON, result);
                RequestBody body = RequestBody.create(JSON, datodato);
//                OkHttpClient client = new OkHttpClient();

                // Crear un objeto SSLContext sin validar certificados
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }}, new SecureRandom());
                X509TrustManager trustManager = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // Implementación del método
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // Implementación del método
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                };

                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManager)
                        .build();
                Request request = new Request.Builder()
                        .url(urlAux)
                        .post(body)
//                        .addHeader("authorization","Bearer "+plainText)
                        .addHeader("serie",Movil.getImei())
                        .addHeader("version",Parametros.VERSION)
                        .build();
                Response response = client.newCall(request).execute();
                int v = response.code();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    String reader = response.body().string();
                    JSONObject objeto = new JSONObject(reader);
                    String respuesta = objeto.getString("codigo");
                    mensajeRequest = respuesta;
                } else {
                    mensajeRequest = "NO SE PUDO SINCRONIZAR SU INFORMACION, INTENTE NUEVAMENTE";
                }

                return mensajeRequest;
            } catch (JSONException e) {
                return e.getMessage();
            } catch (Exception e) {
                if (e.getMessage().contains("Failed") && e.getMessage().contains("connect")) {
                    return getString(R.string.conexion_error);
                } else {
                    return e.getMessage();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
//            Log.d("BASE444", "onPostExecute: "+datodato);

            SweetDialog.dismiss();
            if (result.equalsIgnoreCase("Ok")) {
                exitoMessage(ActionBarActivityProcess.this ,successMethod, "Concluído", Html.fromHtml("INFORMACION SINCRONIZADA CORRECTAMENTE"), Parametros.FONT_OBS);
            } else if(result.equalsIgnoreCase("actualizar app")){
                errorMessage(ActionBarActivityProcess.this, errorMethod, "Error!", Html.fromHtml(result), Parametros.FONT_OBS);
            }else{
                errorMessage(ActionBarActivityProcess.this, errorMethod, "Error!", Html.fromHtml(result), Parametros.FONT_OBS);
            }
            tablasData.clear();
            endThree();
        }
    }


    // SUBE LOS ARCHIVOS AL SERVIDOR DEL INE
    public class UploadHttpFile extends AsyncTask<String, String, String> {
        private ProgressDialog dialog = new ProgressDialog(ActionBarActivityProcess.this);

        String preUrl;
        URL url;
        String outputDirectory;
        String[] fileNameArray;
        String successMessage;
        int contador = 0;

        @Override
        protected void onPreExecute() {
            dialog.setTitle(getString(R.string.app_name));
            dialog.setCancelable(false);
            dialog.setMessage("Enviando datos al servidor...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            try {
                url = new URL(args[0]);
                outputDirectory = args[1];
                fileNameArray = args[2].split(";");
                successMessage = args[3];
                String res = "Ok";
                for (String fName : fileNameArray) {
                    contador++;
                    /*dialog.setMessage("Enviando... "+fName+"("+c+"/"+fileNameArray.length+")");*/
                    publishProgress(fName, contador+"");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setUseCaches(false);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Cache-Control", "no-cache");
                    connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=*****");

                    String f= Usuario.getLogin();
                    String h = Proyecto.getBoletaVersion();
                    DataOutputStream request = new DataOutputStream(connection.getOutputStream());
                    request.writeBytes("--*****\r\n");
                    request.writeBytes("Content-Disposition: form-data; name=\"username\"\r\n");
                    request.writeBytes("Content-Type: text/plain; charset=UTF-8\r\n");
                    request.writeBytes("\r\n");
                    request.writeBytes(Usuario.getLogin() + "\r\n");
                    request.flush();

                    request.writeBytes("--*****\r\n");
                    request.writeBytes("Content-Disposition: form-data; name=\"password\"\r\n");
                    request.writeBytes("Content-Type: text/plain; charset=UTF-8\r\n");
                    request.writeBytes("\r\n");
                    request.writeBytes("Mw52udZdisjr9fhS\r\n");
                    request.flush();

                    request.writeBytes("--*****\r\n");
                    request.writeBytes("Content-Disposition: form-data; name=\"version\"\r\n");
                    request.writeBytes("Content-Type: text/plain; charset=UTF-8\r\n");
                    request.writeBytes("\r\n");
                    request.writeBytes(String.valueOf(Parametros.VERSION) + "\r\n");
                    request.flush();

                    request.writeBytes("--*****\r\n");
                    request.writeBytes("Content-Disposition: form-data; name=\"fecha\"\r\n");
                    request.writeBytes("Content-Type: text/plain; charset=UTF-8\r\n");
                    request.writeBytes("\r\n");
                    request.writeBytes(Proyecto.getBoletaVersion() + "\r\n");
                    request.flush();

                    request.writeBytes("--*****\r\n");
                    request.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + fName + "\"\r\n");
                    request.writeBytes("\r\n");

                    byte[] buffer = new byte[4096];
                    FileInputStream is = new FileInputStream(outputDirectory + fName);
                    int read;
                    while ((read = is.read(buffer)) != -1) {
                        request.write(buffer, 0, read);
                    }
                    request.writeBytes("\r\n");
                    request.writeBytes("--*****\r\n");
                    request.flush();
                    request.close();

                    InputStream responseStream;
                    int a = connection.getResponseCode();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        responseStream = connection.getInputStream();
                    } else {
                        responseStream = connection.getErrorStream();
                    }

                    BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));

                    String line;
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = responseStreamReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    responseStreamReader.close();
                    responseStream.close();
                    connection.disconnect();
                    String respuesta = stringBuilder.toString().replaceAll("\n", "");
                    if (!(respuesta).equals("")) {
                        res = res + fName + " ("+respuesta+"), ";
                    }
                }
                return res;
            } catch (Exception ex) {
                return ex.getMessage();
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {
            dialog.setMessage("Enviando... "+values[0]+"("+values[1]+"/"+fileNameArray.length+")");
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result.equalsIgnoreCase("Ok")) {
                if (finalMethod == null) {
                    exitoMessage(ActionBarActivityProcess.this, successMethod, "CONSOLIDACIÓN COMPLETA", Html.fromHtml(successMessage), Parametros.FONT_OBS);
                } else {
                    executeFinalMethod();
                }
            } else {
                errorMessage(ActionBarActivityProcess.this, errorMethod, "Error!", Html.fromHtml("<b>No se pudo enviar los siguientes archivos</b><br>:"+result.replace(",","<br>")), Parametros.FONT_OBS);
            }
            endThree();
        }
    }

    // DESCARGA TEXTO MEDIANTE HTTP
    public class DownloadHttpText extends AsyncTask<String, Integer, String> {
        ProgressDialog dialog;
        URL url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ActionBarActivityProcess.this);
            dialog.setMessage("Descargando información...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.app_name));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(params[0]);
                //url = new URL(params[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setUseCaches(false);
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.connect();

                String mime = connection.getContentType();
                if (mime.toLowerCase().startsWith("text/html")) {
                    int responseCode = connection.getResponseCode();
                    BufferedReader reader;
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    } else {
                        reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                        stringBuilder.append("\n");
                    }
                    return stringBuilder.toString();
                }
                return "error:No se recibió texto";
            } catch (Exception e) {
                return "error:"+e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if(result.startsWith("error:")) {
                errorMessage(ActionBarActivityProcess.this, null, "Error!", Html.fromHtml(result), Parametros.FONT_OBS);
            } else {
                downloadedReport = result;
                executeFinalMethod();
            }
            endThree();
        }
    }

    // DESCARGA UN ARCHIVO MEDIANTE HTTP
    public class DownloadHttpFile extends AsyncTask<String, Integer, String> {
        ProgressDialog dialog;
        String outputDirectory;
        String[] fileUrlArray;
        String[] fileNameArray;
        String successMessage;
        String fileName;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ActionBarActivityProcess.this);
            dialog.setMessage("Descargando...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.app_name));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            outputDirectory = params[0];
            fileUrlArray = params[1].split(";");
            fileNameArray = params[2].split(";");
            successMessage = params[3];
            int count;
            int c = 0;
            String res = "Ok";
            try {
                // CREA EL DIRECTORIO DE SALIDA
                File downloadDir = new File(outputDirectory);
                if (!downloadDir.exists()) {
                    if (!downloadDir.mkdirs()) {
                        return "No se ha podido crear el directorio";
                    }
                }
                // ITERAMOS TODOS LOS ARCHIVOS DE DESCARGA
                for(String urlAux : fileUrlArray) {
                    if(fileNameArray[c].endsWith("A")||fileNameArray[c].endsWith("D")) {
                        fileName = fileNameArray[c] + ".zip";
                    }else{
                        fileName = fileNameArray[c];
                    }
                    c++;
                    try {
                        URL currentUrl = new URL(urlAux);
                        HttpURLConnection connection = (HttpURLConnection) currentUrl.openConnection();
//                        connection.setUseCaches(false);
//                        connection.setRequestMethod("GET");
//                        connection.setDoOutput(true);
                        connection.connect();

                        int lenghtOfFile = connection.getContentLength();
                        int responseCode = connection.getResponseCode();

                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            String a = currentUrl.openStream().toString();
                            String b = connection.getContent().toString();
                            InputStream input = new BufferedInputStream(currentUrl.openStream());
                            OutputStream output = new FileOutputStream(outputDirectory + fileName);
                            byte data[] = new byte[1024];
                            long total = 0;
                            while ((count = input.read(data)) != -1) {
                                total += count;
                                publishProgress((int) ((total * 100) / lenghtOfFile), c, fileUrlArray.length);
                                output.write(data, 0, count);
                            }
                            output.close();
                            input.close();
                            if(fileName.endsWith(".zip")) {
                                Movil.unZippea(outputDirectory, outputDirectory, fileName);
                            }
                        } else {
                            res += ", "+ fileName;
                        }
                    } catch (Exception e) {
                        res += ", "+ fileName;
                    }
                }
            } catch (Exception e) {
                return e.getMessage();
            }
            return res;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            dialog.setProgress(progress[0]);
            dialog.setMessage(Html.fromHtml("Descargando... "+fileName+" ("+progress[1]+"/"+progress[2]+")"));
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result.equalsIgnoreCase("Ok")) {
                if(fileName.endsWith(".apk")) {

                        File apkDescargada = new File(outputDirectory + fileName);
                        Uri fileUri = Uri.fromFile(apkDescargada); //for Build.VERSION.SDK_INT <= 24

                    if (Build.VERSION.SDK_INT >= 24) {
                        fileUri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                                Objects.requireNonNull(getApplicationContext()).getApplicationContext().getPackageName()+ ".provider", apkDescargada);
                    }
                        Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
                        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                        intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //dont forget add this line
                        startActivity(intent);

                        finish();



                } else if(fileName.endsWith(".pdf")){
                    File file = new File(outputDirectory + fileName);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Intent intent1 = Intent.createChooser(intent, "Abrir con...");
                    exitoMessage(ActionBarActivityProcess.this, null, "DESCARGA COMPLETA", Html.fromHtml(successMessage), Parametros.FONT_OBS);
                    startActivity(intent1);
                } else {
                    exitoMessage(ActionBarActivityProcess.this, null, "DESCARGA COMPLETA", Html.fromHtml(successMessage), Parametros.FONT_OBS);
                }
            } else {
                errorMessage(ActionBarActivityProcess.this, "error", "Error!", Html.fromHtml("No se lograron descargar los siguientes archivos: "+result.substring(3, result.length())), Parametros.FONT_OBS);
            }
            endThree();
        }
    }

    //TODO:BRP{
    //DESCARGA ARCHIVOS DE UN SERVIDOR FTP
    public class DownloadFtp extends AsyncTask<ArrayList<String>, Integer, String> {
        ProgressDialog dialog;
        URL url;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ActionBarActivityProcess.this);
            dialog.setMessage("Descargando FTP...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.app_name));
            dialog.show();
        }

        @Override
        protected String doInBackground(ArrayList<String>... params) {
            boolean resp = false;
            String imagenes = "";
            for(String param : params[0]){
                if(!param.endsWith("D")) {
                    if (!new File(Parametros.DIR_CARTO + param + ".mbtiles").exists()) {
                        Log.d("archivo", "no existe");
                        FileOutputStream videoOut;
                        File targetFile = new File(Parametros.DIR_CARTO + param + ".mbtiles");
                        FTPClient ftpClient = new FTPClient();
                        try {
                            ftpClient.connect(Parametros.HOST_FTP, 21);
                            ftpClient.enterLocalPassiveMode();
                            ftpClient.login(Parametros.USUARIO_FTP, Parametros.PASSWORD_FTP);
                            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);// Used for video
                            targetFile.createNewFile();
                            videoOut = new FileOutputStream(targetFile);
                            resp = ftpClient.retrieveFile("/EH/" + param + ".mbtiles", videoOut);
                            ftpClient.disconnect();
                            videoOut.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (!resp) {
                            imagenes += param + ",";
                        }
                    }
                }
            }
            return imagenes;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            Log.d("PROGRESSBAR", progress[1]+"/"+progress[2]);
            dialog.setProgress(progress[0]);
            dialog.setMessage(Html.fromHtml("Descargando... "+fileName+" ("+progress[1]+"/"+progress[2]+")"));
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if(result.equals("")){
                exitoMessage(ActionBarActivityProcess.this, null, "DESCARGA COMPLETA", Html.fromHtml("ARCHIVOS DESCARGADOS EXITOSAMENTE"), Parametros.FONT_OBS);
            } else {
                errorMessage(ActionBarActivityProcess.this, "error", "Error!", Html.fromHtml("No se lograron descargar los siguientes archivos: " + result), Parametros.FONT_OBS);
            }
            endThree();
        }
    }
    //TODO:BRP}

    // RESTAURA UN BACKUP
    public class RestoreBackup extends AsyncTask<Intent, String, String> {
        ProgressDialog dialog;
        InputStream inputStream;
        OutputStream outputStream;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ActionBarActivityProcess.this);
            dialog.setMessage("Restaurando...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.app_name));
            dialog.show();
        }

        @Override
        protected String doInBackground(Intent... params) {
            try {
                byte[] buffer = new byte[4 * 1024];
                int read;
                inputStream = getContentResolver().openInputStream(params[0].getData());
                outputStream = new FileOutputStream(DataBase.getFilePathDB());
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
            } catch (Exception e) {
                return e.getMessage();
            }
            return "Ok";
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if (result.equalsIgnoreCase("Ok")) {
                try {
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                exitoMessage(ActionBarActivityProcess.this, successMethod, "Concluido", Html.fromHtml("El backup se restauró correctamente."), Parametros.FONT_OBS);
            } else {
                errorMessage(ActionBarActivityProcess.this, errorMethod, "Error!", Html.fromHtml(result), Parametros.FONT_OBS);
            }
            endThree();
        }
    }

    public class Verifica extends AsyncTask<URL, String, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ActionBarActivityProcess.this);
            dialog.setMessage("Verificando...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.app_name));
            dialog.show();
        }

        @Override
        protected String doInBackground(URL... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .get()
                        .addHeader("cache-control", "no-cache")
                        .build();
                Response response = client.newCall(request).execute();
                return response.code() == HttpURLConnection.HTTP_OK ? response.body().string() : "failure";
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if( !result.equals("failure") ) {
                switch (result) {
                    case "qr":
                        errorMessage(ActionBarActivityProcess.this, null, "Error!", Html.fromHtml("Código inválido"), Parametros.FONT_OBS);
                        break;
                    case "g1":
                        decisionMessage(ActionBarActivityProcess.this, successMethod, null, "GEOPOSICIONAR", Html.fromHtml("El establecimiento aún no fue geoposicionado. ¿Desea geoposicionarlo?"));
                        break;
                    case "g2":
                        informationMessage(ActionBarActivityProcess.this, null, "INFORMACIÓN", Html.fromHtml("El establecimiento ya fue geoposicionado."), Parametros.FONT_OBS);
                        break;
                }
            } else {
                errorMessage(ActionBarActivityProcess.this, errorMethod, "Error!", Html.fromHtml(result), Parametros.FONT_OBS);
            }
            endThree();
        }
    }

    // REALIZA UNA CONSULTA A LA BASE DE DATOS
    public class GeoPosiciona extends AsyncTask<URL, String, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ActionBarActivityProcess.this);
            dialog.setMessage("Geoposicionando...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.app_name));
            dialog.show();
        }

        @Override
        protected String doInBackground(URL... params) {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(params[0])
                        .get()
                        .addHeader("cache-control", "no-cache")
                        .build();
                Response response = client.newCall(request).execute();
                return response.code() == HttpURLConnection.HTTP_OK ? response.body().string() : response.code()+"";
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if(result.startsWith("ok")) {
                informationMessage(ActionBarActivityProcess.this, null, "INFORMACIÓN", Html.fromHtml("Geoposicionamiento exitoso."), Parametros.FONT_OBS);
            } else {
                errorMessage(ActionBarActivityProcess.this, errorMethod, "Error!", Html.fromHtml("No se pudo geoposicionar el establecimiento."), Parametros.FONT_OBS);
            }
            endThree();
        }
    }

    // AUTENTICACION EN LINEA
    public class Autenticar extends AsyncTask<String, String, String> {
//        ProgressDialog dialog;
        String user;
        String pass;
        JSONObject jsonRequest;
        String loginRequest;
        String tokenRequest;
        String mensajeRequest;
        JSONObject usuarioRequest;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SweetDialog = new SweetAlertDialog(ActionBarActivityProcess.this, SweetAlertDialog.PROGRESS_TYPE);
            SweetDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            SweetDialog.setTitleText("VERIFICANDO USUARIO");
            SweetDialog.setCancelable(false);
            SweetDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                user = params[0];
                pass = params[1];

                JSONObject jUsuario = new JSONObject();
                jUsuario.put("login",user);
                jUsuario.put("password",pass);
                jUsuario.put("serie",Movil.getImei());
                jUsuario.put("version",Parametros.VERSION);

                // Crear un objeto SSLContext sin validar certificados
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }}, new SecureRandom());
                X509TrustManager trustManager = new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // Implementación del método
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        // Implementación del método
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                };

                URL urlAux = new URL(Parametros.URL_AUTENTICAR);
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jUsuario.toString());

                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustManager)
                        .build();
                Request request = new Request.Builder()
                        .url(urlAux)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                int a = response.code();
                if (response.code() == HttpURLConnection.HTTP_OK){
                    String reader = response.body().string();
                    jsonRequest = new JSONObject(reader);
                    if(jsonRequest.length()>1){
                        loginRequest = jsonRequest.getString("login");
                        tokenRequest = jsonRequest.getString("token");
                        usuarioRequest = jsonRequest.getJSONObject("usuario");
                        if(loginRequest.equals(user)){
                            tokenCripto=Usuario.getEncriptToken(tokenRequest,user);
//                            Usuario.autenticarStore(loginRequest,tokenRequest, usuarioRequest);
                            return  "ok";
                            //store user data

                        }else{
                            mensajeRequest = "El usuario no es correcto";
                        }
                    }else{
                        mensajeRequest = jsonRequest.getString("msg");
                    }
                }else{
                    mensajeRequest = "Usuario no autorizado, verificar.";
                }
                return mensajeRequest;
            } catch (Exception e) {
                e.printStackTrace();
                if(e.getMessage().contains("Failed") && e.getMessage().contains("connect")){
                    return getString(R.string.conexion_error);
                }else {
                    return e.getMessage();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            SweetDialog.dismiss();
            if( result.equals("ok") ) {
                try {
                    startThree();
//                    successMethod = "success";
//                    errorMethod = null;
                    new DownloadFileJson().execute(Parametros.URL_DOWNLOAD, Parametros.TABLAS_DESCARGA, user,"Los datos se importaron correctamente.",Usuario.getPlainToken(tokenCripto,user));
                } catch (Exception e) {
                    errorMessage(ActionBarActivityProcess.this, errorMethod, "Error!", Html.fromHtml(e.getMessage()), Parametros.FONT_OBS);
                }
            } else {
                errorMessage(ActionBarActivityProcess.this, errorMethod, "Error!", Html.fromHtml(result), Parametros.FONT_OBS);
            }
            endThree();
        }
    }

    // AUTENTICACION EN LINEA
    public class CerrarSesion extends AsyncTask<URL, String, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ActionBarActivityProcess.this);
            dialog.setMessage("Procesando...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            dialog.setTitle(getString(R.string.app_name));
            dialog.show();
        }

        @Override
        protected String doInBackground(URL... params) {
            try {
                AesCbcWithIntegrity.SecretKeys keys = generateKeyFromPassword(BuildConfig.SECRET, Usuario.getLogin().getBytes("UTF-8"));
                AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMacc = new AesCbcWithIntegrity.CipherTextIvMac(Usuario.getMovilToken());
                String plainText = AesCbcWithIntegrity.decryptString(cipherTextIvMacc, keys);
                HttpUrl.Builder queryUrlBuilder = HttpUrl.get(params[0]).newBuilder();
                queryUrlBuilder.addQueryParameter("id_usuario", Usuario.getLogin());
                queryUrlBuilder.addQueryParameter("serie", Movil.getImei());
                queryUrlBuilder.addQueryParameter("version", Parametros.VERSION);
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(queryUrlBuilder.build())
                        .header("Content-Type","application/json;charset=utf-8")
                        .addHeader("cache-control", "no-cache" )
                        .addHeader("authorization", "Bearer "+plainText )
                        .get()
                        .build();
                Response response = client.newCall(request).execute();
                return response.code() == HttpURLConnection.HTTP_OK ? (new JSONObject(response.body().string())).getString("msg") : "No se pudo obtener una respuesta";
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if( result.equals("ok") ) {
                Entidad.eliminaDatos();
                Usuario.cerrarSesion();
                informationMessage(ActionBarActivityProcess.this, "finish", "INFORMACIÓN", Html.fromHtml("Gracias por su contribucion al INE"), Parametros.FONT_OBS);
            } else {
                errorMessage(ActionBarActivityProcess.this, errorMethod, "Error!", Html.fromHtml(result), Parametros.FONT_OBS);
            }
            endThree();
        }
    }

    public void executeFinalMethod() {
        try {
            if (finalMethod != null) {
                Method m = null;
                m = this.getClass().getMethod(finalMethod);
                m.invoke(this);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public class ConsolidarJson extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        private JSONObject reponse=null;
        URL url_loggin=null;
        HttpURLConnection connection=null;
        long codEst=-1;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ActionBarActivityProcess.this);
            dialog.setMessage("Solicitando Código...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setTitle(getString(R.string.app_name));
            dialog.show();
            //  Informante.eliminaDatos(idTemp);
        }

        @Override
        protected String doInBackground(String... params) {
//            JSONObject objetoPost=new JSONObject();
//            objetoPost.put("version", ParametroConfig.version);
//            objetoPost.put("incidencias",IncidenciaDao.getListIncidenciaJsonArray());
            try{
                URL url_loggin=new URL(params[0]);
//            byte[] postDatBytes=objetoPost.toString().getBytes("UTF-8");
                connection=(HttpURLConnection)url_loggin.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","application/json");
                //connection.setDoOutput(true);
                //connection.setConnectTimeout(120*1000);

//            connection.getOutputStream().write(postDatBytes);
                String resp=readStream(connection.getInputStream());
                Log.v("CODIGO",resp.toString());
                JSONObject jsonRespuesta=new JSONObject(resp);
                if(jsonRespuesta.getBoolean("success")){

                    codEst=jsonRespuesta.getLong("data");
                    return "true";
                }
                else {
                    return "false";
                }

            }catch (MalformedURLException ee){
                Log.v("error","Url mal estructurado");
                return "Url mal estructurado";
            }catch (IOException io){
                Log.v("error","No se pudo abrir connexion");
                return "No se pudo abrir connexion";
            }catch (JSONException je){
                Log.v("error","json no está en formato");
                return "json no está en formato";
            }

        }

        @Override
        protected void onPostExecute(String result) {

            Log.d("RRRR",result);
            if( result.equals("true") ) {
//777
//                irEncuestaInicial(new IdInformante(idAsignacion, 0), idNivel, idPadre,codEst);
//                finish();
                //informationMessage("cargarListado", "INFORMACIÓN", Html.fromHtml("Se logró restaurar el establecimiento"), Parametros.FONT_OBS);

            } else {
                //       errorMessage(null, "ERROR!", Html.fromHtml("No se pudo obtener codigo"));
            }
            dialog.dismiss();
        }
    }
    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

}