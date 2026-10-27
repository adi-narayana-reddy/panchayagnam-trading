import 'package:flutter/material.dart';
import '../../../constants/colors.dart';
import '../../../models/ScripInfo.dart';
import '../../../reusable_widets/stock_search_widget.dart';

class MarketWatchView extends StatefulWidget {
  const MarketWatchView({super.key});

  @override
  State<MarketWatchView> createState() => _MarketWatchViewState();
}

class _MarketWatchViewState extends State<MarketWatchView> {
  int _activeWatchlistIndex = 0;
  final List<String> _watchlists = ['Nifty 50', 'Bank Nifty', 'Custom Watchlist'];

  // Default watchlist instruments
  final List<ScripInfo> _watchlistItems = [
    const ScripInfo(
      tradingSymbol: 'RELIANCE-EQ',
      exchange: 'NSE',
      symbolToken: '2885',
      instrumentType: 'EQUITY',
      lotSize: 1,
      name: 'RELIANCE',
      tickSize: 0.05,
    ),
    const ScripInfo(
      tradingSymbol: 'TCS-EQ',
      exchange: 'NSE',
      symbolToken: '11536',
      instrumentType: 'EQUITY',
      lotSize: 1,
      name: 'TCS',
      tickSize: 0.05,
    ),
    const ScripInfo(
      tradingSymbol: 'HDFCBANK-EQ',
      exchange: 'NSE',
      symbolToken: '1333',
      instrumentType: 'EQUITY',
      lotSize: 1,
      name: 'HDFCBANK',
      tickSize: 0.05,
    ),
    const ScripInfo(
      tradingSymbol: 'INFY-EQ',
      exchange: 'NSE',
      symbolToken: '1594',
      instrumentType: 'EQUITY',
      lotSize: 1,
      name: 'INFY',
      tickSize: 0.05,
    ),
    const ScripInfo(
      tradingSymbol: 'ICICIBANK-EQ',
      exchange: 'NSE',
      symbolToken: '4963',
      instrumentType: 'EQUITY',
      lotSize: 1,
      name: 'ICICIBANK',
      tickSize: 0.05,
    ),
  ];

  void _addScrip(ScripInfo scrip) {
    if (!_watchlistItems.any((item) => item.symbolToken == scrip.symbolToken)) {
      setState(() {
        _watchlistItems.add(scrip);
      });
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('${scrip.tradingSymbol} added to watchlist'),
          backgroundColor: const Color(0xFF10B981),
          behavior: SnackBarBehavior.floating,
          duration: const Duration(seconds: 2),
        ),
      );
    } else {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('${scrip.tradingSymbol} is already in watchlist'),
          behavior: SnackBarBehavior.floating,
          duration: const Duration(seconds: 2),
        ),
      );
    }
  }

  void _removeScrip(int index) {
    final removed = _watchlistItems[index];
    setState(() {
      _watchlistItems.removeAt(index);
    });
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text('${removed.tradingSymbol} removed'),
        behavior: SnackBarBehavior.floating,
        duration: const Duration(seconds: 2),
      ),
    );
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
                      'Market Watch',
                      style: TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.w700,
                        color: Color(0xFF111827),
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      'Search verified contract master instruments across NSE & BSE. Create custom watchlists.',
                      style: TextStyle(fontSize: 13, color: Colors.grey.shade600),
                    ),
                  ],
                ),
                // Watchlist Tabs
                Container(
                  padding: const EdgeInsets.all(4),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(10),
                    border: Border.all(color: Colors.grey.shade200),
                  ),
                  child: Row(
                    children: List.generate(_watchlists.length, (idx) {
                      final isSelected = _activeWatchlistIndex == idx;
                      return InkWell(
                        onTap: () => setState(() => _activeWatchlistIndex = idx),
                        borderRadius: BorderRadius.circular(8),
                        child: Container(
                          padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
                          decoration: BoxDecoration(
                            color: isSelected ? const Color(0xFF7C3AED) : Colors.transparent,
                            borderRadius: BorderRadius.circular(8),
                          ),
                          child: Text(
                            _watchlists[idx],
                            style: TextStyle(
                              fontSize: 12,
                              fontWeight: isSelected ? FontWeight.w700 : FontWeight.w500,
                              color: isSelected ? Colors.white : Colors.grey.shade700,
                            ),
                          ),
                        ),
                      );
                    }),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),

            // Market Data Status Notice
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              decoration: BoxDecoration(
                color: const Color(0xFFFEF3C7),
                borderRadius: BorderRadius.circular(10),
                border: Border.all(color: const Color(0xFFF59E0B).withOpacity(0.3)),
              ),
              child: const Row(
                children: [
                  Icon(Icons.info_outline_rounded, color: Color(0xFFB45309), size: 18),
                  SizedBox(width: 10),
                  Expanded(
                    child: Text(
                      'Market Watch Workspace: Scrip metadata and lot sizes are loaded from verified NSE/BSE contract master files. Real-time WebSocket streaming quotes (LTP/Change) are scheduled for Phase 3.',
                      style: TextStyle(fontSize: 12, color: Color(0xFF92400E)),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 20),

            // Search Bar
            SizedBox(
              width: double.infinity,
              child: StockSearchWidget(onScripSelected: _addScrip),
            ),
            const SizedBox(height: 20),

            // Watchlist Cards / Table
            Container(
              decoration: BoxDecoration(
                color: Colors.white,
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade200),
              ),
              child: Column(
                children: [
                  // Table Header
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                    decoration: BoxDecoration(
                      color: Colors.grey.shade50,
                      borderRadius: const BorderRadius.vertical(top: Radius.circular(12)),
                      border: Border(bottom: BorderSide(color: Colors.grey.shade200)),
                    ),
                    child: const Row(
                      children: [
                        Expanded(flex: 3, child: Text('INSTRUMENT', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        Expanded(flex: 2, child: Text('EXCHANGE', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        Expanded(flex: 2, child: Text('TOKEN / SCRIP', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        Expanded(flex: 2, child: Text('LOT SIZE', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                        Expanded(flex: 2, child: Text('ACTIONS', style: TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6B7280)))),
                      ],
                    ),
                  ),
                  if (_watchlistItems.isEmpty)
                    Padding(
                      padding: const EdgeInsets.symmetric(vertical: 40),
                      child: Center(
                        child: Column(
                          children: [
                            Icon(Icons.format_list_bulleted_rounded, size: 40, color: Colors.grey.shade400),
                            const SizedBox(height: 10),
                            Text('Watchlist is empty', style: TextStyle(fontSize: 14, fontWeight: FontWeight.w600, color: Colors.grey.shade700)),
                            const SizedBox(height: 4),
                            Text('Search above to add instruments to this watchlist', style: TextStyle(fontSize: 12, color: Colors.grey.shade500)),
                          ],
                        ),
                      ),
                    )
                  else
                    ListView.separated(
                      shrinkWrap: true,
                      physics: const NeverScrollableScrollPhysics(),
                      itemCount: _watchlistItems.length,
                      separatorBuilder: (_, __) => Divider(height: 1, color: Colors.grey.shade100),
                      itemBuilder: (ctx, i) {
                        final item = _watchlistItems[i];
                        return Padding(
                          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 14),
                          child: Row(
                            children: [
                              Expanded(
                                flex: 3,
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    Text(
                                      item.name.isNotEmpty ? item.name : item.tradingSymbol,
                                      style: const TextStyle(fontWeight: FontWeight.w700, fontSize: 14, color: Color(0xFF111827)),
                                    ),
                                    Text(item.tradingSymbol, style: TextStyle(fontSize: 11, color: Colors.grey.shade500)),
                                  ],
                                ),
                              ),
                              Expanded(
                                flex: 2,
                                child: Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 3),
                                  decoration: BoxDecoration(
                                    color: const Color(0xFFEDE9FE),
                                    borderRadius: BorderRadius.circular(6),
                                  ),
                                  child: Text(
                                    item.exchange,
                                    style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w700, color: Color(0xFF6D28D9)),
                                  ),
                                ),
                              ),
                              Expanded(
                                flex: 2,
                                child: Text(item.symbolToken, style: TextStyle(fontSize: 12, color: Colors.grey.shade700)),
                              ),
                              Expanded(
                                flex: 2,
                                child: Text('${item.lotSize}', style: TextStyle(fontSize: 12, color: Colors.grey.shade700)),
                              ),
                              Expanded(
                                flex: 2,
                                child: Row(
                                  children: [
                                    IconButton(
                                      icon: const Icon(Icons.delete_outline_rounded, size: 18, color: Colors.redAccent),
                                      onPressed: () => _removeScrip(i),
                                      tooltip: 'Remove from Watchlist',
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
}
