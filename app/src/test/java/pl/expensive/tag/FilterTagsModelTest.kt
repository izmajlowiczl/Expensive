package pl.expensive.tag

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import pl.expensive.storage.Tag

class FilterTagsModelTest {

    @Test
    fun emptyInputReturnsUnfilteredTags() {
        val food = Tag(name = "Food")

        val filtered = filterTags("", listOf(food))

        assertThat(filtered).containsExactly(food)
    }

    @Test
    fun nullInputReturnsUnfilteredTags() {
        val food = Tag(name = "Food")

        val filtered = filterTags(null, listOf(food))

        assertThat(filtered).containsExactly(food)
    }

    @Test
    fun givesResultsContainingInput() {
        val test = Tag(name = "Test")
        val testing = Tag(name = "Testing")

        val filtered = filterTags("est", listOf(test, testing))

        assertThat(filtered).containsExactly(test, testing)
    }

    @Test
    fun giveCaseInSensitiveResults() {
        val test = Tag(name = "Test")
        val testing = Tag(name = "teStIng")

        val filtered = filterTags("test", listOf(test, testing))

        assertThat(filtered).containsExactly(test, testing)
    }

    @Test
    fun excludesNotMatchingResults() {
        val horse = Tag(name = "Horse")
        val carrot = Tag(name = "Carrot")

        val filtered = filterTags("Hors", listOf(horse, carrot))

        assertThat(filtered).containsExactly(horse)
    }
}
