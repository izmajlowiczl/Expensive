package pl.expensive.storage;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static pl.expensive.storage.DatabaseSchemaTestHelper.getTableColumns;

@RunWith(AndroidJUnit4.class)
public class DatabaseSchemaTest {
    private Database database = Injector.provideDatabase();

    @Test
    public void columnsForWalletTable() {
        List<String> columns = getTableColumns(database.getReadableDatabase(), "tbl_wallet");

        assertThat(columns)
                .containsExactly("uuid", "name");
    }

    @Test
    public void columnsForTransactionTable() {
        List<String> columns = getTableColumns(database.getReadableDatabase(), "tbl_transaction");

        assertThat(columns)
                .containsExactly("uuid", "amount", "currency", "date", "description", "wallet_uuid");
    }
}
