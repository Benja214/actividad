package com.example.actividad.utils;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import java.security.Key;
import java.security.KeyStore;

public class EncryptionUtil {

    private static final String KEY_ALIAS = "clave_usuario";

    private static SecretKey getKey() throws Exception {
        KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        Key key = keyStore.getKey(KEY_ALIAS, null);
        if (key == null) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .build());
            key = keyGenerator.generateKey();
        }
        return (SecretKey) key;
    }

    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey key = getKey();

            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] iv = cipher.getIV();
            byte[] encryption = cipher.doFinal(data.getBytes());

            String ivString = Base64.encodeToString(iv, Base64.NO_PADDING);
            String encryptedString = Base64.encodeToString(encryption, Base64.NO_PADDING);

            return ivString + ":" + encryptedString;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encryptedData) {
        try {
            String[] parts = encryptedData.split(":");
            byte[] iv = Base64.decode(parts[0], Base64.NO_PADDING);
            byte[] encryptedText = Base64.decode(parts[1], Base64.NO_PADDING);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKey key = getKey();
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);

            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] decryptedData = cipher.doFinal(encryptedText);
            return new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
