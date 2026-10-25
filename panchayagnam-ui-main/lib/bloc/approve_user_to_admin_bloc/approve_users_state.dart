part of 'approve_users_bloc.dart';

abstract class ApproveUsersState {}

class ApproveUsersInitial extends ApproveUsersState {}

class ApproveUsersLoading extends ApproveUsersState {}

class ApproveUsersLoaded extends ApproveUsersState {
  final List<Map<String, dynamic>> pending;
  final List<Map<String, dynamic>> approved;
  final List<Map<String, dynamic>> rejected;
  final int totalCount;
  final int pendingCount;
  final int approvedCount;
  final int rejectedCount;

  ApproveUsersLoaded({
    required this.pending,
    required this.approved,
    required this.rejected,
    required this.totalCount,
    required this.pendingCount,
    required this.approvedCount,
    required this.rejectedCount,
  });
}

class ApproveUsersReviewing extends ApproveUsersState {
  final List<Map<String, dynamic>> pending;
  final List<Map<String, dynamic>> approved;
  final List<Map<String, dynamic>> rejected;
  final int totalCount;
  final int pendingCount;
  final int approvedCount;
  final int rejectedCount;

  ApproveUsersReviewing({
    required this.pending,
    required this.approved,
    required this.rejected,
    required this.totalCount,
    required this.pendingCount,
    required this.approvedCount,
    required this.rejectedCount,
  });
}

class ApproveUsersReviewSuccess extends ApproveUsersState {
  final List<Map<String, dynamic>> pending;
  final List<Map<String, dynamic>> approved;
  final List<Map<String, dynamic>> rejected;
  final int totalCount;
  final int pendingCount;
  final int approvedCount;
  final int rejectedCount;
  final String message;

  ApproveUsersReviewSuccess({
    required this.pending,
    required this.approved,
    required this.rejected,
    required this.totalCount,
    required this.pendingCount,
    required this.approvedCount,
    required this.rejectedCount,
    required this.message,
  });
}

class ApproveUsersError extends ApproveUsersState {
  final String message;
  final List<Map<String, dynamic>> pending;
  final List<Map<String, dynamic>> approved;
  final List<Map<String, dynamic>> rejected;

  ApproveUsersError(
      this.message, {
        this.pending = const [],
        this.approved = const [],
        this.rejected = const [],
      });
}