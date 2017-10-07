package pl.expensive.storage

import android.support.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import pl.expensive.storage.DatabaseSchemaTestHelper.getTableColumns

@RunWith(AndroidJUnit4::class)
class DatabaseSchemaTest {
    private val database = Injector.provideDatabase()

    @Test
    fun columnsForTransactionTable() {
        val columns = getTableColumns(database.readableDatabase, "tbl_transaction")

        assertThat(columns)
                .containsExactly("uuid", "amount", "currency", "date", "description")
    }

    @Test
    fun columnsForCurrencyTable() {
        val columns = getTableColumns(database.readableDatabase, tbl_currency)

        assertThat(columns)
                .containsExactly("code", "format")
    }
}
