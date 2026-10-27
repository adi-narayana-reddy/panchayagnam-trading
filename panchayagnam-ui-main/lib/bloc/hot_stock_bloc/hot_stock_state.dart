import '../../models/hot_stock_model.dart';

// ── Operation result ──────────────────────────────────────────────────────────
enum OperationStatus { idle, loading, success, failure }

class HotStockState {
  // ── List state ──────────────────────────────────────────────────────────────
  final List<HotStockModel> stocks;
  final bool isLoadingStocks;
  final String? loadError;
  final bool isFallback;
  final String? fallbackDate;

  // ── Selected filters ────────────────────────────────────────────────────────
  final HotStockCategory selectedCategory;
  final String selectedDate; // yyyy-MM-dd

  // ── Operation (ADD / UPDATE / DELETE) state ─────────────────────────────────
  final OperationStatus operationStatus;
  final String? operationMessage;

  const HotStockState({
    this.stocks = const [],
    this.isLoadingStocks = false,
    this.loadError,
    this.isFallback = false,
    this.fallbackDate,
    this.selectedCategory = HotStockCategory.INTRADAY,
    required this.selectedDate,
    this.operationStatus = OperationStatus.idle,
    this.operationMessage,
  });

  HotStockState copyWith({
    List<HotStockModel>? stocks,
    bool? isLoadingStocks,
    String? loadError,
    bool clearLoadError = false,
    bool? isFallback,
    String? fallbackDate,
    bool clearFallback = false,
    HotStockCategory? selectedCategory,
    String? selectedDate,
    OperationStatus? operationStatus,
    String? operationMessage,
  }) {
    return HotStockState(
      stocks: stocks ?? this.stocks,
      isLoadingStocks: isLoadingStocks ?? this.isLoadingStocks,
      loadError: clearLoadError ? null : (loadError ?? this.loadError),
      isFallback: clearFallback ? false : (isFallback ?? this.isFallback),
      fallbackDate: clearFallback ? null : (fallbackDate ?? this.fallbackDate),
      selectedCategory: selectedCategory ?? this.selectedCategory,
      selectedDate: selectedDate ?? this.selectedDate,
      operationStatus: operationStatus ?? this.operationStatus,
      operationMessage: operationMessage ?? this.operationMessage,
    );
  }
}