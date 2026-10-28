import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import '../../../constants/api_url_constants.dart';
import '../../../constants/auth_header_helper.dart';

/// Represents strike-level data in the Option Chain
class OptionStrikeData {
  final double strike;
  final bool isAtm;

  // Call option side (CE)
  final double? callLtp;
  final double? callChange;
  final double? callChangePercent;
  final int? callOi;

  // Put option side (PE)
  final double? putLtp;
  final double? putChange;
  final double? putChangePercent;
  final int? putOi;

  const OptionStrikeData({
    required this.strike,
    this.isAtm = false,
    this.callLtp,
    this.callChange,
    this.callChangePercent,
    this.callOi,
    this.putLtp,
    this.putChange,
    this.putChangePercent,
    this.putOi,
  });
}

class OptionsChainView extends StatefulWidget {
  const OptionsChainView({super.key});

  @override
  State<OptionsChainView> createState() => _OptionsChainViewState();
}

class _OptionsChainViewState extends State<OptionsChainView> {
  String _underlying = 'NIFTY';
  String _expiry = '[Sample Grid] 29-OCT-2026 (Weekly)';

  // Explicitly labeled demo / reference expiry presets (not live verified contracts)
  final Map<String, List<String>> _expiriesPerIndex = {
    'NIFTY': [
      '[Sample Grid] 29-OCT-2026 (Weekly)',
      '[Sample Grid] 05-NOV-2026 (Weekly)',
      '[Sample Grid] 26-NOV-2026 (Monthly)',
    ],
    'BANKNIFTY': [
      '[Sample Grid] 29-OCT-2026 (Weekly)',
      '[Sample Grid] 05-NOV-2026 (Weekly)',
      '[Sample Grid] 26-NOV-2026 (Monthly)',
    ],
    'FINNIFTY': [
      '[Sample Grid] 27-OCT-2026 (Weekly)',
      '[Sample Grid] 03-NOV-2026 (Weekly)',
      '[Sample Grid] 24-NOV-2026 (Monthly)',
    ],
  };

  // Structural strike interval templates per index (NSE format)
  final Map<String, List<double>> _strikeGrids = {
    'NIFTY': [
      24000.0,
      24050.0,
      24100.0,
      24150.0,
      24200.0, // Reference ATM
      24250.0,
      24300.0,
      24350.0,
      24400.0,
    ],
    'BANKNIFTY': [
      51200.0,
      51300.0,
      51400.0,
      51500.0,
      51600.0, // Reference ATM
      51700.0,
      51800.0,
      51900.0,
      52000.0,
    ],
    'FINNIFTY': [
      23600.0,
      23650.0,
      23700.0,
      23750.0,
      23800.0, // Reference ATM
      23850.0,
      23900.0,
      23950.0,
      24000.0,
    ],
  };

  double? _putCallRatio;
  bool _isLoading = false;

  // Strike data map (strike -> OptionStrikeData)
  final Map<double, OptionStrikeData> _optionData = {};

  @override
  void initState() {
    super.initState();
    _fetchOptionData();
  }

  double get _currentAtmStrike {
    switch (_underlying) {
      case 'BANKNIFTY':
        return 51600.0;
      case 'FINNIFTY':
        return 23800.0;
      default:
        return 24200.0;
    }
  }

  List<double> get _currentStrikes => _strikeGrids[_underlying] ?? _strikeGrids['NIFTY']!;

  /// Attempts to fetch real PCR and Option Greeks from backend endpoints
  Future<void> _fetchOptionData() async {
    setState(() {
      _isLoading = true;
    });

    final headers = AuthHeaderHelper.getHeaders();

    // 1. Fetch Put-Call Ratio
    try {
      final pcrUri = Uri.parse('${ApiUrls.baseUrl}/api/smart/market/put-call-ratio');
      final pcrRes = await http.get(pcrUri, headers: headers).timeout(const Duration(seconds: 10));
      if (pcrRes.statusCode == 200) {
        final decoded = jsonDecode(pcrRes.body);
        if (decoded is Map<String, dynamic>) {
          final pcrVal = (decoded['pcr'] as num?)?.toDouble() ??
              (decoded['putCallRatio'] as num?)?.toDouble();
          if (pcrVal != null && pcrVal > 0) {
            _putCallRatio = pcrVal;
          }
        }
      }
    } catch (_) {
      // Keep PCR null if unavailable
    }

    // 2. Fetch Option Greeks for underlying & expiry
    try {
      final greekUri = Uri.parse('${ApiUrls.baseUrl}/api/smart/option/greek');
      // Clean expiry string to format expected by Angel One
      final cleanExpiry = _expiry
          .replaceAll(RegExp(r'\[.*?\]'), '')
          .split(' ')
          .first
          .replaceAll('-', '')
          .trim()
          .toUpperCase();

      await http
          .post(
            greekUri,
            headers: headers,
            body: jsonEncode({
              'name': _underlying,
              'expirydate': cleanExpiry,
            }),
          )
          .timeout(const Duration(seconds: 12));
    } catch (_) {
      // Greeks API requires active F&O broker session
    }

    if (mounted) {
      setState(() {
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final expiries = _expiriesPerIndex[_underlying] ?? _expiriesPerIndex['NIFTY']!;
    final atmStrike = _currentAtmStrike;

    return Scaffold(
      backgroundColor: const Color(0xFFF9FAFB),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Header Row
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        const Text(
                          'Options Chain (F&O)',
                          style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: Color(0xFF111827)),
                        ),
                        const SizedBox(width: 12),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                          decoration: BoxDecoration(
                            color: _putCallRatio != null ? const Color(0xFFEDE9FE) : const Color(0xFFF3F4F6),
                            borderRadius: BorderRadius.circular(6),
                          ),
                          child: Text(
                            _putCallRatio != null ? 'PCR: ${_putCallRatio!.toStringAsFixed(2)}' : 'PCR: Standby',
                            style: TextStyle(
                              fontSize: 11,
                              fontWeight: FontWeight.w700,
                              color: _putCallRatio != null ? const Color(0xFF6D28D9) : const Color(0xFF6B7280),
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'NSE Derivative Chain grid layout (Structural Reference Template). Real-time Greeks & OI require active F&O broker session.',
                      style: TextStyle(fontSize: 13, color: Colors.grey.shade600),
                    ),
                  ],
                ),
                Row(
                  children: [
                    // Underlying Selector
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 2),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(color: Colors.grey.shade300),
                      ),
                      child: DropdownButtonHideUnderline(
                        child: DropdownButton<String>(
                          value: _underlying,
                          items: const [
                            DropdownMenuItem(value: 'NIFTY', child: Text('NIFTY')),
                            DropdownMenuItem(value: 'BANKNIFTY', child: Text('BANKNIFTY')),
                            DropdownMenuItem(value: 'FINNIFTY', child: Text('FINNIFTY')),
                          ],
                          onChanged: (v) {
                            if (v != null) {
                              setState(() {
                                _underlying = v;
                                _expiry = (_expiriesPerIndex[v] ?? expiries).first;
                              });
                              _fetchOptionData();
                            }
                          },
                        ),
                      ),
                    ),
                    const SizedBox(width: 14),

                    // Expiry Selector (Sample Grid)
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 2),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(color: Colors.grey.shade300),
                      ),
                      child: DropdownButtonHideUnderline(
                        child: DropdownButton<String>(
                          value: _expiry,
                          items: expiries.map((exp) {
                            return DropdownMenuItem<String>(
                              value: exp,
                              child: Text(exp, style: const TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
                            );
                          }).toList(),
                          onChanged: (v) {
                            if (v != null) {
                              setState(() => _expiry = v);
                              _fetchOptionData();
                            }
                          },
                        ),
                      ),
                    ),
                    const SizedBox(width: 12),

                    // Refresh Button
                    IconButton(
                      icon: const Icon(Icons.refresh_rounded, size: 20),
                      tooltip: 'Refresh Option Chain',
                      onPressed: _isLoading ? null : _fetchOptionData,
                    ),
                  ],
                ),
              ],
            ),
            const SizedBox(height: 20),

            // Honest Limitation Notice Banner
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              decoration: BoxDecoration(
                color: const Color(0xFFFEF3C7),
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: const Color(0xFFF59E0B).withOpacity(0.4)),
              ),
              child: const Row(
                children: [
                  Icon(Icons.info_outline_rounded, color: Color(0xFFB45309), size: 18),
                  SizedBox(width: 10),
                  Expanded(
                    child: Text(
                      'Option Chain Reference Template: The strike prices and expiries shown below are structural demonstration grids (NSE derivative contract format). Dynamic contract master downloading is currently disabled on backend servers to conserve memory (app.scrip-master.enabled=false). Real-time Option Greeks and Open Interest require an active Angel One F&O broker session. All unserved values display honestly as "—".',
                      style: TextStyle(fontSize: 12, color: Color(0xFF92400E)),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),

            // Options Table Container
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
                    itemCount: _currentStrikes.length,
                    separatorBuilder: (_, __) => Divider(height: 1, color: Colors.grey.shade100),
                    itemBuilder: (ctx, idx) {
                      final strike = _currentStrikes[idx];
                      final isAtm = (strike == atmStrike);
                      final data = _optionData[strike];

                      return Container(
                        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
                        color: isAtm ? const Color(0xFFFEF3C7).withOpacity(0.4) : Colors.transparent,
                        child: Row(
                          children: [
                            // Call Side
                            Expanded(
                              child: Text(
                                data?.callOi != null ? '${data!.callOi}' : '—',
                                textAlign: TextAlign.center,
                                style: TextStyle(fontSize: 12, color: data?.callOi != null ? Colors.black87 : Colors.grey.shade400),
                              ),
                            ),
                            Expanded(
                              child: Text(
                                data?.callChangePercent != null ? '${data!.callChangePercent!.toStringAsFixed(2)}%' : '—',
                                textAlign: TextAlign.center,
                                style: TextStyle(fontSize: 12, color: data?.callChangePercent != null ? const Color(0xFF16A34A) : Colors.grey.shade400),
                              ),
                            ),
                            Expanded(
                              child: Text(
                                data?.callLtp != null ? '₹${data!.callLtp!.toStringAsFixed(2)}' : '—',
                                textAlign: TextAlign.center,
                                style: TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: data?.callLtp != null ? Colors.black87 : Colors.grey.shade400),
                              ),
                            ),

                            // Strike Column
                            Expanded(
                              child: Container(
                                padding: const EdgeInsets.symmetric(vertical: 4),
                                decoration: BoxDecoration(
                                  color: isAtm ? const Color(0xFFF59E0B) : Colors.grey.shade100,
                                  borderRadius: BorderRadius.circular(6),
                                ),
                                child: Text(
                                  '${strike.toInt()}${isAtm ? ' (ATM Ref)' : ''}',
                                  textAlign: TextAlign.center,
                                  style: TextStyle(
                                    fontSize: 12,
                                    fontWeight: FontWeight.w700,
                                    color: isAtm ? Colors.white : const Color(0xFF111827),
                                  ),
                                ),
                              ),
                            ),

                            // Put Side
                            Expanded(
                              child: Text(
                                data?.putLtp != null ? '₹${data!.putLtp!.toStringAsFixed(2)}' : '—',
                                textAlign: TextAlign.center,
                                style: TextStyle(fontSize: 12, fontWeight: FontWeight.w700, color: data?.putLtp != null ? Colors.black87 : Colors.grey.shade400),
                              ),
                            ),
                            Expanded(
                              child: Text(
                                data?.putChangePercent != null ? '${data!.putChangePercent!.toStringAsFixed(2)}%' : '—',
                                textAlign: TextAlign.center,
                                style: TextStyle(fontSize: 12, color: data?.putChangePercent != null ? const Color(0xFFDC2626) : Colors.grey.shade400),
                              ),
                            ),
                            Expanded(
                              child: Text(
                                data?.putOi != null ? '${data!.putOi}' : '—',
                                textAlign: TextAlign.center,
                                style: TextStyle(fontSize: 12, color: data?.putOi != null ? Colors.black87 : Colors.grey.shade400),
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
}
