package com.abcbank.carousel.presentation.xml

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.abcbank.carousel.domain.repository.CarouselRepository
import com.abcbank.carousel.domain.usecase.CalculateStatisticsUseCase
import com.abcbank.carousel.domain.usecase.FilterItemsUseCase
import com.abcbank.carousel.presentation.xml.model.toSheetItem
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlContentState
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlEffect
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlEmptyReason
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlIntent
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CarouselXmlViewModel(
    private val repository: CarouselRepository,
    private val calculateStatisticsUseCase: CalculateStatisticsUseCase,
    private val filterItemsUseCase: FilterItemsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CarouselXmlState())
    val state: StateFlow<CarouselXmlState> = _state.asStateFlow()

    private val effectChannel = Channel<CarouselXmlEffect>(Channel.BUFFERED)
    val effects: Flow<CarouselXmlEffect> = effectChannel.receiveAsFlow()

    fun onIntent(intent: CarouselXmlIntent) {
        when (intent) {
            CarouselXmlIntent.LoadData -> loadData()
            CarouselXmlIntent.RetryLoadClicked -> loadData(forceRefresh = true)
            is CarouselXmlIntent.PageChanged -> updateCurrentPage(intent.page)
            is CarouselXmlIntent.SearchQueryChanged -> updateSearchQuery(intent.query)
            CarouselXmlIntent.StatisticsClicked -> showStatistics()
        }
    }

    private fun loadData(forceRefresh: Boolean = false) {
        if (!forceRefresh && state.value.hasResolvedInitialLoad) return

        viewModelScope.launch {
            reduce {
                copy(contentState = CarouselXmlContentState.Loading)
            }

            runCatching { repository.getPages() }
                .onSuccess { pages ->
                    reduce {
                        copy(
                            contentState = resolveContentState(
                                pages = pages,
                                currentPage = 0,
                                query = ""
                            ),
                            currentPage = 0,
                            searchQuery = ""
                        )
                    }
                }
                .onFailure {
                    reduce {
                        copy(contentState = CarouselXmlContentState.Error)
                    }
                    effectChannel.trySend(CarouselXmlEffect.ShowLoadError)
                }
        }
    }

    private fun updateCurrentPage(page: Int) {
        val currentState = state.value
        if (currentState.pageCount == 0) return

        val safePage = page.coerceIn(0, currentState.pageCount - 1)

        reduce {
            copy(
                currentPage = safePage,
                contentState = resolveContentState(
                    pages = pages,
                    currentPage = safePage,
                    query = searchQuery
                )
            )
        }
        effectChannel.trySend(CarouselXmlEffect.ScrollItemsToTop)
    }

    private fun updateSearchQuery(query: String) {
        if (state.value.pageCount == 0) return

        reduce {
            copy(
                searchQuery = query,
                contentState = resolveContentState(
                    pages = pages,
                    currentPage = currentPage,
                    query = query
                )
            )
        }
    }

    private fun showStatistics() {
        if (state.value.pages.isEmpty()) return

        val statistics = calculateStatisticsUseCase(state.value.pages).map { it.toSheetItem() }
        effectChannel.trySend(CarouselXmlEffect.ShowStatistics(statistics))
    }

    private fun resolveContentState(
        pages: List<com.abcbank.carousel.domain.model.PageData>,
        currentPage: Int,
        query: String
    ): CarouselXmlContentState {
        if (pages.isEmpty()) {
            return CarouselXmlContentState.Empty(
                pages = emptyList(),
                reason = CarouselXmlEmptyReason.NO_PAGES
            )
        }

        val safePage = currentPage.coerceIn(0, pages.lastIndex)
        val filteredItems = filterItemsUseCase(pages[safePage].items, query)

        return if (filteredItems.isEmpty()) {
            CarouselXmlContentState.Empty(
                pages = pages,
                reason = CarouselXmlEmptyReason.NO_SEARCH_RESULTS
            )
        } else {
            CarouselXmlContentState.Content(
                pages = pages,
                filteredItems = filteredItems
            )
        }
    }

    private fun reduce(reducer: CarouselXmlState.() -> CarouselXmlState) {
        _state.update { currentState ->
            currentState.reducer()
        }
    }

    companion object {
        fun factory(
            repository: CarouselRepository,
            calculateStatisticsUseCase: CalculateStatisticsUseCase = CalculateStatisticsUseCase(),
            filterItemsUseCase: FilterItemsUseCase = FilterItemsUseCase()
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                CarouselXmlViewModel(
                    repository = repository,
                    calculateStatisticsUseCase = calculateStatisticsUseCase,
                    filterItemsUseCase = filterItemsUseCase
                )
            }
        }
    }
}
