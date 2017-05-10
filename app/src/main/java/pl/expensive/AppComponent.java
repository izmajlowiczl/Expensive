package pl.expensive;

import javax.inject.Singleton;

import dagger.Component;
import pl.expensive.storage.StorageModule;
import pl.expensive.storage.TransactionStorage;
import pl.expensive.transaction.NewTransactionActivity;
import pl.expensive.wallet.TransactionsActivity;
import pl.expensive.wallet.TransactionsModel;

@Singleton
@Component(modules = {StorageModule.class, AppModule.class})
public interface AppComponent {
    void inject(TransactionsActivity transactionsActivity);
    void inject(NewTransactionActivity newTransactionActivity);

    TransactionsModel transactionsModel();

    TransactionStorage transactions();
}
