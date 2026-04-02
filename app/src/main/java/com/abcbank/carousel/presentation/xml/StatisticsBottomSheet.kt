package com.abcbank.carousel.presentation.xml

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.abcbank.carousel.databinding.BottomSheetStatisticsBinding
import com.abcbank.carousel.presentation.xml.adapter.StatisticsAdapter
import com.abcbank.carousel.presentation.xml.model.StatisticsSheetItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.Serializable

class StatisticsBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetStatisticsBinding? = null
    private val binding get() = _binding!!

    private val statistics: ArrayList<StatisticsSheetItem>
        get() = requireArguments().serializable(ARG_STATISTICS) ?: arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewStatistics.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = StatisticsAdapter().also { it.submitList(statistics) }
        }
    }

    override fun onDestroyView() {
        binding.recyclerViewStatistics.adapter = null
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "statistics_sheet"
        private const val ARG_STATISTICS = "arg_statistics"

        fun newInstance(statistics: ArrayList<StatisticsSheetItem>): StatisticsBottomSheet {
            return StatisticsBottomSheet().apply {
                arguments = bundleOf(ARG_STATISTICS to statistics)
            }
        }
    }
}

@Suppress("DEPRECATION")
private inline fun <reified T : Serializable> Bundle.serializable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializable(key, T::class.java)
    } else {
        getSerializable(key) as? T
    }
}
