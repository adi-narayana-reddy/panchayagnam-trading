import '../../models/hot_stock_model.dart';

abstract class HotStockEvent {
  const HotStockEvent();
}

// ── Fetch stocks for a date + category ───────────────────────────────────────
class FetchHotStocksEvent extends HotStockEvent {
  final String date; // yyyy-MM-dd
  final HotStockCategory category;

  const FetchHotStocksEvent({required this.date, required this.category});
}

// ── Change active category chip ───────────────────────────────────────────────
class ChangeCategoryEvent extends HotStockEvent {
  final HotStockCategory category;
  const ChangeCategoryEvent(this.category);
}

// ── Change selected date ──────────────────────────────────────────────────────
class ChangeDateEvent extends HotStockEvent {
  final String date; // yyyy-MM-dd
  const ChangeDateEvent(this.date);
}

// ── ADD new stock ─────────────────────────────────────────────────────────────
class AddHotStockEvent extends HotStockEvent {
  final HotStockOperationRequest request;
  const AddHotStockEvent(this.request);
}

// ── UPDATE existing stock ─────────────────────────────────────────────────────
class UpdateHotStockEvent extends HotStockEvent {
  final HotStockOperationRequest request;
  const UpdateHotStockEvent(this.request);
}

// ── DELETE (soft) stock ───────────────────────────────────────────────────────
class DeleteHotStockEvent extends HotStockEvent {
  final int stockId;
  const DeleteHotStockEvent(this.stockId);
}

// ── Clear operation result snackbar state ────────────────────────────────────
class ClearOperationResultEvent extends HotStockEvent {
  const ClearOperationResultEvent();
}