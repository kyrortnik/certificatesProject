package com.epam.esm.controller;


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

    @GetMapping("/{id}")
    public ResponseEntity<Tag> get(@PathVariable Long id) {
        Tag giftCertificate = new Tag(1L,"tag");
//        GiftCertificate giftCertificate = service.getOne(id);
//        HttpStatus status = giftCertificate != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        HttpStatus status = id == 1 ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(giftCertificate, status);
    }

}
