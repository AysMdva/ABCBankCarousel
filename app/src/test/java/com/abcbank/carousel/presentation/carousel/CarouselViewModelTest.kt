package com.abcbank.carousel.presentation.carousel

import com.abcbank.carousel.domain.model.ListItem
import com.abcbank.carousel.domain.model.PageData
import com.abcbank.carousel.domain.repository.CarouselRepository
import com.abcbank.carousel.domain.usecase.CalculateStatisticsUseCase
import com.abcbank.carousel.domain.usecase.FilterItemsUseCase
import com.abcbank.carousel.testutil.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CarouselViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads content on init`() = runTest {
        val viewModel = buildViewModel(
            repository = FakeCarouselRepository { samplePages }
        )

        advanceUntilIdle()

        assertTrue(viewModel.state.value.contentState is CarouselContentState.Content)
        assertEquals(0, viewModel.state.value.currentPage)
        assertEquals(samplePages.first().items, viewModel.state.value.filteredItems)
        assertEquals(samplePages.size, viewModel.state.value.statistics.size)
    }

    @Test
    fun `page changed clamps to valid page and reapplies active query`() = runTest {
        val viewModel = buildViewModel(
            repository = FakeCarouselRepository { samplePages }
        )
        advanceUntilIdle()

        viewModel.onIntent(CarouselIntent.SearchQueryChanged("berry"))
        viewModel.onIntent(CarouselIntent.PageChanged(99))
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.currentPage)
        assertEquals(listOf(samplePages[1].items.first()), viewModel.state.value.filteredItems)
    }

    @Test
    fun `search with no results switches to empty state`() = runTest {
        val viewModel = buildViewModel(
            repository = FakeCarouselRepository { samplePages }
        )
        advanceUntilIdle()

        viewModel.onIntent(CarouselIntent.SearchQueryChanged("mortgage"))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.contentState is CarouselContentState.Empty)
        assertEquals(CarouselEmptyReason.NO_SEARCH_RESULTS, viewModel.state.value.emptyReason)
        assertEquals(samplePages, viewModel.state.value.pages)
        assertTrue(viewModel.state.value.filteredItems.isEmpty())
    }

    @Test
    fun `empty repository switches to no pages state`() = runTest {
        val viewModel = buildViewModel(
            repository = FakeCarouselRepository { emptyList() }
        )
        advanceUntilIdle()

        assertTrue(viewModel.state.value.contentState is CarouselContentState.Empty)
        assertEquals(CarouselEmptyReason.NO_PAGES, viewModel.state.value.emptyReason)
        assertFalse(viewModel.state.value.canShowStatistics)
    }

    @Test
    fun `load failure switches to error state and emits effect`() = runTest {
        val viewModel = buildViewModel(
            repository = FakeCarouselRepository { error("boom") }
        )
        advanceUntilIdle()

        assertTrue(viewModel.state.value.contentState is CarouselContentState.Error)
        assertEquals(CarouselEffect.ShowLoadError, viewModel.effects.first())
    }

    private fun buildViewModel(
        repository: CarouselRepository
    ): CarouselViewModel {
        return CarouselViewModel(
            repository = repository,
            calculateStatisticsUseCase = CalculateStatisticsUseCase(),
            filterItemsUseCase = FilterItemsUseCase()
        )
    }

    private class FakeCarouselRepository(
        private val loader: suspend () -> List<PageData>
    ) : CarouselRepository {

        override suspend fun getPages(): List<PageData> = loader()
    }

    private companion object {
        val samplePages = listOf(
            PageData(
                id = 0,
                imageRes = 1,
                items = listOf(
                    ListItem("1", "Savings", "apple banana", 1),
                    ListItem("2", "Insurance", "orange grape", 1)
                )
            ),
            PageData(
                id = 1,
                imageRes = 2,
                items = listOf(
                    ListItem("3", "Travel", "berry rewards", 2),
                    ListItem("4", "Benefits", "melon cashback", 2)
                )
            )
        )
    }
}
