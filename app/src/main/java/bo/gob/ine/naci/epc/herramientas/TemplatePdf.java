package bo.gob.ine.naci.epc.herramientas;

import android.text.Html;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class TemplatePdf {

    private Context context;
    private int idAsignacion;
    private int correlativo;
    private File pdfFile;
    private PdfDocument document;

    int width = 612;
    int height = 792;
    int pagina = 1;

    int marginLeft = 40;
    int marginTop = 40;
    int marginRight = 30;
    int marginBottom = 20;


    private PdfDocument.PageInfo pageInfo;
    private PdfDocument.Page page;
    private Canvas canvas;
    private int pos = 0;

    private Paint paint = new Paint();

    private int currentPages ;
    public TemplatePdf(Context context, int idAsignacion, int correlativo) {
        this.context = context;
        this.idAsignacion = idAsignacion;
        this.correlativo = correlativo;
        currentPages =1 ;
    }

    public void openDocument() {
        createPDF();
        try {

            document = new PdfDocument();
            pageInfo = new PdfDocument.PageInfo.Builder(width, height, pagina).create();
            page = document.startPage(pageInfo);
            canvas = page.getCanvas();

        } catch (Exception e) {
            Log.e("abriendo documento pdf", e.toString());
        }
    }

    private void createPDF() {
        File folder = new File(Parametros.DIR_PDF);
        if (!folder.exists())
            folder.mkdirs();
        pdfFile = new File(folder, "INE" + "_" + idAsignacion + "_" + correlativo + ".pdf");

        try {
            pdfFile.createNewFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeDocument() {
        drawPageNumber();
        document.finishPage(page);
        try {
            document.writeTo(new FileOutputStream(pdfFile));

        } catch (IOException e) {
            e.printStackTrace();
        }

        document.close();
    }

    public void addTitle(String title, String subTitle, Date date) {
        try {
            addChildP(title, 15.3f);
            addChildP(subTitle, 15.3f);
            addChildP("Generado: " + date, 15.3f);
            addChildP(" ", 15.3f);
        } catch (Exception e) {
            Log.e("openDocument", e.toString());
        }
    }

    private void addChildP(String title, float size) {
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
        paint.setTextSize(size);
        pos = pos + 30;
        canvas.drawText(title, pageInfo.getPageWidth() / 2, pos, paint);
    }

    public void addParagraph(String text) {
        try {
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
            paint.setTextSize(15.3f);
            canvas.drawText(text, marginLeft+10, pos, paint);
            pos = pos + 10;
        } catch (Exception e) {
            Log.e("addText", e.toString());
        }
    }


    private void drawPageNumber() {
        String pageNumberString = "Pagina " + currentPages;
        Paint textPaint = new Paint();
        textPaint.setTextSize(7);
        float textWidth = textPaint.measureText(pageNumberString);
        float textX = (width - textWidth) / 2;
        float textY = height -15;  // El valor 0 ajusta el numero de la pagina
        canvas.drawText(pageNumberString, textX, textY, textPaint);
    }

    /********/

    public void createTable(String[] header, ArrayList<String[]> clients) {

        final int pageHeight = height - (marginTop+marginBottom) ;
        int numRows = clients.size();
        int numColumns = header.length;
        int cellPadding = 10;

        int cyanColor = Color.CYAN;
        int blackColor = Color.BLACK;
        int whiteColor = Color.WHITE;

        int columnWidth = (width -(marginLeft+marginRight)- cellPadding * 2) / numColumns;

        Paint textPaint = new Paint();
        textPaint.setColor(blackColor);
        textPaint.setTextSize(7);

        Paint cellBackgroundPaint = new Paint();
        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(blackColor);
        borderPaint.setStrokeWidth(1f);

        if (pos < (height  - (marginBottom))) {


            int newBottomT = drawRow(header, cyanColor, columnWidth, cellPadding, textPaint, cellBackgroundPaint, borderPaint, true);
            pos = newBottomT;
            for (int row = 0; row < numRows; row++) {
                String[] datos = clients.get(row);
                int maxCellHeight = getMaxCellHeight(datos, textPaint, columnWidth, cellPadding);
                if (pos + maxCellHeight > pageHeight - 20) {
                    // Add the page  when the page  is full
                    drawPageNumber();
                    creanewpdf();

                    pos = marginTop;

                }
                pos = drawRow(datos, whiteColor, columnWidth, cellPadding, textPaint, cellBackgroundPaint, borderPaint, row == numRows - 1);
            }

            pos = pos + 20;
        } else {
            creanewpdf();
        }

    }

    private int getMaxCellHeight(String[] data, Paint textPaint, int columnWidth, int cellPadding) {
        int maxCellHeight = 0;
        int numColumns = data.length;
        for (int column = 0; column < numColumns; column++) {
            String cellText = data[column];
            int textHeight = getTextHeight(cellText, textPaint, columnWidth);
            int cellHeight = textHeight + 2 * cellPadding;
            maxCellHeight = Math.max(maxCellHeight, cellHeight);
        }
        return maxCellHeight;
    }

    private int drawRow(String[] data, int rowColor, int columnWidth, int cellPadding, Paint textPaint, Paint cellBackgroundPaint, Paint borderPaint, boolean isLastRow) {
        int numColumns = data.length;
        int maxCellHeight = 0;

        // 1. Calcular la altura necesaria para cada celda.
        for (int column = 0; column < numColumns; column++) {
            String cellText = data[column];
            int textHeight = getTextHeight(cellText, textPaint, columnWidth);
            int cellHeight = textHeight + 2 * cellPadding;
            maxCellHeight = Math.max(maxCellHeight, cellHeight);
        }

        // 2. Dibujar cada celda con la altura máxima.
        int newBottom = 0;
        for (int column = 0; column < numColumns; column++) {
            int left = cellPadding + column * columnWidth + marginLeft;
            int top = pos;
            int right = left + columnWidth;
            int bottom = top + maxCellHeight;


            cellBackgroundPaint.setColor(rowColor);
            canvas.drawRect(left, top, right, bottom, cellBackgroundPaint);


            String cellText = data[column];
            String cellText2 =  cellText;
            try{
                cellText = removeSpaces(cellText);
                CharSequence processedText = Html.fromHtml(cellText);
                cellText = processedText.toString();
            }catch (Exception e )
            {
                cellText = cellText2;
            }



            TextPaint textPaintForDrawing = new TextPaint(textPaint);
            StaticLayout staticLayout = new StaticLayout(cellText, textPaintForDrawing, columnWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);

            canvas.save();
            canvas.translate(left, top + (maxCellHeight - staticLayout.getHeight()) / 2); // Centramos verticalmente el texto.
            staticLayout.draw(canvas);
            canvas.restore();


            drawCellBorder(left, top, right, bottom, borderPaint, isLastRow);

            newBottom = bottom;
        }
        return newBottom;
    }
    private int getTextHeight(String text, Paint textPaint, int maxWidth) {
        TextPaint textPaintForMeasuring = new TextPaint(textPaint);
        StaticLayout staticLayout = new StaticLayout(text, textPaintForMeasuring, maxWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        return staticLayout.getHeight();
    }

    private void drawCellBorder(int left, int top, int right, int bottom, Paint borderPaint, boolean isLastRow) {
        // Borde de arriba
        canvas.drawLine(left, top, right, top, borderPaint);
        // Borde izquierda
        canvas.drawLine(left, top, left, bottom, borderPaint);
        // Borde Derecha
        canvas.drawLine(right, top, right, bottom, borderPaint);
        // Borde Inferior
        canvas.drawLine(left, bottom, right, bottom, borderPaint);
        /*
        if(isLastRow){
              // Borde Inferior
              canvas.drawLine(left, bottom, right, bottom, borderPaint);
         }*/
        /**
         * Para ahorrar recursos en el procesamiento del reporte puede incluir este IF,
         * por el momento se deja como comentario.
         *
         *  if(isLastRow){
         *      // Borde Inferior
         *      canvas.drawLine(left, bottom, right, bottom, borderPaint);
         *  }
         * **/
    }



    /********/

    public void creanewpdf() {
        document.finishPage(page);
        pagina++;
        // Crear una nueva página
        pageInfo = new PdfDocument.PageInfo.Builder(width, height, pagina).create();
        page = document.startPage(pageInfo);
        canvas = page.getCanvas();
        pos = 10;
        currentPages++;
    }

    public void viewPDF() {
        // Crear un objeto Uri con la ruta del archivo PDF
        Uri uri = FileProvider.getUriForFile(context, "bo.gob.ine.naci.epc.provider", pdfFile);

        // Crear un intent con la acción ACTION_VIEW
        Intent intent = new Intent(Intent.ACTION_VIEW);

        // Establecer el tipo de datos del intent como "application/pdf"
        intent.setDataAndType(uri, "application/pdf");

        // Añadir flags para otorgar permisos de lectura y para evitar reiniciar la actividad
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

        // Verificar si hay aplicaciones en el dispositivo capaces de abrir archivos PDF
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Abrir el archivo PDF con la aplicación del dispositivo

            context.startActivity(intent);

        } else {
            Toast.makeText(context.getApplicationContext(), "No cuentas con una aplicacion para visualizar pdf", Toast.LENGTH_LONG).show();
        }
    }
    /*
     * TextUtils
     *
     * */

    public static String removeSpaces(String texto) {
        String res ="";
        boolean sw = false;
        for (int i = 0; i < texto.length(); i++) {
            char car = texto.charAt(i);
            if (car == '<') {
                sw = true;
            } else if (car == '>') {
                sw = false;
            }
            if (sw) {
                if (car == ' ') {
                    continue;
                } else {
                    res = res +car;
                }
            }else{
                res = res +car;
            }
        }
        return res;
    }

}