package es.kiwi.drinksdispenser.application.mapper;

import es.kiwi.drinksdispenser.application.vo.CoinsVO;
import es.kiwi.drinksdispenser.domain.model.Coins;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface CoinsVOMapper {

    CoinsVO toCoinsVO(Coins coins);
    Coins toCoins(CoinsVO coinsVO);
    List<CoinsVO> toCoinsVOList(List<Coins> coinsList);
}
