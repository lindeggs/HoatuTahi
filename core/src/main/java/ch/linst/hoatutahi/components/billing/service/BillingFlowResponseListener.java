package ch.linst.hoatutahi.components.billing.service;

import java.util.List;

/**
 * The BillingFlowResponseListener asynchronously receives via onBillingFlowResponse the
 * purchase result of the items being in billing flow
 */
public interface BillingFlowResponseListener {

    void onBillingFlowResponse(boolean success, List<PurchaseDetails> purchaseList);
}
