
abstract class TradeGroupEvent {}

// ── Fetch ─────────────────────────────────────────────────────────────────────
class LoadTradeGroups extends TradeGroupEvent {
  final int pyId;
  LoadTradeGroups(this.pyId);
}


class FetchGroupById extends TradeGroupEvent {
  final int groupId;
  final String side; // 'BUY' or 'SELL'
  FetchGroupById({required this.groupId, required this.side});
}

// ── Create ────────────────────────────────────────────────────────────────────
class CreateTradeGroup extends TradeGroupEvent {
  final int pyId;
  final String groupName;
  final String? description;
  final List<int>? brokerAccountIds;

  CreateTradeGroup({
    required this.pyId,
    required this.groupName,
    this.description,
    this.brokerAccountIds,
  });
}

// ── Update ────────────────────────────────────────────────────────────────────
class UpdateTradeGroup extends TradeGroupEvent {
  final int groupId;
  final String? groupName;
  final String? description;
  final List<int> brokerAccountIds;

  UpdateTradeGroup({
    required this.groupId,
    this.groupName,
    this.description,
    required this.brokerAccountIds
  });
}

// ── Delete ────────────────────────────────────────────────────────────────────
class DeleteTradeGroup extends TradeGroupEvent {
  final int groupId;
  DeleteTradeGroup(this.groupId);
}

// ── Add Members ───────────────────────────────────────────────────────────────
class AddGroupMembers extends TradeGroupEvent {
  final int groupId;
  final List<int> brokerAccountIds;
  AddGroupMembers({required this.groupId, required this.brokerAccountIds});
}

// ── Remove Member ─────────────────────────────────────────────────────────────
class RemoveGroupMember extends TradeGroupEvent {
  final int groupId;
  final int brokerAccountId;
  RemoveGroupMember({required this.groupId, required this.brokerAccountId});
}