package bo.gob.ine.naci.epc;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;

import bo.gob.ine.naci.epc.entidades.RolPermiso;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityProcess;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;


/*ESTA CLASE PERMITE ENVIAR UN ARCHIVO MEDIANTE UN CONTENT PROVIDER QUE PUEDE SER ACCEDIDO POR OTRA APLICACION, CON LA OPCION DE COMPARTIR*/
public class SendActivity extends ActionBarActivityProcess {
    private Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        String receivedType = receivedIntent.getType();
        String formatoPermitido = ".sqlite";
        // OBTENEMOS LA RUTA DEL ARCHIVO
        filePath = (Uri)receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (RolPermiso.tienePermiso(Usuario.getRol(), "consolida_cartografia")) {
            if(receivedAction.equals(Intent.ACTION_SEND) && receivedType.equals("text/plain")){
                if(filePath.toString().endsWith(formatoPermitido)) {
                    String a = filePath.getLastPathSegment();
                    if( (RolPermiso.tienePermiso(Usuario.getRol(), "control_perimetro") && (filePath.getLastPathSegment().replace(formatoPermitido,"").startsWith("y") || filePath.getLastPathSegment().replace(formatoPermitido,"").startsWith("a") || filePath.getLastPathSegment().replace(formatoPermitido,"").startsWith("r")) && filePath.getLastPathSegment().replace(formatoPermitido,"").endsWith("A")) || (RolPermiso.tienePermiso(Usuario.getRol(), "control_perimetro") && filePath.getLastPathSegment().replace(formatoPermitido,"").endsWith("D")) || (!RolPermiso.tienePermiso(Usuario.getRol(), "control_perimetro")) ) {
                        if(Usuario.getLogin() != null) {
                            String cartodroidFile=null;
                            try {
                                 cartodroidFile = Movil.zippea (Parametros.DIR_BAK, filePath.getPath(), Usuario.getLogin()+"_"+filePath.getLastPathSegment().replace(formatoPermitido, ".zip"), Usuario.getLogin()+"_"+filePath.getLastPathSegment());
                            }catch (Exception ee){

                            }
                            if( cartodroidFile != null ) {
                                try {
                                    errorMethod = "finish";
                                    successMethod = "finish";
                                    finalMethod = null;
                                    startThree();
                                    new UploadHttpFile().execute(Parametros.URL_UPLOAD_CARTODROID, Parametros.DIR_BAK, cartodroidFile, filePath.getPath(), "Se subió la capa exitosamente");
                                } catch (Exception ex) {
                                    errorMessage(SendActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
                                }
                            } else {
                                errorMessage(SendActivity.this, "finish", "Error!", Html.fromHtml("No se pudo comprimir el archivo "+formatoPermitido), Parametros.FONT_OBS);
                            }
                        } else {
                            errorMessage(SendActivity.this, "finish", "Error!", Html.fromHtml("Debe iniciar sesión en la aplicación "+Parametros.SIGLA_PROYECTO), Parametros.FONT_OBS);
                        }
                    } else {
                        errorMessage(SendActivity.this, "finish", "Error!", Html.fromHtml("Solo puede consolidar la cartografía de Predios"), Parametros.FONT_OBS);
                    }
                } else {
                    errorMessage(SendActivity.this, "finish", "Error!", Html.fromHtml("Elija el formato: "+formatoPermitido), Parametros.FONT_OBS);
                }
            } else {
                errorMessage(SendActivity.this, "finish", "Error!", Html.fromHtml("Permiso no válido"), Parametros.FONT_OBS);
            }
        } else {
            errorMessage(SendActivity.this, "finish", "Error!", Html.fromHtml("No tiene permiso para consolidar la Cartografía"), Parametros.FONT_OBS);
        }

        /*Bundle bundle = getIntent().getExtras();
        usuario = bundle.getString("usuario");
        String receivedAction;
        String receivedType;
        if(usuario == null) {
            usuario = Usuario.getLogin();
            Intent receivedIntent = getIntent();
            receivedAction = receivedIntent.getAction();
            receivedType = receivedIntent.getType();
            filePath = (Uri)receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        } else {
            usuario = "";
            receivedAction = Intent.ACTION_SEND;
            receivedType = "text/plain";
            filePath  = Uri.parse(bundle.getString("filePath"));

        }


        if(receivedAction.equals(Intent.ACTION_SEND) && receivedType.equals("text/plain")){
            if(filePath.toString().endsWith(".CSV")) {
                if(Usuario.getLogin() != null) {
                    String cartodroidFile = Movil.zippea (Parametros.DIR_BAK, filePath.getPath(), "c_"+usuario+"_"+filePath.getLastPathSegment().replace(".CSV", ".zip"), "sice_carto");
                    if( cartodroidFile != null ) {
                        try {
                            errorMethod = "finish";
                            //successMethod = "inserta_CSV_cartodroid";
                            startThree();
                            new UploadCartodroid().execute(cartodroidFile, Parametros.URL_UPLOAD_CARTODROID, Parametros.DIR_BAK, filePath.getPath());
                        } catch (Exception ex) {
                            errorMessage("finish", "Error!", Html.fromHtml(ex.getMessage()));
                        }
                    } else {
                        errorMessage("finish", "Error!", Html.fromHtml("No se pudo encontrar el archivo .CSV."));
                    }
                } else {
                    errorMessage("finish", "Error!", Html.fromHtml("Debe iniciar sesión en la aplicación "+Parametros.SIGLA_PROYECTO));
                }
            } else {
                errorMessage("finish", "Error!", Html.fromHtml("Elija el formato CSV"));
            }
        } else {
            errorMessage("finish", "Error!", Html.fromHtml("Permiso no válido"));
        }*/
    }

    /*@SuppressWarnings("unused")
    public void inserta_CSV_cartodroid() {
        startThree();
        new consolidaRegistroCartodroid().execute();
    }*/

}