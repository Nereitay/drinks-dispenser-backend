package es.kiwi.drinksdispenser.infrastructure.persistence.mapper;

import es.kiwi.drinksdispenser.domain.model.CoinType;
import es.kiwi.drinksdispenser.domain.model.Coins;
import es.kiwi.drinksdispenser.infrastructure.persistence.dao.CoinsDAO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CoinsDAOMapper {
    @Mapping(target = "denomination", source = "coinType.denomination")
    CoinsDAO toCoinsDAO(Coins coins);

    @Mapping(target = "coinType", source = "denomination")
    Coins toCoins(CoinsDAO coinsDAO);

    default CoinType map(Double denomination) {
        for (CoinType value : CoinType.values()) {
            if (denomination.equals(value.getDenomination())) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown CoinType with denomination: " + denomination);
    }
}
