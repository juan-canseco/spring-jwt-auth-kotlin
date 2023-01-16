package com.example.authapi.security.jwt

import com.example.authapi.security.services.UserDetailsImpl
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*
import io.jsonwebtoken.*
import java.lang.IllegalArgumentException

@Component
class JwtUtils {

    private val logger: Logger = LoggerFactory.getLogger(JwtUtils::class.java)
    @Value("\${app.jwtSecret}")
    private val jwtSecret: String? = null
    @Value("\${app.jwtExpirationMs}")
    private val jwtExpirationMs : Int = 0


    private fun getExpirationDate() : Date {
        return Date((Date()).time + jwtExpirationMs)
    }

    fun generateJwtToken(authentication : Authentication) : String {
        val userPrincipal : UserDetailsImpl = authentication.principal as UserDetailsImpl
        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(Date())
            .setExpiration(getExpirationDate())
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact()
    }

    fun getUserNameFromJwtToken(token : String) : String {
        return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .body
            .subject
    }

    fun validateJwtToken(authToken : String) : Boolean {
        return try {
            Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(authToken)
            true
        }
        catch (e : Exception) {
            when (e) {
                is SignatureException -> {
                    logger.error("Invalid JWT signature: {}", e.message)
                }
                is MalformedJwtException -> {
                    logger.error("Invalid JWT token: {}", e.message)
                }
                is ExpiredJwtException -> {
                    logger.error("JWT token is expired: {}", e.message)
                }
                is UnsupportedJwtException -> {
                    logger.error("JWT token is unsupported: {}", e.message)
                }
                is IllegalArgumentException -> {
                    logger.error("JWT claims string is empty: {}", e.message)
                }
            }
            false
        }
    }

}