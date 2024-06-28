package es.kiwi.drinksdispenser.application.mapper;

import es.kiwi.drinksdispenser.application.dto.MachineProductsDTO;
import es.kiwi.drinksdispenser.domain.model.MachineProducts;
import es.kiwi.drinksdispenser.domain.model.Products;
import es.kiwi.drinksdispenser.domain.model.ProductsOption;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface MachineProductsDTOMapper {
    @Mapping(target = "machine.id", source = "machineId")
    @Mapping(target = "stock", source = "quantity")
    @Mapping(target = "product.productsOption", source = "product")
    MachineProducts toMachineProducts(MachineProductsDTO machineProductsDTO);

    List<MachineProducts> toMachineProductsList(List<MachineProductsDTO> machineProductsDTOList);

    MachineProductsDTO toMachineProductsDTO(MachineProducts machineProducts);

    @Named("productsOptionToString")
    static String productsOptionToString(ProductsOption productsOption) {
        return productsOption.getName();
    }

    @Named("stringToProductsOption")
    static ProductsOption stringToProductsOption(String name) {
        for (ProductsOption option : ProductsOption.values()) {
            if (option.getName().equals(name)) {
                return option;
            }
        }
        throw new IllegalArgumentException("Unknown ProductsOption name: " + name);
    }

    default ProductsOption map(Products products) {
        return products.getProductsOption();
    }

    default Products map(ProductsOption productsOption) {
        Products products = new Products();
        products.setProductsOption(productsOption);
        return products;
    }
}
