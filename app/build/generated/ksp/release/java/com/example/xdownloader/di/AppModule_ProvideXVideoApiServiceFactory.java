package com.example.xdownloader.di;

import com.example.xdownloader.data.api.XVideoApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AppModule_ProvideXVideoApiServiceFactory implements Factory<XVideoApiService> {
  @Override
  public XVideoApiService get() {
    return provideXVideoApiService();
  }

  public static AppModule_ProvideXVideoApiServiceFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static XVideoApiService provideXVideoApiService() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideXVideoApiService());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideXVideoApiServiceFactory INSTANCE = new AppModule_ProvideXVideoApiServiceFactory();
  }
}
