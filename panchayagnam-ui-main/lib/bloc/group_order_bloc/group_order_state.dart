abstract class TradeGroupState {}

class TradeGroupInitial extends TradeGroupState {}

class TradeGroupLoading extends TradeGroupState {}

class TradeGroupLoaded extends TradeGroupState {
  final List<Map<String, dynamic>> groups;
  TradeGroupLoaded(this.groups);
}

class TradeGroupOperationLoading extends TradeGroupState {
  final List<Map<String, dynamic>> groups;
  TradeGroupOperationLoading(this.groups);
}

class TradeGroupOperationSuccess extends TradeGroupState {
  final String message;
  final List<Map<String, dynamic>> groups;
  TradeGroupOperationSuccess({required this.message, required this.groups});
}

// NEW: emitted after FetchGroupById succeeds — carries fresh group + side
class TradeGroupFetchedForOrder extends TradeGroupState {
  final Map<String, dynamic> group;
  final String side;
  final List<Map<String, dynamic>> groups; // keep list intact
  TradeGroupFetchedForOrder({
    required this.group,
    required this.side,
    required this.groups,
  });
}

class TradeGroupError extends TradeGroupState {
  final String message;
  final List<Map<String, dynamic>> groups;
  TradeGroupError({required this.message, required this.groups});
}