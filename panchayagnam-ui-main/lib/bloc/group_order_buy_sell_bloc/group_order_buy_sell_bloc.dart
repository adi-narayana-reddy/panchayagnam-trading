import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:equatable/equatable.dart';

import '../../service/group_order_buy_sell_service.dart';

// ─── Events ───────────────────────────────────────────────────────────────────

abstract class GroupOrderEvent extends Equatable {
  const GroupOrderEvent();
  @override
  List<Object?> get props => [];
}

class PlaceGroupBasketOrderRequested extends GroupOrderEvent {
  final PlaceGroupOrderRequest request;
  const PlaceGroupBasketOrderRequested({required this.request});

  @override
  List<Object?> get props => [request];
}

class GroupOrderReset extends GroupOrderEvent {
  const GroupOrderReset();
}

// ─── States ───────────────────────────────────────────────────────────────────

abstract class GroupOrderState extends Equatable {
  const GroupOrderState();
  @override
  List<Object?> get props => [];
}

class GroupOrderInitial extends GroupOrderState {
  const GroupOrderInitial();
}

class GroupOrderLoading extends GroupOrderState {
  const GroupOrderLoading();
}

class GroupOrderSuccess extends GroupOrderState {
  final GroupOrderResponse response;
  const GroupOrderSuccess({required this.response});

  @override
  List<Object?> get props => [response];
}

class GroupOrderFailure extends GroupOrderState {
  final String message;
  const GroupOrderFailure({required this.message});

  @override
  List<Object?> get props => [message];
}

// ─── BLoC ─────────────────────────────────────────────────────────────────────

class GroupOrderBloc extends Bloc<GroupOrderEvent, GroupOrderState> {
  final GroupOrderService _service;

  GroupOrderBloc({GroupOrderService? service})
      : _service = service ?? GroupOrderService(),
        super(const GroupOrderInitial()) {
    on<PlaceGroupBasketOrderRequested>(_onPlaceOrder);
    on<GroupOrderReset>(_onReset);
  }

  Future<void> _onPlaceOrder(
      PlaceGroupBasketOrderRequested event,
      Emitter<GroupOrderState> emit,
      ) async {
    emit(const GroupOrderLoading());
    try {
      final response = await _service.placeGroupOrder(event.request);
      emit(GroupOrderSuccess(response: response));
    } on GroupOrderException catch (e) {
      emit(GroupOrderFailure(message: e.message));
    } catch (e) {
      emit(GroupOrderFailure(message: 'Unexpected error: ${e.toString()}'));
    }
  }

  void _onReset(GroupOrderReset event, Emitter<GroupOrderState> emit) {
    emit(const GroupOrderInitial());
  }
}