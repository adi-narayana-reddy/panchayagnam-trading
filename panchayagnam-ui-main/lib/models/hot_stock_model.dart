// ── Enums ─────────────────────────────────────────────────────────────────────

enum HotStockCategory {
  INTRADAY,
  SHORT_TERM,
  MID_TERM,
  LONG_TERM;

  String get label {
    switch (this) {
      case HotStockCategory.INTRADAY:
        return 'Intraday';
      case HotStockCategory.SHORT_TERM:
        return 'Short Term';
      case HotStockCategory.MID_TERM:
        return 'Mid Term';
      case HotStockCategory.LONG_TERM:
        return 'Long Term';
    }
  }

  String get value => name; // 'INTRADAY', 'SHORT_TERM', etc.
}

enum HotStockSignal {
  BUY,
  STRONG_BUY,
  HOLD,
  SELL;

  String get label {
    switch (this) {
      case HotStockSignal.BUY:
        return 'BUY';
      case HotStockSignal.STRONG_BUY:
        return 'STRONG BUY';
      case HotStockSignal.HOLD:
        return 'HOLD';
      case HotStockSignal.SELL:
        return 'SELL';
    }
  }

  static HotStockSignal fromString(String value) {
    return HotStockSignal.values.firstWhere(
          (e) => e.name == value,
      orElse: () => HotStockSignal.BUY,
    );
  }
}

enum HotStockDirection {
  UP,
  DOWN;

  static HotStockDirection fromString(String value) {
    return HotStockDirection.values.firstWhere(
          (e) => e.name == value,
      orElse: () => HotStockDirection.UP,
    );
  }
}

enum HotStockActionType { ADD, UPDATE, DELETE }


class HotStockModel {
  final int stockId;
  final String symbol;
  final String symbolToken;
  final String companyName;
  final double price;
  final double target;
  final double? changePercent;
  final HotStockDirection direction;
  final HotStockSignal signal;
  final HotStockCategory category;
  final int recommendedByPyId;
  final String recommendedByName;
  final bool isActive;
  final String? createdAt;
  final String? updatedAt;

  const HotStockModel({
    required this.stockId,
    required this.symbol,
    required this.symbolToken,
    required this.companyName,
    required this.price,
    required this.target,
    this.changePercent,
    required this.direction,
    required this.signal,
    required this.category,
    required this.recommendedByPyId,
    required this.recommendedByName,
    required this.isActive,
    this.createdAt,
    this.updatedAt,
  });

  factory HotStockModel.fromJson(Map<String, dynamic> json) {
    return HotStockModel(
      stockId: json['stockId'] ?? 0,
      symbol: json['symbol'] ?? '',
      symbolToken: json['symbolToken'] ?? '',
      companyName: json['companyName'] ?? '',
      price: (json['price'] ?? 0).toDouble(),
      target: (json['target'] ?? 0).toDouble(),
      changePercent: json['changePercent'] != null
          ? (json['changePercent']).toDouble()
          : null,
      direction: HotStockDirection.fromString(json['direction'] ?? 'UP'),
      signal: HotStockSignal.fromString(json['signal'] ?? 'BUY'),
      category: HotStockCategory.values.firstWhere(
            (e) => e.name == json['category'],
        orElse: () => HotStockCategory.INTRADAY,
      ),
      recommendedByPyId: json['recommendedByPyId'] ?? 0,
      recommendedByName: json['recommendedByName'] ?? '',
      isActive: json['isActive'] ?? true,
      createdAt: json['createdAt'],
      updatedAt: json['updatedAt'],
    );
  }
}

// ── Operation Request ─────────────────────────────────────────────────────────

class HotStockOperationRequest {
  final HotStockActionType actionType;
  final int? pyId;
  final int? stockId;
  final String? symbol;
  final String? symbolToken;
  final String? companyName;
  final double? price;
  final double? target;
  final double? changePercent;
  final HotStockDirection? direction;
  final HotStockSignal? signal;
  final HotStockCategory? category;

  const HotStockOperationRequest({
    required this.actionType,
    this.pyId,
    this.stockId,
    this.symbol,
    this.symbolToken,
    this.companyName,
    this.price,
    this.target,
    this.changePercent,
    this.direction,
    this.signal,
    this.category,
  });

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{'actionType': actionType.name};
    if (pyId != null) map['pyId'] = pyId;
    if (stockId != null) map['stockId'] = stockId;
    if (symbol != null) map['symbol'] = symbol;
    if (symbolToken != null) map['symbolToken'] = symbolToken;
    if (companyName != null) map['companyName'] = companyName;
    if (price != null) map['price'] = price;
    if (target != null) map['target'] = target;
    if (changePercent != null) map['changePercent'] = changePercent;
    if (direction != null) map['direction'] = direction!.name;
    if (signal != null) map['signal'] = signal!.name;
    if (category != null) map['category'] = category!.name;
    return map;
  }
}