package cl.grupo5.farmacias_lascondes;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ListView       lvFarmacias;
    private EditText       etFilter;
    private FarmaciaAdapter adapter;
    private FarmaciaService service;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_main);

        lvFarmacias = findViewById(R.id.lvFarmacias);
        etFilter    = findViewById(R.id.etFilter);

        setupRetrofit();
        obtenerFarmacias();

        // Filtrado en vivo
        etFilter.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int a,int c,int d){}
            public void onTextChanged(CharSequence s,int a,int b,int c){
                if (adapter != null) adapter.getFilter().filter(s);
            }
            public void afterTextChanged(Editable s){}
        });
    }

    /* ------------------ Retrofit ------------------ */

    private void setupRetrofit() {
        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(log).build();

        // Algun campo numérico llega con coma al final → convertimos seguro
        JsonDeserializer<Double> doubleFix = (json, t, c) -> {
            String s = json.getAsString().trim();
            if (s.endsWith(",")) s = s.substring(0, s.length()-1);
            try { return Double.parseDouble(s); } catch (NumberFormatException e) { return 0.0; }
        };

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Double.class,  doubleFix)
                .registerTypeAdapter(double.class, doubleFix)
                .setLenient()
                .create();

        Retrofit r = new Retrofit.Builder()
                .baseUrl("https://midas.minsal.cl/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        service = r.create(FarmaciaService.class);
    }

    /* ------------------ Llamada a la API ------------------ */

    private void obtenerFarmacias() {
        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        service.getFarmacias(hoy, "102")  // 102 = comuna Las Condes
                .enqueue(new Callback<List<Farmacia>>() {
                    @Override
                    public void onResponse(Call<List<Farmacia>> call,
                                           Response<List<Farmacia>> rsp) {
                        if (rsp.isSuccessful() && rsp.body()!=null) {
                            List<Farmacia> filtrado = new ArrayList<>();
                            for (Farmacia f : rsp.body()) {
                                if ("LAS CONDES".equalsIgnoreCase(f.getComuna()))
                                    filtrado.add(f);
                            }
                            adapter = new FarmaciaAdapter(MainActivity.this, filtrado);
                            lvFarmacias.setAdapter(adapter);
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Error al obtener datos", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Farmacia>> c, Throwable t) {
                        Toast.makeText(MainActivity.this,
                                "Fallo de red: "+t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
