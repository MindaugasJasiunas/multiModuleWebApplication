package com.example.demo.converters;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.RoundingQueryBuilder;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Converter
public class MonetaryAmountConverter implements AttributeConverter<MonetaryAmount, BigDecimal> {

    @Override
    public BigDecimal convertToDatabaseColumn(MonetaryAmount attribute){
        BigDecimal bigDecimal= BigDecimal.valueOf(attribute.getNumber().doubleValueExact());
        bigDecimal= bigDecimal.setScale(2, RoundingMode.HALF_EVEN);

        return bigDecimal;
    }

    @Override
    public MonetaryAmount convertToEntityAttribute(BigDecimal dbData){
        BigDecimal bigDecimal= dbData;
        bigDecimal= bigDecimal.setScale(2, RoundingMode.HALF_EVEN);

        CurrencyUnit euro = Monetary.getCurrency("EUR");
        MonetaryAmount monetaryAmount = Monetary.getDefaultAmountFactory().setCurrency(euro).setNumber( bigDecimal.doubleValue() ).create();
        monetaryAmount= monetaryAmount.with(Monetary.getDefaultRounding()); // 2 decimals
        return monetaryAmount;
    }

}