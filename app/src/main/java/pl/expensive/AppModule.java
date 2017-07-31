package pl.expensive;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import pl.expensive.storage.Database;
import pl.expensive.transaction.list.TransactionsModel;

@Module
public class AppModule {
    private final Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    TransactionsModel transactionsModel(Database db,
                                        Resources resources,
                                        SharedPreferences preferences) {
        return new TransactionsModel(db, resources, preferences);
    }

    @Provides
    @Singleton
    Resources resources() {
        return context.getResources();
    }

    @Provides
    @Singleton
    SharedPreferences preferences() {
        return context.getSharedPreferences("expensive_prefs", 0);
    }

    @Singleton
    @Provides
    Database database(SharedPreferences prefs) {
        return new Database(context, prefs);
    }
}
