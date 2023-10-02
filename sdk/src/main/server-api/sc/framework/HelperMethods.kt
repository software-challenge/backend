package sc.framework

import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.stream.IntStream
import kotlin.random.Random

/** A Stream of unique integers between 0 (incl) and [max] (exclusive)
 * with at most [limit] elements. */
fun shuffledIndices(max: Int, limit: Int = max, random: Random = Random): IntStream =
        IntStream.generate { random.nextInt(max) }
                .distinct()
                .limit(limit.toLong())

object HelperMethods {
    val replayFolder = File("replays")
    val dateTimeFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
    
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
    fun getReplayFile(gameId: String, names: List<String>): File =
            replayFolder.resolve("replay_${gameId}_${currentDateTime}_" +
            "${names.joinToString("_").replace(' ', '_')}.xml")
}