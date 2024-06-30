package es.kiwi.drinksdispenser.application.mapper;

import es.kiwi.drinksdispenser.application.vo.ProductsOptionVO;
import es.kiwi.drinksdispenser.domain.model.Products;
import org.mapstruct.Mapper;

@Mapper
public interface ProductsOptionVOMapper {

    ProductsOptionVO toProductsOptionVO(Products products);
    Products toProducts(ProductsOptionVO productsOptionVO);
}
