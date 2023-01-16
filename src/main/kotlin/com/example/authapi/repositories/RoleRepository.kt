package com.example.authapi.repositories

import com.example.authapi.models.Role
import com.example.authapi.models.RoleType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findRoleByName(name : RoleType) : Role?
}