import 'package:flutter_bloc/flutter_bloc.dart';
import '../../models/py_user.dart';
import '../../service/service_class.dart';
part 'fetch_py_users_event.dart';
part 'fetch_py_users_state.dart';

class FetchPyUsersBloc extends Bloc<FetchPyUsersEvent, FetchPyUsersState> {
  FetchPyUsersBloc() : super(FetchPyUsersInitial()) {
    on<LoadAdminUsers>(_onLoadAdminUsers);
  }

  ServiceClass serviceClass = ServiceClass();
  Future<void> _onLoadAdminUsers(
    LoadAdminUsers event,
    Emitter<FetchPyUsersState> emit,
  ) async {
    emit(FetchPyUsersLoading());
    try {
      final users = await serviceClass.fetchUsersByRole('ADMIN');
      emit(FetchPyUsersLoaded(users));
    } catch (e) {
      emit(FetchPyUsersError(e.toString()));
    }
  }
}
