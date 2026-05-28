package com.game.lottery.mapper;

import com.game.lottery.dto.PaymentMethodDto;
import com.game.lottery.model.PaymentMethod;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    PaymentMethodDto toDto(PaymentMethod paymentMethod);
}
