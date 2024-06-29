package es.kiwi.drinksdispenser.infrastructure.persistence.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table(name = "machines")
@NoArgsConstructor
@AllArgsConstructor
public class MachinesDAO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String name;

    private String location;

    /**
     * 0 - AVAILABLE, 1 - OUT_OF_ORDER
     */
    private Integer status;

    private String operator;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
