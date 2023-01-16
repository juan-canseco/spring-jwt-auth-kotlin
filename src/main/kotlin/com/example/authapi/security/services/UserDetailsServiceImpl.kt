package com.example.authapi.security.services

import  com.example.authapi.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsServiceImpl constructor(
    private val userRepository: UserRepository
) : UserDetailsService {
    @Transactional
    override fun loadUserByUsername(username: String?): UserDetails {
        username?: throw NullPointerException("Username cannot be null")
        val user = userRepository.findUserByUsername(username)
            ?: throw UsernameNotFoundException("User Not Found with username: $username")
        return UserDetailsImpl.build(user)
    }
}