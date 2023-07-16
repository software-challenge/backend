package sc.plugin2024

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import sc.api.plugins.CubeCoordinates
import kotlin.math.round

class SegmentTest: FunSpec({
    context("SegmentFields.get(coordinates: CubeCoordinates) should return right value") {
        val fields: SegmentFields = generateSegment(false, arrayOf())
        val centerX = round(fields.size / 2.0).toInt() - 1
        val centerY = round(fields[centerX].size / 2.0).toInt()

        fields[CubeCoordinates.ORIGIN] shouldBe fields[centerX][centerY]
        fields[CubeCoordinates(1, -1)] shouldBe fields[centerX + 1][centerY - 1]
        fields[CubeCoordinates(-1, 1)] shouldBe fields[centerX - 1][centerY + 1]
    }
})