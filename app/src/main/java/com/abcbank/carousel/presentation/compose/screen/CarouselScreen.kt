@file:OptIn(ExperimentalFoundationApi::class)
package com.abcbank.carousel.presentation.compose.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.abcbank.carousel.R
import com.abcbank.carousel.presentation.carousel.CarouselContentState
import com.abcbank.carousel.presentation.carousel.CarouselEffect
import com.abcbank.carousel.presentation.carousel.CarouselEmptyReason
import com.abcbank.carousel.presentation.carousel.CarouselIntent
import com.abcbank.carousel.presentation.carousel.CarouselState
import com.abcbank.carousel.presentation.carousel.CarouselViewModel
import com.abcbank.carousel.presentation.compose.component.ImageCarousel
import com.abcbank.carousel.presentation.compose.component.ItemList
import com.abcbank.carousel.presentation.compose.component.ScreenStateMessage
import com.abcbank.carousel.presentation.compose.component.SearchBar
import com.abcbank.carousel.presentation.compose.component.StatisticsBottomSheet
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CarouselScreenRoute(
    viewModel: CarouselViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val loadErrorMessage = stringResource(R.string.carousel_load_error)

    LaunchedEffect(viewModel, snackbarHostState, loadErrorMessage) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                CarouselEffect.ShowLoadError -> snackbarHostState.showSnackbar(loadErrorMessage)
            }
        }
    }

    CarouselScreen(
        state = state,
        snackbarHostState = snackbarHostState,
        onIntent = viewModel::onIntent
    )
}

@Composable
fun CarouselScreen(
    state: CarouselState,
    snackbarHostState: SnackbarHostState,
    onIntent: (CarouselIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { state.pageCount })
    val listState = rememberLazyListState()
    val searchBarVerticalPadding = dimensionResource(R.dimen.spacing_medium)
    val fabBottomSpacing = dimensionResource(R.dimen.fab_list_spacing_bottom)
    val errorTitle = stringResource(R.string.carousel_error_title)
    val errorMessage = stringResource(R.string.carousel_error_message)
    val retryLabel = stringResource(R.string.retry)
    val noDataTitle = stringResource(R.string.carousel_empty_title)
    val noDataMessage = stringResource(R.string.carousel_empty_message)
    val noSearchResultsTitle = stringResource(R.string.carousel_no_results_title)
    val noSearchResultsMessage = stringResource(R.string.carousel_no_results_message)

    LaunchedEffect(pagerState.currentPage) {
        if (state.pageCount > 0 && pagerState.currentPage != state.currentPage) {
            onIntent(CarouselIntent.PageChanged(pagerState.currentPage))
            listState.scrollToItem(0)
        }
    }

    LaunchedEffect(state.currentPage, state.pageCount) {
        if (state.pageCount > 0 && pagerState.currentPage != state.currentPage) {
            pagerState.animateScrollToPage(state.currentPage)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButton = {
            if (state.canShowStatistics) {
                FloatingActionButton(
                    onClick = { onIntent(CarouselIntent.StatisticsClicked) }
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_info_details),
                        contentDescription = stringResource(R.string.statistics)
                    )
                }
            }
        }
    ) { paddingValues ->
        when (state.contentState) {
            CarouselContentState.Error -> {
                ScreenStateMessage(
                    title = errorTitle,
                    message = errorMessage,
                    actionLabel = retryLabel,
                    onActionClick = { onIntent(CarouselIntent.RetryLoadClicked) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            CarouselContentState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is CarouselContentState.Content,
            is CarouselContentState.Empty -> {
                ItemList(
                    items = state.filteredItems,
                    listState = listState,
                    topContent = {
                        if (state.pageCount > 0) {
                            ImageCarousel(
                                pages = state.pages,
                                pagerState = pagerState
                            )
                        }
                    },
                    stickySearchContent = {
                        if (state.pageCount > 0) {
                            SearchBar(
                                query = state.searchQuery,
                                onQueryChange = { query ->
                                    onIntent(CarouselIntent.SearchQueryChanged(query))
                                },
                                modifier = Modifier.padding(
                                    top = searchBarVerticalPadding,
                                    bottom = searchBarVerticalPadding
                                )
                            )
                        }
                    },
                    emptyContent = {
                        when (state.emptyReason) {
                            CarouselEmptyReason.NO_PAGES -> {
                                ScreenStateMessage(
                                    title = noDataTitle,
                                    message = noDataMessage
                                )
                            }

                            CarouselEmptyReason.NO_SEARCH_RESULTS -> {
                                ScreenStateMessage(
                                    title = noSearchResultsTitle,
                                    message = noSearchResultsMessage
                                )
                            }

                            null -> Unit
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(PaddingValues(bottom = fabBottomSpacing))
                )
            }
        }
    }

    if (state.isStatisticsVisible && state.canShowStatistics) {
        StatisticsBottomSheet(
            statistics = state.statistics,
            onDismiss = { onIntent(CarouselIntent.StatisticsDismissed) }
        )
    }
}
