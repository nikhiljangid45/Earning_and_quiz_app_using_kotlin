package com.example.earningapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.earningapp.R
import com.example.earningapp.adapter.CategoryAdapter
import com.example.earningapp.databinding.FragmentHomeBinding
import com.example.earningapp.model.CategoryModelClass
import com.example.earningapp.model.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

private val binding : FragmentHomeBinding by lazy {
    FragmentHomeBinding.inflate(layoutInflater)
}


    private var categoryList = ArrayList<CategoryModelClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

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



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        categoryList.clear()
        categoryList.add(CategoryModelClass(R.drawable.scince1,"Science"))
        categoryList.add(CategoryModelClass(R.drawable.english1,"english"))
        categoryList.add(CategoryModelClass(R.drawable.geography,"Geography"))
        categoryList.add(CategoryModelClass(R.drawable.math,"Math"))

        binding.recycleViewHome.layoutManager = GridLayoutManager(requireContext(),2)
        var adapter = CategoryAdapter(categoryList, requireActivity())
        binding.recycleViewHome.adapter = adapter
        binding.recycleViewHome.setHasFixedSize(true)


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
    }

}