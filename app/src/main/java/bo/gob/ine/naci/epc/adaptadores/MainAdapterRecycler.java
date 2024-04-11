package bo.gob.ine.naci.epc.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
//import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.util.ArrayList;
import java.util.Map;

import bo.gob.ine.naci.epc.ListadoViviendasActivity;
import bo.gob.ine.naci.epc.MainActivity;
import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.ObservacionActivity;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.RolPermiso;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.Circle;
import bo.gob.ine.naci.epc.herramientas.IntegerFormatter;
import bo.gob.ine.naci.epc.herramientas.Parametros;

import static bo.gob.ine.naci.epc.MyApplication.getContext;

public class MainAdapterRecycler extends RecyclerView.Adapter<MainAdapterRecycler.ViewHolder>  implements View.OnClickListener{

    private static final String TAG = "RecyclerAdapter";
    private ArrayList<Map<String, Object>> dataView;
    private Activity activity = null;
    private View.OnClickListener listener;
    private ListPopupWindow listPopupWindow;
    private int lastPosition = -1;
    private Animation animation;

    public MainAdapterRecycler (Activity activity, ArrayList<Map<String, Object>> dataView) {
        this.dataView = dataView;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.adapter_list_main, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Map<String, Object> objView = dataView.get(position);
//        vi.setId((int) objView.get("id_upm"));

        holder.main_map_view.setVisibility(View.VISIBLE);
        if (RolPermiso.tienePermiso(Usuario.getRol(), "activity_googlemap")) {
            holder.main_map_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        //((MainActivity) activity).irMap((objView.get("latitud")==null? -1d:(double)objView.get("latitud")), (objView.get("longitud")==null? -1d:(double)objView.get("longitud")));
//                        ((MainActivity) activity).irMap((Integer) objView.get("id_upm"));
                        holder.fabMenuMain2.clearChecked();
                        //TODO:BRP{
                        ((MainActivity) activity).irMap2((Integer) objView.get("id_upm"));
                        //TODO:BRP}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        holder.main_codigo.setTextSize(Parametros.FONT_LIST_BIG);
        holder.main_codigo.setText((String) objView.get("codigo"));

        holder.main_codigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilderAdiRem=new StringBuilder();
                if(!String.valueOf(objView.get("qUpmsRemplazo")).equals("")){
                    stringBuilderAdiRem.append("REEMPLAZADO: "+String.valueOf(objView.get("qUpmsRemplazo"))+"<br>");
                }
                if(!String.valueOf(objView.get("qUpmsAdicionales")).equals("")){
                    stringBuilderAdiRem.append("ADICIONALES: "+String.valueOf(objView.get("qUpmsAdicionales").toString().replace(" ","<br>")));
                }
                ((MainActivity) activity).informationMessage(activity, null, "ADICIONALES/REEMPLAZOS", Html.fromHtml(stringBuilderAdiRem.toString()), Parametros.FONT_OBS);
            }
        });
        String nombre = (String)objView.get("nombre");
        String vector[] = nombre.split("-");


        String ciudad = "-";
        String municipio= "-";
        String zona= "-";
//        String departamento = vector[0];
        try{

            ciudad = vector[0];
            municipio= vector[1];
            zona= vector[2];
        }catch (Exception e){

        }
        String nombre_upm = "<font color="+MyApplication.getContext().getResources().getColor(R.color.colorPrimaryDark)+">DEPARTAMENTO:</font>"+ciudad+ "<br>" +
                "<font color="+MyApplication.getContext().getResources().getColor(R.color.colorPrimaryDark)+">PROVINCIA:</font>"+municipio +"<br>"+
                        "<font color="+MyApplication.getContext().getResources().getColor(R.color.colorPrimaryDark)+">MUNICIPIO:</font>"+zona;
        holder.main_descripcion.setText(Html.fromHtml(nombre_upm));

        if(Integer.parseInt(objView.get("qBoletasElaboradas").toString())>0){
            holder.main_mensaje.setVisibility(View.VISIBLE);
            holder.main_mensaje.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_anulado));
            holder.main_mensaje.setText("TIENE BOLETAS PENDIENTES");
        } else {
            holder.main_mensaje.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_concluido));
            holder.main_mensaje.setText("NO TIENE BOLETAS PENDIENTES");
        }
        holder.txtLvTitle.setTextSize(Parametros.FONT_LIST_SMALL);
        holder.txtTotal.setTextSize(Parametros.FONT_LIST_SMALL_SMALL);
        holder.txtViviendas.setTextSize(Parametros.FONT_LIST_SMALL_SMALL);
        if (RolPermiso.tienePermiso(Usuario.getRol(), "activity_listado_viviendas")) {
            holder.txtViviendasElaboradas.setVisibility(View.VISIBLE);
            holder.txtViviendasSeleccionadas.setVisibility(View.VISIBLE);
            holder.txtTotal.setText("LV");
            String datosLvTotal = "<font color="+MyApplication.getContext().getResources().getColor(R.color.colorPrimaryDark)+">TOTAL REGISTROS:</font>"+objView.get("qViviendasElaboradas");
            String datosLvSeleccionadas=   "<font color="+MyApplication.getContext().getResources().getColor(R.color.colorPrimaryDark)+">VIV. SELECCIONADAS:</font>"+objView.get("qViviendasSeleccionadas") ;
            holder.txtLvTitle.setText("LV");
            holder.txtTotal.setText(Html.fromHtml(datosLvTotal));
            holder.txtViviendas.setText(Html.fromHtml(datosLvSeleccionadas));
        } else {
            holder.txtViviendasElaboradas.setVisibility(View.GONE);
            holder.txtViviendasSeleccionadas.setVisibility(View.GONE);
            holder.txtLvTitle.setVisibility(View.GONE);
            holder.txtTotal.setVisibility(View.GONE);
            holder.txtViviendas.setVisibility(View.GONE);
        }
        String listAdicionales[]=(objView.get("qUpmsAdicionales").toString()).split(" ");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>((Context) this.activity, android.R.layout.simple_spinner_item,listAdicionales);
        holder.listViewViviendasAdicion.setAdapter(adapter);
        setListViewHeightBasedOnChildren(holder.listViewViviendasAdicion);
        holder.listViewViviendasAdicion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Bundle bundle = new Bundle();
//                bundle.putInt("IdUpm", (Integer)  objView.get("id_upm"));
//                bundle.putInt("idUpmHijo", UpmHijo.getIdUpmHijo(listAdicionales[position]));
//
//                Intent informante = new Intent(getContext(), BoletaActivity.class);
//                informante.putExtras(bundle);
//                activity.startActivity(informante);
            }
        });
//        holder.listViewViviendasAdicion.setOnItemClickListener()
        holder.txtViviendasRemplazo.setText("REEMPLAZO: "+objView.get("qUpmsRemplazo"));
        holder.txtBoletasElaboradas.setText("ELABORADAS: "+objView.get("c"));
        holder.txtBoletasConcluidas.setText("CONCLUIDAS: "+objView.get("qBoletasConcluidas"));
//        String[] products={"Camera", "Laptop", "Watch","Smartphone",
//                "Television"};
//        listPopupWindow = new ListPopupWindow(activity.getApplicationContext());
//        listPopupWindow.setAdapter(new ArrayAdapter(activity.getApplicationContext(),android.R.layout.simple_expandable_list_item_1, products));
//        listPopupWindow.setAnchorView(holder.info_boleta);
//        listPopupWindow.setWidth(300);
//        listPopupWindow.setHeight(400);
//        listPopupWindow.setModal(true);
//        boolean open = false;
        if(objView.get("estado_lv").toString().equals("1")){
            holder.main_lv_view.setTextColor(MyApplication.getContext().getResources().getColor(R.color.color_concluido));
            holder.main_lv_view.setIconTintResource(R.color.color_concluido);
        }else if(objView.get("estado_lv").toString().equals("2")){
            holder.main_lv_view.setTextColor(MyApplication.getContext().getResources().getColor(R.color.colorAccent));
            holder.main_lv_view.setIconTintResource(R.color.colorAccent);
        }else {
//            holder.main_lv_view.setBackground();
            holder.main_lv_view.setTextColor(MyApplication.getContext().getResources().getColor(R.color.colorPrimary));
            holder.main_lv_view.setIconTintResource(R.color.colorPrimary);
        }
        holder.info_boleta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(open){
//                    listPopupWindow.show();

//                if(holder.main_info_view.getVisibility()== View.VISIBLE){
//                    holder.main_info_view.setVisibility(View.GONE);
//                }else{
//                    holder.main_info_view.setVisibility(View.VISIBLE);
//                }
            }
        });
        if(objView.get("qBoletasObservadas").toString().equals("0")){
//            holder.info_observacion.setText("0");
            holder.info_observacion.setVisibility(View.VISIBLE);

        }else{
            holder.info_observacion.setText(String.valueOf(objView.get("qBoletasObservadas")) );
            holder.info_observacion.setVisibility(View.VISIBLE);
            Circle circle = new Circle();
            circle.setCount(objView.get("qBoletasObservadas").toString());
            Drawable[] a = {activity.getApplication().getResources().getDrawable(R.drawable.ic_notifications), circle};
            LayerDrawable image = new LayerDrawable(a);
            holder.info_observacion.setIcon(image);
        }

        if(Usuario.getRol() == Parametros.SUPERVISOR) {
            holder.main_lv_view.setVisibility(View.VISIBLE);
            holder.main_lv_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("IdUpm", (Integer) objView.get("id_upm"));
                    Intent listado = new Intent(activity.getApplicationContext(), ListadoViviendasActivity.class);
                    listado.putExtras(bundle);
                    holder.fabMenuMain2.clearChecked();
                    activity.startActivity(listado);
                }
            });
        } else {
            holder.main_lv_view.setVisibility(View.GONE);
        }
        holder.info_observacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt("IdUpm", (Integer)  objView.get("id_upm"));
                bundle.putInt("IdUpmHijo", 0);

                Intent listado = new Intent(activity.getApplicationContext(), ObservacionActivity.class);
                listado.putExtras(bundle);
                holder.fabMenuMain2.clearChecked();
                activity.startActivity(listado);
            }
        });
//        }
//        setAnimation(holder.itemView, position);

        holder.chart.setDrawBarShadow(false);
        holder.chart.setDrawValueAboveBar(true);
        holder.chart.getDescription().setEnabled(false);
        holder.chart.setPinchZoom(false);
        holder.chart.setDrawGridBackground(false);
        holder.chart.setDrawBarShadow(false);
        XAxis xAxis = holder.chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-50);
        xAxis.setLabelCount(2);
        YAxis leftAxis = holder.chart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        holder.chart.getXAxis().setDrawAxisLine(false);
        holder.chart.setTouchEnabled(false);
        holder.chart.setClickable(true);
        holder.chart.setDoubleTapToZoomEnabled(false);
        holder.chart.getXAxis().setDrawAxisLine(false);
        holder.chart.getXAxis().setDrawGridLines(false);
        holder.chart.getXAxis().setDrawAxisLine(false);
        holder.chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        holder.chart.getXAxis().setTextSize(7f);
        holder.chart.getAxisLeft().setDrawGridLines(false);
        holder.chart.getAxisLeft().setDrawLabels(false);
        holder.chart.getAxisLeft().setDrawAxisLine(false);
        holder.chart.getAxisRight().setDrawAxisLine(false);
        holder.chart.getAxisRight().setDrawGridLines(false);
        holder.chart.getAxisRight().setDrawLabels(false);

//        holder.chart.setFitBars(true);

        Legend l = holder.chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);

        setData(holder,objView);
        holder.chart.animateXY(2000, 2000);
    }

    private void setData(ViewHolder holder, Map<String, Object> objView) {
        ArrayList<BarEntry> values = new ArrayList<>();
        int total = 0;
        int qBoletasElaboradas = (int) objView.get("qBoletasElaboradas");
        int qBoletasConcluidas = (int) objView.get("qBoletasConcluidas");
//        int qViviendasElaboradas = (int) objView.get("qViviendasElaboradas");
//        int qViviendasSeleccionadas = (int) objView.get("qViviendasSeleccionadas");
//        values.add(new BarEntry(0, qViviendasSeleccionadas));
//        values.add(new BarEntry(1, qViviendasElaboradas));
        values.add(new BarEntry(0, qBoletasConcluidas));
        values.add(new BarEntry(1, qBoletasElaboradas));

        final ArrayList<String> xAxisLabel = new ArrayList<>();
//        xAxisLabel.add("Viv.Seleccionadas");
//        xAxisLabel.add("Viv.Elaboradas");
        xAxisLabel.add("Concluidas");
        xAxisLabel.add("Elaboradas");
        holder.chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));
        BarDataSet set1;
        if (holder.chart.getData() != null &&
                holder.chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) holder.chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            holder.chart.getData().notifyDataChanged();
            holder.chart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, " ");
            final int[] VORDIPLOM_COLORS = {
                    MyApplication.getContext().getResources().getColor(R.color.color_concluido),
                    MyApplication.getContext().getResources().getColor(R.color.colorAccent)
//                    MyApplication.getContext().getResources().getColor(R.color.color_preconcluido),
//                    MyApplication.getContext().getResources().getColor(R.color.color_elaborado),
            };
            set1.setColors(VORDIPLOM_COLORS);
            set1.setDrawIcons(false);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueFormatter(new IntegerFormatter());
            data.setValueTextSize(10f);
            data.setBarWidth(0.7f);
            holder.chart.setData(data);
        }
    }

    @Override
    public int getItemCount() {
        return dataView.size();
    }

    public Object getItem(int position) {
        return dataView.get(position);
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView main_map_view;
        public TextView main_codigo;
        public TextView main_descripcion;
        public TextView main_mensaje;
        public ImageButton info_boleta;
//        public TextView info_observacion;
        public MaterialButton info_observacion;
        public MaterialButton main_lv_view;
        public MaterialButtonToggleGroup fabMenuMain2;
//        public LinearLayout main_info_view;
        public TextView txtViviendasElaboradas;
        public TextView txtViviendasSeleccionadas;
        public TextView txtBoletasElaboradas;
        public TextView txtBoletasConcluidas;
        public ListView listViewViviendasAdicion;
        public TextView txtViviendasRemplazo;
        public HorizontalBarChart chart;
        public TextView txtTotal;
        public TextView txtViviendas;
        public TextView txtLvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            main_map_view = itemView.findViewById(R.id.main_map_view);
            main_codigo = itemView.findViewById(R.id.main_codigo);
            main_descripcion = itemView.findViewById(R.id.main_descripcion);
            main_mensaje = itemView.findViewById(R.id.main_mensaje);
            info_boleta = itemView.findViewById(R.id.info_boleta);
            info_observacion = itemView.findViewById(R.id.info_observacion);
            main_lv_view = itemView.findViewById(R.id.main_lv_view);
            fabMenuMain2 = itemView.findViewById(R.id.fabMenuMain2);
//            main_info_view = itemView.findViewById(R.id.main_info_view);
            txtViviendasElaboradas = itemView.findViewById(R.id.list_boletas_elaboradas);
            txtViviendasSeleccionadas = itemView.findViewById(R.id.list_viviendas_seleccionadas);
            txtViviendasElaboradas = itemView.findViewById(R.id.list_viviendas_elaboradas);
            txtBoletasElaboradas = itemView.findViewById(R.id.list_boletas_elaboradas);
            txtBoletasConcluidas = itemView.findViewById(R.id.list_boletas_concluidas);
            listViewViviendasAdicion = itemView.findViewById(R.id.list_upm_adicional);
            txtViviendasRemplazo = itemView.findViewById(R.id.list_upm_remplazo);
            chart = itemView.findViewById(R.id.chart);
            txtTotal=itemView.findViewById(R.id.id_text_lv_total);
            txtViviendas=itemView.findViewById(R.id.id_text_lv_viviendas);
            txtLvTitle=itemView.findViewById(R.id.id_text_lv_lv);


        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            animation = AnimationUtils.loadAnimation(getContext(), R.anim.item_anim_from_right);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
