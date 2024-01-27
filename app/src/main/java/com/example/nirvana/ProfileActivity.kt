package com.example.nirvana


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.nirvana.databinding.ActivityProfileBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProfileBinding
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val username :String = binding.username.text.toString()
    }

    private fun readData(username: String,firstName: String, lastName: String, email: String, password: String){
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(username).get().addOnSuccessListener{
            if(it.exists()){
                val username = it.child("username").value
                val firstName = it.child("firstName").value
                val lastName = it.child("lastName").value
                val email = it.child("email").value
                val password = it.child("password").value
            }else{
                Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener{
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun updateUI(user: User?) {
        if (user != null) {
            findViewById<TextView>(R.id.name).text = user.username
            findViewById<EditText>(R.id.firstName).setText(user.firstName)
            findViewById<EditText>(R.id.lastName).setText(user.lastName)
            findViewById<EditText>(R.id.email).setText(user.email)
        } else {
            Toast.makeText(this, "User data is null", Toast.LENGTH_SHORT).show()
        }
    }
}