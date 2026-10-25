import 'package:flutter_bloc/flutter_bloc.dart';
import '../../service/service_class.dart';

part 'fetch_account_balance_event.dart';
part 'fetch_account_balance_state.dart';

class FetchAccountBalanceBloc
    extends Bloc<FetchAccountBalanceEvent, FetchAccountBalanceState> {
  FetchAccountBalanceBloc() : super(FetchAccountBalanceInitial()) {
    on<LoadAccountBalance>(_onLoad);
  }

  final ServiceClass _serviceClass = ServiceClass();

  Future<void> _onLoad(
      LoadAccountBalance event,
      Emitter<FetchAccountBalanceState> emit,
      ) async {
    emit(FetchAccountBalanceLoading(event.pyBrokerId));
    try {
      final accounts =
      await _serviceClass.fetchDematAccountsByBrokerId(event.pyBrokerId);

      if (accounts.isNotEmpty) {
        emit(FetchAccountBalanceLoaded(
          event.pyBrokerId,
          accounts.first.availableCash,
        ));
      } else {
        emit(FetchAccountBalanceError(
          event.pyBrokerId,
          'No account data returned.',
        ));
      }
    } catch (e) {
      emit(FetchAccountBalanceError(event.pyBrokerId, e.toString()));
    }
  }
}