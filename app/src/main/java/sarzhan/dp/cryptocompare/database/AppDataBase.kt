package sarzhan.dp.cryptocompare.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import sarzhan.dp.cryptocompare.pojo.CoinPriceInfo

@Database(entities = [CoinPriceInfo::class], version = 1, exportSchema = false)
abstract class AppDataBase:RoomDatabase() {
    companion object{
        private const val DB_NAME:String = "main.db"
        private var db:AppDataBase? = null
        private val LOCK = Any()

        fun getInstance (context: Context):AppDataBase{
            synchronized(LOCK) {
                db?.let { return it }
                val instance =
                    Room.databaseBuilder(context, AppDataBase::class.java, DB_NAME).build()
                db = instance
                return instance
            }
        }
    }

abstract fun coinPriceInfoDao():CoinPriceInfoDao
}