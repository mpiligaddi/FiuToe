package com.codingstuff.fiutoe

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.database.*
import androidx.appcompat.app.AppCompatActivity

var isCodeMaker = true;
var code = "null";
var codeFound = false
var checkTemp = true
var keyValue:String = "null"
var playerOneName:String = "null"
var playerTwoName:String = "null"

class CodeGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code)

        findViewById<Button>(R.id.Create).setOnClickListener{
            code = "null";
            codeFound = false
            checkTemp = true
            keyValue= "null"
            code = findViewById<EditText>(R.id.GameCode).text.toString()
            playerOneName = findViewById<EditText>(R.id.playerName).text.toString()
            findViewById<Button>(R.id.Create).visibility = View.GONE
            findViewById<Button>(R.id.Join).visibility = View.GONE
            findViewById<EditText>(R.id.GameCode).visibility = View.GONE
            findViewById<TextView>(R.id.textView4).visibility = View.GONE
            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
            if(code != "null" && code != null && code != "") {
                isCodeMaker = true;
                FirebaseDatabase.getInstance().reference.child("codes").addValueEventListener(object  :ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var check = isValueAvailable(snapshot , code)

                        Handler().postDelayed({
                            if(check == true) {
                                findViewById<Button>(R.id.Create).visibility = View.VISIBLE
                                findViewById<Button>(R.id.Join).visibility = View.VISIBLE
                                findViewById<EditText>(R.id.GameCode).visibility = View.VISIBLE
                                findViewById<TextView>(R.id.textView4).visibility = View.VISIBLE
                                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                            }
                            else{
                                FirebaseDatabase.getInstance().reference.child("codes").push().setValue(code)
                                isValueAvailable(snapshot,code)
                                checkTemp = false
                                Handler().postDelayed({
                                    accepted()
                                    errorMsg("Please don't go back")
                                } , 300)
                            }
                        }, 2000)
                    }

                })
            }
            else {
                findViewById<Button>(R.id.Create).visibility = View.VISIBLE
                findViewById<Button>(R.id.Join).visibility = View.VISIBLE
                findViewById<EditText>(R.id.GameCode).visibility = View.VISIBLE
                findViewById<TextView>(R.id.textView4).visibility = View.VISIBLE
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                errorMsg("Enter Code Properly")
            }
        }
        findViewById<Button>(R.id.Join).setOnClickListener {
            code = "null";
            codeFound = false
            checkTemp = true
            keyValue= "null"
            code = findViewById<EditText>(R.id.GameCode).text.toString()
            playerTwoName = findViewById<EditText>(R.id.playerName).text.toString()
            if(code != "null" && code != null && code != "") {
                findViewById<Button>(R.id.Create).visibility = View.GONE
                findViewById<Button>(R.id.Join).visibility = View.GONE
                findViewById<EditText>(R.id.GameCode).visibility = View.GONE
                findViewById<TextView>(R.id.textView4).visibility = View.GONE
                findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE
                isCodeMaker = false;
                FirebaseDatabase.getInstance().reference.child("codes").addValueEventListener(object:ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        var data:Boolean = isValueAvailable(snapshot , code)

                        Handler().postDelayed({
                            if(data == true) {
                                codeFound = true
                                accepted()
                                findViewById<Button>(R.id.Create).visibility = View.VISIBLE
                                findViewById<Button>(R.id.Join).visibility = View.VISIBLE
                                findViewById<EditText>(R.id.GameCode).visibility = View.VISIBLE
                                findViewById<TextView>(R.id.textView4).visibility = View.VISIBLE
                                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                            }
                            else{
                                findViewById<Button>(R.id.Create).visibility = View.VISIBLE
                                findViewById<Button>(R.id.Join).visibility = View.VISIBLE
                                findViewById<EditText>(R.id.GameCode).visibility = View.VISIBLE
                                findViewById<TextView>(R.id.textView4).visibility = View.VISIBLE
                                findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
                                errorMsg("Invalid Code")
                            }
                        } , 2000)
                    }
                })
            }
            else {
                errorMsg("Enter Code Properly")
            }
        }
    }

    fun accepted() {
        startActivity(Intent(this, GameActivity::class.java));
        findViewById<Button>(R.id.Create).visibility = View.VISIBLE
        findViewById<Button>(R.id.Join).visibility = View.VISIBLE
        findViewById<EditText>(R.id.GameCode).visibility = View.VISIBLE
        findViewById<TextView>(R.id.textView4).visibility = View.VISIBLE
        findViewById<ProgressBar>(R.id.progressBar).visibility = View.GONE
    }

    fun errorMsg(value : String) {
        Toast.makeText(this , value  , Toast.LENGTH_SHORT).show()
    }

    fun isValueAvailable(snapshot: DataSnapshot , code : String): Boolean {
        var data = snapshot.children
        data.forEach{
            var value = it.getValue().toString()
            if(value == code) {
                keyValue = it.key.toString()
                return true;
            }
        }
        return false
    }
}