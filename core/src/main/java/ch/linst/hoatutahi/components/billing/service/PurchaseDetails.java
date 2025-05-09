package ch.linst.hoatutahi.components.billing.service;

public class PurchaseDetails {

    public enum PurchaseState{
        UNDEFINED,
        PENDING,
        PURCHASED,
        GRANTED_FOR_FREE
    }

    private ProductId id;
    private PurchaseState purchaseState;
    private boolean isAknowledged;
    AcknowledgePurchaseHandler acknowledgePurchaseHandler;


    public PurchaseDetails(ProductId id, PurchaseState purchaseState, boolean isAknowledged, AcknowledgePurchaseHandler acknowledgePurchaseHandler) {
        this.id = id;
        this.purchaseState = purchaseState;
        this.isAknowledged = isAknowledged;
        this.acknowledgePurchaseHandler = acknowledgePurchaseHandler;
    }

    public ProductId getId() {
        return id;
    }

    public PurchaseState getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(PurchaseState purchaseState) {
        this.purchaseState = purchaseState;
    }

    public boolean isAknowledged() {
        return isAknowledged;
    }

    public void acknowledgePurchase(){
        if(acknowledgePurchaseHandler == null) return;
        acknowledgePurchaseHandler.AcknowledgePurchase(id);
    }
}
