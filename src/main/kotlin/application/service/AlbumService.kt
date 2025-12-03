package com.josecalvo.application.service

import com.josecalvo.application.dto.request.CreateAlbumRequest
import com.josecalvo.application.dto.request.UpdateAlbumRequest
import com.josecalvo.application.dto.response.AlbumResponse
import com.josecalvo.application.dto.response.AlbumWithTracksResponse
import com.josecalvo.application.dto.response.TrackResponse
import com.josecalvo.domain.exception.BadRequestException
import com.josecalvo.domain.exception.ConflictException
import com.josecalvo.domain.exception.NotFoundException
import com.josecalvo.domain.model.Album
import com.josecalvo.domain.repository.IAlbumRepository
import com.josecalvo.domain.repository.IArtistRepository
import com.josecalvo.domain.repository.ITrackRepository
import java.util.UUID

class AlbumService(
    private val albumRepository: IAlbumRepository,
    private val artistRepository: IArtistRepository,
    private val trackRepository: ITrackRepository
) {

    suspend fun createAlbum(request: CreateAlbumRequest): AlbumResponse {
        if (request.title.isBlank()) {
            throw BadRequestException("El titulo del album no puede estar en blanco")
        }

        if (request.releaseYear < 1900) {
            throw BadRequestException("El año de lanzamiento debe ser 1900 o posterior")
        }

        val artistUuid = parseUUID(request.artistId)

        artistRepository.findById(artistUuid)
            ?: throw NotFoundException("El artista con el ID ${request.artistId} no fue encontrado")

        val album = Album(
            title = request.title.trim(),
            releaseYear = request.releaseYear,
            artistId = artistUuid
        )

        val created = albumRepository.create(album)
        return created.toResponse()
    }

    suspend fun getAlbumById(id: String): AlbumResponse {
        val uuid = parseUUID(id)
        val album = albumRepository.findById(uuid)
            ?: throw NotFoundException("El album con el ID $id no fue encontrado")

        return album.toResponse()
    }

    suspend fun getAlbumWithTracks(id: String): AlbumWithTracksResponse {
        val uuid = parseUUID(id)
        val album = albumRepository.findById(uuid)
            ?: throw NotFoundException("El album con el ID $id no fue encontrado")

        val tracks = trackRepository.findByAlbumId(uuid)

        return AlbumWithTracksResponse(
            id = album.id.toString(),
            title = album.title,
            releaseYear = album.releaseYear,
            artistId = album.artistId.toString(),
            createdAt = album.createdAt.toString(),
            updatedAt = album.updatedAt.toString(),
            tracks = tracks.map { it.toResponse() }
        )
    }

    suspend fun getAllAlbums(): List<AlbumResponse> {
        return albumRepository.findAll().map { it.toResponse() }
    }

    suspend fun updateAlbum(id: String, request: UpdateAlbumRequest): AlbumResponse {
        val uuid = parseUUID(id)

        val existingAlbum = albumRepository.findById(uuid)
            ?: throw NotFoundException("El album con el ID $id no fue encontrado")

        if (request.title == null && request.releaseYear == null) {
            throw BadRequestException("\n" + "Se debe proporcionar al menos un campo para actualizar")
        }

        if (request.title != null && request.title.isBlank()) {
            throw BadRequestException("El titulo del album no puede estar en blanco")
        }

        if (request.releaseYear != null && request.releaseYear < 1900) {
            throw BadRequestException("El año de lanzamiento debe ser 1900 o posterior")
        }

        val updatedAlbum = existingAlbum.copy(
            title = request.title?.trim() ?: existingAlbum.title,
            releaseYear = request.releaseYear ?: existingAlbum.releaseYear
        )

        val result = albumRepository.update(uuid, updatedAlbum)
            ?: throw NotFoundException("El album con el ID $id no fue encontrado")

        return result.toResponse()
    }

    suspend fun deleteAlbum(id: String): Boolean {
        val uuid = parseUUID(id)

        albumRepository.findById(uuid)
            ?: throw NotFoundException("El album con el ID $id no fue encontrado")

        if (albumRepository.hasTracks(uuid)) {
            throw ConflictException("No se puede eliminar un álbum con tracks existentes")
        }

        return albumRepository.delete(uuid)
    }

    private fun parseUUID(id: String): UUID {
        return try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Formato UUID invalido : $id")
        }
    }

    private fun Album.toResponse() = AlbumResponse(
        id = id.toString(),
        title = title,
        releaseYear = releaseYear,
        artistId = artistId.toString(),
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )

    private fun com.josecalvo.domain.model.Track.toResponse() = TrackResponse(
        id = id.toString(),
        title = title,
        duration = duration,
        albumId = albumId.toString(),
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}