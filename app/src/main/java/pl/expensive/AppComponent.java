package pl.expensive;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;
import pl.expensive.storage.Database;
import pl.expensive.tag.TagsActivity;
import pl.expensive.transaction.details.TransactionDetailsActivity;
import pl.expensive.transaction.list.TransactionsActivity;
import pl.expensive.transaction.list.TransactionsModel;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(TransactionsActivity transactionsActivity);

    void inject(TransactionDetailsActivity transactionDetailsActivity);

    void inject(TagsActivity tagsActivity);

    TransactionsModel transactionsModel();

    SharedPreferences prefs();

    Database db();
}
