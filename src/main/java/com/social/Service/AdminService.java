package com.social.Service;

import java.util.List;

import com.social.DTO.AdminMessageDTO;
import com.social.Model.User;

public interface AdminService {

    void disableUser(User target, String reason);

    void warnUser(User target, String reason);
    
    public List<AdminMessageDTO> getMyAdminMessages(User user);

}
