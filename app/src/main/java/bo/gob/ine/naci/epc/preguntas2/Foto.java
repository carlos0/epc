package bo.gob.ine.naci.epc.preguntas2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.herramientas.Parametros;

/**
 * Created by INE.
 */
public class Foto extends PreguntaView {
    protected ImageView valor;
    protected TextView txtRuta;
    protected Button tomar;
    protected String file;
    protected Activity context;

    public Foto(Activity context, int id, int idSeccion, String cod, String preg, IdInformante idInformante, int idPregunta, String ayuda, final Boolean mostrarSeccion, String observacion) {
        super(context, id, idSeccion, cod, preg, ayuda, mostrarSeccion, observacion);

        this.context = context;
        file = idInformante.id_asignacion + "-" + idInformante.correlativo + "-" + idPregunta + ".jpg";

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        valor = new ImageView(context);
        valor.setLayoutParams(layoutParams);
        addView(valor);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        tomar = new Button(context);
        tomar.setText("Tomar Fotografía");
        tomar.setLayoutParams(layoutParams);
        addView(tomar);
        tomar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                File dir = new File(Parametros.DIR_FOTO);
                if (!dir.exists()) {
                    if (!dir.mkdir()) {
                        Toast.makeText(Foto.this.context, "No se pudo crear el directorio.", Toast.LENGTH_LONG).show();
                    }
                }
                Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(dir, file)));
                Foto.this.context.startActivityForResult(captureIntent, 150);

            }
        });
        txtRuta = new TextView(context);
        addView(txtRuta);
    }

    /*@Override
    public void onActivityResult(Intent dataIntent) {
        File img = new File(Parametros.DIR_FOTO + file);
        if (img.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(img.getAbsolutePath(), options);
            int photoW = options.outWidth;
            int scale = photoW / valor.getWidth();
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            Bitmap image = BitmapFactory.decodeFile(img.getAbsolutePath(), options);

            Matrix matrix = new Matrix();
            matrix.setRotate(90, (float) image.getWidth() / 2, (float) image.getHeight() / 2);
            Bitmap rotatedBitmap = Bitmap.createBitmap(image, 0, 0, options.outWidth, options.outHeight, matrix, true);

            valor.setImageBitmap(rotatedBitmap);
        }
        txtRuta.setText(img.getPath());
    }*/

    @Override
    public String getCodResp() {
        File img = new File(Parametros.DIR_FOTO + file);
        if (img.exists())
            return "0";
        else
            return "-1";
    }

    @Override
    public String getResp() {
        return file;
    }

    @Override
    public void setResp(String value) {
        File img = new File(Parametros.DIR_FOTO + file);
        if (img.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(img.getAbsolutePath(), options);
            int photoW = options.outWidth;
            int scale = photoW / context.getResources().getDisplayMetrics().widthPixels;
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            Bitmap image = BitmapFactory.decodeFile(img.getAbsolutePath(), options);

            Matrix matrix = new Matrix();
            matrix.setRotate(90, (float) image.getWidth() / 2, (float) image.getHeight() / 2);
            Bitmap rotatedBitmap = Bitmap.createBitmap(image, 0, 0, options.outWidth, options.outHeight, matrix, true);

            valor.setImageBitmap(rotatedBitmap);
        }
        txtRuta.setText(img.getPath());
    }

    @Override
    public void setFocus() {

    }
}
