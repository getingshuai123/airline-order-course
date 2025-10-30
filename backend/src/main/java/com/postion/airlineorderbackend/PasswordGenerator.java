package com.postion.airlineorderbackend; // ?保?个包名和?文件所在的目?一致！

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        // ?建一个 BCryptPasswordEncoder ?例
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // ?想要?置的明文密?
        String rawPassword = "password";

        // ?明文密??行加密
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 打印出加密后的?果
        System.out.println("Plaintext: " + rawPassword);
        System.out.println("Encoded Password (for database): " + encodedPassword);
    }
}