package sc

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCaseOrder
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTime::class)
object KotestConfig: AbstractProjectConfig() {
    override val parallelism = Runtime.getRuntime().availableProcessors()
    
    override val isolationMode = IsolationMode.InstancePerLeaf
    override val testCaseOrder = TestCaseOrder.Random
    
    private const val timeoutSecs = 10
    override val invocationTimeout = timeoutSecs * 1000L
    override val timeout = timeoutSecs.times(5).seconds
    
    init {
        System.setProperty("kotest.assertions.multi-line-diff", "simple")
    }
}
