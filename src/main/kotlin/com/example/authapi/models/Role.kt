package com.example.authapi.models

import javax.persistence.*

@Entity
@Table(name = "roles")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long = 0,
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    var name : RoleType = RoleType.ROLE_USER
)