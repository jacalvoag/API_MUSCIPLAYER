package com.josecalvo.infrastructure.web.routes

import com.josecalvo.application.dto.request.CreateArtistRequest
import com.josecalvo.application.dto.request.UpdateArtistRequest
import com.josecalvo.application.service.ArtistService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.artistRoutes(artistService: ArtistService) {
    route("/artistas") {

        post {
            val request = call.receive<CreateArtistRequest>()
            val artist = artistService.createArtist(request)
            call.respond(HttpStatusCode.Created, artist)
        }

        get {
            val artists = artistService.getAllArtists()
            call.respond(HttpStatusCode.OK, artists)
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Parámetro de identificación faltante")
            )

            val includeAlbums = call.request.queryParameters["includeAlbums"]?.toBoolean() ?: false

            val response = if (includeAlbums) {
                artistService.getArtistWithAlbums(id)
            } else {
                artistService.getArtistById(id)
            }

            call.respond(HttpStatusCode.OK, response)
        }

        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Parámetro de identificación faltante")
            )

            val request = call.receive<UpdateArtistRequest>()
            val artist = artistService.updateArtist(id, request)
            call.respond(HttpStatusCode.OK, artist)
        }

        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Parámetro de identificación faltante")
            )

            artistService.deleteArtist(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}