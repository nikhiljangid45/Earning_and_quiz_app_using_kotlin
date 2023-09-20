package com.example.earningapp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.earningapp.adapter.HistoryAdapter
import com.example.earningapp.databinding.FragmentHistoryBinding
import com.example.earningapp.model.HistoryModelClass
import com.example.earningapp.model.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Collections


class HistoryFragment : Fragment() {


//    var binding = lazy {
//        FragmentHistoryBinding.inflate(layoutInflater)
//    }

    private val binding : FragmentHistoryBinding by lazy {
        FragmentHistoryBinding.inflate(layoutInflater)
    }

    private var listHistory = ArrayList<HistoryModelClass>()
    private lateinit var adapter :HistoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        Firebase.database.reference.child("playerCoinHistory").child(Firebase.auth.currentUser!!.uid).
        addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listHistory.clear()
                var listHistory1 = ArrayList<HistoryModelClass>()
                for (dataSnapshot in snapshot.children){
                    val data = dataSnapshot.getValue(HistoryModelClass::class.java)

                    listHistory1.add(data!!)

                }

                Collections.reverse(listHistory1)
                listHistory.addAll(listHistory1)
                adapter.notifyDataSetChanged()


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Add the name
        Firebase.database.reference.child("Users").child(Firebase.auth.currentUser!!.uid).
        addValueEventListener(
            object  : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    var user = snapshot.getValue<User>(User::class.java)
                    binding.Name.text = user?.name


                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
        )


        // set the Coin

        Firebase.database.reference.child("playerCoin").child(Firebase.auth.currentUser!!.uid)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        //currentCoin = snapshot.value as Long
                        binding.coniWthdrawHome.text = (snapshot.value as Long).toString()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        binding.coinHome.setOnClickListener(){
            val bottomSheetDialog : BottomSheetDialogFragment = WithDrawalFragment()
            bottomSheetDialog.show(requireActivity().supportFragmentManager,"TEST")
            bottomSheetDialog.enterTransition
        }
        binding.coniWthdrawHome.setOnClickListener(){
            val bottomSheetDialog : BottomSheetDialogFragment = WithDrawalFragment()
            bottomSheetDialog.show(requireActivity().supportFragmentManager,"TEST")
            bottomSheetDialog.enterTransition
        }
         binding.recyclerViewHistory.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter(listHistory)
        binding.recyclerViewHistory.adapter = adapter
        binding.recyclerViewHistory.setHasFixedSize(true)

     return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}