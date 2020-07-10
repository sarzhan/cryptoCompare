package sarzhan.dp.cryptocompare

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import sarzhan.dp.cryptocompare.api.ApiFactory
import sarzhan.dp.cryptocompare.database.AppDataBase
import sarzhan.dp.cryptocompare.pojo.CoinPriceInfo
import sarzhan.dp.cryptocompare.pojo.CoinPriceInfoRawData
import java.util.concurrent.TimeUnit

class CoinViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDataBase.getInstance(application)
    private val compositeDisposable = CompositeDisposable()

    val priceList = db.coinPriceInfoDao().getPriceList()
    fun getDetailInfo(fSym:String) :LiveData<CoinPriceInfo>{
        return db.coinPriceInfoDao().getPriceInfoAboutCoin(fSym)
    }

    init {
        loadData()
    }

    private fun loadData() {
        val disposable = ApiFactory.apiService.getTopCoinsInfo(limit = 50)
            .map { it.data?.map { it.coinInfo?.name }?.joinToString (",")  }
            .flatMap { ApiFactory.apiService.getFullPriceList(fsyms = it) }
            .map { getPriceListFromRawData(it) }
            .delay(10,TimeUnit.SECONDS)
            .repeat()
            .retry()
            .subscribeOn(Schedulers.io())
            .subscribe({
                db.coinPriceInfoDao().insertPriceList(it)
                Log.d("TEST", "Success $it")
            }, {
                Log.d("TEST", "Failure ${it.message}")
            })
        compositeDisposable.add(disposable)
    }

    private fun getPriceListFromRawData(coinPriceInfoRawData: CoinPriceInfoRawData)
            : List<CoinPriceInfo> {
        val result = ArrayList<CoinPriceInfo>()
        val jsonObjects = coinPriceInfoRawData.coinPriceInfoJsonObject ?: return result
        val coinKeySet = jsonObjects.keySet()
        for (coinKey in coinKeySet) {
            val currencyJson = jsonObjects.getAsJsonObject(coinKey)
            val currencyKeySet = currencyJson.keySet()
            for (currencyKey in currencyKeySet) {
                val priceInfo = Gson().fromJson(
                    currencyJson.getAsJsonObject(currencyKey),
                    CoinPriceInfo::class.java
                )
                result.add(priceInfo)
            }

        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}