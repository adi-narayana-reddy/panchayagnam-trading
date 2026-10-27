import 'dart:html' as html;
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:intl/intl.dart';
import '../../app_route/user_session.dart';
import '../../models/hot_stock_model.dart';
import '../../service/hot_stock_service.dart';
import 'hot_stock_event.dart';
import 'hot_stock_state.dart';

class HotStockBloc extends Bloc<HotStockEvent, HotStockState> {
  final HotStockService _service;

  HotStockBloc({HotStockService? service})
      : _service = service ?? HotStockService(),
        super(
        HotStockState(
          selectedDate: DateFormat('yyyy-MM-dd').format(DateTime.now()),
        ),
      ) {
    on<FetchHotStocksEvent>(_onFetch);
    on<ChangeCategoryEvent>(_onChangeCategory);
    on<ChangeDateEvent>(_onChangeDate);
    on<AddHotStockEvent>(_onAdd);
    on<UpdateHotStockEvent>(_onUpdate);
    on<DeleteHotStockEvent>(_onDelete);
    on<ClearOperationResultEvent>(_onClearResult);

    // Auto-fetch on init only if authenticated
    String token = UserSession().token;
    if (token.isEmpty) {
      try {
        token = html.window.sessionStorage['auth_token'] ?? '';
      } catch (_) {}
    }
    if (token.isNotEmpty) {
      add(
        FetchHotStocksEvent(
          date: state.selectedDate,
          category: state.selectedCategory,
        ),
      );
    }
  }

  // ── Fetch ─────────────────────────────────────────────────────────────────

  Future<void> _onFetch(
      FetchHotStocksEvent event,
      Emitter<HotStockState> emit,
      ) async {
    String token = UserSession().token;
    if (token.isEmpty) {
      try {
        token = html.window.sessionStorage['auth_token'] ?? '';
      } catch (_) {}
    }
    if (token.isEmpty) {
      emit(state.copyWith(
        selectedDate: event.date,
        selectedCategory: event.category,
        isLoadingStocks: false,
        clearLoadError: true,
      ));
      return;
    }

    emit(state.copyWith(
      selectedDate: event.date,
      selectedCategory: event.category,
      isLoadingStocks: true,
      clearLoadError: true,
    ));

    final result = await _service.fetchByDateAndCategory(
      date: event.date,
      category: event.category,
    );

    if (result.success) {
      final stocks = result.data ?? [];
      bool isFallback = false;
      String? fallbackDate;

      if (stocks.isNotEmpty) {
        // Detect if the stocks returned have a creation date different from the requested date
        final stockCreated = stocks.first.createdAt;
        if (stockCreated != null && stockCreated.isNotEmpty) {
          try {
            final parsedDate = stockCreated.length >= 10
                ? stockCreated.substring(0, 10)
                : '';
            if (parsedDate.isNotEmpty && parsedDate != event.date) {
              isFallback = true;
              fallbackDate = parsedDate;
            }
          } catch (_) {}
        }
      }

      emit(
        state.copyWith(
          stocks: stocks,
          isLoadingStocks: false,
          clearLoadError: true,
          isFallback: isFallback,
          fallbackDate: fallbackDate,
        ),
      );
    } else {
      emit(
        state.copyWith(
          isLoadingStocks: false,
          loadError: result.message,
        ),
      );
    }
  }

  // ── Category chip change ──────────────────────────────────────────────────

  void _onChangeCategory(
      ChangeCategoryEvent event,
      Emitter<HotStockState> emit,
      ) {
    emit(state.copyWith(selectedCategory: event.category));
    add(
      FetchHotStocksEvent(
        date: state.selectedDate,
        category: event.category,
      ),
    );
  }

  // ── Date change ───────────────────────────────────────────────────────────

  void _onChangeDate(ChangeDateEvent event, Emitter<HotStockState> emit) {
    emit(state.copyWith(selectedDate: event.date));
    add(
      FetchHotStocksEvent(
        date: event.date,
        category: state.selectedCategory,
      ),
    );
  }

  // ── ADD ───────────────────────────────────────────────────────────────────

  Future<void> _onAdd(
      AddHotStockEvent event,
      Emitter<HotStockState> emit,
      ) async {
    emit(state.copyWith(operationStatus: OperationStatus.loading));

    final result = await _service.operations(event.request);

    if (result.success) {
      emit(
        state.copyWith(
          operationStatus: OperationStatus.success,
          operationMessage: result.message,
        ),
      );
      // Refresh list
      add(
        FetchHotStocksEvent(
          date: state.selectedDate,
          category: state.selectedCategory,
        ),
      );
    } else {
      emit(
        state.copyWith(
          operationStatus: OperationStatus.failure,
          operationMessage: result.message,
        ),
      );
    }
  }

  // ── UPDATE ────────────────────────────────────────────────────────────────

  Future<void> _onUpdate(
      UpdateHotStockEvent event,
      Emitter<HotStockState> emit,
      ) async {
    emit(state.copyWith(operationStatus: OperationStatus.loading));

    final result = await _service.operations(event.request);

    if (result.success) {
      emit(
        state.copyWith(
          operationStatus: OperationStatus.success,
          operationMessage: result.message,
        ),
      );
      add(
        FetchHotStocksEvent(
          date: state.selectedDate,
          category: state.selectedCategory,
        ),
      );
    } else {
      emit(
        state.copyWith(
          operationStatus: OperationStatus.failure,
          operationMessage: result.message,
        ),
      );
    }
  }

  // ── DELETE ────────────────────────────────────────────────────────────────

  Future<void> _onDelete(
      DeleteHotStockEvent event,
      Emitter<HotStockState> emit,
      ) async {
    emit(state.copyWith(operationStatus: OperationStatus.loading));

    final result = await _service.operations(
      HotStockOperationRequest(
        actionType: HotStockActionType.DELETE,
        stockId: event.stockId
      ),
    );

    if (result.success) {
      emit(
        state.copyWith(
          operationStatus: OperationStatus.success,
          operationMessage: result.message,
        ),
      );
      add(
        FetchHotStocksEvent(
          date: state.selectedDate,
          category: state.selectedCategory,
        ),
      );
    } else {
      emit(
        state.copyWith(
          operationStatus: OperationStatus.failure,
          operationMessage: result.message,
        ),
      );
    }
  }

  // ── Clear operation result ────────────────────────────────────────────────

  void _onClearResult(
      ClearOperationResultEvent event,
      Emitter<HotStockState> emit,
      ) {
    emit(state.copyWith(operationStatus: OperationStatus.idle));
  }
}