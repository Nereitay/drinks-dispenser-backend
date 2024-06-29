package es.kiwi.drinksdispenser.infrastructure.persistence.mapper;

import es.kiwi.drinksdispenser.domain.model.MachineStatus;
import es.kiwi.drinksdispenser.domain.model.Machines;
import es.kiwi.drinksdispenser.infrastructure.persistence.dao.MachinesDAO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface MachineDAOMapper {

    @Mapping(source = "status", target = "status")
    MachinesDAO machinesToMachinesDAO(Machines machines);

    @Mapping(source = "status", target = "status")
    Machines machinesDAOToMachines(MachinesDAO machinesDAO);

    default Integer map(MachineStatus status) {
        return status != null ? status.getStatus() : null;
    }

    default MachineStatus map(Integer status) {
        if (status == null) {
            return null;
        }
        for (MachineStatus s : MachineStatus.values()) {
            if (s.getStatus() == status) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown MachineStatus with status: " + status);
    }
}
