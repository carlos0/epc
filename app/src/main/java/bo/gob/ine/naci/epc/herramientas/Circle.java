package bo.gob.ine.naci.epc.herramientas;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

public class Circle extends Drawable {
    //Declaramos una variable tipo Paint para nuestro circulo
    private Paint circlePaint;
    //Declaramos una variable para el texto que irá dentro del círculo
    private Paint textPaint;
    //Declaramos una variable tipo Rect para manejar el tamaño del círculo
    private Rect rect = new Rect();
    //Declaramos una variable para guardar el número de notificaciones
    private String count;

    //Creamos una bandera saber si debemos de pintar o no el círculo
    private boolean draw = false;

    public Circle(){
//Le agregamos las características a nuestro círculo
        circlePaint=new Paint();
        circlePaint.setColor(Color.RED);
//Con este atributo vamos a evitar que se pixelado nuestro círculo
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);

        textPaint=new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(25);
//Con este atributo vamos a evitar que se pixelado nuestro círculo
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void draw(Canvas canvas) {

//Validamos si debemos de pintar o no
        if(!draw)
            return;
//Con getBounds obtendremos datos del lienzo
        Rect bounds=getBounds();
        /*Para poder entender cómo funciona esta parte es necesario que veas el siguiente articulo  https://codigofacilito.com/articulos/articulo_16_10_2019_16_38_48 */
        float width=bounds.right-bounds.left;
        float radius=width/3;
        float x = bounds.right;
        float y = bounds.top+9;

//Dibujamos el círculo en el lienzo
        canvas.drawCircle(x,y,radius,circlePaint);

        if(Integer.parseInt(count) > 99) {
            textPaint.getTextBounds("+99",0,3,rect);
            float height = rect.bottom-rect.top;
            float textY=y + (height/2);
//Dibujamos el texto en el círculo
            canvas.drawText("+99",x,textY,textPaint);
        } else {
            textPaint.getTextBounds(count,0,count.length(),rect);
            float height = rect.bottom-rect.top;
            float textY=y + (height/2);
//Dibujamos el texto en el círculo
            canvas.drawText(count,x,textY,textPaint);
        }

    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
//Colocamos en opacidad desconocida
        return PixelFormat.UNKNOWN;
    }

    /*Este método nos servirá para poder agregar y modificar el número de la notificación */

    public void setCount(String count){
        this.count=count;
        /*Convertimos a entero y verificamos si hay alguna notificación si no hay entonces no dibujamos nada */
        draw = Integer.parseInt(count)>0;
    }
}
