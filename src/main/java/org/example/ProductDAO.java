package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private Connection connection;

    public ProductDAO() throws SQLException {
            this.connection = Database.getConnection();
        }

        // Method to add a product with unique name constraint
        public void addProduct (Product product) throws SQLException {
            // Check if a product with the same name already exists
            if (isProductExists(product.getName())) {
                throw new SQLException("Product with name '" + product.getName() + "' already exists.");
            }

            String sql = "INSERT INTO products (name, description, manufacturer, quantity, price_per_unit, group_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, product.getName());
                statement.setString(2, product.getDescription());
                statement.setString(3, product.getManufacturer());
                statement.setInt(4, product.getQuantity());
                statement.setDouble(5, product.getPricePerUnit());
                statement.setInt(6, product.getGroupId());
                statement.executeUpdate();
            }
        }


        public Product getProductByName (String productName) throws SQLException {
            Product product = null;
            String query = "SELECT * FROM products WHERE name = ?";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, productName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        product = extractProductFromResultSet(resultSet);
                    }
                }
            }

            return product;
        }

        // Method to search products by name
        public List<Product> searchProductsByName (String name) throws SQLException {
            List<Product> products = new ArrayList<>();
            String query = "SELECT * FROM products WHERE LOWER(name) LIKE LOWER(?)";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + name + "%");
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Product product = extractProductFromResultSet(resultSet);
                        products.add(product);
                    }
                }
            }

            return products;
        }

        // Method to check if a product with the given name already exists
        private boolean isProductExists (String productName) throws SQLException {
            String sql = "SELECT 1 FROM products WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, productName);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        }

        // Method to delete products by group ID
        public void deleteProductsByGroupId ( int groupId) throws SQLException {
            String sql = "DELETE FROM products WHERE group_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, groupId);
                statement.executeUpdate();
            }
        }

        // Method to get products by group ID
        public List<Product> getProductsByGroupId ( int groupId) throws SQLException {
            List<Product> products = new ArrayList<>();
            String sql = "SELECT * FROM products WHERE group_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, groupId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        Product product = extractProductFromResultSet(resultSet);
                        products.add(product);
                    }
                }
            }
            return products;
        }



        // Method to extract a Product from a ResultSet
        private Product extractProductFromResultSet (ResultSet resultSet) throws SQLException {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String description = resultSet.getString("description");
            String manufacturer = resultSet.getString("manufacturer");
            int quantity = resultSet.getInt("quantity");
            double pricePerUnit = resultSet.getDouble("price_per_unit");
            int groupId = resultSet.getInt("group_id");
            Product product = new Product(name, description, manufacturer, quantity, pricePerUnit);
            product.setId(id);
            product.setGroupId(groupId);
            return product;
        }

        // Method to update a product
        public void updateProduct (Product product) throws SQLException {
            String sql = "UPDATE products SET description = ?, manufacturer = ?, quantity = ?, price_per_unit = ?, group_id = ? WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, product.getDescription());
                statement.setString(2, product.getManufacturer());
                statement.setInt(3, product.getQuantity());
                statement.setDouble(4, product.getPricePerUnit());
                statement.setInt(5, product.getGroupId());
                statement.setString(6, product.getName());
                statement.executeUpdate();
            }
        }

        // Method to delete a product by its name
        public void deleteProduct (String name) throws SQLException {
            String sql = "DELETE FROM products WHERE name = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.executeUpdate();
            }
        }

        // Method to get all products
        public List<Product> getAllProducts () throws SQLException {
            List<Product> products = new ArrayList<>();
            String sql = "SELECT * FROM products";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Product product = extractProductFromResultSet(resultSet);
                    products.add(product);
                }
            }
            return products;
        }
    }
