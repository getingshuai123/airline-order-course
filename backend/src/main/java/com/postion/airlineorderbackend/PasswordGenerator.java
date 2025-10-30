package com.postion.airlineorderbackend; // ?��?����a?�������ݓI��?��v�I

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        // ?���꘢ BCryptPasswordEncoder ?��
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // ?�z�v?�u�I������?
        String rawPassword = "password";

        // ?������??�s����
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // �ň�o�����@�I?��
        System.out.println("Plaintext: " + rawPassword);
        System.out.println("Encoded Password (for database): " + encodedPassword);
    }
}