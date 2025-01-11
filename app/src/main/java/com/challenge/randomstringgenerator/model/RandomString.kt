package com.challenge.randomstringgenerator.model

data class RandomTextWrapper(
    val randomText : RandomString
)
data class RandomString(
    val value: String,
    val length: Int,
    val created: String
)

