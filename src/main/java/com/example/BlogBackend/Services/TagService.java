package com.example.BlogBackend.Services;

import com.example.BlogBackend.Models.Tag.CreateTagDto;
import com.example.BlogBackend.Models.Tag.TagDto;
import com.example.BlogBackend.Repositories.TagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;
    public List<TagDto> getTagList(){
        return tagRepository.findAll();
    }

    public TagDto createTag(CreateTagDto createTagDto){
        TagDto tag = new TagDto(UUID.randomUUID(), LocalDateTime.now(), createTagDto.getName());
        tagRepository.save(tag);

        return tag;
    }
}
