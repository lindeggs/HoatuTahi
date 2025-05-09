package ch.linst.hoatutahi.lwjgl3.components.billing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.linst.hoatutahi.components.billing.service.BillingFlowResponseListener;
import ch.linst.hoatutahi.components.billing.service.BillingService;
import ch.linst.hoatutahi.components.billing.service.BillingServiceStateListener;
import ch.linst.hoatutahi.components.billing.service.ProductDetails;
import ch.linst.hoatutahi.components.billing.service.ProductDetailsResponseListener;
import ch.linst.hoatutahi.components.billing.service.ProductId;
import ch.linst.hoatutahi.components.billing.service.PurchaseDetails;
import ch.linst.hoatutahi.components.billing.service.PurchasesResponseListener;

/**
 * This service is a dummy implementation
 */
public class DesktopBillingService implements BillingService {

    private BillingFlowResponseListener billingFlowResponseListener;

    private Map<ProductId, PurchaseDetails.PurchaseState> itemsPurchaseState = new HashMap<>();

    public DesktopBillingService() {
        itemsPurchaseState.put(ProductId.PLAYGROUND_ITEMSET_1_NUMBERS_2048, PurchaseDetails.PurchaseState.UNDEFINED);
    }

    // BillingService interface overrides (cross platform)

    @Override
    public void connectBillingService(BillingServiceStateListener serviceStateListener, BillingFlowResponseListener flowResponseListener) {
        billingFlowResponseListener = flowResponseListener;
        serviceStateListener.onBillingSetupFinished(true);
    }

    @Override
    public void requestInAppProductDetails(ProductDetailsResponseListener listener) {
        List<ProductDetails> productDetailsList = new ArrayList<>();
        productDetailsList.add(new ProductDetails(ProductId.PLAYGROUND_ITEMSET_1_NUMBERS_2048, "1.00", "CHF"));
        listener.onProductDetailsResponse(true,productDetailsList);
    }

    @Override
    public void requestPurchases(PurchasesResponseListener listener) {
        PurchaseDetails.PurchaseState fidgetSpinnerPurchaseState = itemsPurchaseState.get(ProductId.PLAYGROUND_ITEMSET_1_NUMBERS_2048);

        List<PurchaseDetails> purchaseDetailsList = new ArrayList<>();
        purchaseDetailsList.add(new PurchaseDetails(
                ProductId.PLAYGROUND_ITEMSET_1_NUMBERS_2048,
                fidgetSpinnerPurchaseState,
                fidgetSpinnerPurchaseState == PurchaseDetails.PurchaseState.PURCHASED,
                null));
        listener.onPurchasesResponse(true, purchaseDetailsList);
    }

    @Override
    public boolean launchBillingFlow(ProductId productId) {
        if(!itemsPurchaseState.containsKey(productId)) return false;

        itemsPurchaseState.put(productId, PurchaseDetails.PurchaseState.PURCHASED);

        List<PurchaseDetails> corePurchases = new ArrayList<>();
        PurchaseDetails corePurchase = new PurchaseDetails(productId, PurchaseDetails.PurchaseState.PURCHASED, true, null);
        corePurchases.add(corePurchase);
        billingFlowResponseListener.onBillingFlowResponse(true, corePurchases);
        return true;
    }
}
