package cz.muni.fi.pa165.currency;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import static org.junit.Assert.*;



public class CurrencyConvertorImplTest {

    @Mock
    ExchangeRateTable table;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testConvert() throws ExternalServiceFailureException {
        // Don't forget to test border values and proper rounding.
        CurrencyConvertor convertor = new CurrencyConvertorImpl(table);
        Currency usd = Currency.getInstance(Locale.US);
        Currency eur = Currency.getInstance("EUR");
        BigDecimal amount = new BigDecimal("1.0");

        assertEquals(convertor.convert(usd, eur, amount),
                Math.round(100 * table.getExchangeRate(usd, eur).floatValue() * amount.doubleValue())/100);

        amount = new BigDecimal("14.32");

        assertEquals(convertor.convert(usd, eur, amount),
                Math.round(100 * table.getExchangeRate(usd, eur).floatValue() * amount.doubleValue())/100);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertWithNullSourceCurrency() {
        CurrencyConvertor convertor = new CurrencyConvertorImpl(table);
        Currency eur = Currency.getInstance("EUR");
        BigDecimal amount = new BigDecimal("1.0");
        BigDecimal aux = convertor.convert(null, eur, amount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertWithNullTargetCurrency() {
        CurrencyConvertor convertor = new CurrencyConvertorImpl(table);
        Currency eur = Currency.getInstance("EUR");
        BigDecimal amount = new BigDecimal("1.0");
        BigDecimal aux = convertor.convert(eur, null, amount);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertWithNullSourceAmount() {
        CurrencyConvertor convertor = new CurrencyConvertorImpl(table);
        Currency eur = Currency.getInstance("EUR");
        Currency usd = Currency.getInstance(Locale.US);
        convertor.convert(usd, eur, null);
    }

    @Test(expected = UnknownExchangeRateException.class)
    public void testConvertWithUnknownCurrency() {
        CurrencyConvertor convertor = new CurrencyConvertorImpl(table);
        Currency gib = Currency.getInstance("HKD");
        Currency usd = Currency.getInstance(Locale.US);
        BigDecimal amount = new BigDecimal("1.0");
        convertor.convert(usd, gib, amount);
    }

    @Test(expected = ExternalServiceFailureException.class)
    public void testConvertWithExternalServiceFailure() {
        CurrencyConvertor convertor = new CurrencyConvertorImpl(table);
        Currency gib = Currency.getInstance("bluaaarrrghhh");
        Currency usd = Currency.getInstance(Locale.US);
        BigDecimal amount = new BigDecimal("1.0");
        convertor.convert(usd, gib, amount);
    }

}
