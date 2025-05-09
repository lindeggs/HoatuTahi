package ch.linst.hoatutahi.components.billing.service;

import java.util.List;

/**
 * The ProductDetailsResponseListener asynchronously receives via onProductDetailsResponse the
 * product details for each buy-able product
 */
public interface ProductDetailsResponseListener {

    void onProductDetailsResponse(boolean success, List<ProductDetails> productDetailsList);
}
