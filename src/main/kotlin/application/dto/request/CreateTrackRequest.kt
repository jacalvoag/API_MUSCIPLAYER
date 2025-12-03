package com.josecalvo.application.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateTrackRequest(
    val title: String,
    val duration: Int,
    val albumId: String
)