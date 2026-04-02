package com.abcbank.carousel.presentation.carousel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.abcbank.carousel.domain.repository.CarouselRepository
import com.abcbank.carousel.domain.usecase.CalculateStatisticsUseCase
import com.abcbank.carousel.domain.usecase.FilterItemsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CarouselViewModel(
    private val repository: CarouselRepository,
    private val calculateStatisticsUseCase: CalculateStatisticsUseCase,
    private val filterItemsUseCase: FilterItemsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CarouselState())
    val state: StateFlow<CarouselState> = _state.asStateFlow()

    private val effectChannel = Channel<CarouselEffect>(Channel.BUFFERED)
    val effects: Flow<CarouselEffect> = effectChannel.receiveAsFlow()

    init {
        onIntent(CarouselIntent.LoadData)
    }

    fun onIntent(intent: CarouselIntent) {
        when (intent) {
            CarouselIntent.LoadData -> loadData()
            CarouselIntent.RetryLoadClicked -> loadData(forceRefresh = true)
            is CarouselIntent.PageChanged -> updateCurrentPage(intent.page)
            is CarouselIntent.SearchQueryChanged -> updateSearchQuery(intent.query)
            CarouselIntent.StatisticsClicked -> reduce {
                if (canShowStatistics) {
                    copy(isStatisticsVisible = true)
                } else {
                    this
                }
            }
            CarouselIntent.StatisticsDismissed -> reduce {
                copy(isStatisticsVisible = false)
            }
        }
    }

    private fun loadData(forceRefresh: Boolean = false) {
        val currentState = state.value
        if (!forceRefresh && currentState.hasResolvedInitialLoad) return

        viewModelScope.launch {
            reduce {
                copy(
                    contentState = CarouselContentState.Loading,
                    isStatisticsVisible = false
                )
            }

            runCatching { repository.getPages() }
                .onSuccess { pages ->
                    val statistics = calculateStatisticsUseCase(pages)
                    reduce {
                        copy(
                            contentState = resolveContentState(
                                pages = pages,
                                currentPage = 0,
                                query = ""
                            ),
                            currentPage = 0,
                            searchQuery = "",
                            statistics = statistics
                        )
                    }
                }
                .onFailure {
                    reduce {
                        copy(
                            contentState = CarouselContentState.Error,
                            statistics = emptyList(),
                            isStatisticsVisible = false
                        )
                    }
                    effectChannel.send(CarouselEffect.ShowLoadError)
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
    }

    private fun updateSearchQuery(query: String) {
        val currentState = state.value
        if (currentState.pageCount == 0) return

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

    private fun resolveContentState(
        pages: List<com.abcbank.carousel.domain.model.PageData>,
        currentPage: Int,
        query: String
    ): CarouselContentState {
        if (pages.isEmpty()) {
            return CarouselContentState.Empty(
                pages = emptyList(),
                reason = CarouselEmptyReason.NO_PAGES
            )
        }

        val safePage = currentPage.coerceIn(0, pages.lastIndex)
        val filteredItems = filterItemsUseCase(pages[safePage].items, query)

        return if (filteredItems.isEmpty()) {
            CarouselContentState.Empty(
                pages = pages,
                reason = CarouselEmptyReason.NO_SEARCH_RESULTS
            )
        } else {
            CarouselContentState.Content(
                pages = pages,
                filteredItems = filteredItems
            )
        }
    }

    private fun reduce(reducer: CarouselState.() -> CarouselState) {
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
                CarouselViewModel(
                    repository = repository,
                    calculateStatisticsUseCase = calculateStatisticsUseCase,
                    filterItemsUseCase = filterItemsUseCase
                )
            }
        }
    }
}
