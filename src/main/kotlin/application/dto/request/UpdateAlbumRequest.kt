package com.josecalvo.application.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateAlbumRequest(
    val title: String? = null,
    val releaseYear: Int? = null
)