package com.ovais.ndjsonparser.di

import com.ovais.ndjsonparser.data.repository.NDJsonRepositoryImpl
import com.ovais.ndjsonparser.domain.repository.NDJsonRepository
import com.ovais.ndjsonparser.domain.usecase.ConvertToCsvUseCase
import com.ovais.ndjsonparser.domain.usecase.ConvertToJsonUseCase
import com.ovais.ndjsonparser.domain.usecase.DownloadFileUseCase
import com.ovais.ndjsonparser.domain.usecase.FilterByKeyUseCase
import com.ovais.ndjsonparser.domain.usecase.ParseNDJsonUseCase
import com.ovais.ndjsonparser.presentation.viewmodel.NDJsonViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin dependency injection modules
 * Follows Dependency Inversion Principle (SOLID)
 */
val appModule = module {
    // Repository
    single<NDJsonRepository> {
        NDJsonRepositoryImpl(androidContext())
    }
    
    // Use Cases
    factory { ParseNDJsonUseCase(get()) }
    factory { ConvertToJsonUseCase(get()) }
    factory { ConvertToCsvUseCase(get()) }
    factory { FilterByKeyUseCase(get()) }
    factory { 
        DownloadFileUseCase(
            repository = get(),
            convertToJsonUseCase = get(),
            convertToCsvUseCase = get()
        )
    }
    
    // ViewModel
    viewModel {
        NDJsonViewModel(
            parseNDJsonUseCase = get(),
            convertToJsonUseCase = get(),
            convertToCsvUseCase = get(),
            filterByKeyUseCase = get(),
            downloadFileUseCase = get()
        )
    }
}
