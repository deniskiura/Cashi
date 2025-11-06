package ke.kiura.cashi.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

/**
 * Initializer for Koin dependency injection
 * Call this from the platform-specific application entry point
 */
object KoinInitializer {
    fun init(appDeclaration: KoinAppDeclaration = {}) {
        startKoin {
            appDeclaration()
            modules(sharedModule, platformModule)
        }
    }
}
