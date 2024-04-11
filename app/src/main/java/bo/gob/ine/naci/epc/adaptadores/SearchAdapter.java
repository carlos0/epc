package bo.gob.ine.naci.epc.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import bo.gob.ine.naci.epc.R;


public class SearchAdapter extends ArrayAdapter<String> {
    Context context;
    int  textViewResourceId;
    List<String> items,tempItems, suggestions;
    public SearchAdapter(@NonNull Context context, int textViewResourceId, @NonNull List<String> objects) {
        super(context, textViewResourceId, objects);
        this.context=context;
        this.textViewResourceId=textViewResourceId;
        this.items=objects;
        tempItems=new ArrayList<>();
        suggestions=new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view=convertView;
        if(convertView==null){
            LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view=inflater.inflate(R.layout.autocomplete_item,parent,false);
        }
        String values=items.get(position);
        if(values!=null){
            TextView lblValue=(TextView) view.findViewById(R.id.lbl_value);
            if(lblValue!=null)
                lblValue.setText(values);
        }
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return valueFilter;
    }
    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter valueFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if(constraint!=null){
                suggestions.clear();
                for (String values:tempItems){
                    if(values.toLowerCase().contains(constraint.toString().toLowerCase())){
                        suggestions.add(values);
                    }
                }
                FilterResults filterResults=new FilterResults();
                filterResults.values=suggestions;
                filterResults.count=suggestions.size();
                return filterResults;
            }else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            List<String> filterList=(ArrayList<String>)results.values;
            if(results!=null && results.count>0){
                clear();
                for(String values:filterList){
                    add(values);
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return super.convertResultToString(resultValue);
        }
    };
}
