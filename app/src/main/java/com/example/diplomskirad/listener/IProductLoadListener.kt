package com.example.diplomskirad.listener

import com.example.diplomskirad.model.Product

interface IProductLoadListener {
    fun onSuccess(cartList: List<Product>)
    fun onError(message: String?)
}