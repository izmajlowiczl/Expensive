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
    public void walletTableIsCreatedWithAllColumns() {
        List<String> columns = getTableColumns(database.getReadableDatabase(), "tbl_wallet");

        assertThat(columns)
                .containsExactly("uuid", "name");
    }
}
