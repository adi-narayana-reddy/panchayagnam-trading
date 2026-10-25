
import 'package:equatable/equatable.dart';

abstract class DashboardCountEvent extends Equatable {
  const DashboardCountEvent();
  @override
  List<Object?> get props => [];
}

class FetchDashboardStatsEvent extends DashboardCountEvent {
  final int pyId;
  const FetchDashboardStatsEvent(this.pyId);
  @override
  List<Object?> get props => [pyId];
}

class RefreshDashboardStatsEvent extends DashboardCountEvent {
  final int pyId;
  const RefreshDashboardStatsEvent(this.pyId);
  @override
  List<Object?> get props => [pyId];
}