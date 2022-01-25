package com.epam.esm.controller;

import com.epam.esm.CRUDService;
import com.epam.esm.CertificateService;
import com.epam.esm.CustomError;
import com.epam.esm.GiftCertificate;
import com.epam.esm.exception.GiftCertificateNotFoundException;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "api/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestCertificateController {


    private final CRUDService<GiftCertificate> service;

    @Autowired
    public RestCertificateController(CertificateService service) {
        this.service = service;
    }


    /**
     * @GetMapping("/") getList()
     */

 /*   @GetMapping("/")
    public ResponseEntity<?> getList() {
        List<GiftCertificate> certificates = service.getList();
    }*/
    @GetMapping("/{id}")
    public GiftCertificate get(@PathVariable Long id) {
        GiftCertificate giftCertificate = service.getOne(id);
        if (giftCertificate == null) {
            throw new GiftCertificateNotFoundException(id);
        }
        return giftCertificate;
    }


    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public GiftCertificate create(@RequestBody GiftCertificate giftCertificate) {
        GiftCertificate createdGiftCertificate = service.create(giftCertificate);
        if (createdGiftCertificate == null) {
            throw  new DuplicateKeyException("");
        }
        return createdGiftCertificate;
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    //TODO refactor to PATCH
    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody GiftCertificate giftCertificate, @PathVariable Long id) {
        if (service.update(giftCertificate, id)) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            CustomError error = new CustomError(getErrorCode(400), "Error while updating");
            return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
        }

    }


    @ExceptionHandler(GiftCertificateNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomError certificateNotFound(GiftCertificateNotFoundException e) {
        long certificateId = e.getCertificateId();
        return new CustomError(getErrorCode(404), "Gift Certificate [" + certificateId + "] not found");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CustomError duplicateKeyValues(DuplicateKeyException e) {
        return new CustomError(getErrorCode(500), e.getCause().getMessage());
    }


    private static int getErrorCode(int errorCode) {
        long counter = 0;
        counter++;
        return Integer.parseInt(errorCode + String.valueOf(counter));

    }

}