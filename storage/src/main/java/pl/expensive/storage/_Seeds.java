package pl.expensive.storage;

import java.util.UUID;

public class _Seeds {
    public static final Wallet CASH = Wallet.create(
            UUID.fromString("c2ee3260-94eb-4cc2-8d5c-af38f964fbd5"), "cash");

    public static final Currency EUR = Currency.create("EUR", "##.##\u00a0\u20ac");
    public static final Currency GBP = Currency.create("GBP", "\u00a3##.##");
    public static final Currency CHF = Currency.create("CHF", "##.##\u00a0CHF");
    public static final Currency PLN = Currency.create("PLN", "##.##\u00a0zł");
    public static final Currency CZK = Currency.create("CZK", "##.##\u00a0K\u010d");
}
