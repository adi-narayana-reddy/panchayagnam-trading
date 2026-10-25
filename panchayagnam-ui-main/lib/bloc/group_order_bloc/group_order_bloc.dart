import 'package:flutter_bloc/flutter_bloc.dart';
import '../../service/group_order_service.dart';
import 'group_order_event.dart';
import 'group_order_state.dart';

class TradeGroupBloc extends Bloc<TradeGroupEvent, TradeGroupState> {
  final TradeGroupService _service;
  List<Map<String, dynamic>> _groups = [];

  TradeGroupBloc({TradeGroupService? service})
    : _service = service ?? TradeGroupService(),
      super(TradeGroupInitial()) {
    on<LoadTradeGroups>(_onLoad);
    on<FetchGroupById>(_onFetchGroupById); // NEW
    on<CreateTradeGroup>(_onCreate);
    on<UpdateTradeGroup>(_onUpdate);
    on<DeleteTradeGroup>(_onDelete);
    on<AddGroupMembers>(_onAddMembers);
    on<RemoveGroupMember>(_onRemoveMember);
  }

  // ── Load all ──────────────────────────────────────────────────────────────
  Future<void> _onLoad(
    LoadTradeGroups event,
    Emitter<TradeGroupState> emit,
  ) async {
    emit(TradeGroupLoading());
    try {
      _groups = await _service.fetchAllGroups(event.pyId);
      emit(TradeGroupLoaded(_groups));
    } catch (e) {
      emit(TradeGroupError(message: e.toString(), groups: _groups));
    }
  }

  // ── Fetch single group for order panel ───────────────────────────────────
  // Emits TradeGroupOperationLoading (shows spinner on the Buy/Sell button)
  // then TradeGroupFetchedForOrder on success so the UI can open the dialog.
  Future<void> _onFetchGroupById(
    FetchGroupById event,
    Emitter<TradeGroupState> emit,
  ) async {
    try {
      final freshGroup = await _service.fetchGroupById(event.groupId);
      emit(
        TradeGroupFetchedForOrder(
          group: freshGroup,
          side: event.side,
          groups: _groups,
        ),
      );
      // Immediately re-emit loaded so the table re-renders normally
      emit(TradeGroupLoaded(_groups));
    } catch (e) {
      emit(TradeGroupError(message: e.toString(), groups: _groups));
    }
  }

  // ── Create ────────────────────────────────────────────────────────────────
  Future<void> _onCreate(
    CreateTradeGroup event,
    Emitter<TradeGroupState> emit,
  ) async {
    emit(TradeGroupOperationLoading(_groups));
    try {
      final result = await _service.createGroup(
        pyId: event.pyId,
        groupName: event.groupName,
        description: event.description,
        brokerAccountIds: event.brokerAccountIds,
      );
      emit(
        TradeGroupOperationSuccess(
          message: result['message'] ?? 'Group created successfully',
          groups: _groups,
        ),
      );
    } catch (e) {
      emit(TradeGroupError(message: e.toString(), groups: _groups));
    }
  }

  // ── Update ────────────────────────────────────────────────────────────────
  Future<void> _onUpdate(
    UpdateTradeGroup event,
    Emitter<TradeGroupState> emit,
  ) async {
    emit(TradeGroupOperationLoading(_groups));
    try {
      final result = await _service.updateGroup(
        groupId: event.groupId,
        groupName: event.groupName,
        description: event.description,
        brokerAccountIds: event.brokerAccountIds,
      );

      emit(
        TradeGroupOperationSuccess(
          message: result['message'] ?? 'Group updated successfully',
          groups: _groups,
        ),
      );
    } catch (e) {
      emit(TradeGroupError(message: e.toString(), groups: _groups));
    }
  }

  // ── Delete ────────────────────────────────────────────────────────────────
  Future<void> _onDelete(
    DeleteTradeGroup event,
    Emitter<TradeGroupState> emit,
  ) async {
    emit(TradeGroupOperationLoading(_groups));
    try {
      final result = await _service.deleteGroup(event.groupId);
      _groups = _groups.where((g) => g['groupId'] != event.groupId).toList();
      emit(
        TradeGroupOperationSuccess(
          message: result['message'] ?? 'Group deleted successfully',
          groups: _groups,
        ),
      );
    } catch (e) {
      emit(TradeGroupError(message: e.toString(), groups: _groups));
    }
  }

  // ── Add Members ───────────────────────────────────────────────────────────
  Future<void> _onAddMembers(
    AddGroupMembers event,
    Emitter<TradeGroupState> emit,
  ) async {
    emit(TradeGroupOperationLoading(_groups));
    try {
      final result = await _service.addMembers(
        groupId: event.groupId,
        brokerAccountIds: event.brokerAccountIds,
      );
      final updated = Map<String, dynamic>.from(result['data']);
      _groups = _groups
          .map((g) => g['groupId'] == event.groupId ? updated : g)
          .toList();
      emit(
        TradeGroupOperationSuccess(
          message: result['message'] ?? 'Members added successfully',
          groups: _groups,
        ),
      );
    } catch (e) {
      emit(TradeGroupError(message: e.toString(), groups: _groups));
    }
  }

  // ── Remove Member ─────────────────────────────────────────────────────────
  Future<void> _onRemoveMember(
    RemoveGroupMember event,
    Emitter<TradeGroupState> emit,
  ) async {
    emit(TradeGroupOperationLoading(_groups));
    try {
      final result = await _service.removeMember(
        groupId: event.groupId,
        brokerAccountId: event.brokerAccountId,
      );
      _groups = _groups.map((g) {
        if (g['groupId'] != event.groupId) return g;
        final members = List<Map<String, dynamic>>.from(g['members'] ?? []);
        members.removeWhere(
          (m) => m['brokerAccountId'] == event.brokerAccountId,
        );
        return {...g, 'members': members, 'memberCount': members.length};
      }).toList();
      emit(
        TradeGroupOperationSuccess(
          message: result['message'] ?? 'Member removed successfully',
          groups: _groups,
        ),
      );
    } catch (e) {
      emit(TradeGroupError(message: e.toString(), groups: _groups));
    }
  }
}
