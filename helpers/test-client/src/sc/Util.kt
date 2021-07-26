@file:JvmName("Util")
package sc

import sc.shared.ScoreFragment
import java.io.File
import java.math.BigDecimal

fun isJar(f: File): Boolean = f.name.endsWith("jar") && f.exists()
fun factorial(n: Int): Double = if (n <= 1) 1.0 else factorial(n - 1) * n
fun factorial(n: Int, downTo: Int): Double = if (n <= downTo) 1.0 else factorial(n - 1, downTo) * n

data class ScoreValue(val fragment: ScoreFragment, var value: BigDecimal)

internal class ClientPlayer {
    var name: String? = null
    var canTimeout = false
    var executable: File? = null
    var isJar = false
    
    
    var proc: Process? = null
    var score: Array<ScoreValue>
    override fun toString(): String =
            String.format("ClientPlayer{name='%s', executable='%s', isJar=%s, canTimeout=%s}", name, executable, isJar, canTimeout)
}

