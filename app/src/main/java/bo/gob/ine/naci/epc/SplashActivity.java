package bo.gob.ine.naci.epc;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.ActionBarActivityProcess;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class SplashActivity extends ActionBarActivityProcess {

    private static final long SPLASH_SCREEN_DELAY = 2000;
    private Animation topAnim, bottonAnim;
    private ImageView fondo,image;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        getSupportActionBar().hide();

//        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
//        bottonAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
//
//        fondo = findViewById(R.id.fondo_splash);
        image = findViewById(R.id.logo);
//        TextView txtSiceNombre = findViewById(R.id.txt_sice_nombre_completo);
//        TextView textView2 = findViewById(R.id.txt_sice_nombre_completo);
//        txtSiceNombre.setText(String.format(getString(R.string.sice_nombre_completo), Parametros.VERSION));

//        fondo.setAnimation(topAnim);
        image.setAnimation(bottonAnim);
//        txtSiceNombre.setAnimation(bottonAnim);
//        textView2.setAnimation(bottonAnim);

        if ( checkPermission() ) {
            inicializa();
        } else {
            requestPermissions();
        }
    }


    public void inicializa () {
        String login = Usuario.getLogin();
        if (login == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View,String>(image, "logo_login");

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pairs);
                        startActivity(intent, options.toBundle());
                        finish();
                    }
                }
            }, SPLASH_SCREEN_DELAY);

        } else {
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    irPrincipal();
                    finish();
                }
            };
            timer = new Timer();
            timer.schedule(task, SPLASH_SCREEN_DELAY);
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
                    informationMessage(SplashActivity.this, "finish", "Sin permisos!", Html.fromHtml("Dirijase al Administrador de aplicaciones para otorgarle los permisos a la aplicación"), Parametros.FONT_OBS);
                }
                return;
            }
        }
    }

}
