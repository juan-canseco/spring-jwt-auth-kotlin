package com.example.authapi.payload.response

data class JwtResponse(
    var token : String = "",
    var type : String = "Bearer",
    var username : String = "",
    var email : String = "",
    var roles : List<String> = mutableListOf()
)