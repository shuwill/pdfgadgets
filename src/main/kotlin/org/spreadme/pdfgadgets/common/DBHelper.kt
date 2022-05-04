package org.spreadme.pdfgadgets.common

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DBHelper {

    fun help(name: String): DBHelper {
        Database.connect("jdbc:sqlite:$name")
        return this
    }

    fun createTable(table: Table): DBHelper {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(table)
        }
        return this
    }

}