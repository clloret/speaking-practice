package com.clloret.speakingpractice.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.clloret.speakingpractice.BaseFragment
import com.clloret.speakingpractice.R
import com.clloret.speakingpractice.databinding.StatsFragmentBinding
import com.clloret.speakingpractice.stats.formatters.CustomPercentFormatter
import com.clloret.speakingpractice.stats.formatters.DayOfWeekFormatter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.DayOfWeek

class StatsFragment : BaseFragment() {

    companion object {
        private const val CIRCLE_RADIUS = 5F
        private const val LINE_WIDTH = 3F
        fun newInstance() = StatsFragment()
    }

    private val viewModel: StatsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: StatsFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.stats_fragment, container, false
        )
        binding.model = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupChart(binding.lineChart)
        showChartData(binding.lineChart)

        return binding.root
    }

    private fun showChartData(chart: LineChart) {
        viewModel.dailyStats.observe(viewLifecycleOwner, { dailyStats ->
            val entries = mutableListOf<Entry>()

            dailyStats.forEach { value ->
                val dayOfWeek = value.date.dayOfWeek.value
                entries.add(Entry(dayOfWeek.toFloat(), value.successRate.toFloat()))
            }
            val dataSet = LineDataSet(entries, "Success Rate").apply {
                circleRadius = CIRCLE_RADIUS
                lineWidth = LINE_WIDTH
                axisDependency = YAxis.AxisDependency.LEFT
                valueFormatter = CustomPercentFormatter()
                setCircleColors(arrayOf(R.color.color_secondary).toIntArray(), context)
                setColors(arrayOf(R.color.color_primary).toIntArray(), context)
            }

            chart.apply {
                data = LineData(dataSet)
                invalidate()
            }
        })
    }

    private fun setupChart(chart: LineChart) {
        chart.xAxis.apply {
            granularity = 1F
            axisMinimum = DayOfWeek.MONDAY.value.toFloat()
            axisMaximum = DayOfWeek.SUNDAY.value.toFloat()
            valueFormatter = DayOfWeekFormatter()
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
        }

        chart.axisLeft.apply {
            axisMinimum = 0F
            axisMaximum = 100F
            valueFormatter = CustomPercentFormatter()
        }

        chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false

            invalidate()
        }
    }
}
