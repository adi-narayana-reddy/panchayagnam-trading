import 'package:flutter_bloc/flutter_bloc.dart';

import '../../service/individual_drop_down_service.dart';
import 'individual_drop_down_event.dart';
import 'individual_drop_down_state.dart';

class BrokerBloc extends Bloc<BrokerEvent, BrokerState> {
  final BrokerService brokerService;

  BrokerBloc({required this.brokerService}) : super(BrokerInitial()) {
    on<FetchBrokers>(_onFetch);
    on<SelectBroker>(_onSelect);
  }

  Future<void> _onFetch(FetchBrokers event, Emitter<BrokerState> emit) async {
    emit(BrokerLoading());

    try {
      final brokers = await brokerService.fetchBrokers(
        pyId: event.pyId,
        broker: event.broker, // 👈 IMPORTANT FIX
      );

      if (brokers.isEmpty) {
        emit(BrokerError('No ${event.broker} accounts found'));
        return;
      }

      final selected = brokers.firstWhere(
        (b) => b.isOwn,
        orElse: () => brokers.first,
      );

      emit(BrokerLoaded(selectedBroker: selected, brokers: brokers));
    } catch (e) {
      emit(BrokerError(e.toString()));
    }
  }

  void _onSelect(SelectBroker event, Emitter<BrokerState> emit) {
    final current = state;

    if (current is BrokerLoaded) {
      emit(current.copyWith(selectedBroker: event.broker));
    }
  }
}
