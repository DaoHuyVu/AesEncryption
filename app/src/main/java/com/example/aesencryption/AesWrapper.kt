package com.example.aesencryption

class AesWrapper private constructor() {
    companion object{
        @Volatile
        private var aes : AES? = null
        fun getInstance() : AES{
            return aes ?: synchronized(this){
                aes ?: CBC().also {
                    aes = it
                }
            }
        }
        fun getSecreteKey() : String{
            return aes!!.secretKey
        }
        fun getIV() : String{
            val iv = (aes as CBC).initializerVector
            var res = ""
            for(i in 0 until 4){
                for(j in 0 until 4){
                    res += iv[i][j].toString()
                }
            }
            return res
        }
    }
}