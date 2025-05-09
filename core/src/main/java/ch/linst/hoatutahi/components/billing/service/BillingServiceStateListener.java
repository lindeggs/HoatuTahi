package ch.linst.hoatutahi.components.billing.service;

public interface BillingServiceStateListener {

    public void onBillingSetupFinished(boolean success);
    public void onBillingServiceDisconnected();
}
