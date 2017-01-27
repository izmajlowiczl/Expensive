package pl.expensive;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import pl.expensive.storage.Transaction;
import pl.expensive.storage.TransactionStorage;
import pl.expensive.storage.Wallet;
import pl.expensive.storage.WalletsStorage;
import pl.expensive.storage._Seeds;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.expensive.storage._Seeds.EUR;

@RunWith(MockitoJUnitRunner.class)
public class DisplayWalletsTest {
    private static final Wallet CASH = _Seeds.CASH;
    private static final Wallet CREDIT_CARD = Wallet.create(UUID.randomUUID(), "Credit Card", EUR);

    @Mock
    WalletsViewContract view;
    @Mock
    WalletsStorage walletsStorage;
    @Mock
    TransactionStorage transactionStorage;

    @Test
    public void displayStoredWallets() {
        when(walletsStorage.list()).thenReturn(asList(CASH, CREDIT_CARD));

        DisplayWallets displayWallets = new DisplayWallets(walletsStorage, transactionStorage);
        displayWallets.runFor(view);

        verify(view).showWallets(asList(
                WalletViewModel.create("Cash", Collections.emptyList(), EUR),
                WalletViewModel.create("Credit Card", Collections.emptyList(), EUR)));
    }

    @Test
    public void containTransactions() {
        when(walletsStorage.list()).thenReturn(asList(CASH, CREDIT_CARD));
        List<Transaction> transactions = asList(Transaction.deposit(CASH.uuid(), BigDecimal.TEN, EUR, ""));
        when(transactionStorage.select(CASH.uuid())).thenReturn(transactions);

        DisplayWallets displayWallets = new DisplayWallets(walletsStorage, transactionStorage);
        displayWallets.runFor(view);

        verify(view).showWallets(asList(
                WalletViewModel.create("Cash", transactions, EUR),
                WalletViewModel.create("Credit Card", Collections.emptyList(), EUR)));
    }

    @Test
    public void usePrimeCurrencyWhenNoTransactions() {
        when(walletsStorage.list()).thenReturn(asList(CASH));
        when(transactionStorage.select(CASH.uuid())).thenReturn(Collections.emptyList());

        DisplayWallets displayWallets = new DisplayWallets(walletsStorage, transactionStorage);
        displayWallets.runFor(view);

        verify(view).showWallets(asList(
                WalletViewModel.create("Cash", Collections.emptyList(), EUR)));
    }

    @Test
    public void displayNoWallets() {
        when(walletsStorage.list()).thenReturn(Collections.<Wallet>emptyList());

        DisplayWallets displayWallets = new DisplayWallets(walletsStorage, transactionStorage);
        displayWallets.runFor(view);

        verify(view).showEmpty();
    }
}
