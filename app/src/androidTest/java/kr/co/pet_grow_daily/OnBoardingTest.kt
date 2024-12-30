package kr.co.pet_grow_daily

import io.kotest.core.spec.style.BehaviorSpec
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText

class NameScreenTest: BehaviorSpec({
    lateinit var composeTestRule: ComposeTestRule

    beforeTest {
        composeTestRule = createComposeRule()
    }

    Given("NameScreen이 유저에게 노출 된 상태에서") {
//        composeTestRule.

        When("텍스트 필드가 비어 있는 경우") {
            Then("버튼이 비활성화 상태여야 한다") {
                composeTestRule.onNodeWithText("시작하기").assertIsNotEnabled()
            }
        }
    }
})
