import 'package:flutter/material.dart';

import '../../service/group_order_buy_sell_service.dart';

const _kSurface = Color(0xFFFFFFFF);
const _kBg = Color(0xFFFAFAFC);
const _kBorder = Color(0xFFE5E7EB);
const _kTextPrimary = Color(0xFF111827);
const _kTextSecondary = Color(0xFF6B7280);
const _kGreen = Color(0xFF059669);
const _kGreenBg = Color(0xFFD1FAE5);
const _kRed = Color(0xFFDC2626);
const _kRedBg = Color(0xFFFEE2E2);
const _kIndigo = Color(0xFF6366F1);
const _kIndigoBg = Color(0xFFEEF2FF);
const _kAmberBg = Color(0xFFFEF3C7);
const _kAmberBorder = Color(0xFFFCD34D);
const _kAmberText = Color(0xFF92400E);

// ─── Entry point ─────────────────────────────────────────────────────────────

void showGroupOrderResultSheet(
  BuildContext context,
  GroupOrderResponse response,
) {
  showModalBottomSheet(
    context: context,
    isScrollControlled: true,
    backgroundColor: Colors.transparent,
    builder: (_) => GroupOrderResultSheet(response: response),
  );
}

// ─── Sheet widget ─────────────────────────────────────────────────────────────

class GroupOrderResultSheet extends StatelessWidget {
  final GroupOrderResponse response;
  const GroupOrderResultSheet({super.key, required this.response});

  @override
  Widget build(BuildContext context) {
    // Group results by tradingSymbol
    final Map<String, List<GroupOrderResult>> byStock = {};
    for (final r in response.results) {
      byStock.putIfAbsent(r.tradingSymbol, () => []).add(r);
    }

    return Container(
      decoration: const BoxDecoration(
        color: _kBg,
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      constraints: BoxConstraints(
        maxHeight: MediaQuery.of(context).size.height * 0.88,
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          // Drag handle
          Center(
            child: Container(
              width: 36,
              height: 4,
              margin: const EdgeInsets.symmetric(vertical: 10),
              decoration: BoxDecoration(
                color: _kBorder,
                borderRadius: BorderRadius.circular(2),
              ),
            ),
          ),
          // Header
          _buildHeader(context),
          const Divider(height: 1, color: _kBorder),
          // Summary banner
          _buildSummaryBanner(),
          // Stock sections
          Expanded(
            child: ListView(
              padding: const EdgeInsets.fromLTRB(12, 8, 12, 24),
              children: [
                ...byStock.entries.map(
                  (e) =>
                      _StockResultCard(tradingSymbol: e.key, results: e.value),
                ),
                const SizedBox(height: 8),
                // Done button
                SizedBox(
                  width: double.infinity,
                  height: 48,
                  child: ElevatedButton(
                    onPressed: () => Navigator.pop(context),
                    style: ElevatedButton.styleFrom(
                      backgroundColor: _kIndigo,
                      foregroundColor: Colors.white,
                      elevation: 0,
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10),
                      ),
                    ),
                    child: const Text(
                      'DONE',
                      style: TextStyle(
                        fontSize: 14,
                        fontWeight: FontWeight.w800,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildHeader(BuildContext context) {
    final isBuy = response.transactionType == 'BUY';
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 4, 16, 12),
      child: Row(
        children: [
          Container(
            width: 36,
            height: 36,
            decoration: BoxDecoration(
              color: isBuy ? _kGreenBg : _kRedBg,
              shape: BoxShape.circle,
            ),
            child: Icon(
              isBuy ? Icons.trending_up_rounded : Icons.trending_down_rounded,
              color: isBuy ? _kGreen : _kRed,
              size: 18,
            ),
          ),
          const SizedBox(width: 10),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Order Results — ${response.groupName}',
                  style: const TextStyle(
                    fontSize: 13,
                    fontWeight: FontWeight.w700,
                    color: _kTextPrimary,
                  ),
                ),
                Text(
                  '${response.transactionType} · ${response.stockCount} stock${response.stockCount == 1 ? '' : 's'} · ${response.totalOrders} order${response.totalOrders == 1 ? '' : 's'}',
                  style: const TextStyle(fontSize: 11, color: _kTextSecondary),
                ),
              ],
            ),
          ),
          IconButton(
            icon: const Icon(
              Icons.close_rounded,
              color: _kTextSecondary,
              size: 20,
            ),
            onPressed: () => Navigator.pop(context),
          ),
        ],
      ),
    );
  }

  Widget _buildSummaryBanner() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
      decoration: BoxDecoration(
        color: _kSurface,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: _kBorder),
      ),
      child: Row(
        children: [
          _SummaryChip(
            label: 'Success',
            count: response.successCount,
            color: _kGreen,
            bg: _kGreenBg,
          ),
          const SizedBox(width: 8),
          _SummaryChip(
            label: 'Failed',
            count: response.failedCount,
            color: _kRed,
            bg: _kRedBg,
          ),
          const SizedBox(width: 8),
          _SummaryChip(
            label: 'Skipped',
            count: response.skippedCount,
            color: _kAmberText,
            bg: _kAmberBg,
          ),
          const Spacer(),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              const Text(
                'Total',
                style: TextStyle(fontSize: 10, color: _kTextSecondary),
              ),
              Text(
                '${response.totalOrders} orders',
                style: const TextStyle(
                  fontSize: 12,
                  fontWeight: FontWeight.w700,
                  color: _kTextPrimary,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

// ─── Per-stock result card ────────────────────────────────────────────────────

class _StockResultCard extends StatelessWidget {
  final String tradingSymbol;
  final List<GroupOrderResult> results;

  const _StockResultCard({required this.tradingSymbol, required this.results});

  @override
  Widget build(BuildContext context) {
    final name = tradingSymbol.replaceAll('-EQ', '');
    final successCount = results.where((r) => r.isSuccess).length;
    final failedCount = results.where((r) => r.isFailed).length;
    final skippedCount = results.where((r) => r.isSkipped).length;

    return Container(
      margin: const EdgeInsets.only(bottom: 10),
      decoration: BoxDecoration(
        color: _kSurface,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: _kBorder),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // Stock header
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
            decoration: const BoxDecoration(
              color: _kBg,
              borderRadius: BorderRadius.vertical(top: Radius.circular(12)),
            ),
            child: Row(
              children: [
                Container(
                  width: 32,
                  height: 32,
                  decoration: BoxDecoration(
                    color: _avatarColor(name).withOpacity(0.12),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Center(
                    child: Text(
                      name.isNotEmpty ? name[0] : '?',
                      style: TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.w800,
                        color: _avatarColor(name),
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 10),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        name,
                        style: const TextStyle(
                          fontSize: 13,
                          fontWeight: FontWeight.w700,
                          color: _kTextPrimary,
                        ),
                      ),
                      Text(
                        results.first.exchange,
                        style: const TextStyle(
                          fontSize: 10,
                          color: _kTextSecondary,
                        ),
                      ),
                    ],
                  ),
                ),
                // Mini counts
                Row(
                  children: [
                    if (successCount > 0)
                      _MiniCount(
                        count: successCount,
                        color: _kGreen,
                        bg: _kGreenBg,
                        icon: Icons.check_rounded,
                      ),
                    if (failedCount > 0) ...[
                      const SizedBox(width: 4),
                      _MiniCount(
                        count: failedCount,
                        color: _kRed,
                        bg: _kRedBg,
                        icon: Icons.close_rounded,
                      ),
                    ],
                    if (skippedCount > 0) ...[
                      const SizedBox(width: 4),
                      _MiniCount(
                        count: skippedCount,
                        color: _kAmberText,
                        bg: _kAmberBg,
                        icon: Icons.skip_next_rounded,
                      ),
                    ],
                  ],
                ),
              ],
            ),
          ),
          const Divider(height: 1, color: _kBorder),
          // Broker rows
          ...results.asMap().entries.map(
            (e) => Column(
              children: [
                _BrokerResultRow(result: e.value),
                if (e.key < results.length - 1)
                  const Divider(height: 1, color: _kBorder, indent: 14),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Color _avatarColor(String name) {
    const colors = [
      Color(0xFF3F51B5),
      Color(0xFF00897B),
      Color(0xFFE53935),
      Color(0xFFF57C00),
      Color(0xFF8E24AA),
      Color(0xFF039BE5),
      Color(0xFF43A047),
      Color(0xFFD81B60),
    ];
    if (name.isEmpty) return colors[0];
    return colors[name.codeUnitAt(0) % colors.length];
  }
}

// ─── Individual broker result row ─────────────────────────────────────────────

class _BrokerResultRow extends StatelessWidget {
  final GroupOrderResult result;
  const _BrokerResultRow({required this.result});

  @override
  Widget build(BuildContext context) {
    final (
      statusLabel,
      statusColor,
      statusBg,
      statusIcon,
    ) = switch (result.status) {
      'SUCCESS' => ('Success', _kGreen, _kGreenBg, Icons.check_circle_rounded),
      'FAILED' => ('Failed', _kRed, _kRedBg, Icons.cancel_rounded),
      'SKIPPED' => (
        'Skipped',
        _kAmberText,
        _kAmberBg,
        Icons.do_not_disturb_rounded,
      ),
      _ => (result.status, _kTextSecondary, _kBg, Icons.info_outline_rounded),
    };

    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              // Avatar
              CircleAvatar(
                radius: 14,
                backgroundColor: _kIndigoBg,
                child: Text(
                  result.userName.isNotEmpty
                      ? result.userName
                            .split(' ')
                            .where(
                              (w) => w.isNotEmpty,
                            ) // guard against empty segments
                            .take(2)
                            .map((w) => w[0])
                            .join()
                            .toUpperCase()
                      : '?',
                  style: const TextStyle(
                    fontSize: 9,
                    fontWeight: FontWeight.w700,
                    color: _kIndigo,
                  ),
                ),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      result.userName,
                      style: const TextStyle(
                        fontSize: 12,
                        fontWeight: FontWeight.w600,
                        color: _kTextPrimary,
                      ),
                    ),
                    Text(
                      '${result.clientId} · ${result.brokerType} · Qty ${result.quantity}',
                      style: const TextStyle(
                        fontSize: 10,
                        color: _kTextSecondary,
                      ),
                    ),
                  ],
                ),
              ),
              // Status badge
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: statusBg,
                  borderRadius: BorderRadius.circular(6),
                ),
                child: Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Icon(statusIcon, size: 11, color: statusColor),
                    const SizedBox(width: 4),
                    Text(
                      statusLabel,
                      style: TextStyle(
                        fontSize: 10,
                        fontWeight: FontWeight.w700,
                        color: statusColor,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
          // Order ID
          if (result.orderId != null) ...[
            const SizedBox(height: 4),
            Padding(
              padding: const EdgeInsets.only(left: 38),
              child: Row(
                children: [
                  const Icon(
                    Icons.receipt_long_rounded,
                    size: 11,
                    color: _kTextSecondary,
                  ),
                  const SizedBox(width: 4),
                  Text(
                    'Order ID: ${result.orderId}',
                    style: const TextStyle(
                      fontSize: 10,
                      color: _kTextSecondary,
                    ),
                  ),
                ],
              ),
            ),
          ],
          // Remarks (error message)
          if (result.remarks != null && result.remarks!.isNotEmpty) ...[
            const SizedBox(height: 4),
            Padding(
              padding: const EdgeInsets.only(left: 38),
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 5),
                decoration: BoxDecoration(
                  color: result.isFailed ? _kRedBg : _kAmberBg,
                  borderRadius: BorderRadius.circular(6),
                  border: Border.all(
                    color: result.isFailed
                        ? _kRed.withOpacity(0.3)
                        : _kAmberBorder,
                  ),
                ),
                child: Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Icon(
                      Icons.info_outline_rounded,
                      size: 11,
                      color: result.isFailed ? _kRed : _kAmberText,
                    ),
                    const SizedBox(width: 5),
                    Expanded(
                      child: Text(
                        result.remarks!,
                        style: TextStyle(
                          fontSize: 10,
                          color: result.isFailed ? _kRed : _kAmberText,
                          height: 1.4,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ],
      ),
    );
  }
}

// ─── Helper widgets ───────────────────────────────────────────────────────────

class _SummaryChip extends StatelessWidget {
  final String label;
  final int count;
  final Color color, bg;

  const _SummaryChip({
    required this.label,
    required this.count,
    required this.color,
    required this.bg,
  });

  @override
  Widget build(BuildContext context) => Container(
    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
    decoration: BoxDecoration(
      color: bg,
      borderRadius: BorderRadius.circular(8),
    ),
    child: Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Text(
          '$count',
          style: TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.w800,
            color: color,
          ),
        ),
        Text(
          label,
          style: TextStyle(
            fontSize: 9,
            fontWeight: FontWeight.w600,
            color: color,
          ),
        ),
      ],
    ),
  );
}

class _MiniCount extends StatelessWidget {
  final int count;
  final Color color, bg;
  final IconData icon;

  const _MiniCount({
    required this.count,
    required this.color,
    required this.bg,
    required this.icon,
  });

  @override
  Widget build(BuildContext context) => Container(
    padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 3),
    decoration: BoxDecoration(
      color: bg,
      borderRadius: BorderRadius.circular(5),
    ),
    child: Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(icon, size: 10, color: color),
        const SizedBox(width: 3),
        Text(
          '$count',
          style: TextStyle(
            fontSize: 10,
            fontWeight: FontWeight.w700,
            color: color,
          ),
        ),
      ],
    ),
  );
}
