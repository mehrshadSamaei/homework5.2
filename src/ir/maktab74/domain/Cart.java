package ir.maktab74.domain;

public class Cart {
    private Product[] products = new Product[5];

    public Product[] getProducts() {

        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }
}