package pl.expensive;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import pl.expensive.storage.Wallet;
import pl.expensive.storage.WalletsStorage;
import rx.observers.TestSubscriber;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FetchWalletsTest {
    @Mock
    WalletsStorage storage;
    @InjectMocks
    FetchWallets fetchWallets;

    TestSubscriber<Collection<Wallet>> subscriber = new TestSubscriber<>();

    @Test
    public void storedWallets() {
        Collection<Wallet> storedWallets = asList(
                Wallet.create(UUID.randomUUID(), "Cash"),
                Wallet.create(UUID.randomUUID(), "Credit Card"));
        when(storage.list()).thenReturn(storedWallets);

        fetchWallets.fetchWallets()
                .subscribe(subscriber);

        subscriber.assertValue(storedWallets);
    }

    @Test
    public void noWallets() {
        Collection<Wallet> storedWallets = Collections.emptyList();
        when(storage.list()).thenReturn(storedWallets);

        fetchWallets.fetchWallets()
                .subscribe(subscriber);

        subscriber.assertValue(storedWallets);
    }
}