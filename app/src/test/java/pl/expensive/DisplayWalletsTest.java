package pl.expensive;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import pl.expensive.db.WalletModel;
import pl.expensive.db.Wallet;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DisplayWalletsTest {
    @Mock
    WalletModel model;
    @Mock
    WalletsView view;

    @InjectMocks
    DisplayWallets useCase;

    @Test
    public void fetchesWalletsFromStorage() {
        useCase.doInBackground();

        verify(model).list();
    }

    @Test
    public void updatesViewWithStoredWallets() {
        Collection<Wallet> storedWallets = Arrays.asList(
                Wallet.create(UUID.randomUUID(), "Test-wallet"));

        useCase.setCallback(view)
                .onPostExecute(storedWallets);

        verify(view).showWallets(storedWallets);
    }

    @Test
    public void handlesEmptyStateWhenNothingWasStored() {
        Collection<Wallet> noWallets = Collections.emptyList();

        useCase.setCallback(view)
                .onPostExecute(noWallets);

        verify(view).showEmpty();
    }
}