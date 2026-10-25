

import 'package:equatable/equatable.dart';

import '../../models/dashboard_count_states_model.dart';

enum DashboardCountStatus { initial, loading, success, failure }

class DashboardCountState extends Equatable {
  final DashboardCountStatus status;
  final DashboardCountModel? stats;
  final String? errorMessage;

  const DashboardCountState({
    this.status = DashboardCountStatus.initial,
    this.stats,
    this.errorMessage,
  });

  bool get isLoading => status == DashboardCountStatus.loading;
  bool get isSuccess => status == DashboardCountStatus.success;
  bool get isFailure => status == DashboardCountStatus.failure;

  DashboardCountState copyWith({
    DashboardCountStatus? status,
    DashboardCountModel? stats,
    String? errorMessage,
  }) {
    return DashboardCountState(
      status: status ?? this.status,
      stats: stats ?? this.stats,
      errorMessage: errorMessage ?? this.errorMessage,
    );
  }

  @override
  List<Object?> get props => [status, stats, errorMessage];
}