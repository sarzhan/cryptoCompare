package sarzhan.dp.cryptocompare


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_coin_price_list.*
import sarzhan.dp.cryptocompare.pojo.CoinPriceInfo


class CoinPriceListActivity : AppCompatActivity() {

    private lateinit var viewModel: CoinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_price_list)
        val adapter = CoinInfoAdapter(this)
        adapter.onCoinClickListener = object :CoinInfoAdapter.OnCoinClickListener{
            override fun OnCoinClick(coinPriceInfo: CoinPriceInfo) {
                val intent = CoinDetailActivity.newIntent(this@CoinPriceListActivity,coinPriceInfo.fromSymbol)
                startActivity(intent)
            }

        }
        rvCoinPriceList.adapter = adapter
        viewModel = ViewModelProvider(this)[CoinViewModel::class.java]
        viewModel.priceList.observe(this, Observer { adapter.coinInfoList = it })

    }

}
