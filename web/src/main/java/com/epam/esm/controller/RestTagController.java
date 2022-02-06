package com.epam.esm.controller;

import com.epam.esm.CustomError;
import com.epam.esm.Tag;
import com.epam.esm.impl.TagService;
import com.epam.esm.exception.NoTagsFoundException;
import com.epam.esm.exception.TagNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestTagController {

    private final TagService service;


    @Autowired
    public RestTagController(TagService service) {
        this.service = service;
    }


    @GetMapping("/{id}")
    public Tag getTag(@PathVariable Long id) {
        Tag tag = service.getEntity(id);
        if (tag == null) {
            throw new TagNotFoundException(id);
        }
        return tag;
    }

    @GetMapping("/")
    public List<Tag> getTags(
            @RequestParam(value = "order", defaultValue = "ASC") String order,
            @RequestParam(value = "max", defaultValue = "20") int max) {
        List<Tag> tags = service.getEntities(order, max);
        if (tags.isEmpty()) {
            throw new NoTagsFoundException();
        }
        return tags;
    }


    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Tag create(@RequestBody Tag tag) {
        Tag createdTag = service.create(tag);
        if (createdTag == null) {
            throw new DuplicateKeyException("");
        }
        return createdTag;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        ResponseEntity<String> response;
        if (service.delete(id)) {
            response = new ResponseEntity<>(HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("No tag with such id was found", HttpStatus.OK);
        }
        return response;
    }


    @ExceptionHandler(TagNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomError tagNotFound(TagNotFoundException e) {
        long tagId = e.getTagId();
        return new CustomError(getErrorCode(404), "Tag [" + tagId + "] not found");
    }

    @ExceptionHandler(NoTagsFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomError tagsNotFound(NoTagsFoundException e) {
        return new CustomError(getErrorCode(404), "No tags are found");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomError duplicateKeyException(DuplicateKeyException e) {
        return new CustomError(getErrorCode(400), "Tag with such name already exists");
    }


    private static int getErrorCode(int errorCode) {
        long counter = 0;
        counter++;
        return Integer.parseInt(errorCode + String.valueOf(counter));
    }
}
