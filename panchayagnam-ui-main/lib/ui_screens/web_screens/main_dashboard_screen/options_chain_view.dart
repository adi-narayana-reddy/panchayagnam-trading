import 'package:flutter/material.dart';

class OptionsChainView extends StatefulWidget {
  const OptionsChainView({super.key});

  @override
  State<OptionsChainView> createState() => _OptionsChainViewState();
}

class _OptionsChainViewState extends State<OptionsChainView> {
  String _underlying = 'NIFTY';
  String _expiry = '31-OCT-2026';

  final List<double> _strikes = [
    24000.0,
    24050.0,
    24100.0,
    24150.0,
    24200.0,
    24250.0,
    24300.0,
    24350.0,
    24400.0,
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'Options Chain (F&O)',
                      style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: Color(0xFF111827)),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'NSE Derivative Chain with Calls, Strikes, Puts & Open Interest.',
                      style: TextStyle(fontSize: 13, color: Colors.grey.shade600),
                    ),
                  ],
                ),
                Row(
                  children: [
                    DropdownButton<String>(
                      value: _underlying,
                      items: const [
                        DropdownMenuItem(value: 'NIFTY', child: Text('NIFTY')),
                        DropdownMenuItem(value: 'BANKNIFTY', child: Text('BANKNIFTY')),
                        DropdownMenuItem(value: 'FINNIFTY', child: Text('FINNIFTY')),
                      ],
                      onChanged: (v) => setState(() => _underlying = v!),
                    ),
                    const SizedBox(width: 16),
                    DropdownButton<String>(
                      value: _expiry,
                      items: const [
                        DropdownMenuItem(value: '31-OCT-2026', child: Text('31-OCT-2026 (Weekly)')),
                        DropdownMenuItem(value: '07-NOV-2026', child: Text('07-NOV-2026 (Weekly)')),
                        DropdownMenuItem(value: '28-NOV-2026', child: Text('28-NOV-2026 (Monthly)')),
                      ],
                      onChanged: (v) => setState(() => _expiry = v!),
                    ),
                  ],
                ),
              ],
            ),
            const SizedBox(height: 20),

            // Provider Warning Notice
            Container(
              padding: const EdgeInsets.all(14),
              decoration: BoxDecoration(
                color: const Color(0xFFEDE9FE),
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: const Color(0xFF7C3AED).withOpacity(0.3)),
              ),
              child: const Row(
                children: [
                  Icon(Icons.info_outline_rounded, color: Color(0xFF7C3AED), size: 20),
                  SizedBox(width: 12),
                  Expanded(
                    child: Text(
                      'Option Chain Matrix: Strike grid rendered from NSE derivative contract specifications. Live LTP, Change %, and Open Interest streaming feeds will be connected in Phase 3 broker integration.',
                      style: TextStyle(fontSize: 12, color: Color(0xFF5B21B6)),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),

            // Options Table
            Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: Column(
                children: [
                  // Super Header
                  Row(
                    children: [
                      Expanded(
                        flex: 5,
                        child: Container(
                          padding: const EdgeInsets.symmetric(vertical: 8),
                          decoration: const BoxDecoration(
                            color: Color(0xFFDCFCE7),
                            borderRadius: BorderRadius.only(topLeft: Radius.circular(12)),
                          ),
                          child: const Center(
                            child: Text(
                              'CALL OPTIONS (CE)',
                              style: TextStyle(fontSize: 11, fontWeight: FontWeight.w800, color: Color(0xFF166534)),
                            ),
                          ),
                        ),
                      ),
                      Expanded(
                        flex: 2,
                        child: Container(
                          padding: const EdgeInsets.symmetric(vertical: 8),
                          color: const Color(0xFFF3F4F6),
                          child: const Center(
                            child: Text(
                              'STRIKE',
                              style: TextStyle(fontSize: 11, fontWeight: FontWeight.w800, color: Color(0xFF374151)),
                            ),
                          ),
                        ),
                      ),
                      Expanded(
                        flex: 5,
                        child: Container(
                          padding: const EdgeInsets.symmetric(vertical: 8),
                          decoration: const BoxDecoration(
                            color: Color(0xFFFEE2E2),
                            borderRadius: BorderRadius.only(topRight: Radius.circular(12)),
                          ),
                          child: const Center(
                            child: Text(
                              'PUT OPTIONS (PE)',
                              style: TextStyle(fontSize: 11, fontWeight: FontWeight.w800, color: Color(0xFF991B1B)),
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),

                  // Column Headers
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
                    decoration: BoxDecoration(
                      color: Colors.grey.shade50,
                      border: Border(bottom: BorderSide(color: Colors.grey.shade200)),
                    ),
                    child: const Row(
                      children: [
                        // Calls
                        Expanded(child: Text('OI', textAlign: TextAlign.center, style: TextStyle(fontSize: 10.5, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        Expanded(child: Text('CHG %', textAlign: TextAlign.center, style: TextStyle(fontSize: 10.5, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        Expanded(child: Text('LTP (₹)', textAlign: TextAlign.center, style: TextStyle(fontSize: 10.5, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        // Strike
                        Expanded(child: Text('STRIKE PRICE', textAlign: TextAlign.center, style: TextStyle(fontSize: 11, fontWeight: FontWeight.w800, color: Color(0xFF1F2937)))),
                        // Puts
                        Expanded(child: Text('LTP (₹)', textAlign: TextAlign.center, style: TextStyle(fontSize: 10.5, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        Expanded(child: Text('CHG %', textAlign: TextAlign.center, style: TextStyle(fontSize: 10.5, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        Expanded(child: Text('OI', textAlign: TextAlign.center, style: TextStyle(fontSize: 10.5, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                      ],
                    ),
                  ),

                  // Strike Rows
                  ListView.separated(
                    shrinkWrap: true,
                    physics: const NeverScrollableScrollPhysics(),
                    itemCount: _strikes.length,
                    separatorBuilder: (_, __) => Divider(height: 1, color: Colors.grey.shade100),
                    itemBuilder: (ctx, idx) {
                      final strike = _strikes[idx];
                      final isAtm = (strike == 24200.0);

                      return Container(
                        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
                        color: isAtm ? const Color(0xFFFEF3C7).withOpacity(0.5) : Colors.transparent,
                        child: Row(
                          children: [
                            // Call side (Awaiting feed)
                            const Expanded(child: Text('—', textAlign: TextAlign.center, style: TextStyle(fontSize: 12, color: Colors.grey))),
                            const Expanded(child: Text('—', textAlign: TextAlign.center, style: TextStyle(fontSize: 12, color: Colors.grey))),
                            const Expanded(child: Text('—', textAlign: TextAlign.center, style: TextStyle(fontSize: 12, color: Colors.grey))),

                            // Strike Column
                            Expanded(
                              child: Container(
                                padding: const EdgeInsets.symmetric(vertical: 4),
                                decoration: BoxDecoration(
                                  color: isAtm ? const Color(0xFFF59E0B) : Colors.grey.shade100,
                                  borderRadius: BorderRadius.circular(6),
                                ),
                                child: Text(
                                  '${strike.toInt()}${isAtm ? ' (ATM)' : ''}',
                                  textAlign: TextAlign.center,
                                  style: TextStyle(
                                    fontSize: 12,
                                    fontWeight: FontWeight.w700,
                                    color: isAtm ? Colors.white : const Color(0xFF111827),
                                  ),
                                ),
                              ),
                            ),

                            // Put side (Awaiting feed)
                            const Expanded(child: Text('—', textAlign: TextAlign.center, style: TextStyle(fontSize: 12, color: Colors.grey))),
                            const Expanded(child: Text('—', textAlign: TextAlign.center, style: TextStyle(fontSize: 12, color: Colors.grey))),
                            const Expanded(child: Text('—', textAlign: TextAlign.center, style: TextStyle(fontSize: 12, color: Colors.grey))),
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
}
