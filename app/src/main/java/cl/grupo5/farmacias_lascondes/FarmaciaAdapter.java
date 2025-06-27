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
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FarmaciaAdapter extends ArrayAdapter<Farmacia> implements Filterable {

    private final List<Farmacia> originalList;
    private final List<Farmacia> filteredList;
    private final FarmaciaFilter farmaciaFilter;

    public FarmaciaAdapter(Context context, List<Farmacia> farmacias) {
        super(context, 0, farmacias);
        this.originalList = new ArrayList<>(farmacias);
        this.filteredList = new ArrayList<>(farmacias);
        this.farmaciaFilter = new FarmaciaFilter();
    }

    @Override public int      getCount()         { return filteredList.size(); }
    @Override public Farmacia getItem(int pos)   { return filteredList.get(pos); }
    @Override public long     getItemId(int pos) { return pos; }

    @NonNull @Override
    public View getView(int pos, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.item_farmacia, parent, false);
        }
        Farmacia f = getItem(pos);

        TextView tvNombre    = convertView.findViewById(R.id.tvNombre);
        TextView tvComuna    = convertView.findViewById(R.id.tvComuna);
        TextView tvDireccion = convertView.findViewById(R.id.tvDireccion);
        TextView tvApertura  = convertView.findViewById(R.id.tvApertura);
        TextView tvCierre    = convertView.findViewById(R.id.tvCierre);
        TextView tvTelefono  = convertView.findViewById(R.id.tvTelefono);
        ImageButton btnLlamar   = convertView.findViewById(R.id.btnLlamar);
        ImageButton   btnUbicar   = convertView.findViewById(R.id.btnUbicar);

        tvNombre.setText   (noNull(f.getNombre(),    "—"));
        tvComuna.setText   (noNull(f.getComuna(),    "—"));
        tvDireccion.setText(noNull(f.getDireccion(), "—"));
        tvTelefono.setText (noNull(f.getTelefono(),  "—"));

        tvApertura.setText("Abre: "   + horaCorta(f.getApertura(), "—"));
        tvCierre  .setText("Cierra: " + horaCorta(f.getCierre(),   "—"));

        btnLlamar.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(f.getTelefono())) {
                Intent i = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + f.getTelefono()));
                getContext().startActivity(i);
            }
        });

        btnUbicar.setOnClickListener(v -> {
            Uri uri = Uri.parse("google.navigation:q=" + f.getLatitud() + "," + f.getLongitud());
            Intent map = new Intent(Intent.ACTION_VIEW, uri);
            map.setPackage("com.google.android.apps.maps");
            if (map.resolveActivity(getContext().getPackageManager()) == null) {
                // Fallback a navegador
                uri = Uri.parse(String.format(Locale.US,
                        "https://www.google.com/maps/dir/?api=1&destination=%f,%f",
                        f.getLatitud(), f.getLongitud()));
                map = new Intent(Intent.ACTION_VIEW, uri);
            }
            getContext().startActivity(map);
        });
        return convertView;
    }

    @NonNull @Override public Filter getFilter() { return farmaciaFilter; }

    /* ----------------------- helpers ----------------------- */

    private String noNull(String s, String def) { return s != null && !s.isEmpty() ? s : def; }

    private String horaCorta(String h, String def) {
        return !TextUtils.isEmpty(h) ? (h.length() >= 5 ? h.substring(0,5) : h) : def;
    }

    /* ----------------------- filtro ----------------------- */

    private class FarmaciaFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence cons) {
            FilterResults r = new FilterResults();
            if (cons == null || cons.length() == 0) {
                r.values = new ArrayList<>(originalList);
                r.count  = originalList.size();
            } else {
                String q = cons.toString().toLowerCase(Locale.getDefault()).trim();
                List<Farmacia> match = new ArrayList<>();
                for (Farmacia f : originalList) {
                    if (f.getNombre() != null &&
                            f.getNombre().toLowerCase(Locale.getDefault()).contains(q)) {
                        match.add(f);
                    }
                }
                r.values = match;
                r.count  = match.size();
            }
            return r;
        }
        @SuppressWarnings("unchecked")
        @Override protected void publishResults(CharSequence c, FilterResults r) {
            filteredList.clear();
            filteredList.addAll((List<Farmacia>) r.values);
            clear(); addAll(filteredList); notifyDataSetChanged();
        }
    }
}
