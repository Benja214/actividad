package com.example.actividad.adapters;


import android.app.Activity;
import android.content.Context;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.actividad.EditarClave;
import com.example.actividad.R;
import com.example.actividad.models.Clave;
import java.util.List;

public class PassAdapter extends RecyclerView.Adapter<PassAdapter.PasswordViewHolder> {

    private Context context;
    private List<Clave> passwordList;

    public PassAdapter(Context context, List<Clave> passwordList) {
        this.context = context;
        this.passwordList = passwordList;
    }

    @NonNull
    @Override
    public PasswordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.password_card, parent, false);
        return new PasswordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordViewHolder holder, int position) {
        Clave password = passwordList.get(position);

        holder.tvSitioWeb.setText(password.getSitioWeb());
        holder.tvUsuario.setText("Usuario: " + password.getNombreUsuario());
        holder.tvNotas.setText("Notas adicionales: " + password.getNotas());

        holder.btnVer.setOnClickListener(view -> {
            showPassword(password.getIdClave());
        });
    }

    @Override
    public int getItemCount() {
        return passwordList.size();
    }

    private void showPassword(String passwordId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            BiometricPrompt biometricPrompt;
            BiometricPrompt.PromptInfo promptInfo;

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Autenticación biométrica")
                    .setSubtitle("Usa tu huella para ver la contraseña")
                    .setNegativeButtonText("Cancelar")
                    .build();

            biometricPrompt = new BiometricPrompt((FragmentActivity) context,
                    ContextCompat.getMainExecutor(context),
                    new BiometricPrompt.AuthenticationCallback() {
                        @Override
                        public void onAuthenticationSucceeded(
                                @NonNull BiometricPrompt.AuthenticationResult result) {
                            super.onAuthenticationSucceeded(result);
                            Intent intent = new Intent(context, EditarClave.class);
                            intent.putExtra("idClave", passwordId);
                            ((Activity) context).startActivityForResult(intent, 1);
                        }

                        @Override
                        public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                            super.onAuthenticationError(errorCode, errString);
                            Toast.makeText(context, "Error: " + errString, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onAuthenticationFailed() {
                            super.onAuthenticationFailed();
                            Toast.makeText(context, "Autenticación fallida", Toast.LENGTH_SHORT).show();
                        }
                    });

            biometricPrompt.authenticate(promptInfo);
        } else {
            Toast.makeText(context, "La autenticación biométrica no está disponible en este dispositivo", Toast.LENGTH_SHORT).show();
        }
    }

    public static class PasswordViewHolder extends RecyclerView.ViewHolder {
        TextView tvSitioWeb, tvUsuario, tvNotas;
        Button btnVer;

        public PasswordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSitioWeb = itemView.findViewById(R.id.tvSitioWeb);
            tvUsuario = itemView.findViewById(R.id.tvUsuario);
            tvNotas = itemView.findViewById(R.id.tvNotas);
            btnVer = itemView.findViewById(R.id.btnVer);
        }
    }
}