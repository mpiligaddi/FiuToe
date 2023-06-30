package com.codingstuff.fiutoe

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

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
            code = findViewById<EditText>(R.id.GameCode).text.toString()
            playerOneName = findViewById<EditText>(R.id.playerName).text.toString()

            if(code != "null" && code != null && code != "") {
                isCodeMaker = true;
                FirebaseDatabase.getInstance().reference.child("codes").addValueEventListener(object  :ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var codeAlreadyUsed:Boolean = isValueAvailable(snapshot,code)

                        Handler().postDelayed({
                            if(!codeAlreadyUsed){
                                FirebaseDatabase.getInstance().reference.child("codes").push().setValue(code)
                                checkTemp = false
                                Handler().postDelayed({
                                    accepted()
                                } , 300)
                            }
                        }, 2000)
                    }
                })
            }
            else {
                errorMsg("Enter a valid code, please")
            }
        }

        findViewById<Button>(R.id.Join).setOnClickListener {
            code = findViewById<EditText>(R.id.GameCode).text.toString()
            playerTwoName = findViewById<EditText>(R.id.playerName).text.toString()

            if(code != "null" && code != null && code != "") {
                isCodeMaker = false;
                FirebaseDatabase.getInstance().reference.child("codes").addValueEventListener(object:ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var codeExists:Boolean = isValueAvailable(snapshot,code)
                        Handler().postDelayed({
                            if(codeExists) {
                                codeFound = true
                                accepted()
                            }
                        } , 2000)
                    }
                })
            }
            else {
                errorMsg("Enter a valid code, please")
            }
        }
    }
    fun errorMsg(value : String) {
        Toast.makeText(this , value  , Toast.LENGTH_SHORT).show()
    }
    fun accepted() {
        startActivity(Intent(this, GameActivity::class.java));
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