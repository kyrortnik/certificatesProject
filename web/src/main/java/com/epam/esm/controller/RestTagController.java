package com.epam.esm.controller;


import com.epam.esm.CRUDService;
import com.epam.esm.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "rest/tags", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestTagController {

    private CRUDService<Tag> service;

    @GetMapping("/{id}")
    public ResponseEntity<Tag> get(@PathVariable Long id) {

        Tag tag = service.getOne(id);
        HttpStatus status = tag != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(tag, status);
    }

}
