package com.epam.esm.controller;

import com.epam.esm.CRUDService;
import com.epam.esm.CertificateService;
import com.epam.esm.CustomError;
import com.epam.esm.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "api/certificates", produces = MediaType.APPLICATION_JSON_VALUE)
public class RestCertificateController {


    private final CRUDService<GiftCertificate> service;

    @Autowired
    public RestCertificateController(CertificateService service) {
        this.service = service;
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {

        GiftCertificate giftCertificate = service.getOne(id);
        if (giftCertificate == null) {
            CustomError error = new CustomError(123, "error message");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(giftCertificate, HttpStatus.OK);
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

        } else {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }


    /*@DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return new ResponseEntity<Object>();
    }*/

   /* @PutMapping(value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GiftCertificate> update(@RequestBody GiftCertificate giftCertificate, @PathVariable Long id) {
        service.update(giftCertificate, id);
    }*/

}