package com.epam.esm.controller;

import com.epam.esm.CustomError;
import com.epam.esm.GiftCertificate;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.NoEntitiesFoundException;
import com.epam.esm.impl.CertificateService;
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

    /**
     * Returns GiftCertificate with provided id
     *
     * @param id GiftCertificate id
     * @return GiftCertificate if found, if null GiftCertificateNotFoundException is handled
     * @throws EntityNotFoundException
     */
    @GetMapping("/{id}")
    public GiftCertificate getCertificate(@PathVariable Long id) {
        GiftCertificate giftCertificate = service.getEntity(id);
        if (giftCertificate == null) {
            throw new EntityNotFoundException(id);
        }
        return giftCertificate;
    }

    /**
     * Returns List<GiftCertificate> based on provided parameters
     *
     * @param order   list sorting order, ASC by default
     * @param max     maximum number of rows, by default 20
     * @param tag     tag name to use in search
     * @param pattern tag name or description with this pattern
     * @return List<GiftCertificate> with applied search parameters, if no certificates are found -  NoEntitiesFoundException is handled
     * @throws NoEntitiesFoundException
     */
    @GetMapping("/")
    public List<GiftCertificate> getCertificates(
            @RequestParam(value = "order", defaultValue = DEFAULT_ORDER) String order,
            @RequestParam(value = "max", defaultValue = MAX_CERTIFICATES_IN_REQUEST) int max,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "pattern", required = false) String pattern) {
        List<GiftCertificate> giftCertificates = service.getEntitiesWithParams(order, max, tag, pattern);
        if (giftCertificates.isEmpty()) {
            throw new NoEntitiesFoundException();
        }
        return giftCertificates;
    }

    /**
     * Creates a GiftCertificate
     *
     * @param giftCertificate to be created
     * @return created GiftCertificate
     * @throws DuplicateKeyException
     */
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

    /**
     * Deletes a GiftCertificate with provided id
     *
     * @param id to find GiftCertificate
     * @return ResponseEntity  with OK status if GiftCertificate was deleted, if GiftCertificate was not found - OK ResponseEntity with message
     */
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

    /**
     * Updates existing GiftCertificate
     *
     * @param giftCertificate new state of GiftCertificate
     * @param id              to find GiftCertificate for update
     * @return ResponseEntity  with OK status if GiftCertificate was update, if GiftCertificate was not updated - OK ResponseEntity Error and message
     */
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


    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomError certificateNotFound(EntityNotFoundException e) {
        long certificateId = e.getEntityId();
        return new CustomError(getErrorCode(404), "Gift Certificate [" + certificateId + "] not found");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CustomError duplicateKeyValues(DuplicateKeyException e) {
        return new CustomError(getErrorCode(500), e.getCause().getMessage());
    }

    @ExceptionHandler(NoEntitiesFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomError certificatesNotFound(NoEntitiesFoundException e) {
        return new CustomError(getErrorCode(404), "No certificates are found");
    }


    private static int getErrorCode(int errorCode) {
        long counter = 0;
        counter++;
        return Integer.parseInt(errorCode + String.valueOf(counter));
    }

}