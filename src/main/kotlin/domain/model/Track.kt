package com.josecalvo.domain.model

import java.time.Instant
import java.util.UUID

data class Track(
    val id: UUID = UUID.randomUUID(),
    val title: String,
    val duration: Int,
    val albumId: UUID,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)