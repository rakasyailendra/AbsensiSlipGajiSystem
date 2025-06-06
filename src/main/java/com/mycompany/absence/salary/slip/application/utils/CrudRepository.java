/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.absence.salary.slip.application.utils;

import java.util.ArrayList;

/**
 *
 * @author User
 * @param <T>
 */
public interface CrudRepository<T> {
    Response<T> save(T entity);
    Response<T> update(T entity);
    Response<T> findById(Integer id);
    Response<Boolean> deleteById(Integer id);
    Response<ArrayList<T>> findAll();
}