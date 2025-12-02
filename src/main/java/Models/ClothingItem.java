package Models;

import java.math.BigDecimal;

public class ClothingItem {
    private String clothId;
    // REMOVED: private String color;
    private String material;
    private String category;
    private BigDecimal price;
    private String supplierId;

    // Constructor without color
    public ClothingItem(String clothId, String material, String category, BigDecimal price, String supplierId) {
        this.clothId = clothId;
        this.material = material;
        this.category = category;
        this.price = price;
        this.supplierId = supplierId;
    }

    public ClothingItem() {}

    // Getters and Setters
    public String getClothId() { return clothId; }
    public void setClothId(String clothId) { this.clothId = clothId; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    // Utility method for JTable conversion
    public String[] toValuesArray() {
        return new String[]{clothId, material, category, price.toString(), supplierId};
    }
}