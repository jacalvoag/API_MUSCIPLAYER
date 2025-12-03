package com.josecalvo.application.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class ArtistResponse(
    val id: String,
    val name: String,
    val genre: String?,
    val createdAt: String,
    val updatedAt: String
)