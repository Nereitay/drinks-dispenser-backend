package es.kiwi.drinksdispenser.infrastructure.persistence.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table(name = "coins")
public class CoinsDAO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private Long machineId;

    private String denomination;

    private Double value;

    private Integer quantity;

    private String operator;

    private LocalDateTime updatedAt;

}
