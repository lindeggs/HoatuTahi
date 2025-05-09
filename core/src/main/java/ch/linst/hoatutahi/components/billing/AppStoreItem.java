package ch.linst.hoatutahi.components.billing;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import ch.linst.hoatutahi.components.billing.service.ProductDetails;
import ch.linst.hoatutahi.components.billing.service.ProductId;
import ch.linst.hoatutahi.components.billing.service.PurchaseDetails;

/**
 * Represents an item which can be (or has been) bought in the platform specific store
 */
public class AppStoreItem {

    public static final String PRODUCT_DETAILS_CHANGED_EV_ID = "ProductDetailsChanged";
    public static final String PURCHASE_STATE_CHANGED_EV_ID = "PurchaseStateChanged";

    private BillingManager manager;
    private ProductDetails productDetails;
    private PurchaseDetails.PurchaseState purchaseState = PurchaseDetails.PurchaseState.UNDEFINED;

    private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public AppStoreItem(ProductId productId, BillingManager manager) {
        this.productDetails = new ProductDetails(productId, "...", "...");
        this.manager = manager;
    }

    public void setPropertyChangeListener(PropertyChangeListener listener) {
        for (PropertyChangeListener oldlistener : pcs.getPropertyChangeListeners()){
            pcs.removePropertyChangeListener(oldlistener);
        }
        pcs.addPropertyChangeListener(listener);
    }

    public void setProductDetails(ProductDetails productDetails){
        if(productDetails.getId() != this.productDetails.getId()){
            throw new IllegalArgumentException("Wrong product id (does not match original id");
        }

        ProductDetails oldProductDetails = this.productDetails;
        this.productDetails = productDetails;
        pcs.firePropertyChange(PRODUCT_DETAILS_CHANGED_EV_ID, oldProductDetails, productDetails);

    }

    public ProductDetails getProductDetails() {
        return productDetails;
    }

    public void setPurchaseState(PurchaseDetails.PurchaseState purchaseState) {
        if(this.purchaseState != purchaseState){
            PurchaseDetails.PurchaseState oldPurchaseState = this.purchaseState;
            this.purchaseState = purchaseState;
            pcs.firePropertyChange(PURCHASE_STATE_CHANGED_EV_ID, oldPurchaseState, purchaseState);
        }
    }

    public PurchaseDetails.PurchaseState getPurchaseState() {
        return purchaseState;
    }

    public boolean isUnlocked(){
        if((purchaseState == PurchaseDetails.PurchaseState.PURCHASED)
                || (purchaseState == PurchaseDetails.PurchaseState.GRANTED_FOR_FREE)){
            return true;
        }
        return false;
    }

    public boolean launchBillingWorkflow(){
        return manager.launchBillingWorkflow(productDetails.getId());
    }
}
