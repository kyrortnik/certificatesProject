package com.epam.esm.controller;


import com.epam.esm.GiftCertificate;
import com.epam.esm.Tag;
import com.epam.esm.TagService;
import com.epam.esm.exception.TagNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "rest/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestTagController {

    private final TagService service;


    @Autowired
    public RestTagController(TagService service) {
        this.service = service;
    }


    @GetMapping("/{id}")
    public Tag getOne(@PathVariable Long id) {
        Tag tag = service.getOne(id);
        if (tag == null) {
            throw new TagNotFoundException(id);
        }
        return tag;
    }

    @GetMapping("/")
    public List<Tag> getTags(
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @RequestParam(value = "max", defaultValue = "20") int max) {
        return service.getAll(order, max);
    }


    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Tag create(@RequestBody Tag tag) {
        Tag createdTag = service.create(tag);
        if (createdTag == null) {
            throw new DuplicateKeyException("");
        }
        return createdTag;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
