package com.josecalvo.infrastructure.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import com.josecalvo.infrastructure.database.table.ArtistasTable
import com.josecalvo.infrastructure.database.table.AlbumesTable
import com.josecalvo.infrastructure.database.table.TracksTable

object DatabaseFactory {

    fun init(
        driver: String,
        url: String,
        user: String,
        password: String,
        maxPoolSize: Int = 10
    ) {
        val database = Database.connect(
            datasource = createHikariDataSource(
                driver = driver,
                url = url,
                user = user,
                password = password,
                maxPoolSize = maxPoolSize
            )
        )

        transaction(database) {
            SchemaUtils.create(ArtistasTable, AlbumesTable, TracksTable)
        }
    }

    private fun createHikariDataSource(
        driver: String,
        url: String,
        user: String,
        password: String,
        maxPoolSize: Int
    ): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = driver
            jdbcUrl = url
            username = user
            this.password = password
            maximumPoolSize = maxPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
