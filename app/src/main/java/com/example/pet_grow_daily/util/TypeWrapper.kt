package com.example.pet_grow_daily.util

import com.example.pet_grow_daily.feature.add.CategoryType

fun getCategoryType(categoryType: CategoryType): String {
    when (categoryType) {
        CategoryType.SNACK -> {
            return "간식"
        }

        CategoryType.WATER -> {
            return "물 마시기"
        }

        CategoryType.MEDICINE -> {
            return "약 먹기 "
        }

        CategoryType.BATH -> {
            return "목욕"
        }

        CategoryType.HOSPITAL -> {
            return "병원"
        }

        CategoryType.OUT_WORK -> {
            return "산책"
        }

        CategoryType.SLEEP -> {
            return "수면"

        }

        CategoryType.IN_PLAY -> {
            return "실내놀이"
        }

        CategoryType.OUT_PLAY -> {
            return "실외놀이"
        }

        CategoryType.EVENT -> {
            return "이벤트"
        }

        CategoryType.ETC -> {
            return "기타"
        }

        CategoryType.NONE -> {
            return "없음"
        }

        else -> {
            return "없음"
        }
    }
}

