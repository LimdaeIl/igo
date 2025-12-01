package com.book.igo.tag.application.service;

import com.book.igo.tag.domain.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class TagService {


    private final TagRepository tagRepository;


}
