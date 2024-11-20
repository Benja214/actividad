package com.example.actividad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actividad.adapters.PassAdapter;
import com.example.actividad.models.Clave;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private PassAdapter passAdapter;
    private List<Clave> passwordList;
    private Button btnCrearPass, btnSalir;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        btnCrearPass = findViewById(R.id.btnCrearPass);
        btnSalir = findViewById(R.id.btnSalir);

        passwordList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        passAdapter = new PassAdapter(this, passwordList);
        recyclerView.setAdapter(passAdapter);

        loadPasswordsList();

        btnCrearPass.setOnClickListener(view -> {
            Intent intent = new Intent(this, CrearClave.class);
            ((Activity) this).startActivityForResult(intent, 1);
        });

        btnSalir.setOnClickListener(view -> {
            auth.signOut();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(HomeActivity.this, "Ha salido con exito!!", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String action = data.getStringExtra("action");
                if ("created".equals(action) || "updated".equals(action) || "deleted".equals(action)) {
                    loadPasswordsList();
                }
            }
        }
    }

    private void loadPasswordsList() {
        String correo = auth.getCurrentUser().getEmail();

        db.collection("contraseñas")
                .whereEqualTo("propietario", correo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        passwordList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Clave password = document.toObject(Clave.class);
                            passwordList.add(password);
                        }
                        passAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error al actualizar la lista", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
