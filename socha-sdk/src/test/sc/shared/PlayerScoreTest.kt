package sc.shared

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import sc.shared.PlayerScore

import java.math.BigDecimal

class PlayerScoreTest: StringSpec({
    "construct PlayerScore" {
        val scoreList: BigDecimal? = null
        shouldThrow<IllegalArgumentException> { PlayerScore(ScoreCause.UNKNOWN, "", scoreList) }
    }
})