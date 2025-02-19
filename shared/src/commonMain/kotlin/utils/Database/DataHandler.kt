package utils.Database

import com.kotlinorm.Kronos
import com.kotlinorm.interfaces.KPojo
import com.kotlinorm.orm.database.table
import com.kotlinorm.orm.delete.DeleteClause.Companion.execute
import com.kotlinorm.orm.delete.delete
import com.kotlinorm.orm.insert.InsertClause.Companion.execute
import com.kotlinorm.orm.insert.insert
import com.kotlinorm.orm.update.update

object DataHandler {
    private val table = Kronos.dataSource.table

    inline fun <reified T : KPojo> insert(vararg kPojo: T) {
        listOf(*kPojo).insert().execute()
    }

    inline fun <reified T : KPojo> delete(vararg kPojo: T) {
        listOf(*kPojo).delete().execute()
    }

    inline fun <reified T : KPojo> update(kPojo: T, key: String, value: Any) {
        kPojo.update().set { it[key] = value }.execute()
    }
}