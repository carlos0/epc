package bo.gob.ine.naci.epc.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import bo.gob.ine.naci.epc.MainActivity;
import bo.gob.ine.naci.epc.MyApplication;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.entidades.RolPermiso;
import bo.gob.ine.naci.epc.entidades.Usuario;
import bo.gob.ine.naci.epc.herramientas.Movil;
import bo.gob.ine.naci.epc.herramientas.Parametros;


/**
 * Created by INE.
 */
public class MainAdapter extends BaseAdapter implements Filterable{
    private ArrayList<Map<String, Object>> dataView;
    private LayoutInflater inflater = null;
    private Activity activity = null;
    private SwipeDetector swipeDetector;

    public MainAdapter(Activity activity, ArrayList<Map<String, Object>> dataView) {
        this.dataView = dataView;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        swipeDetector = new SwipeDetector();
    }

    public void refreshData(ArrayList<Map<String, Object>> dataView) {
        this.dataView = dataView;
    }

    @Override
    public int getCount() {
        if (dataView.size() <= 0)
            return 1;
        return dataView.size();
    }

    @Override
    public Object getItem(int position) {
        return dataView.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (dataView.size() > 0) {
            return (long)(int)dataView.get(position).entrySet().iterator().next().getValue();
        } else {
            return 0;
        }
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                //arrayListNames = (List<String>) results.values;
                //ArrayList<Map<String, Object>> dataViewSelected;
                dataView = (ArrayList<Map<String, Object>>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence searchText) {

                FilterResults results = new FilterResults();
                // ArrayList<String> FilteredArrayNames = new ArrayList<String>();
                ArrayList<Map<String, Object>> dataViewSelected = new ArrayList<Map<String, Object>>();

                searchText = searchText.toString().toLowerCase();
                for (int i = 0; i < dataView.size(); i++) {
                    Map<String, Object> objView = dataView.get(i);
                    String valor = (String) objView.get("codigo");
                    String descripcion = (String) objView.get("nombre");
                    if (valor.toLowerCase().contains(searchText.toString()) || descripcion.toLowerCase().contains(searchText.toString()))  {
                        dataViewSelected.add(objView);
                    }
                }
                results.count = dataViewSelected.size();
                results.values = dataViewSelected;
                return results;
            }
        };
        return filter;
    }

    public static class ViewHolder
    {
        public TextView txtValor;
        public TextView txtDescripcion;
        public TextView txtViviendasElaboradas;
        public TextView txtViviendasSeleccionadas;
        public TextView txtBoletasElaboradas;
        public TextView txtBoletasConcluidas;
        public TextView txtViviendasAdicion;
        public TextView txtViviendasRemplazo;
        public ImageView imgView;
        public ImageButton imgButton;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if (convertView == null) {
            vi = inflater.inflate(R.layout.adapter_list_main, null);
            holder = new ViewHolder();
            holder.txtValor = (TextView) vi.findViewById(R.id.list_value);
            holder.txtValor.setTextSize(Parametros.FONT_LIST_BIG);
//            holder.txtDescripcion = (TextView) vi.findViewById(R.id.list_description);
//            holder.txtDescripcion.setTextSize(Parametros.FONT_LIST_BIG);
            holder.txtViviendasElaboradas = (TextView) vi.findViewById(R.id.list_viviendas_elaboradas);
            holder.txtViviendasElaboradas.setTextSize(Parametros.FONT_LIST_SMALL);
            holder.txtViviendasSeleccionadas = (TextView) vi.findViewById(R.id.list_viviendas_seleccionadas);
            holder.txtViviendasSeleccionadas.setTextSize(Parametros.FONT_LIST_SMALL);
            holder.txtBoletasElaboradas = (TextView) vi.findViewById(R.id.list_boletas_elaboradas);
            holder.txtBoletasElaboradas.setTextSize(Parametros.FONT_LIST_SMALL);
            holder.txtBoletasConcluidas = (TextView) vi.findViewById(R.id.list_boletas_concluidas);
            holder.txtBoletasConcluidas.setTextSize(Parametros.FONT_LIST_SMALL);

            holder.txtViviendasAdicion = (TextView) vi.findViewById(R.id.list_upm_adicional);
            holder.txtViviendasAdicion.setTextSize(Parametros.FONT_LIST_SMALL);
//            holder.txtViviendasRemplazo = (TextView) vi.findViewById(R.id.list_upm_remplazo);
            holder.txtViviendasRemplazo.setTextSize(Parametros.FONT_LIST_SMALL);

//            holder.imgView = (ImageView) vi.findViewById(R.id.imageView);
//            holder.imgButton = (ImageButton) vi.findViewById(R.id.imageButton);
            vi.setTag(holder);
            vi.setLongClickable(true);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        if (dataView.size() > 0) {
            final Map<String, Object> objView = dataView.get(position);
            vi.setId((int) objView.get("id_upm"));
//            holder.imgView.setVisibility(View.VISIBLE);
            holder.imgButton.setVisibility(View.VISIBLE);

            if (RolPermiso.tienePermiso(Usuario.getRol(), "activity_googlemap")) {
                holder.imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            //((MainActivity) activity).irMap((objView.get("latitud")==null? -1d:(double)objView.get("latitud")), (objView.get("longitud")==null? -1d:(double)objView.get("longitud")));
//                            ((MainActivity) activity).irMap((Integer) objView.get("id_upm"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            holder.txtValor.setTextSize(Parametros.FONT_LIST_BIG);
            holder.imgButton.setVisibility(View.VISIBLE);
            holder.txtValor.setText((String) objView.get("codigo"));
            holder.txtDescripcion.setText((String)objView.get("nombre"));
            if (RolPermiso.tienePermiso(Usuario.getRol(), "activity_listado_viviendas")) {
                holder.txtViviendasElaboradas.setVisibility(View.VISIBLE);
                holder.txtViviendasSeleccionadas.setVisibility(View.VISIBLE);
                holder.txtViviendasElaboradas.setText("VIVIENDAS: "+objView.get("qViviendasElaboradas"));
                holder.txtViviendasSeleccionadas.setText("SELECCIONADAS: "+objView.get("qViviendasSeleccionadas"));
            } else {
                holder.txtViviendasElaboradas.setVisibility(View.GONE);
                holder.txtViviendasSeleccionadas.setVisibility(View.GONE);
            }
            holder.txtBoletasElaboradas.setText("ELABORADAS: "+objView.get("qBoletasElaboradas"));
            holder.txtBoletasConcluidas.setText("CONCLUIDAS: "+objView.get("qBoletasConcluidas"));
            holder.txtViviendasAdicion.setText("ADICIONALES: "+objView.get("qUpmsAdicionales"));
            holder.txtViviendasRemplazo.setText("REMPLAZADO: "+objView.get("qUpmsRemplazo"));
            holder.imgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    StringBuilder areaCensalDetalles = new StringBuilder();
                    if (!RolPermiso.tienePermiso(Usuario.getRol(), "indices")) {
                        areaCensalDetalles.append("<b>ID-ASIG: </b>").append(objView.get("id_asignacion")).append("<br>");
                        areaCensalDetalles.append("<b>ID-UPM: </b>").append(objView.get("id_upm")).append("<br>");
                    }
                    areaCensalDetalles.append("<b>CODIGO: </b>").append(objView.get("codigo")).append("<br>");
                    areaCensalDetalles.append("<b>NOMBRE: </b>").append(objView.get("nombre")).append("<br>");
                    areaCensalDetalles.append("<b>FECHA INICIO: </b>").append(Movil.dateExtractor((long) objView.get("fecinicio"))).append("<br>");
                    areaCensalDetalles.append("<b>LATITUD: </b>").append(objView.get("latitud")).append("<br>");
                    areaCensalDetalles.append("<b>LONGITUD: </b>").append(objView.get("longitud")).append("<br>");
                    areaCensalDetalles.append("<b>ESTADO: </b>").append(objView.get("estado")).append("<br>");
                    areaCensalDetalles.append("<b>URL_PDF: </b>").append(objView.get("url_pdf")).append("<br>");
                    areaCensalDetalles.append("<b>MES: </b>").append(objView.get("mes")).append("<br>");
                    if (RolPermiso.tienePermiso(Usuario.getRol(), "activity_listado_viviendas")) {
                        areaCensalDetalles.append("<b>VIVIENDAS ELABORADAS: </b>").append(objView.get("qViviendasElaboradas")).append("<br>");
                        areaCensalDetalles.append("<b>VIVIENDAS SELECCIONADAS: </b>").append(objView.get("qViviendasSeleccionadas")).append("<br>");
                        areaCensalDetalles.append("<b>VIVIENDAS ANULADAS: </b>").append(objView.get("qViviendasAnuladas")).append("<br>");
                    }
                    areaCensalDetalles.append("<b>B. ELABORADAS: </b>").append(objView.get("qBoletasElaboradas")).append("<br>");
                    areaCensalDetalles.append("<b>B. CONCLUIDAS: </b>").append(objView.get("qBoletasConcluidas")).append("<br>");
                    areaCensalDetalles.append("<b>B. ANULADAS: </b>").append(objView.get("qBoletasAnuladas")).append("<br>");
                    ((MainActivity) activity).informationMessage(MyApplication.getContext(), null, MyApplication.getContext().getString(R.string.action_detalles_area_censal).toUpperCase(), Html.fromHtml(areaCensalDetalles.toString()), Parametros.FONT_OBS);

                }
            });
            vi.setOnClickListener(new OnItemClickListener(position));
            vi.setOnLongClickListener(new OnLongItemClickListener(position));
            //vi.setOnTouchListener(swipeDetector);
        } else {
            holder.imgView.setVisibility(View.GONE);
            holder.txtValor.setText("Sin carga de trabajo.");
            holder.txtDescripcion.setText("");
            holder.txtBoletasElaboradas.setText("");
            holder.txtBoletasConcluidas.setText("");
            holder.imgButton.setVisibility(View.GONE);
        }
        return vi;
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            switch (swipeDetector.getAction()) {
                case None: // Click
                    AdapterEvents sct = (AdapterEvents) activity;
                    sct.onItemClick(mPosition);
                    break;
                case RL: // Swipe Right to Left
                    break;
                case LR: // Swipe Left to Right
                    break;
            }
        }
    }

    private class OnLongItemClickListener implements View.OnLongClickListener {
        private int mPosition;

        OnLongItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public boolean onLongClick(View v) {
            AdapterEvents sct = (AdapterEvents) activity;
            sct.onLongItemClick(mPosition);
            return true;
        }
    }
}
