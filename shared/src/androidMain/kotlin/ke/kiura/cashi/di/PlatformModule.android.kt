package ke.kiura.cashi.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import ke.kiura.cashi.db.AppDatabase
import ke.kiura.cashi.remote.FirebaseRemoteApi
import ke.kiura.cashi.remote.RemoteApi
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

    // Firebase
    single<FirebaseFirestore> {
        FirebaseFirestore.getInstance()
    }

    // Remote API - Using Firebase
    single<RemoteApi> {
        FirebaseRemoteApi(get())
    }
}
