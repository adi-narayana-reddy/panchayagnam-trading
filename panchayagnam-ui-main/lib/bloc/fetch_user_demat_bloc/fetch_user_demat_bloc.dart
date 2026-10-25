import 'package:flutter_bloc/flutter_bloc.dart';
import '../../models/demat_account.dart';
import '../../service/service_class.dart';
part 'fetch_user_demat_event.dart';
part 'fetch_user_demat_state.dart';

class FetchUserDematBloc
    extends Bloc<FetchUserDematEvent, FetchUserDematState> {
  FetchUserDematBloc() : super(FetchUserDematInitial()) {
    on<LoadUserDematAccounts>(_onLoad);
  }

  ServiceClass serviceClass = ServiceClass();

  Future<void> _onLoad(
    LoadUserDematAccounts event,
    Emitter<FetchUserDematState> emit,
  ) async {
    emit(FetchUserDematLoading(event.pyUserId));
    try {
      final accounts = await serviceClass.fetchDematAccountsApi(event.pyUserId);
      emit(FetchUserDematLoaded(event.pyUserId, accounts));
    } catch (e) {
      emit(FetchUserDematError(event.pyUserId, e.toString()));
    }
  }
}
