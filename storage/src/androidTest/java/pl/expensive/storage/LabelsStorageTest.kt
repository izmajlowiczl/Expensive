package pl.expensive.storage

import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class LabelsStorageTest {
    private val database = Injector.provideDatabase()

    @Test
    fun listLabels() {
        val someLabel = Label(UUID.randomUUID(), "aLabel")
        insertLabel(someLabel, database)

        assertThat(listLabels(database))
                .contains(someLabel)
    }
}
