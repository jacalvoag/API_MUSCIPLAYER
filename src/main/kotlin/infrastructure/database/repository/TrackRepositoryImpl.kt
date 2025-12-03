package com.josecalvo.infrastructure.database.repository

import com.josecalvo.domain.model.Track
import com.josecalvo.domain.repository.ITrackRepository
import com.josecalvo.infrastructure.database.DatabaseFactory.dbQuery
import com.josecalvo.infrastructure.database.table.TracksTable
import org.jetbrains.exposed.sql.*
import java.time.Instant
import java.util.UUID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class TrackRepositoryImpl : ITrackRepository {

    override suspend fun create(track: Track): Track = dbQuery {
        TracksTable.insert {
            it[id] = track.id
            it[title] = track.title
            it[duration] = track.duration
            it[albumId] = track.albumId
            it[createdAt] = track.createdAt
            it[updatedAt] = track.updatedAt
        }
        track
    }

    override suspend fun findById(id: UUID): Track? = dbQuery {
        TracksTable
            .selectAll()
            .where { TracksTable.id eq id }
            .map { rowToTrack(it) }
            .singleOrNull()
    }

    override suspend fun findAll(): List<Track> = dbQuery {
        TracksTable
            .selectAll()
            .map { rowToTrack(it) }
    }

    override suspend fun findByAlbumId(albumId: UUID): List<Track> = dbQuery {
        TracksTable
            .selectAll()
            .where { TracksTable.albumId eq albumId }
            .map { rowToTrack(it) }
    }

    override suspend fun update(id: UUID, track: Track): Track? = dbQuery {
        val updated = TracksTable.update(
            where = { TracksTable.id eq id }
        ) {
            it[title] = track.title
            it[duration] = track.duration
            it[updatedAt] = Instant.now()
        }

        if (updated > 0) findById(id) else null
    }

    override suspend fun delete(id: UUID): Boolean = dbQuery {
        val deleted = TracksTable.deleteWhere() { TracksTable.id eq id }
        deleted > 0
    }

    private fun rowToTrack(row: ResultRow): Track {
        return Track(
            id = row[TracksTable.id],
            title = row[TracksTable.title],
            duration = row[TracksTable.duration],
            albumId = row[TracksTable.albumId],
            createdAt = row[TracksTable.createdAt],
            updatedAt = row[TracksTable.updatedAt]
        )
    }
}