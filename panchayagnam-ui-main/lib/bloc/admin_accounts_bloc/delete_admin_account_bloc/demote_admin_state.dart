part of 'demote_admin_bloc.dart';

abstract class DemoteAdminState {}

class DemoteAdminInitial extends DemoteAdminState {}

class DemoteAdminLoading extends DemoteAdminState {
  final int targetPyId;
  DemoteAdminLoading(this.targetPyId);
}

class DemoteAdminSuccess extends DemoteAdminState {
  final int targetPyId;
  final String fullName;
  final String newRole;
  final int groupsAffected;
  final String message;

  DemoteAdminSuccess({
    required this.targetPyId,
    required this.fullName,
    required this.newRole,
    required this.groupsAffected,
    required this.message,
  });
}

class DemoteAdminError extends DemoteAdminState {
  final int targetPyId;
  final String message;
  DemoteAdminError({required this.targetPyId, required this.message});
}