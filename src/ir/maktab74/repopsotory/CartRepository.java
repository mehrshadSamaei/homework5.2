package ir.maktab74.repopsotory;


import ir.maktab74.domain.Cart;
import ir.maktab74.util.ApplicationContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CartRepository {

    private Cart cart;
    private ResultSet resultSet;
    private Connection connection;

    public CartRepository(Connection connection) {
        this.connection = connection;
    }

    public void insertIntoCart(int product_id, int user_id) throws SQLException, ClassNotFoundException {
        ApplicationContext context = new ApplicationContext();
        // find rows with same user_id and product_id
        int sameRows = 0;
        String findRowsQuery = "select * from onlineshop2.cart where product_id = ? and user_id = ?";
        PreparedStatement preparedStatement = context.getDatabaseUtil().getConnection().prepareStatement(findRowsQuery);
        preparedStatement.setInt(1, product_id);
        preparedStatement.setInt(2, user_id);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            if (resultSet.getInt("product_id") == product_id
                    && resultSet.getInt("user_id") == user_id) {
                sameRows++;
                break;
            }
        }

        if (sameRows == 0) {
            String query = "insert into onlineshop2.cart(product_id,numbers,user_id) values(?,?,?)";

            preparedStatement = context.getDatabaseUtil().getConnection().prepareStatement(query);
            preparedStatement.setInt(1, product_id);
            preparedStatement.setInt(2, 0);
            preparedStatement.setInt(3, user_id);
            preparedStatement.executeUpdate();

        }
    }

    public void updateCart(int product_id, int user_id, boolean firstTime) throws SQLException, ClassNotFoundException {
        int productsInCart = 1;
        ApplicationContext context = new ApplicationContext();
        String selectNumbers = "select numbers from onlineshop2.cart where product_id = ? and user_id = ?";
        PreparedStatement preparedStatement = context.getDatabaseUtil().getConnection().prepareStatement(selectNumbers);
        preparedStatement.setInt(1, product_id);
        preparedStatement.setInt(2, user_id);
        ResultSet resultSet = preparedStatement.executeQuery();


        while (resultSet.next()) {
            productsInCart = resultSet.getInt("numbers") + 1;
            //System.out.println("productsInCart: "+productsInCart);
            String checkProductNumberQuery = "select sum(numbers) as check_product_number from onlineshop2.cart where user_id = ?";
            int checkProductNumber = 0;
            preparedStatement = context.getDatabaseUtil().getConnection().prepareStatement(checkProductNumberQuery);
            preparedStatement.setInt(1,user_id);
            ResultSet resultSet1 = preparedStatement.executeQuery();
            while (resultSet1.next()){
                checkProductNumber += resultSet1.getInt("check_product_number");
            }

            String addProductNumber = "update onlineshop2.cart set numbers = ? where product_id = ? and user_id = ?";
            preparedStatement = context.getDatabaseUtil().getConnection().prepareStatement(addProductNumber);

            if(checkProductNumber < 5)
                preparedStatement.setInt(1, productsInCart);

            else if(checkProductNumber >= 5){
                System.out.println("================\nYou can't buy more than 5 Products!");
                preparedStatement.setInt(1, productsInCart - 1);
            }

            preparedStatement.setInt(2, product_id);
            preparedStatement.setInt(3, user_id);
            preparedStatement.executeUpdate();
        }


        showCart(product_id, user_id);
    }

    public void showCart(int product_id, int user_id) throws SQLException, ClassNotFoundException {
        System.out.println("==============");
        System.out.println("Your Cart:");

        ApplicationContext context = new ApplicationContext();
        String query = "select p.title,c.numbers,u.id from onlineshop2.cart as c " +
                "join onlineshop2.product as p on c.product_id = p.id " +
                "join onlineshop2.user_table as u on c.user_id = u.id";

        PreparedStatement preparedStatement = context.getDatabaseUtil().getConnection().prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            if (resultSet.getInt("id") == user_id) {
                System.out.print(resultSet.getString("title") + "\t");
                System.out.println("Quantity: " + resultSet.getInt("numbers") + "\t");
            }

        }
        totalPrice(product_id, user_id);

    }

    public void removeProductFromCart(int product_id, int user_id) throws SQLException, ClassNotFoundException {
        ApplicationContext context = new ApplicationContext();
        System.out.println("Select product to remove from Cart:");
        int productId = context.getStringScanner().nextInt();
        String quer = "select * from onlineshop2.cart where product_id = ? and user_id = ?";
        PreparedStatement preparedStatement = context.getDatabaseUtil().getConnection().prepareStatement(quer);
        preparedStatement.setInt(1, productId);
        preparedStatement.setInt(2, user_id);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            if (resultSet.getInt("numbers") == 1) {
                String query = "delete from onlineshop2.cart where product_id = ? and user_id = ?";
                preparedStatement = context.getDatabaseUtil().getConnection().prepareStatement(query);
                preparedStatement.setInt(1, productId);
                preparedStatement.setInt(2, user_id);
                preparedStatement.execute();
                break;
            } else {
                int newNumber = resultSet.getInt("numbers") - 1;
                String query = "update onlineshop2.cart set numbers = ? where product_id = ? and user_id = ?";
                preparedStatement = context.getDatabaseUtil().getConnection().prepareStatement(query);
                preparedStatement.setInt(1, newNumber);
                preparedStatement.setInt(2, productId);
                preparedStatement.setInt(3, user_id);
                preparedStatement.executeUpdate();
                break;
            }
        }
        showCart(product_id, user_id);
    }

    public void emptyCart(int user_id) throws SQLException, ClassNotFoundException {
        ApplicationContext context = new ApplicationContext();
        String query = "delete from onlineshop2.cart where user_id = ?";
        PreparedStatement preparedStatement = context.getDatabaseUtil().getConnection().prepareStatement(query);
        preparedStatement.setInt(1, user_id);
        preparedStatement.execute();
    }

    public void totalPrice(int product_id, int user_id) throws SQLException, ClassNotFoundException {
        ApplicationContext context = new ApplicationContext();
        int totalPrice = 0;

        String query = "select c.numbers*p.price as total_price_each from onlineshop2.cart as c " +
                "join product as p " +
                "on c.product_id = p.id " +
                "join user_table as u " +
                "on c.user_id = u.id where u.id = ?";

        PreparedStatement preparedStatement = context.getDatabaseUtil().getConnection().prepareStatement(query);

        preparedStatement.setInt(1, user_id);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            totalPrice += resultSet.getInt("total_price_each");
        }
        System.out.println("Total Price: " + totalPrice);
    }


}

