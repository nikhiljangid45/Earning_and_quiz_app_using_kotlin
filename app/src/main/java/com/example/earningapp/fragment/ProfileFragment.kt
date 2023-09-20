package com.example.earningapp.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.earningapp.R
import com.example.earningapp.databinding.FragmentProfileBinding
import com.example.earningapp.model.User
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.UUID

class ProfileFragment : Fragment() {

    val biding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }

    var isExpand = true
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var imageUri: Uri




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        biding.imageButton.setOnClickListener(){
            if(isExpand){
                biding.expadableconstable.visibility = View.VISIBLE
                biding.imageButton.setImageResource(R.drawable.arrowup)
            }else{
                biding.expadableconstable.visibility = View.GONE
                biding.imageButton.setImageResource(R.drawable.downarrow)
            }
            isExpand =! isExpand

        }

        // get  the image from firebase and save in image view
        getTheImageFromFirebase()


        // Get the User data from Firebase
        getTheUserDataFromFirebase()


        // Get the image from Gallery
        biding.profileImage.setOnClickListener(){
              openGallery()
        }

        // save the image in  Firebase database
        biding.saveButton.setOnClickListener() {
            uploadProfileImage()
        }



        return biding.root
    }

    private fun getTheImageFromFirebase() {
        Firebase.database.reference.child("Users").child(Firebase.auth.currentUser!!.uid)
            .child("profileImage").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val downloadUrl = snapshot.getValue(String::class.java)

                    // Check if the download URL is not null and load the image into the ImageView
                    if (!downloadUrl.isNullOrEmpty()) {
                        Picasso.get().load(downloadUrl).into(biding.profileImage)
                    } else {
                        // Handle the case where the download URL is null or empty
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle any errors that occurred while retrieving the download URL
                }
            })


    }

    private fun getTheUserDataFromFirebase() {
        Firebase.database.reference.child("Users").child(Firebase.auth.currentUser!!.uid).
        addValueEventListener(
            object  : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    var user = snapshot.getValue<User>(User::class.java)
                    biding.Name.text = user?.name
                    biding.nameUp.text = user?.name
                    biding.Email.text = user?.email
                    biding.age.text = user?.age.toString()
                    biding.Passwod.text = user?.password

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
        )
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data

            // Now, you can use the selectedImageUri to display the chosen image
            if (selectedImageUri != null) {
                imageUri = selectedImageUri
                biding.profileImage.setImageURI(selectedImageUri)
            }
        }
    }

    private fun uploadProfileImage() {
        if (imageUri != null) {
            // Generate a random unique image name (or use your own logic)
            val imageName = UUID.randomUUID().toString()

            // Reference to Firebase Storage
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference
            val imageRef: StorageReference = storageRef.child("profile_images/$imageName")

            // Upload the image
            imageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    // Image uploaded successfully, you can get the download URL
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        // Now, you can save the downloadUrl to your database or use it as needed
                        // For example, update the user's profile with the downloadUrl
                        updateProfileImage(downloadUrl)
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors that occurred during the upload
                    // You can show an error message to the user if needed
                }
        }
    }

    private fun updateProfileImage(downloadUrl: String) {
        // Update the user's profile in the database with the download URL
        // For example, you can update a "profileImage" field with downloadUrl
        Firebase.database.reference.child("Users").child(Firebase.auth.currentUser!!.uid)
            .child("profileImage").setValue(downloadUrl)
            .addOnSuccessListener {
                // Profile image URL saved to the database
                // You can show a success message to the user if needed
            }
            .addOnFailureListener { exception ->
                // Handle any errors that occurred while updating the profile
                // You can show an error message to the user if needed
            }
    }

}

