package compensation.domain;

import compensation.OrderApplication;
import compensation.domain.OrderCancelled;
import compensation.domain.OrderPlaced;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Order_table")
@Data
//<<< DDD / Aggregate Root
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String productId;

    private Integer qty;

    private String customerId;

    private Double amount;

    private String status;

    private String address;

    @PostPersist
    public void onPostPersist() {
        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publishAfterCommit();
    }

    // @PrePersist
    // public void onPrePersist() {
    //     // Get request from Inventory
    //     compensation.external.Inventory inventory =
    //        OrderApplication.applicationContext.getBean(compensation.external.InventoryService.class)
    //        .getInventory(Long.valueOf(getProductId()));

    //     if(inventory.getStock() < getQty())
    //         throw new RuntimeException("Out of stock!");

    // }

    @PreRemove
    public void onPreRemove() {
        OrderCancelled orderCancelled = new OrderCancelled(this);
        orderCancelled.publishAfterCommit();
    }

    public static OrderRepository repository() {
        OrderRepository orderRepository = OrderApplication.applicationContext.getBean(
            OrderRepository.class
        );
        return orderRepository;
    }

    //<<< Clean Arch / Port Method
    public static void updateStatus(OutOfStock outOfStock) {
        repository().findById(outOfStock.getOrderId()).ifPresent(order ->{
            
            order.setStatus("Out Of Stock");
            repository().save(order);
        });

    }

}
