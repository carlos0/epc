package bo.gob.ine.naci.epc.herramientas;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//TODO:BRP

public class ToolMaps {
    public static final int PATTERN_DASH_LENGTH_PX = 5;
    public static final int PATTERN_GAP_LENGTH_PX = 5;
    public static final PatternItem DOT = new Dot();
    public static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    public static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    public static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    public static final int PATTERN_DASH_LENGTH_PX1 = 15;
    public static final int PATTERN_GAP_LENGTH_PX1 = 10;
    public static final PatternItem DASH1 = new Dash(PATTERN_DASH_LENGTH_PX1);
    public static final PatternItem GAP1 = new Gap(PATTERN_GAP_LENGTH_PX1);
    public static final PatternItem DOT1 = new Dot();
    public static final List<PatternItem> PATTERN_POLYGON_BETA = Arrays.asList(GAP1, DASH1);

    public static final int PATTERN_DASH_LENGTH_PX2 = 20;
    public static final int PATTERN_GAP_LENGTH_PX2 = 10;
    public static final PatternItem DASH2 = new Dash(PATTERN_DASH_LENGTH_PX2);
    public static final PatternItem GAP2 = new Gap(PATTERN_GAP_LENGTH_PX2);
    public static final PatternItem DOT2 = new Dot();
    public static final List<PatternItem> PATTERN_POLYGON_GAMA = Arrays.asList(DOT2, GAP2, DASH2, GAP2);

    //CAPA 1
    private ArrayList<String> valLimite_municipio = new ArrayList<>();
    private ArrayList<Map<String, Object>> valD_hidrografia = new ArrayList<>();
    private ArrayList<Map<String, Object>> valD_hipsografia = new ArrayList<>();
    //CAPA 2
    private ArrayList<String> valA_areatrabajo = new ArrayList<>();
    private ArrayList<String> valD_areatrabajo = new ArrayList<>();
    //CAPA 3
    private ArrayList<String> valA_manzanos = new ArrayList<>();
    private ArrayList<String> valA_perimetro = new ArrayList<>();
    private ArrayList<Map<String, Object>> valD_com_loc = new ArrayList<>();
    private ArrayList<Map<String, Object>> valD_com_loc_text = new ArrayList<>();
    //CAPA 4
    private ArrayList<String> valA_vias = new ArrayList<>();
    private ArrayList<String> valD_vias = new ArrayList<>();
    private ArrayList<Map<String, Object>> valA_equipamiento = new ArrayList<>();
    private ArrayList<Map<String, Object>> valD_equipamiento = new ArrayList<>();

//    public PolygonOptions poligonoBlanco(ArrayList<LatLng> latLngs){
//        PolygonOptions polygonOptions = new PolygonOptions()
//                .clickable(false)
//                .strokeColor(Color.WHITE)
//                .fillColor(MyApplication.getContext().getResources().getColor(R.color.color_blanco_transparente));
//        polygonOptions.addAll(latLngs);
//        polygonOptions.strokePattern(PATTERN_POLYGON_ALPHA);
//
//        return polygonOptions;
//    }

    public static TileOverlayOptions graficaMBTiles(String dir){
        TileProvider tileProvider = new ExpandedMBTilesTileProvider(new File(dir), 1024, 1024);
        TileOverlayOptions tileOverlayOptions = new TileOverlayOptions().tileProvider(tileProvider);
        return tileOverlayOptions;
    }

    public static LatLng getCentroid(List<LatLng> polygon) {
        List<LatLng> points = polygon;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < points.size() - 1; ++i) {
            builder.include(points.get(i));
        }
        LatLngBounds a = builder.build();
        return a.getCenter();
    }

    public static boolean validador(Polyline pLinea) {
        for (int i = 0; i < pLinea.getPoints().size(); i++) {
            if (i != pLinea.getPoints().size() - 1) {
                LatLng pInicial = pLinea.getPoints().get(i);
                LatLng pFinal = pLinea.getPoints().get(i + 1);
                Log.d("size", String.valueOf(pLinea.getPoints().size()));

                for (int j = 0; j < pLinea.getPoints().size(); j++) {
                    Log.d("pos", String.valueOf(j));
                    if (j != pLinea.getPoints().size() - 1) {
                        LatLng pA = pLinea.getPoints().get(j);
                        LatLng pB = pLinea.getPoints().get(j + 1);

                        if (!pFinal.equals(pA) && !pFinal.equals(pB) && !pInicial.equals(pA) && !pInicial.equals(pB)) {
                            LatLng interseccion = interseccion(pInicial, pFinal, pA, pB);
                            Log.d("interseccion", (pInicial + "|" + pFinal + "|" + pA + "|" + pB));
                            Log.d("interseccion", String.valueOf(interseccion));
                            if (!interseccion.equals(new LatLng(0.0, 0.0))) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static LatLng interseccion(LatLng a1, LatLng a2, LatLng b1, LatLng b2) {
        double a1x = a1.latitude;
        double a1y = a1.longitude;
        double a2x = a2.latitude;
        double a2y = a2.longitude;
        double b1x = b1.latitude;
        double b1y = b1.longitude;
        double b2x = b2.latitude;
        double b2y = b2.longitude;

        double a21x = a2x - a1x;
        double a21y = a2y - a1y;
        double b21x = b2x - b1x;
        double b21y = b2y - b1y;

        double denom = a21x * b21y - b21x * a21y;

        if (denom == 0) {
            return new LatLng(0, 0);
        }

        boolean denom_positive = denom > 0;

        double ab12x = a1x - b1x;
        double ab12y = a1y - b1y;

        double s_numer = a21x * ab12y - a21y * ab12x;

        if ((s_numer < 0) == denom_positive) {
            return new LatLng(0, 0);
        }

        double t_numer = b21x * ab12y - b21y * ab12x;

        if ((t_numer < 0) == denom_positive) {
            return new LatLng(0, 0);
        }

        if ((s_numer > denom) == denom_positive || (t_numer > denom) == denom_positive) {
            return new LatLng(0, 0);
        }

        double t = t_numer / denom;

        double x = a1x + (t * a21x);
        double y = a1y + (t * a21y);

        LatLng p = new LatLng(x, y);
        return p;
    }

//    public static Bitmap textAsBitmap(String text, float textSize, int textColor) {
//
//        Paint paint = new Paint();
//        paint.setTextSize(textSize);
//        paint.setColor(textColor);
//        paint.setStrokeWidth(5);
//        paint.setShadowLayer(5, 0, 0, 0xff000000);
//        paint.setTextAlign(Paint.Align.LEFT);
//
//        int width = (int) (paint.measureText(text) + 0.5f); // round
//        float baseline = (int) (-paint.ascent() + 0.5f); // ascent() is negative
//        int height = (int) (baseline + paint.descent() + 0.5f);
//
//        Bitmap image = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(image);
//        canvas.drawText(text, 0, baseline, paint);
//
//        Drawable drawable2 = new BitmapDrawable(MyApplication.getContext().getResources(), image);
////        Drawable drawable1 = getResources().getDrawable(R.drawable.bg_default);
//        Drawable drawable1 = MyApplication.getContext().getResources().getDrawable(R.drawable.bg_marker);
//
//        Drawable[] layers = new Drawable[2];
//        layers[0] = drawable1;
//        layers[1] = drawable2;
//
//        LayerDrawable layerDrawable = new LayerDrawable(layers);
//        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
//
//        Bitmap bitmap = Bitmap.createBitmap((width + 5), (height + 5),
//                Bitmap.Config.ARGB_8888);
//
//        Canvas can = new Canvas(bitmap);
//        layerDrawable.setBounds(0, 0, width, height);
//        layerDrawable.draw(can);
//
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(MyApplication.getContext().getResources(),
//                bitmap);
//        bitmapDrawable.setBounds(0, 0, (width + 5), (height + 5));
//
//        return bitmapDrawable.getBitmap().copy(
//                Bitmap.Config.ARGB_8888, true);
//    }
//
//    public static Bitmap textAsBitmapManzana(String text, float textSize, int textColor, int tipo) {
//        Drawable drawable1;
//        drawable1 = MyApplication.getContext().getResources().getDrawable(R.drawable.bg_manzana);
//
//        Paint paint = new Paint();
//        paint.setTextSize(textSize);
//        paint.setColor(textColor);
//        paint.setShadowLayer(10, 0, 0, 0xffffffff);
//        paint.setTextAlign(Paint.Align.LEFT);
//
//        int width = (int) (paint.measureText(text) + 5); // round
//        float baseline = (int) (-paint.ascent() + 125); // ascent() is negative
//        int height = (int) (baseline + paint.descent());
//
//        Bitmap image = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(image);
//        canvas.drawText(text, 0, baseline, paint);
//
//        Drawable drawable2 = new BitmapDrawable(MyApplication.getContext().getResources(), image);
////        Drawable drawable1 = getResources().getDrawable(R.drawable.bg_default);
//
//        Drawable[] layers = new Drawable[2];
//        layers[0] = drawable1;
//        layers[1] = drawable2;
//
//        LayerDrawable layerDrawable = new LayerDrawable(layers);
//        layerDrawable.setLayerInset(1, 0, 0, 0, 70);
//        Bitmap bitmap;
//
////        if(zoom>18){
////            Log.d("BITMAP", String.valueOf(b));
////             bitmap = Bitmap.createBitmap((width + 50), (height + 50),
////                    Bitmap.Config.ARGB_8888);
////        } else{
//        bitmap = Bitmap.createBitmap((width), (height),
//                Bitmap.Config.ARGB_8888);
////        }
//
//
//        Canvas can = new Canvas(bitmap);
//        layerDrawable.setBounds(0, 0, width, height);
//        layerDrawable.draw(can);
//
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(MyApplication.getContext().getResources(),
//                bitmap);
//        bitmapDrawable.setBounds(0, 0, 25, 25);
//
//        return bitmapDrawable.getBitmap().copy(
//                Bitmap.Config.ARGB_8888, true);
//    }
//
//    public static Bitmap textAsBitmap0(String text, float textSize, int textColor, int tipo) {
//        Drawable drawable1;
//        switch (tipo) {
//            case 1:
//                drawable1 = MyApplication.getContext().getResources().getDrawable(R.drawable.bg_sector);
//                break;
//            case 2:
//                drawable1 = MyApplication.getContext().getResources().getDrawable(R.drawable.bg_segmento);
//                break;
//            default:
//                drawable1 = MyApplication.getContext().getResources().getDrawable(R.drawable.bg_default);
//                break;
//        }
//        Paint paint = new Paint();
//        paint.setTextSize(textSize);
//        paint.setColor(textColor);
//        paint.setShadowLayer(10, 0, 0, 0xffffffff);
//        paint.setTextAlign(Paint.Align.LEFT);
//
//        int width = (int) (paint.measureText(text) + 5); // round
//        float baseline = (int) (-paint.ascent() + 125); // ascent() is negative
//        int height = (int) (baseline + paint.descent());
//
//        Bitmap image = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(image);
//        canvas.drawText(text, 0, baseline, paint);
//
//        Drawable drawable2 = new BitmapDrawable(MyApplication.getContext().getResources(), image);
////        Drawable drawable1 = getResources().getDrawable(R.drawable.bg_default);
//
//        Drawable[] layers = new Drawable[2];
//        layers[0] = drawable1;
//        layers[1] = drawable2;
//
//        LayerDrawable layerDrawable = new LayerDrawable(layers);
//        layerDrawable.setLayerInset(1, 0, 0, 0, 70);
//        Bitmap bitmap;
//
////        if(zoom>18){
////            Log.d("BITMAP", String.valueOf(b));
////             bitmap = Bitmap.createBitmap((width + 50), (height + 50),
////                    Bitmap.Config.ARGB_8888);
////        } else{
//        bitmap = Bitmap.createBitmap((width), (height),
//                Bitmap.Config.ARGB_8888);
////        }
//
//
//        Canvas can = new Canvas(bitmap);
//        layerDrawable.setBounds(0, 0, width, height);
//        layerDrawable.draw(can);
//
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(MyApplication.getContext().getResources(),
//                bitmap);
//        bitmapDrawable.setBounds(0, 0, 25, 25);
//
//        return bitmapDrawable.getBitmap().copy(
//                Bitmap.Config.ARGB_8888, true);
//    }
//
//    public Bitmap textAsBitmap2(String text, float textSize, int textColor) {
//
//        Paint paint = new Paint();
//        paint.setTextSize(textSize);
//        paint.setColor(textColor);
//        paint.setShadowLayer(10, 0, 0, 0xffffffff);
//        paint.setTextAlign(Paint.Align.LEFT);
//
//        int width = (int) (paint.measureText(text) + 5); // round
//        float baseline = (int) (-paint.ascent() + 125); // ascent() is negative
//        int height = (int) (baseline + paint.descent());
//
//        Bitmap image = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(image);
//        canvas.drawText(text, 0, baseline, paint);
//
//        Drawable drawable2 = new BitmapDrawable(MyApplication.getContext().getResources(), image);
//        Drawable drawable1 = MyApplication.getContext().getResources().getDrawable(R.drawable.bg_default4);
//
//        Drawable[] layers = new Drawable[2];
//        layers[0] = drawable1;
//        layers[1] = drawable2;
//
//        LayerDrawable layerDrawable = new LayerDrawable(layers);
//        layerDrawable.setLayerInset(1, 0, 0, 0, 70);
//        Bitmap bitmap;
//
////        if(zoom>18){
////            Log.d("BITMAP", String.valueOf(b));
////             bitmap = Bitmap.createBitmap((width + 50), (height + 50),
////                    Bitmap.Config.ARGB_8888);
////        } else{
//        bitmap = Bitmap.createBitmap((width), (height),
//                Bitmap.Config.ARGB_8888);
////        }
//
//
//        Canvas can = new Canvas(bitmap);
//        layerDrawable.setBounds(0, 0, width, height);
//        layerDrawable.draw(can);
//
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(MyApplication.getContext().getResources(),
//                bitmap);
//        bitmapDrawable.setBounds(0, 0, 25, 25);
//
//        return bitmapDrawable.getBitmap().copy(
//                Bitmap.Config.ARGB_8888, true);
//    }
//
//    public Bitmap textAsBitmap3(String text, float textSize, int textColor) {
//
//        text = "010";
//
//        Paint paint = new Paint();
//        paint.setTextSize(textSize);
//        paint.setColor(textColor);
//        paint.setShadowLayer(10, 0, 0, 0xffffffff);
//        paint.setTextAlign(Paint.Align.LEFT);
//
//        int width = (int) (paint.measureText(text) + 5); // round
//        float baseline = (int) (-paint.ascent() + 120); // ascent() is negative
//        int height = (int) (baseline + paint.descent());
//
//        Bitmap image = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(image);
//        canvas.drawText(text, 0, baseline, paint);
//
//        Drawable drawable2 = new BitmapDrawable(MyApplication.getContext().getResources(), image);
//        Drawable drawable1 = MyApplication.getContext().getResources().getDrawable(R.drawable.bg_default3);
//
//        Drawable[] layers = new Drawable[2];
//        layers[0] = drawable1;
//        layers[1] = drawable2;
//
//        LayerDrawable layerDrawable = new LayerDrawable(layers);
//        layerDrawable.setLayerInset(1, 0, 0, 0, 70);
//        Bitmap bitmap;
//
////        if(zoom>18){
////            Log.d("BITMAP", String.valueOf(b));
////             bitmap = Bitmap.createBitmap((width + 50), (height + 50),
////                    Bitmap.Config.ARGB_8888);
////        } else{
//        bitmap = Bitmap.createBitmap((width), (height),
//                Bitmap.Config.ARGB_8888);
////        }
//
//
//        Canvas can = new Canvas(bitmap);
//        layerDrawable.setBounds(0, 0, width, height);
//        layerDrawable.draw(can);
//
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(MyApplication.getContext().getResources(),
//                bitmap);
//        bitmapDrawable.setBounds(0, 0, 25, 25);
//
//        return bitmapDrawable.getBitmap().copy(
//                Bitmap.Config.ARGB_8888, true);
//    }
//
//    public Bitmap textAsBitmap4(String text, float textSize, int textColor) {
//
//        text = "010";
//
//        Paint paint = new Paint();
//        paint.setTextSize(textSize);
//        paint.setColor(textColor);
//        paint.setShadowLayer(10, 0, 0, 0xffffffff);
//        paint.setTextAlign(Paint.Align.LEFT);
//
//        int width = (int) (paint.measureText(text) + 5); // round
//        float baseline = (int) (-paint.ascent() + 120); // ascent() is negative
//        int height = (int) (baseline + paint.descent());
//
//        Bitmap image = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(image);
//        canvas.drawText(text, 0, baseline, paint);
//
//        Drawable drawable2 = new BitmapDrawable(MyApplication.getContext().getResources(), image);
//        Drawable drawable1 = MyApplication.getContext().getResources().getDrawable(R.drawable.bg_default2);
//
//        Drawable[] layers = new Drawable[2];
//        layers[0] = drawable1;
//        layers[1] = drawable2;
//
//        LayerDrawable layerDrawable = new LayerDrawable(layers);
//        layerDrawable.setLayerInset(1, 0, 0, 0, 70);
//        Bitmap bitmap;
//
////        if(zoom>18){
////            Log.d("BITMAP", String.valueOf(b));
////             bitmap = Bitmap.createBitmap((width + 50), (height + 50),
////                    Bitmap.Config.ARGB_8888);
////        } else{
//        bitmap = Bitmap.createBitmap((width), (height),
//                Bitmap.Config.ARGB_8888);
////        }
//
//
//        Canvas can = new Canvas(bitmap);
//        layerDrawable.setBounds(0, 0, width, height);
//        layerDrawable.draw(can);
//
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(MyApplication.getContext().getResources(),
//                bitmap);
//        bitmapDrawable.setBounds(0, 0, 25, 25);
//
//        return bitmapDrawable.getBitmap().copy(
//                Bitmap.Config.ARGB_8888, true);
//    }

//    public Bitmap textAsBitmap5(String text, float textSize, int textColor) {
//
//        Paint paint = new Paint();
//        paint.setTextSize(textSize);
//        paint.setColor(textColor);
//        paint.setShadowLayer(10, 0, 0, 0xffffffff);
//        paint.setTextAlign(Paint.Align.LEFT);
//
//        int width = (int) (paint.measureText(text) + 5); // round
//        float baseline = (int) (-paint.ascent() + 120); // ascent() is negative
//        int height = (int) (baseline + paint.descent());
//
//        Bitmap image = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(image);
//        canvas.drawText(text, 0, baseline, paint);
//
//        Drawable drawable2 = new BitmapDrawable(MyApplication.getContext().getResources(), image);
//        Drawable drawable1 = MyApplication.getContext().getResources().getDrawable(R.drawable.marcador_concluido);
//
//        Drawable[] layers = new Drawable[2];
//        layers[0] = drawable1;
//        layers[1] = drawable2;
//
//        LayerDrawable layerDrawable = new LayerDrawable(layers);
//        layerDrawable.setLayerInset(1, 0, 0, 0, 70);
//        Bitmap bitmap;
//
//        bitmap = Bitmap.createBitmap((width), (height),
//                Bitmap.Config.ARGB_8888);
//
//
//        Canvas can = new Canvas(bitmap);
//        layerDrawable.setBounds(0, 0, width, height);
//        layerDrawable.draw(can);
//
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(MyApplication.getContext().getResources(),
//                bitmap);
//        bitmapDrawable.setBounds(0, 0, 25, 25);
//
//        return bitmapDrawable.getBitmap().copy(
//                Bitmap.Config.ARGB_8888, true);
//    }

//    public static Bitmap textAsBitmap6(String text, float textSize, int textColor) {
//
//        Paint paint = new Paint();
//        paint.setTextSize(textSize);
//        paint.setColor(textColor);
//        paint.setShadowLayer(10, 0, 0, 0xffffffff);
//        paint.setTextAlign(Paint.Align.LEFT);
//
//        int width = (int) (paint.measureText(text) + 5); // round
//        float baseline = (int) (-paint.ascent() + 120); // ascent() is negative
//        int height = (int) (baseline + paint.descent());
//
//        Bitmap image = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//
//        Canvas canvas = new Canvas(image);
//        canvas.drawText(text, 0, baseline, paint);
//
//        Drawable drawable2 = new BitmapDrawable(MyApplication.getContext().getResources(), image);
//        Drawable drawable1 = MyApplication.getContext().getResources().getDrawable(R.drawable.bg_default5);
//
//        Drawable[] layers = new Drawable[2];
//        layers[0] = drawable1;
//        layers[1] = drawable2;
//
//        LayerDrawable layerDrawable = new LayerDrawable(layers);
//        layerDrawable.setLayerInset(1, 0, 0, 0, 70);
//        Bitmap bitmap;
//
//        bitmap = Bitmap.createBitmap((width), (height),
//                Bitmap.Config.ARGB_8888);
//
//
//        Canvas can = new Canvas(bitmap);
//        layerDrawable.setBounds(0, 0, width, height);
//        layerDrawable.draw(can);
//
//        BitmapDrawable bitmapDrawable = new BitmapDrawable(MyApplication.getContext().getResources(),
//                bitmap);
//        bitmapDrawable.setBounds(0, 0, 25, 25);
//
//        return bitmapDrawable.getBitmap().copy(
//                Bitmap.Config.ARGB_8888, true);
//    }

//    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int icono, String texto) {
//        Drawable background = ContextCompat.getDrawable(context, icono);
////        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
//        background.setBounds(0, 0, 50, 50);
//        IconGenerator factory = new IconGenerator(context);
//        factory.setBackground(null);
//        factory.setTextAppearance(R.style.textGenerator1);
////        factory.setBackground(getResources().getDrawable(R.drawable.circle));
////        factory.setBackground(R.color.button_text_color);
////        factory.setContentPadding(15,15,15,15);
//        Bitmap icon = factory.makeIcon(texto);
////        Bitmap icon = factory.makeIcon(texto);
//        Drawable d = new BitmapDrawable(MyApplication.getContext().getResources(), icon);
////        Drawable vectorDrawable = ContextCompat.getDrawable(context, d);
////        d.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
////        d.setBounds(-70, -10, d.getIntrinsicWidth() + 90, d.getIntrinsicHeight() + 10);
//        d.setBounds(-10, -10, d.getIntrinsicWidth(), d.getIntrinsicHeight()-10);
//        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        background.draw(canvas);
//        d.draw(canvas);
//        return BitmapDescriptorFactory.fromBitmap(bitmap);
//    }

    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels,
                displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void snapShot(GoogleMap mMap) {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            Bitmap bitmap;

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                bitmap = snapshot;
                try {
                    FileOutputStream out = new FileOutputStream(Parametros.DIR_RAIZ + "map.png");
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        mMap.snapshot(callback);
    }
}
