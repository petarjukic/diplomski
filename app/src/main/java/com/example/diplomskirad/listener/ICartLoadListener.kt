package com.example.diplomskirad.listener

import com.example.diplomskirad.model.Cart

interface ICartLoadListener {
    fun onLoadCartSuccess(cartList: List<Cart>)
    fun onSuccessMessage(message: String)
    fun onLoadCartError(errorMessage: String?)
}