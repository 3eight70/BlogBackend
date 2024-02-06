package com.example.BlogBackend.Services;

import com.example.BlogBackend.Mappers.CommunityMapper;
import com.example.BlogBackend.Models.Community.Community;
import com.example.BlogBackend.Models.Community.CommunityDto;
import com.example.BlogBackend.Models.Community.CreateCommunityDto;
import com.example.BlogBackend.Models.Exceptions.ExceptionResponse;
import com.example.BlogBackend.Models.Post.FullPost;
import com.example.BlogBackend.Models.User.User;
import com.example.BlogBackend.Repositories.CommunityRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {
    private final CommunityRepository communityRepository;

    public ResponseEntity<?> getCommunities(){
        List <Community> communities = communityRepository.findAll();
        List<CommunityDto> communitiesDto = new ArrayList<>();

        for(Community community: communities){
            communitiesDto.add(CommunityMapper.communityToCommunityDto(community));
        }
        return ResponseEntity.ok(communitiesDto);
    }

    public ResponseEntity<?> createCommunity(CreateCommunityDto createCommunityDto, User user){
        if (communityRepository.findByName(createCommunityDto.getName()) != null){
            return new ResponseEntity<>(new ExceptionResponse(HttpStatus.BAD_REQUEST.value(), "Сообщество с таким названием уже существует"), HttpStatus.BAD_REQUEST);
        }

        Community community = CommunityMapper.createCommunityDtoToFullCommunityDto(createCommunityDto);

        community.getAdministrators().add(user);
        community.updateAdministrators();

        communityRepository.save(community);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> subscribe(User user, UUID id) {
        Community community = communityRepository.findCommunityById(id);
        if (community == null) {
            throw new EntityNotFoundException();
        }

        if (community.getSubscribers().contains(user)) {
            throw new IllegalStateException();
        }

        community.getSubscribers().add(user);
        community.updateSubscribers();

        communityRepository.save(community);

        return ResponseEntity.ok().build();
    }

    @Transactional
    public ResponseEntity<?> unsubscribe(User user, UUID id) {
        Community community = communityRepository.findCommunityById(id);
        if (community == null) {
            throw new EntityNotFoundException();
        }

        if (!community.getSubscribers().contains(user)) {
            throw new IllegalStateException();
        }

        community.getSubscribers().remove(user);
        community.updateSubscribers();

        communityRepository.save(community);

        return ResponseEntity.ok().build();
    }
}
