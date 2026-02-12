package com.example.xdownloader.domain.usecase;

import com.example.xdownloader.data.repository.DownloadRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class CancelDownloadUseCase_Factory implements Factory<CancelDownloadUseCase> {
  private final Provider<DownloadRepository> downloadRepositoryProvider;

  public CancelDownloadUseCase_Factory(Provider<DownloadRepository> downloadRepositoryProvider) {
    this.downloadRepositoryProvider = downloadRepositoryProvider;
  }

  @Override
  public CancelDownloadUseCase get() {
    return newInstance(downloadRepositoryProvider.get());
  }

  public static CancelDownloadUseCase_Factory create(
      Provider<DownloadRepository> downloadRepositoryProvider) {
    return new CancelDownloadUseCase_Factory(downloadRepositoryProvider);
  }

  public static CancelDownloadUseCase newInstance(DownloadRepository downloadRepository) {
    return new CancelDownloadUseCase(downloadRepository);
  }
}
