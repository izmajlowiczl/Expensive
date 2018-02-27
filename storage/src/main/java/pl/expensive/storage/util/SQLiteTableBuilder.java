package pl.expensive.storage.util;


import java.util.ArrayList;
import java.util.List;


public class SQLiteTableBuilder {
    private String table;
    private List<String> commands;

    private List<String> fields, pks, fks;

    private SQLiteTableBuilder(String table) {
        this.table = table;
        commands = new ArrayList<>();
    }

    public static SQLiteTableBuilder newTable(String table) {
        checkNotEmpty(table);
        return new SQLiteTableBuilder(table);
    }

    private static void checkNotEmpty(String param) {
        if (param == null || param.isEmpty()) {
            throw new IllegalArgumentException("Table name is mandatory.");
        }
    }

    /**
     * <p>Joins the elements of the provided array into a single String
     * containing the provided list of elements.</p>
     * <p>
     * <p>No delimiter is added before or after the list.
     * Null objects or empty strings within the array are represented by
     * empty strings.</p>
     * <p>
     * <pre>
     * StringUtils.join(null, *)               = null
     * StringUtils.join([], *)                 = ""
     * StringUtils.join([null], *)             = ""
     * StringUtils.join(["a", "b", "c"], ';')  = "a;b;c"
     * StringUtils.join(["a", "b", "c"], null) = "abc"
     * StringUtils.join([null, "", "a"], ';')  = ";;a"
     * </pre>
     *
     * @param array the array of values to join together, may be null
     * @return the joined String, <code>null</code> if null array input
     * @since 2.0
     */
    private static String join(List<String> array) {
        if (array == null) {
            return null;
        }

        int startIndex = 0;
        int endIndex = array.size();
        char separator = ',';

        int bufSize = (endIndex - startIndex);
        if (bufSize <= 0) {
            return "";
        }

        bufSize *= ((array.get(startIndex) == null ? 16 : array.get(startIndex).length()) + 1);
        StringBuilder buf = new StringBuilder(bufSize);

        for (int i = startIndex; i < endIndex; i++) {
            if (i > startIndex) {
                buf.append(separator);
            }
            if (array.get(i) != null) {
                buf.append(array.get(i));
            }
        }
        return buf.toString();
    }

    public SQLiteTableBuilder withOptionalText(String column) {
        checkNotEmpty(column);
        initLazyFieldsCollection();

        fields.add(column);
        commands.add(column + " TEXT");
        return this;
    }

    public SQLiteTableBuilder withOptionalDecimal(String column) {
        checkNotEmpty(column);

        initLazyFieldsCollection();
        fields.add(column);
        commands.add(column + " INTEGER");
        return this;
    }

    public SQLiteTableBuilder withMandatoryText(String column) {
        checkNotEmpty(column);

        initLazyFieldsCollection();
        fields.add(column);
        commands.add(column + " TEXT NOT NULL");
        return this;
    }

    public SQLiteTableBuilder withMandatoryDecimal(String column) {
        checkNotEmpty(column);

        initLazyFieldsCollection();
        fields.add(column);
        commands.add(column + " INTEGER NOT NULL");
        return this;
    }

    public SQLiteTableBuilder withPrimaryKey(String column) {
        checkNotEmpty(column);

        initLazyPrimaryKeysCollection();
        pks.add(column);
        commands.add("PRIMARY KEY (" + column + ")");
        return this;
    }

    /**
     * It does not check if referenced column exists.
     * It does not check if referenced table exists, is PRIMARY KEY or UNIQUE!
     */
    public SQLiteTableBuilder withForeignKey(String column, String referenceTable, String referenceColumn) {
        checkNotEmpty(column);
        checkNotEmpty(referenceTable);
        checkNotEmpty(referenceColumn);

        initLazyForeignKeysCollection();
        fks.add(column);
        commands.add("FOREIGN KEY (" + column + ") REFERENCES " + referenceTable + "(" + referenceColumn + ")");
        return this;
    }

    public String build() {
        if (commands.isEmpty()) {
            throw new IllegalStateException("Cannot create table without any field");
        }

        // Check if all Primary Keys have backing fields
        if (pks != null) {
            for (String pk : pks) {
                if (!fields.contains(pk)) {
                    throw new IllegalStateException("Cannot find field for primary key [" + pk + "]");
                }
            }
        }

        // Check if all Foreign Keys have backing fields
        if (fks != null) {
            for (String pk : fks) {
                if (!fields.contains(pk)) {
                    throw new IllegalStateException("Cannot find field for foreign key [" + pk + "]");
                }
            }
        }

        return "CREATE TABLE " + table + " (" + join(commands) + ");";
    }

    private void initLazyPrimaryKeysCollection() {
        if (pks == null) {
            pks = new ArrayList<>();
        }
    }

    private void initLazyForeignKeysCollection() {
        if (fks == null) {
            fks = new ArrayList<>();
        }
    }

    private void initLazyFieldsCollection() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
    }
}
