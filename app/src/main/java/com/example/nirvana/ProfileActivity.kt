package com.example.nirvana


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.nirvana.databinding.ActivityProfileBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)

        if (username != null) {
            loadData(username)
        }
        binding.saveBtn.setOnClickListener{
            if (username != null) {
                saveData(username)
            }
        }


    }

    private fun loadData(username: String) {
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(username).get().addOnSuccessListener {
            if (it.exists()) {
                val user = it.getValue(User::class.java)
                if (user != null) {
                    displayUserData(user)
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "User doesn't exist", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayUserData(user: User) {
        binding.name.text = user.username
        binding.firstName.text = Editable.Factory.getInstance().newEditable(user.firstName)
        binding.lastName.text = Editable.Factory.getInstance().newEditable(user.lastName)
        binding.email.text = Editable.Factory.getInstance().newEditable(user.email)
    }

    private fun saveData(username: String) {
        val firstName = binding.firstName.text.toString()
        val lastName = binding.lastName.text.toString()
        val email = binding.email.text.toString()
        val oldPassword = binding.oldPassword.text.toString()
        val newPassword = binding.newPassword.text.toString()

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            showToast("Please fill in all the fields")
            return
        }

        database = FirebaseDatabase.getInstance().getReference("Users")

        if (oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
            changePassword(username, oldPassword, newPassword, firstName, lastName, email)
        } else {
            updateUserInfo(username, firstName, lastName, email)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun changePassword(username: String, oldPassword: String, newPassword: String,
                               firstName: String, lastName: String, email: String) {
        database.child(username).get().addOnSuccessListener {
            if (it.exists() && it.getValue(User::class.java)?.password == oldPassword) {
                database.child(username).apply {
                    child("password").setValue(newPassword)
                    updateUserInfo(username, firstName, lastName, email)
                    showToast("Password changed successfully")
                }
            } else {
                showToast("Incorrect password")
            }
        }
    }

    private fun updateUserInfo(username: String, firstName: String, lastName: String, email: String) {
        database.child(username).apply {
            child("firstName").setValue(firstName)
            child("lastName").setValue(lastName)
            child("email").setValue(email)
        }
        showToast("Data saved successfully")
    }

}
