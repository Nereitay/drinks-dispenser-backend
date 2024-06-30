package es.kiwi.drinksdispenser.infrastructure.persistence.mapper;

import es.kiwi.drinksdispenser.domain.model.MachineProducts;
import es.kiwi.drinksdispenser.infrastructure.persistence.dao.MachineProductsDAO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MachineProductsDAOMapper {

    @Mapping(target = "machineId", source = "machine.id")
    @Mapping(target = "productId", source = "product.id")
    MachineProductsDAO toMachineProductsDAO(MachineProducts machineProducts);

    @Mapping(target = "machine.id", source = "machineId")
    @Mapping(target = "product.id", source = "productId")
    MachineProducts toMachineProducts(MachineProductsDAO machineProductsDAO);
}
