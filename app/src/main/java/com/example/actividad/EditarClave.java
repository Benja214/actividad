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
import com.example.actividad.utils.EncryptionUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditarClave extends AppCompatActivity {

    private String idClave;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText etSitioWeb, etNombreUsuario, etClave, etNotas;
    private Button btnVolver, btnActualizar, btnBorrar, btnDesencriptar;

    private boolean isPasswordEncrypted = true; // contraseña encriptada

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_clave);

        idClave = getIntent().getStringExtra("idClave");

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etSitioWeb = findViewById(R.id.etSitioWeb);
        etNombreUsuario = findViewById(R.id.etUsuario);
        etClave = findViewById(R.id.etClave);
        etNotas = findViewById(R.id.etNotas);
        btnVolver = findViewById(R.id.btnVolver);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnDesencriptar = findViewById(R.id.btnDesencriptar);

        db.collection("contraseñas").document(idClave).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Clave clave = documentSnapshot.toObject(Clave.class);
                etSitioWeb.setText(clave.getSitioWeb());
                etNombreUsuario.setText(clave.getNombreUsuario());
                etClave.setText(clave.getClave());
                etNotas.setText(clave.getNotas());
            } else {
                Toast.makeText(EditarClave.this, "Contraseña no encontrada", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(EditarClave.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
        });

        btnVolver.setOnClickListener(view -> {
            finish();
        });

        btnActualizar.setOnClickListener(view -> {
            updatePassword();
        });

        btnBorrar.setOnClickListener(view -> {
            deletePassword();
        });

        btnDesencriptar.setOnClickListener(view -> {
            String currentPassword = etClave.getText().toString();

            if (isPasswordEncrypted) {
                String claveDesencriptada = EncryptionUtil.decrypt(currentPassword);
                etClave.setText(claveDesencriptada);
                btnDesencriptar.setText("Encriptar");
                isPasswordEncrypted = false;
            } else {
                String claveEncriptada = EncryptionUtil.encrypt(currentPassword);
                etClave.setText(claveEncriptada);
                btnDesencriptar.setText("Desencriptar");
                isPasswordEncrypted = true;
            }
        });
    }

    private void updatePassword() {
        String sitioWeb = etSitioWeb.getText().toString().trim();
        String nombreUsuario = etNombreUsuario.getText().toString().trim();
        String clave = etClave.getText().toString().trim();
        String notas = etNotas.getText().toString().trim();

        if (TextUtils.isEmpty(sitioWeb) || TextUtils.isEmpty(nombreUsuario) || TextUtils.isEmpty(clave)) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        String encryptedClave = EncryptionUtil.encrypt(clave);
        String propietario = auth.getCurrentUser().getEmail();

        Clave claves = new Clave(idClave, propietario, sitioWeb, nombreUsuario, encryptedClave, notas);

        db.collection("contraseñas").document(idClave).set(claves)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("action", "updated");
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                });
    }

    private void deletePassword() {
        db.collection("contraseñas").document(idClave).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Contraseña eliminada", Toast.LENGTH_SHORT).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("action", "deleted");
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                });
    }
}
