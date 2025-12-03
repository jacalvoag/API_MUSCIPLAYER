package com.josecalvo.application.service

import com.josecalvo.application.dto.request.CreateTrackRequest
import com.josecalvo.application.dto.request.UpdateTrackRequest
import com.josecalvo.application.dto.response.TrackResponse
import com.josecalvo.domain.exception.BadRequestException
import com.josecalvo.domain.exception.NotFoundException
import com.josecalvo.domain.model.Track
import com.josecalvo.domain.repository.IAlbumRepository
import com.josecalvo.domain.repository.ITrackRepository
import java.util.UUID

class TrackService(
    private val trackRepository: ITrackRepository,
    private val albumRepository: IAlbumRepository
) {

    suspend fun createTrack(request: CreateTrackRequest): TrackResponse {
        if (request.title.isBlank()) {
            throw BadRequestException("El titulo de la cancion no puede estar en blanco")
        }

        if (request.duration <= 0) {
            throw BadRequestException("\n" + "La duración de la cancion debe ser mayor a 0")
        }

        val albumUuid = parseUUID(request.albumId)

        albumRepository.findById(albumUuid)
            ?: throw NotFoundException("El album con el id ${request.albumId} no fue encontrado")

        val track = Track(
            title = request.title.trim(),
            duration = request.duration,
            albumId = albumUuid
        )

        val created = trackRepository.create(track)
        return created.toResponse()
    }

    suspend fun getTrackById(id: String): TrackResponse {
        val uuid = parseUUID(id)
        val track = trackRepository.findById(uuid)
            ?: throw NotFoundException("Cancion con el id $id no fue encontrado")

        return track.toResponse()
    }

    suspend fun getAllTracks(): List<TrackResponse> {
        return trackRepository.findAll().map { it.toResponse() }
    }

    suspend fun updateTrack(id: String, request: UpdateTrackRequest): TrackResponse {
        val uuid = parseUUID(id)

        val existingTrack = trackRepository.findById(uuid)
            ?: throw NotFoundException("Cancion con el id $id no fue encontrado")

        if (request.title == null && request.duration == null) {
            throw BadRequestException("Se debe proporcionar al menos un campo para actualizar")
        }

        if (request.title != null && request.title.isBlank()) {
            throw BadRequestException("El titulo de la cancion no puede estar en blanco")
        }

        if (request.duration != null && request.duration <= 0) {
            throw BadRequestException("La duración de la cancion debe ser mayor a 0")
        }

        val updatedTrack = existingTrack.copy(
            title = request.title?.trim() ?: existingTrack.title,
            duration = request.duration ?: existingTrack.duration
        )

        val result = trackRepository.update(uuid, updatedTrack)
            ?: throw NotFoundException("Cancion con el id $id no fue encontrado")

        return result.toResponse()
    }

    suspend fun deleteTrack(id: String): Boolean {
        val uuid = parseUUID(id)

        trackRepository.findById(uuid)
            ?: throw NotFoundException("Cancion con el id $id no fue encontrado")

        return trackRepository.delete(uuid)
    }

    private fun parseUUID(id: String): UUID {
        return try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            throw BadRequestException("Formato UUID invalido : $id")
        }
    }

    private fun Track.toResponse() = TrackResponse(
        id = id.toString(),
        title = title,
        duration = duration,
        albumId = albumId.toString(),
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )
}