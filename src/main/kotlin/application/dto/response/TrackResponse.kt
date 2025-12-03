package com.josecalvo.application.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class TrackResponse(
    val id: String,
    val title: String,
    val duration: Int,
    val albumId: String,
    val createdAt: String,
    val updatedAt: String
)