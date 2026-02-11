package com.social.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class CreateGroupDTO {
    private String name;
    private Set<Long> participantIds;
    private String groupImageUrl;
}
