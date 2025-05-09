package ch.linst.hoatutahi.components.billing;

import com.badlogic.gdx.Gdx;

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
 * The billing manager is responsible for the following functionality:
 * - Creates and caches a stateful object for each buy-able product
 */
public class BillingManager implements BillingFlowResponseListener {

    private BillingService billingService;
    private boolean billingServiceConnected = false;

    private Map<ProductId, AppStoreItem> appStoreItemMap = new HashMap<>();

    public BillingManager(BillingService billingServiceArg) {
        billingService = billingServiceArg;

        for(ProductId id: ProductId.values()){
            appStoreItemMap.put(id, new AppStoreItem(id, this));
        }
        // Set the PLAYGROUND_ITEMSET_BALLS purchase state to GRANTED_FOR_FREE, as this set is basic content
        appStoreItemMap.get(ProductId.PLAYGROUND_ITEMSET_0_BALLS).setPurchaseState(PurchaseDetails.PurchaseState.GRANTED_FOR_FREE);
    }

    /**
     * Connects to billing service and refreshes all store items upon success
     */
    public void connectToBillingService(){
        billingService.connectBillingService(new BillingServiceStateListener() {
            @Override
            public void onBillingSetupFinished(boolean success) {
                billingServiceConnected = success;
                updateProductDetails();
                updateProductPurchases();
            }

            @Override
            public void onBillingServiceDisconnected() {
                billingServiceConnected = false;
            }
        }, this);
    }

    public void updateProductPurchases(){
        if(billingServiceConnected){
            billingService.requestPurchases(new PurchasesResponseListener() {
                @Override
                public void onPurchasesResponse(boolean success, List<PurchaseDetails> purchaseList) {
                    Gdx.app.debug("HoatuTahi-core", String.format("onPurchasesResponse: success = %s   purchaseList.size = %s", success, purchaseList.size()));
                    if(success){
                        for(PurchaseDetails details : purchaseList){
                            if(appStoreItemMap.containsKey(details.getId())){
                                AppStoreItem item = appStoreItemMap.get(details.getId());
                                item.setPurchaseState(details.getPurchaseState());
                                Gdx.app.debug("HoatuTahi-core", String.format(
                                        "onPurchasesResponse: PurchaseDetails Id = %s    State = %s    Acknowledged = %s",
                                        details.getId(), details.getPurchaseState(), details.isAknowledged()));
                                acknowledgePurchase(details);
                            }
                        }
                    }
                    else{
                        Gdx.app.debug("HoatuTahi-core", "onPurchasesResponse failed");
                    }
                }
            });
        }
        else{
            Gdx.app.log("HoatuTahi-core", "Can update purchases because billing service is disconnected");
        }


    }


    public AppStoreItem getAppStoreItem(ProductId id){
        if(!appStoreItemMap.containsKey(id)){
            throw new IllegalArgumentException("AppStoreItem id " + id.toString() + " does not exist");
        }
        return appStoreItemMap.get(id);
    }


    public boolean launchBillingWorkflow(ProductId id){
        if(!appStoreItemMap.containsKey(id)){
            throw new IllegalArgumentException("AppStoreItem id " + id.toString() + " does not exist");
        }

        AppStoreItem item = appStoreItemMap.get(id);
        if(item.isUnlocked()) return true; // Item doesn't have to be bought, its already unlocked

        return billingService.launchBillingFlow(id);
    }


    private void updateProductDetails(){
        billingService.requestInAppProductDetails(new ProductDetailsResponseListener() {
            @Override
            public void onProductDetailsResponse(boolean success, List<ProductDetails> productDetailsList) {
                if (success) {
                    for (ProductDetails details : productDetailsList) {
                        if (appStoreItemMap.containsKey(details.getId())) {
                            AppStoreItem item = appStoreItemMap.get(details.getId());
                            item.setProductDetails(details);
                        }
                    }
                } else {
                    Gdx.app.debug("HoatuTahi-core", "onProductDetailsResponse failed");
                }
            }
        });
    }

    @Override
    public void onBillingFlowResponse(boolean success, List<PurchaseDetails> purchaseList) {
        if(success){
            for(PurchaseDetails details : purchaseList){
                if(appStoreItemMap.containsKey(details.getId())){
                    AppStoreItem item = appStoreItemMap.get(details.getId());
                    item.setPurchaseState(details.getPurchaseState());
                    Gdx.app.debug("HoatuTahi-core", String.format(
                            "onBillingFlowResponse: PurchaseDetails Id = %s    State = %s    Acknowledged = %s",
                            details.getId(), details.getPurchaseState(), details.isAknowledged()));
                    acknowledgePurchase(details);
                }
            }
        }
        else{
            Gdx.app.debug("HoatuTahi-core", "onBillingFlowResponse: Billing failed");
        }
    }

    private void acknowledgePurchase(PurchaseDetails purchaseDetails){
        if(!purchaseDetails.isAknowledged()){
            purchaseDetails.acknowledgePurchase();
        }
    }
}
