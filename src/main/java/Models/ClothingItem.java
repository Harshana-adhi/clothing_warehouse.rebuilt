package Models;

import java.math.BigDecimal;

public class ClothingItem {
    private String clothId;
    private String material;
    private String category;
    private BigDecimal costPrice;
    private BigDecimal retailPrice;
    private String supplierId;

    //default constructor
    public ClothingItem() {}

    // Constructor
    public ClothingItem(String clothId, String material, String category, BigDecimal costPrice, BigDecimal retailPrice, String supplierId) {
        this.clothId = clothId;
        this.material = material;
        this.category = category;
        this.costPrice = costPrice;
        this.retailPrice = retailPrice;
        this.supplierId = supplierId;
    }


    // Getters and Setters
    public String getClothId() { return clothId; }
    public void setClothId(String clothId) { this.clothId = clothId; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getRetailPrice() { return retailPrice; }
    public void setRetailPrice(BigDecimal retailPrice) { this.retailPrice = retailPrice; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    // Utility method for creating JTable
    public String[] toValuesArray() {
        return new String[]{clothId, material, category,
                costPrice.toString(), retailPrice.toString(), supplierId};
    }
}
