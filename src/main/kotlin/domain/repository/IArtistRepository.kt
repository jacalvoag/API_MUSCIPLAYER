package com.josecalvo.domain.repository

import com.josecalvo.domain.model.Artist
import java.util.UUID

interface IArtistRepository {
    suspend fun create(artist: Artist): Artist
    suspend fun findById(id: UUID): Artist?
    suspend fun findAll(): List<Artist>
    suspend fun update(id: UUID, artist: Artist): Artist?
    suspend fun delete(id: UUID): Boolean
    suspend fun hasAlbums(artistId: UUID): Boolean
}