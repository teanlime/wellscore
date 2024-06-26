package com.teanlime.wellscore.track.stores

import com.teanlime.wellscore.track.model.MetricEntry
import com.teanlime.wellscore.track.model.TrackedId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import javax.inject.Inject

class InMemoryTrackedRepository @Inject constructor() : TrackedRepository {

  private val metricEntryFlow = MutableStateFlow(emptyList<MetricEntry>())
  private var nextId = 0

  override suspend fun observeTracked(): Flow<List<MetricEntry>> {
    return metricEntryFlow
  }

  override suspend fun addNewTracked(metricEntry: MetricEntry) {
    metricEntryFlow.update { metricEntryFlow.value.toMutableList().apply { add(metricEntry) }.toList() }
  }

  override suspend fun modifyItem(id: TrackedId, normalizedValueChange: BigDecimal) {
    metricEntryFlow.update {
      val entryId = metricEntryFlow.value.indexOfFirst { it.id == id }

      metricEntryFlow.value.toMutableList().apply {
        val value = this[entryId]
        this[entryId] = this[entryId].setTo(value.normalizedValue + normalizedValueChange)
      }.toList()
    }
  }

  override suspend fun generateId(): Int {
    return nextId++
  }
}