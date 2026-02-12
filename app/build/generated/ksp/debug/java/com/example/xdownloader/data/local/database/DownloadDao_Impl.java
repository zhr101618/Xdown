package com.example.xdownloader.data.local.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.xdownloader.data.local.database.entities.DownloadTaskEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DownloadDao_Impl implements DownloadDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DownloadTaskEntity> __insertionAdapterOfDownloadTaskEntity;

  private final EntityDeletionOrUpdateAdapter<DownloadTaskEntity> __updateAdapterOfDownloadTaskEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateDownloadProgress;

  private final SharedSQLiteStatement __preparedStmtOfMarkAsCompleted;

  private final SharedSQLiteStatement __preparedStmtOfDeleteDownload;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllDownloads;

  private final SharedSQLiteStatement __preparedStmtOfDeleteInactiveDownloads;

  public DownloadDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDownloadTaskEntity = new EntityInsertionAdapter<DownloadTaskEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `download_tasks` (`id`,`videoId`,`tweetId`,`quality`,`url`,`fileName`,`thumbnailUrl`,`videoTitle`,`fileSize`,`duration`,`status`,`progress`,`downloadedBytes`,`filePath`,`thumbnailPath`,`createdAt`,`completedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DownloadTaskEntity value) {
        stmt.bindLong(1, value.getId());
        if (value.getVideoId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getVideoId());
        }
        if (value.getTweetId() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getTweetId());
        }
        if (value.getQuality() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getQuality());
        }
        if (value.getUrl() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getUrl());
        }
        if (value.getFileName() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getFileName());
        }
        if (value.getThumbnailUrl() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getThumbnailUrl());
        }
        if (value.getVideoTitle() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getVideoTitle());
        }
        stmt.bindLong(9, value.getFileSize());
        stmt.bindLong(10, value.getDuration());
        if (value.getStatus() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getStatus());
        }
        stmt.bindLong(12, value.getProgress());
        stmt.bindLong(13, value.getDownloadedBytes());
        if (value.getFilePath() == null) {
          stmt.bindNull(14);
        } else {
          stmt.bindString(14, value.getFilePath());
        }
        if (value.getThumbnailPath() == null) {
          stmt.bindNull(15);
        } else {
          stmt.bindString(15, value.getThumbnailPath());
        }
        stmt.bindLong(16, value.getCreatedAt());
        stmt.bindLong(17, value.getCompletedAt());
      }
    };
    this.__updateAdapterOfDownloadTaskEntity = new EntityDeletionOrUpdateAdapter<DownloadTaskEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `download_tasks` SET `id` = ?,`videoId` = ?,`tweetId` = ?,`quality` = ?,`url` = ?,`fileName` = ?,`thumbnailUrl` = ?,`videoTitle` = ?,`fileSize` = ?,`duration` = ?,`status` = ?,`progress` = ?,`downloadedBytes` = ?,`filePath` = ?,`thumbnailPath` = ?,`createdAt` = ?,`completedAt` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DownloadTaskEntity value) {
        stmt.bindLong(1, value.getId());
        if (value.getVideoId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getVideoId());
        }
        if (value.getTweetId() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getTweetId());
        }
        if (value.getQuality() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getQuality());
        }
        if (value.getUrl() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getUrl());
        }
        if (value.getFileName() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getFileName());
        }
        if (value.getThumbnailUrl() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getThumbnailUrl());
        }
        if (value.getVideoTitle() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getVideoTitle());
        }
        stmt.bindLong(9, value.getFileSize());
        stmt.bindLong(10, value.getDuration());
        if (value.getStatus() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getStatus());
        }
        stmt.bindLong(12, value.getProgress());
        stmt.bindLong(13, value.getDownloadedBytes());
        if (value.getFilePath() == null) {
          stmt.bindNull(14);
        } else {
          stmt.bindString(14, value.getFilePath());
        }
        if (value.getThumbnailPath() == null) {
          stmt.bindNull(15);
        } else {
          stmt.bindString(15, value.getThumbnailPath());
        }
        stmt.bindLong(16, value.getCreatedAt());
        stmt.bindLong(17, value.getCompletedAt());
        stmt.bindLong(18, value.getId());
      }
    };
    this.__preparedStmtOfUpdateDownloadProgress = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE download_tasks SET status = ?, progress = ?, downloadedBytes = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkAsCompleted = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE download_tasks SET status = ?, filePath = ?, thumbnailPath = ?, completedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteDownload = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM download_tasks WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllDownloads = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM download_tasks";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteInactiveDownloads = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM download_tasks WHERE status = 'COMPLETED' OR status = 'FAILED' OR status = 'CANCELLED'";
        return _query;
      }
    };
  }

  @Override
  public Object insertDownload(final DownloadTaskEntity download,
      final Continuation<? super Long> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfDownloadTaskEntity.insertAndReturnId(download);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object updateDownload(final DownloadTaskEntity download,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDownloadTaskEntity.handle(download);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object updateDownloadProgress(final long id, final String status, final int progress,
      final long downloadedBytes, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateDownloadProgress.acquire();
        int _argIndex = 1;
        if (status == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, status);
        }
        _argIndex = 2;
        _stmt.bindLong(_argIndex, progress);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, downloadedBytes);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, id);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfUpdateDownloadProgress.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object markAsCompleted(final long id, final String status, final String filePath,
      final String thumbnailPath, final long completedAt,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkAsCompleted.acquire();
        int _argIndex = 1;
        if (status == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, status);
        }
        _argIndex = 2;
        if (filePath == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, filePath);
        }
        _argIndex = 3;
        if (thumbnailPath == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, thumbnailPath);
        }
        _argIndex = 4;
        _stmt.bindLong(_argIndex, completedAt);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, id);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfMarkAsCompleted.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteDownload(final long id, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteDownload.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteDownload.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteAllDownloads(final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllDownloads.acquire();
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteAllDownloads.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteInactiveDownloads(final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteInactiveDownloads.acquire();
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteInactiveDownloads.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<DownloadTaskEntity>> getAllDownloads() {
    final String _sql = "SELECT * FROM download_tasks ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"download_tasks"}, new Callable<List<DownloadTaskEntity>>() {
      @Override
      public List<DownloadTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVideoId = CursorUtil.getColumnIndexOrThrow(_cursor, "videoId");
          final int _cursorIndexOfTweetId = CursorUtil.getColumnIndexOrThrow(_cursor, "tweetId");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
          final int _cursorIndexOfVideoTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "videoTitle");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "progress");
          final int _cursorIndexOfDownloadedBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadedBytes");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfThumbnailPath = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailPath");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<DownloadTaskEntity> _result = new ArrayList<DownloadTaskEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DownloadTaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpVideoId;
            if (_cursor.isNull(_cursorIndexOfVideoId)) {
              _tmpVideoId = null;
            } else {
              _tmpVideoId = _cursor.getString(_cursorIndexOfVideoId);
            }
            final String _tmpTweetId;
            if (_cursor.isNull(_cursorIndexOfTweetId)) {
              _tmpTweetId = null;
            } else {
              _tmpTweetId = _cursor.getString(_cursorIndexOfTweetId);
            }
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final String _tmpUrl;
            if (_cursor.isNull(_cursorIndexOfUrl)) {
              _tmpUrl = null;
            } else {
              _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            }
            final String _tmpFileName;
            if (_cursor.isNull(_cursorIndexOfFileName)) {
              _tmpFileName = null;
            } else {
              _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            }
            final String _tmpThumbnailUrl;
            if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null;
            } else {
              _tmpThumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl);
            }
            final String _tmpVideoTitle;
            if (_cursor.isNull(_cursorIndexOfVideoTitle)) {
              _tmpVideoTitle = null;
            } else {
              _tmpVideoTitle = _cursor.getString(_cursorIndexOfVideoTitle);
            }
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final long _tmpDuration;
            _tmpDuration = _cursor.getLong(_cursorIndexOfDuration);
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            final int _tmpProgress;
            _tmpProgress = _cursor.getInt(_cursorIndexOfProgress);
            final long _tmpDownloadedBytes;
            _tmpDownloadedBytes = _cursor.getLong(_cursorIndexOfDownloadedBytes);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpThumbnailPath;
            if (_cursor.isNull(_cursorIndexOfThumbnailPath)) {
              _tmpThumbnailPath = null;
            } else {
              _tmpThumbnailPath = _cursor.getString(_cursorIndexOfThumbnailPath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpCompletedAt;
            _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            _item = new DownloadTaskEntity(_tmpId,_tmpVideoId,_tmpTweetId,_tmpQuality,_tmpUrl,_tmpFileName,_tmpThumbnailUrl,_tmpVideoTitle,_tmpFileSize,_tmpDuration,_tmpStatus,_tmpProgress,_tmpDownloadedBytes,_tmpFilePath,_tmpThumbnailPath,_tmpCreatedAt,_tmpCompletedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DownloadTaskEntity>> getActiveDownloads() {
    final String _sql = "SELECT * FROM download_tasks WHERE status = 'DOWNLOADING' OR status = 'PENDING' ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"download_tasks"}, new Callable<List<DownloadTaskEntity>>() {
      @Override
      public List<DownloadTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVideoId = CursorUtil.getColumnIndexOrThrow(_cursor, "videoId");
          final int _cursorIndexOfTweetId = CursorUtil.getColumnIndexOrThrow(_cursor, "tweetId");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
          final int _cursorIndexOfVideoTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "videoTitle");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "progress");
          final int _cursorIndexOfDownloadedBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadedBytes");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfThumbnailPath = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailPath");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final List<DownloadTaskEntity> _result = new ArrayList<DownloadTaskEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DownloadTaskEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpVideoId;
            if (_cursor.isNull(_cursorIndexOfVideoId)) {
              _tmpVideoId = null;
            } else {
              _tmpVideoId = _cursor.getString(_cursorIndexOfVideoId);
            }
            final String _tmpTweetId;
            if (_cursor.isNull(_cursorIndexOfTweetId)) {
              _tmpTweetId = null;
            } else {
              _tmpTweetId = _cursor.getString(_cursorIndexOfTweetId);
            }
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final String _tmpUrl;
            if (_cursor.isNull(_cursorIndexOfUrl)) {
              _tmpUrl = null;
            } else {
              _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            }
            final String _tmpFileName;
            if (_cursor.isNull(_cursorIndexOfFileName)) {
              _tmpFileName = null;
            } else {
              _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            }
            final String _tmpThumbnailUrl;
            if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null;
            } else {
              _tmpThumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl);
            }
            final String _tmpVideoTitle;
            if (_cursor.isNull(_cursorIndexOfVideoTitle)) {
              _tmpVideoTitle = null;
            } else {
              _tmpVideoTitle = _cursor.getString(_cursorIndexOfVideoTitle);
            }
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final long _tmpDuration;
            _tmpDuration = _cursor.getLong(_cursorIndexOfDuration);
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            final int _tmpProgress;
            _tmpProgress = _cursor.getInt(_cursorIndexOfProgress);
            final long _tmpDownloadedBytes;
            _tmpDownloadedBytes = _cursor.getLong(_cursorIndexOfDownloadedBytes);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpThumbnailPath;
            if (_cursor.isNull(_cursorIndexOfThumbnailPath)) {
              _tmpThumbnailPath = null;
            } else {
              _tmpThumbnailPath = _cursor.getString(_cursorIndexOfThumbnailPath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpCompletedAt;
            _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            _item = new DownloadTaskEntity(_tmpId,_tmpVideoId,_tmpTweetId,_tmpQuality,_tmpUrl,_tmpFileName,_tmpThumbnailUrl,_tmpVideoTitle,_tmpFileSize,_tmpDuration,_tmpStatus,_tmpProgress,_tmpDownloadedBytes,_tmpFilePath,_tmpThumbnailPath,_tmpCreatedAt,_tmpCompletedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getDownloadById(final long id,
      final Continuation<? super DownloadTaskEntity> continuation) {
    final String _sql = "SELECT * FROM download_tasks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DownloadTaskEntity>() {
      @Override
      public DownloadTaskEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVideoId = CursorUtil.getColumnIndexOrThrow(_cursor, "videoId");
          final int _cursorIndexOfTweetId = CursorUtil.getColumnIndexOrThrow(_cursor, "tweetId");
          final int _cursorIndexOfQuality = CursorUtil.getColumnIndexOrThrow(_cursor, "quality");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfFileName = CursorUtil.getColumnIndexOrThrow(_cursor, "fileName");
          final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
          final int _cursorIndexOfVideoTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "videoTitle");
          final int _cursorIndexOfFileSize = CursorUtil.getColumnIndexOrThrow(_cursor, "fileSize");
          final int _cursorIndexOfDuration = CursorUtil.getColumnIndexOrThrow(_cursor, "duration");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfProgress = CursorUtil.getColumnIndexOrThrow(_cursor, "progress");
          final int _cursorIndexOfDownloadedBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "downloadedBytes");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfThumbnailPath = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailPath");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final DownloadTaskEntity _result;
          if(_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpVideoId;
            if (_cursor.isNull(_cursorIndexOfVideoId)) {
              _tmpVideoId = null;
            } else {
              _tmpVideoId = _cursor.getString(_cursorIndexOfVideoId);
            }
            final String _tmpTweetId;
            if (_cursor.isNull(_cursorIndexOfTweetId)) {
              _tmpTweetId = null;
            } else {
              _tmpTweetId = _cursor.getString(_cursorIndexOfTweetId);
            }
            final String _tmpQuality;
            if (_cursor.isNull(_cursorIndexOfQuality)) {
              _tmpQuality = null;
            } else {
              _tmpQuality = _cursor.getString(_cursorIndexOfQuality);
            }
            final String _tmpUrl;
            if (_cursor.isNull(_cursorIndexOfUrl)) {
              _tmpUrl = null;
            } else {
              _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            }
            final String _tmpFileName;
            if (_cursor.isNull(_cursorIndexOfFileName)) {
              _tmpFileName = null;
            } else {
              _tmpFileName = _cursor.getString(_cursorIndexOfFileName);
            }
            final String _tmpThumbnailUrl;
            if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null;
            } else {
              _tmpThumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl);
            }
            final String _tmpVideoTitle;
            if (_cursor.isNull(_cursorIndexOfVideoTitle)) {
              _tmpVideoTitle = null;
            } else {
              _tmpVideoTitle = _cursor.getString(_cursorIndexOfVideoTitle);
            }
            final long _tmpFileSize;
            _tmpFileSize = _cursor.getLong(_cursorIndexOfFileSize);
            final long _tmpDuration;
            _tmpDuration = _cursor.getLong(_cursorIndexOfDuration);
            final String _tmpStatus;
            if (_cursor.isNull(_cursorIndexOfStatus)) {
              _tmpStatus = null;
            } else {
              _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            }
            final int _tmpProgress;
            _tmpProgress = _cursor.getInt(_cursorIndexOfProgress);
            final long _tmpDownloadedBytes;
            _tmpDownloadedBytes = _cursor.getLong(_cursorIndexOfDownloadedBytes);
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpThumbnailPath;
            if (_cursor.isNull(_cursorIndexOfThumbnailPath)) {
              _tmpThumbnailPath = null;
            } else {
              _tmpThumbnailPath = _cursor.getString(_cursorIndexOfThumbnailPath);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpCompletedAt;
            _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            _result = new DownloadTaskEntity(_tmpId,_tmpVideoId,_tmpTweetId,_tmpQuality,_tmpUrl,_tmpFileName,_tmpThumbnailUrl,_tmpVideoTitle,_tmpFileSize,_tmpDuration,_tmpStatus,_tmpProgress,_tmpDownloadedBytes,_tmpFilePath,_tmpThumbnailPath,_tmpCreatedAt,_tmpCompletedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
