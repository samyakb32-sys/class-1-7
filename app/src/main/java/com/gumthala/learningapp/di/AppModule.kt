package com.gumthala.learningapp.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.gumthala.learningapp.data.local.AppDatabase
import com.gumthala.learningapp.data.remote.FirestoreRemoteDataSource
import com.gumthala.learningapp.data.remote.NoOpRemoteDataSource
import com.gumthala.learningapp.data.remote.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val SCHOOL_ID = "zp_primary_gumthala"

    @Provides
    @Singleton
    fun database(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.NAME)
            .fallbackToDestructiveMigrationOnDowngrade()
            .build()

    @Provides fun userDao(db: AppDatabase) = db.userDao()
    @Provides fun contentDao(db: AppDatabase) = db.contentDao()
    @Provides fun attemptDao(db: AppDatabase) = db.attemptDao()
    @Provides fun progressDao(db: AppDatabase) = db.progressDao()
    @Provides fun badgeDao(db: AppDatabase) = db.badgeDao()
    @Provides fun slideDao(db: AppDatabase) = db.slideDao()

    @Provides
    @Singleton
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Firestore is strictly optional. If google-services.json is missing,
     * FirebaseApp.initializeApp returns null and we fall back to a no-op source,
     * so the app stays a working offline product.
     */
    @Provides
    @Singleton
    fun remoteDataSource(@ApplicationContext context: Context): RemoteDataSource {
        val app = runCatching { FirebaseApp.initializeApp(context) }.getOrNull()
            ?: return NoOpRemoteDataSource()
        return runCatching {
            val firestore = FirebaseFirestore.getInstance(app).apply {
                firestoreSettings = FirebaseFirestoreSettings.Builder()
                    .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
                    .build()
            }
            FirestoreRemoteDataSource(firestore, SCHOOL_ID) as RemoteDataSource
        }.getOrElse { NoOpRemoteDataSource() }
    }
}
