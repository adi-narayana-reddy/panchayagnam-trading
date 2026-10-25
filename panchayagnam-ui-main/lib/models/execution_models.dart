class ScripInfo {
  final String tradingSymbol;
  final String exchange;
  final String symbolToken;

  ScripInfo({
    required this.tradingSymbol,
    required this.exchange,
    required this.symbolToken,
  });

  factory ScripInfo.fromJson(Map<String, dynamic> json) => ScripInfo(
        tradingSymbol: json['tradingsymbol'] ?? '',
        exchange: json['exchange'] ?? '',
        symbolToken: json['symboltoken'] ?? '',
      );
}

class LtpData {
  final double ltp;
  final double open;
  final double high;
  final double low;
  final double close;

  LtpData({
    required this.ltp,
    required this.open,
    required this.high,
    required this.low,
    required this.close,
  });

  factory LtpData.fromJson(Map<String, dynamic> json) => LtpData(
        ltp: (json['ltp'] as num).toDouble(),
        open: (json['open'] as num).toDouble(),
        high: (json['high'] as num).toDouble(),
        low: (json['low'] as num).toDouble(),
        close: (json['close'] as num).toDouble(),
      );

  double get changePercent => close != 0 ? ((ltp - close) / close) * 100 : 0;
  double get changeAbs => ltp - close;
  bool get isPositive => ltp >= close;
}

class OrderResult {
  final String status;
  final String orderId;
  final String uniqueOrderId;

  OrderResult({
    required this.status,
    required this.orderId,
    required this.uniqueOrderId,
  });

  factory OrderResult.fromJson(Map<String, dynamic> json) => OrderResult(
        status: json['status'] ?? '',
        orderId: json['orderId'] ?? '',
        uniqueOrderId: json['UniqueOrderId'] ?? '',
      );
}

class OrderHistoryItem {
  final String orderId;
  final String uniqueOrderId;
  final String tradingSymbol;
  final String exchange;
  final int quantity;
  final double price;
  final String orderType;
  final String productType;
  final String status;
  final String side;
  final DateTime? orderTime;
  final double? filledPrice;
  final int? filledQty;

  OrderHistoryItem({
    required this.orderId,
    required this.uniqueOrderId,
    required this.tradingSymbol,
    required this.exchange,
    required this.quantity,
    required this.price,
    required this.orderType,
    required this.productType,
    required this.status,
    required this.side,
    this.orderTime,
    this.filledPrice,
    this.filledQty,
  });

  factory OrderHistoryItem.fromJson(Map<String, dynamic> json) =>
      OrderHistoryItem(
        orderId: json['orderId']?.toString() ?? '',
        uniqueOrderId: json['uniqueOrderId']?.toString() ?? '',
        tradingSymbol: json['tradingSymbol'] ?? '',
        exchange: json['exchange'] ?? '',
        quantity: (json['quantity'] as num?)?.toInt() ?? 0,
        price: (json['price'] as num?)?.toDouble() ?? 0.0,
        orderType: json['orderType'] ?? '',
        productType: json['productType'] ?? '',
        status: json['status'] ?? '',
        side: json['side'] ?? json['transactionType'] ?? '',
        orderTime: json['orderTime'] != null
            ? DateTime.tryParse(json['orderTime'])
            : null,
        filledPrice: (json['filledPrice'] as num?)?.toDouble(),
        filledQty: (json['filledQty'] as num?)?.toInt(),
      );
}

class OrderDetail extends OrderHistoryItem {
  final String? rejectionReason;
  final String? exchangeOrderId;

  OrderDetail({
    required super.orderId,
    required super.uniqueOrderId,
    required super.tradingSymbol,
    required super.exchange,
    required super.quantity,
    required super.price,
    required super.orderType,
    required super.productType,
    required super.status,
    required super.side,
    super.orderTime,
    super.filledPrice,
    super.filledQty,
    this.rejectionReason,
    this.exchangeOrderId,
  });

  factory OrderDetail.fromJson(Map<String, dynamic> json) => OrderDetail(
        orderId: json['orderId']?.toString() ?? '',
        uniqueOrderId: json['uniqueOrderId']?.toString() ?? '',
        tradingSymbol: json['tradingSymbol'] ?? '',
        exchange: json['exchange'] ?? '',
        quantity: (json['quantity'] as num?)?.toInt() ?? 0,
        price: (json['price'] as num?)?.toDouble() ?? 0.0,
        orderType: json['orderType'] ?? '',
        productType: json['productType'] ?? '',
        status: json['status'] ?? '',
        side: json['side'] ?? json['transactionType'] ?? '',
        orderTime: json['orderTime'] != null
            ? DateTime.tryParse(json['orderTime'])
            : null,
        filledPrice: (json['filledPrice'] as num?)?.toDouble(),
        filledQty: (json['filledQty'] as num?)?.toInt(),
        rejectionReason: json['rejectionReason'],
        exchangeOrderId: json['exchangeOrderId'],
      );
}
