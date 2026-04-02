package com.abcbank.carousel.presentation.xml

import com.abcbank.carousel.domain.model.ListItem
import com.abcbank.carousel.domain.model.PageData
import com.abcbank.carousel.domain.repository.CarouselRepository
import com.abcbank.carousel.domain.usecase.CalculateStatisticsUseCase
import com.abcbank.carousel.domain.usecase.FilterItemsUseCase
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlContentState
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlEffect
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlEmptyReason
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlIntent
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
class CarouselXmlViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads content when load intent is received`() = runTest {
        val viewModel = buildViewModel(
            repository = FakeCarouselRepository { samplePages }
        )

        viewModel.onIntent(CarouselXmlIntent.LoadData)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.contentState is CarouselXmlContentState.Content)
        assertEquals(samplePages.first().items, viewModel.state.value.filteredItems)
        assertTrue(viewModel.state.value.canShowStatistics)
    }

    @Test
    fun `page changed clamps to valid page and reapplies active query`() = runTest {
        val viewModel = buildViewModel(
            repository = FakeCarouselRepository { samplePages }
        )
        viewModel.onIntent(CarouselXmlIntent.LoadData)
        advanceUntilIdle()

        viewModel.onIntent(CarouselXmlIntent.SearchQueryChanged("berry"))
        viewModel.onIntent(CarouselXmlIntent.PageChanged(99))
        advanceUntilIdle()

        assertEquals(1, viewModel.state.value.currentPage)
        assertEquals(listOf(samplePages[1].items.first()), viewModel.state.value.filteredItems)
        assertEquals(CarouselXmlEffect.ScrollItemsToTop, viewModel.effects.first())
    }

    @Test
    fun `search with no results switches to empty state`() = runTest {
        val viewModel = buildViewModel(
            repository = FakeCarouselRepository { samplePages }
        )
        viewModel.onIntent(CarouselXmlIntent.LoadData)
        advanceUntilIdle()

        viewModel.onIntent(CarouselXmlIntent.SearchQueryChanged("mortgage"))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.contentState is CarouselXmlContentState.Empty)
        assertEquals(CarouselXmlEmptyReason.NO_SEARCH_RESULTS, viewModel.state.value.emptyReason)
    }

    @Test
    fun `empty repository switches to no pages state`() = runTest {
        val viewModel = buildViewModel(
            repository = FakeCarouselRepository { emptyList() }
        )
        viewModel.onIntent(CarouselXmlIntent.LoadData)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.contentState is CarouselXmlContentState.Empty)
        assertEquals(CarouselXmlEmptyReason.NO_PAGES, viewModel.state.value.emptyReason)
        assertFalse(viewModel.state.value.canShowStatistics)
    }

    @Test
    fun `load failure switches to error state and emits effect`() = runTest {
        val viewModel = buildViewModel(
            repository = FakeCarouselRepository { error("boom") }
        )
        viewModel.onIntent(CarouselXmlIntent.LoadData)
        advanceUntilIdle()

        assertTrue(viewModel.state.value.contentState is CarouselXmlContentState.Error)
        assertEquals(CarouselXmlEffect.ShowLoadError, viewModel.effects.first())
    }

    private fun buildViewModel(
        repository: CarouselRepository
    ): CarouselXmlViewModel {
        return CarouselXmlViewModel(
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
