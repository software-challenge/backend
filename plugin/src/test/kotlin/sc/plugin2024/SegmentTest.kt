package sc.plugin2024

import io.kotest.core.datatest.forAll
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.*
import io.kotest.matchers.collections.*
import sc.api.plugins.CubeCoordinates
import sc.api.plugins.CubeDirection
import sc.helpers.shouldSerializeTo
import sc.plugin2024.util.PluginConstants
import kotlin.random.Random

class SegmentTest: FunSpec({
    context("generates") {
        test("passenger fields") {
            val single = arrayOf(arrayOf<Field>(Field.PASSENGER()))
            single.alignPassengers(Random(0)) shouldBe true
            single.first().first() shouldBe Field.PASSENGER(CubeDirection.LEFT)
            single.alignPassengers(Random(5)) shouldBe true
            single.first().first() shouldBe Field.PASSENGER(CubeDirection.DOWN_LEFT)
            
            arrayOf(arrayOf<Field>(Field.PASSENGER(), Field.WATER)).alignPassengers() shouldBe true
            arrayOf(arrayOf<Field>(Field.BLOCKED), arrayOf<Field>(Field.PASSENGER())).alignPassengers() shouldBe false
            val bottomPassenger = arrayOf(arrayOf<Field>(Field.BLOCKED, Field.BLOCKED, Field.BLOCKED, Field.BLOCKED, Field.PASSENGER()))
            bottomPassenger.alignPassengers() shouldBe true
            (bottomPassenger.first().last() as Field.PASSENGER).direction shouldBeIn arrayOf(CubeDirection.LEFT, CubeDirection.UP_LEFT)
        }
        test("goal fields") {
            val segment = generateSegment(true, arrayOf())
            segment.sumOf { it.count { it == Field.WATER } } shouldBe 17
            forAll(1, 2, 3) {
                segment[PluginConstants.SEGMENT_FIELDS_WIDTH - 1, it] shouldBe Field.GOAL
            }
        }
        test("proper board start") {
            val generatedBoard = Board()
            forAll<Segment>(generatedBoard.segments.take(2)) {
                it.direction shouldBe CubeDirection.RIGHT
            }
            generatedBoard.segments[1].center shouldBe CubeCoordinates(4, 0)
        }
    }
    test("clones deeply") {
        val single = Segment(CubeDirection.RIGHT, CubeCoordinates.ORIGIN, arrayOf(arrayOf<Field>(Field.PASSENGER())))
        val clone = single.clone()
        clone shouldBe single
        (clone.fields[0][0] as Field.PASSENGER).passenger--
        clone shouldNotBe single
        (single.fields[0][0] as Field.PASSENGER).passenger shouldBe 1
    }
    xtest("serialize Segment") {
        Segment(CubeDirection.RIGHT, CubeCoordinates.ORIGIN, arrayOf(arrayOf(Field.WATER))) shouldSerializeTo """
          <segment direction="RIGHT">
            <column>
              <field type="WATER"/>
            </column>
          </segment>
        """
        Segment(CubeDirection.RIGHT, CubeCoordinates.ORIGIN, arrayOf(arrayOf(Field.PASSENGER(CubeDirection.LEFT)))) shouldSerializeTo """
          <segment direction="RIGHT">
            <column>
              <field type="PASSENGER" direction="LEFT" passenger="1" />
            </column>
          </segment>
        """
        Segment(CubeDirection.DOWN_LEFT, CubeCoordinates.ORIGIN, arrayOf(arrayOf(Field.PASSENGER(CubeDirection.RIGHT, 0), Field.WATER), arrayOf(Field.SANDBANK, Field.GOAL))) shouldSerializeTo """
          <segment direction="DOWN_LEFT">
            <column>
              <field type="PASSENGER" direction="RIGHT" passenger="0" />
              <field type="WATER" />
            </column>
            <column>
              <field type="SANDBANK" />
              <field type="GOAL" />
            </column>
          </segment>
        """ // Do not serialize center to avoid imposing coordinate system
        // TODO how to serialize ship position?
    }
})