// ignore_for_file: library_private_types_in_public_api
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:intl/intl.dart';
import 'package:lottie/lottie.dart';
import 'dart:html' as html;

import '../../../bloc/group_order_bloc/group_order_bloc.dart';
import '../../../bloc/group_order_bloc/group_order_event.dart';
import '../../../bloc/group_order_bloc/group_order_state.dart';
import '../../../bloc/fetch_user_demat_bloc/fetch_user_demat_bloc.dart';
import '../../../bloc/group_order_buy_sell_bloc/group_order_buy_sell_bloc.dart';
import '../../../bloc/individual_bloc/individual_execution_bloc.dart';
import '../../../bloc/group_positions_bloc/group_positions_bloc.dart';
import '../../../bloc/group_positions_bloc/group_positions_event.dart';
import '../../../bloc/group_positions_bloc/group_positions_state.dart';
import '../../../constants/colors.dart';
import '../../../reusable_widets/group_order_panel.dart';

// ── Palette ───────────────────────────────────────────────────────────────────
const _kPrimary = Color(0xFF6D28D9);
const _kPrimaryLight = Color(0xFF7C3AED);
const _kPrimaryDark = Color(0xFF4C1D95);
const _kPrimaryBg = Color(0xFFF5F3FF);
const _kPrimaryBorder = Color(0xFFDDD6FE);
const _kSurface = Color(0xFFFFFFFF);
const _kBg = Color(0xFFFAFAFC);
const _kBorder = Color(0xFFE5E7EB);
const _kTextPrimary = Color(0xFF111827);
const _kTextSecondary = Color(0xFF6B7280);
const _kTextMuted = Color(0xFF9CA3AF);
const _kGreen = Color(0xFF059669);
const _kGreenBg = Color(0xFFECFDF5);
const _kGreenBorder = Color(0xFFA7F3D0);
const _kRed = Color(0xFFDC2626);
const _kRedBg = Color(0xFFFEF2F2);
const _kRedBorder = Color(0xFFFECACA);
const _kIndigo = Color(0xFF6366F1);
const _kOrange = Color(0xFFD97706);
const _kAmberBg = Color(0xFFFFFBEB);
const _kAmberBorder = Color(0xFFFDE68A);
const _kAmberText = Color(0xFF78350F);
const _kSlateBg = Color(0xFFF8FAFC);
const _kSlate = Color(0xFF475569);

// ── Helpers ───────────────────────────────────────────────────────────────────
bool _isSuccessMsg(String m) {
  final l = m.toLowerCase();
  return l.contains('success') ||
      l.contains('updated') ||
      l.contains('created') ||
      l.contains('deleted') ||
      l.contains('added') ||
      l.contains('removed');
}

String _clean(String m) =>
    m.startsWith('Exception: ') ? m.substring('Exception: '.length) : m;

String _fmtMoney(double v) {
  if (v >= 10000000) return '₹${(v / 10000000).toStringAsFixed(2)} Cr';
  if (v >= 100000) return '₹${(v / 100000).toStringAsFixed(2)} L';
  return '₹${v.toStringAsFixed(2)}';
}

String _fmtDate(String? iso) {
  if (iso == null) return '-';
  try {
    return DateFormat('dd MMM yyyy, hh:mm a').format(DateTime.parse(iso).toLocal());
  } catch (_) {
    return iso;
  }
}

String _fmtTimeOnly(String? iso) {
  if (iso == null) return '';
  try {
    return DateFormat('hh:mm a').format(DateTime.parse(iso).toLocal());
  } catch (_) {
    return '';
  }
}

String _fmtDateOnly(String? iso) {
  if (iso == null) return '-';
  try {
    return DateFormat('dd MMM yyyy').format(DateTime.parse(iso).toLocal());
  } catch (_) {
    return iso;
  }
}

Color _statusColor(String s) {
  switch (s.toUpperCase()) {
    case 'OPEN': return _kGreen;
    case 'CLOSED': return _kPrimary;
    case 'FAILED': return _kRed;
    case 'PARTIAL': return _kOrange;
    default: return _kTextSecondary;
  }
}

Color _statusBg(String s) {
  switch (s.toUpperCase()) {
    case 'OPEN': return _kGreenBg;
    case 'CLOSED': return _kPrimaryBg;
    case 'FAILED': return _kRedBg;
    case 'PARTIAL': return _kAmberBg;
    default: return _kBg;
  }
}

Color _statusBorder(String s) {
  switch (s.toUpperCase()) {
    case 'OPEN': return _kGreenBorder;
    case 'CLOSED': return _kPrimaryBorder;
    case 'FAILED': return _kRedBorder;
    case 'PARTIAL': return _kAmberBorder;
    default: return _kBorder;
  }
}

// ── Members builder ───────────────────────────────────────────────────────────
List<GroupMember> _buildGroupMembers(Map<String, dynamic> group) {
  final members = List<Map<String, dynamic>>.from(group['members'] ?? []);
  return members.map((m) {
    final name = (m['userName'] as String?) ?? 'User';
    final initials = name.length >= 2
        ? name.substring(0, 2).toUpperCase()
        : name.toUpperCase();
    return GroupMember(
      id: (m['brokerAccountId'] as int?) ?? (m['id'] as int?) ?? 0,
      initials: initials,
      name: name,
      clientId: (m['clientId'] as String?) ?? '-',
      brokerType: (m['brokerType'] as String?) ?? '-',
      cashBalance: (m['cashBalance'] as num?)?.toDouble() ?? 0.0,
      linked: (m['linked'] as bool?) ?? false,
      memberActive: (m['memberActive'] as bool?) ?? true,
    );
  }).toList();
}

// ═══════════════════════════════════════════════════════════════════════════════
//  ROOT TAB
// ═══════════════════════════════════════════════════════════════════════════════
class GroupOrdersTab extends StatefulWidget {
  const GroupOrdersTab({super.key});
  @override
  State<GroupOrdersTab> createState() => _GroupOrdersTabState();
}

class _GroupOrdersTabState extends State<GroupOrdersTab>
    with SingleTickerProviderStateMixin {
  late final TabController _tabCtrl;

  @override
  void initState() {
    super.initState();
    _tabCtrl = TabController(length: 2, vsync: this);
    _load();
  }

  @override
  void dispose() {
    _tabCtrl.dispose();
    super.dispose();
  }

  int get _pyId =>
      int.tryParse(html.window.sessionStorage['user_id'] ?? '') ?? 0;

  void _load() => context.read<TradeGroupBloc>().add(LoadTradeGroups(_pyId));

  void _showGroupOrderPanel(BuildContext context, Map<String, dynamic> group, String side) {
    context.read<TradeGroupBloc>().add(
      FetchGroupById(groupId: group['groupId'] as int, side: side),
    );
  }

  void _openOrderDialog(BuildContext context, Map<String, dynamic> group, String side) {
    final List<GroupMember> members = _buildGroupMembers(group);
    showDialog(
      context: context,
      barrierColor: Colors.black.withOpacity(0.55),
      builder: (_) => MultiBlocProvider(
        providers: [
          BlocProvider.value(value: context.read<GroupOrderBloc>()),
          BlocProvider.value(value: context.read<IndividualExecutionBloc>()),
        ],
        child: _GroupOrderDialog(group: group, members: members, initialSide: side),
      ),
    );
  }

  void _openAddGroupDialog() {
    context.read<FetchUserDematBloc>().add(LoadUserDematAccounts(_pyId));
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => MultiBlocProvider(
        providers: [
          BlocProvider.value(value: context.read<TradeGroupBloc>()),
          BlocProvider.value(value: context.read<FetchUserDematBloc>()),
        ],
        child: _AddGroupDialog(pyId: _pyId),
      ),
    );
  }

  void _openEditDialog(Map<String, dynamic> group) {
    context.read<FetchUserDematBloc>().add(LoadUserDematAccounts(_pyId));
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => MultiBlocProvider(
        providers: [
          BlocProvider.value(value: context.read<TradeGroupBloc>()),
          BlocProvider.value(value: context.read<FetchUserDematBloc>()),
        ],
        child: _EditGroupDialog(group: group),
      ),
    );
  }

  void _confirmDelete(Map<String, dynamic> group) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => BlocProvider.value(
        value: context.read<TradeGroupBloc>(),
        child: _DeleteGroupDialog(group: group),
      ),
    );
  }

  void _openGroupDetail(Map<String, dynamic> group) {
    showDialog(context: context, builder: (_) => _GroupDetailDialog(group: group));
  }

  @override
  Widget build(BuildContext context) {
    return BlocConsumer<TradeGroupBloc, TradeGroupState>(
      listener: (ctx, state) {
        if (state is TradeGroupFetchedForOrder) {
          _openOrderDialog(ctx, state.group, state.side);
        }
        if (state is TradeGroupOperationSuccess) {
          _load();
          _showSnack(ctx, _clean(state.message), success: true);
        }
        if (state is TradeGroupError) {
          if (_isSuccessMsg(state.message)) {
            _load();
            _showSnack(ctx, _clean(state.message), success: true);
          } else {
            _showSnack(ctx, _clean(state.message), success: false);
          }
        }
      },
      builder: (ctx, state) {
        final groups = _resolveGroups(state);
        final isLoading =
            state is TradeGroupLoading || state is TradeGroupOperationLoading;

        return Container(
          color: _kBg,
          child: Column(
            children: [
              Container(
                color: _kSurface,
                child: TabBar(
                  controller: _tabCtrl,
                  labelColor: _kPrimary,
                  unselectedLabelColor: _kTextSecondary,
                  indicatorColor: _kPrimary,
                  indicatorWeight: 2.5,
                  labelStyle: const TextStyle(fontSize: 13, fontWeight: FontWeight.w700),
                  unselectedLabelStyle:
                  const TextStyle(fontSize: 13, fontWeight: FontWeight.w500),
                  tabs: const [
                    Tab(
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(Icons.group_work_rounded, size: 16),
                          SizedBox(width: 6),
                          Text('Groups'),
                        ],
                      ),
                    ),
                    Tab(
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(Icons.history_rounded, size: 16),
                          SizedBox(width: 6),
                          Text('History'),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
              Expanded(
                child: TabBarView(
                  controller: _tabCtrl,
                  children: [
                    SingleChildScrollView(
                      padding: const EdgeInsets.fromLTRB(28, 24, 28, 32),
                      child: Column(
                        children: [_buildGroupsCard(ctx, groups, state, isLoading)],
                      ),
                    ),
                    BlocProvider.value(
                      value: context.read<GroupPositionsBloc>(),
                      child: _HistoryTab(groups: groups, pyId: _pyId),
                    ),
                  ],
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  List<Map<String, dynamic>> _resolveGroups(TradeGroupState state) {
    if (state is TradeGroupLoaded) return state.groups;
    if (state is TradeGroupOperationLoading) return state.groups;
    if (state is TradeGroupOperationSuccess) return state.groups;
    if (state is TradeGroupFetchedForOrder) return state.groups;
    if (state is TradeGroupError) return state.groups;
    return [];
  }

  Widget _buildGroupsCard(
      BuildContext context,
      List<Map<String, dynamic>> groups,
      TradeGroupState state,
      bool isLoading,
      ) {
    final active = groups.where((g) => g['isActive'] == true).toList();
    return Container(
      width: double.infinity,
      decoration: BoxDecoration(
        color: _kSurface,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: _kBorder),
        boxShadow: [
          BoxShadow(color: Colors.black.withOpacity(0.04), blurRadius: 16, offset: const Offset(0, 4)),
        ],
      ),
      child: Column(
        children: [
          _CardHeader(groupCount: active.length, onAddGroup: _openAddGroupDialog),
          if (isLoading)
            const _LoadingBody()
          else if (state is TradeGroupError && active.isEmpty)
            _ErrorBody(message: state.message, onRetry: _load)
          else if (active.isEmpty)
              const _EmptyBody()
            else
              _GroupTable(
                groups: active,
                onEdit: _openEditDialog,
                onDelete: _confirmDelete,
                onView: _openGroupDetail,
                onTrade: (g, side) => _showGroupOrderPanel(context, g, side),
              ),
        ],
      ),
    );
  }

  void _showSnack(BuildContext ctx, String msg, {required bool success}) {
    ScaffoldMessenger.of(ctx)
      ..clearSnackBars()
      ..showSnackBar(
        SnackBar(
          content: Row(
            children: [
              Container(
                width: 28,
                height: 28,
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.2),
                  borderRadius: BorderRadius.circular(6),
                ),
                child: Icon(
                  success ? Icons.check_rounded : Icons.error_outline_rounded,
                  color: Colors.white,
                  size: 16,
                ),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: Text(msg,
                    style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w500)),
              ),
            ],
          ),
          backgroundColor: success ? _kGreen : _kRed,
          behavior: SnackBarBehavior.floating,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
          margin: const EdgeInsets.all(16),
          duration: const Duration(seconds: 3),
        ),
      );
  }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  HISTORY TAB
// ═══════════════════════════════════════════════════════════════════════════════
class _HistoryTab extends StatefulWidget {
  final List<Map<String, dynamic>> groups;
  final int pyId;
  const _HistoryTab({required this.groups, required this.pyId});
  @override
  State<_HistoryTab> createState() => _HistoryTabState();
}

class _HistoryTabState extends State<_HistoryTab> {
  int? _selectedGroupId;
  String? _selectedGroupName;

  @override
  void initState() {
    super.initState();
    final active = widget.groups.where((g) => g['isActive'] == true).toList();
    if (active.isNotEmpty) {
      _selectedGroupId = active.first['groupId'] as int?;
      _selectedGroupName = active.first['groupName'] as String?;
      WidgetsBinding.instance.addPostFrameCallback((_) {
        if (_selectedGroupId != null) {
          context.read<GroupPositionsBloc>().add(LoadGroupPositions(_selectedGroupId!));
        }
      });
    }
  }

  void _selectGroup(Map<String, dynamic> g) {
    setState(() {
      _selectedGroupId = g['groupId'] as int?;
      _selectedGroupName = g['groupName'] as String?;
    });
    if (_selectedGroupId != null) {
      context.read<GroupPositionsBloc>().add(LoadGroupPositions(_selectedGroupId!));
    }
  }

  void _refresh() {
    if (_selectedGroupId != null) {
      context.read<GroupPositionsBloc>().add(LoadGroupPositions(_selectedGroupId!));
    }
  }

  @override
  Widget build(BuildContext context) {
    final active = widget.groups.where((g) => g['isActive'] == true).toList();

    return BlocConsumer<GroupPositionsBloc, GroupPositionsState>(
      listener: (ctx, state) {
        if (state is GroupPositionsOperationSuccess) {
          if (state.resultData != null) {
            _showExitResultDialog(ctx, state.resultData!);
          } else {
            _showSnack(ctx, _clean(state.message), success: true);
          }
        }
        if (state is GroupPositionsError) {
          _showSnack(ctx, _clean(state.message), success: false);
        }
      },
      builder: (ctx, state) {
        final batches = _resolveBatches(state);
        final isLoading =
            state is GroupPositionsLoading || state is GroupPositionsOperationLoading;

        return SingleChildScrollView(
          padding: const EdgeInsets.fromLTRB(28, 24, 28, 32),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              _buildGroupSelector(active),
              const SizedBox(height: 16),
              Container(
                width: double.infinity,
                decoration: BoxDecoration(
                  color: _kSurface,
                  borderRadius: BorderRadius.circular(16),
                  border: Border.all(color: _kBorder),
                  boxShadow: [
                    BoxShadow(
                        color: Colors.black.withOpacity(0.04),
                        blurRadius: 16,
                        offset: const Offset(0, 4)),
                  ],
                ),
                child: Column(
                  children: [
                    _HistoryHeader(
                      groupName: _selectedGroupName ?? 'Select a group',
                      batchCount: batches.length,
                      isLoading: isLoading,
                      onRefresh: _refresh,
                    ),
                    if (_selectedGroupId == null)
                      const _NoBatchSelected()
                    else if (isLoading)
                      const _LoadingBody()
                    else if (state is GroupPositionsError && batches.isEmpty)
                        _ErrorBody(message: state.message, onRetry: _refresh)
                      else if (batches.isEmpty)
                          const _EmptyBatchBody()
                        else
                          _BatchList(
                            batches: batches,
                            groupId: _selectedGroupId!,
                            onExitAll: (batchId, orderType, productType, triggerPrice) {
                              context.read<GroupPositionsBloc>().add(
                                FullGroupExit(
                                  groupOrderBatchId: batchId,
                                  orderType: orderType,
                                  productType: productType,
                                  groupId: _selectedGroupId ?? 0,
                                  triggerPrice: triggerPrice,
                                ),
                              );
                            },
                            onExitSelective: (batchId, brokerIds, ot, pt, triggerPrice) {
                              context.read<GroupPositionsBloc>().add(
                                SelectiveGroupExit(
                                  groupOrderBatchId: batchId,
                                  brokerAccountIds: brokerIds,
                                  orderType: ot,
                                  productType: pt,
                                  groupId: _selectedGroupId ?? 0,
                                  triggerPrice: triggerPrice,
                                ),
                              );
                            },
                            onBuyOneSellOne: (payload) {
                              context.read<GroupPositionsBloc>().add(
                                BuyOneSellOne(
                                  groupId: _selectedGroupId!,
                                  brokerAccountId: payload['brokerAccountId'],
                                  tradingSymbol: payload['tradingSymbol'],
                                  symbolToken: payload['symbolToken'],
                                  exchange: payload['exchange'],
                                  quantity: payload['quantity'],
                                  price: payload['price'],
                                  orderType: payload['orderType'],
                                  productType: payload['productType'],
                                ),
                              );
                            },
                            onSellOneBuyOne: (payload) {
                              context.read<GroupPositionsBloc>().add(
                                SellOneBuyOne(
                                  groupId: _selectedGroupId!,
                                  brokerAccountId: payload['brokerAccountId'],
                                  tradingSymbol: payload['tradingSymbol'],
                                  symbolToken: payload['symbolToken'],
                                  exchange: payload['exchange'],
                                  quantity: payload['quantity'],
                                  price: payload['price'],
                                  orderType: payload['orderType'],
                                  productType: payload['productType'],
                                ),
                              );
                            },
                          ),
                  ],
                ),
              ),
            ],
          ),
        );
      },
    );
  }

  List<Map<String, dynamic>> _resolveBatches(GroupPositionsState state) {
    if (state is GroupPositionsLoaded) return state.batches;
    if (state is GroupPositionsOperationLoading) return state.batches;
    if (state is GroupPositionsOperationSuccess) return state.batches;
    if (state is GroupPositionsError) return state.batches;
    return [];
  }

  Widget _buildGroupSelector(List<Map<String, dynamic>> groups) {
    return Container(
      padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
      decoration: BoxDecoration(
        color: _kSurface,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: _kBorder),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('Select Group',
              style: TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: _kTextPrimary)),
          const SizedBox(height: 10),
          if (groups.isEmpty)
            const Text('No active groups',
                style: TextStyle(fontSize: 12, color: _kTextMuted))
          else
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: groups.map((g) {
                final isSelected = (g['groupId'] as int?) == _selectedGroupId;
                return InkWell(
                  onTap: () => _selectGroup(g),
                  borderRadius: BorderRadius.circular(8),
                  child: AnimatedContainer(
                    duration: const Duration(milliseconds: 150),
                    padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 7),
                    decoration: BoxDecoration(
                      color: isSelected ? _kPrimary : _kBg,
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(color: isSelected ? _kPrimary : _kBorder),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Icon(Icons.group_work_rounded,
                            size: 13,
                            color: isSelected ? Colors.white : _kTextSecondary),
                        const SizedBox(width: 6),
                        Text(
                          g['groupName'] ?? '-',
                          style: TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            color: isSelected ? Colors.white : _kTextPrimary,
                          ),
                        ),
                        const SizedBox(width: 5),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 5, vertical: 1),
                          decoration: BoxDecoration(
                            color: isSelected
                                ? Colors.white.withOpacity(0.2)
                                : _kPrimaryBg,
                            borderRadius: BorderRadius.circular(4),
                          ),
                          child: Text(
                            '${g['memberCount'] ?? 0}',
                            style: TextStyle(
                              fontSize: 9,
                              fontWeight: FontWeight.w700,
                              color: isSelected ? Colors.white : _kPrimary,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                );
              }).toList(),
            ),
        ],
      ),
    );
  }

  void _showExitResultDialog(BuildContext ctx, Map<String, dynamic> result) {
    showDialog(context: ctx, builder: (_) => _ExitResultDialog(result: result));
  }

  void _showSnack(BuildContext ctx, String msg, {required bool success}) {
    ScaffoldMessenger.of(ctx)
      ..clearSnackBars()
      ..showSnackBar(
        SnackBar(
          content: Text(msg,
              style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w500)),
          backgroundColor: success ? _kGreen : _kRed,
          behavior: SnackBarBehavior.floating,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
          margin: const EdgeInsets.all(16),
          duration: const Duration(seconds: 3),
        ),
      );
  }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  HISTORY HEADER
// ═══════════════════════════════════════════════════════════════════════════════
class _HistoryHeader extends StatelessWidget {
  final String groupName;
  final int batchCount;
  final bool isLoading;
  final VoidCallback onRefresh;

  const _HistoryHeader({
    required this.groupName,
    required this.batchCount,
    required this.isLoading,
    required this.onRefresh,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.fromLTRB(20, 14, 16, 14),
      decoration: const BoxDecoration(border: Border(bottom: BorderSide(color: _kBorder))),
      child: Row(
        children: [
          const Icon(Icons.history_rounded, size: 16, color: _kPrimary),
          const SizedBox(width: 8),
          Text(groupName,
              style: const TextStyle(
                  fontSize: 14, fontWeight: FontWeight.w700, color: _kTextPrimary)),
          const SizedBox(width: 10),
          if (batchCount > 0)
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 9, vertical: 3),
              decoration: BoxDecoration(
                color: _kPrimaryBg,
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: _kPrimaryBorder),
              ),
              child: Text(
                '$batchCount batch${batchCount == 1 ? '' : 'es'}',
                style: const TextStyle(
                    fontSize: 11, fontWeight: FontWeight.w600, color: _kPrimary),
              ),
            ),
          const Spacer(),
          InkWell(
            onTap: isLoading ? null : onRefresh,
            borderRadius: BorderRadius.circular(8),
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 7),
              decoration: BoxDecoration(
                color: _kBg,
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: _kBorder),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  isLoading
                      ? const SizedBox(
                      width: 12,
                      height: 12,
                      child: CircularProgressIndicator(
                          strokeWidth: 1.5, color: _kPrimary))
                      : const Icon(Icons.refresh_rounded, size: 14, color: _kPrimary),
                  const SizedBox(width: 5),
                  const Text('Refresh',
                      style: TextStyle(
                          fontSize: 12, fontWeight: FontWeight.w600, color: _kPrimary)),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  BATCH LIST
// ═══════════════════════════════════════════════════════════════════════════════
class _BatchList extends StatelessWidget {
  final List<Map<String, dynamic>> batches;
  final int groupId;
  final void Function(String batchId, String orderType, String productType, int triggerPrice)
  onExitAll;
  final void Function(
      String batchId, List<int> brokerIds, String ot, String pt, int triggerPrice)
  onExitSelective;
  final void Function(Map<String, dynamic> payload) onBuyOneSellOne;
  final void Function(Map<String, dynamic> payload) onSellOneBuyOne;

  const _BatchList({
    required this.batches,
    required this.groupId,
    required this.onExitAll,
    required this.onExitSelective,
    required this.onBuyOneSellOne,
    required this.onSellOneBuyOne,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Column(
        children: batches.asMap().entries.map((entry) {
          final index = entry.key;
          final batch = entry.value;
          return Padding(
            padding: const EdgeInsets.only(bottom: 16),
            child: _BatchCard(
              batch: batch,
              batchNumber: batches.length - index, // newest = highest number
              groupId: groupId,
              onExitAll: onExitAll,
              onExitSelective: onExitSelective,
              onBuyOneSellOne: onBuyOneSellOne,
              onSellOneBuyOne: onSellOneBuyOne,
            ),
          );
        }).toList(),
      ),
    );
  }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  BATCH CARD
// ═══════════════════════════════════════════════════════════════════════════════
class _BatchCard extends StatefulWidget {
  final Map<String, dynamic> batch;
  final int batchNumber;
  final int groupId;
  final void Function(String batchId, String orderType, String productType, int triggerPrice)
  onExitAll;
  final void Function(
      String batchId, List<int> brokerIds, String ot, String pt, int triggerPrice)
  onExitSelective;
  final void Function(Map<String, dynamic> payload) onBuyOneSellOne;
  final void Function(Map<String, dynamic> payload) onSellOneBuyOne;

  const _BatchCard({
    required this.batch,
    required this.batchNumber,
    required this.groupId,
    required this.onExitAll,
    required this.onExitSelective,
    required this.onBuyOneSellOne,
    required this.onSellOneBuyOne,
  });

  @override
  State<_BatchCard> createState() => _BatchCardState();
}

class _BatchCardState extends State<_BatchCard> {
  bool _expanded = false;

  // ── Derive the dominant productType from all open brokers in this batch ──
  String _detectProductType() {
    final stocks = List<Map<String, dynamic>>.from(widget.batch['stocks'] ?? []);
    for (final stock in stocks) {
      final brokers = List<Map<String, dynamic>>.from(stock['brokers'] ?? []);
      for (final broker in brokers) {
        if ((broker['status'] as String?)?.toUpperCase() == 'OPEN') {
          final pt = broker['productType'] as String?;
          if (pt != null && pt.isNotEmpty) return pt;
        }
      }
    }
    return 'INTRADAY';
  }

  @override
  Widget build(BuildContext context) {
    final batch = widget.batch;
    final batchId = (batch['groupOrderBatchId'] as String?) ?? '-';
    final batchDate = batch['batchDate'] as String?;
    final dateOnly = _fmtDateOnly(batchDate);
    final timeOnly = _fmtTimeOnly(batchDate);
    final totalStocks = (batch['totalStocks'] as int?) ?? 0;
    final totalBrokers = (batch['totalBrokers'] as int?) ?? 0;
    final openCount = (batch['openCount'] as int?) ?? 0;
    final closedCount = (batch['closedCount'] as int?) ?? 0;
    final failedCount = (batch['failedCount'] as int?) ?? 0;
    final partialCount = (batch['partialCount'] as int?) ?? 0;
    final stocks = List<Map<String, dynamic>>.from(batch['stocks'] ?? []);
    final hasOpen = openCount > 0;
    final productType = _detectProductType();

    return Container(
      decoration: BoxDecoration(
        color: _kSurface,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: _kBorder),
        boxShadow: [
          BoxShadow(
              color: Colors.black.withOpacity(0.03),
              blurRadius: 8,
              offset: const Offset(0, 2)),
        ],
      ),
      child: Column(
        children: [
          // ── Batch header ─────────────────────────────────────────────
          InkWell(
            onTap: () => setState(() => _expanded = !_expanded),
            borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
            child: Padding(
              padding: const EdgeInsets.fromLTRB(16, 14, 16, 14),
              child: Column(
                children: [
                  Row(
                    children: [
                      // Sequential batch number badge
                      Container(
                        width: 42,
                        height: 42,
                        decoration: BoxDecoration(
                          gradient: const LinearGradient(
                            colors: [_kPrimaryLight, _kPrimaryDark],
                            begin: Alignment.topLeft,
                            end: Alignment.bottomRight,
                          ),
                          borderRadius: BorderRadius.circular(10),
                        ),
                        child: Center(
                          child: Text(
                            '#${widget.batchNumber}',
                            style: const TextStyle(
                              fontSize: 13,
                              fontWeight: FontWeight.w800,
                              color: Colors.white,
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Row(
                              children: [
                                Text(
                                  'Batch #${widget.batchNumber}',
                                  style: const TextStyle(
                                    fontSize: 14,
                                    fontWeight: FontWeight.w700,
                                    color: _kTextPrimary,
                                  ),
                                ),
                                const SizedBox(width: 8),
                                // productType pill
                                Container(
                                  padding: const EdgeInsets.symmetric(
                                      horizontal: 7, vertical: 2),
                                  decoration: BoxDecoration(
                                    color: _kPrimaryBg,
                                    borderRadius: BorderRadius.circular(4),
                                    border: Border.all(color: _kPrimaryBorder),
                                  ),
                                  child: Text(
                                    productType,
                                    style: const TextStyle(
                                      fontSize: 9,
                                      fontWeight: FontWeight.w700,
                                      color: _kPrimary,
                                    ),
                                  ),
                                ),
                              ],
                            ),
                            const SizedBox(height: 4),
                            Row(
                              children: [
                                // Date
                                Text(
                                  dateOnly,
                                  style: const TextStyle(
                                    fontSize: 11,
                                    color: _kTextSecondary,
                                    fontWeight: FontWeight.w500,
                                  ),
                                ),
                                if (timeOnly.isNotEmpty) ...[
                                  const SizedBox(width: 6),
                                  Container(
                                    padding: const EdgeInsets.symmetric(
                                        horizontal: 7, vertical: 2),
                                    decoration: BoxDecoration(
                                      color: const Color(0xFFF0FDF4),
                                      borderRadius: BorderRadius.circular(5),
                                      border: Border.all(
                                          color: const Color(0xFFBBF7D0)),
                                    ),
                                    child: Row(
                                      mainAxisSize: MainAxisSize.min,
                                      children: [
                                        const Icon(Icons.access_time_rounded,
                                            size: 10, color: _kGreen),
                                        const SizedBox(width: 3),
                                        Text(
                                          timeOnly,
                                          style: const TextStyle(
                                            fontSize: 10,
                                            fontWeight: FontWeight.w700,
                                            color: _kGreen,
                                          ),
                                        ),
                                      ],
                                    ),
                                  ),
                                ],
                              ],
                            ),
                          ],
                        ),
                      ),
                      AnimatedRotation(
                        turns: _expanded ? 0.5 : 0,
                        duration: const Duration(milliseconds: 200),
                        child: const Icon(Icons.keyboard_arrow_down_rounded,
                            size: 18, color: _kTextSecondary),
                      ),
                    ],
                  ),
                  const SizedBox(height: 12),
                  // ── Stats row ──────────────────────────────────────
                  Row(
                    children: [
                      _BatchStat(label: 'Stocks', value: '$totalStocks', color: _kPrimary),
                      _BatchStat(label: 'Brokers', value: '$totalBrokers', color: _kSlate),
                      _BatchStat(label: 'Open', value: '$openCount', color: _kGreen),
                      _BatchStat(label: 'Closed', value: '$closedCount', color: _kIndigo),
                      _BatchStat(label: 'Failed', value: '$failedCount', color: _kRed),
                      if (partialCount > 0)
                        _BatchStat(
                            label: 'Partial', value: '$partialCount', color: _kOrange),
                    ],
                  ),
                ],
              ),
            ),
          ),

          // ── Exit actions ─────────────────────────────────────────────
          if (hasOpen) ...[
            Container(
              padding: const EdgeInsets.fromLTRB(16, 10, 16, 10),
              decoration: const BoxDecoration(
                color: Color(0xFFFAFAFC),
                border: Border(top: BorderSide(color: _kBorder)),
              ),
              child: Row(
                children: [
                  const Icon(Icons.info_outline_rounded, size: 12, color: _kTextMuted),
                  const SizedBox(width: 6),
                  Text(
                    '$openCount open position${openCount == 1 ? '' : 's'}',
                    style: const TextStyle(fontSize: 11, color: _kTextSecondary),
                  ),
                  const Spacer(),
                  _ExitButton(
                    label: 'Exit All',
                    color: _kRed,
                    icon: Icons.logout_rounded,
                    onTap: () => _showExitDialog(
                      context,
                      batchId: batchId,
                      isSelective: false,
                      brokers: [],
                      productType: productType,
                    ),
                  ),
                ],
              ),
            ),
          ],

          // ── Expanded stocks ───────────────────────────────────────────
          AnimatedSize(
            duration: const Duration(milliseconds: 250),
            curve: Curves.easeInOut,
            child: _expanded
                ? Column(
              children: [
                const Divider(height: 1, color: _kBorder),
                ...stocks.map(
                      (stock) => _StockSection(
                    stock: stock,
                    onBuyOneSellOne: widget.onBuyOneSellOne,
                    onSellOneBuyOne: widget.onSellOneBuyOne,
                    onExitSelective: (brokerIds, ot, pt, tp) =>
                        widget.onExitSelective(batchId, brokerIds, ot, pt, tp),
                  ),
                ),
              ],
            )
                : const SizedBox.shrink(),
          ),
        ],
      ),
    );
  }

  void _showExitDialog(
      BuildContext context, {
        required String batchId,
        required bool isSelective,
        required List<int> brokers,
        required String productType,
        String? brokerProductType,
      }) {
    showDialog(
      context: context,
      builder: (_) => _ExitConfirmDialog(
        batchId: batchId,
        isSelective: isSelective,
        brokerIds: brokers,
        lockedProductType: brokerProductType ?? productType,
        onConfirm: (ot, pt, tp) {
          if (isSelective) {
            widget.onExitSelective(batchId, brokers, ot, pt, tp);
          } else {
            widget.onExitAll(batchId, ot, pt, tp);
          }
        },
      ),
    );
  }
}

// ─── Batch stat pill ──────────────────────────────────────────────────────────
class _BatchStat extends StatelessWidget {
  final String label, value;
  final Color color;
  const _BatchStat({required this.label, required this.value, required this.color});

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Column(
        children: [
          Text(value,
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.w800, color: color)),
          const SizedBox(height: 2),
          Text(label,
              style: const TextStyle(
                  fontSize: 9, color: _kTextMuted, fontWeight: FontWeight.w500)),
        ],
      ),
    );
  }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  STOCK SECTION
// ═══════════════════════════════════════════════════════════════════════════════
class _StockSection extends StatefulWidget {
  final Map<String, dynamic> stock;
  final void Function(Map<String, dynamic>) onBuyOneSellOne;
  final void Function(Map<String, dynamic>) onSellOneBuyOne;
  final void Function(List<int> brokerIds, String ot, String pt, int tp) onExitSelective;

  const _StockSection({
    required this.stock,
    required this.onBuyOneSellOne,
    required this.onSellOneBuyOne,
    required this.onExitSelective,
  });

  @override
  State<_StockSection> createState() => _StockSectionState();
}

class _StockSectionState extends State<_StockSection> {
  bool _expanded = true;

  @override
  Widget build(BuildContext context) {
    final stock = widget.stock;
    final symbol = (stock['tradingSymbol'] as String?) ?? '-';
    final exchange = (stock['exchange'] as String?) ?? 'NSE';
    final brokers = List<Map<String, dynamic>>.from(stock['brokers'] ?? []);
    final openCount = (stock['openCount'] as int?) ?? 0;
    final failedCount = (stock['failedCount'] as int?) ?? 0;
    final closedCount = (stock['closedCount'] as int?) ?? 0;
    final partialCount = (stock['partialCount'] as int?) ?? 0;

    return Column(
      children: [
        InkWell(
          onTap: () => setState(() => _expanded = !_expanded),
          child: Container(
            color: const Color(0xFFFAFBFF),
            padding: const EdgeInsets.fromLTRB(16, 11, 16, 11),
            child: Row(
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 9, vertical: 4),
                  decoration: BoxDecoration(
                    color: _kPrimaryBg,
                    borderRadius: BorderRadius.circular(6),
                    border: Border.all(color: _kPrimaryBorder),
                  ),
                  child: Text(
                    symbol.replaceAll('-EQ', ''),
                    style: const TextStyle(
                        fontSize: 12, fontWeight: FontWeight.w700, color: _kPrimary),
                  ),
                ),
                const SizedBox(width: 8),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 3),
                  decoration: BoxDecoration(
                    color: _kBg,
                    borderRadius: BorderRadius.circular(4),
                    border: Border.all(color: _kBorder),
                  ),
                  child: Text(exchange,
                      style: const TextStyle(
                          fontSize: 9,
                          fontWeight: FontWeight.w700,
                          color: _kTextSecondary)),
                ),
                const SizedBox(width: 10),
                if (openCount > 0) _StatusPill(status: 'OPEN', count: openCount),
                if (failedCount > 0) ...[
                  const SizedBox(width: 5),
                  _StatusPill(status: 'FAILED', count: failedCount),
                ],
                if (closedCount > 0) ...[
                  const SizedBox(width: 5),
                  _StatusPill(status: 'CLOSED', count: closedCount),
                ],
                if (partialCount > 0) ...[
                  const SizedBox(width: 5),
                  _StatusPill(status: 'PARTIAL', count: partialCount),
                ],
                const Spacer(),
                AnimatedRotation(
                  turns: _expanded ? 0.5 : 0,
                  duration: const Duration(milliseconds: 200),
                  child: const Icon(Icons.keyboard_arrow_down_rounded,
                      size: 16, color: _kTextSecondary),
                ),
              ],
            ),
          ),
        ),
        AnimatedSize(
          duration: const Duration(milliseconds: 220),
          curve: Curves.easeInOut,
          child: _expanded
              ? Column(
            children: brokers.asMap().entries.map((e) {
              final isLast = e.key == brokers.length - 1;
              return Column(
                children: [
                  const Divider(height: 1, color: _kBorder),
                  _BrokerPositionRow(
                    broker: e.value,
                    stock: stock,
                    onBuyOneSellOne: widget.onBuyOneSellOne,
                    onSellOneBuyOne: widget.onSellOneBuyOne,
                    onExitSelective: (ot, pt, tp) => widget.onExitSelective(
                      [(e.value['brokerId'] as int?) ?? 0],
                      ot,
                      pt,
                      tp,
                    ),
                  ),
                  if (isLast) const Divider(height: 1, color: _kBorder),
                ],
              );
            }).toList(),
          )
              : const SizedBox.shrink(),
        ),
      ],
    );
  }
}

// ─── Status pill ──────────────────────────────────────────────────────────────
class _StatusPill extends StatelessWidget {
  final String status;
  final int count;
  const _StatusPill({required this.status, required this.count});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 2),
      decoration: BoxDecoration(
        color: _statusBg(status),
        borderRadius: BorderRadius.circular(4),
        border: Border.all(color: _statusBorder(status)),
      ),
      child: Text('$count $status',
          style: TextStyle(
              fontSize: 9, fontWeight: FontWeight.w700, color: _statusColor(status))),
    );
  }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  BROKER POSITION ROW
// ═══════════════════════════════════════════════════════════════════════════════
class _BrokerPositionRow extends StatelessWidget {
  final Map<String, dynamic> broker;
  final Map<String, dynamic> stock;
  final void Function(Map<String, dynamic>) onBuyOneSellOne;
  final void Function(Map<String, dynamic>) onSellOneBuyOne;
  final void Function(String ot, String pt, int tp) onExitSelective;

  const _BrokerPositionRow({
    required this.broker,
    required this.stock,
    required this.onBuyOneSellOne,
    required this.onSellOneBuyOne,
    required this.onExitSelective,
  });

  @override
  Widget build(BuildContext context) {
    final userName = (broker['userName'] as String?) ?? '-';
    final clientId = (broker['clientId'] as String?) ?? '-';
    final brokerType = (broker['brokerType'] as String?) ?? '-';
    final status = (broker['status'] as String?) ?? '-';
    final buyQty = (broker['buyQuantity'] as int?) ?? 0;
    final buyPrice = (broker['buyPrice'] as num?)?.toDouble() ?? 0.0;
    final sellQty = (broker['sellQuantity'] as int?) ?? 0;
    final sellPrice = (broker['sellPrice'] as num?)?.toDouble() ?? 0.0;
    final buyOrderId = broker['buyOrderId'] as String?;
    final sellOrderId = broker['sellOrderId'] as String?;
    final remarks = broker['remarks'] as String?;
    final boughtAt = _fmtDate(broker['boughtAt'] as String?);
    final soldAt = broker['soldAt'] as String?;
    final isOpen = status.toUpperCase() == 'OPEN';
    final isFailed = status.toUpperCase() == 'FAILED';
    final productType = (broker['productType'] as String?) ?? 'INTRADAY';

    final initials = userName.length >= 2
        ? userName.substring(0, 2).toUpperCase()
        : userName.toUpperCase();

    return Container(
      padding: const EdgeInsets.fromLTRB(16, 12, 16, 12),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CircleAvatar(
            radius: 16,
            backgroundColor: _kPrimaryBg,
            child: Text(initials,
                style: const TextStyle(
                    fontSize: 10, fontWeight: FontWeight.w700, color: _kPrimary)),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Expanded(
                      child: Text(userName,
                          style: const TextStyle(
                              fontSize: 12,
                              fontWeight: FontWeight.w600,
                              color: _kTextPrimary),
                          overflow: TextOverflow.ellipsis),
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 7, vertical: 3),
                      decoration: BoxDecoration(
                        color: _statusBg(status),
                        borderRadius: BorderRadius.circular(5),
                        border: Border.all(color: _statusBorder(status)),
                      ),
                      child: Text(status,
                          style: TextStyle(
                              fontSize: 9,
                              fontWeight: FontWeight.w700,
                              color: _statusColor(status))),
                    ),
                  ],
                ),
                const SizedBox(height: 3),
                Text('$clientId · $brokerType',
                    style: const TextStyle(fontSize: 10, color: _kTextSecondary)),
                const SizedBox(height: 6),
                if (buyQty > 0)
                  _TradeInfoRow(
                    icon: Icons.trending_up_rounded,
                    color: _kGreen,
                    label: 'BUY',
                    qty: buyQty,
                    price: buyPrice,
                    orderId: buyOrderId,
                    time: boughtAt,
                  ),
                if (sellQty > 0) ...[
                  const SizedBox(height: 4),
                  _TradeInfoRow(
                    icon: Icons.trending_down_rounded,
                    color: _kRed,
                    label: 'SELL',
                    qty: sellQty,
                    price: sellPrice,
                    orderId: sellOrderId,
                    time: soldAt != null ? _fmtDate(soldAt) : null,
                  ),
                ],
                if (remarks != null && remarks.isNotEmpty) ...[
                  const SizedBox(height: 6),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 5),
                    decoration: BoxDecoration(
                      color: _kAmberBg,
                      borderRadius: BorderRadius.circular(6),
                      border: Border.all(color: _kAmberBorder),
                    ),
                    child: Row(
                      children: [
                        const Icon(Icons.warning_amber_rounded,
                            size: 11, color: _kOrange),
                        const SizedBox(width: 5),
                        Expanded(
                          child: Text(remarks,
                              style: const TextStyle(fontSize: 10, color: _kAmberText)),
                        ),
                      ],
                    ),
                  ),
                ],
                if (isOpen || isFailed) ...[
                  const SizedBox(height: 8),
                  _BrokerActionButtons(
                    broker: broker,
                    stock: stock,
                    isOpen: isOpen,
                    productType: productType,
                    onBuyOneSellOne: onBuyOneSellOne,
                    onSellOneBuyOne: onSellOneBuyOne,
                    onExitOne: onExitSelective,
                  ),
                ],
              ],
            ),
          ),
        ],
      ),
    );
  }
}

// ─── Trade info row ───────────────────────────────────────────────────────────
class _TradeInfoRow extends StatelessWidget {
  final IconData icon;
  final Color color;
  final String label;
  final int qty;
  final double price;
  final String? orderId;
  final String? time;

  const _TradeInfoRow({
    required this.icon,
    required this.color,
    required this.label,
    required this.qty,
    required this.price,
    this.orderId,
    this.time,
  });

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
          decoration: BoxDecoration(
            color: color.withOpacity(0.08),
            borderRadius: BorderRadius.circular(4),
            border: Border.all(color: color.withOpacity(0.25)),
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(icon, size: 10, color: color),
              const SizedBox(width: 3),
              Text(label,
                  style: TextStyle(
                      fontSize: 9, fontWeight: FontWeight.w700, color: color)),
            ],
          ),
        ),
        const SizedBox(width: 8),
        Text('$qty @ ₹${price.toStringAsFixed(2)}',
            style: const TextStyle(
                fontSize: 11, fontWeight: FontWeight.w600, color: _kTextPrimary)),
      ],
    );
  }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  BROKER ACTION BUTTONS
// ═══════════════════════════════════════════════════════════════════════════════
class _BrokerActionButtons extends StatelessWidget {
  final Map<String, dynamic> broker;
  final Map<String, dynamic> stock;
  final bool isOpen;
  final String productType;
  final void Function(Map<String, dynamic>) onBuyOneSellOne;
  final void Function(Map<String, dynamic>) onSellOneBuyOne;
  final void Function(String ot, String pt, int tp) onExitOne;

  const _BrokerActionButtons({
    required this.broker,
    required this.stock,
    required this.isOpen,
    required this.productType,
    required this.onBuyOneSellOne,
    required this.onSellOneBuyOne,
    required this.onExitOne,
  });

  void _openExitOne(BuildContext ctx) {
    showDialog(
      context: ctx,
      builder: (_) => _ExitConfirmDialog(
        batchId: '',
        isSelective: true,
        brokerIds: [(broker['brokerId'] as int?) ?? 0],
        lockedProductType: productType,
        onConfirm: onExitOne,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Wrap(
      spacing: 6,
      runSpacing: 6,
      children: [
        if (isOpen)
          _ActionChip(
            label: 'Exit One',
            icon: Icons.logout_rounded,
            color: _kRed,
            tooltip: 'Exit position for this broker only',
            onTap: () => _openExitOne(context),
          ),
      ],
    );
  }
}

// ─── Action chip ──────────────────────────────────────────────────────────────
class _ActionChip extends StatefulWidget {
  final String label;
  final IconData icon;
  final Color color;
  final String tooltip;
  final VoidCallback onTap;

  const _ActionChip({
    required this.label,
    required this.icon,
    required this.color,
    required this.tooltip,
    required this.onTap,
  });

  @override
  State<_ActionChip> createState() => _ActionChipState();
}

class _ActionChipState extends State<_ActionChip> {
  bool _hovered = false;

  @override
  Widget build(BuildContext context) {
    return Tooltip(
      message: widget.tooltip,
      preferBelow: false,
      child: MouseRegion(
        cursor: SystemMouseCursors.click,
        onEnter: (_) => setState(() => _hovered = true),
        onExit: (_) => setState(() => _hovered = false),
        child: GestureDetector(
          onTap: widget.onTap,
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 130),
            height: 26,
            padding: const EdgeInsets.symmetric(horizontal: 8),
            decoration: BoxDecoration(
              color: _hovered ? widget.color : widget.color.withOpacity(0.08),
              borderRadius: BorderRadius.circular(6),
              border: Border.all(
                  color: widget.color.withOpacity(_hovered ? 1.0 : 0.3)),
            ),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(widget.icon,
                    size: 11, color: _hovered ? Colors.white : widget.color),
                const SizedBox(width: 4),
                Text(widget.label,
                    style: TextStyle(
                        fontSize: 10,
                        fontWeight: FontWeight.w600,
                        color: _hovered ? Colors.white : widget.color)),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  EXIT CONFIRM DIALOG  — Professional UI with locked productType,
//                         auto-flipped transactionType, trigger price field
// ═══════════════════════════════════════════════════════════════════════════════
class _ExitConfirmDialog extends StatefulWidget {
  final String batchId;
  final bool isSelective;
  final List<int> brokerIds;
  final String lockedProductType; // pre-filled, read-only
  final void Function(String orderType, String productType, int triggerPrice) onConfirm;

  const _ExitConfirmDialog({
    required this.batchId,
    required this.isSelective,
    required this.brokerIds,
    required this.lockedProductType,
    required this.onConfirm,
  });

  @override
  State<_ExitConfirmDialog> createState() => _ExitConfirmDialogState();
}

class _ExitConfirmDialogState extends State<_ExitConfirmDialog>
    with SingleTickerProviderStateMixin {
  late AnimationController _ctrl;
  late Animation<double> _scale, _fade;

  // MARKET or LIMIT only for exit
  String _orderType = 'MARKET';
  final _triggerPriceCtrl = TextEditingController();
  final _limitPriceCtrl = TextEditingController();

  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 260));
    _scale = CurvedAnimation(parent: _ctrl, curve: Curves.easeOutBack);
    _fade = CurvedAnimation(parent: _ctrl, curve: Curves.easeIn);
    _ctrl.forward();
  }

  @override
  void dispose() {
    _ctrl.dispose();
    _triggerPriceCtrl.dispose();
    _limitPriceCtrl.dispose();
    super.dispose();
  }

  bool get _isMarket => _orderType == 'MARKET';
  bool get _isLimit => _orderType == 'LIMIT';

  String _productTypeLabel(String pt) {
    switch (pt.toUpperCase()) {
      case 'INTRADAY': return 'Intraday (MIS)';
      case 'DELIVERY': return 'Delivery (CNC)';
      case 'MARGIN': return 'Margin (NRML)';
      default: return pt;
    }
  }

  Color _productTypeColor(String pt) {
    switch (pt.toUpperCase()) {
      case 'INTRADAY': return _kIndigo;
      case 'DELIVERY': return _kGreen;
      case 'MARGIN': return _kOrange;
      default: return _kPrimary;
    }
  }

  Color _productTypeBg(String pt) {
    switch (pt.toUpperCase()) {
      case 'INTRADAY': return const Color(0xFFEEF2FF);
      case 'DELIVERY': return _kGreenBg;
      case 'MARGIN': return _kAmberBg;
      default: return _kPrimaryBg;
    }
  }

  void _submit() {
    int triggerPrice = 0;
    if (_isLimit) {
      final lp = double.tryParse(_limitPriceCtrl.text.trim()) ?? 0.0;
      if (lp <= 0) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
              content: Text('Enter a valid limit price'),
              backgroundColor: _kRed),
        );
        return;
      }
      triggerPrice = lp.toInt();
    }
    Navigator.pop(context);
    widget.onConfirm(_orderType, widget.lockedProductType, triggerPrice);
  }

  @override
  Widget build(BuildContext context) {
    final title = widget.isSelective
        ? 'Exit Position${widget.brokerIds.length > 1 ? 's' : ''}'
        : 'Exit All Positions';
    final subtitle = widget.isSelective
        ? '${widget.brokerIds.length} broker${widget.brokerIds.length > 1 ? 's' : ''} selected'
        : 'All open positions in this batch';

    final ptColor = _productTypeColor(widget.lockedProductType);
    final ptBg = _productTypeBg(widget.lockedProductType);
    final ptLabel = _productTypeLabel(widget.lockedProductType);

    return FadeTransition(
      opacity: _fade,
      child: ScaleTransition(
        scale: _scale,
        child: Dialog(
          backgroundColor: Colors.transparent,
          elevation: 0,
          child: Container(
            width: 460,
            decoration: BoxDecoration(
              color: _kSurface,
              borderRadius: BorderRadius.circular(20),
              boxShadow: [
                BoxShadow(
                    color: Colors.black.withOpacity(0.18),
                    blurRadius: 40,
                    offset: const Offset(0, 16)),
              ],
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                // ── Header ─────────────────────────────────────────────
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.fromLTRB(24, 24, 24, 20),
                  decoration: const BoxDecoration(
                    gradient: LinearGradient(
                      colors: [Color(0xFF7F1D1D), Color(0xFFB91C1C)],
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                    ),
                    borderRadius: BorderRadius.only(
                      topLeft: Radius.circular(20),
                      topRight: Radius.circular(20),
                    ),
                  ),
                  child: Row(
                    children: [
                      Container(
                        width: 48,
                        height: 48,
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.15),
                          borderRadius: BorderRadius.circular(14),
                        ),
                        child: const Icon(Icons.logout_rounded,
                            color: Colors.white, size: 22),
                      ),
                      const SizedBox(width: 14),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(title,
                                style: const TextStyle(
                                    fontSize: 17,
                                    fontWeight: FontWeight.w700,
                                    color: Colors.white)),
                            const SizedBox(height: 3),
                            Text(subtitle,
                                style: TextStyle(
                                    fontSize: 11,
                                    color: Colors.white.withOpacity(0.72))),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),

                // ── Body ───────────────────────────────────────────────
                Padding(
                  padding: const EdgeInsets.all(22),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [

                      // ── Product type (locked, read-only) ─────────────
                      const Text('Product Type',
                          style: TextStyle(
                              fontSize: 11,
                              fontWeight: FontWeight.w700,
                              color: _kTextPrimary)),
                      const SizedBox(height: 8),
                      Container(
                        width: double.infinity,
                        padding: const EdgeInsets.symmetric(
                            horizontal: 14, vertical: 12),
                        decoration: BoxDecoration(
                          color: ptBg,
                          borderRadius: BorderRadius.circular(10),
                          border: Border.all(color: ptColor.withOpacity(0.3)),
                        ),
                        child: Row(
                          children: [
                            Container(
                              padding: const EdgeInsets.all(6),
                              decoration: BoxDecoration(
                                color: ptColor.withOpacity(0.12),
                                borderRadius: BorderRadius.circular(7),
                              ),
                              child: Icon(Icons.lock_outline_rounded,
                                  size: 14, color: ptColor),
                            ),
                            const SizedBox(width: 10),
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(ptLabel,
                                    style: TextStyle(
                                        fontSize: 13,
                                        fontWeight: FontWeight.w700,
                                        color: ptColor)),
                                Text('Pre-set from your open position',
                                    style: TextStyle(
                                        fontSize: 10,
                                        color: ptColor.withOpacity(0.7))),
                              ],
                            ),
                            const Spacer(),
                            Container(
                              padding: const EdgeInsets.symmetric(
                                  horizontal: 8, vertical: 3),
                              decoration: BoxDecoration(
                                color: ptColor.withOpacity(0.12),
                                borderRadius: BorderRadius.circular(6),
                              ),
                              child: Text(
                                widget.lockedProductType,
                                style: TextStyle(
                                    fontSize: 10,
                                    fontWeight: FontWeight.w800,
                                    color: ptColor),
                              ),
                            ),
                          ],
                        ),
                      ),

                      const SizedBox(height: 20),

                      // ── Order type selector ───────────────────────────
                      const Text('Order Type',
                          style: TextStyle(
                              fontSize: 11,
                              fontWeight: FontWeight.w700,
                              color: _kTextPrimary)),
                      const SizedBox(height: 8),
                      Row(
                        children: [
                          Expanded(
                            child: _OrderTypeCard(
                              label: 'MARKET',
                              subtitle: 'Execute at best price',
                              icon: Icons.flash_on_rounded,
                              color: _kGreen,
                              isSelected: _isMarket,
                              onTap: () => setState(() => _orderType = 'MARKET'),
                            ),
                          ),
                          const SizedBox(width: 10),
                          Expanded(
                            child: _OrderTypeCard(
                              label: 'LIMIT',
                              subtitle: 'Set your exit price',
                              icon: Icons.tune_rounded,
                              color: _kIndigo,
                              isSelected: _isLimit,
                              onTap: () => setState(() => _orderType = 'LIMIT'),
                            ),
                          ),
                        ],
                      ),

                      // ── Limit price (visible only for LIMIT) ──────────
                      AnimatedSize(
                        duration: const Duration(milliseconds: 220),
                        curve: Curves.easeInOut,
                        child: _isLimit
                            ? Padding(
                          padding: const EdgeInsets.only(top: 16),
                          child: _DialogField(
                            ctrl: _limitPriceCtrl,
                            label: 'Limit Price (₹)',
                            hint: '0.00',
                            icon: Icons.currency_rupee_rounded,
                            isDecimal: true,
                            accentColor: _kIndigo,
                          ),
                        )
                            : const SizedBox.shrink(),
                      ),

                      const SizedBox(height: 18),

                      // ── Warning banner ────────────────────────────────
                      Container(
                        padding: const EdgeInsets.all(13),
                        decoration: BoxDecoration(
                          color: _kAmberBg,
                          borderRadius: BorderRadius.circular(10),
                          border: Border.all(color: _kAmberBorder),
                        ),
                        child: Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Container(
                              padding: const EdgeInsets.all(4),
                              decoration: BoxDecoration(
                                color: _kOrange.withOpacity(0.15),
                                borderRadius: BorderRadius.circular(6),
                              ),
                              child: const Icon(Icons.warning_amber_rounded,
                                  size: 14, color: _kOrange),
                            ),
                            const SizedBox(width: 10),
                            Expanded(
                              child: Text(
                                widget.isSelective
                                    ? 'SELL order will be placed for ${widget.brokerIds.length} broker${widget.brokerIds.length > 1 ? 's' : ''}. This cannot be undone.'
                                    : 'SELL orders will be placed for ALL open positions. This cannot be undone.',
                                style: const TextStyle(
                                    fontSize: 11,
                                    color: _kAmberText,
                                    height: 1.45),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),

                // ── Footer ─────────────────────────────────────────────
                Container(
                  padding: const EdgeInsets.fromLTRB(22, 12, 22, 20),
                  decoration: const BoxDecoration(
                    border: Border(top: BorderSide(color: _kBorder)),
                  ),
                  child: Row(
                    children: [
                      Expanded(
                        child: OutlinedButton(
                          onPressed: () => Navigator.pop(context),
                          style: OutlinedButton.styleFrom(
                            foregroundColor: _kTextSecondary,
                            side: const BorderSide(color: _kBorder),
                            padding: const EdgeInsets.symmetric(vertical: 13),
                            shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(10)),
                          ),
                          child: const Text('Cancel',
                              style: TextStyle(
                                  fontSize: 13, fontWeight: FontWeight.w600)),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        flex: 2,
                        child: ElevatedButton(
                          onPressed: _submit,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: _kRed,
                            foregroundColor: Colors.white,
                            padding: const EdgeInsets.symmetric(vertical: 13),
                            shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(10)),
                            elevation: 0,
                          ),
                          child: Row(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              const Icon(Icons.logout_rounded, size: 15),
                              const SizedBox(width: 7),
                              Text(
                                'Confirm Exit · ${_isMarket ? 'MARKET' : 'LIMIT'}',
                                style: const TextStyle(
                                    fontSize: 13, fontWeight: FontWeight.w700),
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

// ─── Order type selection card ────────────────────────────────────────────────
class _OrderTypeCard extends StatelessWidget {
  final String label;
  final String subtitle;
  final IconData icon;
  final Color color;
  final bool isSelected;
  final VoidCallback onTap;

  const _OrderTypeCard({
    required this.label,
    required this.subtitle,
    required this.icon,
    required this.color,
    required this.isSelected,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(10),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 160),
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
        decoration: BoxDecoration(
          color: isSelected ? color.withOpacity(0.08) : _kBg,
          borderRadius: BorderRadius.circular(10),
          border: Border.all(
            color: isSelected ? color : _kBorder,
            width: isSelected ? 1.8 : 1.0,
          ),
        ),
        child: Row(
          children: [
            Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(
                color: isSelected ? color.withOpacity(0.12) : const Color(0xFFF3F4F6),
                borderRadius: BorderRadius.circular(8),
              ),
              child: Icon(icon,
                  size: 16, color: isSelected ? color : _kTextMuted),
            ),
            const SizedBox(width: 10),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(label,
                      style: TextStyle(
                          fontSize: 12,
                          fontWeight: FontWeight.w700,
                          color: isSelected ? color : _kTextPrimary)),
                  const SizedBox(height: 2),
                  Text(subtitle,
                      style: const TextStyle(
                          fontSize: 9, color: _kTextMuted),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis),
                ],
              ),
            ),
            if (isSelected)
              Icon(Icons.check_circle_rounded, size: 16, color: color),
          ],
        ),
      ),
    );
  }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  SINGLE ORDER DIALOG  (unchanged logic, kept for completeness)
// ═══════════════════════════════════════════════════════════════════════════════
enum _SingleOrderMode { buyOneSellOne, sellOneBuyOne }

class _SingleOrderDialog extends StatefulWidget {
  final Map<String, dynamic> broker;
  final Map<String, dynamic> stock;
  final _SingleOrderMode mode;
  final void Function(Map<String, dynamic> payload) onConfirm;

  const _SingleOrderDialog({
    required this.broker,
    required this.stock,
    required this.mode,
    required this.onConfirm,
  });

  @override
  State<_SingleOrderDialog> createState() => _SingleOrderDialogState();
}

class _SingleOrderDialogState extends State<_SingleOrderDialog>
    with SingleTickerProviderStateMixin {
  late AnimationController _ctrl;
  late Animation<double> _scale, _fade;
  final _qtyCtrl = TextEditingController();
  final _priceCtrl = TextEditingController();
  String _orderType = 'LIMIT';
  String _productType = 'INTRADAY';
  String _variety = 'NORMAL';

  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 260));
    _scale = CurvedAnimation(parent: _ctrl, curve: Curves.easeOutBack);
    _fade = CurvedAnimation(parent: _ctrl, curve: Curves.easeIn);
    _ctrl.forward();
    final buyQty = (widget.broker['buyQuantity'] as int?) ?? 1;
    _qtyCtrl.text = '$buyQty';
    final buyPrice = (widget.broker['buyPrice'] as num?)?.toDouble() ?? 0.0;
    if (buyPrice > 0) _priceCtrl.text = buyPrice.toStringAsFixed(2);
    final pt = (widget.broker['productType'] as String?) ?? 'INTRADAY';
    _productType = pt == 'MARGIN' ? 'MARGIN' : pt;
  }

  @override
  void dispose() {
    _ctrl.dispose();
    _qtyCtrl.dispose();
    _priceCtrl.dispose();
    super.dispose();
  }

  bool get _isBOSO => widget.mode == _SingleOrderMode.buyOneSellOne;
  Color get _accentColor => _isBOSO ? _kGreen : _kIndigo;
  String get _modeTitle => _isBOSO ? 'Buy One · Sell One' : 'Sell One · Buy One';
  String get _modeSubtitle => _isBOSO
      ? 'Place a SELL order for this broker\'s open BUY position'
      : 'Place a fresh BUY order for this broker';
  String get _actionLabel => _isBOSO ? 'Place SELL' : 'Place BUY';

  void _submit() {
    final qty = int.tryParse(_qtyCtrl.text.trim()) ?? 0;
    final price = double.tryParse(_priceCtrl.text.trim()) ?? 0.0;
    if (qty <= 0) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
            content: Text('Enter a valid quantity'), backgroundColor: _kRed),
      );
      return;
    }
    widget.onConfirm({
      'brokerAccountId': (widget.broker['brokerId'] as int?) ?? 0,
      'tradingSymbol': (widget.stock['tradingSymbol'] as String?) ?? '',
      'symbolToken': (widget.stock['symbolToken'] as String?) ?? '',
      'exchange': (widget.stock['exchange'] as String?) ?? 'NSE',
      'quantity': qty,
      'price': _orderType == 'MARKET' ? 0.0 : price,
      'orderType': _orderType,
      'productType': _productType,
      'variety': _variety,
    });
  }

  @override
  Widget build(BuildContext context) {
    final symbol =
        (widget.stock['tradingSymbol'] as String?)?.replaceAll('-EQ', '') ?? '-';
    final userName = (widget.broker['userName'] as String?) ?? '-';
    final clientId = (widget.broker['clientId'] as String?) ?? '-';
    final brokerType = (widget.broker['brokerType'] as String?) ?? '-';
    final buyQty = (widget.broker['buyQuantity'] as int?) ?? 0;
    final buyPrice = (widget.broker['buyPrice'] as num?)?.toDouble() ?? 0.0;

    return FadeTransition(
      opacity: _fade,
      child: ScaleTransition(
        scale: _scale,
        child: Dialog(
          backgroundColor: Colors.transparent,
          elevation: 0,
          child: Container(
            width: 480,
            constraints: const BoxConstraints(maxHeight: 620),
            decoration: BoxDecoration(
              color: _kSurface,
              borderRadius: BorderRadius.circular(18),
              boxShadow: [
                BoxShadow(
                    color: Colors.black.withOpacity(0.16),
                    blurRadius: 40,
                    offset: const Offset(0, 16)),
              ],
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.symmetric(vertical: 22),
                  decoration: BoxDecoration(
                    gradient: LinearGradient(
                      colors: _isBOSO
                          ? [const Color(0xFF065F46), const Color(0xFF047857)]
                          : [const Color(0xFF1E40AF), const Color(0xFF3B82F6)],
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                    ),
                    borderRadius: const BorderRadius.only(
                      topLeft: Radius.circular(18),
                      topRight: Radius.circular(18),
                    ),
                  ),
                  child: Column(
                    children: [
                      Container(
                        width: 46,
                        height: 46,
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.2),
                          shape: BoxShape.circle,
                        ),
                        child: Icon(
                          _isBOSO
                              ? Icons.call_made_rounded
                              : Icons.call_received_rounded,
                          color: Colors.white,
                          size: 20,
                        ),
                      ),
                      const SizedBox(height: 10),
                      Text(_modeTitle,
                          style: const TextStyle(
                              fontSize: 17,
                              fontWeight: FontWeight.w700,
                              color: Colors.white)),
                      const SizedBox(height: 4),
                      Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 24),
                        child: Text(_modeSubtitle,
                            textAlign: TextAlign.center,
                            style: TextStyle(
                                fontSize: 11,
                                color: Colors.white.withOpacity(0.75))),
                      ),
                    ],
                  ),
                ),
                Flexible(
                  child: SingleChildScrollView(
                    padding: const EdgeInsets.all(22),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Container(
                          padding: const EdgeInsets.all(14),
                          decoration: BoxDecoration(
                            color: _kBg,
                            borderRadius: BorderRadius.circular(10),
                            border: Border.all(color: _kBorder),
                          ),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              const Text('Position Info',
                                  style: TextStyle(
                                      fontSize: 11,
                                      fontWeight: FontWeight.w700,
                                      color: _kTextSecondary)),
                              const SizedBox(height: 10),
                              _InfoRow(label: 'Broker', value: '$userName ($clientId)'),
                              _InfoRow(label: 'Platform', value: brokerType),
                              _InfoRow(label: 'Stock', value: symbol),
                              _InfoRow(
                                  label: 'Open BUY',
                                  value: '$buyQty @ ₹${buyPrice.toStringAsFixed(2)}'),
                            ],
                          ),
                        ),
                        const SizedBox(height: 18),
                        const Text('Order Type',
                            style: TextStyle(
                                fontSize: 12,
                                fontWeight: FontWeight.w700,
                                color: _kTextPrimary)),
                        const SizedBox(height: 8),
                        _SegRow(
                          options: const ['LIMIT', 'MARKET'],
                          selected: _orderType,
                          accentColor: _accentColor,
                          onSelect: (v) => setState(() => _orderType = v),
                        ),
                        const SizedBox(height: 16),
                        const Text('Product Type',
                            style: TextStyle(
                                fontSize: 12,
                                fontWeight: FontWeight.w700,
                                color: _kTextPrimary)),
                        const SizedBox(height: 8),
                        _SegRow(
                          options: const ['INTRADAY', 'DELIVERY', 'MARGIN'],
                          selected: _productType,
                          accentColor: _accentColor,
                          onSelect: (v) => setState(() => _productType = v),
                        ),
                        const SizedBox(height: 16),
                        Row(
                          children: [
                            Expanded(
                              child: _DialogField(
                                ctrl: _qtyCtrl,
                                label: 'Quantity',
                                hint: 'e.g. 100',
                                icon: Icons.format_list_numbered_rounded,
                                isNumber: true,
                                accentColor: _accentColor,
                              ),
                            ),
                            const SizedBox(width: 14),
                            Expanded(
                              child: AnimatedOpacity(
                                opacity: _orderType == 'MARKET' ? 0.4 : 1.0,
                                duration: const Duration(milliseconds: 200),
                                child: _DialogField(
                                  ctrl: _priceCtrl,
                                  label: 'Price (₹)',
                                  hint: '0.00',
                                  icon: Icons.currency_rupee_rounded,
                                  isDecimal: true,
                                  enabled: _orderType != 'MARKET',
                                  accentColor: _accentColor,
                                ),
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 16),
                        const Text('Variety',
                            style: TextStyle(
                                fontSize: 12,
                                fontWeight: FontWeight.w700,
                                color: _kTextPrimary)),
                        const SizedBox(height: 8),
                        _SegRow(
                          options: const ['NORMAL', 'STOPLOSS', 'ROBO', 'GTT'],
                          selected: _variety,
                          accentColor: _accentColor,
                          onSelect: (v) => setState(() => _variety = v),
                        ),
                      ],
                    ),
                  ),
                ),
                Container(
                  padding: const EdgeInsets.fromLTRB(22, 12, 22, 20),
                  decoration:
                  const BoxDecoration(border: Border(top: BorderSide(color: _kBorder))),
                  child: Row(
                    children: [
                      Expanded(
                        child: OutlinedButton(
                          onPressed: () => Navigator.pop(context),
                          style: OutlinedButton.styleFrom(
                            foregroundColor: _kTextSecondary,
                            side: const BorderSide(color: _kBorder),
                            padding: const EdgeInsets.symmetric(vertical: 12),
                            shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(9)),
                          ),
                          child: const Text('Cancel',
                              style: TextStyle(
                                  fontSize: 13, fontWeight: FontWeight.w600)),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: ElevatedButton(
                          onPressed: _submit,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: _accentColor,
                            foregroundColor: Colors.white,
                            padding: const EdgeInsets.symmetric(vertical: 12),
                            shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(9)),
                            elevation: 0,
                          ),
                          child: Text(_actionLabel,
                              style: const TextStyle(
                                  fontSize: 13, fontWeight: FontWeight.w700)),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

// ─── Info row ──────────────────────────────────────────────────────────────────
class _InfoRow extends StatelessWidget {
  final String label, value;
  const _InfoRow({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 3),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label,
              style: const TextStyle(fontSize: 11, color: _kTextSecondary)),
          Text(value,
              style: const TextStyle(
                  fontSize: 11, fontWeight: FontWeight.w600, color: _kTextPrimary)),
        ],
      ),
    );
  }
}

// ─── Segmented row selector ───────────────────────────────────────────────────
class _SegRow extends StatelessWidget {
  final List<String> options;
  final String selected;
  final Color accentColor;
  final ValueChanged<String> onSelect;

  const _SegRow({
    required this.options,
    required this.selected,
    required this.accentColor,
    required this.onSelect,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
          color: _kBg,
          borderRadius: BorderRadius.circular(8),
          border: Border.all(color: _kBorder)),
      padding: const EdgeInsets.all(3),
      child: Row(
        children: options.map((opt) {
          final isSelected = opt == selected;
          return Expanded(
            child: InkWell(
              onTap: () => onSelect(opt),
              borderRadius: BorderRadius.circular(6),
              child: AnimatedContainer(
                duration: const Duration(milliseconds: 140),
                padding: const EdgeInsets.symmetric(vertical: 7),
                decoration: BoxDecoration(
                  color: isSelected ? accentColor : Colors.transparent,
                  borderRadius: BorderRadius.circular(6),
                ),
                child: Text(opt,
                    textAlign: TextAlign.center,
                    style: TextStyle(
                        fontSize: 10,
                        fontWeight: FontWeight.w700,
                        color: isSelected ? Colors.white : _kTextSecondary,
                        letterSpacing: 0.2)),
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}

// ─── Dialog text field ────────────────────────────────────────────────────────
class _DialogField extends StatelessWidget {
  final TextEditingController ctrl;
  final String label, hint;
  final IconData icon;
  final bool isNumber;
  final bool isDecimal;
  final bool enabled;
  final Color accentColor;

  const _DialogField({
    required this.ctrl,
    required this.label,
    required this.hint,
    required this.icon,
    required this.accentColor,
    this.isNumber = false,
    this.isDecimal = false,
    this.enabled = true,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label,
            style: const TextStyle(
                fontSize: 11, fontWeight: FontWeight.w600, color: _kTextPrimary)),
        const SizedBox(height: 6),
        TextField(
          controller: ctrl,
          enabled: enabled,
          keyboardType: isDecimal
              ? const TextInputType.numberWithOptions(decimal: true)
              : isNumber
              ? TextInputType.number
              : TextInputType.text,
          inputFormatters: isDecimal
              ? [FilteringTextInputFormatter.allow(RegExp(r'^\d*\.?\d*'))]
              : isNumber
              ? [FilteringTextInputFormatter.digitsOnly]
              : null,
          style: const TextStyle(fontSize: 13, color: _kTextPrimary),
          decoration: InputDecoration(
            hintText: hint,
            hintStyle: const TextStyle(fontSize: 12, color: _kTextMuted),
            prefixIcon: Icon(icon, size: 15, color: _kTextMuted),
            filled: true,
            fillColor: enabled ? const Color(0xFFF9FAFB) : _kBg,
            contentPadding:
            const EdgeInsets.symmetric(horizontal: 12, vertical: 11),
            border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8),
                borderSide: const BorderSide(color: _kBorder)),
            enabledBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8),
                borderSide: const BorderSide(color: _kBorder)),
            focusedBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8),
                borderSide: BorderSide(color: accentColor, width: 1.5)),
            disabledBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8),
                borderSide: const BorderSide(color: _kBorder)),
          ),
        ),
      ],
    );
  }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  EXIT RESULT DIALOG
// ═══════════════════════════════════════════════════════════════════════════════
class _ExitResultDialog extends StatelessWidget {
  final Map<String, dynamic> result;
  const _ExitResultDialog({required this.result});

  @override
  Widget build(BuildContext context) {
    final totalOrders = (result['totalOrders'] as int?) ?? 0;
    final successCount = (result['successCount'] as int?) ?? 0;
    final failedCount = (result['failedCount'] as int?) ?? 0;
    final skippedCount = (result['skippedCount'] as int?) ?? 0;
    final results = List<Map<String, dynamic>>.from(result['results'] ?? []);

    return Dialog(
      backgroundColor: Colors.transparent,
      child: Container(
        width: 540,
        constraints: const BoxConstraints(maxHeight: 600),
        decoration: BoxDecoration(
          color: _kSurface,
          borderRadius: BorderRadius.circular(16),
          boxShadow: [
            BoxShadow(
                color: Colors.black.withOpacity(0.14),
                blurRadius: 32,
                offset: const Offset(0, 10)),
          ],
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(vertical: 20),
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  colors: failedCount == 0
                      ? [const Color(0xFF065F46), const Color(0xFF059669)]
                      : [const Color(0xFF1E40AF), const Color(0xFF3B82F6)],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                borderRadius: const BorderRadius.only(
                  topLeft: Radius.circular(16),
                  topRight: Radius.circular(16),
                ),
              ),
              child: Column(
                children: [
                  Icon(
                    failedCount == 0
                        ? Icons.check_circle_outline_rounded
                        : Icons.info_outline_rounded,
                    color: Colors.white,
                    size: 32,
                  ),
                  const SizedBox(height: 10),
                  const Text('Exit Result',
                      style: TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.w700,
                          color: Colors.white)),
                  const SizedBox(height: 8),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      _ResultBadge(
                          label: 'Total', value: '$totalOrders', color: Colors.white),
                      const SizedBox(width: 8),
                      _ResultBadge(
                          label: 'Success',
                          value: '$successCount',
                          color: const Color(0xFF34D399)),
                      const SizedBox(width: 8),
                      _ResultBadge(
                          label: 'Failed',
                          value: '$failedCount',
                          color: const Color(0xFFF87171)),
                      if (skippedCount > 0) ...[
                        const SizedBox(width: 8),
                        _ResultBadge(
                            label: 'Skipped',
                            value: '$skippedCount',
                            color: const Color(0xFFFBBF24)),
                      ],
                    ],
                  ),
                ],
              ),
            ),
            Flexible(
              child: ListView.separated(
                padding: const EdgeInsets.all(16),
                itemCount: results.length,
                separatorBuilder: (_, __) => const SizedBox(height: 8),
                itemBuilder: (_, i) {
                  final r = results[i];
                  final isSuccess =
                      (r['status'] as String?)?.toUpperCase() == 'SUCCESS';
                  final symbol =
                      (r['tradingSymbol'] as String?)?.replaceAll('-EQ', '') ?? '-';
                  final userName = (r['userName'] as String?) ?? '-';
                  final qty = (r['quantity'] as int?) ?? 0;
                  final orderId = r['orderId'] as String?;
                  final remarks = r['remarks'] as String?;

                  return Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: isSuccess ? _kGreenBg : _kRedBg,
                      borderRadius: BorderRadius.circular(8),
                      border: Border.all(
                          color: isSuccess ? _kGreenBorder : _kRedBorder),
                    ),
                    child: Row(
                      children: [
                        Icon(
                          isSuccess
                              ? Icons.check_circle_rounded
                              : Icons.cancel_rounded,
                          size: 16,
                          color: isSuccess ? _kGreen : _kRed,
                        ),
                        const SizedBox(width: 10),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                children: [
                                  Text(symbol,
                                      style: const TextStyle(
                                          fontSize: 12,
                                          fontWeight: FontWeight.w700,
                                          color: _kTextPrimary)),
                                  const SizedBox(width: 8),
                                  Text('$qty shares',
                                      style: const TextStyle(
                                          fontSize: 11, color: _kTextSecondary)),
                                ],
                              ),
                              Text(userName,
                                  style: const TextStyle(
                                      fontSize: 10, color: _kTextSecondary)),
                              if (orderId != null)
                                Text('Order: $orderId',
                                    style: const TextStyle(
                                        fontSize: 9, color: _kTextMuted)),
                              if (remarks != null && remarks.isNotEmpty)
                                Text(remarks,
                                    style: const TextStyle(
                                        fontSize: 10, color: _kRed)),
                            ],
                          ),
                        ),
                      ],
                    ),
                  );
                },
              ),
            ),
            Container(
              padding: const EdgeInsets.fromLTRB(16, 10, 16, 16),
              decoration: const BoxDecoration(
                  border: Border(top: BorderSide(color: _kBorder))),
              child: SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () => Navigator.pop(context),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: _kPrimary,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(8)),
                    elevation: 0,
                  ),
                  child: const Text('Close',
                      style: TextStyle(fontWeight: FontWeight.w700)),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _ResultBadge extends StatelessWidget {
  final String label, value;
  final Color color;
  const _ResultBadge(
      {required this.label, required this.value, required this.color});

  @override
  Widget build(BuildContext context) => Container(
    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 5),
    decoration: BoxDecoration(
      color: Colors.white.withOpacity(0.15),
      borderRadius: BorderRadius.circular(6),
    ),
    child: Column(
      children: [
        Text(value,
            style: TextStyle(
                fontSize: 14, fontWeight: FontWeight.w800, color: color)),
        Text(label,
            style:
            TextStyle(fontSize: 9, color: color.withOpacity(0.8))),
      ],
    ),
  );
}

// ─── Exit button ──────────────────────────────────────────────────────────────
class _ExitButton extends StatefulWidget {
  final String label;
  final Color color;
  final IconData icon;
  final VoidCallback onTap;
  const _ExitButton(
      {required this.label,
        required this.color,
        required this.icon,
        required this.onTap});

  @override
  State<_ExitButton> createState() => _ExitButtonState();
}

class _ExitButtonState extends State<_ExitButton> {
  bool _hov = false;
  @override
  Widget build(BuildContext context) => MouseRegion(
    cursor: SystemMouseCursors.click,
    onEnter: (_) => setState(() => _hov = true),
    onExit: (_) => setState(() => _hov = false),
    child: GestureDetector(
      onTap: widget.onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 130),
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
        decoration: BoxDecoration(
          color: _hov ? widget.color : widget.color.withOpacity(0.08),
          borderRadius: BorderRadius.circular(7),
          border: Border.all(
              color: widget.color.withOpacity(_hov ? 1 : 0.3)),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(widget.icon,
                size: 13,
                color: _hov ? Colors.white : widget.color),
            const SizedBox(width: 5),
            Text(widget.label,
                style: TextStyle(
                    fontSize: 11,
                    fontWeight: FontWeight.w600,
                    color: _hov ? Colors.white : widget.color)),
          ],
        ),
      ),
    ),
  );
}

// ─── Placeholders ─────────────────────────────────────────────────────────────
class _NoBatchSelected extends StatelessWidget {
  const _NoBatchSelected();
  @override
  Widget build(BuildContext context) => const Padding(
    padding: EdgeInsets.symmetric(vertical: 60),
    child: Center(
      child: Column(
        children: [
          Icon(Icons.touch_app_rounded, size: 40, color: _kTextMuted),
          SizedBox(height: 12),
          Text('Select a group above to view history',
              style: TextStyle(fontSize: 13, color: _kTextSecondary)),
        ],
      ),
    ),
  );
}

class _EmptyBatchBody extends StatelessWidget {
  const _EmptyBatchBody();
  @override
  Widget build(BuildContext context) => Padding(
    padding: const EdgeInsets.all(20),
    child: Center(
      child: Column(
        children: [
          Container(
            width: double.infinity,
            padding: const EdgeInsets.symmetric(vertical: 40),
            decoration: BoxDecoration(
              border: Border.all(color: _kBorder),
              borderRadius: BorderRadius.circular(8),
              color: const Color(0xFFF9FAFB),
            ),
            child: Center(
              child: Lottie.asset('assets/animations/No Data.json',
                  width: 160, height: 160, fit: BoxFit.contain),
            ),
          ),
          const SizedBox(height: 8),
          const Text('No positions found for this group',
              style: TextStyle(fontSize: 11, color: _kTextSecondary)),
        ],
      ),
    ),
  );
}

// ═══════════════════════════════════════════════════════════════════════════════
//  GROUP ORDER DIALOG WRAPPER
// ═══════════════════════════════════════════════════════════════════════════════
class _GroupOrderDialog extends StatelessWidget {
  final Map<String, dynamic> group;
  final List<GroupMember> members;
  final String initialSide;

  const _GroupOrderDialog(
      {required this.group, required this.members, required this.initialSide});

  @override
  Widget build(BuildContext context) {
    final screenH = MediaQuery.of(context).size.height;
    final screenW = MediaQuery.of(context).size.width;
    return Dialog(
      backgroundColor: Colors.transparent,
      insetPadding: EdgeInsets.symmetric(
          horizontal: screenW * 0.04, vertical: screenH * 0.04),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(24),
        child: SizedBox(
          width: screenW / 2.2,
          height: screenH * 1.8,
          child: GroupOrderPanel(
              group: group, members: members, initialSide: initialSide),
        ),
      ),
    );
  }
}

// ─── Card Header ──────────────────────────────────────────────────────────────
class _CardHeader extends StatelessWidget {
  final int groupCount;
  final VoidCallback onAddGroup;
  const _CardHeader({required this.groupCount, required this.onAddGroup});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.fromLTRB(20, 14, 16, 14),
      decoration: const BoxDecoration(
          border: Border(bottom: BorderSide(color: _kBorder))),
      child: Row(
        children: [
          const Text('Trade Groups',
              style: TextStyle(
                  fontSize: 14, fontWeight: FontWeight.w700, color: _kTextPrimary)),
          const SizedBox(width: 10),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 9, vertical: 3),
            decoration: BoxDecoration(
              color: _kPrimaryBg,
              borderRadius: BorderRadius.circular(20),
              border: Border.all(color: _kPrimaryBorder),
            ),
            child: Text('$groupCount active',
                style: const TextStyle(
                    fontSize: 11, fontWeight: FontWeight.w600, color: _kPrimary)),
          ),
          const Spacer(),
          _PrimaryButton(label: 'Add Group', icon: Icons.add_rounded, onTap: onAddGroup),
        ],
      ),
    );
  }
}

// ─── Primary Button ───────────────────────────────────────────────────────────
class _PrimaryButton extends StatefulWidget {
  final String label;
  final IconData icon;
  final VoidCallback onTap;
  final Color? color;
  const _PrimaryButton(
      {required this.label, required this.icon, required this.onTap, this.color});

  @override
  State<_PrimaryButton> createState() => _PrimaryButtonState();
}

class _PrimaryButtonState extends State<_PrimaryButton> {
  bool _hovered = false;
  @override
  Widget build(BuildContext context) {
    final base = widget.color ?? _kPrimary;
    return MouseRegion(
      cursor: SystemMouseCursors.click,
      onEnter: (_) => setState(() => _hovered = true),
      onExit: (_) => setState(() => _hovered = false),
      child: InkWell(
        onTap: widget.onTap,
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 150),
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 9),
          decoration: BoxDecoration(
            color: _hovered ? base.withOpacity(0.88) : base,
            borderRadius: BorderRadius.circular(10),
            boxShadow: [
              BoxShadow(
                  color: base.withOpacity(_hovered ? 0.4 : 0.2),
                  blurRadius: _hovered ? 12 : 6,
                  offset: const Offset(0, 3)),
            ],
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(widget.icon, color: Colors.white, size: 16),
              const SizedBox(width: 7),
              Text(widget.label,
                  style: const TextStyle(
                      color: Colors.white,
                      fontSize: 13,
                      fontWeight: FontWeight.w600,
                      letterSpacing: 0.1)),
            ],
          ),
        ),
      ),
    );
  }
}

// ─── Loading / Empty / Error ──────────────────────────────────────────────────
class _LoadingBody extends StatelessWidget {
  const _LoadingBody();
  @override
  Widget build(BuildContext context) => const Padding(
    padding: EdgeInsets.symmetric(vertical: 80),
    child: Center(
        child: CircularProgressIndicator(color: _kPrimary, strokeWidth: 2.5)),
  );
}

class _EmptyBody extends StatelessWidget {
  const _EmptyBody();
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: Center(
        child: Column(
          children: [
            Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(vertical: 50),
              decoration: BoxDecoration(
                border: Border.all(color: const Color(0xFFE5E7EB)),
                borderRadius: BorderRadius.circular(8),
                color: const Color(0xFFF9FAFB),
              ),
              child: Center(
                child: Lottie.asset('assets/animations/No Data.json',
                    width: 200, height: 200, fit: BoxFit.contain),
              ),
            ),
            const SizedBox(height: 6),
            const Text('Create your first group to start collective order execution',
                style: TextStyle(fontSize: 10, color: _kTextSecondary)),
          ],
        ),
      ),
    );
  }
}

class _ErrorBody extends StatelessWidget {
  final String message;
  final VoidCallback onRetry;
  const _ErrorBody({required this.message, required this.onRetry});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 64),
      child: Center(
        child: Column(
          children: [
            Container(
              width: 60,
              height: 60,
              decoration: BoxDecoration(
                color: const Color(0xFFFEF2F2),
                borderRadius: BorderRadius.circular(16),
              ),
              child:
              const Icon(Icons.wifi_off_rounded, color: _kRed, size: 26),
            ),
            const SizedBox(height: 14),
            const Text('Failed to load',
                style: TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w700,
                    color: _kTextPrimary)),
            const SizedBox(height: 4),
            Text(message,
                style: const TextStyle(fontSize: 10, color: _kTextSecondary),
                textAlign: TextAlign.center),
            const SizedBox(height: 16),
            TextButton.icon(
              onPressed: onRetry,
              icon: const Icon(Icons.refresh_rounded, size: 15),
              label: const Text('Retry'),
              style: TextButton.styleFrom(foregroundColor: _kPrimary),
            ),
          ],
        ),
      ),
    );
  }
}

// ─── Group Table ──────────────────────────────────────────────────────────────
class _GroupTable extends StatelessWidget {
  final List<Map<String, dynamic>> groups;
  final ValueChanged<Map<String, dynamic>> onEdit;
  final ValueChanged<Map<String, dynamic>> onDelete;
  final ValueChanged<Map<String, dynamic>> onView;
  final void Function(Map<String, dynamic> group, String side) onTrade;

  const _GroupTable({
    required this.groups,
    required this.onEdit,
    required this.onDelete,
    required this.onView,
    required this.onTrade,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(20.0),
      child: Container(
        decoration: BoxDecoration(
          border: Border.all(color: const Color(0xFFE5E7EB)),
          borderRadius: BorderRadius.circular(10),
        ),
        child: ClipRRect(
          borderRadius: BorderRadius.circular(10),
          child: Table(
            columnWidths: const {
              0: FlexColumnWidth(1.4),
              1: FlexColumnWidth(1.0),
              2: FlexColumnWidth(1.5),
              3: FlexColumnWidth(1.2),
              4: FlexColumnWidth(0.7),
              5: FlexColumnWidth(0.9),
              6: FlexColumnWidth(2.2),
            },
            border: const TableBorder(
              horizontalInside:
              BorderSide(color: Color(0xFFF3F4F6), width: 1),
            ),
            children: [
              TableRow(
                decoration: const BoxDecoration(color: AppColors.primary),
                children: [
                  _th('Group Name'),
                  _th('Description'),
                  _th('Created Date & Time'),
                  _th('Created By'),
                  _th('Members'),
                  _th('Status'),
                  _th('Actions'),
                ],
              ),
              ...List.generate(groups.length, (i) {
                final g = groups[i];
                return TableRow(
                  decoration: BoxDecoration(
                    color: i.isEven ? _kSurface : const Color(0xFFFCFBFF),
                  ),
                  children: [
                    _td(InkWell(
                      onTap: () => onView(g),
                      borderRadius: BorderRadius.circular(6),
                      child: Row(
                        children: [
                          Flexible(
                            child: Text(
                              g['groupName'] ?? '-',
                              overflow: TextOverflow.ellipsis,
                              style: const TextStyle(
                                fontSize: 14,
                                fontWeight: FontWeight.w600,
                                color: _kPrimary,
                                decoration: TextDecoration.underline,
                                decorationColor: _kPrimaryDark,
                              ),
                            ),
                          ),
                        ],
                      ),
                    )),
                    _td(Text(g['description'] ?? '—',
                        overflow: TextOverflow.ellipsis,
                        maxLines: 2,
                        style: const TextStyle(
                            fontSize: 10, color: _kTextSecondary))),
                    _td(Text(
                      g['createdAt'] != null
                          ? DateFormat('dd MMM yyyy, hh:mm a')
                          .format(DateTime.parse(g['createdAt']).toLocal())
                          : '-',
                      style: const TextStyle(
                          fontSize: 13,
                          color: _kTextPrimary,
                          fontWeight: FontWeight.w500),
                    )),
                    _td(Text(g['createdByName'] ?? '-',
                        style: const TextStyle(
                            fontSize: 13,
                            color: _kTextPrimary,
                            fontWeight: FontWeight.w500))),
                    _td(_MemberBadge(count: g['memberCount'] ?? 0),
                        alignment: Alignment.center),
                    _td(_StatusChipWidget(isActive: g['isActive'] ?? true)),
                    _td(_ActionRow(
                        group: g,
                        onEdit: onEdit,
                        onDelete: onDelete,
                        onTrade: onTrade)),
                  ],
                );
              }),
            ],
          ),
        ),
      ),
    );
  }

  Widget _th(String label) => Container(
    height: 46,
    alignment: Alignment.centerLeft,
    padding: const EdgeInsets.symmetric(horizontal: 15),
    child: Text(label.toUpperCase(),
        style: const TextStyle(
            fontSize: 9,
            fontWeight: FontWeight.bold,
            color: Colors.white,
            letterSpacing: 0.5)),
  );

  Widget _td(Widget child, {AlignmentGeometry alignment = Alignment.centerLeft}) =>
      Container(
        alignment: alignment,
        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 10),
        child: child,
      );
}

// ─── Member Badge ─────────────────────────────────────────────────────────────
class _MemberBadge extends StatelessWidget {
  final int count;
  const _MemberBadge({required this.count});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 9, vertical: 4),
      decoration: BoxDecoration(
        color: _kPrimaryBg,
        borderRadius: BorderRadius.circular(20),
        border: Border.all(color: _kPrimaryBorder),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Icon(Icons.people_alt_rounded, size: 11, color: _kPrimary),
          const SizedBox(width: 4),
          Text('$count',
              style: const TextStyle(
                  fontSize: 10, fontWeight: FontWeight.w700, color: _kPrimary)),
        ],
      ),
    );
  }
}

// ─── Status Chip ──────────────────────────────────────────────────────────────
class _StatusChipWidget extends StatelessWidget {
  final bool isActive;
  const _StatusChipWidget({required this.isActive});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 9, vertical: 4),
      decoration: BoxDecoration(
        color: isActive ? const Color(0xFFD1FAE5) : const Color(0xFFFEE2E2),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 5,
            height: 5,
            decoration: BoxDecoration(
              color: isActive ? _kGreen : _kRed,
              shape: BoxShape.circle,
            ),
          ),
          const SizedBox(width: 5),
          Text(isActive ? 'Active' : 'Inactive',
              style: TextStyle(
                  fontSize: 11,
                  fontWeight: FontWeight.w600,
                  color: isActive ? _kGreen : _kRed)),
        ],
      ),
    );
  }
}

// ─── Action Row ───────────────────────────────────────────────────────────────
class _ActionRow extends StatefulWidget {
  final Map<String, dynamic> group;
  final ValueChanged<Map<String, dynamic>> onEdit;
  final ValueChanged<Map<String, dynamic>> onDelete;
  final void Function(Map<String, dynamic>, String) onTrade;

  const _ActionRow({
    required this.group,
    required this.onEdit,
    required this.onDelete,
    required this.onTrade,
  });

  @override
  State<_ActionRow> createState() => _ActionRowState();
}

class _ActionRowState extends State<_ActionRow> {
  String? _loadingSide;

  void _handleTrade(String side) {
    if (_loadingSide != null) return;
    setState(() => _loadingSide = side);
    widget.onTrade(widget.group, side);
  }

  @override
  Widget build(BuildContext context) {
    return BlocListener<TradeGroupBloc, TradeGroupState>(
      listener: (_, state) {
        if (state is TradeGroupFetchedForOrder || state is TradeGroupError) {
          if (mounted) setState(() => _loadingSide = null);
        }
      },
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          _CompactTradeBtn(
            label: 'Buy',
            icon: Icons.trending_up_rounded,
            color: _kGreen,
            isLoading: _loadingSide == 'BUY',
            disabled: _loadingSide != null,
            onTap: () => _handleTrade('BUY'),
          ),
          const SizedBox(width: 6),
          _CompactTradeBtn(
            label: 'Sell',
            icon: Icons.trending_down_rounded,
            color: _kRed,
            isLoading: _loadingSide == 'SELL',
            disabled: _loadingSide != null,
            onTap: () => _handleTrade('SELL'),
          ),
          const SizedBox(width: 8),
          Container(width: 1, height: 22, color: _kBorder),
          const SizedBox(width: 8),
          _IconBtn(
              icon: Icons.edit_outlined,
              color: _kIndigo,
              tooltip: 'Edit',
              onTap: () => widget.onEdit(widget.group)),
          const SizedBox(width: 4),
          _IconBtn(
              icon: Icons.delete_outline_rounded,
              color: _kRed,
              tooltip: 'Delete',
              onTap: () => widget.onDelete(widget.group)),
        ],
      ),
    );
  }
}

// ─── Compact Trade Button ─────────────────────────────────────────────────────
class _CompactTradeBtn extends StatefulWidget {
  final String label;
  final IconData icon;
  final Color color;
  final VoidCallback onTap;
  final bool isLoading;
  final bool disabled;

  const _CompactTradeBtn({
    required this.label,
    required this.icon,
    required this.color,
    required this.onTap,
    this.isLoading = false,
    this.disabled = false,
  });

  @override
  State<_CompactTradeBtn> createState() => _CompactTradeBtnState();
}

class _CompactTradeBtnState extends State<_CompactTradeBtn> {
  bool _hovered = false;

  @override
  Widget build(BuildContext context) {
    final interactive = !widget.disabled && !widget.isLoading;
    return MouseRegion(
      cursor: interactive ? SystemMouseCursors.click : SystemMouseCursors.basic,
      onEnter: (_) {
        if (interactive) setState(() => _hovered = true);
      },
      onExit: (_) => setState(() => _hovered = false),
      child: GestureDetector(
        onTap: interactive ? widget.onTap : null,
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 130),
          height: 28,
          padding: const EdgeInsets.symmetric(horizontal: 5),
          decoration: BoxDecoration(
            color: widget.isLoading
                ? widget.color.withOpacity(0.08)
                : _hovered && interactive
                ? widget.color
                : widget.color.withOpacity(widget.disabled ? 0.05 : 0.10),
            borderRadius: BorderRadius.circular(6),
            border: Border.all(
              color: widget.color.withOpacity(widget.isLoading
                  ? 0.25
                  : _hovered && interactive
                  ? 1.0
                  : widget.disabled
                  ? 0.15
                  : 0.4),
            ),
          ),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              if (widget.isLoading) ...[
                SizedBox(
                  width: 11,
                  height: 11,
                  child: CircularProgressIndicator(
                      strokeWidth: 1.8, color: widget.color),
                ),
              ] else ...[
                Icon(widget.icon,
                    size: 13,
                    color: _hovered && interactive
                        ? Colors.white
                        : widget.color
                        .withOpacity(widget.disabled ? 0.4 : 1.0)),
              ],
              const SizedBox(width: 4),
              Text(widget.label,
                  style: TextStyle(
                      fontSize: 10,
                      fontWeight: FontWeight.w600,
                      color: _hovered && interactive
                          ? Colors.white
                          : widget.color
                          .withOpacity(widget.disabled ? 0.4 : 1.0))),
            ],
          ),
        ),
      ),
    );
  }
}

// ─── Icon Button ──────────────────────────────────────────────────────────────
class _IconBtn extends StatefulWidget {
  final IconData icon;
  final Color color;
  final String tooltip;
  final VoidCallback onTap;
  const _IconBtn(
      {required this.icon,
        required this.color,
        required this.tooltip,
        required this.onTap});

  @override
  State<_IconBtn> createState() => _IconBtnState();
}

class _IconBtnState extends State<_IconBtn> {
  bool _hovered = false;
  @override
  Widget build(BuildContext context) {
    return Tooltip(
      message: widget.tooltip,
      preferBelow: false,
      child: MouseRegion(
        cursor: SystemMouseCursors.click,
        onEnter: (_) => setState(() => _hovered = true),
        onExit: (_) => setState(() => _hovered = false),
        child: InkWell(
          onTap: widget.onTap,
          child: AnimatedContainer(
            duration: const Duration(milliseconds: 130),
            width: 30,
            height: 30,
            decoration: BoxDecoration(
              color: _hovered
                  ? widget.color.withOpacity(0.14)
                  : widget.color.withOpacity(0.07),
              borderRadius: BorderRadius.circular(7),
            ),
            child: Icon(widget.icon, size: 15, color: widget.color),
          ),
        ),
      ),
    );
  }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  ADD / EDIT / DELETE / DETAIL DIALOGS
// ═══════════════════════════════════════════════════════════════════════════════
class _AddGroupDialog extends StatefulWidget {
  final int pyId;
  const _AddGroupDialog({required this.pyId});
  @override
  State<_AddGroupDialog> createState() => _AddGroupDialogState();
}

class _AddGroupDialogState extends State<_AddGroupDialog>
    with SingleTickerProviderStateMixin {
  late AnimationController _ctrl;
  late Animation<double> _scale, _fade;
  final _nameCtrl = TextEditingController();
  final _descCtrl = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  final List<int> _selectedChildIds = [];

  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 280));
    _scale = CurvedAnimation(parent: _ctrl, curve: Curves.easeOutBack);
    _fade = CurvedAnimation(parent: _ctrl, curve: Curves.easeIn);
    _ctrl.forward();
  }

  @override
  void dispose() {
    _ctrl.dispose();
    _nameCtrl.dispose();
    _descCtrl.dispose();
    super.dispose();
  }

  void _submit() {
    if (!_formKey.currentState!.validate()) return;
    context.read<TradeGroupBloc>().add(CreateTradeGroup(
      pyId: widget.pyId,
      groupName: _nameCtrl.text.trim(),
      description: _descCtrl.text.trim().isEmpty ? null : _descCtrl.text.trim(),
      brokerAccountIds: _selectedChildIds,
    ));
    Navigator.pop(context);
  }

  List<Map<String, dynamic>> _accounts(FetchUserDematState state) {
    if (state is FetchUserDematLoaded) {
      return state.accounts
          .where((a) => a.active && !a.isAdmin)
          .map((a) => <String, dynamic>{
        'id': a.id,
        'clientId': a.clientId,
        'name': a.userName,
        'broker': a.brokerType,
      })
          .toList();
    }
    return [];
  }

  void _toggleChild(int id) => setState(() {
    _selectedChildIds.contains(id)
        ? _selectedChildIds.remove(id)
        : _selectedChildIds.add(id);
  });

  @override
  Widget build(BuildContext context) {
    return _DialogShell(
      fade: _fade,
      scale: _scale,
      width: 520,
      child: Form(
        key: _formKey,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            _DialogHeader(
              icon: Icons.group_add_rounded,
              title: 'Create Trade Group',
              subtitle: 'Group broker accounts for collective trading',
              colors: [_kPrimaryLight, _kPrimaryDark],
            ),
            Flexible(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _FormField(
                      controller: _nameCtrl,
                      label: 'Group Name',
                      hint: 'e.g. Alpha Group',
                      icon: Icons.group_work_outlined,
                      validator: (v) =>
                      (v == null || v.trim().isEmpty) ? 'Group name is required' : null,
                    ),
                    const SizedBox(height: 16),
                    _FormField(
                      controller: _descCtrl,
                      label: 'Description',
                      hint: 'Optional description...',
                      icon: Icons.notes_rounded,
                      maxLines: 3,
                    ),
                    const SizedBox(height: 20),
                    _AccountSelector(
                      dematStateBuilder: _accounts,
                      selectedIds: _selectedChildIds,
                      onToggle: _toggleChild,
                    ),
                  ],
                ),
              ),
            ),
            _DialogFooter(
              onCancel: () => Navigator.pop(context),
              onConfirm: _submit,
              confirmLabel: 'Create Group',
              confirmIcon: Icons.add_rounded,
              confirmColor: _kPrimary,
            ),
          ],
        ),
      ),
    );
  }
}

class _EditGroupDialog extends StatefulWidget {
  final Map<String, dynamic> group;
  const _EditGroupDialog({required this.group});
  @override
  State<_EditGroupDialog> createState() => _EditGroupDialogState();
}

class _EditGroupDialogState extends State<_EditGroupDialog>
    with SingleTickerProviderStateMixin {
  late AnimationController _ctrl;
  late Animation<double> _scale, _fade;
  late TextEditingController _nameCtrl;
  late TextEditingController _descCtrl;
  final _formKey = GlobalKey<FormState>();
  late List<int> _originalMemberIds;
  late List<int> _selectedIds;

  @override
  void initState() {
    super.initState();
    _nameCtrl = TextEditingController(text: widget.group['groupName'] ?? '');
    _descCtrl = TextEditingController(text: widget.group['description'] ?? '');
    final members =
    List<Map<String, dynamic>>.from(widget.group['members'] ?? []);
    _originalMemberIds = members
        .where((m) => m['linked'] == true)
        .map((m) => (m['brokerAccountId'] as int?) ?? (m['id'] as int?) ?? 0)
        .where((id) => id != 0)
        .toList();
    _selectedIds = List<int>.from(_originalMemberIds);
    _ctrl = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 280));
    _scale = CurvedAnimation(parent: _ctrl, curve: Curves.easeOutBack);
    _fade = CurvedAnimation(parent: _ctrl, curve: Curves.easeIn);
    _ctrl.forward();
  }

  @override
  void dispose() {
    _ctrl.dispose();
    _nameCtrl.dispose();
    _descCtrl.dispose();
    super.dispose();
  }

  List<int> get _addedIds =>
      _selectedIds.where((id) => !_originalMemberIds.contains(id)).toList();
  List<int> get _removedIds =>
      _originalMemberIds.where((id) => !_selectedIds.contains(id)).toList();

  void _submit() {
    if (!_formKey.currentState!.validate()) return;
    context.read<TradeGroupBloc>().add(UpdateTradeGroup(
      groupId: widget.group['groupId'],
      groupName: _nameCtrl.text.trim(),
      description: _descCtrl.text.trim(),
      brokerAccountIds: _selectedIds,
    ));
    Navigator.pop(context);
  }

  List<Map<String, dynamic>> _accounts(FetchUserDematState state) {
    if (state is FetchUserDematLoaded) {
      return state.accounts
          .where((a) => a.active && !a.isAdmin)
          .map((a) => <String, dynamic>{
        'id': a.id,
        'clientId': a.clientId,
        'name': a.userName,
        'broker': a.brokerType,
      })
          .toList();
    }
    return [];
  }

  void _toggleId(int id) => setState(() {
    _selectedIds.contains(id)
        ? _selectedIds.remove(id)
        : _selectedIds.add(id);
  });

  @override
  Widget build(BuildContext context) {
    final hasChanges = _addedIds.isNotEmpty || _removedIds.isNotEmpty;
    return _DialogShell(
      fade: _fade,
      scale: _scale,
      width: 520,
      child: Form(
        key: _formKey,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            _DialogHeader(
              icon: Icons.edit_rounded,
              title: 'Edit Trade Group',
              subtitle: 'Update group details and members',
              colors: [_kIndigo, const Color(0xFF4338CA)],
            ),
            Flexible(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    _FormField(
                      controller: _nameCtrl,
                      label: 'Group Name',
                      hint: 'Enter group name',
                      icon: Icons.group_work_outlined,
                      validator: (v) =>
                      (v == null || v.trim().isEmpty) ? 'Group name is required' : null,
                    ),
                    const SizedBox(height: 16),
                    _FormField(
                      controller: _descCtrl,
                      label: 'Description',
                      hint: 'Optional description...',
                      icon: Icons.notes_rounded,
                      maxLines: 3,
                    ),
                    const SizedBox(height: 20),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('Member Accounts',
                                style: TextStyle(
                                    fontSize: 10,
                                    fontWeight: FontWeight.w600,
                                    color: Color(0xFF374151))),
                            const SizedBox(height: 2),
                            Text('Add or remove accounts from this group',
                                style: TextStyle(
                                    fontSize: 11, color: Colors.grey.shade500)),
                          ],
                        ),
                        if (hasChanges)
                          Row(
                            children: [
                              if (_addedIds.isNotEmpty)
                                _ChangeBadge(
                                    label: '+${_addedIds.length}',
                                    color: _kGreen,
                                    bgColor: const Color(0xFFD1FAE5)),
                              if (_removedIds.isNotEmpty) ...[
                                const SizedBox(width: 6),
                                _ChangeBadge(
                                    label: '-${_removedIds.length}',
                                    color: _kRed,
                                    bgColor: const Color(0xFFFEE2E2)),
                              ],
                            ],
                          ),
                      ],
                    ),
                    const SizedBox(height: 10),
                    BlocBuilder<FetchUserDematBloc, FetchUserDematState>(
                      builder: (ctx, dematState) {
                        if (dematState is FetchUserDematLoading) {
                          return const _AccountsLoading();
                        }
                        final accounts = _accounts(dematState);
                        if (accounts.isEmpty) return _EmptyAccountsBox();
                        return _AccountListBox(
                          accounts: accounts,
                          selectedIds: _selectedIds,
                          originalIds: _originalMemberIds,
                          onToggle: _toggleId,
                        );
                      },
                    ),
                  ],
                ),
              ),
            ),
            _DialogFooter(
              onCancel: () => Navigator.pop(context),
              onConfirm: _submit,
              confirmLabel: 'Save Changes',
              confirmIcon: Icons.save_rounded,
              confirmColor: _kIndigo,
            ),
          ],
        ),
      ),
    );
  }
}

class _DeleteGroupDialog extends StatefulWidget {
  final Map<String, dynamic> group;
  const _DeleteGroupDialog({required this.group});
  @override
  State<_DeleteGroupDialog> createState() => _DeleteGroupDialogState();
}

class _DeleteGroupDialogState extends State<_DeleteGroupDialog>
    with SingleTickerProviderStateMixin {
  late AnimationController _ctrl;
  late Animation<double> _scale, _fade;

  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 260));
    _scale = CurvedAnimation(parent: _ctrl, curve: Curves.easeOutBack);
    _fade = CurvedAnimation(parent: _ctrl, curve: Curves.easeIn);
    _ctrl.forward();
  }

  @override
  void dispose() {
    _ctrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final name = widget.group['groupName'] ?? 'this group';
    final count = widget.group['memberCount'] ?? 0;
    return _DialogShell(
      fade: _fade,
      scale: _scale,
      width: 420,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          _DialogHeader(
            icon: Icons.delete_forever_rounded,
            title: 'Delete Trade Group',
            subtitle: 'This action cannot be undone',
            colors: [_kRed, const Color(0xFFB91C1C)],
          ),
          Padding(
            padding: const EdgeInsets.all(24),
            child: Column(
              children: [
                Container(
                  width: double.infinity,
                  padding: const EdgeInsets.all(14),
                  decoration: BoxDecoration(
                    color: const Color(0xFFFFF5F5),
                    borderRadius: BorderRadius.circular(10),
                    border: Border.all(color: const Color(0xFFFECACA)),
                  ),
                  child: Row(
                    children: [
                      _GroupAvatar(name: name),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(name,
                                style: const TextStyle(
                                    fontSize: 14,
                                    fontWeight: FontWeight.w700,
                                    color: _kTextPrimary)),
                            const SizedBox(height: 2),
                            Text('$count member${count == 1 ? '' : 's'}',
                                style: const TextStyle(
                                    fontSize: 10, color: _kTextSecondary)),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(height: 14),
                Container(
                  padding: const EdgeInsets.symmetric(
                      horizontal: 14, vertical: 10),
                  decoration: BoxDecoration(
                    color: const Color(0xFFFEF3C7),
                    borderRadius: BorderRadius.circular(8),
                    border: Border.all(color: const Color(0xFFFCD34D)),
                  ),
                  child: const Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Icon(Icons.warning_amber_rounded,
                          size: 15, color: Color(0xFFD97706)),
                      SizedBox(width: 8),
                      Expanded(
                        child: Text(
                          'The group will be soft-deleted. Members retain their accounts but the group will be deactivated.',
                          style: TextStyle(
                              fontSize: 10,
                              color: Color(0xFF92400E),
                              fontWeight: FontWeight.w500),
                        ),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          _DialogFooter(
            onCancel: () => Navigator.pop(context),
            onConfirm: () {
              context
                  .read<TradeGroupBloc>()
                  .add(DeleteTradeGroup(widget.group['groupId']));
              Navigator.pop(context);
            },
            confirmLabel: 'Delete Group',
            confirmIcon: Icons.delete_outline_rounded,
            confirmColor: _kRed,
          ),
        ],
      ),
    );
  }
}

class _GroupDetailDialog extends StatelessWidget {
  final Map<String, dynamic> group;
  const _GroupDetailDialog({required this.group});

  @override
  Widget build(BuildContext context) {
    final members = List<Map<String, dynamic>>.from(group['members'] ?? []);
    final isActive = group['isActive'] ?? true;
    return Dialog(
      backgroundColor: Colors.transparent,
      elevation: 0,
      child: Container(
        width: 520,
        constraints: const BoxConstraints(maxHeight: 600),
        decoration: BoxDecoration(
          color: _kSurface,
          borderRadius: BorderRadius.circular(18),
          boxShadow: [
            BoxShadow(
                color: Colors.black.withOpacity(0.14),
                blurRadius: 40,
                offset: const Offset(0, 12)),
          ],
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              padding: const EdgeInsets.all(22),
              decoration: const BoxDecoration(
                gradient: LinearGradient(
                  colors: [Color(0xFF1E293B), Color(0xFF334155)],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                borderRadius: BorderRadius.only(
                  topLeft: Radius.circular(18),
                  topRight: Radius.circular(18),
                ),
              ),
              child: Row(
                children: [
                  _GroupAvatar(name: group['groupName'] ?? ''),
                  const SizedBox(width: 14),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(group['groupName'] ?? '-',
                            style: const TextStyle(
                                fontSize: 16,
                                fontWeight: FontWeight.w700,
                                color: Colors.white)),
                        const SizedBox(height: 3),
                        Text(group['description'] ?? 'No description',
                            style: TextStyle(
                                fontSize: 10,
                                color: Colors.white.withOpacity(0.6)),
                            maxLines: 1,
                            overflow: TextOverflow.ellipsis),
                      ],
                    ),
                  ),
                  Container(
                    padding:
                    const EdgeInsets.symmetric(horizontal: 11, vertical: 5),
                    decoration: BoxDecoration(
                      color: isActive
                          ? _kGreen.withOpacity(0.2)
                          : _kRed.withOpacity(0.2),
                      borderRadius: BorderRadius.circular(20),
                      border: Border.all(
                          color: isActive
                              ? _kGreen.withOpacity(0.5)
                              : _kRed.withOpacity(0.5)),
                    ),
                    child: Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Container(
                          width: 5,
                          height: 5,
                          decoration: BoxDecoration(
                            color: isActive
                                ? const Color(0xFF34D399)
                                : const Color(0xFFF87171),
                            shape: BoxShape.circle,
                          ),
                        ),
                        const SizedBox(width: 5),
                        Text(isActive ? 'ACTIVE' : 'INACTIVE',
                            style: TextStyle(
                                fontSize: 10,
                                fontWeight: FontWeight.w700,
                                color: isActive
                                    ? const Color(0xFF34D399)
                                    : const Color(0xFFF87171))),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 16, 20, 0),
              child: Row(
                children: [
                  _StatCard(
                      label: 'Created By',
                      value: group['createdByName'] ?? '-',
                      icon: Icons.person_outline_rounded),
                  const SizedBox(width: 10),
                  _StatCard(
                      label: 'Members',
                      value: '${group['memberCount'] ?? 0}',
                      icon: Icons.people_alt_rounded),
                  const SizedBox(width: 10),
                  _StatCard(
                      label: 'Group ID',
                      value: '#${group['groupId'] ?? '-'}',
                      icon: Icons.tag_rounded),
                ],
              ),
            ),
            if (members.any((m) => m['memberActive'] == false))
              Padding(
                padding: const EdgeInsets.fromLTRB(20, 12, 20, 0),
                child: Container(
                  padding: const EdgeInsets.symmetric(
                      horizontal: 12, vertical: 8),
                  decoration: BoxDecoration(
                    color: const Color(0xFFFEF3C7),
                    borderRadius: BorderRadius.circular(8),
                    border: Border.all(color: const Color(0xFFFCD34D)),
                  ),
                  child: const Row(
                    children: [
                      Icon(Icons.info_outline_rounded,
                          size: 13, color: _kOrange),
                      SizedBox(width: 7),
                      Expanded(
                        child: Text(
                          'Deactivated accounts are shown but will be skipped during Buy / Sell execution.',
                          style: TextStyle(
                              fontSize: 11,
                              color: Color(0xFF92400E),
                              fontWeight: FontWeight.w500),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 14, 20, 0),
              child: Row(
                children: [
                  const Icon(Icons.group_rounded,
                      size: 13, color: _kTextSecondary),
                  const SizedBox(width: 6),
                  Text('Members (${members.length})',
                      style: const TextStyle(
                          fontSize: 13,
                          fontWeight: FontWeight.w700,
                          color: _kTextPrimary)),
                ],
              ),
            ),
            const SizedBox(height: 10),
            if (members.isEmpty)
              Padding(
                padding: const EdgeInsets.symmetric(vertical: 20),
                child: Text('No members in this group',
                    style:
                    TextStyle(fontSize: 13, color: Colors.grey.shade400)),
              )
            else
              Flexible(
                child: ListView.separated(
                  shrinkWrap: true,
                  padding: const EdgeInsets.fromLTRB(20, 0, 20, 16),
                  itemCount: members.length,
                  separatorBuilder: (_, __) => const SizedBox(height: 6),
                  itemBuilder: (_, i) => _MemberTileDetail(member: members[i]),
                ),
              ),
            Container(
              padding:
              const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
              decoration: const BoxDecoration(
                color: Color(0xFFF9FAFB),
                borderRadius: BorderRadius.only(
                  bottomLeft: Radius.circular(18),
                  bottomRight: Radius.circular(18),
                ),
                border: Border(top: BorderSide(color: _kBorder)),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  TextButton(
                    onPressed: () => Navigator.pop(context),
                    style:
                    TextButton.styleFrom(foregroundColor: _kTextSecondary),
                    child: const Text('Close'),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// ═══════════════════════════════════════════════════════════════════════════════
//  SHARED DIALOG COMPONENTS
// ═══════════════════════════════════════════════════════════════════════════════
class _DialogShell extends StatelessWidget {
  final Animation<double> fade, scale;
  final double width;
  final Widget child;
  const _DialogShell(
      {required this.fade,
        required this.scale,
        required this.width,
        required this.child});

  @override
  Widget build(BuildContext context) {
    return FadeTransition(
      opacity: fade,
      child: ScaleTransition(
        scale: scale,
        child: Dialog(
          backgroundColor: Colors.transparent,
          elevation: 0,
          child: Container(
            width: width,
            constraints: const BoxConstraints(maxHeight: 720),
            decoration: BoxDecoration(
              color: _kSurface,
              borderRadius: BorderRadius.circular(18),
              boxShadow: [
                BoxShadow(
                    color: Colors.black.withOpacity(0.16),
                    blurRadius: 40,
                    offset: const Offset(0, 16)),
              ],
            ),
            child: child,
          ),
        ),
      ),
    );
  }
}

class _DialogHeader extends StatelessWidget {
  final IconData icon;
  final String title, subtitle;
  final List<Color> colors;
  const _DialogHeader(
      {required this.icon,
        required this.title,
        required this.subtitle,
        required this.colors});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.symmetric(vertical: 24),
      decoration: BoxDecoration(
        gradient: LinearGradient(
            colors: colors,
            begin: Alignment.topLeft,
            end: Alignment.bottomRight),
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(18),
          topRight: Radius.circular(18),
        ),
      ),
      child: Column(
        children: [
          Container(
            width: 48,
            height: 48,
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.2),
              shape: BoxShape.circle,
            ),
            child: Icon(icon, color: Colors.white, size: 22),
          ),
          const SizedBox(height: 10),
          Text(title,
              style: const TextStyle(
                  fontSize: 17, fontWeight: FontWeight.w700, color: Colors.white)),
          const SizedBox(height: 4),
          Text(subtitle,
              style: TextStyle(
                  fontSize: 10, color: Colors.white.withOpacity(0.72))),
        ],
      ),
    );
  }
}

class _DialogFooter extends StatelessWidget {
  final VoidCallback onCancel;
  final VoidCallback? onConfirm;
  final String confirmLabel;
  final IconData confirmIcon;
  final Color confirmColor;
  const _DialogFooter({
    required this.onCancel,
    required this.onConfirm,
    required this.confirmLabel,
    required this.confirmIcon,
    required this.confirmColor,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.fromLTRB(24, 12, 24, 22),
      decoration:
      const BoxDecoration(border: Border(top: BorderSide(color: _kBorder))),
      child: Row(
        children: [
          Expanded(
            child: OutlinedButton(
              onPressed: onCancel,
              style: OutlinedButton.styleFrom(
                foregroundColor: _kTextSecondary,
                side: const BorderSide(color: _kBorder),
                padding: const EdgeInsets.symmetric(vertical: 12),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(9)),
              ),
              child: const Text('Cancel',
                  style:
                  TextStyle(fontSize: 13, fontWeight: FontWeight.w600)),
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: ElevatedButton(
              onPressed: onConfirm,
              style: ElevatedButton.styleFrom(
                backgroundColor: confirmColor,
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 12),
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(9)),
                elevation: 0,
                disabledBackgroundColor: confirmColor.withOpacity(0.5),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(confirmIcon, size: 15),
                  const SizedBox(width: 6),
                  Text(confirmLabel,
                      style: const TextStyle(
                          fontSize: 13, fontWeight: FontWeight.w700)),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _AccountSelector extends StatelessWidget {
  final List<Map<String, dynamic>> Function(FetchUserDematState) dematStateBuilder;
  final List<int> selectedIds;
  final ValueChanged<int> onToggle;
  const _AccountSelector({
    required this.dematStateBuilder,
    required this.selectedIds,
    required this.onToggle,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('Child Accounts',
            style: TextStyle(
                fontSize: 10,
                fontWeight: FontWeight.w600,
                color: Color(0xFF374151))),
        const SizedBox(height: 3),
        Text('Select accounts to include in this group',
            style: TextStyle(fontSize: 11, color: Colors.grey.shade500)),
        const SizedBox(height: 10),
        BlocBuilder<FetchUserDematBloc, FetchUserDematState>(
          builder: (ctx, dematState) {
            if (dematState is FetchUserDematLoading) {
              return const _AccountsLoading();
            }
            final accounts = dematStateBuilder(dematState);
            if (accounts.isEmpty) return _EmptyAccountsBox();
            return _AccountListBox(
              accounts: accounts,
              selectedIds: selectedIds,
              originalIds: const [],
              onToggle: onToggle,
            );
          },
        ),
        if (selectedIds.isNotEmpty) ...[
          const SizedBox(height: 8),
          Row(
            children: [
              Container(
                  width: 6,
                  height: 6,
                  decoration: const BoxDecoration(
                      color: _kPrimary, shape: BoxShape.circle)),
              const SizedBox(width: 6),
              Text(
                '${selectedIds.length} account${selectedIds.length == 1 ? '' : 's'} selected',
                style: const TextStyle(
                    fontSize: 11,
                    color: _kPrimary,
                    fontWeight: FontWeight.w600),
              ),
            ],
          ),
        ],
      ],
    );
  }
}

class _AccountListBox extends StatelessWidget {
  final List<Map<String, dynamic>> accounts;
  final List<int> selectedIds;
  final List<int> originalIds;
  final ValueChanged<int> onToggle;
  const _AccountListBox({
    required this.accounts,
    required this.selectedIds,
    required this.originalIds,
    required this.onToggle,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
          border: Border.all(color: _kBorder),
          borderRadius: BorderRadius.circular(10)),
      constraints: const BoxConstraints(maxHeight: 220),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(10),
        child: ListView.separated(
          shrinkWrap: true,
          itemCount: accounts.length,
          separatorBuilder: (_, __) =>
          const Divider(height: 1, color: Color(0xFFF3F4F6)),
          itemBuilder: (ctx, index) {
            final acc = accounts[index];
            final id = acc['id'] as int;
            final isSelected = selectedIds.contains(id);
            final isNew = isSelected &&
                originalIds.isNotEmpty &&
                !originalIds.contains(id);
            final isRemoved = !isSelected && originalIds.contains(id);

            return InkWell(
              onTap: () => onToggle(id),
              child: Container(
                color: isSelected
                    ? _kPrimaryBg
                    : isRemoved
                    ? const Color(0xFFFFF5F5)
                    : Colors.white,
                padding: const EdgeInsets.symmetric(
                    horizontal: 14, vertical: 10),
                child: Row(
                  children: [
                    AnimatedContainer(
                      duration: const Duration(milliseconds: 130),
                      width: 18,
                      height: 18,
                      decoration: BoxDecoration(
                        color:
                        isSelected ? _kPrimary : Colors.transparent,
                        borderRadius: BorderRadius.circular(4),
                        border: Border.all(
                            color: isSelected ? _kPrimary : _kBorder,
                            width: 1.5),
                      ),
                      child: isSelected
                          ? const Icon(Icons.check,
                          size: 12, color: Colors.white)
                          : null,
                    ),
                    const SizedBox(width: 10),
                    CircleAvatar(
                      radius: 14,
                      backgroundColor: _kPrimaryBg,
                      child: Text(
                        (acc['name'] as String).isNotEmpty
                            ? (acc['name'] as String)[0].toUpperCase()
                            : '?',
                        style: const TextStyle(
                            fontSize: 11,
                            fontWeight: FontWeight.w700,
                            color: _kPrimary),
                      ),
                    ),
                    const SizedBox(width: 10),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Row(
                            children: [
                              Text(acc['name'] as String,
                                  style: TextStyle(
                                      fontSize: 10,
                                      fontWeight: FontWeight.w600,
                                      color: _kTextPrimary,
                                      decoration: isRemoved
                                          ? TextDecoration.lineThrough
                                          : null)),
                              if (isNew) ...[
                                const SizedBox(width: 6),
                                Container(
                                  padding: const EdgeInsets.symmetric(
                                      horizontal: 5, vertical: 1),
                                  decoration: BoxDecoration(
                                    color: const Color(0xFFD1FAE5),
                                    borderRadius: BorderRadius.circular(4),
                                  ),
                                  child: const Text('NEW',
                                      style: TextStyle(
                                          fontSize: 9,
                                          fontWeight: FontWeight.w700,
                                          color: _kGreen)),
                                ),
                              ],
                            ],
                          ),
                          Text('${acc['clientId']} · ${acc['broker']}',
                              style: const TextStyle(
                                  fontSize: 11, color: _kTextSecondary)),
                        ],
                      ),
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 7, vertical: 3),
                      decoration: BoxDecoration(
                        color: isSelected
                            ? _kPrimary.withOpacity(0.08)
                            : const Color(0xFFF3F4F6),
                        borderRadius: BorderRadius.circular(6),
                      ),
                      child: Text(acc['broker'] as String,
                          style: TextStyle(
                              fontSize: 10,
                              fontWeight: FontWeight.w600,
                              color: isSelected ? _kPrimary : _kTextSecondary)),
                    ),
                  ],
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}

class _AccountsLoading extends StatelessWidget {
  const _AccountsLoading();
  @override
  Widget build(BuildContext context) => const Center(
    child: Padding(
      padding: EdgeInsets.symmetric(vertical: 20),
      child:
      CircularProgressIndicator(color: _kPrimary, strokeWidth: 2),
    ),
  );
}

class _EmptyAccountsBox extends StatelessWidget {
  @override
  Widget build(BuildContext context) => Container(
    width: double.infinity,
    padding:
    const EdgeInsets.symmetric(vertical: 20, horizontal: 16),
    decoration: BoxDecoration(
      color: const Color(0xFFF9FAFB),
      borderRadius: BorderRadius.circular(10),
      border: Border.all(color: _kBorder),
    ),
    child: Center(
      child: Text('No active child accounts found',
          style: TextStyle(fontSize: 10, color: Colors.grey.shade400)),
    ),
  );
}

class _ChangeBadge extends StatelessWidget {
  final String label;
  final Color color, bgColor;
  const _ChangeBadge(
      {required this.label, required this.color, required this.bgColor});

  @override
  Widget build(BuildContext context) => Container(
    padding:
    const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
    decoration: BoxDecoration(
        color: bgColor, borderRadius: BorderRadius.circular(6)),
    child: Text(label,
        style: TextStyle(
            fontSize: 11,
            fontWeight: FontWeight.w700,
            color: color)),
  );
}

class _StatCard extends StatelessWidget {
  final String label, value;
  final IconData icon;
  const _StatCard(
      {required this.label, required this.value, required this.icon});

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Container(
        padding:
        const EdgeInsets.symmetric(vertical: 10, horizontal: 12),
        decoration: BoxDecoration(
          color: const Color(0xFFF9FAFB),
          borderRadius: BorderRadius.circular(10),
          border: Border.all(color: _kBorder),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(icon, size: 12, color: _kPrimary),
                const SizedBox(width: 5),
                Text(label,
                    style: const TextStyle(
                        fontSize: 10,
                        color: _kTextMuted,
                        fontWeight: FontWeight.w500)),
              ],
            ),
            const SizedBox(height: 4),
            Text(value,
                style: const TextStyle(
                    fontSize: 13,
                    fontWeight: FontWeight.w700,
                    color: _kTextPrimary),
                overflow: TextOverflow.ellipsis),
          ],
        ),
      ),
    );
  }
}

class _GroupAvatar extends StatelessWidget {
  final String name;
  const _GroupAvatar({required this.name});

  @override
  Widget build(BuildContext context) {
    final letter = name.isNotEmpty ? name[0].toUpperCase() : 'G';
    return Container(
      width: 32,
      height: 32,
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFFA78BFA), _kPrimary],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(8),
      ),
      child: Center(
        child: Text(letter,
            style: const TextStyle(
                color: Colors.white,
                fontSize: 13,
                fontWeight: FontWeight.w700)),
      ),
    );
  }
}

class _MemberTileDetail extends StatelessWidget {
  final Map<String, dynamic> member;
  const _MemberTileDetail({required this.member});

  @override
  Widget build(BuildContext context) {
    final bool linked = (member['linked'] as bool?) ?? false;
    final bool memberActive = (member['memberActive'] as bool?) ?? true;
    final bool isDeactivated = !memberActive;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
      decoration: BoxDecoration(
        color: isDeactivated
            ? const Color(0xFFF9FAFB)
            : Colors.white,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(
            color: isDeactivated
                ? const Color(0xFFE5E7EB)
                : _kBorder),
      ),
      child: Row(
        children: [
          Tooltip(
            message: linked ? 'Member linked to group' : 'Not linked to group',
            child: Container(
              width: 18,
              height: 18,
              decoration: BoxDecoration(
                color: linked
                    ? (isDeactivated ? _kTextMuted : _kPrimary)
                    : Colors.transparent,
                borderRadius: BorderRadius.circular(4),
                border: Border.all(
                    color: linked
                        ? (isDeactivated ? _kTextMuted : _kPrimary)
                        : _kBorder,
                    width: 1.5),
              ),
              child: linked
                  ? const Icon(Icons.check, size: 11, color: Colors.white)
                  : null,
            ),
          ),
          const SizedBox(width: 10),
          CircleAvatar(
            radius: 16,
            backgroundColor: isDeactivated
                ? const Color(0xFFF3F4F6)
                : _kPrimaryBg,
            child: Text(
              (member['userName'] as String? ?? '?').isNotEmpty
                  ? (member['userName'] as String)[0].toUpperCase()
                  : '?',
              style: TextStyle(
                  fontSize: 10,
                  fontWeight: FontWeight.w700,
                  color: isDeactivated ? _kTextMuted : _kPrimary),
            ),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Flexible(
                      child: Text(member['userName'] ?? '-',
                          style: TextStyle(
                              fontSize: 10,
                              fontWeight: FontWeight.w600,
                              color: isDeactivated
                                  ? _kTextMuted
                                  : _kTextPrimary),
                          overflow: TextOverflow.ellipsis),
                    ),
                    if (isDeactivated) ...[
                      const SizedBox(width: 6),
                      Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 6, vertical: 2),
                        decoration: BoxDecoration(
                          color: const Color(0xFFFEF3C7),
                          borderRadius: BorderRadius.circular(4),
                          border:
                          Border.all(color: const Color(0xFFFCD34D)),
                        ),
                        child: const Text('Deactivated',
                            style: TextStyle(
                                fontSize: 9,
                                fontWeight: FontWeight.w700,
                                color: _kOrange)),
                      ),
                    ],
                  ],
                ),
                Text('${member['clientId'] ?? ''} · ${member['brokerType'] ?? ''}',
                    style: TextStyle(
                        fontSize: 11,
                        color: isDeactivated
                            ? _kTextMuted
                            : _kTextSecondary)),
              ],
            ),
          ),
          Container(
            padding:
            const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
            decoration: BoxDecoration(
              color: isDeactivated
                  ? const Color(0xFFF3F4F6)
                  : _kPrimaryBg,
              borderRadius: BorderRadius.circular(6),
            ),
            child: Text(member['brokerType'] ?? '',
                style: TextStyle(
                    fontSize: 10,
                    fontWeight: FontWeight.w600,
                    color: isDeactivated ? _kTextMuted : _kPrimary)),
          ),
        ],
      ),
    );
  }
}

class _FormField extends StatelessWidget {
  final TextEditingController controller;
  final String label, hint;
  final IconData icon;
  final int maxLines;
  final TextInputType? keyboardType;
  final String? Function(String?)? validator;
  final bool enabled;
  const _FormField({
    required this.controller,
    required this.label,
    required this.hint,
    required this.icon,
    this.maxLines = 1,
    this.keyboardType,
    this.validator,
    this.enabled = true,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label,
            style: const TextStyle(
                fontSize: 10,
                fontWeight: FontWeight.w600,
                color: Color(0xFF374151))),
        const SizedBox(height: 6),
        TextFormField(
          controller: controller,
          maxLines: maxLines,
          keyboardType: keyboardType,
          enabled: enabled,
          validator: validator,
          style: const TextStyle(fontSize: 13, color: _kTextPrimary),
          decoration: InputDecoration(
            hintText: hint,
            hintStyle:
            const TextStyle(fontSize: 13, color: Color(0xFFD1D5DB)),
            prefixIcon: Icon(icon, size: 16, color: _kTextMuted),
            filled: true,
            fillColor: enabled
                ? const Color(0xFFF9FAFB)
                : const Color(0xFFF3F4F6),
            contentPadding: const EdgeInsets.symmetric(
                horizontal: 14, vertical: 11),
            border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(9),
                borderSide: const BorderSide(color: _kBorder)),
            enabledBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(9),
                borderSide: const BorderSide(color: _kBorder)),
            focusedBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(9),
                borderSide:
                const BorderSide(color: _kPrimary, width: 1.5)),
            errorBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(9),
                borderSide: const BorderSide(color: _kRed)),
            focusedErrorBorder: OutlineInputBorder(
                borderRadius: BorderRadius.circular(9),
                borderSide:
                const BorderSide(color: _kRed, width: 1.5)),
          ),
        ),
      ],
    );
  }
}