package com.example.BlogBackend.Services;

import com.example.BlogBackend.Models.Tag.CreateTagDto;
import com.example.BlogBackend.Models.Tag.Tag;

import java.util.List;

public interface ITagService {
    List<Tag> getTagList();
    Tag createTag(CreateTagDto createTagDto);
}
