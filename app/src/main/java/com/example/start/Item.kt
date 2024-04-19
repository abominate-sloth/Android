package com.example.start

import java.io.Serializable

data class Item(var title: String? = null, var desc: String? = null, var text: String? = null, var images: ArrayList<ByteArray>? = null) : Serializable