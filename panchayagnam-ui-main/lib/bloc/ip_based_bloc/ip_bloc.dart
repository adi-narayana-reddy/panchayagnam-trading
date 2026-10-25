import 'package:flutter_bloc/flutter_bloc.dart';
import '../../models/ip_model.dart';
import '../../service/ip_service.dart';

part 'ip_event.dart';
part 'ip_state.dart';

class IpBloc extends Bloc<IpEvent, IpState> {
  final IpRepository _repository;

  IpBloc({required IpRepository repository})
    : _repository = repository,
      super(const IpState()) {
    on<SaveIpsEvent>(_onSaveIps);
    on<FetchAllIpsEvent>(_onFetchAllIps);
    on<FetchIpReportEvent>(_onFetchIpReport);
    on<CheckIpHealthEvent>(_onCheckIpHealth);
    on<ClearSaveStateEvent>(_onClearSaveState);
    on<ClearHealthStateEvent>(_onClearHealthState);
  }

  Future<void> _onSaveIps(SaveIpsEvent event, Emitter<IpState> emit) async {
    emit(
      state.copyWith(isSaving: true, saveError: null, clearSaveResponse: true),
    );
    try {
      final response = await _repository.saveIps(event.ips);
      emit(state.copyWith(isSaving: false, saveResponse: response));
    } catch (e) {
      emit(state.copyWith(isSaving: false, saveError: e.toString()));
    }
  }

  Future<void> _onFetchAllIps(
    FetchAllIpsEvent event,
    Emitter<IpState> emit,
  ) async {
    emit(state.copyWith(isFetchingAll: true, fetchAllError: null));
    try {
      final response = await _repository.fetchAllIps();
      emit(state.copyWith(isFetchingAll: false, allIps: response.data));
    } catch (e) {
      emit(state.copyWith(isFetchingAll: false, fetchAllError: e.toString()));
    }
  }

  Future<void> _onFetchIpReport(
    FetchIpReportEvent event,
    Emitter<IpState> emit,
  ) async {
    emit(state.copyWith(isFetchingReport: true, reportError: null));
    try {
      final response = await _repository.fetchIpReport();
      emit(state.copyWith(isFetchingReport: false, reportResponse: response));
    } catch (e) {
      emit(state.copyWith(isFetchingReport: false, reportError: e.toString()));
    }
  }

  Future<void> _onCheckIpHealth(
    CheckIpHealthEvent event,
    Emitter<IpState> emit,
  ) async {
    emit(
      state.copyWith(
        isCheckingHealth: true,
        healthError: null,
        clearHealthResponse: true,
      ),
    );
    try {
      final response = await _repository.checkIpHealth(event.ips);
      emit(state.copyWith(isCheckingHealth: false, healthResponse: response));
    } catch (e) {
      emit(state.copyWith(isCheckingHealth: false, healthError: e.toString()));
    }
  }

  void _onClearSaveState(ClearSaveStateEvent event, Emitter<IpState> emit) {
    emit(state.copyWith(clearSaveResponse: true, saveError: null));
  }

  void _onClearHealthState(ClearHealthStateEvent event, Emitter<IpState> emit) {
    emit(state.copyWith(clearHealthResponse: true, healthError: null));
  }
}
