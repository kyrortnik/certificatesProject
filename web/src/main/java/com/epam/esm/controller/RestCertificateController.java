package com.epam.esm.controller;

import com.epam.esm.CertificateService;


import com.epam.esm.GiftCertificate;
import com.epam.esm.CRUDService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UncheckedIOException;
import java.rmi.ServerException;

@RestController
@RequestMapping(value = "api/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestCertificateController {


    private final CRUDService<GiftCertificate> service;

    @Autowired
    public RestCertificateController(CertificateService service) {
        this.service = service;
    }


    @GetMapping("/{id}")
    public ResponseEntity<GiftCertificate> get(@PathVariable Long id) {
        GiftCertificate giftCertificate = service.getOne(id);
        HttpStatus status = giftCertificate != null ? HttpStatus.OK : HttpStatus.NOT_FOUND;
        return new ResponseEntity<>(giftCertificate, status);
    }

/*
* TODO negative scenarios
* */
    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GiftCertificate> create(@RequestBody GiftCertificate giftCertificate) {
        GiftCertificate createsGiftCertificate = service.create(giftCertificate);
        if (createsGiftCertificate != null) {

            return new ResponseEntity<>(createsGiftCertificate, HttpStatus.CREATED);

        }else{
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

 /*   @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WebGiftCertificate> createWithLocation(@RequestBody WebGiftCertificate webGiftCertificate) {
        WebGiftCertificate created =  gift;
       URI createdURI =  ServletUriComponentsBuilder.fromCurrentContextPath().path("rest/certificates" + "/{id}").buildAndExpand(created.getId()).toUri();
       return ResponseEntity.created(createdURI).body(created);
    }*/

  /*  @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
       // super.delete(id);
    }*/

   /* @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody WebGiftCertificate webGiftCertificate, @PathVariable Long id) {
       // super.update(giftCertificate, id);
    }*/

}