package com.josecalvo.application.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class AlbumWithTracksResponse(
    val id: String,
    val title: String,
    val releaseYear: Int,
    val artistId: String,
    val createdAt: String,
    val updatedAt: String,
    val tracks: List<TrackResponse>
)