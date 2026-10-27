import 'package:flutter/material.dart';

class BacktestingView extends StatefulWidget {
  const BacktestingView({super.key});

  @override
  State<BacktestingView> createState() => _BacktestingViewState();
}

class _BacktestingViewState extends State<BacktestingView> {
  String _instrument = 'NIFTY50-INDEX';
  String _timeframe = '15m';
  String _strategy = 'EMA 9/21 Trend Crossover';
  double _initialCapital = 100000.0;
  DateTime _startDate = DateTime.now().subtract(const Duration(days: 90));
  DateTime _endDate = DateTime.now();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Title
            const Text(
              'Strategy Backtesting',
              style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: Color(0xFF111827)),
            ),
            const SizedBox(height: 4),
            Text(
              'Simulate algorithmic strategy performance on verified historical market data.',
              style: TextStyle(fontSize: 13, color: Colors.grey.shade600),
            ),
            const SizedBox(height: 20),

            // Provider Integration Status Notice
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: const Color(0xFFFEF3C7),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: const Color(0xFFF59E0B).withOpacity(0.3)),
              ),
              child: Row(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Icon(Icons.history_edu_rounded, color: Color(0xFFB45309), size: 22),
                  const SizedBox(width: 14),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          'Historical Data Provider: Awaiting API Access',
                          style: TextStyle(fontSize: 13, fontWeight: FontWeight.w700, color: Color(0xFF92400E)),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          'Accurate backtesting requires access to authorized minute-level historical OHLCV data (e.g. Angel One SmartAPI Historical Data service). In accordance with data integrity guidelines, synthetic or fabricated backtest curves are disabled until the real data feed is attached in Phase 4.',
                          style: TextStyle(fontSize: 12, color: Colors.amber.shade900, height: 1.4),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),

            // Configuration Panel
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
                  const Text(
                    'BACKTEST CONFIGURATION',
                    style: TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: Color(0xFF6B7280), letterSpacing: 0.5),
                  ),
                  const SizedBox(height: 16),
                  Row(
                    children: [
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('SELECT STRATEGY', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF4B5563))),
                            const SizedBox(height: 6),
                            DropdownButtonFormField<String>(
                              value: _strategy,
                              items: const [
                                DropdownMenuItem(value: 'EMA 9/21 Trend Crossover', child: Text('EMA 9/21 Trend Crossover')),
                                DropdownMenuItem(value: 'RSI Mean Reversion', child: Text('RSI Mean Reversion')),
                                DropdownMenuItem(value: 'Supertrend Trend Following', child: Text('Supertrend Trend Following')),
                              ],
                              onChanged: (v) => setState(() => _strategy = v!),
                              decoration: InputDecoration(
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
                                contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(width: 16),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('INSTRUMENT', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF4B5563))),
                            const SizedBox(height: 6),
                            DropdownButtonFormField<String>(
                              value: _instrument,
                              items: const [
                                DropdownMenuItem(value: 'NIFTY50-INDEX', child: Text('NIFTY 50 Index')),
                                DropdownMenuItem(value: 'BANKNIFTY-INDEX', child: Text('BANK NIFTY Index')),
                                DropdownMenuItem(value: 'RELIANCE-EQ', child: Text('RELIANCE (NSE)')),
                              ],
                              onChanged: (v) => setState(() => _instrument = v!),
                              decoration: InputDecoration(
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
                                contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(width: 16),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('TIMEFRAME CANDLES', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF4B5563))),
                            const SizedBox(height: 6),
                            DropdownButtonFormField<String>(
                              value: _timeframe,
                              items: const [
                                DropdownMenuItem(value: '1m', child: Text('1 Minute')),
                                DropdownMenuItem(value: '5m', child: Text('5 Minutes')),
                                DropdownMenuItem(value: '15m', child: Text('15 Minutes')),
                                DropdownMenuItem(value: '1D', child: Text('1 Day')),
                              ],
                              onChanged: (v) => setState(() => _timeframe = v!),
                              decoration: InputDecoration(
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
                                contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 16),
                  Row(
                    children: [
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('INITIAL CAPITAL (₹)', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF4B5563))),
                            const SizedBox(height: 6),
                            TextFormField(
                              initialValue: '100000',
                              keyboardType: TextInputType.number,
                              onChanged: (v) => _initialCapital = double.tryParse(v) ?? 100000.0,
                              decoration: InputDecoration(
                                border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
                                contentPadding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(width: 16),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text('HISTORICAL RANGE', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF4B5563))),
                            const SizedBox(height: 6),
                            Container(
                              height: 44,
                              padding: const EdgeInsets.symmetric(horizontal: 12),
                              decoration: BoxDecoration(
                                border: Border.all(color: Colors.grey.shade400),
                                borderRadius: BorderRadius.circular(8),
                              ),
                              child: Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Text(
                                    '${_startDate.toIso8601String().substring(0, 10)} to ${_endDate.toIso8601String().substring(0, 10)}',
                                    style: const TextStyle(fontSize: 13, color: Color(0xFF374151)),
                                  ),
                                  const Icon(Icons.date_range_rounded, size: 18, color: Color(0xFF7C3AED)),
                                ],
                              ),
                            ),
                          ],
                        ),
                      ),
                      const SizedBox(width: 16),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const SizedBox(height: 22),
                            SizedBox(
                              width: double.infinity,
                              height: 44,
                              child: ElevatedButton.icon(
                                onPressed: () {
                                  ScaffoldMessenger.of(context).showSnackBar(
                                    const SnackBar(
                                      content: Text('Backtest engine awaiting historical candle data subscription in Phase 4.'),
                                      backgroundColor: Color(0xFF7C3AED),
                                      behavior: SnackBarBehavior.floating,
                                    ),
                                  );
                                },
                                icon: const Icon(Icons.play_arrow_rounded, size: 18),
                                label: const Text('Run Backtest'),
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: const Color(0xFF7C3AED),
                                  foregroundColor: Colors.white,
                                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),

            // Performance Report Placeholder
            Container(
              padding: const EdgeInsets.symmetric(vertical: 40, horizontal: 20),
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: Center(
                child: Column(
                  children: [
                    Icon(Icons.query_stats_rounded, size: 48, color: Colors.grey.shade300),
                    const SizedBox(height: 12),
                    Text(
                      'No Backtest Execution Active',
                      style: TextStyle(fontSize: 15, fontWeight: FontWeight.w700, color: Colors.grey.shade700),
                    ),
                    const SizedBox(height: 6),
                    Text(
                      'Configure strategy parameters above and run backtest once historical data feed is connected.',
                      style: TextStyle(fontSize: 12.5, color: Colors.grey.shade500),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
