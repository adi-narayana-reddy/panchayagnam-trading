abstract class GroupPositionsEvent {}

// ── Load all batches for a group ──────────────────────────────────────────────
class LoadGroupPositions extends GroupPositionsEvent {
  final int groupId;
  LoadGroupPositions(this.groupId);
}

// ── Refresh a single batch ────────────────────────────────────────────────────
class LoadBatchPositions extends GroupPositionsEvent {
  final int groupId;
  final String batchId;

  LoadBatchPositions({required this.groupId, required this.batchId});
}

// ── Full exit for a group (all OPEN positions) ────────────────────────────────
class FullGroupExit extends GroupPositionsEvent {
  final String groupOrderBatchId;
  final int groupId;
  final int triggerPrice;
  final String orderType;
  final String productType;
  final String variety;

  FullGroupExit({
    required this.groupOrderBatchId,
    required this.groupId,
    required this.triggerPrice,
    required this.orderType,
    required this.productType,
    this.variety = 'NORMAL',
  });
}

// ── Selective exit (chosen brokers) ──────────────────────────────────────────
class SelectiveGroupExit extends GroupPositionsEvent {
  final int groupId;
  final String groupOrderBatchId;
  final List<int> brokerAccountIds;
  final String orderType;
  final String productType;
  final String variety;
  final int triggerPrice;

  SelectiveGroupExit({
    required this.groupId,
    required this.groupOrderBatchId,
    required this.brokerAccountIds,
    required this.orderType,
    required this.productType,
    required this.triggerPrice,
    this.variety = 'NORMAL',
  });
}

// ── Buy-One-Sell-One: place a SELL for one broker who has an OPEN BUY ─────────
class BuyOneSellOne extends GroupPositionsEvent {
  final int groupId;
  final int brokerAccountId;
  final String tradingSymbol;
  final String symbolToken;
  final String exchange;
  final int quantity;
  final double price;
  final String orderType;
  final String productType;
  final String variety;

  BuyOneSellOne({
    required this.groupId,
    required this.brokerAccountId,
    required this.tradingSymbol,
    required this.symbolToken,
    required this.exchange,
    required this.quantity,
    required this.price,
    required this.orderType,
    required this.productType,
    this.variety = 'NORMAL',
  });
}

// ── Sell-One-Buy-One: place a BUY for one broker ─────────────────────────────
class SellOneBuyOne extends GroupPositionsEvent {
  final int groupId;
  final int brokerAccountId;
  final String tradingSymbol;
  final String symbolToken;
  final String exchange;
  final int quantity;
  final double price;
  final String orderType;
  final String productType;
  final String variety;

  SellOneBuyOne({
    required this.groupId,
    required this.brokerAccountId,
    required this.tradingSymbol,
    required this.symbolToken,
    required this.exchange,
    required this.quantity,
    required this.price,
    required this.orderType,
    required this.productType,
    this.variety = 'NORMAL',
  });
}
