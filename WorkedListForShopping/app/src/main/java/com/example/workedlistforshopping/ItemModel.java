package com.example.workedlistforshopping;
public class ItemModel {
    private String name;
    private boolean isPurchased;

    public ItemModel(String name, boolean isPurchased) {
        this.name = name;
        this.isPurchased = isPurchased;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPurchased() {
        return isPurchased;
    }

    public void setPurchased(boolean purchased) {
        isPurchased = purchased;
    }
}
