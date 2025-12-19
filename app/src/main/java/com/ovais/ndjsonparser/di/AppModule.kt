package com.ovais.ndjsonparser.di

import android.content.Context
import com.ovais.ndjsonparser.data.repository.NDJsonRepositoryImpl
import com.ovais.ndjsonparser.domain.repository.NDJsonRepository
import com.ovais.ndjsonparser.domain.usecase.ConvertToCsvUseCase
import com.ovais.ndjsonparser.domain.usecase.ConvertToJsonUseCase
import com.ovais.ndjsonparser.domain.usecase.DownloadFileUseCase
import com.ovais.ndjsonparser.domain.usecase.FilterByKeyUseCase
import com.ovais.ndjsonparser.domain.usecase.ParseNDJsonUseCase
import com.ovais.ndjsonparser.presentation.viewmodel.NDJsonViewModel

/**
 * Dependency injection module
 * Follows Dependency Inversion Principle (SOLID)
 */
object AppModule {
    
    fun provideNDJsonRepository(
        context: Context
    ): NDJsonRepository {
        return NDJsonRepositoryImpl(context)
    }
    
    fun provideParseNDJsonUseCase(repository: NDJsonRepository): ParseNDJsonUseCase {
        return ParseNDJsonUseCase(repository)
    }
    
    fun provideConvertToJsonUseCase(repository: NDJsonRepository): ConvertToJsonUseCase {
        return ConvertToJsonUseCase(repository)
    }
    
    fun provideConvertToCsvUseCase(repository: NDJsonRepository): ConvertToCsvUseCase {
        return ConvertToCsvUseCase(repository)
    }
    
    fun provideFilterByKeyUseCase(repository: NDJsonRepository): FilterByKeyUseCase {
        return FilterByKeyUseCase(repository)
    }
    
    fun provideDownloadFileUseCase(
        repository: NDJsonRepository,
        convertToJsonUseCase: ConvertToJsonUseCase,
        convertToCsvUseCase: ConvertToCsvUseCase
    ): DownloadFileUseCase {
        return DownloadFileUseCase(repository, convertToJsonUseCase, convertToCsvUseCase)
    }
    
    fun provideNDJsonViewModel(
        context: Context
    ): NDJsonViewModel {
        val repository = provideNDJsonRepository(context)
        val parseNDJsonUseCase = provideParseNDJsonUseCase(repository)
        val convertToJsonUseCase = provideConvertToJsonUseCase(repository)
        val convertToCsvUseCase = provideConvertToCsvUseCase(repository)
        val filterByKeyUseCase = provideFilterByKeyUseCase(repository)
        val downloadFileUseCase = provideDownloadFileUseCase(
            repository,
            convertToJsonUseCase,
            convertToCsvUseCase
        )
        
        return NDJsonViewModel(
            parseNDJsonUseCase,
            convertToJsonUseCase,
            convertToCsvUseCase,
            filterByKeyUseCase,
            downloadFileUseCase
        )
    }
}

