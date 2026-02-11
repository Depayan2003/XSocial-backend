package com.social.Repository;

import com.social.Model.AdminAction;
import com.social.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminActionRepository
        extends JpaRepository<AdminAction, Long> {

    List<AdminAction> findByTargetUserAndActionOrderByCreatedAtDesc(
        User targetUser,
        String action
    );
    
    List<AdminAction> findByTargetUserAndActionIn(
    	    User user, List<String> actions
    	);
}
