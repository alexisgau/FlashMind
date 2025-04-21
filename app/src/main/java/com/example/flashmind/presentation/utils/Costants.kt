package com.example.flashmind.presentation.utils

import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

private val generativeModel = GenerativeModel(
    modelName = "gemini-1.5-flash",
    apiKey = "BuildConfig.apiKey"
)

//val response = generativeModel.generateContent(
//    content {
//        image(bitmap)
//        text(prompt)
//    }
//) AIzaSyBtlIkRofL3TfCe5dJwso3t-ljoLHkQIqc