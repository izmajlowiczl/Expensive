package pl.expensive;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.UUID;

import pl.expensive.storage.Wallet;
import pl.expensive.storage.WalletsStorage;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DisplayWalletsTest {
    private static final Wallet CASH = Wallet.create(UUID.randomUUID(), "Cash");
    private static final Wallet CREDIT_CARD = Wallet.create(UUID.randomUUID(), "Credit Card");

    @Mock
    WalletsViewContract view;
    @Mock
    WalletsStorage storage;

    @Test
    public void displayStoredWallets() {
        when(storage.list()).thenReturn(asList(CASH, CREDIT_CARD));
        FetchWallets fetchWallets = new FetchWallets(storage);

        new DisplayWallets(fetchWallets)
                .runFor(view);

        verify(view).showWallets(
                asList(WalletViewModel.create("Cash"), WalletViewModel.create("Credit Card")));
    }

    @Test
    public void displayNoWallets() {
        when(storage.list()).thenReturn(Collections.<Wallet>emptyList());
        FetchWallets fetchWallets = new FetchWallets(storage);

        new DisplayWallets(fetchWallets)
                .runFor(view);

        verify(view).showEmpty();
    }
}