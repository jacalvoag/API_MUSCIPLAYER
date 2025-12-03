package com.josecalvo.domain.model

import java.time.Instant
import java.util.UUID

data class Artist(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val genre: String?,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = Instant.now()
)