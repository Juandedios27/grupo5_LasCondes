package cl.grupo5.farmacias_lascondes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referencias UI
        etEmail     = findViewById(R.id.etEmail);
        etPassword  = findViewById(R.id.etPassword);
        btnLogin    = findViewById(R.id.btnLogin);
        tvRegister  = findViewById(R.id.tvRegister);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Validando credenciales...");
        progressDialog.setCancelable(false);

        // Click en Ingresar
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass  = etPassword.getText().toString().trim();

            // Validación básica
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Ingresa tu correo");
                return;
            }
            if (TextUtils.isEmpty(pass) || pass.length() < 6) {
                etPassword.setError("Contraseña mínima 6 caracteres");
                return;
            }

            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, task -> {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            // Login OK → MainActivity
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            // Manejo de errores
                            Exception e = task.getException();
                            String msg = "Error de autenticación";
                            if (e instanceof FirebaseAuthInvalidUserException) {
                                msg = "Usuario no registrado";
                            } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                msg = "Correo o contraseña incorrectos";
                            }
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    });
        });

        // Click en Registrar (si tienes Activity de registro)
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Si ya hay usuario logueado, ir directo a Main
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
