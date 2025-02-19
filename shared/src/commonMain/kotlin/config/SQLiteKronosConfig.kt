package config

import com.kotlinorm.Kronos
import com.kotlinorm.KronosBasicWrapper
import org.apache.commons.dbcp2.BasicDataSource

class SQLiteKronosConfig(private val path: String) {
    init {
        Kronos.init {
            dataSource = {
                KronosBasicWrapper(
                    BasicDataSource().apply {
                        driverClassName = "org.sqlite.JDBC"
                        url = "jdbc:sqlite:$path"
                    }
                )
            }
        }
    }
}
