package pl.expensive.storage.util;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SQLiteTableBuilderTest {

    @Test
    public void CreateOptionalText() {
        String sql = SQLiteTableBuilder.newTable("tbl")
                .withOptionalText("col_text")
                .build();

        String expected = "CREATE TABLE tbl (col_text TEXT);";

        assertThat(sql, equalTo(expected));
    }

    @Test
    public void CreateOptionalDecimal() {
        String sql = SQLiteTableBuilder.newTable("tbl")
                .withOptionalDecimal("col_number")
                .build();

        String expected = "CREATE TABLE tbl (col_number INTEGER);";

        assertThat(sql, equalTo(expected));
    }

    @Test
    public void CreateMandatoryText() {
        String sql = SQLiteTableBuilder.newTable("tbl")
                .withMandatoryText("col_text")
                .build();

        String expected = "CREATE TABLE tbl (col_text TEXT NOT NULL);";

        assertThat(sql, equalTo(expected));
    }

    @Test
    public void mandatoryDecimal() {
        String sql = SQLiteTableBuilder.newTable("tbl")
                .withMandatoryDecimal("col_number")
                .build();

        String expected = "CREATE TABLE tbl (col_number INTEGER NOT NULL);";

        assertThat(sql, equalTo(expected));
    }

    @Test
    public void CreateMultipleFields() {
        String sql = SQLiteTableBuilder.newTable("tbl")
                .withOptionalText("col_text")
                .withMandatoryDecimal("col_number")
                .build();

        String expected = "CREATE TABLE tbl (" +
                "col_text TEXT," +
                "col_number INTEGER NOT NULL);";

        assertThat(sql, equalTo(expected));
    }

    @Test
    public void CreatePrimaryKey() {
        String sql = SQLiteTableBuilder.newTable("tbl")
                .withMandatoryText("pk_col")
                .withPrimaryKey("pk_col")
                .build();

        String expected = "CREATE TABLE tbl (" +
                "pk_col TEXT NOT NULL," +
                "PRIMARY KEY (pk_col));";

        assertThat(sql, equalTo(expected));
    }

    @Test
    public void CreateMultiplePrimaryKeys() {
        String sql = SQLiteTableBuilder.newTable("tbl")
                .withMandatoryText("pk_col")
                .withMandatoryText("pk_col1")
                .withPrimaryKey("pk_col")
                .withPrimaryKey("pk_col1")
                .build();

        String expected = "CREATE TABLE tbl (" +
                "pk_col TEXT NOT NULL," +
                "pk_col1 TEXT NOT NULL," +
                "PRIMARY KEY (pk_col)," +
                "PRIMARY KEY (pk_col1)" +
                ");";

        assertThat(sql, equalTo(expected));
    }

    @Test
    public void CreateForeignKey() {
        String sql = SQLiteTableBuilder.newTable("tbl")
                .withMandatoryText("fk_col")
                .withForeignKey("fk_col", "tbl_other", "col_other")
                .build();

        String expected = "CREATE TABLE tbl (" +
                "fk_col TEXT NOT NULL," +
                "FOREIGN KEY (fk_col) REFERENCES tbl_other(col_other));";

        assertThat(sql, equalTo(expected));
    }

    @Test
    public void CreateMultipleForeignKey() {
        String sql = SQLiteTableBuilder.newTable("tbl")
                .withMandatoryText("fk_col")
                .withMandatoryDecimal("fk_col1")
                .withForeignKey("fk_col", "tbl_other", "col_other")
                .withForeignKey("fk_col1", "tbl_other1", "col_other1")
                .build();

        String expected = "CREATE TABLE tbl (" +
                "fk_col TEXT NOT NULL," +
                "fk_col1 INTEGER NOT NULL," +
                "FOREIGN KEY (fk_col) REFERENCES tbl_other(col_other)," +
                "FOREIGN KEY (fk_col1) REFERENCES tbl_other1(col_other1)" +
                ");";

        assertThat(sql, equalTo(expected));
    }

    @Test(expected = IllegalStateException.class)
    public void CreateTable_WithoutFields_ThrowsException() {
        SQLiteTableBuilder.newTable("tbl").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void CreateTable_WithoutName_ThrowsException() {
        SQLiteTableBuilder.newTable("")
                .withOptionalText("col")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void CreateOptionalText_WithoutFieldName_ThrowsException() {
        SQLiteTableBuilder.newTable("table")
                .withOptionalText("")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void CreateOptionalDecimal_WithoutFieldName_ThrowsException() {
        SQLiteTableBuilder.newTable("table")
                .withOptionalDecimal("")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void CreateMandatoryText_WithoutFieldName_ThrowsException() {
        SQLiteTableBuilder.newTable("table")
                .withMandatoryText("")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void CreateMandatoryDecimal_WithoutFieldName_ThrowsException() {
        SQLiteTableBuilder.newTable("table")
                .withMandatoryDecimal("")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void CreatePrimaryKey_WithoutFieldName_ThrowsException() {
        SQLiteTableBuilder.newTable("table")
                .withOptionalText("col_text")
                .withPrimaryKey("")
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void CreatePrimaryKey_WithoutBakingField_ThrowsException() {
        SQLiteTableBuilder.newTable("tbl")
                .withOptionalDecimal("col_number")
                .withPrimaryKey("pk_col")
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void CreateMultiplePrimaryKeys_WithMissingBackingField_ThrowsException() {
        SQLiteTableBuilder.newTable("tbl")
                .withMandatoryText("pk_col")
                .withPrimaryKey("pk_col")
                .withPrimaryKey("pk_col1")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void CreateForeignKey_WithoutColumnName_ThrowsException() {
        SQLiteTableBuilder.newTable("table")
                .withForeignKey("", "other_tbl", "other_field")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void CreateForeignKey_WithoutReferenceTable_ThrowsException() {
        SQLiteTableBuilder.newTable("table")
                .withForeignKey("col", "", "other_field")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void CreateForeignKey_WithoutReferenceColumn_ThrowsException() {
        SQLiteTableBuilder.newTable("table")
                .withForeignKey("col", "other", "")
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void CreateForeignKey_WithoutBakingField_ThrowsException() {
        SQLiteTableBuilder.newTable("tbl")
                .withOptionalDecimal("not-a-foreign-key")
                .withForeignKey("fk_col", "tbl_other", "col_other")
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void CreateMultipleForeignKeys_WithMissingBackingField_ThrowsException() {
        SQLiteTableBuilder.newTable("tbl")
                .withMandatoryText("fk_col")
                .withForeignKey("fk_col", "tbl_other", "col_other")
                .withForeignKey("fk_col1", "tbl_other1", "col_other1")
                .build();
    }
}
