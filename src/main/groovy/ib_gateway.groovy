import com.ib.client.Contract
import com.ib.client.ContractDetails
import com.ib.client.EClientSocket
import com.ib.client.EWrapper
import com.ib.client.EWrapperMsgGenerator
import com.ib.client.Execution
import com.ib.client.Order
import com.ib.client.OrderState
import com.ib.client.UnderComp

class IbGateway implements EWrapper {
   def client_socket = new EClientSocket(this)
   
   public void accountDownloadEnd(java.lang.String end) {}
   public void bondContractDetails(int reqId, ContractDetails contractDetails) {}
   public void connectionClosed() {}
   public void contractDetails(int reqId, ContractDetails contractDetails) {}
   public void contractDetailsEnd(int reqId) {}
   public void currentTime(long time) {}
   public void deltaNeutralValidation(int int_1, com.ib.client.UnderComp underComp) {}
   public void deltaNeutralValidation(undercomp) {}
   public void error( Exception e) { println e }
   public void error( String str) { println str }
   public void error(int id, int errorCode, String errorMsg){}
   public void execDetails( int orderId, Contract contract, Execution execution) {}
   public void execDetailsEnd(int at) {}
   public void fundamentalData(int reqId, String data) {}
   public void historicalData(int reqId, String date, double open, double high, double low, double close, int volume, int count, double WAP, boolean hasGaps) {}
   public void managedAccounts( String accountsList) {}
   public void marketDataType(int int_1, int int_2) {}
   public void nextValidId(int orderId) {}
   public void openOrder( int orderId, Contract contract, Order order, OrderState orderState) {}
   public void openOrderEnd() {}
   public void orderStatus( int orderId, String status, int filled, int remaining, double avgFillPrice, int permId, int parentId, double lastFillPrice, int clientId, String whyHeld) {}
   public void realtimeBar(int reqId, long time, double open, double high, double low, double close, long volume, double wap, int count) {}
   public void receiveFA(int faDataType, String xml) {}
   public void scannerData(int reqId, int rank, ContractDetails contractDetails, String distance, String benchmark, String projection, String legsStr) {}
   public void scannerDataEnd(int reqId) {}
   public void scannerParameters(String xml) {}
   public void tickEFP(int tickerId, int tickType, double basisPoints, String formattedBasisPoints, double impliedFuture, int holdDays, String futureExpiry, double dividendImpact, double dividendsToExpiry) {}
   public void tickGeneric(int tickerId, int tickType, double value) {}
   public void tickOptionComputation( int tickerId, int field, double impliedVol, double delta, double modelPrice, double pvDividend) {}
   public void tickOptionComputation(int int_1, int int_2, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8) {}
   public void tickPrice( int tickerId, int field, double price, int canAutoExecute) {}
   public void tickSize( int tickerId, int field, int size) {}
   public void tickSnapshotEnd(int at) {}
   public void tickString(int tickerId, int tickType, String value) {}
   public void updateAccountTime(String timeStamp) {}
   public void updateAccountValue(String key, String value, String currency, String accountName) {}
   public void updateMktDepth( int tickerId, int position, int operation, int side, double price, int size) {}
   public void updateMktDepthL2( int tickerId, int position, String marketMaker, int operation, int side, double price, int size) {}
   public void updateNewsBulletin( int msgId, int msgType, String message, String origExchange) {}
   public void updatePortfolio(Contract contract, int position, double marketPrice, double marketValue, double averageCost, double unrealizedPNL, double realizedPNL, String accountName) {}
}
