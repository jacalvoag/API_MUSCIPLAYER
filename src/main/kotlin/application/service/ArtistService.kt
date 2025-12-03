package com.josecalvo.application.service

import com.josecalvo.application.dto.request.CreateArtistRequest
import com.josecalvo.application.dto.request.UpdateArtistRequest
import com.josecalvo.application.dto.response.AlbumResponse
import com.josecalvo.application.dto.response.ArtistResponse
import com.josecalvo.application.dto.response.ArtistWithAlbumsResponse
import com.josecalvo.domain.exception.BadRequestException
import com.josecalvo.domain.exception.ConflictException
import com.josecalvo.domain.exception.NotFoundException
import com.josecalvo.domain.model.Artist
import com.josecalvo.domain.repository.IArtistRepository
import com.josecalvo.domain.repository.IAlbumRepository
import java.util.UUID

class ArtistService(
    private val artistRepository: IArtistRepository,
    private val albumRepository: IAlbumRepository
) {

    suspend fun createArtist(request: CreateArtistRequest): ArtistResponse {
        if (request.name.isBlank()) {
            throw BadRequestException("El nombre del Artista no puede estar en blanco")
        }

        val artist = Artist(
            name = request.name.trim(),
            genre = request.genre?.trim()
        )

        val created = artistRepository.create(artist)
        return created.toResponse()
    }

    suspend fun getArtistById(id: String): ArtistResponse {
        val uuid = parseUUID(id)
        val artist = artistRepository.findById(uuid)
            ?: throw NotFoundException("Artista con el ID $id no encontrado")

        return artist.toResponse()
    }

    suspend fun getArtistWithAlbums(id: String): ArtistWithAlbumsResponse {
        val uuid = parseUUID(id)
        val artist = artistRepository.findById(uuid)
            ?: throw NotFoundException("Artista con el ID $id no encontrado")

        val albums = albumRepository.findByArtistId(uuid)

        return ArtistWithAlbumsResponse(
            id = artist.id.toString(),
            name = artist.name,
            genre = artist.genre,
            createdAt = artist.createdAt.toString(),
            updatedAt = artist.updatedAt.toString(),
            albums = albums.map { it.toResponse() }
        )
    }

    suspend fun getAllArtists(): List<ArtistResponse> {
        return artistRepository.findAll().map { it.toResponse() }
    }

    suspend fun updateArtist(id: String, request: UpdateArtistRequest): ArtistResponse {
        val uuid = parseUUID(id)

        val existingArtist = artistRepository.findById(uuid)
            ?: throw NotFoundException("Artista con el ID $id no encontrado")

        if (request.name == null && request.genre == null) {
            throw BadRequestException("Se debe proporcionar al menos un campo para actualizar")
        }

        if (request.name != null && request.name.isBlank()) {
            throw BadRequestException("El nombre del artista no puede estar vacio")
        }

        val updatedArtist = existingArtist.copy(
            name = request.name?.trim() ?: existingArtist.name,
            genre = request.genre?.trim() ?: existingArtist.genre
        )

        val result = artistRepository.update(uuid, updatedArtist)
            ?: throw NotFoundException("Artista con el ID $id no encontrado")

        return result.toResponse()
    }

    suspend fun deleteArtist(id: String): Boolean {
        val uuid = parseUUID(id)

        artistRepository.findById(uuid)
            ?: throw NotFoundException("Artista con el ID $id no encontrado")

        if (artistRepository.hasAlbums(uuid)) {
            throw ConflictException("No se puede eliminar a un artista con albumes existentes")
        }

        return artistRepository.delete(uuid)
    }

    private fun parseUUID(id: String): UUID {
        return try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Formato de UUID invalido: $id")
        }
    }

    private fun Artist.toResponse() = ArtistResponse(
        id = id.toString(),
        name = name,
        genre = genre,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )

    private fun com.josecalvo.domain.model.Album.toResponse() = AlbumResponse(
        id = id.toString(),
        title = title,
        releaseYear = releaseYear,
        artistId = artistId.toString(),
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}