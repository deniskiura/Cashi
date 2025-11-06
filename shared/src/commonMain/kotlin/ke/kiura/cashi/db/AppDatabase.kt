package ke.kiura.cashi.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ke.kiura.cashi.db.dao.TransactionDao
import ke.kiura.cashi.db.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}
