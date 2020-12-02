package sc

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.core.test.TestCaseOrder

object KotestConfig: AbstractProjectConfig() {
    override val isolationMode = IsolationMode.InstancePerLeaf
    override val testCaseOrder = TestCaseOrder.Random
    override val parallelism = Runtime.getRuntime().availableProcessors()
}
