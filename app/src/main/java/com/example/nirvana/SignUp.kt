package com.example.nirvana

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.nirvana.databinding.ActivitySignUpBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var binding : ActivitySignUpBinding
    private lateinit var database : DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.continueBtn.setOnClickListener {
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val repeatPassword = binding.repeatPassword.text.toString()

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()){

                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()

            } else if(password != repeatPassword){

                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()

            } else {

                database = FirebaseDatabase.getInstance().getReference("Users")
                val user = User(firstName, lastName, email, password, repeatPassword)
                database.child(firstName).setValue(user).addOnSuccessListener {
                    binding.firstName.text.clear()
                    binding.lastName.text.clear()
                    binding.email.text.clear()
                    binding.password.text.clear()
                    binding.repeatPassword.text.clear()

                    Toast.makeText(this, "User added", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, HomeActivity::class.java))

                }.addOnFailureListener {

                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}