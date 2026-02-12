package com.example.xdownloader.service;

import com.example.xdownloader.data.local.media.MediaStoreHelper;
import com.example.xdownloader.data.repository.DownloadRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class DownloadService_MembersInjector implements MembersInjector<DownloadService> {
  private final Provider<DownloadRepository> downloadRepositoryProvider;

  private final Provider<MediaStoreHelper> mediaStoreHelperProvider;

  public DownloadService_MembersInjector(Provider<DownloadRepository> downloadRepositoryProvider,
      Provider<MediaStoreHelper> mediaStoreHelperProvider) {
    this.downloadRepositoryProvider = downloadRepositoryProvider;
    this.mediaStoreHelperProvider = mediaStoreHelperProvider;
  }

  public static MembersInjector<DownloadService> create(
      Provider<DownloadRepository> downloadRepositoryProvider,
      Provider<MediaStoreHelper> mediaStoreHelperProvider) {
    return new DownloadService_MembersInjector(downloadRepositoryProvider, mediaStoreHelperProvider);
  }

  @Override
  public void injectMembers(DownloadService instance) {
    injectDownloadRepository(instance, downloadRepositoryProvider.get());
    injectMediaStoreHelper(instance, mediaStoreHelperProvider.get());
  }

  @InjectedFieldSignature("com.example.xdownloader.service.DownloadService.downloadRepository")
  public static void injectDownloadRepository(DownloadService instance,
      DownloadRepository downloadRepository) {
    instance.downloadRepository = downloadRepository;
  }

  @InjectedFieldSignature("com.example.xdownloader.service.DownloadService.mediaStoreHelper")
  public static void injectMediaStoreHelper(DownloadService instance,
      MediaStoreHelper mediaStoreHelper) {
    instance.mediaStoreHelper = mediaStoreHelper;
  }
}
