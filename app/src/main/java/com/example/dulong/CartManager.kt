package com.example.dulong

object CartManager {
    // Biến lưu trữ danh sách sản phẩm trong giỏ
    private val cartItems = mutableListOf<Product>()

    // Hàm thêm sản phẩm vào giỏ
    fun addProduct(product: Product) {
        cartItems.add(product)
    }

    // Hàm lấy danh sách ra để hiển thị
    fun getCartItems(): List<Product> {
        return cartItems
    }

    // Hàm tính tổng tiền
    fun getTotalPrice(): Double {
        var total = 0.0
        for (item in cartItems) {
            total += item.price
        }
        return total
    }

    // Hàm xóa giỏ hàng (dùng khi checkout xong)
    fun clearCart() {
        cartItems.clear()
    }
}