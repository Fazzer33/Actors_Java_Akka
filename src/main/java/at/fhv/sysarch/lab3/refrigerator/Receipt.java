package at.fhv.sysarch.lab3.refrigerator;

import akka.japi.Pair;

import java.util.List;

public class Receipt {
    private double totalSum = 0;
    private List<Pair<Product, Integer>> products;

    public Receipt(List<Pair<Product, Integer>> products) {
        this.products = products;
        this.totalSum = calculateTotalSum(this.products);
    }

    public double calculateTotalSum(List<Pair<Product, Integer>> products) {
        double sum = 0;
        for(Pair<Product, Integer> product : products) {
            sum += (product.first().getPrice() * product.second());
        }
        return sum;
    }

    public void printAllPrizes() {
        System.out.println("Receipt:");
        System.out.println("*******************");
        for(Pair<Product, Integer> product : products) {
            ProductType type = product.first().getType();
            int amount = product.second();
            double price = (product.first().getPrice() * product.second());
            System.out.println(type + " * " +amount + " = " +price +" €");
        }
        System.out.println("--------------------");
        System.out.println("Total price: "+totalSum +" €");
        System.out.println("*******************");
    }
}
