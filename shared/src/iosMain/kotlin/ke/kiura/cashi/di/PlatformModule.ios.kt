package ke.kiura.cashi.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import ke.kiura.cashi.db.AppDatabase
import ke.kiura.cashi.remote.RemoteApi
import ke.kiura.cashi.remote.RemoteApiImpl
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

/**
 * iOS-specific Koin module
 * Provides platform-specific dependencies like Room database and RemoteApi
 */
actual val platformModule: Module = module {
    // Room Database
    single<AppDatabase> {
        val dbFilePath = NSHomeDirectory() + "/cashi_database.db"
        Room.databaseBuilder<AppDatabase>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .build()
    }

    single { get<AppDatabase>().transactionDao() }

    // Remote API
    single<RemoteApi> {
        RemoteApiImpl()
    }
}
