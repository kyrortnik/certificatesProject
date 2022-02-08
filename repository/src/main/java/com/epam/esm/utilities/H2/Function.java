package com.epam.esm.utilities.H2;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//@Profile("dev")
@Component
public class Function {

    public static final String COUNT = " SELECT COUNT(name) FROM tags WHERE name = ?";

    public static final String CREATE_TAG = "INSERT INTO tags VALUES (DEFAULT,?)";

    public static final String GET_TAG_ID = " SELECT tags.id  FROM tags WHERE name = ?";

    public static final String CREATE_CERTIFICATE_TAG_RELATION = "INSERT INTO certificates_tags VALUES (?, ?)";


    public static void createNewTags(Connection connection, List<String> tagNames) throws SQLException {
        PreparedStatement countStatement;
        PreparedStatement insertStatement;
        ResultSet countRs;

        for (String tag : tagNames) {
            countStatement = connection.prepareStatement(COUNT);
            countStatement.setString(1, tag);
            countRs = countStatement.executeQuery();
            if (countRs.next() && countRs.getInt(1) == 0) {
                insertStatement = connection.prepareStatement(CREATE_TAG);
                insertStatement.setString(1, tag);
                insertStatement.executeUpdate();
            }
        }
    }

    public static Long[] getTagIdsForNames(Connection connection, List<String> tagNames) throws SQLException {
        List<Long> tagsIds = new ArrayList<>();
        PreparedStatement statement;
        ResultSet rs;

        for (String name : tagNames) {
            statement = connection.prepareStatement(GET_TAG_ID);
            statement.setString(1, name);
            rs = statement.executeQuery();
            if (rs.next()) {
                tagsIds.add(rs.getLong(1));
            }
        }
        return tagsIds.toArray(new Long[0]);
    }


    public static void createCertificateTagRelation(Connection connection, Long createdGiftId, List<Long> list) throws SQLException {
        PreparedStatement statement;

        for (Long id : list) {
            statement = connection.prepareStatement(CREATE_CERTIFICATE_TAG_RELATION);
            statement.setLong(1, createdGiftId);
            statement.setLong(2, id);
            statement.executeUpdate();
        }
    }

}
