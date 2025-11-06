package ke.kiura.cashi.di

import ke.kiura.cashi.presentation.history.TransactionHistoryViewModel
import ke.kiura.cashi.presentation.sending.SendPaymentViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { SendPaymentViewModel(sendPaymentUseCase = get()) }
    viewModel { TransactionHistoryViewModel(getTransactionsUseCase = get()) }
}
