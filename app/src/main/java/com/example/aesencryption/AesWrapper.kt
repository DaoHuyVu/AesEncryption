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
    }
}