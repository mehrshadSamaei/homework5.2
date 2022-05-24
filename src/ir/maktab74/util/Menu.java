package ir.maktab74.util;


import ir.maktab74.Application;
import ir.maktab74.domain.User;
import ir.maktab74.repopsotory.ProductRepository;

import java.sql.SQLException;

import static ir.maktab74.Application.*;

public class Menu {
    public void showGetUsername() {
        System.out.println("First Name(username):");
    }

    public void showGetLastName() {
        System.out.println("Last Name:");
    }

    public void showGetPassword() {
        System.out.println("password:");
    }

    public void showGetState() {
        System.out.println("State:");
    }

    public void showGetCity() {
        System.out.println("City:");
    }

    public void showGetStreet() {
        System.out.println("Street:");
    }

    public void showGetEmail() {
        System.out.println("Email:");
    }

    public void showGetPhone() {
        System.out.println("Phone Number:");
    }

    public void showGetPostalCode() {
        System.out.println("Postal Code:");
    }

    public void showSuccessfully() {
        System.out.println("==============\nSuccessfully!\n==============");
    }

    public void showFailed() {
        System.out.println("==============\nFailed!\n==============");
    }

    public void showWelcome(User user) {
        System.out.println("Welcome, " + user.getFirstName() + " " + user.getLastName());
        System.out.println("==============");
    }

    public void showSelectProduct() {
        System.out.println("Select your Product:");
    }


    public void showProductsList() throws SQLException, ClassNotFoundException {
        System.out.println("Products:");
        ProductRepository productRepository = new ProductRepository();
        productRepository.showGetProducts();
    }

    public void cartMenu() {
        System.out.println("================\n1.Add\n2.Remove\n3.Purchase\n4.See Products\n5.Your Cart Status\n6.Logout");
    }

    public void firstMenu(User user) throws SQLException, ClassNotFoundException {
        Menu menu = new Menu();
        ApplicationContext context = new ApplicationContext();
        System.out.println("1.Login\n2.Register\n3.Products\n4.Exit");
        int select = context.getIntScanner().nextInt();

        switch (select){

            case 1:
                loginMenu(user);
                break;

            case 2:
                register(menu, context, user);
                context.getUserRepository().insertUser(user);
                int user_id = settingForeignKey(user);
                context.getAddressRepository().insertAddress(user, user_id);
                break;

            case 3:
                context.getProductRepository().showAllProducts();
                System.out.println("System.out.println(==============\n0.Back to Menu");
                select = context.getIntScanner().nextInt();
                if(select == 0)
                    loginMenu(user);

            case 4:
                System.exit(0);
        }
    }

    public void backToSelectInCartMenu(User user, ApplicationContext context, Menu menu, int select, boolean addOrRem, int user_id) throws SQLException, ClassNotFoundException {
        Application.selectInCartMenu(user,context,menu, select, addOrRem, user_id);
    }

    public void showFirstTimeJoin(ApplicationContext context,int user_id,boolean addOrRem) throws SQLException, ClassNotFoundException {
        Menu menu = new Menu();
        User user = new User();
        System.out.println("1.See & Buy Products\n2.Cart Status\n3.Logout");
        int select = context.getIntScanner().nextInt();

        switch (select){
            case 1:
                menu.showProductsList();
                menu.showSelectProduct();
                int product_id = context.getIntScanner().nextInt();
                context.getProductRepository().calculateAvailableProducts(product_id, addOrRem);
                //show carts
                settingCart(context, product_id, user_id);
                //
                selectInCartMenu(user, context, menu, product_id, addOrRem, user_id);

            case 2:
                context.getCartRepository().showCart(select,user_id);
                menu.backToSelectInCartMenu(user,context,menu,select,addOrRem,user_id);
                break;

            case 3:
                firstMenu(user);
                break;
        }

    }

}
