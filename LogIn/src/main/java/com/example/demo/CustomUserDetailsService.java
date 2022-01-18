package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository repo;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = repo.findByEmail(email);
		
		if(user == null) {
			throw new UsernameNotFoundException("User not Found");
		}
		
		return new CustomUserDetails(user);
	}
	
	public void updateResetPassword(String token, String email) {
		User user = repo.findByEmail(email);
		
		if(user != null) {
			user.setResetPasswordToken(token);
			repo.save(user);
		}else {
			throw new UsernameNotFoundException("Could not find any customer with email" + email);
		}
	}
	
	public User get(String resetPasswordToken) {
		return repo.findByResetPasswordToken(resetPasswordToken);
	}
	
	public void updatePassword(User user, String newPassword) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(newPassword);
		
		user.setPassword(encodedPassword);
		user.setResetPasswordToken(null);
		
		repo.save(user);
	}

}
