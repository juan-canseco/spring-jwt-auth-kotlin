# JWT Authentication Api

# Intro
Simple Jwt rest api for learning purposes.

## AuthController 
```kotlin
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
```
