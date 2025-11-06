package ke.kiura.cashi

import android.app.Application
import ke.kiura.cashi.di.platformModule
import ke.kiura.cashi.di.presentationModule
import ke.kiura.cashi.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CashiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@CashiApplication)
            modules(platformModule, sharedModule, presentationModule)
        }
    }
}