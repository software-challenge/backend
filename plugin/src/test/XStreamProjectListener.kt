import io.kotest.core.listeners.ProjectListener
import io.kotest.core.spec.AutoScan
import sc.plugin2021.GamePlugin.Companion.registerXStream

@AutoScan
object XStreamProjectListener: ProjectListener {
    override suspend fun beforeProject() {
        registerXStream()
    }
}