    package com.example.earningapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.earningapp.databinding.ActivityQizeBinding
import com.example.earningapp.fragment.WithDrawalFragment
import com.example.earningapp.model.Question
import com.example.earningapp.model.User
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.values
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

    class QizeActivity : AppCompatActivity() {


    private val  binding by lazy {
        ActivityQizeBinding.inflate(layoutInflater)
    }

        private lateinit var questionList  : ArrayList<Question>
        var currentQuestion = 0
        var score =0
        var  currentChance =0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        questionList = ArrayList<Question>()

        val image = intent.getIntExtra("categoryImage",0)
        val catText = intent.getStringExtra("questionType")
        binding.imageView2.setImageResource(image)

        // Get the  chance vale
        Firebase.database.reference.child("PlayChance").child(Firebase.auth.currentUser!!.uid)
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        currentChance = snapshot.value  as Long
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        //Get the Coin
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

        Firebase.firestore.collection("Questions").document(catText.toString()).
        collection("question1").get().addOnSuccessListener {
             questionData->
            questionList.clear()
            for (data in questionData.documents){
                  val question :Question? = data.toObject(Question::class.java)
                  questionList.add(question!!)
            }

            if(questionList.size > 0){
                binding.question.text = questionList.get(currentQuestion).question
                binding.option1.text = questionList.get(currentQuestion).option1
                binding.option2.text = questionList.get(currentQuestion).option2
                binding.option3.text = questionList.get(currentQuestion).option3
                binding.option4.text = questionList.get(currentQuestion).option4
            }

        }



        // open the bottom Dialog
        binding.coinHome.setOnClickListener(){
            val bottomSheetDialog : BottomSheetDialogFragment = WithDrawalFragment()
            bottomSheetDialog.show(this@QizeActivity.supportFragmentManager,"TEST")
            bottomSheetDialog.enterTransition
        }
        binding.coniWthdrawHome.setOnClickListener(){
            val bottomSheetDialog : BottomSheetDialogFragment = WithDrawalFragment()
            bottomSheetDialog.show(this@QizeActivity.supportFragmentManager,"TEST")
            bottomSheetDialog.enterTransition
        }



        // click listner on button
        binding.option1.setOnClickListener {
            nextQuestionAndScoreUpdate(binding.option1.text.toString())
        }
        binding.option2.setOnClickListener {
            nextQuestionAndScoreUpdate(binding.option2.text.toString())
        }
        binding.option3.setOnClickListener {
            nextQuestionAndScoreUpdate(binding.option3.text.toString())
        }
        binding.option4.setOnClickListener {
            nextQuestionAndScoreUpdate(binding.option4.text.toString())
        }

    }

        private fun nextQuestionAndScoreUpdate(s:String) {

            if (s.equals(questionList.get(currentQuestion).ans.toString())){
                score+=10
              //  Toast.makeText(this,"  " +score,Toast.LENGTH_SHORT).show()
            }

            currentQuestion++
            if (questionList.size > currentQuestion){
                binding.question.text = questionList.get(currentQuestion).question
                binding.option1.text = questionList.get(currentQuestion).option1
                binding.option2.text = questionList.get(currentQuestion).option2
                binding.option3.text = questionList.get(currentQuestion).option3
                binding.option4.text = questionList.get(currentQuestion).option4
            }else{
                if (((score.toDouble() / questionList.size * 10) * 100).toInt()  >= 60 ) {
                    // Show a Toast message with the score percentage
                    var scorePercentage = ((score.toDouble() / questionList.size * 10) * 100).toInt()
                    Toast.makeText(this, "Score: $scorePercentage%", Toast.LENGTH_SHORT).show()

                    binding.win.visibility = View.VISIBLE

                    var isUpdate = false

                    if (!isUpdate) {
                        // Assuming you have the currentChance variable defined earlier
                        val currentChance = // Initialize currentChance with the appropriate value

                            Firebase.database.reference.child("PlayChance").child(Firebase.auth.currentUser!!.uid)
                                .setValue(currentChance + 1)
                                .addOnSuccessListener {
                                    isUpdate = true
                                }
                                .addOnFailureListener { e ->
                                    // Handle the failure to update Firebase database if needed
                                }
                    }
                } else {
                    // Show a Toast message with the score percentage
                    var scorePercentage = score / (questionList.size * 10) * 100
                    Toast.makeText(this, "Score: $scorePercentage%", Toast.LENGTH_SHORT).show()

                    binding.sorry.visibility = View.VISIBLE
                }

            }

        }
    }