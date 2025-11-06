package ke.kiura.cashi.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import ke.kiura.cashi.db.dao.TransactionDao
import ke.kiura.cashi.db.entity.TransactionEntity

@Database(entities = [TransactionEntity::class], version = 1)
@ConstructedBy(CashiDatabaseConstructor::class)
abstract class CashiDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object CashiDatabaseConstructor : RoomDatabaseConstructor<CashiDatabase> {
    override fun initialize(): CashiDatabase
}