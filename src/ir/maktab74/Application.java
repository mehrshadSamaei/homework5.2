package ir.maktab74;

import ir.maktab74.domain.User;
import ir.maktab74.util.ApplicationContext;
import ir.maktab74.util.DatabaseUtil;
import ir.maktab74.util.Menu;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Application {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        ApplicationContext context = new ApplicationContext();
        //context.getDatabaseInitializer().init();
        //int select = context.getIntScanner().nextInt();
        Menu menu = new Menu();
        User user = new User();

        while(true)
            menu.firstMenu(user);

    }


    public static int userIdForFK(User user) throws SQLException, ClassNotFoundException {
        DatabaseUtil databaseUtil = new DatabaseUtil();
        Statement statement = databaseUtil.getConnection().createStatement();
        String query = "select * from onlineshop2.user_table order by id";
        ResultSet resultSet = statement.executeQuery(query);

        int user_id = 0;
        while (resultSet.next()) {
            if (user.getUsername() != null) {
                if (user.getUsername().equals(resultSet.getString("username"))) {
                    user_id = resultSet.getInt("id");
                    return user_id;
                }
            }
        }
        return user_id;
    }

    public static void selectInCartMenu(User user, ApplicationContext context, Menu menu, int select, boolean addOrRem, int user_id) throws SQLException, ClassNotFoundException {

        while (true) {

            menu.cartMenu();

            int choose = context.getIntScanner().nextInt();

            if (choose == 1) {

                menu.showSelectProduct();
                select = context.getIntScanner().nextInt();
                context.getProductRepository().calculateAvailableProducts(select, addOrRem);
                context.getCartRepository().insertIntoCart(select, user_id);
                context.getCartRepository().updateCart(select, user_id, false);
            }

            else if (choose == 2) {
                addOrRem = false;
                context.getCartRepository().removeProductFromCart(select, user_id);
                context.getProductRepository().calculateAvailableProducts(select, addOrRem);

            }

            else if (choose == 3) {
                context.getCartRepository().emptyCart(user_id);
                System.out.println("==============\nSettled!");
                menu.backToSelectInCartMenu(user,context,menu,select,addOrRem,user_id);
                System.exit(0);
            }

            else if(choose == 4){
                context.getProductRepository().showAllProducts();
                menu.backToSelectInCartMenu(user,context,menu,select,addOrRem,user_id);
            }

            else if(choose == 5)
                context.getCartRepository().showCart(select,user_id);

            else if (choose == 6)
                menu.firstMenu(user);
        }
    }

    public static void register(Menu menu, ApplicationContext context, User user) throws SQLException, ClassNotFoundException {
        boolean addOrRem = true;
        menu.showGetUsername();
        String username = context.getStringScanner().nextLine();

        menu.showGetLastName();
        String lastName = context.getStringScanner().nextLine();

        menu.showGetPassword();
        String password = context.getStringScanner().nextLine();

        menu.showGetState();
        String state = context.getStringScanner().nextLine();

        menu.showGetCity();
        String city = context.getStringScanner().nextLine();

        menu.showGetStreet();
        String street = context.getStringScanner().nextLine();

        menu.showGetPhone();
        String phoneNumber = context.getStringScanner().nextLine();

        menu.showGetEmail();
        String email = context.getStringScanner().nextLine();

        menu.showGetPostalCode();
        String postalCode = context.getStringScanner().nextLine();

        user.setFirstName(username);
        user.setUsername(username);
        user.setLastName(lastName);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setAddress(1, user.getId(), state, city, street, postalCode);

        context.getUserRepository().insertUser(user);
        int user_id = settingForeignKey(user);
        context.getAddressRepository().insertAddress(user, user_id);
        menu.showSuccessfully();
        menu.showWelcome(user);
        menu.showProductsList();
        menu.showSelectProduct();

        int select = context.getIntScanner().nextInt();
        context.getProductRepository().calculateAvailableProducts(select, addOrRem);

        //show carts
        settingCart(context, select, user_id);
        //
        selectInCartMenu( user, context, menu, select, addOrRem, user_id);

    }

    public static int settingForeignKey(User user) throws SQLException, ClassNotFoundException {
        Application application = new Application();
        int user_id = application.userIdForFK(user);
        return user_id;
    }

    public static void settingCart(ApplicationContext context, int select, int user_id) throws SQLException, ClassNotFoundException {
        context.getCartRepository().insertIntoCart(select, user_id);
        context.getCartRepository().updateCart(select, user_id, true);
    }

    public static void loginMenu(User user) throws SQLException, ClassNotFoundException {
        ApplicationContext context = new ApplicationContext();
        Menu menu = new Menu();

        while (true) {
            System.out.println("username:");
            String username = context.getStringScanner().nextLine();
            System.out.println("password:");
            String password = context.getStringScanner().nextLine();
            String query = "select * from onlineshop2.user_table where username = '" + (username) + "' and password = '" + (password) + "'";
            PreparedStatement preparedStatement = context.getDatabaseUtil().getConnection().prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery(query);
            boolean userIsIn = false;
            boolean addOrRem = true;

            while (resultSet.next()) {
                if (resultSet.getString("username").equals(username) && resultSet.getString("password").equals(password)) {
                    userIsIn = true;
                    menu.showSuccessfully();
                    user.setFirstName(resultSet.getString("first_name"));
                    user.setLastName(resultSet.getString("last_name"));
                    user.setPassword(resultSet.getString("password"));
                    user.setPhoneNumber(resultSet.getString("phone_number"));
                    user.setEmail(resultSet.getString("email"));
                    user.setId(resultSet.getInt("id"));
                    menu.showWelcome(user);
                    int user_id = user.getId();
                    menu.showFirstTimeJoin(context,user_id,addOrRem);

                    int select = context.getIntScanner().nextInt();
                    context.getProductRepository().calculateAvailableProducts(select, addOrRem);
                    //show carts
                    settingCart(context, select, user_id);
                    //
                    selectInCartMenu(user, context, menu, select, addOrRem, user_id);
                }
            }

            if (userIsIn == false)
                System.out.println("Wrong user or password!");
        }
    }
}
