package com.example.BlogBackend.Services;

import com.example.BlogBackend.Models.Tag.CreateTagDto;
import com.example.BlogBackend.Models.Tag.Tag;
import com.example.BlogBackend.Repositories.TagRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TagService implements ITagService{
    private final TagRepository tagRepository;

    @Transactional
    public List<Tag> getTagList() {
        return tagRepository.findAll();
    }

    @Transactional
    public Tag createTag(CreateTagDto createTagDto) {
        Tag tag = new Tag(UUID.randomUUID(), LocalDateTime.now(), createTagDto.getName());
        tagRepository.save(tag);

        return tag;
    }
}
