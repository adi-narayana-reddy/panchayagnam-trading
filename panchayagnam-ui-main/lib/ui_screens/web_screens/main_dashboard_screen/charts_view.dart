import 'package:flutter/material.dart';

class ChartsView extends StatefulWidget {
  const ChartsView({super.key});

  @override
  State<ChartsView> createState() => _ChartsViewState();
}

class _ChartsViewState extends State<ChartsView> {
  String _selectedSymbol = 'NIFTY 50';
  String _timeframe = '15m';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('Interactive Charts', style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: Color(0xFF111827))),
                    const SizedBox(height: 4),
                    Text('Technical candlestick analysis with indicator overlays.', style: TextStyle(fontSize: 13, color: Colors.grey.shade600)),
                  ],
                ),
                Row(
                  children: [
                    DropdownButton<String>(
                      value: _selectedSymbol,
                      items: const [
                        DropdownMenuItem(value: 'NIFTY 50', child: Text('NIFTY 50 Index')),
                        DropdownMenuItem(value: 'BANKNIFTY', child: Text('BANK NIFTY Index')),
                        DropdownMenuItem(value: 'RELIANCE', child: Text('RELIANCE (NSE)')),
                      ],
                      onChanged: (v) => setState(() => _selectedSymbol = v!),
                    ),
                    const SizedBox(width: 14),
                    SegmentedButton<String>(
                      segments: const [
                        ButtonSegment(value: '5m', label: Text('5m')),
                        ButtonSegment(value: '15m', label: Text('15m')),
                        ButtonSegment(value: '1h', label: Text('1h')),
                        ButtonSegment(value: '1D', label: Text('1D')),
                      ],
                      selected: {_timeframe},
                      onSelectionChanged: (set) => setState(() => _timeframe = set.first),
                    ),
                  ],
                ),
              ],
            ),
            const SizedBox(height: 20),

            // Chart Canvas Simulation Container
            Container(
              height: 460,
              width: double.infinity,
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade200),
              ),
              padding: const EdgeInsets.all(20),
              child: Column(
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Row(
                        children: [
                          Text(_selectedSymbol, style: const TextStyle(fontWeight: FontWeight.w800, fontSize: 16)),
                          const SizedBox(width: 8),
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                            decoration: BoxDecoration(color: Colors.green.shade50, borderRadius: BorderRadius.circular(4)),
                            child: const Text('NSE', style: TextStyle(fontSize: 10.5, fontWeight: FontWeight.w700, color: Color(0xFF166534))),
                          ),
                          const SizedBox(width: 12),
                          Text('Interval: $_timeframe', style: TextStyle(fontSize: 12, color: Colors.grey.shade600)),
                        ],
                      ),
                      Row(
                        children: [
                          Icon(Icons.candlestick_chart, color: Colors.purple.shade700, size: 20),
                          const SizedBox(width: 6),
                          const Text('Candlesticks', style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
                        ],
                      ),
                    ],
                  ),
                  const Spacer(),
                  // Chart Empty / Awaiting Feed State
                  Icon(Icons.show_chart_rounded, size: 56, color: Colors.grey.shade300),
                  const SizedBox(height: 12),
                  Text(
                    'Real-Time WebSocket Feed Awaiting Connection',
                    style: TextStyle(fontWeight: FontWeight.w700, fontSize: 14, color: Colors.grey.shade700),
                  ),
                  const SizedBox(height: 6),
                  Text(
                    'Candlestick rendering is ready for Phase 3 broker streaming integration. Showing $_selectedSymbol on $_timeframe.',
                    style: TextStyle(fontSize: 12, color: Colors.grey.shade500),
                  ),
                  const Spacer(),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
