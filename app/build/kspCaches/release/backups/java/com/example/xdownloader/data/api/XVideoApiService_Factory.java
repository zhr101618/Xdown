package com.example.xdownloader.data.api;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class XVideoApiService_Factory implements Factory<XVideoApiService> {
  @Override
  public XVideoApiService get() {
    return newInstance();
  }

  public static XVideoApiService_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static XVideoApiService newInstance() {
    return new XVideoApiService();
  }

  private static final class InstanceHolder {
    private static final XVideoApiService_Factory INSTANCE = new XVideoApiService_Factory();
  }
}
