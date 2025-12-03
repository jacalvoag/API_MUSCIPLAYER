package com.josecalvo.application.dto.request

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class CreateAlbumRequest(
    val title: String,
    val releaseYear: Int,
    val artistId: String
)