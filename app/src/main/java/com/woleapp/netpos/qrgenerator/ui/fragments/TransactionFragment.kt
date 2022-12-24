package com.woleapp.netpos.qrgenerator.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.woleapp.netpos.qrgenerator.R
import com.woleapp.netpos.qrgenerator.databinding.FragmentTransactionBinding
import com.woleapp.netpos.qrgenerator.databinding.FragmentTransactionHistoryBinding
import com.woleapp.netpos.qrgenerator.ui.adapter.TransactionAdapter
import com.woleapp.netpos.qrgenerator.ui.model.TransactionModel
import java.util.ArrayList

class TransactionFragment : Fragment(), TransactionAdapter.OnTransactionClick {

    private lateinit var _binding: FragmentTransactionBinding
    private val binding get() = _binding
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var transactionDataList: ArrayList<TransactionModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =  FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generateTransactionData()
        transactionSetUp()
        binding.seeAll.setOnClickListener {
            val action = TransactionsFragmentDirections.actionTransactionsFragmentToTransactionHistoryFragment()
            findNavController().navigate(action)
        }
    }

    private fun transactionSetUp() {
        transactionAdapter = TransactionAdapter(transactionDataList, this)
        binding.transactionRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.transactionRecycler.adapter = transactionAdapter
    }

    private fun generateTransactionData(){
        transactionDataList = arrayListOf()
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
        transactionDataList.add(TransactionModel("Prince Supermarket", "20th of November 15:32", "4000"))
    }

    override fun onTransactionClicked(transaction: TransactionModel) {
        val action = TransactionsFragmentDirections.actionTransactionsFragmentToTransactionDetailsFragment()
        findNavController().navigate(action)
    }
}

