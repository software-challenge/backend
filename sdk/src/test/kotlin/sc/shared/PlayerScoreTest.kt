package sc.shared

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import sc.helpers.shouldSerializeTo

class PlayerScoreTest: StringSpec({
    "check equality" {
        val playerScoreUnknown1 = PlayerScore(0)
        playerScoreUnknown1 shouldBe playerScoreUnknown1
        val playerScoreScores = PlayerScore(0, 1)
        playerScoreUnknown1 shouldNotBe playerScoreScores
        playerScoreScores shouldBe playerScoreScores
    }
    "convert XML" {
        PlayerScore(0, 1, 2) shouldSerializeTo """
            <score>
              <part>0</part>
              <part>1</part>
              <part>2</part>
            </score>""".trimIndent()
        PlayerScore(0, 1, 2) shouldSerializeTo """
            <score>
              <part>0</part>
              <part>1</part>
              <part>2</part>
            </score>""".trimIndent()
    }
})
