package com.example.authapi.controllers

import com.example.authapi.models.Role
import com.example.authapi.models.RoleType
import com.example.authapi.models.User
import com.example.authapi.payload.request.LoginRequest
import com.example.authapi.payload.request.SignupRequest
import com.example.authapi.payload.response.JwtResponse
import com.example.authapi.payload.response.MessageResponse
import com.example.authapi.repositories.RoleRepository
import com.example.authapi.repositories.UserRepository
import com.example.authapi.security.jwt.JwtUtils
import com.example.authapi.security.services.UserDetailsImpl
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.RuntimeException
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
class AuthController constructor(
    private val authenticationManager : AuthenticationManager,
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val encoder : PasswordEncoder,
    private val jwtUtils: JwtUtils
) {

    @PostMapping("/signIn")
    fun authenticateUser(@Valid @RequestBody loginRequest : LoginRequest) : ResponseEntity<Any> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        )

        SecurityContextHolder.getContext().authentication = authentication
        val jwt = jwtUtils.generateJwtToken(authentication)
        val userDetails = authentication.principal as UserDetailsImpl
        val roles = userDetails.authorities.map {
            it.authority
        }

        val result = JwtResponse(
            token = jwt,
            username = userDetails.username,
            email =  userDetails.getEmail(),
            roles = roles
        )
        return ResponseEntity.ok(result)
    }


    private fun mapStrRoleToEntity(str : String) : Role {
        val roleType = when (str) {
            "admin" -> RoleType.ROLE_ADMIN
            "mod" -> RoleType.ROLE_MODERATOR
            else -> RoleType.ROLE_USER
        }
        roleRepository.findRoleByName(roleType)?.let {role ->
            return role
        }
        throw RuntimeException("Error: Role is not found.")
    }

    @PostMapping("/signUp")
    fun registerUser(@Valid @RequestBody signupRequest: SignupRequest) : ResponseEntity<Any> {
        if (userRepository.existsUserByUsername(signupRequest.username)) {
            return ResponseEntity
                .badRequest()
                .body(MessageResponse("Error: Username is already taken!"))
        }

        if (userRepository.existsUserByEmail(signupRequest.email)) {
            return ResponseEntity
                .badRequest()
                .body(MessageResponse("Error: Email is already in use!"))
        }


        if (signupRequest.roles.isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body(MessageResponse("Error: there are no roles selected!"))
        }

        val user = User(
            username = signupRequest.username,
            email = signupRequest.email,
            password = encoder.encode(signupRequest.password),
            roles = signupRequest.roles.map { mapStrRoleToEntity(it) }
        )

        userRepository.save(user)

        return ResponseEntity.ok(MessageResponse(
            message = "User registered successfully!"
        ))
    }

}