package es.kiwi.drinksdispenser.infrastructure.persistence.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table(name = "machine_products")
public class MachineProductsDAO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private Long machineId;

    private Long productId;

    private Integer stock;

    private LocalDate expirationDate;

    private String operator;

    private LocalDateTime updatedAt;

}
