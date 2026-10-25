import 'package:flutter_bloc/flutter_bloc.dart';
import '../../models/demat_account_model.dart';
import '../../repo/demat_repository.dart';
import '../../service/service_class.dart';
part 'fetch_demat_event.dart';
part 'fetch_demat_state.dart';

class FetchDematBloc
    extends Bloc<FetchDematEvent, FetchDematState> {
  final DematRepository _repository;

  FetchDematBloc({DematRepository? repository})
      : _repository = repository ?? DematRepositoryImpl(),
        super(FetchDematInitial()) {
    on<LoadDematAccounts>(_onLoad);
  }

  Future<void> _onLoad(
      LoadDematAccounts event,
      Emitter<FetchDematState> emit,
      ) async {
    emit(FetchDematLoading());

    try {
      final response =
      await _repository.fetchAccounts(event.isAdmin);

      if (response.status == "SUCCESS") {
        emit(FetchDematLoaded(response.data));
      } else {
        emit(FetchDematError(response.message));
      }
    } on ServiceException catch (e) {
      emit(FetchDematError(e.message));
    } catch (e) {
      emit(FetchDematError("Something went wrong"));
    }
  }
}