package container.restaurant.android.di

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import container.restaurant.android.DATABASE_NAME
import container.restaurant.android.data.db.AppDatabase
import container.restaurant.android.data.repository.*
import container.restaurant.android.worker.MainFoodDatabaseWorker
import container.restaurant.android.worker.SideDishDatabaseWorker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val dataModule = module {
    single<AuthRepository> { AuthDataRepository(get()) }
    single<FeedExploreRepository> { FeedExploreRepository(get()) }
    single<FeedWriteRepository> { FeedWriteRepository(get(), get(), get()) }
    single<MyRepository> { MyRepository(get())  }
    single<RestaurantRepository> { ResDataRepository(get()) }
    single<HomeRepository> { HomeRepository(get(), get())}
    single<FeedDetailRepository> { FeedDetailRepository(get(), get())}
}

val roomDBModule = module {
    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, DATABASE_NAME).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val mainFoodRequest = OneTimeWorkRequestBuilder<MainFoodDatabaseWorker>().build()
                val sideDishRequest = OneTimeWorkRequestBuilder<SideDishDatabaseWorker>().build()
                WorkManager.getInstance(androidContext()).beginWith(mainFoodRequest).then(sideDishRequest).enqueue()
            }
        }).build()
    }
    single { get<AppDatabase>().mainFoodDao() }
    single { get<AppDatabase>().sideDishDao() }
}