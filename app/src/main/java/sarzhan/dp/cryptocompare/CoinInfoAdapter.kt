package sarzhan.dp.cryptocompare

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_coin_info.view.*
import sarzhan.dp.cryptocompare.pojo.CoinPriceInfo

class CoinInfoAdapter(private val context: Context) :
    RecyclerView.Adapter<CoinInfoAdapter.CoinInfoViewHolder>() {

    var coinInfoList: List<CoinPriceInfo> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onCoinClickListener: OnCoinClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinInfoViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_coin_info, parent, false)
        return CoinInfoViewHolder(view)
    }

    override fun getItemCount() = coinInfoList.size

    override fun onBindViewHolder(holder: CoinInfoViewHolder, position: Int) {
        val coin = coinInfoList[position]
        with(holder) {
            val symbolTemp = context.resources.getString(R.string.symbols_temp)
            val updateTemp = context.resources.getString(R.string.update_temp)

            tvSymbols.text = String.format(symbolTemp, coin.fromSymbol, coin.toSymbol)
            tvPrice.text = coin.price
            tvUpdate.text = String.format(updateTemp, coin.getFormattedTime())
            Picasso.get().load(coin.getFullImageUrl()).into(ivLogoCoin)
            itemView.setOnClickListener { onCoinClickListener?.OnCoinClick(coin) }
        }
    }

    inner class CoinInfoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivLogoCoin = itemView.ivLogoCoin
        val tvSymbols = itemView.tvSymbols
        val tvPrice = itemView.tvPrice
        val tvUpdate = itemView.tvUpdate
    }

    interface OnCoinClickListener {
        fun OnCoinClick(coinPriceInfo: CoinPriceInfo)
    }
}