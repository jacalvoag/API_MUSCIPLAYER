package com.josecalvo.application.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateArtistRequest(
    val name: String,
    val genre: String? = null
)