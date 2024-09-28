package com.example.employee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests((requests) -> requests
                                .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
		return http.build();
	}
	
	 @Bean
	    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

		 
		 UserDetails admin=User.withUsername("admin")
				 .password(passwordEncoder.encode("12345"))
				 .authorities("admin")
				 .build();
		 
		 UserDetails user = User.withUsername("Neeraj")
		            .password(passwordEncoder.encode("12345"))
		            .roles("USER")
		            .build();
		        return new InMemoryUserDetailsManager(admin,user);
	    }
	 
	 @Bean
	    public PasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder();
	    }

}
