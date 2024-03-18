package com.example.communiverse.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCommunitiesModifyRequest {
    private List<String> createdCommunities;
    private List<String> moderatedCommunities;
    private List<String> memberCommunities;
}
