package com.example.dulong.activity.utils

import com.example.dulong.model.Product

object CartManager {
    // List lưu trữ sản phẩm trong giỏ (Private để chỉ quản lý qua các hàm bên dưới)
    private val cartItems = mutableListOf<Product>()

    // Lấy danh sách sản phẩm hiện tại
    fun getCartItems(): List<Product> {
        return cartItems
    }

    // Thêm sản phẩm vào giỏ
    // Logic: Nếu sản phẩm đã có (trùng ID) thì tăng số lượng, ngược lại thêm mới
    fun addToCart(product: Product) {
        val existingItem = cartItems.find { it._id == product._id }
        if (existingItem != null) {
            existingItem.quantity += 1
        } else {
            product.quantity = 1 // Đảm bảo khi thêm mới số lượng là 1
            cartItems.add(product)
        }
    }

    // Xóa toàn bộ giỏ hàng (Khi đặt hàng thành công hoặc User muốn xóa)
    fun clearCart() {
        cartItems.clear()
    }

    // Xóa một sản phẩm cụ thể khỏi giỏ (Dùng khi số lượng giảm về 0)
    fun removeFromCart(product: Product) {
        cartItems.remove(product)
    }

    // TÍNH TỔNG TIỀN: (Giá * Số lượng) cho tất cả sản phẩm
    fun getTotalPrice(): Double {
        var total = 0.0
        for (item in cartItems) {
            total += (item.price * item.quantity)
        }
        return total
    }
}