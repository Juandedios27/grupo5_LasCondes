package cl.grupo5.farmacias_lascondes;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIMEOUT = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(() -> {
            if (!hayConexion(this)) {
                mostrarAlertaSinInternet();
            } else {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
                finish();
            }
        }, SPLASH_TIMEOUT);
    }

    private void mostrarAlertaSinInternet() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SplashActivity.this);
        alertBuilder.setTitle("Sin conexión");
        alertBuilder.setMessage("Necesitas estar conectado a Internet para usar la aplicación.");
        alertBuilder.setPositiveButton("OK", (dialogInterface, i) -> finish());

        // 1) Muestra el diálogo y guarda la referencia
        AlertDialog alerta = alertBuilder.show();
        alerta.setCancelable(false);
        alerta.setCanceledOnTouchOutside(false);

        // 2) Pinta el botón OK de verde
        int verde = ContextCompat.getColor(this, R.color.verde);
        alerta.getButton(DialogInterface.BUTTON_POSITIVE)
                .setTextColor(verde);
    }


    private boolean hayConexion(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo datos = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            return (datos != null && datos.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting());
        }
        return false;
    }
}
