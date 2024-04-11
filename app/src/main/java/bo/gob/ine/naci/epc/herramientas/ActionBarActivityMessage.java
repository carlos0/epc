package bo.gob.ine.naci.epc.herramientas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bo.gob.ine.naci.epc.BoletaActivity;
import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.Brigada;
import bo.gob.ine.naci.epc.entidades.IdInformante;
import bo.gob.ine.naci.epc.entidades.Informante;
import bo.gob.ine.naci.epc.entidades.Observacion;
import bo.gob.ine.naci.epc.entidades.Proyecto;
import bo.gob.ine.naci.epc.entidades.TableDynamicObservacion;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.fragments.FragmentEncuesta;
import bo.gob.ine.naci.epc.fragments.FragmentInicial;
import bo.gob.ine.naci.epc.fragments.FragmentInicial2;
import bo.gob.ine.naci.epc.fragments.FragmentList;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static android.graphics.Color.green;

public class ActionBarActivityMessage extends AppCompatActivity implements DialogInterface.OnDismissListener {
    protected String buttonPressed;
    protected String methodAceptar;
    protected String methodCancelar;
    protected String observation;
    protected Integer id;
    protected Integer listPosition;
    protected LinearLayout.LayoutParams layoutParamsButton = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    protected LinearLayout.LayoutParams layoutParamsButton2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    //BLOQUEA LA ORIENTACION DE LA PANTALLA
    public void startThree() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }

    //DESBLOQUEA LA ORIENTACION DE LA PANTALLA
    public void endThree() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public void toastMessage(String mensaje, Integer color) {
        if(color == null) {
            Toast toast = Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -300);
            toast.show();
        } else {
            Toast toast = Toast.makeText(this, mensaje, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.getView().setBackgroundColor(color);
            TextView textView =(TextView) toast.getView().findViewById(android.R.id.message);
            textView.setTextSize(Parametros.FONT_LIST_SMALL);
            textView.setTextColor(WHITE);
            toast.show();
        }
    }

    public void exitoMessage(final Context context, String method, String titulo, Spanned mensaje, float tamanoLetra) {
        this.methodAceptar = method;
        this.methodCancelar = null;
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
        sweetAlertDialog.setTitleText(titulo);
        sweetAlertDialog.setContentText(mensaje.toString());
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Aceptar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton);
    }

    public void errorMessages(Context context, String method, String titulo, Spanned mensaje, float tamanoLetra) {
        this.methodAceptar = method;
        this.methodCancelar = null;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensaje);
        alertDialog.setIcon(R.drawable.error);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                buttonPressed = "Aceptar";
            }
        });
        buttonPressed = "";
        AlertDialog dialog = alertDialog.create();
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    public void errorMessage(Context context, String method, String titulo, Spanned mensaje, float tamanoLetra) {
        this.methodAceptar = method;
        this.methodCancelar = null;
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        sweetAlertDialog.setTitleText(titulo);
        sweetAlertDialog.setContentText(mensaje.toString());
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Aceptar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton);
    }

    public void decisionMessage(Context context, String methodAceptar, String methodCancelar, String titulo, Spanned mensaje) {
        this.methodAceptar = methodAceptar;
        this.methodCancelar = methodCancelar;
        layoutParamsButton2.setMargins(5,5,5,5);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText(titulo);
        sweetAlertDialog.setContentText(mensaje.toString());
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setConfirmText("Si");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Aceptar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        sweetAlertDialog.setCancelText("No");
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Cancelar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton2);

        Button buttonCancel = sweetAlertDialog.findViewById(R.id.cancel_button);
        buttonCancel.setLayoutParams(layoutParamsButton2);
    }


    public void decisionMessageFinalizar(Context context, final String methodAceptar, String methodCancelar, String titulo, Spanned mensaje) {
        this.methodAceptar = methodAceptar;
        this.methodCancelar = methodCancelar;
        layoutParamsButton2.setMargins(5,5,5,5);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText(titulo);
        sweetAlertDialog.setContentText(mensaje.toString());
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setConfirmText("Si");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                buttonPressed = "Aceptar";
                if(methodAceptar.equals("resumen")){
                    FragmentEncuesta.resumen();
                } else if(methodAceptar.equals("crear_nueva_boleta")){
                    FragmentInicial.crear_nueva_boleta();
                }
                else {
//                    FragmentEncuesta.pasarObservado(activity, context, observation);
                }

                sweetAlertDialog.dismissWithAnimation();
            }
        });
        sweetAlertDialog.setCancelText("No");
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if(methodCancelar.equals("vivienda")){
                    FragmentInicial.vivienda(context);
//                    finish();
                }
                buttonPressed = "Cancelar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton2);

        Button buttonCancel = sweetAlertDialog.findViewById(R.id.cancel_button);
        buttonCancel.setLayoutParams(layoutParamsButton2);
    }


    public void decisionMessageDelete(final Activity activity, String methodAceptar, String methodCancelar, String titulo, Spanned mensaje, final int idAsignacion, final int correlativo, final int idUpm, final String tipo) {
        this.methodAceptar = null;
        this.methodCancelar = null;
        layoutParamsButton2.setMargins(5,5,5,5);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setTitleText(titulo);
        sweetAlertDialog.setContentText(mensaje.toString());
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setConfirmText("Si");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Aceptar";
                switch (tipo){
                    case "persona":
                        FragmentList.descartar(activity, new IdInformante(idAsignacion, correlativo));
                        break;
                    case "hogar":
                        Informante.eliminaBoletas(new IdInformante(idAsignacion, correlativo));
                        finish();
                        Bundle bundle = new Bundle();
                        bundle.putInt("IdUpm",  idUpm);

                        Intent informante = new Intent(activity, BoletaActivity.class);
                        informante.putExtras(bundle);
                        activity.startActivity(informante);
                        break;
                    case "lv":
//                        BoletaActivity.descartar(new IdInformante(idAsignacion, correlativo));
//                        finish();
//                        Bundle bundle = new Bundle();
//                        bundle.putInt("IdUpm",  idUpm);
//
//                        Intent informante = new Intent(context, BoletaActivity.class);
//                        informante.putExtras(bundle);
//                        context.startActivity(informante);
                        break;
                }
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        sweetAlertDialog.setCancelText("No");
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Cancelar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton2);

        Button buttonCancel = sweetAlertDialog.findViewById(R.id.cancel_button);
        buttonCancel.setLayoutParams(layoutParamsButton2);
    }

    public void informationMessage(Context context, String method, String titulo, Spanned mensaje, float tamanoLetra) {
        this.methodAceptar = method;
        this.methodCancelar = null;
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        sweetAlertDialog.setTitleText(titulo);
        sweetAlertDialog.setContentText(String.valueOf(Html.fromHtml(String.valueOf(mensaje.toString()))));
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.setCustomImage(R.drawable.ic_info_outline_black_24dp);
        sweetAlertDialog.setConfirmText("Aceptar");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Aceptar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton);

    }

    public void informationMessageObs(String method, String titulo, Spanned mensaje, float tamanoLetra) {
        this.methodAceptar = method;
        this.methodCancelar = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensaje);
        alertDialog.setIcon(R.drawable.info);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                buttonPressed = "Aceptar";
            }
        });
        buttonPressed = "";
        AlertDialog dialog = alertDialog.create();
        dialog.setOnDismissListener(this);
        dialog.show();

        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(tamanoLetra);
    }

    public void informationMessageObs2(Context context, String titulo, Spanned mensaje, float tamanoLetra) {

        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        ScrollView scrollView = new ScrollView(context);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView observaciones = new TextView(context);
        observaciones.setText(Html.fromHtml(mensaje.toString()));
        observaciones.setTextSize(Parametros.FONT_RESP);
        linearLayout.addView(observaciones);
        scrollView.addView(linearLayout);
        relativeLayout.addView(scrollView);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setView(relativeLayout);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                buttonPressed = "Aceptar";
            }
        });
        buttonPressed = "";
        AlertDialog dialog = alertDialog.create();
        dialog.show();

        TextView textView = (TextView) dialog.findViewById(android.R.id.message);
        textView.setTextSize(tamanoLetra);
    }

    public void aboutMessage(Context context, String nombreProyecto, String qrContenido) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.adapter_about, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        TextView title = new TextView(this);
        title.setText(Html.fromHtml(nombreProyecto));
        title.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(getResources().getColor(R.color.color_list));
        title.setTextSize(Parametros.FONT_PREG);
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("<b>").append(Usuario.getRolDescripcion()).append(": </b><br>");
        mensaje.append(Usuario.getNombreUsuario()).append(" (" + Usuario.getLogin() + ")<br>");
        mensaje.append("<b>Brigada:</b> ").append(Brigada.getCodigoBrigada(Usuario.getBrigada())).append("<br>");
        mensaje.append("<b>Versión Aplicación:</b> ").append(Parametros.VERSION).append("<br>");
        mensaje.append("<b>Versión Boleta:</b> ").append(Proyecto.getBoletaVersion()).append("<br>");
        mensaje.append(Movil.getImei()).append("<br>");

        TextView aboutContenido = (TextView)(((LinearLayout)dialogLayout).findViewById(R.id.aboutTextView));
        aboutContenido.setText(Html.fromHtml(mensaje.toString()));
        aboutContenido.setTextSize(Parametros.FONT_RESP);

        ImageView imageViewQR = (ImageView) (((LinearLayout)dialogLayout).findViewById(R.id.qrImageView));
        try {
            Bitmap bitmap = encodeAsBitmap(qrContenido);
            imageViewQR.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        sweetAlertDialog.setCustomView(dialogLayout);
        sweetAlertDialog.setConfirmText("Aceptar");
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton);
    }

    // UTILIZADO PARA MOSTRAR UN CODIGO QR
    public Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        int WIDTH = 500;
        try {
            result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }

    public void observationMessage(Context context, String method, String titulo, Spanned mensaje, String hint, boolean isPassword) {
        this.methodAceptar = method;
        this.methodCancelar = null;
        LayoutInflater inflater =  getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.adapter_observacion, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        TextView titleContenido = (TextView)(((LinearLayout)dialogLayout).findViewById(R.id.observacionTitle));
        titleContenido.setText(Html.fromHtml(titulo));
        titleContenido.setTextSize(Parametros.FONT_RESP);

        layoutParamsButton2.setMargins(5,5,5,5);

        TextView aboutContenido = (TextView)(((LinearLayout)dialogLayout).findViewById(R.id.observacionTextView));
        aboutContenido.setText(Html.fromHtml(String.valueOf(mensaje)));
        aboutContenido.setTextSize(Parametros.FONT_RESP);

        final TextInputEditText editText = (TextInputEditText)(((LinearLayout)dialogLayout).findViewById(R.id.observacion));
        editText.setText(hint);
        InputFilter characterFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                if(source != null && Parametros.BLOCK_CHARACTER_SET.contains(""+source)){
                    return "";
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{characterFilter});

        if(isPassword) {

        }
        sweetAlertDialog.setCustomView(dialogLayout);
        sweetAlertDialog.setCancelable(false);

        sweetAlertDialog.setConfirmText("Aceptar");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                observation = editText.getText().toString();
                buttonPressed = "Aceptar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        sweetAlertDialog.setCancelText("Cancelar");
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Cancelar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton2);

        Button buttonCancel = sweetAlertDialog.findViewById(R.id.cancel_button);
        buttonCancel.setLayoutParams(layoutParamsButton2);
    }

    public void observationMessageLV(Context context, String method, String titulo, Spanned mensaje, String hint, boolean isPassword) {
        this.methodAceptar = method;
        this.methodCancelar = null;
        LayoutInflater inflater =  getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.adapter_observacion, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        TextView titleContenido = (TextView)(((LinearLayout)dialogLayout).findViewById(R.id.observacionTitle));
        titleContenido.setText(Html.fromHtml(titulo));
        titleContenido.setTextSize(Parametros.FONT_RESP);

        layoutParamsButton2.setMargins(5,5,5,5);

        TextView aboutContenido = (TextView)(((LinearLayout)dialogLayout).findViewById(R.id.observacionTextView));
        aboutContenido.setText(Html.fromHtml(String.valueOf(mensaje)));
        aboutContenido.setTextSize(Parametros.FONT_RESP);

        final TextInputEditText editText = (TextInputEditText)(((LinearLayout)dialogLayout).findViewById(R.id.observacion));
        editText.setText(hint);
        InputFilter characterFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                if(source != null && Parametros.BLOCK_CHARACTER_SET.contains(""+source)){
                    return "";
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{characterFilter});

        if(isPassword) {

        }        RelativeLayout relativeLayout = new RelativeLayout(context);
        relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        Button button=new Button(context);
        button.setText("NO TIENE");
        relativeLayout.addView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observation = "NO TIENE";
                buttonPressed = "Aceptar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        ((LinearLayout) dialogLayout).addView(relativeLayout);
        sweetAlertDialog.setCustomView(dialogLayout);
        sweetAlertDialog.setCancelable(false);

        sweetAlertDialog.setConfirmText("Aceptar");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                observation = editText.getText().toString();
                buttonPressed = "Aceptar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        sweetAlertDialog.setCancelText("Cancelar");
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Cancelar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton2);

        Button buttonCancel = sweetAlertDialog.findViewById(R.id.cancel_button);
        buttonCancel.setLayoutParams(layoutParamsButton2);
    }
    public void observationSupervisor(final Context context, String method, String titulo, Spanned mensaje, String hint, boolean isPassword, final int idAsignacion, final int correlativo, final int idUpm, final ArrayList supervisores) {
        this.methodAceptar = null;
        this.methodCancelar = null;
        LayoutInflater inflater =  getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.adapter_observacion, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        TextView titleContenido = (TextView)(((LinearLayout)dialogLayout).findViewById(R.id.observacionTitle));
        titleContenido.setText(Html.fromHtml(titulo));
        titleContenido.setTextSize(Parametros.FONT_RESP);

        layoutParamsButton2.setMargins(5,5,5,5);

        TextView aboutContenido = (TextView)(((LinearLayout)dialogLayout).findViewById(R.id.observacionTextView));
        aboutContenido.setText(Html.fromHtml(String.valueOf(mensaje)));
        aboutContenido.setTextSize(Parametros.FONT_RESP);

        final TextInputEditText editText = (TextInputEditText)(((LinearLayout)dialogLayout).findViewById(R.id.observacion));
        final InputFilter characterFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                if(source != null && Parametros.BLOCK_CHARACTER_SET.contains(""+source)){
                    return "";
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{characterFilter});

        if(isPassword) {

        }
        sweetAlertDialog.setCustomView(dialogLayout);
        sweetAlertDialog.setCancelable(false);

        sweetAlertDialog.setConfirmText("Aceptar");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                observation = editText.getText().toString();
                buttonPressed = "Aceptar";
                Log.d("supervisor", editText.getText().toString());
                if(supervisores.contains(editText.getText().toString())) {
                    BoletaActivity.habilitaSupervisor(new IdInformante(idAsignacion, correlativo));
                    finish();
                    Bundle bundle = new Bundle();
                    bundle.putInt("IdUpm",  idUpm);

                    Intent informante = new Intent(context, BoletaActivity.class);
                    informante.putExtras(bundle);
                    context.startActivity(informante);
                }
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        sweetAlertDialog.setCancelText("Cancelar");
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Cancelar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton2);

        Button buttonCancel = sweetAlertDialog.findViewById(R.id.cancel_button);
        buttonCancel.setLayoutParams(layoutParamsButton2);
    }

    public void observationSupervisorTab(final Context context, String method, String titulo, Spanned mensaje, String hint, boolean isPassword, final int idAsignacion, final int correlativo, final int idUpm, final ArrayList supervisores, IdInformante idInformante) {
        this.methodAceptar = null;
        this.methodCancelar = null;
        LayoutInflater inflater =  getLayoutInflater();
        View dialogLayout = inflater.inflate(R.layout.adapter_observacion, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        TextView titleContenido = (TextView)(((LinearLayout)dialogLayout).findViewById(R.id.observacionTitle));
        titleContenido.setText(Html.fromHtml(titulo));
        titleContenido.setTextSize(Parametros.FONT_RESP);

        layoutParamsButton2.setMargins(5,5,5,5);

        TextView aboutContenido = (TextView)(((LinearLayout)dialogLayout).findViewById(R.id.observacionTextView));
        aboutContenido.setText(Html.fromHtml(String.valueOf(mensaje)));
        aboutContenido.setTextSize(Parametros.FONT_RESP);

        TableLayout tableLayout = (TableLayout)(((LinearLayout)dialogLayout).findViewById(R.id.table_obs));
        try{
            List<Map<String,Object>> dataMap= Observacion.obtenerDatosTabla(idInformante);
            String [] header=dataMap.get(0).keySet().toArray(new String[0]);
            ArrayList<String[]> data=new ArrayList<>();
            for (Map<String,Object> it:dataMap
            ) {
                ArrayList<String> a=new ArrayList<>();
                for (String e:header
                ) {
                    a.add(String.valueOf(it.get(e)));
                }
                data.add(a.toArray(new String[0]));
            }
            TableDynamicObservacion tableDynamic=new TableDynamicObservacion(tableLayout, context);
            tableDynamic.addHeader(header);
            tableDynamic.addData(data);
            tableDynamic.backgroundHeader(MyApplication.getContext().getResources().getColor(R.color.colorPrimary));
            tableDynamic.textColorHeader(MyApplication.getContext().getResources().getColor(R.color.color_list));
            tableDynamic.backgroundData(MyApplication.getContext().getResources().getColor(R.color.colorInfo),MyApplication.getContext().getResources().getColor(R.color.colorPrimary));
            tableDynamic.lineColor(MyApplication.getContext().getResources().getColor(R.color.color_list));
            tableDynamic.textColorData(MyApplication.getContext().getResources().getColor(R.color.color_list));
        }catch (Exception e){

        }


        final TextInputEditText editText = (TextInputEditText)(((LinearLayout)dialogLayout).findViewById(R.id.observacion));
        final InputFilter characterFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                if(source != null && Parametros.BLOCK_CHARACTER_SET.contains(""+source)){
                    return "";
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{characterFilter});

        if(isPassword) {

        }
        sweetAlertDialog.setCustomView(dialogLayout);
        sweetAlertDialog.setCancelable(false);

        sweetAlertDialog.setConfirmText("Aceptar");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                observation = editText.getText().toString();
                buttonPressed = "Aceptar";
                Log.d("supervisor", editText.getText().toString());
                if(supervisores.contains(editText.getText().toString())) {
                    BoletaActivity.habilitaSupervisor(new IdInformante(idAsignacion, correlativo));
                    finish();
                    Bundle bundle = new Bundle();
                    bundle.putInt("IdUpm",  idUpm);

                    Intent informante = new Intent(context, BoletaActivity.class);
                    informante.putExtras(bundle);
                    context.startActivity(informante);
                }
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        sweetAlertDialog.setCancelText("Cancelar");
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Cancelar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton2);

        Button buttonCancel = sweetAlertDialog.findViewById(R.id.cancel_button);
        buttonCancel.setLayoutParams(layoutParamsButton2);
    }


    public void observationMessageEnc(final Activity activity, final Context context, final String method, String titulo, Spanned mensaje, String hint, boolean isPassword) {
        this.methodAceptar = null;
        this.methodCancelar = null;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.adapter_observacion, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        TextView titleContenido = (TextView)(((LinearLayout)dialogLayout).findViewById(R.id.observacionTitle));
        titleContenido.setText(Html.fromHtml(titulo));
        titleContenido.setTextSize(Parametros.FONT_RESP);

        layoutParamsButton2.setMargins(5,5,5,5);

        TextView aboutContenido = (TextView)(((LinearLayout)dialogLayout).findViewById(R.id.observacionTextView));
        aboutContenido.setText(Html.fromHtml(String.valueOf(mensaje)));
        aboutContenido.setTextSize(Parametros.FONT_RESP);

        final TextInputEditText editText = (TextInputEditText)(((LinearLayout)dialogLayout).findViewById(R.id.observacion));
        editText.setText(hint);
        InputFilter characterFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                if(source != null && Parametros.BLOCK_CHARACTER_SET.contains(""+source)){
                    return "";
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{characterFilter});

        if(isPassword) {

        }
        sweetAlertDialog.setCustomView(dialogLayout);
        sweetAlertDialog.setCancelable(false);

        sweetAlertDialog.setConfirmText("Aceptar");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                observation = editText.getText().toString();
                buttonPressed = "Aceptar";
                if(method.equals("obs")){
//                    FragmentEncuesta.obs(observation);
                } else{
//                    FragmentEncuesta.pasarObservado(activity, context, observation);
                }

                sweetAlertDialog.dismissWithAnimation();
            }
        });
        sweetAlertDialog.setCancelText("Cancelar");
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Cancelar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton2);

        Button buttonCancel = sweetAlertDialog.findViewById(R.id.cancel_button);
        buttonCancel.setLayoutParams(layoutParamsButton2);
    }

    public void observationMessageEncIni(final Activity activity, final Context context, final String method, String titulo, Spanned mensaje, String hint, boolean isPassword, final int indiceInicial) {
        this.methodAceptar = null;
        this.methodCancelar = null;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.adapter_observacion, null);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        TextView titleContenido = (TextView)(((LinearLayout)dialogLayout).findViewById(R.id.observacionTitle));
        titleContenido.setText(Html.fromHtml(titulo));
        titleContenido.setTextSize(Parametros.FONT_RESP);

        layoutParamsButton2.setMargins(5,5,5,5);

        TextView aboutContenido = (TextView)(((LinearLayout)dialogLayout).findViewById(R.id.observacionTextView));
        aboutContenido.setText(Html.fromHtml(String.valueOf(mensaje)));
        aboutContenido.setTextSize(Parametros.FONT_RESP);

        final TextInputEditText editText = (TextInputEditText)(((LinearLayout)dialogLayout).findViewById(R.id.observacion));
        editText.setText(hint);
        InputFilter characterFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned spanned, int i2, int i3) {
                if(source != null && Parametros.BLOCK_CHARACTER_SET.contains(""+source)){
                    return "";
                }
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{characterFilter});

        if(isPassword) {

        }
        sweetAlertDialog.setCustomView(dialogLayout);
        sweetAlertDialog.setCancelable(false);

        sweetAlertDialog.setConfirmText("Aceptar");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                observation = editText.getText().toString();
                buttonPressed = "Aceptar";
                if(method.equals("obsIni")){
                    FragmentInicial.obsIni(observation,indiceInicial);
                }
                if(method.equals("obsIni2")){
                    FragmentInicial2.obsIni2(observation,indiceInicial);
                }

                sweetAlertDialog.dismissWithAnimation();
            }
        });
        sweetAlertDialog.setCancelText("Cancelar");
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Cancelar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton2);

        Button buttonCancel = sweetAlertDialog.findViewById(R.id.cancel_button);
        buttonCancel.setLayoutParams(layoutParamsButton2);
    }

    public String[] retornaObs(){
        String[] res;
        res = new String[]{buttonPressed, observation};
        return res;
    }



    public void numeroMessage(Context context, String methodAceptar, String methodCancelar, Spanned mensaje, int val) {
        this.methodAceptar = methodAceptar;
        this.methodCancelar = methodCancelar;
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setTitle("Introduzca");

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textMensaje = new TextView(this);
        textMensaje.setText(mensaje);
        linearLayout.addView(textMensaje);

        final EditText valor = new EditText(this);
        valor.setText(String.valueOf(val));
        valor.setInputType(InputType.TYPE_CLASS_NUMBER);
        valor.setSingleLine();
        linearLayout.addView(valor);

        sweetAlertDialog.setCustomView(linearLayout);
        sweetAlertDialog.setCancelable(false);

        sweetAlertDialog.setConfirmText("Aceptar");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                observation = valor.getText().toString();
                buttonPressed = "Aceptar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        sweetAlertDialog.setCancelText("Cancelar");
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Cancelar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton);

        Button buttonCancel = sweetAlertDialog.findViewById(R.id.cancel_button);
        buttonCancel.setLayoutParams(layoutParamsButton);

    }

    public void selectionMessage(Context context, String method, String titulo, Spanned mensaje, Map<Integer, String> options, int seleccionado) {
        this.methodAceptar = method;
        this.methodCancelar = null;

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        sweetAlertDialog.setTitle(titulo);

        ScrollView scrollView = new ScrollView(this);
        scrollView.setVerticalScrollBarEnabled(true);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setPadding(15,15,15,15);
        linearLayout.setVerticalScrollBarEnabled(true);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textMensaje = new TextView(this);
        textMensaje.setText(mensaje);
        textMensaje.setTextSize(Parametros.FONT_RESP);
        linearLayout.addView(textMensaje);

        final RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        for (Integer key : options.keySet()) {
            RadioButton rb = new RadioButton(this);
            rb.setText(options.get(key));
            rb.setTextSize(Parametros.FONT_RESP);
            rb.setId(key);
            radioGroup.addView(rb);
            if (key == seleccionado) {
                rb.setChecked(true);
            }
        }
        linearLayout.addView(radioGroup);

        scrollView.addView(linearLayout);
        sweetAlertDialog.setCustomView(scrollView);
        sweetAlertDialog.setCancelable(false);

        sweetAlertDialog.setConfirmText("Aceptar");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                id = radioGroup.getCheckedRadioButtonId();
                buttonPressed = "Aceptar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        sweetAlertDialog.setCancelText("Cancelar");
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                buttonPressed = "Cancelar";
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        buttonPressed = "";
        sweetAlertDialog.setOnDismissListener(this);
        sweetAlertDialog.show();

        Button buttonConfirm = sweetAlertDialog.findViewById(R.id.confirm_button);
        buttonConfirm.setLayoutParams(layoutParamsButton);

        Button buttonCancel = sweetAlertDialog.findViewById(R.id.cancel_button);
        buttonCancel.setLayoutParams(layoutParamsButton);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            switch (buttonPressed) {
                case "Aceptar": if (methodAceptar != null) {
                    Method m = this.getClass().getMethod(methodAceptar);
                    m.invoke(this);
                    break;
                }
                case "Cancelar": if (methodCancelar != null) {
                    Method m = this.getClass().getMethod(methodCancelar);
                    m.invoke(this);
                    break;
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //METODOS PARA EL CONTROL DE SERVICIOS EN ANDROID 6.0, SUPERIORES
    protected boolean checkPermission() {
        boolean result = true;
        for (String permission : Parametros.permissions) {
            if ( !(ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) ) {
                result = false;
            }
        }
        return result;
    }

    protected void requestPermissions() {
        ActivityCompat.requestPermissions(this, Parametros.permissions, 1);
    }
}

