package com.example.xdownloader.domain.usecase;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class ParseXLinkUseCase_Factory implements Factory<ParseXLinkUseCase> {
  @Override
  public ParseXLinkUseCase get() {
    return newInstance();
  }

  public static ParseXLinkUseCase_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ParseXLinkUseCase newInstance() {
    return new ParseXLinkUseCase();
  }

  private static final class InstanceHolder {
    private static final ParseXLinkUseCase_Factory INSTANCE = new ParseXLinkUseCase_Factory();
  }
}
