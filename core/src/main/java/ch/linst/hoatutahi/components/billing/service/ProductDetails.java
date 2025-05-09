package ch.linst.hoatutahi.components.billing.service;

import ch.linst.hoatutahi.components.billing.AppStoreItem;

/**
 * Provides detail information for a single buy-able product
 * Instances of this class are provided for each buy-able product in response to
 * a call to BillingService.requestInAppProductDetails(...)
 */
public class ProductDetails {
    private ProductId id;
    private String price;
    private String currency;

    public ProductDetails(ProductId id, String price, String currency) {
        this.id = id;
        this.price = price;
        this.currency = currency;
    }

    public String getPrice(){
        return price;
    }

    public String getCurrency(){
        return currency;
    }

    public ProductId getId() {
        return id;
    }
}
