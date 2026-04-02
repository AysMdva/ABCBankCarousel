package com.abcbank.carousel.presentation.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.abcbank.carousel.di.CarouselAppContainer
import com.abcbank.carousel.presentation.carousel.CarouselViewModel
import com.abcbank.carousel.presentation.compose.screen.CarouselScreenRoute

class ComposeMainActivity : ComponentActivity() {

    private val appContainer by lazy { CarouselAppContainer(applicationContext) }
    private val viewModel: CarouselViewModel by viewModels {
        CarouselViewModel.factory(repository = appContainer.carouselRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                CarouselScreenRoute(viewModel = viewModel)
            }
        }
    }
}
