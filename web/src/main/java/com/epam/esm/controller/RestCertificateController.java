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
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "api/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestCertificateController {

    private static final String MAX_CERTIFICATES_IN_REQUEST = "20";
    private static final String DEFAULT_ORDER = "ASC";


    private final CertificateService service;

    @Autowired
    public RestCertificateController(CertificateService service) {
        this.service = service;
    }


    @GetMapping("/{id}")
    public GiftCertificate getOne(@PathVariable Long id) {
        GiftCertificate giftCertificate = service.getOne(id);
        if (giftCertificate == null) {
            throw new GiftCertificateNotFoundException(id);
        }
        return giftCertificate;
    }

    //TODO test this method
    @GetMapping("/")
    public List<GiftCertificate> getCertificates(
            @RequestParam(value = "order", defaultValue = DEFAULT_ORDER) String order,
            @RequestParam(value = "max", defaultValue = MAX_CERTIFICATES_IN_REQUEST) int max,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "pattern", required = false) String pattern) {
        return service.getAllWithParams(order, max, tag, pattern);
    }


    @PostMapping(path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody GiftCertificate create(@RequestBody GiftCertificate giftCertificate) {
        GiftCertificate createdGiftCertificate = service.create(giftCertificate);
        if (createdGiftCertificate == null) {
            throw new DuplicateKeyException("");
        }
        return createdGiftCertificate;
    }

    //TODO if no element found - return 404
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    //TODO @Deprecated
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


    //TODO replace reflection
    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@RequestBody Map<Object, Object> fields, @PathVariable Long id) {
        GiftCertificate certificate = service.getOne(id);
        if (certificate != null) {
            fields.forEach((key, value) -> {
                Field field = ReflectionUtils.findField(GiftCertificate.class, (String) key);
                field.setAccessible(true);
                ReflectionUtils.setField(field, certificate, value);
            });
            return new ResponseEntity<>(certificate, HttpStatus.OK);
        } else {
            CustomError error = new CustomError(getErrorCode(400), "Error while patching");
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