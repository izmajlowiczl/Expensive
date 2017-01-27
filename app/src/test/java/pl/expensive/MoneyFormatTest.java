package pl.expensive;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static pl.expensive.core.MoneyFormat.formatValue;
import static pl.expensive.storage._Seeds.CHF;
import static pl.expensive.storage._Seeds.CZK;
import static pl.expensive.storage._Seeds.EUR;
import static pl.expensive.storage._Seeds.GBP;
import static pl.expensive.storage._Seeds.PLN;

public class MoneyFormatTest {
    @Test
    public void formattedEUR() {
        assertEquals("9,99 €", formatValue(Locale.GERMANY, new BigDecimal("9.99"), EUR));
    }

    @Test
    public void formattedGBP() {
        assertEquals("£1,02", formatValue(Locale.GERMANY, new BigDecimal("1.02"), GBP));
    }

    @Test
    public void formattedCHF() {
        assertEquals("0,12 CHF", formatValue(Locale.GERMANY, new BigDecimal("0.12"), CHF));
    }

    @Test
    public void formattedPLN() {
        assertEquals("123,45 zł", formatValue(Locale.GERMANY, new BigDecimal("123.45"), PLN));
    }

    @Test
    public void formattedCZK() {
        assertEquals("1,9 Kč", formatValue(Locale.GERMANY, new BigDecimal("1.90"), CZK));
    }
}
