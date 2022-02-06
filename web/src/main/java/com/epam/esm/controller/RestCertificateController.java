package com.epam.esm.controller;

import com.epam.esm.CertificateService;
import com.epam.esm.CustomError;
import com.epam.esm.GiftCertificate;
import com.epam.esm.exception.GiftCertificateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "api/v1/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestCertificateController {

    private static final String MAX_CERTIFICATES_IN_REQUEST = "20";
    private static final String DEFAULT_ORDER = "ASC";


    private final CertificateService service;

    @Autowired
    public RestCertificateController(CertificateService service) {
        this.service = service;
    }


    @GetMapping("/{id}")
    public GiftCertificate getCertificate(@PathVariable Long id) {
        GiftCertificate giftCertificate = service.getEntity(id);
        if (giftCertificate == null) {
            throw new GiftCertificateNotFoundException(id);
        }
        return giftCertificate;
    }

    //TODO implement this method
    @GetMapping("/")
    public List<GiftCertificate> getCertificates(
            @RequestParam(value = "order", defaultValue = DEFAULT_ORDER) String order,
            @RequestParam(value = "max", defaultValue = MAX_CERTIFICATES_IN_REQUEST) int max,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "pattern", required = false) String pattern) {
        return service.getEntitiesWithParams(order, max, tag, pattern);
    }


    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    GiftCertificate create(@RequestBody GiftCertificate giftCertificate) {
        GiftCertificate createdGiftCertificate = service.create(giftCertificate);
        if (createdGiftCertificate == null) {
            throw new DuplicateKeyException("");
        }
        return createdGiftCertificate;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        ResponseEntity<String> response;
        if (service.delete(id)) {
            response = new ResponseEntity<>(HttpStatus.OK);
        } else {
            response = new ResponseEntity<>("No certificate with such id was found", HttpStatus.OK);
        }
        return response;
    }


    @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody GiftCertificate giftCertificate, @PathVariable Long id) {
        ResponseEntity<?> responseEntity;
        if (service.update(giftCertificate, id)) {
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } else {
            CustomError error = new CustomError(getErrorCode(400), "Error while updating");
            responseEntity = new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
        }
        return responseEntity;
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