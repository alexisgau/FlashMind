package com.example.flashmind.domain.model

interface AuthResponse {
    data object Success : AuthResponse
    data object Init : AuthResponse
    data class Error(val message: String) : AuthResponse
}

