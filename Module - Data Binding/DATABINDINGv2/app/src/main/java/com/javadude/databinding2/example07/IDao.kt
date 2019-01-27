package com.javadude.databinding2.example07

interface IDao<in T> {
    fun insert(item : T)
}