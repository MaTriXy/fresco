/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.imagepipeline.cache;

import android.net.Uri;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.Postprocessor;
import com.facebook.infer.annotation.Nullsafe;
import javax.annotation.Nullable;

/** Default implementation of {@link CacheKeyFactory}. */
@Nullsafe(Nullsafe.Mode.LOCAL)
public class DefaultCacheKeyFactory implements CacheKeyFactory {

  private static @Nullable DefaultCacheKeyFactory sInstance = null;

  protected DefaultCacheKeyFactory() {}

  public static synchronized DefaultCacheKeyFactory getInstance() {
    if (sInstance == null) {
      sInstance = new DefaultCacheKeyFactory();
    }
    return sInstance;
  }

  @Override
  public CacheKey getBitmapCacheKey(ImageRequest request, @Nullable Object callerContext) {
    BitmapMemoryCacheKey cacheKey =
        new BitmapMemoryCacheKey(
            getCacheKeySourceUri(request.getSourceUri()).toString(),
            request.getResizeOptions(),
            request.getRotationOptions(),
            request.getImageDecodeOptions(),
            null,
            null);
    return cacheKey;
  }

  @Override
  public CacheKey getPostprocessedBitmapCacheKey(
      ImageRequest request, @Nullable Object callerContext) {
    final Postprocessor postprocessor = request.getPostprocessor();
    final CacheKey postprocessorCacheKey;
    final String postprocessorName;
    if (postprocessor != null) {
      postprocessorCacheKey = postprocessor.getPostprocessorCacheKey();
      postprocessorName = postprocessor.getClass().getName();
    } else {
      postprocessorCacheKey = null;
      postprocessorName = null;
    }
    BitmapMemoryCacheKey cacheKey =
        new BitmapMemoryCacheKey(
            getCacheKeySourceUri(request.getSourceUri()).toString(),
            request.getResizeOptions(),
            request.getRotationOptions(),
            request.getImageDecodeOptions(),
            postprocessorCacheKey,
            postprocessorName);
    return cacheKey;
  }

  @Override
  public CacheKey getEncodedCacheKey(ImageRequest request, @Nullable Object callerContext) {
    return getEncodedCacheKey(request, request.getSourceUri(), callerContext);
  }

  @Override
  public CacheKey getEncodedCacheKey(
      ImageRequest request, Uri sourceUri, @Nullable Object callerContext) {
    return new SimpleCacheKey(getCacheKeySourceUri(sourceUri).toString());
  }

  /**
   * @return a {@link Uri} that unambiguously indicates the source of the image.
   */
  protected Uri getCacheKeySourceUri(Uri sourceUri) {
    return sourceUri;
  }
}
