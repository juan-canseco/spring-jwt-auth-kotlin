package com.example.authapi.models

import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "users", uniqueConstraints = [
    UniqueConstraint(columnNames = ["username"]),
    UniqueConstraint(columnNames = ["email"])
])
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long = 0,
    @NotBlank
    @Size(max = 20)
    var username : String = "",
    @NotBlank
    @Size(max = 50)
    @Email
    var email : String = "",
    @NotBlank
    @Size(max = 120)
    var password : String = "",
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")])
    var roles : List<Role> = mutableListOf()
)
