package com.example.actividad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.actividad.models.Clave;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class CrearClave extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText etSitioWeb, etUsuario, etClave, etNotas;
    private Button btnVolver, btnCrearPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_clave);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etSitioWeb = findViewById(R.id.etSitioWeb);
        etUsuario = findViewById(R.id.etUsuario);
        etClave = findViewById(R.id.etClave);
        etNotas = findViewById(R.id.etNotas);
        btnVolver = findViewById(R.id.btnVolver);
        btnCrearPassword = findViewById(R.id.btnCreatePassword);

        btnVolver.setOnClickListener(view -> {
            finish();
        });

        btnCrearPassword.setOnClickListener(view -> {
            createPassword();
        });
    }

    private void createPassword() {
        String sitioWeb = etSitioWeb.getText().toString().trim();
        String nombreUsuario = etUsuario.getText().toString().trim();
        String clave = etClave.getText().toString().trim();
        String notas = etNotas.getText().toString().trim();

        if (TextUtils.isEmpty(sitioWeb) || TextUtils.isEmpty(nombreUsuario) || TextUtils.isEmpty(clave)) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String idClave = UUID.randomUUID().toString();
        String propietario = auth.getCurrentUser().getEmail();

        Clave claves = new Clave(idClave, propietario, sitioWeb, nombreUsuario, clave, notas);
        claves.encriptarClave();

        db.collection("contraseñas").document(idClave).set(claves)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Contraseña guardada", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("action", "created");
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                });
    }
}
