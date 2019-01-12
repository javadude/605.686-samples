package com.javadude.databinding2.example08

interface IDao<in T> {
    fun insert(item : T)
}