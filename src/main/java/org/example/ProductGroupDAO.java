package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductGroupDAO {
    private Connection connection;

    public ProductGroupDAO() throws SQLException {
        this.connection = Database.getConnection();
    }

    public void addProductGroup(ProductGroup group) throws SQLException {
        String sql = "INSERT INTO product_groups (name, description) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, group.getName());
            statement.setString(2, group.getDescription());
            statement.executeUpdate();
        }
    }

    public void updateProductGroup(ProductGroup group) throws SQLException {
        String sql = "UPDATE product_groups SET name = ?, description = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, group.getName());
            statement.setString(2, group.getDescription());
            statement.setInt(3, group.getId());
            statement.executeUpdate();
        }
    }

    public ProductGroup getProductGroupByName(String name) throws SQLException {
        String sql = "SELECT * FROM product_groups WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractProductGroupFromResultSet(resultSet);
                }
            }
        }
        return null;
    }

    public List<ProductGroup> getAllGroups() throws SQLException { // Renamed method to getAllGroups
        List<ProductGroup> groups = new ArrayList<>();
        String sql = "SELECT * FROM product_groups";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                ProductGroup group = extractProductGroupFromResultSet(resultSet);
                groups.add(group);
            }
        }
        return groups;
    }

    public void deleteProductGroup(int groupId) throws SQLException {
        String deleteProductsSql = "DELETE FROM products WHERE group_id = ?";
        String deleteGroupSql = "DELETE FROM product_groups WHERE id = ?";

        try (PreparedStatement deleteProductsStmt = connection.prepareStatement(deleteProductsSql)) {
            deleteProductsStmt.setInt(1, groupId);
            deleteProductsStmt.executeUpdate();
        }

        try (PreparedStatement deleteGroupStmt = connection.prepareStatement(deleteGroupSql)) {
            deleteGroupStmt.setInt(1, groupId);
            deleteGroupStmt.executeUpdate();
        }
    }

    public boolean isProductGroupNameUnique(String name) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM product_groups WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt("count");
                    return count == 0;
                }
            }
        }
        return true; // If no result found, name is unique
    }

    private ProductGroup extractProductGroupFromResultSet(ResultSet resultSet) throws SQLException {
        ProductGroup group = new ProductGroup();
        group.setId(resultSet.getInt("id"));
        group.setName(resultSet.getString("name"));
        group.setDescription(resultSet.getString("description"));
        return group;
    }
}
