package com.josecalvo.infrastructure.web.routes

import com.josecalvo.application.dto.request.CreateTrackRequest
import com.josecalvo.application.dto.request.UpdateTrackRequest
import com.josecalvo.application.service.TrackService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.trackRoutes(trackService: TrackService) {
    route("/tracks") {

        post {
            val request = call.receive<CreateTrackRequest>()
            val track = trackService.createTrack(request)
            call.respond(HttpStatusCode.Created, track)
        }

        get {
            val tracks = trackService.getAllTracks()
            call.respond(HttpStatusCode.OK, tracks)
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Parámetro de identificación faltante")
            )

            val track = trackService.getTrackById(id)
            call.respond(HttpStatusCode.OK, track)
        }

        put("/{id}") {
            val id = call.parameters["id"] ?: return@put call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Parámetro de identificación faltante")
            )

            val request = call.receive<UpdateTrackRequest>()
            val track = trackService.updateTrack(id, request)
            call.respond(HttpStatusCode.OK, track)
        }

        delete("/{id}") {
            val id = call.parameters["id"] ?: return@delete call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "\n" + "Parámetro de identificación faltante")
            )

            trackService.deleteTrack(id)
            call.respond(HttpStatusCode.NoContent)
        }
    }
}