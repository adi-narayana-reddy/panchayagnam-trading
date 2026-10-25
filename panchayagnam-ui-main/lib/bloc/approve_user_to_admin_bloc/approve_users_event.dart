part of 'approve_users_bloc.dart';

abstract class ApproveUsersEvent {}

class FetchPendingRequests extends ApproveUsersEvent {
  final String? client;
  FetchPendingRequests(this.client);
}

class ReviewRequest extends ApproveUsersEvent {
  final int superAdminId;
  final int requestId;
  final String status;
  final String? reason;

  ReviewRequest({
    required this.superAdminId,
    required this.requestId,
    required this.status,
    this.reason,
  });
}
