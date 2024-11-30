package kr.co.hyunwook.pet_grow_daily.feature.total

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetMonthlyCategoryGrowRecordsUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetMonthlyGrowRecordUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetNameUseCase
import kr.co.hyunwook.pet_grow_daily.feature.add.CategoryType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TotalViewModel @Inject constructor(
    private val getMonthlyGrowRecordUseCase: GetMonthlyGrowRecordUseCase,
    private val getMonthlyCategoryGrowRecordsUseCase: GetMonthlyCategoryGrowRecordsUseCase,
    private val getNameUseCase: GetNameUseCase
) : ViewModel() {

    private val _monthlyGrowRecords = MutableStateFlow<List<GrowRecord>>(emptyList())
    val monthlyGrowRecords: StateFlow<List<GrowRecord>> get() = _monthlyGrowRecords

    private val _dogName = MutableStateFlow("")
    val dogName: StateFlow<String> get() = _dogName

    private val _topMonthlyCategories = MutableStateFlow<List<CategoryCount>>(emptyList())
    val topCategories: StateFlow<List<CategoryCount>> get() = _topMonthlyCategories



    fun getMonthlyCategoryGrowRecord(categoryType: CategoryType, month: String) {
        viewModelScope.launch {
            val records = if (categoryType == CategoryType.ALL) {
                getMonthlyGrowRecordUseCase(month).firstOrNull() ?: emptyList()
            } else {
                getMonthlyCategoryGrowRecordsUseCase(categoryType, month).firstOrNull() ?: emptyList()
            }

            _monthlyGrowRecords.value = records

            if (categoryType == CategoryType.ALL) {
                calculateTopCategories(records)
            }
        }
    }

    private fun calculateTopCategories(records: List<GrowRecord>) {
        val categoryCounts = records
            .groupBy { it.categoryType }
            .map { CategoryCount(it.key, it.value.size) }
            .sortedByDescending { it.count }
            .take(3)

        _topMonthlyCategories.value = categoryCounts
    }


    fun fetchDogName() {
        viewModelScope.launch {
            getNameUseCase().collect { name ->
                _dogName.value = name
            }
        }
    }
}

data class CategoryCount(
    val categoryType: CategoryType,
    val count: Int
)

data class TotalScreenState(
    val dogName: String,
    val categories: List<CategoryCount>,
    val monthlyGrowRecords: List<GrowRecord>,
    val currentMonth: Int
)