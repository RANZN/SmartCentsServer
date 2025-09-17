package com.ranjan.domain.service

import com.ranjan.domain.model.User

interface TokenProvider {
    fun createToken(user: User) : String
}