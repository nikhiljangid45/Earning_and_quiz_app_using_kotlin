package com.example.earningapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.earningapp.databinding.HistoryitemBinding
import com.example.earningapp.model.HistoryModelClass
import java.sql.Date
import java.sql.Timestamp

class HistoryAdapter(var listHistory : ArrayList<HistoryModelClass>) : RecyclerView.Adapter<HistoryAdapter.HistoryCoinViewModel>() {


    class HistoryCoinViewModel(var binding : HistoryitemBinding): RecyclerView.ViewHolder(binding.root)
    {


    }

    override fun onCreateViewHolder(  parent: ViewGroup, viewType: Int  ): HistoryAdapter.HistoryCoinViewModel  {
      return HistoryCoinViewModel(HistoryitemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: HistoryAdapter.HistoryCoinViewModel, position: Int) {
        var timeStamp = Timestamp(listHistory[position].timeAndDate.toLong())
      holder.binding.Time.text = Date(timeStamp.time).toString()
      holder.binding.CoinHistory.text = listHistory[position].coin
        holder.binding.status.text = if (listHistory.get(position).isWithDrawal){
            "- Money WithDrawal "


        }else{
            " + Money Added"
        }


    }

    override fun getItemCount(): Int {
       return listHistory.size
    }
}