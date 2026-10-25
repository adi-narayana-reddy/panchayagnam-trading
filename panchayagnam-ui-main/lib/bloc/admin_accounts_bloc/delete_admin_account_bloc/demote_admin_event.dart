part of 'demote_admin_bloc.dart';

abstract class DemoteAdminEvent {}

class DemoteAdminUser extends DemoteAdminEvent {
  final int superAdminPyId;
  final int targetPyId;

  DemoteAdminUser({
    required this.superAdminPyId,
    required this.targetPyId,
  });
}