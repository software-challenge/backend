package sc

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCaseOrder
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

object KotestConfig: AbstractProjectConfig() {
    override val parallelism = Runtime.getRuntime().availableProcessors()
    
    override val isolationMode = IsolationMode.InstancePerLeaf
    override val testCaseOrder = TestCaseOrder.Random
    
    private const val timeoutSecs = 10
    override val invocationTimeout = timeoutSecs * 1000L
    @OptIn(ExperimentalTime::class)
    override val timeout = timeoutSecs.times(5).toDuration(DurationUnit.SECONDS)
    
    init {
        System.setProperty("kotest.assertions.multi-line-diff", "simple")
    }
}
