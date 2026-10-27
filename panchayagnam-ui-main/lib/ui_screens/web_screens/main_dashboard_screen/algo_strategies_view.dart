import 'package:flutter/material.dart';

class AlgoStrategiesView extends StatefulWidget {
  const AlgoStrategiesView({super.key});

  @override
  State<AlgoStrategiesView> createState() => _AlgoStrategiesViewState();
}

class _AlgoStrategy {
  final String id;
  final String name;
  final String instrument;
  final String timeframe;
  final String indicator;
  final double stopLossPct;
  final double targetPct;
  String status; // 'RUNNING', 'PAUSED', 'DRAFT', 'STOPPED'
  final String createdAt;

  _AlgoStrategy({
    required this.id,
    required this.name,
    required this.instrument,
    required this.timeframe,
    required this.indicator,
    required this.stopLossPct,
    required this.targetPct,
    required this.status,
    required this.createdAt,
  });
}

class _AlgoStrategiesViewState extends State<AlgoStrategiesView> {
  String _selectedFilter = 'ALL';

  final List<_AlgoStrategy> _strategies = [
    _AlgoStrategy(
      id: 'STRAT-001',
      name: 'Nifty EMA 9/21 Trend Crossover',
      instrument: 'NIFTY50-INDEX',
      timeframe: '5m',
      indicator: 'EMA (9, 21) Crossover',
      stopLossPct: 0.5,
      targetPct: 1.5,
      status: 'PAUSED',
      createdAt: '2026-10-25',
    ),
    _AlgoStrategy(
      id: 'STRAT-002',
      name: 'BankNifty RSI Mean Reversion',
      instrument: 'BANKNIFTY-INDEX',
      timeframe: '15m',
      indicator: 'RSI (14) < 30 / > 70',
      stopLossPct: 0.8,
      targetPct: 2.0,
      status: 'DRAFT',
      createdAt: '2026-10-26',
    ),
  ];

  void _showNewStrategyDialog() {
    final nameCtrl = TextEditingController();
    String instrument = 'NIFTY50-INDEX';
    String timeframe = '5m';
    String indicator = 'EMA Crossover (9, 21)';
    double sl = 0.5;
    double target = 1.5;

    showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (context, setDlgState) => AlertDialog(
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          title: const Row(
            children: [
              Icon(Icons.psychology_rounded, color: Color(0xFF7C3AED)),
              SizedBox(width: 8),
              Text('Create Algo Strategy', style: TextStyle(fontSize: 18, fontWeight: FontWeight.w700)),
            ],
          ),
          content: SizedBox(
            width: 480,
            child: SingleChildScrollView(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Text('STRATEGY NAME', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280))),
                  const SizedBox(height: 6),
                  TextField(
                    controller: nameCtrl,
                    decoration: InputDecoration(
                      hintText: 'e.g. Bank Nifty Morning Breakout',
                      border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
                      contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
                    ),
                  ),
                  const SizedBox(height: 16),
                  Row(
                    children: [
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('INSTRUMENT', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280))),
                            const SizedBox(height: 6),
                            DropdownButtonFormField<String>(
                              value: instrument,
                              items: const [
                                DropdownMenuItem(value: 'NIFTY50-INDEX', child: Text('NIFTY 50')),
                                DropdownMenuItem(value: 'BANKNIFTY-INDEX', child: Text('BANK NIFTY')),
                                DropdownMenuItem(value: 'RELIANCE-EQ', child: Text('RELIANCE')),
                              ],
                              onChanged: (val) => setDlgState(() => instrument = val!),
                              decoration: InputDecoration(
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
                                contentPadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('TIMEFRAME', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280))),
                            const SizedBox(height: 6),
                            DropdownButtonFormField<String>(
                              value: timeframe,
                              items: const [
                                DropdownMenuItem(value: '1m', child: Text('1 Minute')),
                                DropdownMenuItem(value: '5m', child: Text('5 Minutes')),
                                DropdownMenuItem(value: '15m', child: Text('15 Minutes')),
                                DropdownMenuItem(value: '1h', child: Text('1 Hour')),
                              ],
                              onChanged: (val) => setDlgState(() => timeframe = val!),
                              decoration: InputDecoration(
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
                                contentPadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),
                  const Text('ENTRY INDICATOR RULE', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280))),
                  const SizedBox(height: 6),
                  DropdownButtonFormField<String>(
                    value: indicator,
                    items: const [
                      DropdownMenuItem(value: 'EMA Crossover (9, 21)', child: Text('EMA Crossover (Fast 9, Slow 21)')),
                      DropdownMenuItem(value: 'RSI Oversold/Overbought (14)', child: Text('RSI (14) < 30 / > 70')),
                      DropdownMenuItem(value: 'MACD Signal Cross', child: Text('MACD (12, 26, 9) Signal Cross')),
                      DropdownMenuItem(value: 'Supertrend (10, 3)', child: Text('Supertrend (10, 3)')),
                    ],
                    onChanged: (val) => setDlgState(() => indicator = val!),
                    decoration: InputDecoration(
                      border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
                      contentPadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
                    ),
                  ),
                  const SizedBox(height: 16),
                  Row(
                    children: [
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('STOP LOSS (%)', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280))),
                            const SizedBox(height: 6),
                            TextFormField(
                              initialValue: '0.5',
                              keyboardType: TextInputType.number,
                              onChanged: (v) => sl = double.tryParse(v) ?? 0.5,
                              decoration: InputDecoration(
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
                                contentPadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('TARGET PROFIT (%)', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280))),
                            const SizedBox(height: 6),
                            TextFormField(
                              initialValue: '1.5',
                              keyboardType: TextInputType.number,
                              onChanged: (v) => target = double.tryParse(v) ?? 1.5,
                              decoration: InputDecoration(
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
                                contentPadding: const EdgeInsets.symmetric(horizontal: 10, vertical: 10),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),
                  Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: const Color(0xFFEDE9FE),
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: const Text(
                      'Strategies created here are saved in local session memory as Drafts. Backend database persistence and cloud execution are scheduled for Phase 4.',
                      style: TextStyle(fontSize: 11.5, color: Color(0xFF5B21B6), height: 1.4),
                    ),
                  ),
                ],
              ),
            ),
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(ctx).pop(),
              child: const Text('Cancel'),
            ),
            ElevatedButton(
              onPressed: () {
                if (nameCtrl.text.trim().isEmpty) return;
                setState(() {
                  _strategies.add(
                    _AlgoStrategy(
                      id: 'STRAT-00${_strategies.length + 1}',
                      name: nameCtrl.text.trim(),
                      instrument: instrument,
                      timeframe: timeframe,
                      indicator: indicator,
                      stopLossPct: sl,
                      targetPct: target,
                      status: 'DRAFT',
                      createdAt: DateTime.now().toIso8601String().substring(0, 10),
                    ),
                  );
                });
                Navigator.of(ctx).pop();
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text('Strategy saved to local session memory'),
                    backgroundColor: Color(0xFF10B981),
                    behavior: SnackBarBehavior.floating,
                  ),
                );
              },
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF7C3AED),
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
              ),
              child: const Text('Save Local Draft'),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final filtered = _selectedFilter == 'ALL'
        ? _strategies
        : _strategies.where((s) => s.status == _selectedFilter).toList();

    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Title & Actions
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'Algo Strategies',
                      style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: Color(0xFF111827)),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Configure visual rule-based trading algorithms for Paper Trading & Backtesting.',
                      style: TextStyle(fontSize: 13, color: Colors.grey.shade600),
                    ),
                  ],
                ),
                ElevatedButton.icon(
                  onPressed: _showNewStrategyDialog,
                  icon: const Icon(Icons.add_rounded, size: 18),
                  label: const Text('New Strategy'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF7C3AED),
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(horizontal: 18, vertical: 12),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),

            // Notice
            Container(
              padding: const EdgeInsets.all(14),
              decoration: BoxDecoration(
                color: const Color(0xFFEDE9FE),
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: const Color(0xFF7C3AED).withOpacity(0.3)),
              ),
              child: const Row(
                children: [
                  Icon(Icons.hub_outlined, color: Color(0xFF7C3AED), size: 20),
                  SizedBox(width: 12),
                  Expanded(
                    child: Text(
                      'Phase 4 Execution Engine: Automated backtesting runners and database persistence are scheduled for Phase 4. Strategies currently save in local session memory.',
                      style: TextStyle(fontSize: 12, color: Color(0xFF5B21B6), fontWeight: FontWeight.w500),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),

            // Summary Cards
            Row(
              children: [
                _buildSummaryBadge('TOTAL STRATEGIES', '${_strategies.length}', const Color(0xFF7C3AED)),
                const SizedBox(width: 14),
                _buildSummaryBadge('ACTIVE RUNNING', '${_strategies.where((s) => s.status == 'RUNNING').length}', const Color(0xFF10B981)),
                const SizedBox(width: 14),
                _buildSummaryBadge('PAUSED', '${_strategies.where((s) => s.status == 'PAUSED').length}', const Color(0xFFF59E0B)),
                const SizedBox(width: 14),
                _buildSummaryBadge('DRAFTS', '${_strategies.where((s) => s.status == 'DRAFT').length}', Colors.grey),
              ],
            ),
            const SizedBox(height: 24),

            // Strategy Cards / Table
            Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: Column(
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                    decoration: BoxDecoration(
                      color: Colors.grey.shade50,
                      borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
                      border: Border(bottom: BorderSide(color: Colors.grey.shade200)),
                    ),
                    child: const Row(
                      children: [
                        Expanded(flex: 3, child: Text('STRATEGY NAME', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        Expanded(flex: 2, child: Text('INSTRUMENT', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        Expanded(flex: 3, child: Text('RULE / INDICATOR', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        Expanded(flex: 2, child: Text('SL / TARGET', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        Expanded(flex: 2, child: Text('STATUS', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        Expanded(flex: 2, child: Text('ACTIONS', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                      ],
                    ),
                  ),
                  if (filtered.isEmpty)
                    Padding(
                      padding: const EdgeInsets.symmetric(vertical: 40),
                      child: Center(
                        child: Text('No strategies found', style: TextStyle(color: Colors.grey.shade500)),
                      ),
                    )
                  else
                    ListView.separated(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: filtered.length,
                      separatorBuilder: (_, __) => Divider(height: 1, color: Colors.grey.shade100),
                      itemBuilder: (ctx, idx) {
                        final s = filtered[idx];
                        return Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
                          child: Row(
                            children: [
                              Expanded(
                                flex: 3,
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(s.name, style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 13.5, color: Color(0xFF111827))),
                                    Text('ID: ${s.id} · Created ${s.createdAt}', style: TextStyle(fontSize: 11, color: Colors.grey.shade500)),
                                  ],
                                ),
                              ),
                              Expanded(
                                flex: 2,
                                child: Text('${s.instrument} (${s.timeframe})', style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600, color: Color(0xFF4B5563))),
                              ),
                              Expanded(
                                flex: 3,
                                child: Text(s.indicator, style: TextStyle(fontSize: 12, color: Colors.grey.shade800)),
                              ),
                              Expanded(
                                flex: 2,
                                child: Text('SL: ${s.stopLossPct}% · TGT: ${s.targetPct}%', style: TextStyle(fontSize: 12, color: Colors.grey.shade700)),
                              ),
                              Expanded(
                                flex: 2,
                                child: Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                  decoration: BoxDecoration(
                                    color: s.status == 'RUNNING'
                                        ? const Color(0xFFD1FAE5)
                                        : s.status == 'PAUSED'
                                            ? const Color(0xFFFEF3C7)
                                            : const Color(0xFFF3F4F6),
                                    borderRadius: BorderRadius.circular(6),
                                  ),
                                  child: Text(
                                    s.status,
                                    style: TextStyle(
                                      fontSize: 10.5,
                                      fontWeight: FontWeight.w700,
                                      color: s.status == 'RUNNING'
                                          ? const Color(0xFF065F46)
                                          : s.status == 'PAUSED'
                                              ? const Color(0xFF92400E)
                                              : Colors.grey.shade700,
                                    ),
                                  ),
                                ),
                              ),
                              Expanded(
                                flex: 2,
                                child: Row(
                                  children: [
                                    IconButton(
                                      icon: Icon(
                                        s.status == 'PAUSED' ? Icons.play_arrow_rounded : Icons.pause_rounded,
                                        size: 18,
                                        color: const Color(0xFF7C3AED),
                                      ),
                                      onPressed: () {
                                        setState(() {
                                          s.status = s.status == 'PAUSED' ? 'DRAFT' : 'PAUSED';
                                        });
                                      },
                                      tooltip: s.status == 'PAUSED' ? 'Resume' : 'Pause',
                                    ),
                                    IconButton(
                                      icon: const Icon(Icons.delete_outline_rounded, size: 18, color: Colors.redAccent),
                                      onPressed: () {
                                        setState(() => _strategies.remove(s));
                                      },
                                      tooltip: 'Delete Strategy',
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                        );
                      },
                    ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSummaryBadge(String label, String value, Color color) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: Colors.grey.shade200),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(label, style: const TextStyle(fontSize: 10.5, fontWeight: FontWeight.w700, color: Color(0xFF6B7280), letterSpacing: 0.5)),
            const SizedBox(height: 6),
            Text(value, style: TextStyle(fontSize: 22, fontWeight: FontWeight.w800, color: color)),
          ],
        ),
      ),
    );
  }
}
