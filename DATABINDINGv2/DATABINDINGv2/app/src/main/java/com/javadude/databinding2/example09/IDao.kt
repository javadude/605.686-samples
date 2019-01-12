package com.javadude.databinding2.example09

interface IDao<in T> {
    fun insert(item : T)
}