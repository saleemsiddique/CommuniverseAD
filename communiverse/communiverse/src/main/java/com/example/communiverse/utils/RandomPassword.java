package com.example.communiverse.utils;

import java.security.SecureRandom;

public class RandomPassword {
    // Método para generar una contraseña aleatoria
    static public String generateRandomPassword() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!_";
        int length = 12; // Longitud de la contraseña

        StringBuilder newPassword = new StringBuilder();

        // Uso de SecureRandom para mayor seguridad
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            newPassword.append(characters.charAt(index));
        }

        return newPassword.toString();
    }
}
