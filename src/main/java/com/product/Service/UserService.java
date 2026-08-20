package com.product.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import com.DTO.LoginRequest;
import com.DTO.UserDto;
import com.product.Entity.User;
import com.product.Repository.UserRepo;
import com.product.Security.JwtUtil;
import com.product.response.UserResponseDto;

@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authManager;

    // Register
    public String register(User user) {

        // Default Role
        if (user.getRole() == null) {
            user.setRole("USER");
        }

        user.setPassword(
                encoder.encode(user.getPassword()));

        repo.save(user);

        return "User Registered Successfully";
    }

    // Login
    public String login(LoginRequest request) {

        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getMail(),
                        request.getPassword()));

        // Get user from DB
        User user = repo.findByMail(request.getMail())
                .orElseThrow(() ->
                        new RuntimeException("User Not Found"));

        // Generate JWT with Role
        return jwtUtil.generateToken(
                user.getMail(),
                user.getRole());
    }
    
    public Page<UserResponseDto> allUsers(int page, int size){
    	Pageable pageble=PageRequest.of(page, size);
    	Page<User> users=repo.findAll(pageble);
    	
    	return users.map(user -> {

    		UserResponseDto response = new UserResponseDto();

            response.setId(user.getId());
            response.setName(user.getName());
            response.setMail(user.getMail());
            response.setRole(user.getRole());
            response.setAddress(user.getAddress());
            response.setMobile(user.getMobile());
            response.setPinCode(user.getPinCode());

            return response;
        });
    }
    public UserResponseDto updateUser(Long userId, UserDto userDto){
		
    	User user=repo.findById(userId).orElseThrow(()-> new RuntimeException("User Not found with UserId: "+userId));
    	
    	user.setAddress(userDto.getAddress());
    	user.setMail(userDto.getMail());
    	user.setMobile(userDto.getMobile());
    	user.setName(userDto.getName());
    	user.setPassword(userDto.getPassword());
    	user.setPinCode(userDto.getPinCode());
    	user.setSurname(userDto.getSurname());
    	user.setRole(userDto.getRole());
    	
    	User users=repo.save(user);
    	
    	UserResponseDto userResponse=new UserResponseDto();
    	userResponse.setAddress(users.getAddress());
    	userResponse.setId(users.getId());
    	userResponse.setMail(users.getMail());
    	userResponse.setMobile(users.getMobile());
    	userResponse.setPinCode(users.getPinCode());
    	userResponse.setRole(users.getRole());
    	userResponse.setMassage("User Updated Successfully.....");
    	
    	return userResponse;
    }
    public boolean deleteUser(Long userId) {
		if(repo.existsById(userId)) {
    	repo.deleteById(userId);
    	return true;
		}
    	return false;
    }
}