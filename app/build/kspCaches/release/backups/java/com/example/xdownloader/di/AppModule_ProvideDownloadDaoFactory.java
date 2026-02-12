package com.example.xdownloader.di;

import com.example.xdownloader.data.local.database.AppDatabase;
import com.example.xdownloader.data.local.database.DownloadDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class AppModule_ProvideDownloadDaoFactory implements Factory<DownloadDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideDownloadDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public DownloadDao get() {
    return provideDownloadDao(databaseProvider.get());
  }

  public static AppModule_ProvideDownloadDaoFactory create(Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideDownloadDaoFactory(databaseProvider);
  }

  public static DownloadDao provideDownloadDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideDownloadDao(database));
  }
}
