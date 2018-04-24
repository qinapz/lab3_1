package pl.com.bottega.ecommerce.sales.domain.invoicing;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

@RunWith(MockitoJUnitRunner.class)

public class BookKeeperTest {

    private BookKeeper bookKeeper;
    private InvoiceFactory invoiceFactory;
    private TaxPolicy mockedTaxPolicy;
    private InvoiceRequest invoiceRequest;
    private ProductData productData;
    private ProductData anotherProductData;

    @Before
    public void setUp() throws Exception {
        bookKeeper = new BookKeeper(new InvoiceFactory());
        mockedTaxPolicy = mock(TaxPolicy.class);
        invoiceRequest = new InvoiceRequest(new ClientData(Id.generate(), "John Doe"));
        Money moneyEUR = new Money(125.0);
        Date snapshotDate = new Date();
        productData = new ProductData(Id.generate(), moneyEUR, "Ziemniak", ProductType.FOOD, snapshotDate);
        anotherProductData = new ProductData(Id.generate(), moneyEUR, "Seler", ProductType.FOOD, snapshotDate);
    }

    @Test
    public void requestingOneInvoiceShouldReturnInvoiceWithOnePosition() {
        when(mockedTaxPolicy.calculateTax(productData.getType(), productData.getPrice()))
                .thenReturn(new Tax(new Money(0.50), "Item Tax"));
        RequestItem requestItem = new RequestItem(productData, 5, productData.getPrice());
        invoiceRequest.add(requestItem);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, mockedTaxPolicy);
        assertThat(invoice.getItems().size(), is(equalTo(1)));
    }

    @Test
    public void invoiceWithTwoItemsShouldUseCalculateTaxMethodTwoTimes() {
        when(mockedTaxPolicy.calculateTax(productData.getType(), productData.getPrice()))
                .thenReturn(new Tax(new Money(0.50), "Item Tax"));
        RequestItem requestItem = new RequestItem(productData, 5, productData.getPrice());
        invoiceRequest.add(requestItem);
        invoiceRequest.add(requestItem);
        bookKeeper.issuance(invoiceRequest, mockedTaxPolicy);
        verify(mockedTaxPolicy, times(2)).calculateTax(productData.getType(), productData.getPrice());
    }

    @Test
    public void invoiceRequestWithTwoItemsShouldReturnInvoiceWithTwoDifferentItems() {
        when(mockedTaxPolicy.calculateTax(productData.getType(), productData.getPrice()))
                .thenReturn(new Tax(new Money(0.50), "Item Tax"));
        when(mockedTaxPolicy.calculateTax(anotherProductData.getType(), anotherProductData.getPrice()))
                .thenReturn(new Tax(new Money(0.75), "Item Tax"));
        RequestItem requestItem = new RequestItem(productData, 5, productData.getPrice());
        invoiceRequest.add(requestItem);
        requestItem = new RequestItem(anotherProductData, 4, anotherProductData.getPrice());
        invoiceRequest.add(requestItem);
        Invoice invoice = bookKeeper.issuance(invoiceRequest, mockedTaxPolicy);
        assertThat(invoice.getItems().get(0).getProduct(), is(not(invoice.getItems().get(1).getProduct())));
    }
}
