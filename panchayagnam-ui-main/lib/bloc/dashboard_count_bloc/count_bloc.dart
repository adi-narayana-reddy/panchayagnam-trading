

import 'package:flutter_bloc/flutter_bloc.dart';

import '../../service/dashboard_count_service.dart';
import 'count_event.dart';
import 'count_state.dart';

class DashboardStatsBloc
    extends Bloc<DashboardCountEvent, DashboardCountState> {
  final DashboardCountRepository _repository;

  DashboardStatsBloc({DashboardCountRepository? repository})
      : _repository = repository ?? DashboardCountRepository(),
        super(const DashboardCountState()) {
    on<FetchDashboardStatsEvent>(_onFetch);
    on<RefreshDashboardStatsEvent>(_onRefresh);
  }

  Future<void> _onFetch(
      FetchDashboardStatsEvent event,
      Emitter<DashboardCountState> emit,
      ) async {
    if (state.isLoading) return;
    emit(state.copyWith(status: DashboardCountStatus.loading));
    try {
      final stats = await _repository.fetchDashboardStats(event.pyId);
      emit(state.copyWith(
        status: DashboardCountStatus.success,
        stats: stats,
        errorMessage: null,
      ));
    } catch (e) {
      emit(state.copyWith(
        status: DashboardCountStatus.failure,
        errorMessage: e.toString().replaceFirst('Exception: ', ''),
      ));
    }
  }

  Future<void> _onRefresh(
      RefreshDashboardStatsEvent event,
      Emitter<DashboardCountState> emit,
      ) async {
    try {
      final stats = await _repository.fetchDashboardStats(event.pyId);
      emit(state.copyWith(
        status: DashboardCountStatus.success,
        stats: stats,
        errorMessage: null,
      ));
    } catch (e) {
      emit(state.copyWith(
        status: DashboardCountStatus.failure,
        errorMessage: e.toString().replaceFirst('Exception: ', ''),
      ));
    }
  }
}