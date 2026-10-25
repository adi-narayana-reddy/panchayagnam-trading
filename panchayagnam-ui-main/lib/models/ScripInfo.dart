// ─── Domain Entities ──────────────────────────────────────────────────────────
// Pure domain objects — no Flutter, no http, no JSON dependencies here.
// These replace the scattered models in execution_models.dart.
// ─────────────────────────────────────────────────────────────────────────────

class ScripInfo {
  final String tradingSymbol;
  final String exchange;
  final String symbolToken;
  final String instrumentType; // 'EQ', 'FUTSTK', 'OPTSTK', etc.
  final int lotSize;
  final String name;
  final double tickSize;

  const ScripInfo({
    required this.tradingSymbol,
    required this.exchange,
    required this.symbolToken,
    this.instrumentType = '',
    this.lotSize = 1,
    this.name = '',
    this.tickSize = 0.05,
  });

  /// Convenience: human-readable short type label
  String get typeLabel {
    final t = instrumentType.toUpperCase();
    if (t.contains('FUT')) return 'FUT';
    if (t.contains('OPT')) return 'OPT';
    if (t == 'EQ' || t.isEmpty) return 'EQ';
    return t;
  }

  bool get isDerivative {
    final t = instrumentType.toUpperCase();
    return t.contains('FUT') || t.contains('OPT');
  }

  factory ScripInfo.fromJson(Map<String, dynamic> j) => ScripInfo(
    tradingSymbol:
        j['tradingsymbol'] as String? ?? j['tradingSymbol'] as String? ?? '',
    exchange: j['exch_seg'] as String? ?? j['exchange'] as String? ?? 'NSE',
    symbolToken:
        j['symboltoken'] as String? ?? j['symbolToken'] as String? ?? '',
    instrumentType:
        j['instrumentType'] as String? ?? j['instrument_type'] as String? ?? '',
    lotSize:
        int.tryParse((j['lotSize'] ?? j['lot_size'] ?? '1').toString()) ?? 1,
    name: j['name'] as String? ?? '',
    tickSize:
        double.tryParse(
          (j['tickSize'] ?? j['tick_size'] ?? '0.05').toString(),
        ) ??
        0.05,
  );
}

class LtpData {
  final double ltp;
  final double open;
  final double high;
  final double low;
  final double close;
  final double change;
  final double percentChange;

  const LtpData({
    required this.ltp,
    required this.open,
    required this.high,
    required this.low,
    required this.close,
    required this.change,
    required this.percentChange,
  });

  factory LtpData.fromJson(Map<String, dynamic> j) {
    // Support both nested { data: { ltp: ... } } and flat { ltp: ... }
    final d = j['data'] as Map<String, dynamic>? ?? j;
    return LtpData(
      ltp: (d['ltp'] as num?)?.toDouble() ?? 0,
      open: (d['open'] as num?)?.toDouble() ?? 0,
      high: (d['high'] as num?)?.toDouble() ?? 0,
      low: (d['low'] as num?)?.toDouble() ?? 0,
      close: (d['close'] as num?)?.toDouble() ?? 0,
      change: (d['change'] as num?)?.toDouble() ?? 0,
      percentChange:
          (d['percentchange'] as num?)?.toDouble() ??
          (d['percentChange'] as num?)?.toDouble() ??
          0,
    );
  }
}

class OrderHistoryItem {
  final String orderId;
  final String uniqueOrderId;
  final String tradingSymbol;
  final String exchange;
  final int quantity;
  final double price;
  final String orderType;
  final String symboltoken;
  final String variety;
  final String productType;
  final String status;
  final String side;
  final double? filledPrice;
  final int filledQty;
  final DateTime? orderTime;

  const OrderHistoryItem({
    required this.orderId,
    required this.uniqueOrderId,
    required this.tradingSymbol,
    required this.exchange,
    required this.quantity,
    required this.symboltoken,
    required this.price,
    required this.orderType,
    required this.productType,
    required this.variety,
    required this.status,
    required this.side,
    this.filledPrice,
    required this.filledQty,
    this.orderTime,
  });
}

class OrderDetail {
  final String orderId;
  final String uniqueOrderId;
  final String status;
  final String tradingSymbol;
  final int quantity;
  final int filledQty;
  final double? avgPrice;

  const OrderDetail({
    required this.orderId,
    required this.uniqueOrderId,
    required this.status,
    required this.tradingSymbol,
    required this.quantity,
    required this.filledQty,
    this.avgPrice,
  });

  factory OrderDetail.fromJson(Map<String, dynamic> j) {
    final d = j['data'] as Map<String, dynamic>? ?? j;
    return OrderDetail(
      orderId: d['orderid']?.toString() ?? '',
      uniqueOrderId: d['uniqueorderid']?.toString() ?? '',
      status: d['orderstatus']?.toString() ?? d['status']?.toString() ?? '',
      tradingSymbol: d['tradingsymbol']?.toString() ?? '',
      quantity:
          (d['quantity'] as num?)?.toInt() ??
          int.tryParse(d['quantity']?.toString() ?? '0') ??
          0,
      filledQty:
          (d['filledshares'] as num?)?.toInt() ??
          int.tryParse(d['filledshares']?.toString() ?? '0') ??
          0,
      avgPrice: (d['averageprice'] as num?)?.toDouble(),
    );
  }
}
