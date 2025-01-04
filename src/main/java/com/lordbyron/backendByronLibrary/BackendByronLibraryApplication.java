package com.lordbyron.backendByronLibrary;

import com.lordbyron.backendByronLibrary.entity.Role;
import com.lordbyron.backendByronLibrary.services.UsersServices;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendByronLibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendByronLibraryApplication.class, args);
	}

	 @Bean
	 CommandLineRunner run(UsersServices  userService) {
	 	return args -> {

			 if (userService.countRoles()==0){
	 		userService.saveRole(new Role(null, "ROLE_ADMIN"));
	 		userService.saveRole(new Role(null, "ROLE_USER"));
			 }

//	 		userService.saveUser(new Employee(null, "Johson Rodriguez", "jrodriguez", "asdf1234", new ArrayList<>(),true));
//	 		userService.saveUser(new Employee(null, "Luis Flores", "lflores", "asdf1234", new ArrayList<>(),true));
//
//	 		userService.addRoleToUser("jrodriguez", "ROLE_ADMIN");
//	 		userService.addRoleToUser("jrodriguez", "ROLE_USER");
//	 		userService.addRoleToUser("lflores", "ROLE_USER");
	 	};
	 }


}
