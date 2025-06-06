/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.utils;

import io.github.cdimascio.dotenv.Dotenv;

/**
 *
 * @author User
 */
public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.configure()
                                               .directory("./")
                                               .ignoreIfMissing()
                                               .load();

    public static String get(String key) {
        return dotenv.get(key);
    }
}