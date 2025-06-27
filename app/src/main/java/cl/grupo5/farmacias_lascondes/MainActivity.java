package cl.grupo5.farmacias_lascondes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
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

import androidx.appcompat.app.AlertDialog;
import android.view.MenuItem;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private ListView       lvFarmacias;
    private EditText       etFilter;
    private FarmaciaAdapter adapter;
    private FarmaciaService service;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // —————— Inicialización UI y Retrofit ——————
        lvFarmacias = findViewById(R.id.lvFarmacias);
        etFilter    = findViewById(R.id.etFilter);

        setupRetrofit();
        obtenerFarmacias();

        etFilter.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int a,int c,int d){}
            public void onTextChanged(CharSequence s,int a,int b,int c){
                if (adapter != null) adapter.getFilter().filter(s);
            }
            public void afterTextChanged(Editable s){}
        });

        // —————— Flecha “up” en la Toolbar (opcional) ——————
        // Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // —————— Interceptar “Back” físico/software ——————
        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        handleBack();
                    }
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleBack() {
        // 1) Crea y muestra el AlertDialog, guardando la referencia
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Salir")
                .setMessage("¿Desea salir de la aplicación?")
                .setPositiveButton("Sí", (d, which) -> {
                    d.dismiss();
                    showLogoutDialog();
                })
                .setNegativeButton("No", (d, which) -> d.dismiss())
                .show();

        // 2) Cambia el color de “Sí” y “No”
        int miColor = ContextCompat.getColor(this, R.color.verde);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(miColor);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(miColor);
    }


    private void showLogoutDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Desea cerrar sesión?")
                .setPositiveButton("Sí", (d, which) -> {
                    d.dismiss();
                    FirebaseAuth.getInstance().signOut();
                    finishAffinity();
                })
                .setNegativeButton("No", (d, which) -> {
                    d.dismiss();
                    finishAffinity();
                })
                .show();

        int miColor = ContextCompat.getColor(this, R.color.verde);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(miColor);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                .setTextColor(miColor);
    }


}
