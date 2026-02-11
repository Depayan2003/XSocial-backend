package com.social.Service;

import com.social.DTO.UserResponseDTO;
import com.social.Model.User;

import java.util.List;

public interface UserService {

	UserResponseDTO getCurrentUser();
	UserResponseDTO getUserById(Long id);
	List<UserResponseDTO> searchUsers(String query);
	UserResponseDTO updateProfile(String name, String profileImageUrl);
	User getCurrentUserEntity();
}
