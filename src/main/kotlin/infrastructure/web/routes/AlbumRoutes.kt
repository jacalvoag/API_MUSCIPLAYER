package com.josecalvo.infrastructure.web.routes

import com.josecalvo.application.dto.request.CreateAlbumRequest
import com.josecalvo.application.dto.request.UpdateAlbumRequest
import com.josecalvo.application.service.AlbumService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.albumRoutes(albumService: AlbumService) {
    route("/albumes") {

        post {
            val request = call.receive<CreateAlbumRequest>()
            val album = albumService.createAlbum(request)
            call.respond(HttpStatusCode.Created, album)
        }

        get {
            val albums = albumService.getAllAlbums()
            call.respond(HttpStatusCode.OK, albums)
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "\n" + "Parámetro de identificación faltante")
            )

            // Si se pasa ?includeTracks=true, retornar con tracks
            val includeTracks = call.request.queryParameters["includeTracks"]?.toBoolean() ?: false

            val response = if (includeTracks) {
                albumService.getAlbumWithTracks(id)
            } else {
                albumService.getAlbumById(id)
            }

            call.respond(HttpStatusCode.OK, response)
        }

        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Parámetro de identificación faltante")
            )

            val request = call.receive<UpdateAlbumRequest>()
            val album = albumService.updateAlbum(id, request)
            call.respond(HttpStatusCode.OK, album)
        }

        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Parámetro de identificación faltante")
            )

            albumService.deleteAlbum(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}