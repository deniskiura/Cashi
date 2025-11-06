package ke.kiura.cashi.di

import android.content.Context
import androidx.room.Room
import ke.kiura.cashi.db.AppDatabase
import ke.kiura.cashi.remote.RemoteApi
import ke.kiura.cashi.remote.RemoteApiImpl
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Android-specific Koin module
 * Provides platform-specific dependencies like Room database and RemoteApi
 */
actual val platformModule: Module = module {
    // Room Database
    single<AppDatabase> {
        val context = get<Context>()
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cashi_database"
        ).build()
    }

    single { get<AppDatabase>().transactionDao() }

    // Remote API
    single<RemoteApi> {
        RemoteApiImpl()
    }
}
