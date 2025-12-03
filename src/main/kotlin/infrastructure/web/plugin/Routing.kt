package com.josecalvo.infrastructure.web.plugin

import com.josecalvo.application.service.AlbumService
import com.josecalvo.application.service.ArtistService
import com.josecalvo.application.service.TrackService
import com.josecalvo.infrastructure.database.repository.AlbumRepositoryImpl
import com.josecalvo.infrastructure.database.repository.ArtistRepositoryImpl
import com.josecalvo.infrastructure.database.repository.TrackRepositoryImpl
import com.josecalvo.infrastructure.web.routes.albumRoutes
import com.josecalvo.infrastructure.web.routes.artistRoutes
import com.josecalvo.infrastructure.web.routes.trackRoutes
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val artistRepository = ArtistRepositoryImpl()
    val albumRepository = AlbumRepositoryImpl()
    val trackRepository = TrackRepositoryImpl()

    val artistService = ArtistService(artistRepository, albumRepository)
    val albumService = AlbumService(albumRepository, artistRepository, trackRepository)
    val trackService = TrackService(trackRepository, albumRepository)

    routing {
        route("/api") {
            artistRoutes(artistService)
            albumRoutes(albumService)
            trackRoutes(trackService)
        }
    }
}