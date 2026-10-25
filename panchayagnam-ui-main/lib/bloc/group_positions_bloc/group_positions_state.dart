abstract class GroupPositionsState {}

class GroupPositionsInitial extends GroupPositionsState {}

class GroupPositionsLoading extends GroupPositionsState {}

class GroupPositionsLoaded extends GroupPositionsState {
  final List<Map<String, dynamic>> batches;
  final int groupId;
  GroupPositionsLoaded({required this.batches, required this.groupId});
}

class GroupPositionsOperationLoading extends GroupPositionsState {
  final List<Map<String, dynamic>> batches;
  final int groupId;
  GroupPositionsOperationLoading({required this.batches, required this.groupId});
}

class GroupPositionsOperationSuccess extends GroupPositionsState {
  final String message;
  final Map<String, dynamic>? resultData; // exit result details
  final List<Map<String, dynamic>> batches;
  final int groupId;

  GroupPositionsOperationSuccess({
    required this.message,
    this.resultData,
    required this.batches,
    required this.groupId,
  });
}

class GroupPositionsError extends GroupPositionsState {
  final String message;
  final List<Map<String, dynamic>> batches;
  final int groupId;

  GroupPositionsError({
    required this.message,
    required this.batches,
    required this.groupId,
  });
}