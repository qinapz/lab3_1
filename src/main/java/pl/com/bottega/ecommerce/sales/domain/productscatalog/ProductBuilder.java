package pl.com.bottega.ecommerce.sales.domain.productscatalog;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sharedkernel.Money;

public class ProductBuilder {

    private Id id = Id.generate();
    private Money price = new Money(50.0);
    private String name = "Test name";
    private ProductType productType = ProductType.FOOD;

    public ProductBuilder withPrice(Money price) {
        this.price = price;
        return this;
    }

    public ProductBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder withProductType(ProductType type) {
        this.productType = type;
        return this;
    }

    public Product build() {
        return new Product(id, price, name, productType);
    }

}
