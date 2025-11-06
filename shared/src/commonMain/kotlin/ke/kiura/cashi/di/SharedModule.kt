package ke.kiura.cashi.di

import ke.kiura.cashi.data.repository.TransactionRepositoryImpl
import ke.kiura.cashi.data.repository.payment.PaymentRepositoryImpl
import ke.kiura.cashi.domain.repository.PaymentRepository
import ke.kiura.cashi.domain.repository.TransactionRepository
import ke.kiura.cashi.domain.usecase.GetTransactionsUseCase
import ke.kiura.cashi.domain.usecase.SendPaymentUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for shared dependencies
 * Provides all dependencies needed across platforms
 */
val sharedModule = module {
    // Note: RemoteApi and TransactionDao are provided by platformModule (platform-specific)

    // Repositories
    single<PaymentRepository> {
        PaymentRepositoryImpl(
            remoteApi = get(),
            transactionDao = get()
        )
    }

    single<TransactionRepository> {
        TransactionRepositoryImpl(
            transactionDao = get()
        )
    }

    // Use Cases
    factoryOf(::SendPaymentUseCase)
    factoryOf(::GetTransactionsUseCase)
}

/**
 * Platform-specific modules
 * Can be extended by each platform for platform-specific dependencies
 */
expect val platformModule: Module
