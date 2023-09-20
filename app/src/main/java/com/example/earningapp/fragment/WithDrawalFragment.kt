package com.example.earningapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.earningapp.databinding.FragmentWithDrawalBinding
import com.example.earningapp.model.HistoryModelClass
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class WithDrawalFragment : BottomSheetDialogFragment() {

    private val binding : FragmentWithDrawalBinding by lazy {
        FragmentWithDrawalBinding.inflate(layoutInflater)
    }
     private var  currentCoin = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        //  Set the coin in
        Firebase.database.reference.child("playerCoin").child(Firebase.auth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        currentCoin = snapshot.value as Long
                        binding.totalCoin.text = currentCoin.toString()
                        //      binding.coniWthdrawHome.text = (snapshot.value as Long).toString()


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })



        binding.transfar.setOnClickListener() {
            val amountText = binding.amount.text.toString()
            if (amountText.isNotBlank()) {
                try {
                    val amount = amountText.toDouble()
                    if (amount.toInt() <=  currentCoin.toInt()) {
                        Firebase.database.reference.child("playerCoin").child(Firebase.auth.currentUser!!.uid)
                            .setValue(currentCoin - amount).addOnSuccessListener {

                                val historyModelClass = HistoryModelClass(System.currentTimeMillis().toString(), amountText, true)

                                Firebase.database.reference.child("playerCoinHistory").child(Firebase.auth.currentUser!!.uid)
                                    .push()
                                    .setValue(historyModelClass)
                                    .addOnSuccessListener {
                                        binding.amount.text = null
                                        binding.payTemNumbet.text = null
                                    }
                            }
                    }
                    else {
                        Toast.makeText(requireActivity(), "Enter Amount is not valid", Toast.LENGTH_LONG).show()
                    }
                } catch (e: NumberFormatException) {
                    // Handle the case where the input is not a valid number
                    Toast.makeText(requireActivity(), "Invalid input. Please enter a valid number.", Toast.LENGTH_LONG).show()
                }
            } else {
                // Handle the case where the input is empty
                Toast.makeText(requireActivity(), "Amount cannot be empty.", Toast.LENGTH_LONG).show()
            }
        }

        return binding.root
    }

}