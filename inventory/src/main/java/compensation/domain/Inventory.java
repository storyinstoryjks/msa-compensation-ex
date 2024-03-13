package compensation.domain;

import compensation.InventoryApplication;
import compensation.domain.OutOfStock;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Inventory_table")
@Data
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long stock;

    public static InventoryRepository repository() {
        InventoryRepository inventoryRepository = InventoryApplication.applicationContext.getBean(
            InventoryRepository.class
        );
        return inventoryRepository;
    }

    public static void decreaseStock(OrderPlaced orderPlaced) {

        repository().findById(Long.valueOf(orderPlaced.getProductId())).ifPresent(inventory->{
            if(inventory.getStock() >=orderPlaced.getQty()){
                inventory.setStock(inventory.getStock() - orderPlaced.getQty()); 
                repository().save(inventory);
            }else{
                OutOfStock outOfStock = new OutOfStock();
                outOfStock.setOrderId(orderPlaced.getId()); 
                outOfStock.publishAfterCommit();
            }
            
        });
        
    }

    public static void increaseStock(OrderCancelled orderCancelled) {
        
        repository().findById(Long.valueOf(orderCancelled.getProductId())).ifPresent(inventory->{
            
            inventory.setStock(inventory.getStock() + orderCancelled.getQty()); 
            repository().save(inventory);
        });

    }
}
