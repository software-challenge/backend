package sc.framework

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.stream.IntStream
import kotlin.random.Random

/** A Stream of unique integers between 0 (incl) and [max] (exclusive)
 * with at most [limit] elements. */
fun shuffledIndices(max: Int, limit: Int = max): IntStream =
        IntStream.generate { Random.nextInt(max) }
                .distinct()
                .limit(limit.toLong())

object HelperMethods {
    private val dateTimeFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
    /**
     * Returns the current date and time as string formatted as yyyy.MM.dd
     * HH_mm_ss.
     *
     * @return current date and time
     */
    private val currentDateTime: String
        get() = dateTimeFormat.format(Date())
    
    /**
     * Returns a new generated filename for a replay file.
     *
     * @param gameId  UUID of the plugin
     * @param names descriptor of player slot
     *
     * @return name of replay
     */
    @JvmStatic
    fun getReplayFilename(gameId: String, names: List<String>): String =
            "replays/replay_${gameId}_${currentDateTime}_" +
            "${names.joinToString("_") { it.replace(' ', '_') }}.xml"
}