package Models;

import java.math.BigDecimal;

public class ClothingItem {
    private String clothId;
    private String color;
    private String material;
    private String category;
    private BigDecimal price;
    private String supplierId;

    // Full Constructor
    public ClothingItem(String clothId, String color, String material, String category, BigDecimal price, String supplierId) {
        this.clothId = clothId;
        this.color = color;
        this.material = material;
        this.category = category;
        this.price = price;
        this.supplierId = supplierId;
    }

    // Default Constructor (Optional, for utility)
    public ClothingItem() {}

    // Getters and Setters
    public String getClothId() { return clothId; }
    public void setClothId(String clothId) { this.clothId = clothId; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getSupplierId() { return supplierId; }
    public void setSupplierId(String supplierId) { this.supplierId = supplierId; }

    // Utility method for JTable conversion (if needed)
    public String[] toValuesArray() {
        return new String[]{clothId, color, material, category, price.toString(), supplierId};
    }
}