package com.example.xdownloader.di;

import android.content.Context;
import com.example.xdownloader.data.local.media.MediaStoreHelper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class AppModule_ProvideMediaStoreHelperFactory implements Factory<MediaStoreHelper> {
  private final Provider<Context> contextProvider;

  public AppModule_ProvideMediaStoreHelperFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public MediaStoreHelper get() {
    return provideMediaStoreHelper(contextProvider.get());
  }

  public static AppModule_ProvideMediaStoreHelperFactory create(Provider<Context> contextProvider) {
    return new AppModule_ProvideMediaStoreHelperFactory(contextProvider);
  }

  public static MediaStoreHelper provideMediaStoreHelper(Context context) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideMediaStoreHelper(context));
  }
}
