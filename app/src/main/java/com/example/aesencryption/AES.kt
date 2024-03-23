package com.example.aesencryption

import android.util.Log

abstract class AES : EnDecAlgorithm {
    // 16 bytes 0f1571c947d9e8590cb7add6af7f6798
    // 24 bytes 8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b
    // 32 bytes ec26d4ee1eccbda8b659cbb1220a94c9491e7386ea9b53930001e49f165c57b2
    private var secretKey = "0f1571c947d9e8590cb7add6af7f6798"
    private val secretKeyMap = mapOf(
        128 to "0f1571c947d9e8590cb7add6af7f6798",
        192 to "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b",
        256 to "ec26d4ee1eccbda8b659cbb1220a94c9491e7386ea9b53930001e49f165c57b2"
    )
    private val keyMap = mapOf(16 to 44,24 to 52,32 to 60)
    private val roundMap = mapOf(16 to 10,24 to 12,32 to 14)
    private val keyLength = secretKey.length/2
    protected val numOfRound = roundMap[keyLength]!!
    private val word = Array(keyMap[keyLength]!!){Array(4){0} }
    fun setKeySize(size : Int){
        secretKey = secretKeyMap[size]!!
    }

    private fun throughSBox(value : Int) : Int{
        var hex = Integer.toHexString(value)
        hex = padByte(hex)
        val first = hex[0].fromHexToInt()
        val second = hex[1].fromHexToInt()
        return sBox[first][second]
    }
    private fun throughISBox(value : Int) : Int{
        var hex = Integer.toHexString(value)
        hex = padByte(hex)
        val first = hex[0].fromHexToInt()
        val second = hex[1].fromHexToInt()
        return iSBox[first][second]
    }
    fun plainTextToState(plainText : String, blocks : Int) : Array<Array<Array<Int>>>{
        /* Blocks + 1 in case the plainText is perfectly distributed to block.
            In that case , the last block contains all the value of 0x10 to overcome
            the confusing with the last byte of a block being 0x01
        */
        val res = Array(blocks+1){Array(4){Array(4){0} }}
        var count = 0
        for(k in 0 until blocks){
            for(i in 0 until 4){
                for(j in 0 until 4 ){
                    if(count < plainText.length){
                        res[k][i][j] = plainText[count].code
                        count++
                    }
                    else{
                        var a = i
                        // Store the current value of j
                        var c = j
                        while(a < 4){
                            var b = c
                            while(b < 4){
                                res[k][a][b] = 16 - plainText.length%16
                                b++
                            }
                            a++
                            // Since 'a' receives a new value , b has to be reset to 0
                            c = 0
                        }
                        return res
                    }
                }
            }
        }
        for(i in 0 until 4){
            for(j in 0 until 4){
                res[blocks][i][j] = 0x10
            }
        }
        return res
    }
    fun intArrayToPlainText(state : Array<Array<Array<Int>>>,blocks : Int) : String{
        var res = ""
        for(k in 0 until blocks){
            if(k == blocks - 1){
                if(state[k][3][3] <= 15){
                    var temp = state[k][3][3]
                    var isPadded = true
                    var i = 3
                    var j = 3
                    while(temp > 0){
                        if(state[k][i][j] != state[k][3][3]){
                            isPadded = false
                            break
                        }
                        j--
                        if(j == -1){
                            i--
                            j = 3
                        }
                        temp--
                    }
                    if(isPadded){
                        if(0 == state[k+1][0][0]){
                            i = 0
                            j = 0
                            temp = 0
                            while(temp < 16 - state[k][3][3]){
                                res += state[k][i][j].toChar()
                                j++
                                if(j == 4){
                                    i++
                                    j = 0
                                }
                                temp++
                            }
                            return res
                        }
                    }
                }
            }
            for(i in 0 until 4){
                for(j in 0 until 4){
                    res += state[k][i][j].toChar()
                }
            }
        }
        return res
    }
    // Perform n-byte circular left shift on a word
    fun rotWord(word : Array<Int>,degree : Int ) : Array<Int>{
        val temp = word[0]
        for(i in 0 until 3){
            word[i] = word[i+degree]
        }
        word[3] = temp
        return word
    }
    fun subByte(byte : Int ) : Int{
        return throughSBox(byte)
    }
    fun invSubByte(byte : Int) : Int{
        return throughISBox(byte)
    }
    // Pad a zero in front of a one-length string to present a 2 character byte ( 0x9 -> 0x09 )
    fun padByte(str : String) : String{
        var res = str
        if(str.length == 1){
            res = "0${str}"
        }
        return res
    }

    // Map a word using S-BOX
    fun subWord(word : Array<Int>) : Array<Int>{
        for(i in 0 until 4){
            word[i] = subByte(word[i])
        }
        return word
    }
    fun rCon(word : Array<Int>,round : Int) : Array<Int>{
        word[0] = word[0] xor roundConstant[round]
        return word
    }
    fun byteMultiplication(byte1 : Int,byte2 : Int) : Int{
        if(byte2 == 0x01) return byte1
        var res = 0
        var a = byte1
        var b = byte2
        for(i in 0 until 8){
            if(b and 0x01 == 0x01){
                res = a xor res
            }
            var temp = 0
            if(a and 0x80 == 0x80){
                temp = 0x1b
            }
            a = (a shl 1)%256 xor temp
            b = b shr 1
        }
        return res
    }
    fun xorWord(word1 : Array<Int>,word2 : Array<Int>) : Array<Int>{
        for(i in 0 until 4){
            word1[i] = word1[i] xor word2[i]
        }
        return word1
    }

    fun keyExpansion(){
        for(i in 0 until keyLength/4){
            for(j in 0 until 4){
                word[i][j] = "${secretKey[2*(4*i+j)]}${secretKey[2*(4*i+j)+1]}".toInt(16)
            }
        }
        for(i in keyLength/4 until keyMap[keyLength]!!){
            for(j in 0 until 4){
                var temp = word[i-1]
                if(i%(keyLength/4) == 0){
                    temp = rCon(subWord(rotWord(temp,1)),i/(keyLength/4))
                }
                word[i] = xorWord(temp,word[i-(keyLength/4)])
            }
        }
    }
    fun shiftRow(state : Array<Array<Int>>) {
        // Shift column from 1 to 4
        for(i in 1 until 4){
            //Column number i is shifted i times to the left
            for(k in 0 until i){
                // Store the value of the first element of each row to temp
                val temp = state[0][i]
                for(j in 0 until 3){
                    state[j][i] = state[j+1][i]
                }

                state[3][i] = temp
            }
        }
    }
    fun invShiftRow(state : Array<Array<Int>>){
        for(i in 1 until 4){
            for(k in 0 until i){
                val temp = state[3][i]
                for (j in 3 downTo 1){
                    state[j][i] = state[j-1][i]
                }
                state[0][i] = temp
            }
        }
    }
    fun mixColumn(state : Array<Array<Int>>) : Array<Array<Int>>{
        val  temp = Array(4){Array(4){0} }
        for(i in 0 until 4){
            for(j in 0 until 4){
                for(k in 0 until 4){
                    temp[j][i] = temp[j][i] xor byteMultiplication(mixCol[i][k],state[j][k])
                }
            }
        }
        return temp
    }
    fun invMixColumn(state : Array<Array<Int>>) : Array<Array<Int>>{
        val temp = Array(4){Array(4){0} }
        for(i in 0 until 4){
            for(j in 0 until 4){
                for(k in 0 until 4){
                    temp[j][i] = temp[j][i] xor byteMultiplication(invMixCol[i][k],state[j][k])
                }
            }
        }
        return temp
    }
    fun addRoundKey(state : Array<Array<Int>>,round : Int){
        for(i in 0 until 4){
            state[i] = xorWord(state[i], word[4*round + i])
        }
    }
    fun xor4Words(state : Array<Array<Int>>,iV : Array<Array<Int>>){
        for(i in 0 until 4){
            state[i] = xorWord(state[i], iV[i])
        }
    }
    fun subState(state : Array<Array<Int>>){
        for(i in  0 until 4){
            for(j in 0 until 4){
                state[i][j] = subByte(state[i][j])
            }
        }
    }
    fun invSubState(state: Array<Array<Int>>){
        for(i in  0 until 4){
            for(j in 0 until 4){
                state[i][j] = invSubByte(state[i][j])
            }
        }
    }

    fun iV() : Array<Array<Int>>{
        val temp = "%03d%d".format(0,System.currentTimeMillis())
        val res = Array(4){Array(4){0} }
        for(i in 0 until 4){
            for(j in 0 until 4){
                res[i][j] = temp[4*i+j].fromHexToInt()
            }
        }
        return res

    }
    fun copy(a1 : Array<Array<Int>>,a2 : Array<Array<Int>>){
        for(i in 0 until 4){
            for(j in 0 until 4){
                a2[i][j] = a1[i][j]
            }
        }
    }
    fun cipherText(state : Array<Array<Array<Int>>>,blocks : Int) : String {
        var res = ""
        for(i in 0 until blocks){
            for(j in 0 until 4){
                for(k in 0 until 4){
                    res += padByte(Integer.toHexString(state[i][j][k]))
                }
            }
        }
        return res
    }
    fun hexToState(hex : String,blocks : Int) : Array<Array<Array<Int>>>{
        val state = Array(blocks){Array(4){Array(4){0} } }
        for(i in 0 until blocks){
            for(j in 0 until 4){
                for(k in 0 until 4){
                    state[i][j][k] = "${hex[i*32+j*8+2*k]}${hex[i*32+j*8+2*k+1]}".toInt(16)
                }
            }
        }
        return state
    }
}