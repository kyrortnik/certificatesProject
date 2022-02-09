package com.epam.esm;

import com.epam.esm.impl.TagService;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class TagServiceTest {

    //mock
    private final TagRepository tagRepository = Mockito.mock(TagRepository.class, withSettings().verboseLogging());

    //class under test
    private final TagService tagService = new TagService(tagRepository);

    //params
    private final long tagId = 1L;
    private final String tagName = "tag name";

    private final String order = "ASC";
    private final int max = 20;

    private final long giftCertificateId = 1L;


    private final List<Tag> tags = Arrays.asList(
            new Tag(1L, "first tag"),
            new Tag(2L, "second tag"),
            new Tag(3L, "third tag")
    );


    @Test
    void testGetEntity_positive() {
        Tag tag = new Tag(tagId, tagName);

        when(tagRepository.getTag(tagId)).thenReturn(tag);

        Tag returnTag = tagService.getEntity(tagId);

        verify(tagRepository).getTag(tagId);
        assertEquals(tag, returnTag);
    }

    @Test
    void testGetEntities_positive() {

        when(tagRepository.getTags(order, max)).thenReturn(tags);

        List<Tag> returnTags = tagService.getEntities(order, max);

        verify(tagRepository).getTags(order, max);
        assertEquals(tags, returnTags);
    }

    @Test
    void testCreate_positive() {
        Tag tag = new Tag(tagId, tagName);
        when(tagRepository.create(tag)).thenReturn(tag);

        Tag returnTag = tagService.create(tag);

        verify(tagRepository).create(tag);
        assertEquals(tag, returnTag);
    }

    @Test
    void testDelete_positive() {
        boolean result;
        when(tagRepository.delete(tagId)).thenReturn(true);

        result = tagService.delete(tagId);

        verify(tagRepository).delete(tagId);
        assertTrue(result);
    }

    @Test
    void testGetTagsForCertificate_positive(){
        when(tagRepository.getTagsForCertificate(giftCertificateId)).thenReturn(tags);

        List<Tag> returnTags = tagService.getTagsForCertificate(giftCertificateId);

        verify(tagRepository).getTagsForCertificate(giftCertificateId);
        assertEquals(tags,returnTags);
    }

}