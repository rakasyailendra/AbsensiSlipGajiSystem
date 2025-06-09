/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.utils;

import com.mycompany.absence.salary.slip.application.models.Pegawai;

/**
 *
 * @author User
 */
public class SessionManager {
    private static SessionManager instance;
    private Pegawai currentUser;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(Pegawai user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }

    public Pegawai getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
