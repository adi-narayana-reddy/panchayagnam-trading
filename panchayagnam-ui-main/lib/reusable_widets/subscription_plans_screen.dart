import 'dart:html' as html;

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../bloc/subscreption_status_bloc/subscreption_status_bloc.dart';
import '../bloc/subsecrption_bloc/subscription_bloc.dart'
    hide SubscriptionInactive;

// ─────────────────────────────────────────────────────────────────────────────
//  SubscriptionPlansScreen
// ─────────────────────────────────────────────────────────────────────────────

class SubscriptionPlansScreen extends StatefulWidget {
  final int userId;
  final bool isGroup;

  const SubscriptionPlansScreen({
    super.key,
    required this.userId,
    required this.isGroup,
  });

  @override
  State<SubscriptionPlansScreen> createState() =>
      _SubscriptionPlansScreenState();
}

class _SubscriptionPlansScreenState extends State<SubscriptionPlansScreen> {
  static const Color _purple = Color(0xFF5B3FD9);
  static const Color _bgColor = Color(0xFFEEEEF8);
  static const Color _textGrey = Color(0xFF888888);
  static const double _ppm = 359.0; // price per client per month

  // 3 rows (durations) × 3 columns (tiers) = 9 cells
  static const List<int> _durations = [1, 6, 12];
  static const List<int> _tiers = [20, 50, 100];

  int _maxAllowedClients = 0;

  @override
  void initState() {
    super.initState();
    _readMaxClientsFromSession();
  }

  void _readMaxClientsFromSession() {
    try {
      final raw = html.window.sessionStorage['noOfClients'];
      if (raw != null) {
        final parsed = int.tryParse(raw);
        if (parsed != null && parsed > 0) {
          setState(() => _maxAllowedClients = parsed);
        }
      }
    } catch (_) {}
  }

  String get _requestType => widget.isGroup ? 'ADMIN' : 'CLIENT';

  // planType key sent to BLoC:  "<months>_<clients>"  e.g. "1_20", "6_50"
  String _planKey(int months, int clients) => '${months}_$clients';

  double _totalAmount(int months, int clients) => _ppm * clients * months;

  // Indian-style number formatting  ₹1,07,700
  String _formatINR(double amount) {
    final s = amount.toInt().toString();
    if (s.length <= 3) return '₹$s';
    final last3 = s.substring(s.length - 3);
    final rest = s.substring(0, s.length - 3);
    final buf = StringBuffer();
    for (int i = 0; i < rest.length; i++) {
      if (i != 0 && (rest.length - i) % 2 == 0) buf.write(',');
      buf.write(rest[i]);
    }
    return '₹$buf,$last3';
  }

  // ── Dialog helpers ────────────────────────────────────────────────────────

  void _onBuyTap(BuildContext context, int months, int clients,
      String? selectedPlan, bool isLoading) {
    if (isLoading) return;
    final key = _planKey(months, clients);
    if (selectedPlan != key) {
      context.read<SubscriptionBloc>().add(SelectPlan(key));
    }
    _showConfirmDialog(context, months, clients);
  }

  void _showConfirmDialog(BuildContext context, int months, int clients) {
    final tierName = clients == 20
        ? 'Starter'
        : clients == 50
        ? 'Pro'
        : 'Institutional';
    final planName =
        '$tierName — $months Month${months > 1 ? 's' : ''} / $clients Clients';
    final amount = _totalAmount(months, clients);
    final priceLabel =
        '${_formatINR(amount)} / $months month${months > 1 ? 's' : ''} ($clients clients)';
    final bloc = context.read<SubscriptionBloc>();

    showDialog(
      context: context,
      barrierColor: Colors.black45,
      builder: (ctx) => _ConfirmDialog(
        planName: planName,
        price: priceLabel,
        noOfClients: clients,
        totalAmount: amount,
        isGroup: widget.isGroup,
        onConfirm: () {
          Navigator.of(ctx).pop();
          bloc.add(InitiatePayment(
            _planKey(months, clients),
            requestType: _requestType,
            planMonths: months,
            amount: amount,
            noOfClients: clients,
          ));
        },
        onCancel: () => Navigator.of(ctx).pop(),
      ),
    );
  }

  void _showSuccessDialog(
      BuildContext context, SubscriptionRequestSuccess state) {
    showDialog(
      context: context,
      barrierDismissible: false,
      barrierColor: Colors.black45,
      builder: (_) => _SuccessDialog(state: state, isGroup: widget.isGroup),
    );
  }

  void _showErrorDialog(BuildContext context, String message) {
    showDialog(
      context: context,
      barrierColor: Colors.black45,
      builder: (_) => _ErrorDialog(message: message),
    );
  }

  // ── build ─────────────────────────────────────────────────────────────────

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) => SubscriptionStatusBloc()
        ..add(FetchSubscriptionStatus(
            widget.userId, widget.isGroup ? 'ADMIN' : 'CLIENT')),
      child: BlocConsumer<SubscriptionBloc, SubscriptionState>(
        listener: (context, state) {
          if (state is SubscriptionRequestSuccess) {
            context.read<SubscriptionStatusBloc>().add(FetchSubscriptionStatus(
                widget.userId, widget.isGroup ? 'ADMIN' : 'CLIENT'));
            _showSuccessDialog(context, state);
          } else if (state is SubscriptionRequestFailure) {
            context.read<SubscriptionStatusBloc>().add(FetchSubscriptionStatus(
                widget.userId, widget.isGroup ? 'ADMIN' : 'CLIENT'));
            _showErrorDialog(context, state.message);
          }
        },
        builder: (context, state) {
          String? selectedPlan;
          if (state is SubscriptionPaymentInitiated) {
            selectedPlan = state.planType;
          }
          if (state is SubscriptionRequestSuccess) {
            selectedPlan = state.planType;
          }
          if (state is SubscriptionRequestFailure) selectedPlan = null;
          final isLoading = state is SubscriptionPaymentInitiated;

          return Container(
            color: _bgColor,
            child: SingleChildScrollView(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  // ── Status Banner ───────────────────────────────
                  BlocBuilder<SubscriptionStatusBloc, SubscriptionStatusState>(
                    builder: (context, ss) {
                      if (ss is SubscriptionStatusLoading) {
                        return _StatusBanner(
                          color: const Color(0xFF6B7280),
                          icon: Icons.hourglass_top_rounded,
                          message: 'Checking your subscription status...',
                          isLoading: true,
                        );
                      }
                      if (ss is SubscriptionPending) {
                        return _StatusBanner(
                          color: const Color(0xFFF59E0B),
                          icon: Icons.access_time_rounded,
                          message:
                          'Your subscription request is pending for approval.',
                        );
                      }
                      if (ss is SubscriptionApproved) {
                        return _StatusBanner(
                          color: const Color(0xFF10B981),
                          icon: Icons.verified_rounded,
                          message: widget.isGroup
                              ? 'You have an active subscription. Group trading is fully enabled!'
                              : 'You have an active subscription. Portfolio access is fully enabled!',
                          subMessage: 'For changes Logout and Login again....',
                        );
                      }
                      if (ss is SubscriptionRejected) {
                        return _StatusBanner(
                          color: const Color(0xFFEF4444),
                          icon: Icons.cancel_rounded,
                          message:
                          'Your previous request was rejected. You may re-apply below.',
                          subMessage: 'Reason: Contact support for details.',
                        );
                      }
                      if (ss is SubscriptionStatusError) {
                        return _StatusBanner(
                          color: const Color(0xFFEF4444),
                          icon: Icons.error_rounded,
                          message: 'Failed to load status: ${ss.message}',
                        );
                      }
                      return const SizedBox.shrink();
                    },
                  ),

                  // ── Page header ──────────────────────────────────
                  const SizedBox(height: 36),
                  Text(
                    widget.isGroup
                        ? 'GROUP TRADING PLANS'
                        : 'INDIVIDUAL PORTFOLIO PLANS',
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                      fontSize: 13,
                      fontWeight: FontWeight.w600,
                      color: _purple,
                      letterSpacing: 2.5,
                    ),
                  ),
                  const SizedBox(height: 8),
                  Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 60),
                    child: Text(
                      widget.isGroup
                          ? 'Place Buy & Sell orders for group members with a single click. '
                          'Real-time portfolio monitoring, admin-approved access, and secure group trade execution.'
                          : 'View and track the complete portfolio of all your clients in one place. '
                          'Real-time P&L, holdings, and trade history — admin-approved and securely managed.',
                      textAlign: TextAlign.center,
                      style: const TextStyle(
                        fontSize: 13.5,
                        color: _textGrey,
                        height: 1.6,
                      ),
                    ),
                  ),
                  const SizedBox(height: 6),
                  const Text(
                    '₹359 per client · per month',
                    textAlign: TextAlign.center,
                    style: TextStyle(
                      fontSize: 12,
                      color: _purple,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  const SizedBox(height: 28),

                  // ── 9-Box Grid ───────────────────────────────────
                  Padding(
                    padding: const EdgeInsets.fromLTRB(32, 0, 32, 40),
                    child: _buildGrid(context, selectedPlan, isLoading),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  // ── Grid builder ──────────────────────────────────────────────────────────

  Widget _buildGrid(
      BuildContext context, String? selectedPlan, bool isLoading) {
    const tierLabels = ['20 Clients', '50 Clients', '100 Clients'];
    const tierIcons = [
      Icons.person_rounded,
      Icons.group_rounded,
      Icons.corporate_fare_rounded,
    ];

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // ── Column headers ───────────────────────────────────────
        Row(
          children: [
            // Empty cell above row-label column
            const SizedBox(width: 110),
            ...List.generate(_tiers.length, (ci) {
              return Expanded(
                child: Padding(
                  padding: EdgeInsets.only(
                    left: ci == 0 ? 0 : 6,
                    right: ci == 2 ? 0 : 6,
                  ),
                  child: Container(
                    padding: const EdgeInsets.symmetric(vertical: 9),
                    decoration: BoxDecoration(
                      color: const Color(0xFFDDDAF5),
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(tierIcons[ci],
                            size: 13, color: const Color(0xFF3C3489)),
                        const SizedBox(width: 5),
                        Text(
                          tierLabels[ci],
                          style: const TextStyle(
                            fontSize: 11,
                            fontWeight: FontWeight.w700,
                            color: Color(0xFF3C3489),
                            letterSpacing: 0.3,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
              );
            }),
          ],
        ),

        const SizedBox(height: 10),

        // ── Rows (one per duration) ──────────────────────────────
        ...List.generate(_durations.length, (ri) {
          final months = _durations[ri];
          return Padding(
            padding: const EdgeInsets.only(bottom: 10),
            child: IntrinsicHeight(
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  // Row label
                  SizedBox(
                    width: 110,
                    child: Padding(
                      padding: const EdgeInsets.only(right: 10),
                      child: Container(
                        decoration: BoxDecoration(
                          color: const Color(0xFFDDDAF5),
                          borderRadius: BorderRadius.circular(10),
                        ),
                        padding: const EdgeInsets.symmetric(
                            horizontal: 10, vertical: 12),
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            Text(
                              months == 1
                                  ? '1'
                                  : months == 6
                                  ? '6'
                                  : '12',
                              style: const TextStyle(
                                fontSize: 24,
                                fontWeight: FontWeight.w900,
                                color: Color(0xFF3C3489),
                                height: 1,
                              ),
                            ),
                            Text(
                              'Month${months > 1 ? 's' : ''}',
                              style: const TextStyle(
                                fontSize: 11,
                                fontWeight: FontWeight.w600,
                                color: Color(0xFF534AB7),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),

                  // 3 plan cards for this row
                  ...List.generate(_tiers.length, (ci) {
                    final clients = _tiers[ci];
                    final key = _planKey(months, clients);
                    final isSelected = selectedPlan == key;
                    final isCellLoading = isLoading && selectedPlan == key;
                    final isPopular = months == 6 && clients == 50;

                    return Expanded(
                      child: Padding(
                        padding: EdgeInsets.only(
                          left: ci == 0 ? 0 : 6,
                          right: ci == 2 ? 0 : 6,
                        ),
                        child: _PlanCard(
                          months: months,
                          clients: clients,
                          formattedTotal:
                          _formatINR(_totalAmount(months, clients)),
                          ppm: _ppm,
                          isSelected: isSelected,
                          isLoading: isCellLoading,
                          isPopular: isPopular,
                          isGroup: widget.isGroup,
                          onCardTap: () => context
                              .read<SubscriptionBloc>()
                              .add(SelectPlan(key)),
                          onBuyTap: () => _onBuyTap(
                              context, months, clients, selectedPlan, isLoading),
                        ),
                      ),
                    );
                  }),
                ],
              ),
            ),
          );
        }),
      ],
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
//  _PlanCard
// ─────────────────────────────────────────────────────────────────────────────

class _PlanCard extends StatelessWidget {
  final int months;
  final int clients;
  final String formattedTotal;
  final double ppm;
  final bool isSelected;
  final bool isLoading;
  final bool isPopular;
  final bool isGroup;
  final VoidCallback onCardTap;
  final VoidCallback onBuyTap;

  static const Color _purple = Color(0xFF5B3FD9);

  const _PlanCard({
    required this.months,
    required this.clients,
    required this.formattedTotal,
    required this.ppm,
    required this.isSelected,
    required this.isLoading,
    required this.isPopular,
    required this.isGroup,
    required this.onCardTap,
    required this.onBuyTap,
  });

  String get _tierLabel {
    if (clients == 20) return 'STARTER';
    if (clients == 50) return 'PRO';
    return 'INSTITUTIONAL';
  }

  String get _ctaLabel => isGroup ? 'START TRADING' : 'GET ACCESS';

  @override
  Widget build(BuildContext context) {
    final Color cardBg = isPopular ? _purple : Colors.white;
    final Color primaryText = isPopular ? Colors.white : _purple;
    final Color secondaryText =
    isPopular ? const Color(0xFFCCC5F0) : const Color(0xFF888888);
    final Color dividerColor =
    isPopular ? Colors.white.withOpacity(0.2) : const Color(0xFFE5E5F0);
    final Color pillBg =
    isPopular ? Colors.white.withOpacity(0.2) : const Color(0xFFEEEBFA);
    final Color btnBg = isPopular ? Colors.white : _purple;
    final Color btnText = isPopular ? _purple : Colors.white;

    return GestureDetector(
      onTap: onCardTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 180),
        decoration: BoxDecoration(
          color: cardBg,
          borderRadius: BorderRadius.circular(16),
          border: isPopular
              ? null
              : Border.all(
            color: isSelected ? _purple : const Color(0xFFB0A8E8),
            width: isSelected ? 2.0 : 1.5,
          ),
          boxShadow: isPopular
              ? [
            BoxShadow(
              color: _purple.withOpacity(0.35),
              blurRadius: 20,
              offset: const Offset(0, 6),
            ),
          ]
              : [
            BoxShadow(
              color: Colors.black.withOpacity(0.05),
              blurRadius: 8,
              offset: const Offset(0, 3),
            ),
          ],
        ),
        child: Padding(
          padding: const EdgeInsets.fromLTRB(12, 12, 12, 12),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              // ── Tier pill + popular badge ────────────────────
              Row(
                children: [
                  Expanded(
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 8, vertical: 5),
                      decoration: BoxDecoration(
                        color: pillBg,
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: Text(
                        _tierLabel,
                        textAlign: TextAlign.center,
                        style: TextStyle(
                          fontSize: 9,
                          fontWeight: FontWeight.w700,
                          color: primaryText,
                          letterSpacing: 1.0,
                        ),
                      ),
                    ),
                  ),
                  if (isPopular) ...[
                    const SizedBox(width: 4),
                    Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 6, vertical: 3),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(20),
                      ),
                      child: const Text(
                        '★ POPULAR',
                        style: TextStyle(
                          fontSize: 7,
                          fontWeight: FontWeight.w800,
                          color: _purple,
                          letterSpacing: 0.3,
                        ),
                      ),
                    ),
                  ],
                ],
              ),

              const SizedBox(height: 10),

              // ── Total price ──────────────────────────────────
              FittedBox(
                fit: BoxFit.scaleDown,
                alignment: Alignment.centerLeft,
                child: Text(
                  formattedTotal,
                  style: TextStyle(
                    fontSize: 20,
                    fontWeight: FontWeight.w900,
                    color: primaryText,
                    height: 1,
                  ),
                ),
              ),
              const SizedBox(height: 3),
              Text(
                'for $months month${months > 1 ? 's' : ''}',
                style: TextStyle(
                  fontSize: 10,
                  color: secondaryText,
                  fontWeight: FontWeight.w500,
                ),
              ),

              const SizedBox(height: 8),
              Divider(color: dividerColor, height: 1),
              const SizedBox(height: 8),

              // ── Breakdown ────────────────────────────────────
              Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Icon(Icons.people_alt_rounded,
                      size: 11, color: secondaryText),
                  const SizedBox(width: 4),
                  Expanded(
                    child: Text(
                      '₹${ppm.toInt()} × $clients × $months mo',
                      style: TextStyle(fontSize: 10, color: secondaryText),
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ],
              ),

              const SizedBox(height: 10),

              // ── CTA button ───────────────────────────────────
              GestureDetector(
                onTap: onBuyTap,
                child: Container(
                  width: double.infinity,
                  padding: const EdgeInsets.symmetric(vertical: 9),
                  decoration: BoxDecoration(
                    color: btnBg,
                    borderRadius: BorderRadius.circular(24),
                  ),
                  child: Center(
                    child: isLoading
                        ? SizedBox(
                      width: 13,
                      height: 13,
                      child: CircularProgressIndicator(
                          strokeWidth: 2, color: btnText),
                    )
                        : Text(
                      _ctaLabel,
                      style: TextStyle(
                        fontSize: 9,
                        fontWeight: FontWeight.w800,
                        color: btnText,
                        letterSpacing: 0.8,
                      ),
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
//  _StatusBanner
// ─────────────────────────────────────────────────────────────────────────────

class _StatusBanner extends StatelessWidget {
  final Color color;
  final IconData icon;
  final String message;
  final String? subMessage;
  final bool isLoading;

  const _StatusBanner({
    required this.color,
    required this.icon,
    required this.message,
    this.subMessage,
    this.isLoading = false,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.fromLTRB(32, 20, 32, 0),
      padding: const EdgeInsets.symmetric(horizontal: 18, vertical: 14),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: color.withOpacity(0.4), width: 1.5),
      ),
      child: Row(
        children: [
          isLoading
              ? SizedBox(
            width: 22,
            height: 22,
            child: CircularProgressIndicator(
                strokeWidth: 2.5, color: color),
          )
              : Icon(icon, color: color, size: 22),
          const SizedBox(width: 14),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  message,
                  style: TextStyle(
                    fontSize: 13,
                    fontWeight: FontWeight.w700,
                    color: color,
                    height: 1.4,
                  ),
                ),
                if (subMessage != null) ...[
                  const SizedBox(height: 3),
                  Text(
                    subMessage!,
                    style: const TextStyle(
                      fontSize: 12,
                      color: Colors.red,
                      fontWeight: FontWeight.w500,
                    ),
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

// ─────────────────────────────────────────────────────────────────────────────
//  _ConfirmDialog
// ─────────────────────────────────────────────────────────────────────────────

class _ConfirmDialog extends StatelessWidget {
  final String planName;
  final String price;
  final int noOfClients;
  final double totalAmount;
  final bool isGroup;
  final VoidCallback onConfirm;
  final VoidCallback onCancel;

  static const Color _purple = Color(0xFF5B3FD9);
  static const Color _teal = Color(0xFF0D9488);

  const _ConfirmDialog({
    required this.planName,
    required this.price,
    required this.noOfClients,
    required this.totalAmount,
    required this.isGroup,
    required this.onConfirm,
    required this.onCancel,
  });

  Color get _headerColor => isGroup ? _purple : _teal;
  IconData get _headerIcon =>
      isGroup ? Icons.shopping_cart_rounded : Icons.pie_chart_rounded;
  String get _headerTitle =>
      isGroup ? 'Confirm Subscription' : 'Activate Portfolio Plan';
  String get _confirmLabel => isGroup ? 'Yes, Activate!' : 'Yes, Get Access!';

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.transparent,
      elevation: 0,
      child: Container(
        width: 340,
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(20),
          boxShadow: [
            BoxShadow(
              color: _headerColor.withOpacity(0.18),
              blurRadius: 30,
              offset: const Offset(0, 10),
            ),
          ],
        ),
        clipBehavior: Clip.hardEdge,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Header
            Container(
              width: double.infinity,
              padding: const EdgeInsets.fromLTRB(20, 18, 20, 18),
              color: _headerColor,
              child: Row(
                children: [
                  Icon(_headerIcon, size: 20, color: Colors.white),
                  const SizedBox(width: 10),
                  Text(
                    _headerTitle,
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w800,
                      color: Colors.white,
                    ),
                  ),
                ],
              ),
            ),
            // Body
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 20, 20, 20),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    isGroup
                        ? 'Are you sure you want to activate this trading plan?'
                        : 'Are you sure you want to activate this portfolio plan?',
                    style: const TextStyle(
                      fontSize: 14,
                      fontWeight: FontWeight.w700,
                      color: Color(0xFF1E1B4B),
                    ),
                  ),
                  const SizedBox(height: 14),
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.all(14),
                    decoration: BoxDecoration(
                      color: isGroup
                          ? const Color(0xFFEEEBFA)
                          : const Color(0xFFE6FAFA),
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(
                        color: isGroup
                            ? const Color(0xFFCCC5F0)
                            : const Color(0xFF99D6D3),
                      ),
                    ),
                    child: Column(
                      children: [
                        _DRow(label: 'Plan', value: planName),
                        const SizedBox(height: 10),
                        Divider(
                          height: 1,
                          color: isGroup
                              ? const Color(0xFFCCC5F0)
                              : const Color(0xFF99D6D3),
                        ),
                        const SizedBox(height: 10),
                        _DRow(
                            label: 'No. of Clients',
                            value: '$noOfClients clients'),
                        const SizedBox(height: 10),
                        Divider(
                          height: 1,
                          color: isGroup
                              ? const Color(0xFFCCC5F0)
                              : const Color(0xFF99D6D3),
                        ),
                        const SizedBox(height: 10),
                        _DRow(label: 'Total Amount', value: price),
                      ],
                    ),
                  ),
                  const SizedBox(height: 18),
                  Row(
                    children: [
                      Expanded(
                        child: InkWell(
                          onTap: onCancel,
                          child: Container(
                            padding:
                            const EdgeInsets.symmetric(vertical: 13),
                            decoration: BoxDecoration(
                              color: const Color(0xFFF0F0F0),
                              borderRadius: BorderRadius.circular(30),
                            ),
                            child: const Center(
                              child: Text(
                                'Cancel',
                                style: TextStyle(
                                  fontSize: 13,
                                  fontWeight: FontWeight.w700,
                                  color: Color(0xFF666666),
                                ),
                              ),
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(width: 10),
                      Expanded(
                        child: InkWell(
                          onTap: onConfirm,
                          child: Container(
                            padding:
                            const EdgeInsets.symmetric(vertical: 13),
                            decoration: BoxDecoration(
                              color: _headerColor,
                              borderRadius: BorderRadius.circular(30),
                            ),
                            child: Center(
                              child: Text(
                                _confirmLabel,
                                style: const TextStyle(
                                  fontSize: 13,
                                  fontWeight: FontWeight.w700,
                                  color: Colors.white,
                                ),
                              ),
                            ),
                          ),
                        ),
                      ),
                    ],
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

// ─────────────────────────────────────────────────────────────────────────────
//  _DRow
// ─────────────────────────────────────────────────────────────────────────────

class _DRow extends StatelessWidget {
  final String label;
  final String value;

  const _DRow({required this.label, required this.value});

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Text(
          label,
          style: const TextStyle(
            fontSize: 13,
            color: Color(0xFF888888),
            fontWeight: FontWeight.w500,
          ),
        ),
        Flexible(
          child: Text(
            value,
            overflow: TextOverflow.ellipsis,
            textAlign: TextAlign.end,
            style: const TextStyle(
              fontSize: 13,
              fontWeight: FontWeight.w700,
              color: Color(0xFF1E1B4B),
            ),
          ),
        ),
      ],
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
//  _SuccessDialog
// ─────────────────────────────────────────────────────────────────────────────

class _SuccessDialog extends StatelessWidget {
  final SubscriptionRequestSuccess state;
  final bool isGroup;

  static const Color _purple = Color(0xFF5B3FD9);
  static const Color _teal = Color(0xFF0D9488);

  const _SuccessDialog({required this.state, required this.isGroup});

  Color get _headerColor => isGroup ? _purple : _teal;

  // planType is now "<months>_<clients>"
  String _planLabel(String planType) {
    final parts = planType.split('_');
    if (parts.length == 2) {
      final months = int.tryParse(parts[0]) ?? 0;
      final clients = int.tryParse(parts[1]) ?? 0;
      final tier = clients == 20
          ? 'Starter'
          : clients == 50
          ? 'Pro'
          : 'Institutional';
      return '$tier — $months Month${months > 1 ? 's' : ''} / $clients Clients';
    }
    return planType;
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.transparent,
      elevation: 0,
      child: Container(
        width: 320,
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(20),
          boxShadow: [
            BoxShadow(
              color: _headerColor.withOpacity(0.18),
              blurRadius: 30,
              offset: const Offset(0, 10),
            ),
          ],
        ),
        clipBehavior: Clip.hardEdge,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: double.infinity,
              padding: const EdgeInsets.fromLTRB(20, 22, 20, 22),
              color: _headerColor,
              child: Column(
                children: [
                  const Icon(Icons.check_circle_rounded,
                      size: 36, color: Colors.white),
                  const SizedBox(height: 8),
                  Text(
                    isGroup ? 'Plan Activated!' : 'Access Activated!',
                    style: const TextStyle(
                      fontSize: 16,
                      fontWeight: FontWeight.w800,
                      color: Colors.white,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    isGroup
                        ? 'Our admin team will review and enable group trading access.'
                        : 'Our admin team will review and enable your portfolio access.',
                    textAlign: TextAlign.center,
                    style: const TextStyle(
                        fontSize: 12, color: Color(0xFFCCC5F0)),
                  ),
                ],
              ),
            ),
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 18, 20, 20),
              child: Column(
                children: [
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.all(14),
                    decoration: BoxDecoration(
                      color: isGroup
                          ? const Color(0xFFEEEBFA)
                          : const Color(0xFFE6FAFA),
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(
                        color: isGroup
                            ? const Color(0xFFCCC5F0)
                            : const Color(0xFF99D6D3),
                      ),
                    ),
                    child: Column(
                      children: [
                        _DRow(
                            label: 'Request ID',
                            value: '#${state.requestId}'),
                        const SizedBox(height: 10),
                        Divider(
                          height: 1,
                          color: isGroup
                              ? const Color(0xFFCCC5F0)
                              : const Color(0xFF99D6D3),
                        ),
                        const SizedBox(height: 10),
                        _DRow(
                          label: 'Plan',
                          value: _planLabel(state.planType),
                        ),
                        const SizedBox(height: 10),
                        Divider(
                          height: 1,
                          color: isGroup
                              ? const Color(0xFFCCC5F0)
                              : const Color(0xFF99D6D3),
                        ),
                        const SizedBox(height: 10),
                        _DRow(label: 'Status', value: state.status),
                      ],
                    ),
                  ),
                  const SizedBox(height: 18),
                  SizedBox(
                    width: double.infinity,
                    height: 46,
                    child: ElevatedButton(
                      onPressed: () => Navigator.of(context).pop(),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: _headerColor,
                        foregroundColor: Colors.white,
                        elevation: 0,
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(30),
                        ),
                      ),
                      child: const Text(
                        'Done  🎉',
                        style: TextStyle(
                            fontSize: 14, fontWeight: FontWeight.w700),
                      ),
                    ),
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

// ─────────────────────────────────────────────────────────────────────────────
//  _ErrorDialog
// ─────────────────────────────────────────────────────────────────────────────

class _ErrorDialog extends StatelessWidget {
  final String message;

  const _ErrorDialog({required this.message});

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.transparent,
      elevation: 0,
      child: Container(
        width: 300,
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(20),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.12),
              blurRadius: 30,
              offset: const Offset(0, 10),
            ),
          ],
        ),
        clipBehavior: Clip.hardEdge,
        child: Padding(
          padding: const EdgeInsets.fromLTRB(20, 16, 20, 20),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(14),
                decoration: BoxDecoration(
                  color: const Color(0xFFFEF2F2),
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: const Color(0xFFFECACA)),
                ),
                child: Text(
                  message,
                  textAlign: TextAlign.center,
                  maxLines: 4,
                  overflow: TextOverflow.ellipsis,
                  style: const TextStyle(
                    fontSize: 13,
                    color: Color(0xFF7F1D1D),
                    height: 1.5,
                  ),
                ),
              ),
              const SizedBox(height: 16),
              SizedBox(
                width: double.infinity,
                height: 46,
                child: ElevatedButton(
                  onPressed: () => Navigator.of(context).pop(),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFFEF4444),
                    foregroundColor: Colors.white,
                    elevation: 0,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(30),
                    ),
                  ),
                  child: const Text(
                    'Close',
                    style:
                    TextStyle(fontSize: 14, fontWeight: FontWeight.w700),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}