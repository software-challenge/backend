package sc.server.network

import io.kotest.assertions.nondeterministic.DurationFn
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.assertions.nondeterministic.eventuallyConfig
import io.kotest.assertions.nondeterministic.fibonacci
import io.kotest.assertions.withClue
import io.kotest.common.KotestInternal
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(KotestInternal::class)
fun await(
    clue: String? = null,
    duration: Duration = 1.seconds,
    interval: DurationFn = 20.milliseconds.fibonacci(),
    f: suspend () -> Unit,
) = runBlocking {
    withClue(clue) {
        eventually(eventuallyConfig {
            this.duration = duration
            intervalFn = interval
        }, f)
    }
}

@OptIn(KotestInternal::class)
fun awaitUntil(
    clue: String? = null,
    duration: Duration = 1.seconds,
    interval: DurationFn = 20.milliseconds.fibonacci(),
    predicate: () -> Boolean,
) = runBlocking {
    withClue(clue) {
        eventually(eventuallyConfig {
            this.duration = duration
            intervalFn = interval
        }) {
            predicate() shouldBe true
        }
    }
}
