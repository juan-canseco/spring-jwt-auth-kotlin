package com.example.authapi.repositories

import com.example.authapi.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findUserByUsername(username : String) : User?
    fun existsUserByUsername(username : String) : Boolean
    fun existsUserByEmail(email : String) : Boolean
}