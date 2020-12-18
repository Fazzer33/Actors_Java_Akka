package at.fhv.sysarch.lab3.refrigerator;

public class Product {
    private ProductType type;
    private double weight = 0;
    private double price = 0;

    public Product(ProductType type, double price, double weight) {
        this.type = type;
        this.weight = weight;
        this.price = price;
    }

    public Product(ProductType type) {
        this.type = type;

        if (this.type == ProductType.APPLE) {
            this.weight = 0.3;
            this.price = 0.30;
        }

        if (this.type == ProductType.MILK) {
            this.weight = 1;
            this.price = 0.80;
        }

        if (this.type == ProductType.HAM) {
            this.weight = 0.20;
            this.price = 3.00;
        }

        if (this.type == ProductType.EGGS) {
            this.weight = 0.1;
            this.price = 0.2;
        }

        if (this.type == ProductType.COKE) {
            this.weight = 1;
            this.price = 1.50;
        }

        if (this.type == ProductType.BEER) {
            this.weight = 0.5;
            this.price = 0.80;
        }
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
