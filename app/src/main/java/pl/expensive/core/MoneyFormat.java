package pl.expensive.core;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

import pl.expensive.storage.Currency;
import pl.expensive.storage.Transaction;

public class MoneyFormat {
    public static String formatValue(Transaction transaction) {
        return new DecimalFormat(transaction.currency().format()).format(transaction.amount());
    }

    public static String formatValue(BigDecimal money, Currency currency) {
        DecimalFormat numberFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        numberFormat.applyPattern(currency.format());
        return numberFormat.format(money);
    }

    public static String formatValue(Locale locale, BigDecimal money, Currency currency) {
        DecimalFormat numberFormat = (DecimalFormat) DecimalFormat.getInstance(locale);
        numberFormat.applyPattern(currency.format());
        return numberFormat.format(money);
    }
}
