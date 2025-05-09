package ch.linst.hoatutahi.components.billing.service;

import java.util.List;

/**
 * The PurchasesResponseListener asynchronously receives via onPurchasesResponse the
 * purchases for each buy-able product
 */
public interface PurchasesResponseListener {

    void onPurchasesResponse(boolean success, List<PurchaseDetails> purchaseList);
}
