package com.josecalvo.domain.model

import java.time.Instant
import java.util.UUID

data class Album(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val releaseYear: Int,
    val artistId: UUID,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)