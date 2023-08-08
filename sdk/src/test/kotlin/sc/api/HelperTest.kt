package sc.api

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeDirection
import sc.framework.shuffledIndices

class HelperTest: FunSpec({
    test("shuffled indices") {
        shuffledIndices(CubeDirection.values().size)
                .toArray().size shouldBe 6
        shuffledIndices(CubeDirection.values().size)
                .takeWhile { it > -1 }.toArray().size shouldBe 6
    }
})