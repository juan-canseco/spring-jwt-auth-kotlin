package com.example.authapi.security.services

import com.example.authapi.models.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserDetailsImpl(
    private val id: Long,
    private val username: String,
    private val email: String,
    private val password: String,
    private val authorities: List<SimpleGrantedAuthority>
) : UserDetails {

    fun getId() = id
    fun getEmail() = email
    override fun getAuthorities() = authorities
    override fun getPassword() = password
    override fun getUsername() = username
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserDetailsImpl

        if (id != other.id) return false
        if (username != other.username) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (authorities != other.authorities) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + authorities.hashCode()
        return result
    }

    companion object {
        fun build(user : User) : UserDetailsImpl {
            val authorities = user.roles.map { SimpleGrantedAuthority(it.name.name) }
            return UserDetailsImpl(
                user.id,
                user.username,
                user.email,
                user.password,
                authorities)
        }
    }
}