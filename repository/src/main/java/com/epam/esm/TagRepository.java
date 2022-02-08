package com.epam.esm;

import java.util.List;

public interface TagRepository {

    Tag getTag(Long id);

    List<Tag> getTags(String order, int max);

    boolean delete(Long id);

    Tag create(Tag element);

    List<Tag> getTagsForCertificate(Long id);
}
