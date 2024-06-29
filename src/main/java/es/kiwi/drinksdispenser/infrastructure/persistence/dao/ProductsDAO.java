package es.kiwi.drinksdispenser.infrastructure.persistence.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;

@Data
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
public class ProductsDAO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String name;

    private Double price;

    private String type;

}
