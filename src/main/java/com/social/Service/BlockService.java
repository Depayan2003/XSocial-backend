package com.social.Service;

import com.social.Model.User;

public interface BlockService {

    void blockUser(User blocker, User blocked);

    boolean isBlocked(User blocker, User blocked);
}
