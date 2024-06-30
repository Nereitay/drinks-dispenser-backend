package es.kiwi.drinksdispenser.infrastructure.persistence.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table(name = "coins")
@NoArgsConstructor
@AllArgsConstructor
public class CoinsDAO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private Long machineId;

    private Double denomination;

    private Integer quantity;

    private LocalDateTime updatedAt;

}
