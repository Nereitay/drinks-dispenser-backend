package es.kiwi.drinksdispenser.infrastructure.persistence.mapper;

import es.kiwi.drinksdispenser.domain.model.Products;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import es.kiwi.drinksdispenser.infrastructure.persistence.dao.ProductsDAO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProductsDAOMapper {


    @Mapping(target = "name", source = "productsOption.name")
    ProductsDAO productsToProductsDAO(Products products);
    @Mapping(target = "productsOption", source = "name")
    Products productsDAOToProducts(ProductsDAO productsDAO);

    default ProductsOption map(String name) {
        if (name == null) return null;
        for (ProductsOption productsOption : ProductsOption.values()) {
            if (productsOption.getName().equals(name)) {
                return productsOption;
            }
        }
        throw new IllegalArgumentException("Unknown ProductsOption with name: " + name);
    }



}
