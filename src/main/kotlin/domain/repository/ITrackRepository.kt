package com.josecalvo.domain.repository

import com.josecalvo.domain.model.Track
import java.util.UUID

interface ITrackRepository {
    suspend fun create(track: Track): Track
    suspend fun findById(id: UUID): Track?
    suspend fun findAll(): List<Track>
    suspend fun findByAlbumId(albumId: UUID): List<Track>
    suspend fun update(id: UUID, track: Track): Track?
    suspend fun delete(id: UUID): Boolean
}