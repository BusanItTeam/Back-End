package com.shop.backend.models;

//주문상태
public enum OrderStatus {
    PENDING("입금대기중"),
    PAID("결제완료"),
    SHIPPED("배송완료"),
    CANCELLED("취소됨"),
    READY_FOR_SHIPPING("배송준비중");

    private final String description;

    // 생성자
    OrderStatus(String description) {
        this.description = description;
    }

    // 상태에 대한 설명을 반환하는 메서드
    public String getDescription() {
        return description;
    }
}
