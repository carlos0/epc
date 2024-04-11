package bo.gob.ine.naci.epc;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.android.material.textfield.TextInputLayout;

import java.io.UnsupportedEncodingException;
import java.util.Timer;

import bo.gob.ine.naci.epc.entidades.DataBase;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityProcess;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class LoginActivity extends ActionBarActivityProcess {

    private String userInput;
    private String passInput;
    private Timer timer;
    private static final long SPLASH_SCREEN_DELAY = 2000;
    private EditText login;
    private EditText password;
    private TextInputLayout contentLogin;
    private TextInputLayout contentPassword;
    private Button sesion;
    private static long back_pressed;
    private ImageView logo;
    private Animation logoAnim;
    private LinearLayout layoutLogin;
    private TextView textVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //DECLARA LAS VARIABLES DE ESTE ACTIVITY
        cargarOpciones();

        if ( checkPermission() ) {
            inicializa();
        } else {
            requestPermissions();
        }
    }

    public void cargarOpciones(){
        logoAnim= AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        layoutLogin = findViewById(R.id.layoutLogin);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        sesion = findViewById(R.id.iniciar_sesion);
        logo = findViewById(R.id.fondo_splash);
        contentLogin = findViewById(R.id.contenedor_login);
        contentPassword = findViewById(R.id.contenedor_password);
        textVersion = findViewById(R.id.text_version);
        textVersion.setText(getString(R.string.text_version,Parametros.VERSION));
        textVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Movil.isNetworkAvailable()) {
                    decisionMessage(LoginActivity.this,"action_apk", null, "ACTUALIZAR", Html.fromHtml("¿Desea actualizar la aplicación?"));
                } else {
                    errorMessage(LoginActivity.this, null, "Error!", Html.fromHtml("No tiene conexión a Internet"), Parametros.FONT_OBS);
                }
            }
        });

        login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (start == 0 && before == 1 && s.length() == 0) {
                if (login.getText().toString().trim().isEmpty()) {
                    contentLogin.setError(getString(R.string.campo_vacio));
                    vibrate();
                }else{
                    contentLogin.setErrorEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (start == 0 && before == 1 && s.length() == 0) {
                if (password.getText().toString().trim().isEmpty()) {
                    contentPassword.setError(getString(R.string.campo_vacio));
                    vibrate();
                }else{
                    contentPassword.setErrorEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()) {
                    if (Movil.isNetworkAvailable()) {
                        userInput = login.getText().toString().trim();
                        passInput = password.getText().toString();
                        switch (userInput) {
                            //DESCARGA NUEVAMENTE LA BASE DE DATOS
                            case "ine":
                                try {
                                    successMethod = "success";
                                    errorMethod = null;
                                    startThree();
                                    new DownloadFileJson().execute(Parametros.URL_DOWNLOAD, Parametros.TABLAS_DESCARGA, userInput, "Los datos se importaron correctamente.",Usuario.getPlainToken());
                                } catch (Exception e) {
                                    errorMessage(LoginActivity.this, "finish", "Error!", Html.fromHtml(e.getMessage()), Parametros.FONT_OBS);
                                }
                                break;
                            //AUTENTICA AL USUARIO
                            default:
                                // AUTENTICACION ONLINE
//                                startThree();
                                successMethod = "success";
                                errorMethod = null;
                                startThree();
                                new ActionBarActivityProcess.Autenticar().execute(userInput, passInput);
                                break;
                        }
                    } else {
                        errorMessage(LoginActivity.this, null, "Error!", Html.fromHtml("Por favor verifica tu conexión a Internet y vuelve a intentar"), Parametros.FONT_OBS);
                    }
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void inicializa () {
        String login = Usuario.getLogin();

//        if (login == null|| Entidad.getDataBase()==null) {
        if (login == null|| !DataBase.existsDataBase()) {
            logo.animate().translationZ(-1f).setDuration(200).setStartDelay(1000);
//            layoutLogin.setVisibility(View.VISIBLE);
            layoutLogin.setAnimation(logoAnim);
//            logo.animate().translationY(1000).setDuration(2000).setStartDelay(1000);
        } else {
            layoutLogin.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_SCREEN_DELAY);
        }
    }

    public void vibrate() {
        Vibrator vibrate= (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrate.vibrate(50);
    }

    public TranslateAnimation shakeError() {
        TranslateAnimation shake = new TranslateAnimation(0, 10, 0, 0);
        shake.setDuration(500);
        shake.setInterpolator(new CycleInterpolator(5)); return shake;
    }

    public boolean validate() {
        int count = 0;
        if (TextUtils.isEmpty(login.getText())) {
            contentLogin.setError(getString(R.string.campo_vacio));
            contentLogin.startAnimation(shakeError());
            vibrate();
            count ++;
        }
        if (TextUtils.isEmpty(password.getText())) {
            contentPassword.setError(getString(R.string.campo_vacio));
            contentPassword.startAnimation(shakeError());
            vibrate();
        }
        if(count == 0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if( grantResults.length > 0 && checkPermission() ) {
                    inicializa();
                } else {
                    //requestPermissions();
                    informationMessage(LoginActivity.this, "finish", "Sin permisos!", Html.fromHtml("Dirijase al Administrador de aplicaciones para otorgarle los permisos a la aplicación"), Parametros.FONT_OBS);
                }
                return;
            }
        }
    }


    @SuppressWarnings("unused")
    public void success() {
//        new Flujo().updateRegla();
//        new Pregunta().updateRegla();
//        new Regla().updateRegla();
        if ((Usuario.autenticar(userInput, passInput)).equals("Ok")) {
            userInput="";
            passInput="";
            irPrincipal();
            finish();
        } else {
            errorMessage(LoginActivity.this, "finish", "Error!", Html.fromHtml("Base desactualizada"), Parametros.FONT_OBS);
        }
    }

    @SuppressWarnings("unused")
    public void error() {
        finish();
    }

    public void ir_principal(View view) {
        timer.cancel();
        timer.purge();
        irPrincipal();
        finish();
    }

    /*@SuppressWarnings("unused")
    public void actualizaUsuario() {
        try {
            startThree();
            successMethod = null;
            new DownloadFile().execute(new URL(Parametros.URL_CERRAR_SESION + "serie=" + Movil.getMacBluetooth()));
        } catch (MalformedURLException e) {
            errorMessage("finish", "Error!", Html.fromHtml(e.getMessage()));
        }
    }*/


    /**VERIFICAR PARA INTERPRETAR TOKEN JSON*/
    /****/
    public static void decoded(String JWTEncoded) throws Exception {
        try {
            String[] split = JWTEncoded.split("\\.");
            Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
        } catch (UnsupportedEncodingException e) {
            //Error
        }
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException{
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
    /****/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent dataIntent) {
        super.onActivityResult(requestCode, resultCode, dataIntent);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK && !dataIntent.getDataString().contains(".")) {
                    successMethod = "success_bak";
                    errorMethod = "finish";
                    new RestoreBackup().execute(dataIntent);
                } else {
                    errorMessage(LoginActivity.this, "autenticarMessage", "Error!", Html.fromHtml("Archivo denegado."), Parametros.FONT_OBS);
                }
                break;
            /*case 2:
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    if (dataIntent != null) {
                        Barcode barcode = dataIntent.getParcelableExtra(QrCaptureActivity.BarcodeObject);
                        observation = barcode.displayValue.trim();
                        if( !observation.startsWith("SICE") ) {
                            errorMessage("autenticarMessage", "ERROR", Html.fromHtml(getString(R.string.action_qr_ajeno)));
                        } else {
                            String[] part = observation.split(";");
                            if( part.length != 3 ){
                                errorMessage("autenticarMessage", "ERROR", Html.fromHtml(getString(R.string.action_qr_invalido)));
                            } else {
                                try {
                                    startThree();
                                    successMethod = "actualizaUsuario";
                                    new CambiaMac().execute(new URL(Parametros.URL_CAMBIA_MAC + "&serie=" + Movil.getMacBluetooth() +"&new_usr=" + part[1].trim() + "&new_serie=" + part[2].trim()));
                                } catch (Exception ex) {
                                    errorMessage("finish", "Error!", Html.fromHtml(ex.getMessage()));
                                }
                            }
                        }
                    } else {
                        informationMessage("autenticarMessage", "ERROR", Html.fromHtml(getString(R.string.action_qr_no_se_detecto)));
                    }
                } else {
                    toastMessage(String.format("Error: %1$s", CommonStatusCodes.getStatusCodeString(resultCode)), null);
                }
                break;*/
        }
    }

    public void click_download_apk (View v) {
        decisionMessage(LoginActivity.this, "action_apk", null, "ACTUALIZAR", Html.fromHtml("¿Desea actualizar la aplicación?"));
    }

//    @SuppressWarnings("unused")
//    public void action_apk() {
//        try {
//            startThree();
//            new ActionBarActivityProcess.DownloadHttpFile().execute(Parametros.DIR_TEMP, Parametros.URL_APK, Parametros.SIGLA_PROYECTO.toLowerCase()+".apk", null);
//        } catch (Exception ex) {
//            errorMessage("finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
//        }
//    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            this.finish();
//            super.onBackPressed();

        } else {
            Toast.makeText(getBaseContext(), "Presione de nuevo para salir!", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }
    }
    @SuppressWarnings("unused")
    public void action_apk() {
        try {
            startThree();
            new DownloadHttpFile().execute(Parametros.DIR_TEMP, Parametros.URL_APK, Parametros.SIGLA_PROYECTO.toLowerCase()+".apk", null);
        } catch (Exception ex) {
            errorMessage(LoginActivity.this, "finish", "Error!", Html.fromHtml(ex.getMessage()), Parametros.FONT_OBS);
        }
    }
}
