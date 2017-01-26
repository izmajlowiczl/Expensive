package pl.expensive.storage;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static pl.expensive.storage._Seeds.CHF;
import static pl.expensive.storage._Seeds.CZK;
import static pl.expensive.storage._Seeds.EUR;
import static pl.expensive.storage._Seeds.GBP;
import static pl.expensive.storage._Seeds.PLN;

public class CurrencyTest {
    @Test
    public void formattedEUR() {
        assertEquals("9,99 €", EUR.formatValue(Locale.GERMANY, new BigDecimal("9.99")));
    }

    @Test
    public void formattedGBP() {
        assertEquals("£1,02", GBP.formatValue(Locale.GERMANY, new BigDecimal("1.02")));
    }

    @Test
    public void formattedCHF() {
        assertEquals("0,12 CHF", CHF.formatValue(Locale.GERMANY, new BigDecimal("0.12")));
    }

    @Test
    public void formattedPLN() {
        assertEquals("123,45 zł", PLN.formatValue(Locale.GERMANY, new BigDecimal("123.45")));
    }

    @Test
    public void formattedCZK() {
        assertEquals("1,9 Kč", CZK.formatValue(Locale.GERMANY, new BigDecimal("1.90")));
    }
}
