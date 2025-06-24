package cl.grupo5.farmacias_lascondes;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class RegistroActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private LottieAnimationView lottieLoader;
    private EditText etEmailReg, etPassReg, etPassReg2;
    private Button btnRegister;
    private TextView tvGoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Init Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencias UI
        lottieLoader = findViewById(R.id.lottieLoader);
        etEmailReg  = findViewById(R.id.etEmailReg);
        etPassReg   = findViewById(R.id.etPassReg);
        etPassReg2  = findViewById(R.id.etPassReg2);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoLogin   = findViewById(R.id.tvGoLogin);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvGoLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegistroActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String email     = etEmailReg.getText().toString().trim();
        String pass      = etPassReg.getText().toString().trim();
        String pass2     = etPassReg2.getText().toString().trim();

        // Validaciones básicas
        if (TextUtils.isEmpty(email)) {
            etEmailReg.setError("Ingresa tu correo");
            return;
        }
        if (TextUtils.isEmpty(pass) || pass.length() < 6) {
            etPassReg.setError("Mínimo 6 caracteres");
            return;
        }
        if (!pass.equals(pass2)) {
            etPassReg2.setError("Las contraseñas no coinciden");
            return;
        }

        // Mostrar loader
        lottieLoader.setVisibility(View.VISIBLE);
        lottieLoader.playAnimation();
        btnRegister.setEnabled(false);

        // Crear usuario en Firebase
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    // Ocultar loader
                    lottieLoader.pauseAnimation();
                    lottieLoader.setVisibility(View.GONE);
                    btnRegister.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        // Manejo de errores más comunes
                        String msg = "Error desconocido";
                        Exception e = task.getException();
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            msg = "Este correo ya está registrado";
                        } else if (e instanceof FirebaseAuthWeakPasswordException) {
                            msg = "La contraseña es demasiado débil";
                        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            msg = "Formato de correo inválido";
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            msg = "No se puede registrar este usuario";
                        }
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
