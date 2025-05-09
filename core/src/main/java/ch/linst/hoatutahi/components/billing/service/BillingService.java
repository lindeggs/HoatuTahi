package ch.linst.hoatutahi.components.billing.service;

/**
 * Provides an interface which covers the needed functionality to query and buy in app products
 */
public interface BillingService {

    /**
     * Requests to connect the billing service
     * @param serviceStateListener
     * @param flowResponseListener
     */
    void connectBillingService(BillingServiceStateListener serviceStateListener, BillingFlowResponseListener flowResponseListener);

    /**
     * Requests product details for each buy-able product
     * @param listener
     */
    void requestInAppProductDetails(ProductDetailsResponseListener listener);

    /**
     * Requests purchases for each buy-able product
     * @param listener
     */
    void requestPurchases(PurchasesResponseListener listener);

    /**
     * Launches the billing flow in order to buy a specific product
     * @param productId
     * @return true if the billing succeed
     */
    boolean launchBillingFlow(ProductId productId);
}
