package cl.grupo5.farmacias_lascondes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class FarmaciaAdapter extends ArrayAdapter<Farmacia> implements Filterable {
    private final Context context;
    private final List<Farmacia> originalList;
    private final List<Farmacia> filteredList;
    private final FarmaciaFilter farmaciaFilter;

    public FarmaciaAdapter(Context context, List<Farmacia> farmacias) {
        super(context, 0, farmacias);
        this.context = context;
        this.originalList = new ArrayList<>(farmacias);
        this.filteredList = new ArrayList<>(farmacias);
        this.farmaciaFilter = new FarmaciaFilter();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Farmacia getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_farmacia, parent, false);
        }

        Farmacia f = getItem(position);

        TextView tvNombre    = convertView.findViewById(R.id.tvNombre);
        TextView tvComuna    = convertView.findViewById(R.id.tvComuna);
        TextView tvDireccion = convertView.findViewById(R.id.tvDireccion);
        TextView tvApertura  = convertView.findViewById(R.id.tvApertura);
        TextView tvCierre    = convertView.findViewById(R.id.tvCierre);
        TextView tvTelefono  = convertView.findViewById(R.id.tvTelefono);
        Button btnLlamar     = convertView.findViewById(R.id.btnLlamar);
        Button btnUbicar     = convertView.findViewById(R.id.btnUbicar);

        // Rellenar texto principal
        tvNombre   .setText(f != null && f.getNombre()    != null ? f.getNombre()    : "—");
        tvComuna   .setText(f != null && f.getComuna()    != null ? f.getComuna()    : "—");
        tvDireccion.setText(f != null && f.getDireccion() != null ? f.getDireccion() : "—");
        tvTelefono .setText(f != null && f.getTelefono()  != null ? f.getTelefono()  : "—");

        // Formatear hora de apertura
        String apertura = f != null ? f.getApertura() : null;
        if (!TextUtils.isEmpty(apertura)) {
            String hhmm = apertura.length() >= 5 ? apertura.substring(0,5) : apertura;
            tvApertura.setText("Abre: " + hhmm);
        } else {
            tvApertura.setText("Abre: —");
        }

        // Formatear hora de cierre
        String cierre = f != null ? f.getCierre() : null;
        if (!TextUtils.isEmpty(cierre)) {
            String hhmm = cierre.length() >= 5 ? cierre.substring(0,5) : cierre;
            tvCierre.setText("Cierra: " + hhmm);
        } else {
            tvCierre.setText("Cierra: —");
        }

        // Botón Llamar
        btnLlamar.setOnClickListener(v -> {
            if (f != null && !TextUtils.isEmpty(f.getTelefono())) {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + f.getTelefono()));
                context.startActivity(intent);
            }
        });

        // Botón Ubicar (mapa)
        btnUbicar.setOnClickListener(v -> {
            if (f != null) {
                String uri = String.format(
                        "geo:%f,%f?q=%s",
                        f.getLatitud(),
                        f.getLongitud(),
                        Uri.encode(f.getNombre())
                );
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    @NonNull @Override
    public Filter getFilter() {
        return farmaciaFilter;
    }

    private class FarmaciaFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // siempre devolvemos la lista completa
            List<Farmacia> filtered = new ArrayList<>(originalList);
            FilterResults results = new FilterResults();
            results.values = filtered;
            results.count  = filtered.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList.clear();
            filteredList.addAll((List<Farmacia>) results.values);
            clear();
            addAll(filteredList);
            notifyDataSetChanged();
        }
    }
}
