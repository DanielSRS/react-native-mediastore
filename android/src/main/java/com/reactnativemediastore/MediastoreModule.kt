package com.reactnativemediastore

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.facebook.react.bridge.*

class MediastoreModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "Mediastore"
    }

    private fun mapFiles(
      isMusic: Boolean,
      collection: Uri,
      externalContentUri: Uri,
      idColumn: String,
      nameColumn: String,
      durationColumn: String,
      sizeColumn: String,
      mimeColumn: String,
      titleColumn: String,
      albumColumn: String,
      artistColumn: String,
      albumId: String
    ): Array<WritableMap> {

      val files = mutableListOf<WritableMap>()

      var projection = arrayOf(
        idColumn,
        nameColumn,
        durationColumn,
        sizeColumn,
        mimeColumn,
        titleColumn,
        albumColumn,
        artistColumn
      )

      if (isMusic) {
        projection = arrayOf(
          idColumn,
          nameColumn,
          durationColumn,
          sizeColumn,
          mimeColumn,
          titleColumn,
          albumColumn,
          artistColumn,
          albumId
        )
      }

      val query = reactApplicationContext.contentResolver.query(
        collection,
        projection,
        null,
        null,
        null
      )
      query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(idColumn)
        val nameColumn = cursor.getColumnIndexOrThrow(nameColumn)
        val durationColumn = cursor.getColumnIndexOrThrow(durationColumn)
        val sizeColumn = cursor.getColumnIndexOrThrow(sizeColumn)
        val mimeColumn = cursor.getColumnIndexOrThrow(mimeColumn)
        val titleColumn = cursor.getColumnIndexOrThrow(titleColumn)
        val albumColumn = cursor.getColumnIndexOrThrow(albumColumn)
        val artistColumn = cursor.getColumnIndexOrThrow(artistColumn)
        var albumIdColumn = -1
        if (isMusic) {
          albumIdColumn = cursor.getColumnIndexOrThrow(albumId)
        }

        while (cursor.moveToNext()) {

          val item = Arguments.createMap()
          val id = cursor.getLong(idColumn)

          item.putInt("id", id.toInt())
          item.putString("name", cursor.getString(nameColumn))
          item.putInt("duration", cursor.getInt(durationColumn))
          item.putInt("size", cursor.getInt(sizeColumn))
          item.putString("mime", cursor.getString(mimeColumn))
          item.putString("title", cursor.getString(titleColumn))
          item.putString("album", cursor.getString(albumColumn))
          item.putString("artist", cursor.getString(artistColumn))
          item.putString("contentUri", "content://media" + externalContentUri.path + "/" + id)
          if (isMusic) {
            val albumID = cursor.getInt(albumIdColumn).toString()
            item.putString("albumId", albumID)
            item.putString("albumArt" , "content://media/external/audio/albumart/$albumID")
          } else {
            item.putString("albumId", "")
            item.putString("albumArt" , "")
          }

          files += item
        }
      }

      return files.toTypedArray()
    }

    @ReactMethod
    fun readAudioVideoExternalMedias(promise: Promise) {

      data class Media(
        val uri: Uri,
        val name: String,
        val duration: Int,
        val size: Int
      )
      val mediaList = Arguments.createArray()

      mapFiles(
        true,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
          MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        },
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
        MediaStore.Audio.Media._ID,
        MediaStore.Audio.Media.DISPLAY_NAME,
        MediaStore.Audio.Media.DURATION,
        MediaStore.Audio.Media.SIZE,
        MediaStore.Audio.Media.MIME_TYPE,
        MediaStore.Audio.Media.TITLE,
        MediaStore.Audio.Media.ALBUM,
        MediaStore.Audio.Media.ARTIST,
        MediaStore.Audio.Media.ALBUM_ID
      ).forEach { file ->
        mediaList.pushMap(file)
      }

      mapFiles(
        false,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
          MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        },
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.MIME_TYPE,
        MediaStore.Video.Media.TITLE,
        MediaStore.Video.Media.ALBUM,
        MediaStore.Video.Media.ARTIST,
        "" // Video media do not have album id, just pass an empty string
      ).forEach { file ->
        mediaList.pushMap(file)
      }

      promise.resolve(mediaList)
    }

    private fun mapGenreMembers(
      collection: Uri,
      externalContentUri: Uri,
      memberID: String,
      title: String
    ): Array<WritableMap> {
      val files = mutableListOf<WritableMap>()

      var projection = arrayOf(
        memberID,
        title
      )

      val query = reactApplicationContext.contentResolver.query(
        collection,
        projection,
        null,
        null,
        null
      )
      query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(memberID)
        val titleColumn = cursor.getColumnIndexOrThrow(title)

        while (cursor.moveToNext()) {

          val item = Arguments.createMap()
          val id = cursor.getLong(idColumn)

          item.putInt("audioID", id.toInt())
          item.putString("title", cursor.getString(titleColumn))


          files += item
        }
      }

      return files.toTypedArray()
    }

    @ReactMethod
    fun readGenreMembers(genreID: Int, promise: Promise) {
      val mediaList = Arguments.createArray()

      val id = genreID.toLong()

      mapGenreMembers(
        MediaStore.Audio.Genres.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, id),
        MediaStore.Audio.Genres.Members.getContentUri(MediaStore.VOLUME_EXTERNAL, id),
        MediaStore.Audio.Genres.Members._ID,
        MediaStore.Audio.Genres.Members.DURATION
      ).forEach { file -> mediaList.pushMap(file) }

      promise.resolve(mediaList)
    }

    private  fun mapGenres(
      collection: Uri,
      externalContentUri: Uri,
      genreId: String,
      genreName: String
    ): Array<WritableMap> {
      val files = mutableListOf<WritableMap>()

      var projection = arrayOf(
        genreId,
        genreName
      )

      val query = reactApplicationContext.contentResolver.query(
        collection,
        projection,
        null,
        null,
        null
      )
      query?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(genreId)
        val nameColumn = cursor.getColumnIndexOrThrow(genreName)

        while (cursor.moveToNext()) {

          val item = Arguments.createMap()
          val id = cursor.getLong(idColumn)

          item.putInt("id", id.toInt())
          item.putString("name", cursor.getString(nameColumn))
          item.putString("contentUri", "content://media" + externalContentUri.path + "/" + id)

          files += item
        }
      }

      return files.toTypedArray()
    }

    @ReactMethod
    fun readGenreMedias(promise: Promise) {
      val mediaList = Arguments.createArray()

      mapGenres(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          MediaStore.Audio.Genres.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
          MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI
        },
        MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI,
        MediaStore.Audio.Genres._ID,
        MediaStore.Audio.Genres.NAME
      ).forEach { file -> mediaList.pushMap(file) }

      promise.resolve(mediaList)
    }
}
