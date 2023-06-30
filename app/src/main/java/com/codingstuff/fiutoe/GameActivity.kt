package com.codingstuff.fiutoe

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*
import kotlin.collections.ArrayList
import kotlin.system.exitProcess

var isMyMove = isCodeMaker; // Arranca el jugador que creo el codigo

class GameActivity : AppCompatActivity() {
    var player1Count = 0
    var player2Count = 0
    var player1 = ArrayList<Int>()
    var player2 = ArrayList<Int>()
    var emptyCells = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        findViewById<Button>(R.id.resetButton).setOnClickListener {
            reset()
        }

        FirebaseDatabase.getInstance().reference.child("data").child(code).addChildEventListener(object : ChildEventListener{
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var data = snapshot.value
                if(isMyMove) {
                    isMyMove = false
                    moveOnline(data.toString(),isMyMove)
                }
                else {
                    isMyMove = true
                    moveOnline(data.toString(),isMyMove)
                }
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                reset()
            }
        })
    }

    fun clickCell(view:View) {
        if(isMyMove) {
            val button = view as Button
            var cellOnline = 0
            cellOnline = when (button.id) {
                R.id.button1 -> 1
                R.id.button2 -> 2
                R.id.button3 -> 3
                R.id.button4 -> 4
                R.id.button5 -> 5
                R.id.button6 -> 6
                R.id.button7 -> 7
                R.id.button8 -> 8
                R.id.button9 -> 9
                else -> 0
            }
            playNow(button, cellOnline)
            updateDatabase(cellOnline)
        }
        else{
            Toast.makeText(this , "Wait for your turn, please" , Toast.LENGTH_LONG).show()
        }
    }

    // Jugada propia
    private fun playNow(buttonSelected:Button, currentCell:Int) {
        buttonSelected.text = "X"
        emptyCells.remove(currentCell) //creo que se puede sacar
        findViewById<TextView>(R.id.playerTurnText).text = "Turn : Player 2"
        buttonSelected.setTextColor(Color.parseColor("#EC0C0C"))
        player1.add(currentCell)
        emptyCells.add(currentCell)
        buttonSelected.isEnabled = false
        checkWinner()
    }

    // Jugada del oponente
    fun moveOnline(data : String , move : Boolean){
        if(move) {
            var buttonselected: Button? = findViewById<Button>(R.id.button1)
            buttonselected = when (data.toInt()) {
                1 -> findViewById<Button>(R.id.button1)
                2 -> findViewById<Button>(R.id.button2)
                3 -> findViewById<Button>(R.id.button3)
                4 -> findViewById<Button>(R.id.button4)
                5 -> findViewById<Button>(R.id.button5)
                6 -> findViewById<Button>(R.id.button6)
                7 -> findViewById<Button>(R.id.button7)
                8 -> findViewById<Button>(R.id.button8)
                9 -> findViewById<Button>(R.id.button9)
                else -> {
                    findViewById<Button>(R.id.button1)
                }
            }
            buttonselected.text = "⬤"
            findViewById<TextView>(R.id.playerTurnText).text = "Turn : Player 1"
            buttonselected.setTextColor(Color.parseColor("#D22BB804"))
            player2.add(data.toInt())
            emptyCells.add(data.toInt())
            buttonselected.isEnabled = false
            checkWinner()
        }
    }

    private fun updateDatabase(cellId : Int) {
        FirebaseDatabase.getInstance().reference.child("data").child(code).push().setValue(cellId);
    }

    private fun checkWinner():Int {
        if((player1.contains(1) && player1.contains(2) && player1.contains(3) ) || (player1.contains(1) && player1.contains(4) && player1.contains(7))||
            (player1.contains(3) && player1.contains(6) && player1.contains(9)) || (player1.contains(7) && player1.contains(8) && player1.contains(9))||
            (player1.contains(4)&&player1.contains(5)&&player1.contains(6)) || (player1.contains(1)&&player1.contains(5) && player1.contains(9))||
            player1.contains(3)&&player1.contains(5)&&player1.contains(7) || (player1.contains(2)&&player1.contains(5) && player1.contains(8))) {
            player1Count+=1
            buttonDisable()
            disableReset()
            val build = AlertDialog.Builder(this)
            build.setTitle("Game Over")
            build.setMessage("Player 1 Wins")
            build.setPositiveButton("Play again") { dialog, which ->
                reset()
            }
            build.setNegativeButton("Exit") { dialog, which ->
                removeCode()
                exitProcess(1)
            }
            build.show()
            return 1
        }
        else if((player2.contains(1) && player2.contains(2) && player2.contains(3) ) || (player2.contains(1) && player2.contains(4) && player2.contains(7))||
            (player2.contains(3) && player2.contains(6) && player2.contains(9)) || (player2.contains(7) && player2.contains(8) && player2.contains(9))||
            (player2.contains(4)&&player2.contains(5)&&player2.contains(6)) || (player2.contains(1)&&player2.contains(5) && player2.contains(9))||
            player2.contains(3)&&player2.contains(5)&&player2.contains(7) || (player2.contains(2)&&player2.contains(5) && player2.contains(8))){
            player2Count+=1
            buttonDisable()
            disableReset()
            val build = AlertDialog.Builder(this)
            build.setTitle("Game Over")
            build.setMessage("Player 2 Wins")
            build.setPositiveButton("Play again"){dialog, which ->
                reset()
            }
            build.setNegativeButton("Exit"){dialog, which ->
                removeCode()
                exitProcess(1)
            }
            build.show()
            return 1
        }
        else if(emptyCells.contains(1) && emptyCells.contains(2) && emptyCells.contains(3) && emptyCells.contains(4) && emptyCells.contains(5) && emptyCells.contains(6) && emptyCells.contains(7) &&
            emptyCells.contains(8) && emptyCells.contains(9) ) {

            val build = AlertDialog.Builder(this)
            build.setTitle("Game Draw")
            build.setMessage("Nobody Wins")
            build.setPositiveButton("Play again"){dialog, which ->
                reset()
            }
            build.setNegativeButton("Exit"){dialog, which ->
                exitProcess(1)
                removeCode()
            }
            build.show()
            return 1
        }
        return 0
    }

    fun reset() {
        player1.clear()
        player2.clear()
        emptyCells.clear()
        for(i in 1..9) {
            var buttonselected : Button? = findViewById<Button>(R.id.button1)
            buttonselected = when(i) {
                1 -> findViewById<Button>(R.id.button1)
                2 -> findViewById<Button>(R.id.button2)
                3 -> findViewById<Button>(R.id.button3)
                4 -> findViewById<Button>(R.id.button4)
                5 -> findViewById<Button>(R.id.button5)
                6 -> findViewById<Button>(R.id.button6)
                7 -> findViewById<Button>(R.id.button7)
                8 -> findViewById<Button>(R.id.button8)
                9 -> findViewById<Button>(R.id.button9)
                else -> {findViewById<Button>(R.id.button1)}
            }
            buttonselected.isEnabled = true
            buttonselected.text = ""
            findViewById<TextView>(R.id.countWinsPlayer1Text).text = "Player1 : $player1Count"
            findViewById<TextView>(R.id.countWinsPlayer2Text).text = "Player2 : $player2Count"
            isMyMove = isCodeMaker
            if(isCodeMaker){
                FirebaseDatabase.getInstance().reference.child("data").child(code).removeValue()
            }
        }
    }

    private fun buttonDisable() {
        for(i in 1..9) {
            val buttonSelected = when(i) {
                1 -> findViewById<Button>(R.id.button1)
                2 -> findViewById<Button>(R.id.button2)
                3 -> findViewById<Button>(R.id.button3)
                4 -> findViewById<Button>(R.id.button4)
                5 -> findViewById<Button>(R.id.button5)
                6 -> findViewById<Button>(R.id.button6)
                7 -> findViewById<Button>(R.id.button7)
                8 -> findViewById<Button>(R.id.button8)
                9 -> findViewById<Button>(R.id.button9)
                else -> {findViewById<Button>(R.id.button1)}
            }
            if(buttonSelected.isEnabled)
                buttonSelected.isEnabled = false
        }
    }

    private fun removeCode() {
        if(isCodeMaker) {
            FirebaseDatabase.getInstance().reference.child("codes").child(keyValue).removeValue()
        }
    }

    private fun disableReset() {
        findViewById<Button>(R.id.resetButton).isEnabled = false
        Handler().postDelayed(Runnable { findViewById<Button>(R.id.resetButton).isEnabled = true } , 2200)
    }

    override fun onBackPressed() {
        removeCode()
        if(isCodeMaker){
            FirebaseDatabase.getInstance().reference.child("data").child(code).removeValue()
        }
        exitProcess(0)
    }
}
