import 'package:flutter_bloc/flutter_bloc.dart';
import '../../service/group_positions_service.dart';
import 'group_positions_event.dart';
import 'group_positions_state.dart';

class GroupPositionsBloc
    extends Bloc<GroupPositionsEvent, GroupPositionsState> {
  final GroupPositionsService _service;

  List<Map<String, dynamic>> _batches = [];
  int _groupId = 0;

  GroupPositionsBloc({GroupPositionsService? service})
    : _service = service ?? GroupPositionsService(),
      super(GroupPositionsInitial()) {
    on<LoadGroupPositions>(_onLoad);
    on<LoadBatchPositions>(_onLoadBatch);
    on<FullGroupExit>(_onFullExit);
    on<SelectiveGroupExit>(_onSelectiveExit);
    on<BuyOneSellOne>(_onBuyOneSellOne);
    on<SellOneBuyOne>(_onSellOneBuyOne);
  }

  Future<void> _onLoad(
    LoadGroupPositions event,
    Emitter<GroupPositionsState> emit,
  ) async {
    _groupId = event.groupId;
    emit(GroupPositionsLoading());
    try {
      _batches = [];
      _batches = await _service.fetchGroupPositions(event.groupId);
      emit(GroupPositionsLoaded(batches: _batches, groupId: _groupId));
    } catch (e) {
      emit(
        GroupPositionsError(
          message: e.toString(),
          batches: _batches,
          groupId: _groupId,
        ),
      );
    }
  }

  Future<void> _onLoadBatch(
    LoadBatchPositions event,
    Emitter<GroupPositionsState> emit,
  ) async {
    _groupId = event.groupId;
    emit(GroupPositionsLoading());
    try {
      _batches = await _service.fetchBatchPositions(
        event.groupId,
        event.batchId,
      );
      emit(GroupPositionsLoaded(batches: _batches, groupId: _groupId));
    } catch (e) {
      emit(
        GroupPositionsError(
          message: e.toString(),
          batches: _batches,
          groupId: _groupId,
        ),
      );
    }
  }

  Future<void> _onFullExit(
    FullGroupExit event,
    Emitter<GroupPositionsState> emit,
  ) async {
    emit(GroupPositionsOperationLoading(batches: _batches, groupId: _groupId));
    try {
      final result = await _service.fullGroupExit(
        groupBatchId: event.groupOrderBatchId,
        orderType: event.orderType,
        productType: event.productType,
        variety: event.variety,
        triggerPrice: event.triggerPrice,
      );
      emit(
        GroupPositionsOperationSuccess(
          message: result['message'] ?? 'Group exit completed',
          resultData: result['data'] != null
              ? Map<String, dynamic>.from(result['data'])
              : null,
          batches: _batches,
          groupId: _groupId,
        ),
      );
      _batches = await _service.fetchGroupPositions(event.groupId);
      emit(GroupPositionsLoaded(batches: _batches, groupId: _groupId));
    } catch (e) {
      emit(
        GroupPositionsError(
          message: e.toString(),
          batches: _batches,
          groupId: _groupId,
        ),
      );
    }
  }

  Future<void> _onSelectiveExit(
    SelectiveGroupExit event,
    Emitter<GroupPositionsState> emit,
  ) async {
    emit(GroupPositionsOperationLoading(batches: _batches, groupId: _groupId));
    try {
      final result = await _service.selectiveGroupExit(
        groupId: event.groupOrderBatchId,
        brokerAccountIds: event.brokerAccountIds,
        orderType: event.orderType,
        productType: event.productType,
        variety: event.variety,
        triggerPrice: event.triggerPrice,
      );
      emit(
        GroupPositionsOperationSuccess(
          message: result['message'] ?? 'Selective exit completed',
          resultData: result['data'] != null
              ? Map<String, dynamic>.from(result['data'])
              : null,
          batches: _batches,
          groupId: _groupId,
        ),
      );
      _batches = await _service.fetchGroupPositions(event.groupId);
      emit(GroupPositionsLoaded(batches: _batches, groupId: _groupId));
    } catch (e) {
      emit(
        GroupPositionsError(
          message: e.toString(),
          batches: _batches,
          groupId: _groupId,
        ),
      );
    }
  }

  Future<void> _onBuyOneSellOne(
    BuyOneSellOne event,
    Emitter<GroupPositionsState> emit,
  ) async {
    emit(GroupPositionsOperationLoading(batches: _batches, groupId: _groupId));
    try {
      final result = await _service.placeOrderForBroker({
        'groupId': event.groupId,
        'brokerAccountIds': [event.brokerAccountId],
        'tradingSymbol': event.tradingSymbol,
        'symbolToken': event.symbolToken,
        'exchange': event.exchange,
        'transactionType': 'SELL',
        'orderType': event.orderType,
        'productType': event.productType,
        'variety': event.variety,
        'quantity': event.quantity,
        'price': event.price,
      });
      emit(
        GroupPositionsOperationSuccess(
          message: result['message'] ?? 'Sell order placed successfully',
          resultData: result['data'] != null
              ? Map<String, dynamic>.from(result['data'])
              : null,
          batches: _batches,
          groupId: _groupId,
        ),
      );
      _batches = await _service.fetchGroupPositions(event.groupId);
      emit(GroupPositionsLoaded(batches: _batches, groupId: _groupId));
    } catch (e) {
      emit(
        GroupPositionsError(
          message: e.toString(),
          batches: _batches,
          groupId: _groupId,
        ),
      );
    }
  }

  Future<void> _onSellOneBuyOne(
    SellOneBuyOne event,
    Emitter<GroupPositionsState> emit,
  ) async {
    emit(GroupPositionsOperationLoading(batches: _batches, groupId: _groupId));
    try {
      final result = await _service.placeOrderForBroker({
        'groupId': event.groupId,
        'brokerAccountIds': [event.brokerAccountId],
        'tradingSymbol': event.tradingSymbol,
        'symbolToken': event.symbolToken,
        'exchange': event.exchange,
        'transactionType': 'BUY',
        'orderType': event.orderType,
        'productType': event.productType,
        'variety': event.variety,
        'quantity': event.quantity,
        'price': event.price,
      });
      emit(
        GroupPositionsOperationSuccess(
          message: result['message'] ?? 'Buy order placed successfully',
          resultData: result['data'] != null
              ? Map<String, dynamic>.from(result['data'])
              : null,
          batches: _batches,
          groupId: _groupId,
        ),
      );
      _batches = await _service.fetchGroupPositions(event.groupId);
      emit(GroupPositionsLoaded(batches: _batches, groupId: _groupId));
    } catch (e) {
      emit(
        GroupPositionsError(
          message: e.toString(),
          batches: _batches,
          groupId: _groupId,
        ),
      );
    }
  }
}
