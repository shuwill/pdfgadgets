package org.spreadme.pdfgadgets.config

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DBHelper {

    suspend fun help(name: String): DBHelper {
        withContext(Dispatchers.IO) {
            Database.connect("jdbc:sqlite:$name")
        }
        return this
    }

    suspend fun createTable(table: Table): DBHelper {
        withContext(Dispatchers.IO) {
            transaction {
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(table)
            }
        }
        return this
    }

}