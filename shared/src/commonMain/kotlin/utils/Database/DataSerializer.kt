package utils.Database

import JsonFeature
import api.logger
import by
import com.kotlinorm.Kronos
import com.kotlinorm.interfaces.KPojo
import com.kotlinorm.orm.database.table
import com.kotlinorm.orm.insert.InsertClause.Companion.execute
import com.kotlinorm.orm.insert.insert
import config.SQLiteKronosConfig
import getJsonArray
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import parseObject
import java.nio.file.Path
import kotlin.io.path.pathString

inline fun <reified T> Path.fetchFromJson(key: String): List<T> {
    this.toFile()
        .bufferedReader()
        .readText()
        .parseObject()
        .getJsonArray(key)?.let { entries ->
            return entries.map {
                Json.by(
                    JsonFeature.EncodeDefaults,
                    JsonFeature.IgnoreUnknownKeys,
                ).decodeFromJsonElement<T>(it)
            }
        } ?: logger.error("Failed to fetch data from $this")
    return emptyList()
}

inline fun <reified T : KPojo> List<T>.new(filePath: Path) {
    SQLiteKronosConfig(filePath.pathString)
    Kronos.dataSource.table.syncTable<T>()
    this.insert().execute()
}
