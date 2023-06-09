package com.web.billim.order.domain;

import java.time.LocalDate;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.web.billim.common.domain.JpaEntity;
import com.web.billim.order.vo.Period;
import com.web.billim.product.type.TradeMethod;
import com.web.billim.member.domain.Member;
import com.web.billim.order.type.ProductOrderStatus;
import com.web.billim.order.dto.OrderCommand;
import com.web.billim.product.domain.Product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "product_order")
@Builder
@Getter
public class ProductOrder extends JpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_order_id")
    private Integer orderId;

    @JoinColumn(name = "product_id", referencedColumnName = "product_id")
    @ManyToOne
    private Product product;

    @JoinColumn(name = "member_id", referencedColumnName = "member_id")
    @ManyToOne
    private Member member;

    @Enumerated(EnumType.STRING)
    private TradeMethod tradeMethod;

    @Enumerated(EnumType.STRING)
    private ProductOrderStatus status;

    private LocalDate startAt;

    private LocalDate endAt;

    // VO
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="name", column=@Column(name="buyer_name")),
            @AttributeOverride(name="address", column=@Column(name="buyer_address")),
            @AttributeOverride(name="phone", column=@Column(name="buyer_phone"))
    })
    private ProductBuyer buyer;

    public Period getPeriod() {
        return new Period(startAt, endAt);
    }

    public int getPrice() {
        int productPrice = this.product.getPrice();
        int rentDays = java.time.Period.between(this.startAt, this.endAt).getDays() + 1;
        return productPrice * rentDays;
    }

    public static ProductOrder generateNewOrder(Member member, Product product, OrderCommand command) {
        ProductOrderBuilder order = ProductOrder.builder()
                .product(product)
                .member(member)
                .startAt(command.getStartAt())
                .endAt(command.getEndAt())
                .tradeMethod(command.getTradeMethod())
                .status(ProductOrderStatus.IN_PROGRESS);

        if (command.getTradeMethod().equals(TradeMethod.DELIVERY)) {
            ProductBuyer buyer = new ProductBuyer(command.getName(), command.getAddress(), command.getPhone());
            order.buyer(buyer);
        }
        return order.build();
    }

    public void cancel() {
        this.status = ProductOrderStatus.CANCELED;
    }

    public void complete() {
        this.status = ProductOrderStatus.DONE;
    }
}

