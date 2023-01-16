package com.example.authapi

import com.example.authapi.models.Role
import com.example.authapi.models.RoleType
import com.example.authapi.repositories.RoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AuthApiApplication : CommandLineRunner {

	@Autowired
	lateinit var roleRepository : RoleRepository

	override fun run(vararg args: String?) {
		if (roleRepository.count() > 0)
			return
		RoleType.values().forEach {
			roleRepository.saveAndFlush(Role(
				name = it
			))
		}
	}
}

fun main(args: Array<String>) {
	runApplication<AuthApiApplication>(*args)
}
