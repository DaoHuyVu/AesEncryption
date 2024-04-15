package com.example.aesencryption

import android.util.Log
import kotlin.math.ceil

class CBC : AES() {
    private var _initializationVector = Array(4){Array(4){0}}
    val initializerVector get() = _initializationVector
    init{
        keyExpansion()
    }
    override fun encrypt(plainText : String): String {
        _initializationVector = iV()
        var iVTemp = _initializationVector
        val blocks = ceil(plainText.length/16.0).toInt()
        val state = plainTextToState(plainText,blocks)
        for(n in 0 until blocks){
            // Cn = E(Kn,( Pn xor Cn-1))
            xor4Words(state[n],iVTemp)
            addRoundKey(state[n],0)
            for (i in 1 until numOfRound) {
                subState(state[n])
                shiftRow(state[n])
                state[n] = mixColumn(state[n])
                addRoundKey(state[n],i)
            }
            subState(state[n])
            shiftRow(state[n])
            addRoundKey(state[n],numOfRound)
            // IVn = Cn
            iVTemp = state[n]
        }
        return cipherText(state,blocks+1)
    }
    override fun decrypt(cipherText: String): String {
        var iVTemp = _initializationVector
        val blocks =  cipherText.length/32
        val state = hexToState(cipherText,blocks)
        for(n in 0 until blocks-1){
            // Store Cn-1
            val temp = Array(4){Array(4){0}}
            copy(state[n],temp)
            // Pn = Cn-1 xor D(Kn,Cn)
            addRoundKey(state[n],numOfRound)
            for(i in numOfRound-1 downTo 1){
                invShiftRow(state[n])
                invSubState(state[n])
                addRoundKey(state[n],i)
                state[n] = invMixColumn(state[n])
            }
            invShiftRow(state[n])
            invSubState(state[n])
            addRoundKey(state[n],0)
            xor4Words(state[n],iVTemp)

            iVTemp = temp
        }
        return intArrayToPlainText(state,blocks-1)
    }
}