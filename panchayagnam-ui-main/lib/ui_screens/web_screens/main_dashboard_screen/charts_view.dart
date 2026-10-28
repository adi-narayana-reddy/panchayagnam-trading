import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:intl/intl.dart' show DateFormat;
import '../../../constants/api_url_constants.dart';
import '../../../constants/auth_header_helper.dart';
import '../../../models/ScripInfo.dart';

/// Real OHLC Candle model for chart rendering
class ChartCandle {
  final DateTime timestamp;
  final double open;
  final double high;
  final double low;
  final double close;
  final int volume;

  const ChartCandle({
    required this.timestamp,
    required this.open,
    required this.high,
    required this.low,
    required this.close,
    required this.volume,
  });

  bool get isBullish => close >= open;
}

class ChartsView extends StatefulWidget {
  const ChartsView({super.key});

  @override
  State<ChartsView> createState() => _ChartsViewState();
}

class _ChartsViewState extends State<ChartsView> {
  // Verified contract instruments available for technical charting
  final List<ScripInfo> _chartInstruments = const [
    ScripInfo(
      tradingSymbol: 'RELIANCE-EQ',
      exchange: 'NSE',
      symbolToken: '2885',
      instrumentType: 'EQUITY',
      lotSize: 1,
      name: 'RELIANCE',
      tickSize: 0.05,
    ),
    ScripInfo(
      tradingSymbol: 'TCS-EQ',
      exchange: 'NSE',
      symbolToken: '11536',
      instrumentType: 'EQUITY',
      lotSize: 1,
      name: 'TCS',
      tickSize: 0.05,
    ),
    ScripInfo(
      tradingSymbol: 'HDFCBANK-EQ',
      exchange: 'NSE',
      symbolToken: '1333',
      instrumentType: 'EQUITY',
      lotSize: 1,
      name: 'HDFCBANK',
      tickSize: 0.05,
    ),
    ScripInfo(
      tradingSymbol: 'INFY-EQ',
      exchange: 'NSE',
      symbolToken: '1594',
      instrumentType: 'EQUITY',
      lotSize: 1,
      name: 'INFY',
      tickSize: 0.05,
    ),
    ScripInfo(
      tradingSymbol: 'ICICIBANK-EQ',
      exchange: 'NSE',
      symbolToken: '4963',
      instrumentType: 'EQUITY',
      lotSize: 1,
      name: 'ICICIBANK',
      tickSize: 0.05,
    ),
    ScripInfo(
      tradingSymbol: 'SBIN-EQ',
      exchange: 'NSE',
      symbolToken: '3045',
      instrumentType: 'EQUITY',
      lotSize: 1,
      name: 'SBIN',
      tickSize: 0.05,
    ),
  ];

  late ScripInfo _selectedInstrument;
  String _timeframe = '15m'; // 5m, 15m, 1h, 1D

  List<ChartCandle> _candles = [];
  bool _isLoading = false;
  String? _statusMessage;
  DateTime? _lastFetchTime;

  @override
  void initState() {
    super.initState();
    _selectedInstrument = _chartInstruments.first;
    _fetchCandleData();
  }

  /// Maps UI timeframe to Angel One SmartAPI interval parameter
  String _mapTimeframeToInterval(String tf) {
    switch (tf) {
      case '5m':
        return 'FIVE_MINUTE';
      case '15m':
        return 'FIFTEEN_MINUTE';
      case '1h':
        return 'ONE_HOUR';
      case '1D':
        return 'ONE_DAY';
      default:
        return 'FIFTEEN_MINUTE';
    }
  }

  /// Fetches actual historical OHLC candle data from backend /api/smart/market/candle
  Future<void> _fetchCandleData() async {
    setState(() {
      _isLoading = true;
      _statusMessage = null;
    });

    final now = DateTime.now();
    DateTime fromDate;

    if (_timeframe == '1D') {
      // Past 60 days for daily view
      fromDate = now.subtract(const Duration(days: 60));
    } else if (_timeframe == '1h') {
      // Past 14 days for hourly view
      fromDate = now.subtract(const Duration(days: 14));
    } else {
      // Past 5 calendar days for intraday 5m/15m
      fromDate = now.subtract(const Duration(days: 5));
    }

    final dateFmt = DateFormat('yyyy-MM-dd HH:mm');
    final fromDateStr = dateFmt.format(fromDate);
    final toDateStr = dateFmt.format(now);
    final interval = _mapTimeframeToInterval(_timeframe);

    try {
      final uri = Uri.parse('${ApiUrls.baseUrl}/api/smart/market/candle');
      final headers = AuthHeaderHelper.getHeaders();

      final response = await http
          .post(
            uri,
            headers: headers,
            body: jsonEncode({
              'exchange': _selectedInstrument.exchange,
              'symboltoken': _selectedInstrument.symbolToken,
              'interval': interval,
              'fromdate': fromDateStr,
              'todate': toDateStr,
            }),
          )
          .timeout(const Duration(seconds: 15));

      if (response.statusCode == 200) {
        final decoded = jsonDecode(response.body);
        if (decoded is List) {
          final List<ChartCandle> parsed = [];
          for (final item in decoded) {
            if (item is List && item.length >= 6) {
              DateTime ts;
              try {
                ts = DateTime.parse(item[0].toString());
              } catch (_) {
                ts = DateTime.now();
              }
              parsed.add(
                ChartCandle(
                  timestamp: ts,
                  open: (item[1] as num).toDouble(),
                  high: (item[2] as num).toDouble(),
                  low: (item[3] as num).toDouble(),
                  close: (item[4] as num).toDouble(),
                  volume: (item[5] as num).toInt(),
                ),
              );
            }
          }

          if (mounted) {
            setState(() {
              _candles = parsed;
              _isLoading = false;
              _lastFetchTime = DateTime.now();
              if (parsed.isEmpty) {
                _statusMessage =
                    'No candle data returned from exchange for ${_selectedInstrument.name} on $_timeframe interval. Market may be closed or instrument requires broker session.';
              }
            });
          }
          return;
        }
      }

      // If backend returns 400 or other non-200 code
      if (mounted) {
        setState(() {
          _candles = [];
          _isLoading = false;
          _statusMessage =
              'Angel One Candle API requires an authenticated broker session. Connect Angel One under Broker Accounts to stream live candlestick charts.';
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _candles = [];
          _isLoading = false;
          _statusMessage =
              'Unable to reach market candle service (${e.toString()}). Connect your broker in Broker Accounts.';
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
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
                    const Text(
                      'Interactive Candlestick Charts',
                      style: TextStyle(fontSize: 22, fontWeight: FontWeight.w700, color: Color(0xFF111827)),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Verified OHLC technical analysis powered by Angel One SmartAPI candle feeds.',
                      style: TextStyle(fontSize: 13, color: Colors.grey.shade600),
                    ),
                  ],
                ),
                Row(
                  children: [
                    // Instrument Selector
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 2),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(8),
                        border: Border.all(color: Colors.grey.shade300),
                      ),
                      child: DropdownButtonHideUnderline(
                        child: DropdownButton<ScripInfo>(
                          value: _selectedInstrument,
                          items: _chartInstruments.map((item) {
                            return DropdownMenuItem<ScripInfo>(
                              value: item,
                              child: Text(
                                '${item.name} (${item.exchange})',
                                style: const TextStyle(fontSize: 13, fontWeight: FontWeight.w600),
                              ),
                            );
                          }).toList(),
                          onChanged: (val) {
                            if (val != null) {
                              setState(() => _selectedInstrument = val);
                              _fetchCandleData();
                            }
                          },
                        ),
                      ),
                    ),
                    const SizedBox(width: 14),

                    // Timeframe Selector
                    SegmentedButton<String>(
                      segments: const [
                        ButtonSegment(value: '5m', label: Text('5m')),
                        ButtonSegment(value: '15m', label: Text('15m')),
                        ButtonSegment(value: '1h', label: Text('1h')),
                        ButtonSegment(value: '1D', label: Text('1D')),
                      ],
                      selected: {_timeframe},
                      onSelectionChanged: (set) {
                        setState(() => _timeframe = set.first);
                        _fetchCandleData();
                      },
                    ),
                    const SizedBox(width: 12),

                    // Refresh Button
                    IconButton(
                      icon: const Icon(Icons.refresh_rounded, size: 20),
                      tooltip: 'Refresh Candle Data',
                      onPressed: _isLoading ? null : _fetchCandleData,
                    ),
                  ],
                ),
              ],
            ),
            const SizedBox(height: 20),

            // Honest Feed Indicator Notice
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              decoration: BoxDecoration(
                color: _candles.isNotEmpty ? const Color(0xFFF0FDF4) : const Color(0xFFFEF3C7),
                borderRadius: BorderRadius.circular(10),
                border: Border.all(
                  color: _candles.isNotEmpty ? const Color(0xFF86EFAC) : const Color(0xFFFDE68A),
                ),
              ),
              child: Row(
                children: [
                  Icon(
                    _candles.isNotEmpty ? Icons.insights_rounded : Icons.info_outline_rounded,
                    color: _candles.isNotEmpty ? const Color(0xFF16A34A) : const Color(0xFFD97706),
                    size: 18,
                  ),
                  const SizedBox(width: 10),
                  Expanded(
                    child: Text(
                      _candles.isNotEmpty
                          ? 'Showing ${_candles.length} verified OHLC candlesticks for ${_selectedInstrument.name} (${_selectedInstrument.tradingSymbol}) on interval $_timeframe. Updated: ${_lastFetchTime != null ? DateFormat('HH:mm:ss').format(_lastFetchTime!) : 'Just now'}.'
                          : 'Candle Feed Notice: Real-time candlestick generation consumes Angel One SmartConnect historical candle data. If candles are not yet loaded, connect an active broker in Broker Accounts.',
                      style: TextStyle(
                        fontSize: 12,
                        color: _candles.isNotEmpty ? const Color(0xFF166534) : const Color(0xFF92400E),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),

            // Chart Canvas Container
            Container(
              height: 500,
              width: double.infinity,
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade200),
              ),
              padding: const EdgeInsets.all(20),
              child: Column(
                children: [
                  // Chart Top Metrics Bar
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Row(
                        children: [
                          Text(_selectedInstrument.name, style: const TextStyle(fontWeight: FontWeight.w800, fontSize: 16)),
                          const SizedBox(width: 8),
                          Container(
                            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                            decoration: BoxDecoration(color: const Color(0xFFEDE9FE), borderRadius: BorderRadius.circular(4)),
                            child: Text(
                              _selectedInstrument.exchange,
                              style: const TextStyle(fontSize: 10.5, fontWeight: FontWeight.w700, color: Color(0xFF6D28D9)),
                            ),
                          ),
                          const SizedBox(width: 12),
                          Text(
                            'Interval: $_timeframe | Token: ${_selectedInstrument.symbolToken}',
                            style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
                          ),
                        ],
                      ),
                      if (_candles.isNotEmpty)
                        Row(
                          children: [
                            _miniOhlcStat('O', _candles.last.open),
                            const SizedBox(width: 8),
                            _miniOhlcStat('H', _candles.last.high),
                            const SizedBox(width: 8),
                            _miniOhlcStat('L', _candles.last.low),
                            const SizedBox(width: 8),
                            _miniOhlcStat('C', _candles.last.close, isClose: true, isBullish: _candles.last.isBullish),
                          ],
                        )
                      else
                        Row(
                          children: [
                            Icon(Icons.candlestick_chart, color: Colors.purple.shade700, size: 20),
                            const SizedBox(width: 6),
                            const Text('Candlesticks', style: TextStyle(fontSize: 12, fontWeight: FontWeight.w600)),
                          ],
                        ),
                    ],
                  ),
                  const Divider(height: 24),

                  // Chart Area
                  Expanded(
                    child: _isLoading
                        ? Center(
                            child: Column(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                const CircularProgressIndicator(color: Color(0xFF7C3AED)),
                                const SizedBox(height: 12),
                                Text(
                                  'Fetching OHLC candles for ${_selectedInstrument.name} ($_timeframe)...',
                                  style: TextStyle(fontSize: 13, color: Colors.grey.shade600),
                                ),
                              ],
                            ),
                          )
                        : _candles.isNotEmpty
                            ? CustomPaint(
                                painter: _RealCandlestickPainter(candles: _candles),
                                size: Size.infinite,
                              )
                            : Center(
                                child: Padding(
                                  padding: const EdgeInsets.symmetric(horizontal: 40),
                                  child: Column(
                                    mainAxisSize: MainAxisSize.min,
                                    children: [
                                      Icon(Icons.show_chart_rounded, size: 54, color: Colors.grey.shade300),
                                      const SizedBox(height: 14),
                                      Text(
                                        'Candle Feed Standby',
                                        style: TextStyle(fontWeight: FontWeight.w700, fontSize: 15, color: Colors.grey.shade700),
                                      ),
                                      const SizedBox(height: 8),
                                      Text(
                                        _statusMessage ??
                                            'Angel One historical candlestick data requires an active broker account. Connect Angel One under Broker Accounts to enable live chart streams.',
                                        textAlign: TextAlign.center,
                                        style: TextStyle(fontSize: 12.5, color: Colors.grey.shade500),
                                      ),
                                      const SizedBox(height: 16),
                                      ElevatedButton.icon(
                                        onPressed: _fetchCandleData,
                                        icon: const Icon(Icons.refresh_rounded, size: 16),
                                        label: const Text('Retry Fetch'),
                                        style: ElevatedButton.styleFrom(
                                          backgroundColor: const Color(0xFF7C3AED),
                                          foregroundColor: Colors.white,
                                          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                                        ),
                                      ),
                                    ],
                                  ),
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

  Widget _miniOhlcStat(String label, double val, {bool isClose = false, bool isBullish = true}) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
      decoration: BoxDecoration(
        color: isClose ? (isBullish ? const Color(0xFFDCFCE7) : const Color(0xFFFEE2E2)) : Colors.grey.shade100,
        borderRadius: BorderRadius.circular(4),
      ),
      child: Text(
        '$label: ₹${val.toStringAsFixed(1)}',
        style: TextStyle(
          fontSize: 11,
          fontWeight: isClose ? FontWeight.w800 : FontWeight.w600,
          color: isClose ? (isBullish ? const Color(0xFF166534) : const Color(0xFF991B1B)) : const Color(0xFF374151),
        ),
      ),
    );
  }
}

/// Custom Canvas Painter that renders verified OHLC Candlesticks
class _RealCandlestickPainter extends CustomPainter {
  final List<ChartCandle> candles;

  _RealCandlestickPainter({required this.candles});

  @override
  void paint(Canvas canvas, Size size) {
    if (candles.isEmpty) return;

    // Calculate actual min & max prices from candles
    double minVal = candles.first.low;
    double maxVal = candles.first.high;

    for (final c in candles) {
      if (c.low < minVal) minVal = c.low;
      if (c.high > maxVal) maxVal = c.high;
    }

    // Add 5% padding to top and bottom of range for aesthetics
    final padding = (maxVal - minVal) * 0.05;
    minVal -= padding;
    maxVal += padding;
    final range = (maxVal - minVal) > 0 ? (maxVal - minVal) : 1.0;

    double toY(double price) {
      final normalized = (price - minVal) / range;
      return size.height - (normalized * size.height);
    }

    // Draw Price Grid Lines
    final gridPaint = Paint()
      ..color = Colors.grey.shade200
      ..strokeWidth = 1.0;

    final textStyle = TextStyle(color: Colors.grey.shade500, fontSize: 10);

    for (int step = 0; step <= 4; step++) {
      final price = minVal + (range * (step / 4.0));
      final y = toY(price);
      canvas.drawLine(Offset(0, y), Offset(size.width - 60, y), gridPaint);

      // Price label on right axis
      final textSpan = TextSpan(text: '₹${price.toStringAsFixed(1)}', style: textStyle);
      final textPainter = TextPainter(text: textSpan, textDirection: TextDirection.ltr)..layout();
      textPainter.paint(canvas, Offset(size.width - 55, y - 6));
    }

    // Candlesticks layout
    final n = candles.length;
    final availableWidth = size.width - 70;
    final spacing = availableWidth / (n + 0.5);
    final cw = (spacing * 0.5).clamp(2.0, 16.0);

    for (int i = 0; i < n; i++) {
      final c = candles[i];
      final x = spacing * (i + 0.5);

      final col = c.isBullish ? const Color(0xFF16A34A) : const Color(0xFFDC2626);

      final bodyTop = toY(c.isBullish ? c.close : c.open);
      final bodyBottom = toY(c.isBullish ? c.open : c.close);
      final highY = toY(c.high);
      final lowY = toY(c.low);

      // Draw wick
      canvas.drawLine(
        Offset(x, highY),
        Offset(x, lowY),
        Paint()
          ..color = col
          ..strokeWidth = 1.2,
      );

      // Draw candle body
      final rectHeight = (bodyBottom - bodyTop).abs();
      canvas.drawRRect(
        RRect.fromRectAndRadius(
          Rect.fromLTWH(x - cw / 2, bodyTop, cw, rectHeight < 1.0 ? 1.0 : rectHeight),
          const Radius.circular(1.5),
        ),
        Paint()..color = col,
      );
    }
  }

  @override
  bool shouldRepaint(_RealCandlestickPainter oldDelegate) =>
      oldDelegate.candles != candles;
}
