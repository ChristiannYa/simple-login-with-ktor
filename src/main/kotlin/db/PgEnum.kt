package com.example.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.postgresql.util.PGobject

// Custom column type for PostgresSQL ENUM
class PgEnum<T : Enum<T>>(
    private val enumClass: Class<T>,
    private val dbTypeName: String
) : ColumnType<T>() {
    override fun sqlType(): String = dbTypeName

    override fun valueFromDB(value: Any): T {
        if (enumClass.isInstance(value)) {
            @Suppress("UNCHECKED_CAST")
            return value as T
        }

        return when (value) {
            // From database as PGobject
            is PGobject -> enumClass.enumConstants.first {
                it.name.lowercase() == value.value
            }

            // From database as String
            is String -> enumClass.enumConstants.first {
                it.name.lowercase() == value
            }

            else -> {
                error("Unexpected value: $value of type ${value::class.qualifiedName}")
            }
        }
    }

    // Kotlin Enum -> Db type
    override fun notNullValueToDB(value: T): Any {
        return PGobject().apply {
            type = dbTypeName
            this.value = value.name.lowercase()
        }
    }
}

// Extension function to use the enum
inline fun <reified T : Enum<T>> Table.pgEnum(
    name: String,
    dbTypeName: String
): Column<T> = registerColumn(name, PgEnum(T::class.java, dbTypeName))