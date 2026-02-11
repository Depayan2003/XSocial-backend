package com.social.Service.Impl;

import com.social.Model.BlockedUser;
import com.social.Model.User;
import com.social.Repository.BlockedUserRepository;
import com.social.Service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

    private final BlockedUserRepository blockedRepo;

    @Override
    public void blockUser(User blocker, User blocked) {
        if (blockedRepo.existsByBlockerAndBlocked(blocker, blocked)) return;

        BlockedUser block = new BlockedUser();
        block.setBlocker(blocker);
        block.setBlocked(blocked);
        block.setCreatedAt(LocalDateTime.now());

        blockedRepo.save(block);
    }

    @Override
    public boolean isBlocked(User blocker, User blocked) {
        return blockedRepo.existsByBlockerAndBlocked(blocker, blocked);
    }
}
