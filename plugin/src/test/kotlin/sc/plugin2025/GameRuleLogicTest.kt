package sc.plugin2025

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.*
import sc.plugin2025.GameRuleLogic.calculateCarrots
import sc.plugin2025.GameRuleLogic.calculateMoveableFields

class GameRuleLogicTest: StringSpec({
    /** Überprüft die Berechnungen der `calculateCarrots()` Hilfsfunktion */
    "testCalculateCarrots" {
        calculateCarrots(1) shouldBe 1
        calculateCarrots(10) shouldBe 55
    }
    
    /** Überprüft die Berechnung der `calculateMoveableFields()` Hilfsfunktion */
    "testCalculateMoveableFields" {
        calculateMoveableFields(0) shouldBe 0
        calculateMoveableFields(1) shouldBe 1
        calculateMoveableFields(5) shouldBe 2
        calculateMoveableFields(6) shouldBe 3
        calculateMoveableFields(7) shouldBe 3
    }
})
