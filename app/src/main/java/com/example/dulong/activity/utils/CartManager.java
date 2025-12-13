package com.example.dulong.activity.utils;

import com.example.dulong.model.Product;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    // List lưu trữ sản phẩm trong giỏ (Private để chỉ quản lý qua các hàm bên dưới)
    private static final List<Product> cartItems = new ArrayList<>();

    // Lấy danh sách sản phẩm hiện tại
    public static List<Product> getCartItems() {
        return new ArrayList<>(cartItems);
    }

    // Thêm sản phẩm vào giỏ
    // Logic: Nếu sản phẩm đã có (trùng ID) thì tăng số lượng, ngược lại thêm mới
    public static void addToCart(Product product) {
        Product existingItem = null;
        for (Product item : cartItems) {
            if (item.get_id().equals(product.get_id())) {
                existingItem = item;
                break;
            }
        }
        
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + 1);
        } else {
            product.setQuantity(1); // Đảm bảo khi thêm mới số lượng là 1
            cartItems.add(product);
        }
    }

    // Xóa toàn bộ giỏ hàng (Khi đặt hàng thành công hoặc User muốn xóa)
    public static void clearCart() {
        cartItems.clear();
    }

    // Xóa một sản phẩm cụ thể khỏi giỏ (Dùng khi số lượng giảm về 0)
    public static void removeFromCart(Product product) {
        cartItems.remove(product);
    }

    // TÍNH TỔNG TIỀN: (Giá * Số lượng) cho tất cả sản phẩm
    public static double getTotalPrice() {
        double total = 0.0;
        for (Product item : cartItems) {
            total += (item.getPrice() * item.getQuantity());
        }
        return total;
    }
}