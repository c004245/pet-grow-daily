package kr.co.hyunwook.pet_grow_daily.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.feature.add.CategoryType
import kr.co.hyunwook.pet_grow_daily.feature.add.EmotionType
import kr.co.hyunwook.pet_grow_daily.feature.main.SelectTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun getEmotionItem(emotionType: EmotionType): Painter {
    val resourceMap =
        mapOf(
            EmotionType.ANGRY to R.drawable.ic_angry_dailygrow,
            EmotionType.GOOD to R.drawable.ic_good_dailygrow,
            EmotionType.LOVE to R.drawable.ic_love_dailygrow,
            EmotionType.SAD to R.drawable.ic_sad_dailygrow,
            EmotionType.CRY to R.drawable.ic_cry_dailygrow,
            EmotionType.HAPPY to R.drawable.ic_happy_dailygrow,
            EmotionType.SMILE to R.drawable.ic_smile_dailygrow,
            EmotionType.SICK to R.drawable.ic_sink_dailygrow,
        )

    val resourceId = resourceMap[emotionType] ?: R.drawable.ic_happy_dailygrow
    return painterResource(id = resourceId)
}
@Composable
fun getCategoryItem(categoryType: CategoryType, selectTab: SelectTab): Painter {
    val resourceMap = when (selectTab) {
        SelectTab.DAILYGROW -> mapOf(
            CategoryType.SNACK to R.drawable.ic_snack_dailygrow,
            CategoryType.WATER to R.drawable.ic_water_dailygrow,
            CategoryType.MEDICINE to R.drawable.ic_medicine_dailygrow,
            CategoryType.BATH to R.drawable.ic_bath_dailygrow,
            CategoryType.HOSPITAL to R.drawable.ic_hospital_dailygrow,
            CategoryType.OUT_WORK to R.drawable.ic_outwork_dailygrow,
            CategoryType.SLEEP to R.drawable.ic_sleep_dailygrow,
            CategoryType.IN_PLAY to R.drawable.ic_inplay_dailygrow,
            CategoryType.OUT_PLAY to R.drawable.ic_outplay_dailygrow,
            CategoryType.EVENT to R.drawable.ic_event_dailygrow,
            CategoryType.ETC to R.drawable.ic_etc_dailygrow,
            CategoryType.NONE to R.drawable.ic_water_dailygrow,
        )

        SelectTab.TOTAL -> mapOf(
            CategoryType.SNACK to R.drawable.ic_snack_select,
            CategoryType.WATER to R.drawable.ic_water_select,
            CategoryType.MEDICINE to R.drawable.ic_medicine_select,
            CategoryType.BATH to R.drawable.ic_bath_select,
            CategoryType.HOSPITAL to R.drawable.ic_hospital_select,
            CategoryType.OUT_WORK to R.drawable.ic_outwork_select,
            CategoryType.SLEEP to R.drawable.ic_sleep_select,
            CategoryType.IN_PLAY to R.drawable.ic_inplay_select,
            CategoryType.OUT_PLAY to R.drawable.ic_outplay_select,
            CategoryType.EVENT to R.drawable.ic_event_select,
            CategoryType.ETC to R.drawable.ic_etc_select,
            CategoryType.NONE to R.drawable.ic_water_select
        )

        SelectTab.NAME -> TODO()
    }

    // 기본값을 설정하여 null 방지
    val resourceId = resourceMap[categoryType] ?: R.drawable.ic_water_select
    return painterResource(id = resourceId)
}

fun getStringToCategoryType(type: String): CategoryType {
    when (type) {
        "전체" -> {
            return CategoryType.ALL
        }

        "간식" -> {
            return CategoryType.SNACK
        }

        "물 마시기" -> {
            return CategoryType.WATER

        }

        "약 먹기" -> {
            return CategoryType.MEDICINE
        }

        "목욕" -> {
            return CategoryType.BATH
        }

        "병원" -> {
            return CategoryType.HOSPITAL
        }

        "산책" -> {
            return CategoryType.OUT_WORK
        }

        "수면" -> {
            return CategoryType.SLEEP
        }

        "실내놀이" -> {
            return CategoryType.IN_PLAY
        }

        "실외놀이" -> {
            return CategoryType.OUT_PLAY
        }

        "이벤트" -> {
            return CategoryType.EVENT
        }

        "기타" -> {
            return CategoryType.ETC
        }

        else -> {
            return CategoryType.NONE
        }
    }
}

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

fun formatTimestampToDateTime(timestamp: Long): String {
    val dateFormat = SimpleDateFormat("yyyy.MM.dd(E) HH:mm", Locale.getDefault())
    val date = Date(timestamp) // Long형 timestamp를 Date로 변환
    return dateFormat.format(date) // 포맷팅된 문자열 반환
}


fun getMemoOrRandomQuote(memo: String?): String {
    // 감성적인 문구 리스트
    val quotes = listOf(
        "네가 내 하루의 이유가 되어줘서 고마워.",
        "너와 함께라면 이 세상 어디든 천국이야.",
        "작은 몸짓으로 내 마음을 가득 채우는 너.",
        "네가 내게 준 사랑은 말로 다 표현할 수 없어.",
        "강아지의 꼬리 흔들림은 가장 순수한 행복의 언어야.",
        "네가 나를 믿어주는 것만으로도 난 충분히 행복해.",
        "강아지가 주는 사랑은 단순하지만 가장 진실해.",
        "이 세상에서 가장 귀여운 천사는 네 모습이겠지.",
        "네가 웃을 때마다 내 심장은 더 빠르게 뛰어.",
        "너와 함께 걷는 이 길이 나에게는 가장 소중한 순간이야.",
        "강아지의 포근한 온기만으로 하루가 완벽해져.",
        "네가 있어 내 삶이 더 따뜻해졌어.",
        "작은 발로 남긴 네 발자국은 내 인생의 가장 큰 선물이야.",
        "강아지의 사랑은 조건 없이 모든 걸 감싸 안아줘.",
        "네가 내 곁에 있는 한, 나는 절대 외롭지 않아.",
        "강아지와의 하루는 언제나 기적 같아.",
        "너의 해맑은 웃음이 나를 더 나은 사람으로 만들어줘.",
        "강아지의 눈빛에는 세상을 다 가진 듯한 평화가 있어.",
        "너를 바라보는 이 순간이 내겐 가장 행복한 기억이 될 거야.",
        "네가 나에게 주는 사랑은 세상 그 무엇과도 바꿀 수 없어."
    )

    // memo가 null이거나 비어있으면 랜덤 문구 반환, 그렇지 않으면 memo 반환
    return if (memo.isNullOrBlank()) {
        quotes[Random.nextInt(quotes.size)]
    } else {
        memo
    }
}
