import 'package:flutter/material.dart';

// ─── ORDERS VIEW ─────────────────────────────────────────────────────────────

class OrdersView extends StatelessWidget {
  const OrdersView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Orders & Execution', style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: Color(0xFF111827))),
            const SizedBox(height: 4),
            Text('Track pending, executed, cancelled, and paper-simulated orders.', style: TextStyle(fontSize: 13, color: Colors.grey.shade600)),
            const SizedBox(height: 20),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              decoration: BoxDecoration(
                color: const Color(0xFFFEF3C7),
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: const Color(0xFFF59E0B).withOpacity(0.3)),
              ),
              child: const Row(
                children: [
                  Icon(Icons.shield_outlined, color: Color(0xFFB45309), size: 18),
                  SizedBox(width: 10),
                  Expanded(
                    child: Text(
                      'PAPER TRADING ACTIVE — Simulated executions are recorded locally. Live broker orders are disabled.',
                      style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: Color(0xFF92400E)),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(32),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: Column(
                children: [
                  Icon(Icons.receipt_long_rounded, size: 48, color: Colors.grey.shade300),
                  const SizedBox(height: 12),
                  const Text('No Recent Orders Today', style: TextStyle(fontWeight: FontWeight.w700, fontSize: 15)),
                  const SizedBox(height: 6),
                  Text('Orders placed in Market Watch, Baskets, or Algo Trading will appear here.', style: TextStyle(fontSize: 12.5, color: Colors.grey.shade500)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// ─── POSITIONS VIEW ──────────────────────────────────────────────────────────

class PositionsView extends StatelessWidget {
  const PositionsView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Open Positions', style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: Color(0xFF111827))),
            const SizedBox(height: 4),
            Text('Intraday and carry-forward positions across connected accounts.', style: TextStyle(fontSize: 13, color: Colors.grey.shade600)),
            const SizedBox(height: 20),
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(32),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: Column(
                children: [
                  Icon(Icons.pie_chart_outline_rounded, size: 48, color: Colors.grey.shade300),
                  const SizedBox(height: 12),
                  const Text('No Open Positions', style: TextStyle(fontWeight: FontWeight.w700, fontSize: 15)),
                  const SizedBox(height: 6),
                  Text('You have no open positions in your active simulation session.', style: TextStyle(fontSize: 12.5, color: Colors.grey.shade500)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// ─── HOLDINGS VIEW ───────────────────────────────────────────────────────────

class HoldingsView extends StatelessWidget {
  const HoldingsView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Demat Holdings', style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: Color(0xFF111827))),
            const SizedBox(height: 4),
            Text('Long-term delivery holdings confirmed by connected brokers.', style: TextStyle(fontSize: 13, color: Colors.grey.shade600)),
            const SizedBox(height: 20),
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(32),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: Column(
                children: [
                  Icon(Icons.account_balance_wallet_outlined, size: 48, color: Colors.grey.shade300),
                  const SizedBox(height: 12),
                  const Text('No Holdings Synced', style: TextStyle(fontWeight: FontWeight.w700, fontSize: 15)),
                  const SizedBox(height: 6),
                  Text('Connect and authenticate your broker account to view verified Demat holdings.', style: TextStyle(fontSize: 12.5, color: Colors.grey.shade500)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// ─── PAPER TRADING VIEW ──────────────────────────────────────────────────────

class PaperTradingView extends StatelessWidget {
  const PaperTradingView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Paper Trading Engine', style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: Color(0xFF111827))),
            const SizedBox(height: 4),
            Text('Simulated execution environment with zero capital risk.', style: TextStyle(fontSize: 13, color: Colors.grey.shade600)),
            const SizedBox(height: 20),
            Row(
              children: [
                _buildCard('VIRTUAL BALANCE', 'Simulation Mode', 'Zero real funds active', const Color(0xFF7C3AED)),
                const SizedBox(width: 16),
                _buildCard('REALIZED P&L', '₹0.00', 'No live risk', const Color(0xFF10B981)),
                const SizedBox(width: 16),
                _buildCard('SIMULATED TRADES', '0', 'Session total', Colors.blueGrey),
              ],
            ),
            const SizedBox(height: 24),
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: const Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text('PAPER TRADING RULES & ENGINE CONFIGURATION', style: TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: Color(0xFF6B7280))),
                  SizedBox(height: 12),
                  Text('• Virtual margin allocation and persistent ledger tracking are scheduled for the Phase 4 backend engine.\n• Client-side simulation executes in demo mode with zero real capital risk.\n• Real broker APIs are never invoked while PAPER_TRADING_ENABLED=true.', style: TextStyle(fontSize: 13, height: 1.6)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildCard(String title, String val, String sub, Color col) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.all(18),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: Colors.grey.shade200),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(title, style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280))),
            const SizedBox(height: 8),
            Text(val, style: TextStyle(fontSize: 20, fontWeight: FontWeight.w800, color: col)),
            const SizedBox(height: 4),
            Text(sub, style: TextStyle(fontSize: 11, color: Colors.grey.shade500)),
          ],
        ),
      ),
    );
  }
}

// ─── REPORTS VIEW ────────────────────────────────────────────────────────────

class ReportsView extends StatelessWidget {
  const ReportsView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Reports & Audit Log', style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: Color(0xFF111827))),
            const SizedBox(height: 4),
            Text('Searchable execution logs, audit trails, and trade reports.', style: TextStyle(fontSize: 13, color: Colors.grey.shade600)),
            const SizedBox(height: 20),
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(32),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: Column(
                children: [
                  Icon(Icons.assessment_outlined, size: 48, color: Colors.grey.shade300),
                  const SizedBox(height: 12),
                  const Text('No Reports Generated Yet', style: TextStyle(fontWeight: FontWeight.w700, fontSize: 15)),
                  const SizedBox(height: 6),
                  Text('Completed trading sessions will automatically aggregate into trade audit reports.', style: TextStyle(fontSize: 12.5, color: Colors.grey.shade500)),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// ─── NOTIFICATIONS VIEW ──────────────────────────────────────────────────────

class NotificationsView extends StatelessWidget {
  const NotificationsView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('System Notifications', style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: Color(0xFF111827))),
            const SizedBox(height: 4),
            Text('Real-time alerts, order executions, and broker status notifications.', style: TextStyle(fontSize: 13, color: Colors.grey.shade600)),
            const SizedBox(height: 20),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: const Row(
                children: [
                  Icon(Icons.check_circle_rounded, color: Color(0xFF10B981)),
                  SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text('System Connected', style: TextStyle(fontWeight: FontWeight.w700, fontSize: 13.5)),
                        SizedBox(height: 2),
                        Text('Panchayagnam Core Service is active and running in Paper Trading mode.', style: TextStyle(fontSize: 12, color: Colors.grey)),
                      ],
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

// ─── SETTINGS VIEW ───────────────────────────────────────────────────────────

class SettingsView extends StatelessWidget {
  const SettingsView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('Platform Settings', style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: Color(0xFF111827))),
            const SizedBox(height: 4),
            Text('Manage account settings, security safeguards, and connection preferences.', style: TextStyle(fontSize: 13, color: Colors.grey.shade600)),
            const SizedBox(height: 20),
            Container(
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('FINANCIAL SAFEGUARDS', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280))),
                  const SizedBox(height: 12),
                  SwitchListTile(
                    title: const Text('Paper Trading Mode (Simulation Only)', style: TextStyle(fontWeight: FontWeight.w700, fontSize: 14)),
                    subtitle: const Text('Guarantees all orders execute in paper simulation without live broker risk.', style: TextStyle(fontSize: 12)),
                    value: true,
                    onChanged: null, // Locked on for safety
                  ),
                  const Divider(),
                  SwitchListTile(
                    title: const Text('Twilio SMS Gateway', style: TextStyle(fontWeight: FontWeight.w700, fontSize: 14)),
                    subtitle: const Text('Disabled by policy. OTP verification runs via demo allowlist.', style: TextStyle(fontSize: 12)),
                    value: false,
                    onChanged: null, // Locked off for safety
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
