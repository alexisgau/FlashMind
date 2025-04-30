package com.example.flashmind.domain.model

interface UserData{
    data class Success(val name: String, val imageUrl: String):UserData
    data class Error(val message: String) : UserData
    data object Init : UserData

}