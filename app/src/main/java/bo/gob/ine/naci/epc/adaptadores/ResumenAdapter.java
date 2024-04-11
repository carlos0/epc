package bo.gob.ine.naci.epc.adaptadores;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Map;
import bo.gob.ine.naci.epc.MyApplication;
import android.widget.Filter;
import bo.gob.ine.naci.epc.R;
import bo.gob.ine.naci.epc.herramientas.Parametros;

public class ResumenAdapter extends BaseAdapter implements Filterable {
    private ArrayList<Map<String, Object>> dataView;
    private ArrayList<Map<String, Object>> filteredDataView;
    private ItemFilter mFilter = new ItemFilter();
    private LayoutInflater inflater = null;
    private Activity activity = null;
    private SwipeDetector swipeDetector;

    public ResumenAdapter(Activity activity, ArrayList<Map<String, Object>> dataView) {
        this.dataView = dataView;
        this.filteredDataView = dataView;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        swipeDetector = new SwipeDetector();
    }

    public void setDataView (ArrayList<Map<String, Object>> val) {
        this.dataView = val;
        this.filteredDataView = val;
    }

    @Override
    public int getCount() {
        if (filteredDataView.size() <= 0)
            return 1;
        return filteredDataView.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredDataView.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (filteredDataView.size() > 0) {
            return (long)(int)filteredDataView.get(position).entrySet().iterator().next().getValue();
        } else {
            return 0;
        }
    }

    public static class ViewHolder
    {
        public TextView txtPregunta;
        public TextView txtAdicional;
        public TextView txtRespuesta;
        public TextView txtObservacion;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.resumen_adapter, null);

            holder = new ViewHolder();
            holder.txtPregunta = (TextView) vi.findViewById(R.id.list_pregunta);
            holder.txtAdicional = (TextView) vi.findViewById(R.id.list_adicional);
            holder.txtAdicional.setTextSize(Parametros.FONT_AYUD);
            holder.txtRespuesta = (TextView) vi.findViewById(R.id.list_respuesta);
            holder.txtRespuesta.setTextSize(Parametros.FONT_AYUD);
            holder.txtObservacion = (TextView) vi.findViewById(R.id.list_obsservacion);
            holder.txtObservacion.setTextSize(Parametros.FONT_AYUD);

            vi.setTag(holder);

            vi.setLongClickable(true);
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        if (filteredDataView.size() > 0) {
            Map<String, Object> objView = filteredDataView.get(position);

            if (objView.get("id_pregunta") == null) {
                if (objView.get("id_asignacion") == null) {
                    int nroRespuesta=Integer.parseInt(String.valueOf(objView.get("nro_respuestas")).split(" ")[0]==null?"0":String.valueOf(objView.get("nro_respuestas")).split(" ")[0]);
                    // DIBUJA UNA SECCION
                    vi.setId((int) objView.get("id_seccion") * -1);
                    if((int)objView.get("abierta") == 1) {
                        holder.txtPregunta.setText(Html.fromHtml(String.valueOf(objView.get("seccion")+" <br><i>Sección abierta</i>")));
                    } else {
                        holder.txtPregunta.setText(Html.fromHtml(String.valueOf(objView.get("seccion"))));
                    }
                    holder.txtPregunta.setTextSize(Parametros.FONT_RESP);
                    holder.txtPregunta.setTextColor(Color.parseColor("#000000"));
                    holder.txtPregunta.setGravity(Gravity.LEFT);
                    holder.txtAdicional.setText(String.valueOf(nroRespuesta==-1?"":nroRespuesta));
                    holder.txtAdicional.setVisibility(View.VISIBLE);
                    holder.txtRespuesta.setVisibility(View.GONE);
                    holder.txtObservacion.setVisibility(View.GONE);
                    if(nroRespuesta==-1){
                        holder.txtPregunta.setVisibility(View.GONE);
                        holder.txtAdicional.setVisibility(View.GONE);
                        holder.txtRespuesta.setVisibility(View.GONE);
                        holder.txtObservacion.setVisibility(View.GONE);
                    }else{
                        holder.txtPregunta.setVisibility(View.VISIBLE);
                        holder.txtAdicional.setVisibility(View.VISIBLE);
                        holder.txtRespuesta.setVisibility(View.GONE);
                        holder.txtObservacion.setVisibility(View.GONE);
                    }



                } else {
                    // DIBUJA NIVEL 2
                    vi.setId((int) objView.get("id_seccion") * -1);
                    holder.txtPregunta.setText(Html.fromHtml((String) objView.get("descripcion")));
                    holder.txtPregunta.setTextColor(Color.parseColor("#8b4513"));
                    holder.txtPregunta.setTextSize(Parametros.FONT_OBS);
                    holder.txtPregunta.setGravity(Gravity.CENTER);
                    holder.txtAdicional.setText((String) objView.get("nro_respuestas"));
                    holder.txtAdicional.setVisibility(View.VISIBLE);
                    holder.txtRespuesta.setVisibility(View.GONE);
                    holder.txtObservacion.setVisibility(View.GONE);

                }


            } else {
                // DIBUJA UNA PREGUNTA
                vi.setId((int) objView.get("id_pregunta"));
                //holder.txtPregunta.setText(Html.fromHtml("<b>" + objView.get("codigo_pregunta") + "</b>. " + Pregunta.procesaEnunciado(new IdInformante((Integer)objView.get("id_asignacion"), (Integer)objView.get("correlativo")), Integer.parseInt(objView.get("fila").toString()),(String) objView.get("pregunta"))));
                holder.txtPregunta.setText(Html.fromHtml("<b>" + objView.get("codigo_pregunta") + "</b>. " + objView.get("pregunta")));
                holder.txtPregunta.setTextColor(Color.DKGRAY);
                holder.txtPregunta.setGravity(Gravity.LEFT);
                holder.txtPregunta.setTextSize(Parametros.FONT_OBS);
                holder.txtAdicional.setVisibility(View.GONE);
                holder.txtRespuesta.setVisibility(View.VISIBLE);
                holder.txtObservacion.setVisibility(View.VISIBLE);
                holder.txtRespuesta.setText(Html.fromHtml(String.valueOf(objView.get("respuesta"))));
                if (objView.get("observacion") == null) {
                    holder.txtObservacion.setVisibility(View.GONE);
                } else {
                    holder.txtObservacion.setText(String.valueOf(objView.get("observacion")));
                }
            }
            if ( objView.get("cod_obs") != null ) {
                vi.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.colorWaterError));
            } else {
                vi.setBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.color_list));
            }

            vi.setOnClickListener(new OnItemClickListener(position));
            vi.setOnLongClickListener(new OnLongItemClickListener(position));
            vi.setOnTouchListener(swipeDetector);

        } else {
            holder.txtPregunta.setText("No existen datos.");
            holder.txtRespuesta.setText("");
            holder.txtObservacion.setText("");
        }
        return vi;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            final ArrayList<Map<String, Object>> originalData = dataView;
            int count = originalData.size();
            final ArrayList<Map<String, Object>> newData;
            newData = new ArrayList<>(count);

            if(!constraint.equals("")) {
                constraint = constraint.toString().substring(1);
                String filterPart[] = constraint.toString().split("-");
                for (int i = 0; i < count; i++) {
                    Map<String, Object> value = originalData.get(i);
                    for (int j = 0; j < filterPart.length; j++) {
                        String filterString[] = filterPart[j].toString().split(":");
                        if (value.get(filterString[0]).toString().equals(filterString[1])) {
                            newData.add(value);
                            break;
                        }
                    }
                }
            }
            results.values = newData;
            results.count = newData.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredDataView = (ArrayList<Map<String, Object>>) results.values;
            notifyDataSetChanged();
        }
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