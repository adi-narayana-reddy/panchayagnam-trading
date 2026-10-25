class WsHolding {
  final String tradingSymbol;
  final String exchange;          // ← ADD THIS
  final int quantity;
  final double avgPrice;
  final double ltp;
  final double investedValue;
  final double currentValue;
  final double pnl;
  final double pnlPercent;
  final double dayChange;         // ← ADD THIS (used by WsHoldingExtra)
  final String token;

  const WsHolding({
    required this.tradingSymbol,
    required this.exchange,       // ← ADD THIS
    required this.quantity,
    required this.avgPrice,
    required this.ltp,
    required this.investedValue,
    required this.currentValue,
    required this.pnl,
    required this.pnlPercent,
    required this.dayChange,
    required this.token,
  });

  factory WsHolding.fromJson(Map<String, dynamic> j) => WsHolding(
    tradingSymbol: j['tradingsymbol'] as String? ?? '',
    exchange: j['exchange'] as String? ?? 'NSE',   // ← ADD THIS
    quantity: (j['quantity'] as num?)?.toInt() ?? 0,
    avgPrice: (j['avgPrice'] as num?)?.toDouble() ?? 0,
    ltp: (j['ltp'] as num?)?.toDouble() ?? 0,
    investedValue: (j['investedValue'] as num?)?.toDouble() ?? 0,
    currentValue: (j['currentValue'] as num?)?.toDouble() ?? 0,
    pnl: (j['pnl'] as num?)?.toDouble() ?? 0,
    pnlPercent: (j['pnlPercent'] as num?)?.toDouble() ?? 0,
    dayChange: (j['dayChange'] as num?)?.toDouble() ?? 0,  // ← ADD THIS
    token: j['token']?.toString() ?? '',
  );
}