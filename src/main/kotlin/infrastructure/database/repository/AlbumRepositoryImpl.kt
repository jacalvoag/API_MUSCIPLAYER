package com.josecalvo.infrastructure.database.repository

import com.josecalvo.domain.model.Album
import com.josecalvo.domain.repository.IAlbumRepository
import com.josecalvo.infrastructure.database.DatabaseFactory.dbQuery
import com.josecalvo.infrastructure.database.table.AlbumesTable
import com.josecalvo.infrastructure.database.table.TracksTable
import org.jetbrains.exposed.sql.*
import java.time.Instant
import java.util.UUID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class AlbumRepositoryImpl : IAlbumRepository {

    override suspend fun create(album: Album): Album = dbQuery {
        AlbumesTable.insert {
            it[id] = album.id
            it[title] = album.title
            it[releaseYear] = album.releaseYear
            it[artistId] = album.artistId
            it[createdAt] = album.createdAt
            it[updatedAt] = album.updatedAt
        }
        album
    }

    override suspend fun findById(id: UUID): Album? = dbQuery {
        AlbumesTable
            .selectAll()
            .where { AlbumesTable.id eq id }
            .map { rowToAlbum(it) }
            .singleOrNull()
    }

    override suspend fun findAll(): List<Album> = dbQuery {
        AlbumesTable
            .selectAll()
            .map { rowToAlbum(it) }
    }

    override suspend fun findByArtistId(artistId: UUID): List<Album> = dbQuery {
        AlbumesTable
            .selectAll()
            .where { AlbumesTable.artistId eq artistId }
            .map { rowToAlbum(it) }
    }

    override suspend fun update(id: UUID, album: Album): Album? = dbQuery {
        val updated = AlbumesTable.update(
            where = { AlbumesTable.id eq id }
        ) {
            it[title] = album.title
            it[releaseYear] = album.releaseYear
            it[updatedAt] = Instant.now()
        }

        if (updated > 0) findById(id) else null
    }

    override suspend fun delete(id: UUID): Boolean = dbQuery {
        val deleted = AlbumesTable.deleteWhere() { AlbumesTable.id eq id }
        deleted > 0
    }

    override suspend fun hasTracks(albumId: UUID): Boolean = dbQuery {
        TracksTable
            .selectAll()
            .where { TracksTable.albumId eq albumId }
            .count() > 0
    }

    private fun rowToAlbum(row: ResultRow): Album {
        return Album(
            id = row[AlbumesTable.id],
            title = row[AlbumesTable.title],
            releaseYear = row[AlbumesTable.releaseYear],
            artistId = row[AlbumesTable.artistId],
            createdAt = row[AlbumesTable.createdAt],
            updatedAt = row[AlbumesTable.updatedAt]
        )
    }
}