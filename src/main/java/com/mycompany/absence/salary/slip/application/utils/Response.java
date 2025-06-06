/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.utils;

/**
 *
 * @author User
 * @param <T>
 */
public class Response<T> {
    private final boolean success;
    private final String message;
    private final T data;

    private Response(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    /** Factory method untuk respons sukses.
     * @param <T>
     * @param message
     * @param data
     * @return  */
    public static <T> Response<T> success(String message, T data) {
        return new Response<>(true, message, data);
    }

    /** Factory method untuk respons gagal tanpa data.
     * @param <T>
     * @param message
     * @return  */
    public static <T> Response<T> failure(String message) {
        return new Response<>(false, message, null);
    }

    /** Factory method untuk respons sukses tanpa data (misal: delete).
     * @param <T>
     * @param message
     * @return  */
    public static <T> Response<T> success(String message) {
        return new Response<>(true, message, null);
    }

    @Override
    public String toString() {
        return String.format("Response{success=%s, message='%s', data=%s}", success, message, data);
    }
}