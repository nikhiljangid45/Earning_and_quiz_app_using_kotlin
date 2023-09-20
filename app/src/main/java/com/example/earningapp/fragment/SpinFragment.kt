package com.example.earningapp.fragment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.earningapp.databinding.FragmentSpinBinding
import com.example.earningapp.model.HistoryModelClass
import com.example.earningapp.model.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Random


class SpinFragment : Fragment() {


    private lateinit var binding : FragmentSpinBinding
    private lateinit var timer : CountDownTimer
    private val itemTitle= arrayOf("100","Try Again","500","Try Again","200","Try Again")
    var  currentChance =0L
    var currentCoin = 0L
    val CHANNEL_ID = "channelId"
    val CHANNEL_NAME = "channelName"



    override fun onCreateView(  inflater: LayoutInflater, container: ViewGroup?,  savedInstanceState: Bundle?  ): View? {
        binding = FragmentSpinBinding.inflate(inflater,container,false)


           // Create the notification and notification Channel








        // Get the  chance vale
        Firebase.database.reference.child("PlayChance").child(Firebase.auth.currentUser!!.uid)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        currentChance = snapshot.value as Long
                                binding.spineChance.text = (snapshot.value as Long).toString()
                        if ((snapshot.value as Long).toInt() == 0){
                            binding.Spin.isEnabled = false
                        }else{
                            binding.Spin.isEnabled = true
                        }

                    }else{
                            binding.Spin.isEnabled = false
                        binding.spineChance.text = {0}.toString()

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        // Get the coin
        Firebase.database.reference.child("playerCoin").child(Firebase.auth.currentUser!!.uid)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        currentCoin = snapshot.value as Long
                       binding.coniWthdrawHome.text = (snapshot.value as Long).toString()


                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        return binding.root




    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        binding.Spin.setOnClickListener(){

            if(currentChance>0){
                binding.Spin.isEnabled = false
                val spin = Random().nextInt(6)
                val degree = 60f *spin

                timer = object : CountDownTimer(5000,50) {
                    var rotate = 0f

                    override fun onTick(millisUntilFinished: Long) {

                        rotate += 5f
                        if (rotate>= degree){
                            rotate = degree
                            timer.cancel()
                            showResult(itemTitle[spin],spin)
                        }

                        binding.wheel.rotation = rotate

                    }

                    override fun onFinish() {

                    }
                }.start()
            }else{

                Toast.makeText(requireActivity(),"you have not Spine ",Toast.LENGTH_LONG).show()

                }

           val num = currentChance-1
            if (num < 0L){
            }else{
                Firebase.database.reference.child("PlayChance").child(Firebase.auth.currentUser!!.uid)
                    .setValue(num)
                    .addOnSuccessListener { }
            }
        }


    }

    private fun showResult(itemTimer : String, spin:Int){
        if (spin%2 == 0){


            var wineCoin = itemTimer.toInt()
            Firebase.database.reference.child("playerCoin").child(Firebase.auth.currentUser!!.uid).setValue(currentCoin + wineCoin)
                .addOnSuccessListener {
                }


            var historymodelClass = HistoryModelClass(System.currentTimeMillis().toString(),wineCoin.toString(),false)

            Firebase.database.reference.child("playerCoinHistory").child(Firebase.auth.currentUser!!.uid).
                push().
                setValue(historymodelClass)
                .addOnSuccessListener {

                    }
        binding.Spin.isEnabled= true
    }

}

fun createNotificationChannel(){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
        val channel = NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT).apply {
            lightColor = Color.GREEN
            enableLights(true)
        }
      //  val manager =
    }
}




}