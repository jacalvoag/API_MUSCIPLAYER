package com.josecalvo.application.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateArtistRequest(
    val name: String? = null,
    val genre: String? = null
)