package com.woleapp.netpos.qrgenerator.adapter

//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.woleapp.netpos.qrgenerator.databinding.MerchantLayoutBinding
//import com.woleapp.netpos.qrgenerator.databinding.TransactionLayoutBinding
//import com.woleapp.netpos.qrgenerator.model.Merchant
//import com.woleapp.netpos.qrgenerator.model.TransactionModel
//
//
//class MerchantAdapter(private var merchantList: List<Merchant>, private val merchantClickListener:OnMerchantClick): RecyclerView.Adapter<MerchantAdapter.MyViewHolder>() {
//
//    inner class MyViewHolder(private val binding: MerchantLayoutBinding): RecyclerView.ViewHolder(binding.root) {
//        val merchantName = binding.merchantName
//        val merchantAddress = binding.merchantAddress
//
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
//        val binding = MerchantLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return MyViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
//        holder.itemView.apply {
//            with(holder){
//                with(merchantList[position]){
//                    merchantName.text = contact_name
//                    merchantAddress.text = address
//                }
//            }
//        }
//        holder.itemView.setOnClickListener {
//            merchantClickListener.onEachMerchantClicked(merchantList[position])
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return merchantList.size
//    }
//    interface OnMerchantClick {
//        fun onEachMerchantClicked(merchant: Merchant)
//    }
//}
//
//
