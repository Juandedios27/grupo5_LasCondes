package cl.grupo5.farmacias_lascondes;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private ListView lvFarmacias;
    private FarmaciaService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvFarmacias = findViewById(R.id.lvFarmacias);

        setupRetrofit();
        obtenerFarmacias();
    }

    private void setupRetrofit() {
        // 1) Interceptor de logging (opcional)
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        // 2) Deserializador para double que limpia la coma final
        JsonDeserializer<Double> doubleDeserializer = (json, typeOfT, context) -> {
            String s = json.getAsString().trim();
            if (s.endsWith(",")) {
                s = s.substring(0, s.length() - 1);
            }
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        };

        // 3) Gson leniente con nuestro TypeAdapter para double
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Double.class, doubleDeserializer)
                .registerTypeAdapter(double.class, doubleDeserializer)
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://midas.minsal.cl/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        service = retrofit.create(FarmaciaService.class);
    }

    private void obtenerFarmacias() {
        // Fecha de hoy en yyyy-MM-dd
        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());

        service.getFarmacias(fechaHoy, "102")
                .enqueue(new Callback<List<Farmacia>>() {
                    @Override
                    public void onResponse(Call<List<Farmacia>> call,
                                           Response<List<Farmacia>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Filtrar por comuna por seguridad
                            List<Farmacia> resultado = new ArrayList<>();
                            for (Farmacia f : response.body()) {
                                if ("LAS CONDES".equalsIgnoreCase(f.getComuna())) {
                                    resultado.add(f);
                                }
                            }
                            lvFarmacias.setAdapter(
                                    new FarmaciaAdapter(MainActivity.this, resultado)
                            );
                        } else {
                            Toast.makeText(MainActivity.this,
                                    "Error al obtener datos",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Farmacia>> call, Throwable t) {
                        Toast.makeText(MainActivity.this,
                                "Fallo de red: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}
