package com.josecalvo.infrastructure.database.repository

import com.josecalvo.domain.model.Artist
import com.josecalvo.domain.repository.IArtistRepository
import com.josecalvo.infrastructure.database.DatabaseFactory.dbQuery
import com.josecalvo.infrastructure.database.table.ArtistasTable
import com.josecalvo.infrastructure.database.table.AlbumesTable
import org.jetbrains.exposed.sql.*
import java.time.Instant
import java.util.UUID
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class ArtistRepositoryImpl : IArtistRepository {

    override suspend fun create(artist: Artist): Artist = dbQuery {
        ArtistasTable.insert {
            it[id] = artist.id
            it[name] = artist.name
            it[genre] = artist.genre
            it[createdAt] = artist.createdAt
            it[updatedAt] = artist.updatedAt
        }
        artist
    }

    override suspend fun findById(id: UUID): Artist? = dbQuery {
        ArtistasTable
            .selectAll()
            .where { ArtistasTable.id eq id }
            .map { rowToArtist(it) }
            .singleOrNull()
    }

    override suspend fun findAll(): List<Artist> = dbQuery {
        ArtistasTable
            .selectAll()
            .map { rowToArtist(it) }
    }

    override suspend fun update(id: UUID, artist: Artist): Artist? = dbQuery {
        val updated = ArtistasTable.update(
            where = { ArtistasTable.id eq id }
        ) {
            it[name] = artist.name
            it[genre] = artist.genre
            it[updatedAt] = Instant.now()
        }

        if (updated > 0) findById(id) else null
    }

    override suspend fun delete(id: UUID): Boolean = dbQuery {
        val deleted = ArtistasTable.deleteWhere() { ArtistasTable.id eq id }
        deleted > 0
    }

    override suspend fun hasAlbums(artistId: UUID): Boolean = dbQuery {
        AlbumesTable
            .selectAll()
            .where { AlbumesTable.artistId eq artistId }
            .count() > 0
    }

    private fun rowToArtist(row: ResultRow): Artist {
        return Artist(
            id = row[ArtistasTable.id],
            name = row[ArtistasTable.name],
            genre = row[ArtistasTable.genre],
            createdAt = row[ArtistasTable.createdAt],
            updatedAt = row[ArtistasTable.updatedAt]
        )
    }
}