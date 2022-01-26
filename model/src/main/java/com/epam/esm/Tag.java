package com.epam.esm;

import org.springframework.stereotype.Component;

import java.security.cert.Certificate;
import java.util.List;

@Component
public class Tag {

    private Long id;

    private String name;

    private List<Certificate> certificates;


    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public Tag(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Tag(Long id, String name, List<Certificate> certificates) {
        this.id = id;
        this.name = name;
        this.certificates = certificates;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Certificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certificate> certificates) {
        this.certificates = certificates;
    }
}
