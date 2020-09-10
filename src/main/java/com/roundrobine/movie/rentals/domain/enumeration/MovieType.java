package com.roundrobine.movie.rentals.domain.enumeration;

import java.math.BigDecimal;

/**
 * The MovieType enumeration.
 */
public enum MovieType {
    NEW_RELEASE(1, Constants.PREMIUM_PRICE, Currency.SEK, 2),
    REGULAR_FILM(3, Constants.BASIC_PRICE, Currency.SEK, 1),
    OLD_FILM(5, Constants.BASIC_PRICE, Currency.SEK, 1);

    private static class Constants {
        private static final BigDecimal BASIC_PRICE = new BigDecimal(30);
        private static final BigDecimal PREMIUM_PRICE = new BigDecimal(40);
    }

    private int includedRentalDays;
    private BigDecimal price;
    private Currency currency;
    private int bonusPoints;


    MovieType(int includedRentalDays, BigDecimal price, Currency currency, int bonusPoints) {
        this.includedRentalDays = includedRentalDays;
        this.price = price;
        this.currency = currency;
        this.bonusPoints = bonusPoints;
    }

    public BigDecimal calculateMoviePriceOnRental(int plannedRentalDays) {
        BigDecimal total = this.price;
        if (plannedRentalDays > includedRentalDays) {
            total = total.add(this.price.multiply(new BigDecimal(plannedRentalDays - includedRentalDays)));
        }

        return total;
    }

    public BigDecimal calculateSurchargesOnMovieReturn(int plannedRentalDays, int actualRentalDays) {
        BigDecimal surcharges = new BigDecimal(0);
        if (actualRentalDays <= plannedRentalDays){
            return surcharges;
        }
        else {
            if(actualRentalDays <= includedRentalDays){
                return surcharges;
            }
            return surcharges.add(this.price.multiply(new BigDecimal(actualRentalDays - plannedRentalDays )));
        }
    }

    public int getBonusPoints() {
        return bonusPoints;
    }

    public BigDecimal getPrice(){
        return price;
    }
}
