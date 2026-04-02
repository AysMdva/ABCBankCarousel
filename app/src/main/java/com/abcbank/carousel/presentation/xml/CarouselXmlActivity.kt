package com.abcbank.carousel.presentation.xml

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.abcbank.carousel.R
import com.abcbank.carousel.databinding.ActivityMainXmlBinding
import com.abcbank.carousel.di.CarouselAppContainer
import com.abcbank.carousel.presentation.xml.adapter.ImageCarouselAdapter
import com.abcbank.carousel.presentation.xml.adapter.ListItemAdapter
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlContentState
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlEffect
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlEmptyReason
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlIntent
import com.abcbank.carousel.presentation.xml.mvi.CarouselXmlState
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CarouselXmlActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainXmlBinding
    private val appContainer by lazy { CarouselAppContainer(applicationContext) }
    private val viewModel: CarouselXmlViewModel by viewModels {
        CarouselXmlViewModel.factory(repository = appContainer.carouselRepository)
    }

    private val listAdapter = ListItemAdapter()
    private val imageAdapter = ImageCarouselAdapter()

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.onIntent(CarouselXmlIntent.PageChanged(position))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainXmlBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupCarousel()
        setupSearch()
        setupFab()
        observeUi()
        viewModel.onIntent(CarouselXmlIntent.LoadData)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewItems.apply {
            layoutManager = LinearLayoutManager(this@CarouselXmlActivity)
            adapter = listAdapter
        }
    }

    private fun setupCarousel() {
        binding.viewPagerCarousel.adapter = imageAdapter
        binding.viewPagerCarousel.registerOnPageChangeCallback(pageChangeCallback)
    }

    private fun setupSearch() {
        binding.editTextSearch.doAfterTextChanged { editable ->
            viewModel.onIntent(CarouselXmlIntent.SearchQueryChanged(editable?.toString().orEmpty()))
        }
    }

    private fun setupFab() {
        binding.fabStatistics.setOnClickListener {
            viewModel.onIntent(CarouselXmlIntent.StatisticsClicked)
        }
        binding.buttonRetry.setOnClickListener {
            viewModel.onIntent(CarouselXmlIntent.RetryLoadClicked)
        }
    }

    private fun observeUi() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.state.collect(::renderState)
                }
                launch {
                    viewModel.effects.collect(::handleEffect)
                }
            }
        }
    }

    private fun renderState(state: CarouselXmlState) {
        binding.progressBarState.isVisible = state.contentState is CarouselXmlContentState.Loading
        binding.layoutStateMessage.isVisible = state.contentState is CarouselXmlContentState.Error ||
            state.contentState is CarouselXmlContentState.Empty

        when (state.contentState) {
            CarouselXmlContentState.Error -> {
                binding.appBar.isVisible = false
                binding.recyclerViewItems.isVisible = false
                renderStateMessage(
                    titleRes = R.string.carousel_error_title,
                    messageRes = R.string.carousel_error_message,
                    showRetry = true
                )
            }

            CarouselXmlContentState.Loading -> {
                binding.appBar.isVisible = state.pageCount > 0
                binding.recyclerViewItems.isVisible = false
                binding.layoutStateMessage.isVisible = false
            }

            is CarouselXmlContentState.Content -> {
                binding.appBar.isVisible = true
                binding.recyclerViewItems.isVisible = true
                binding.layoutStateMessage.isVisible = false
            }

            is CarouselXmlContentState.Empty -> {
                binding.appBar.isVisible = state.pageCount > 0
                binding.recyclerViewItems.isVisible = false
                val (titleRes, messageRes) = when (state.emptyReason) {
                    CarouselXmlEmptyReason.NO_PAGES -> {
                        R.string.carousel_empty_title to R.string.carousel_empty_message
                    }

                    CarouselXmlEmptyReason.NO_SEARCH_RESULTS -> {
                        R.string.carousel_no_results_title to R.string.carousel_no_results_message
                    }

                    null -> return
                }
                renderStateMessage(
                    titleRes = titleRes,
                    messageRes = messageRes,
                    showRetry = false
                )
            }
        }

        val images = state.pages.map { it.imageRes }
        if (imageAdapter.currentList != images) imageAdapter.submitList(images)

        if (binding.layoutPageIndicators.childCount != state.pages.size) {
            createIndicators(state.pages.size)
        }

        updatePageIndicators(state.currentPage)

        if (binding.viewPagerCarousel.currentItem != state.currentPage) {
            binding.viewPagerCarousel.setCurrentItem(state.currentPage, false)
        }

        val searchQuery = binding.editTextSearch.text?.toString().orEmpty()
        if (searchQuery != state.searchQuery) {
            binding.editTextSearch.setText(state.searchQuery)
            binding.editTextSearch.setSelection(state.searchQuery.length)
        }

        listAdapter.submitList(state.filteredItems)
        binding.fabStatistics.isVisible = state.canShowStatistics
    }

    private fun handleEffect(effect: CarouselXmlEffect) {
        when (effect) {
            CarouselXmlEffect.ShowLoadError -> {
                Snackbar.make(binding.root, R.string.carousel_load_error, Snackbar.LENGTH_LONG).show()
            }
            CarouselXmlEffect.ScrollItemsToTop -> {
                binding.recyclerViewItems.scrollToPosition(0)
            }
            is CarouselXmlEffect.ShowStatistics -> {
                if (supportFragmentManager.findFragmentByTag(StatisticsBottomSheet.TAG) == null) {
                    StatisticsBottomSheet.newInstance(ArrayList(effect.statistics)).show(
                        supportFragmentManager,
                        StatisticsBottomSheet.TAG
                    )
                }
            }
        }
    }

    private fun createIndicators(count: Int) {
        val dotSize = resources.getDimensionPixelSize(R.dimen.indicator_dot_size)
        val dotMargin = resources.getDimensionPixelSize(R.dimen.indicator_dot_margin)

        binding.layoutPageIndicators.removeAllViews()
        repeat(count) {
            val dot = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(dotSize, dotSize).apply {
                    marginStart = dotMargin
                    marginEnd = dotMargin
                }
                background = indicatorDrawable(false)
            }
            binding.layoutPageIndicators.addView(dot)
        }
    }

    private fun updatePageIndicators(selectedIndex: Int) {
        binding.layoutPageIndicators.children.forEachIndexed { index, view ->
            view.background = indicatorDrawable(index == selectedIndex)
        }
    }

    private fun renderStateMessage(titleRes: Int, messageRes: Int, showRetry: Boolean) {
        binding.textStateTitle.setText(titleRes)
        binding.textStateMessage.setText(messageRes)
        binding.buttonRetry.isVisible = showRetry
    }

    private fun indicatorDrawable(selected: Boolean) = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(ContextCompat.getColor(
            this@CarouselXmlActivity,
            if (selected) R.color.primaryBlue else R.color.textSecondary
        ))
    }

    override fun onDestroy() {
        binding.viewPagerCarousel.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroy()
    }
}
