package com.example.xdownloader.di;

import com.example.xdownloader.data.api.XVideoService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class AppModule_ProvideXVideoServiceFactory implements Factory<XVideoService> {
  private final Provider<Retrofit> retrofitProvider;

  public AppModule_ProvideXVideoServiceFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public XVideoService get() {
    return provideXVideoService(retrofitProvider.get());
  }

  public static AppModule_ProvideXVideoServiceFactory create(Provider<Retrofit> retrofitProvider) {
    return new AppModule_ProvideXVideoServiceFactory(retrofitProvider);
  }

  public static XVideoService provideXVideoService(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideXVideoService(retrofit));
  }
}
