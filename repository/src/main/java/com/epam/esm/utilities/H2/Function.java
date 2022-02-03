package com.epam.esm.utilities.H2;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Profile("dev")
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
                insertStatement.setString(1,tag);
                insertStatement.executeUpdate();
            }
        }
    }

    public static List<Integer> getTagIdsForNames(Connection connection, List<String> tagNames) throws SQLException {
        List<Integer> tagsIds = new ArrayList<>();
        PreparedStatement statement;
        ResultSet rs;

        for (String name : tagNames) {
            statement = connection.prepareStatement(GET_TAG_ID);
            statement.setString(1, name);
            rs = statement.executeQuery();
            if (rs.next()) {
                tagsIds.add(rs.getInt(1));
            }
        }
        return tagsIds;
    }

    public static void createCertificateTagRelation(Connection connection, int createdGiftId, List<Integer> list) throws SQLException {
        PreparedStatement statement;

        for (Integer id : list) {
            statement = connection.prepareStatement(CREATE_CERTIFICATE_TAG_RELATION);
            statement.setInt(1, createdGiftId);
            statement.setInt(2, id);
            statement.executeUpdate();
        }
    }

}
