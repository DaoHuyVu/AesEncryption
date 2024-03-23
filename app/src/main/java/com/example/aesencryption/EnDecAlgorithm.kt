package com.example.aesencryption

interface EnDecAlgorithm {
    fun encrypt(plainText : String ) : String
    fun decrypt(cipherText : String) : String
}