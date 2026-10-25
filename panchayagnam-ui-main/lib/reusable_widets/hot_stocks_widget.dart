import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../bloc/hot_stock_bloc/hot_stock_block.dart';
import '../bloc/hot_stock_bloc/hot_stock_event.dart';
import '../bloc/hot_stock_bloc/hot_stock_state.dart';
import '../models/hot_stock_model.dart';

class HotStocksWidgetIndividualScreen extends StatelessWidget {
  const HotStocksWidgetIndividualScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<HotStockBloc, HotStockState>(
      builder: (context, state) {
        return _HotStocksRow(state: state);
      },
    );
  }
}

class _HotStocksRow extends StatelessWidget {
  final HotStockState state;
  const _HotStocksRow({required this.state});

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 85,
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border(
          bottom: BorderSide(color: const Color(0xFFEDE9FE), width: 1.5),
        ),
        boxShadow: [
          BoxShadow(
            color: const Color(0xFF7C3AED).withOpacity(0.06),
            blurRadius: 12,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          // ── Header label
          _HeaderLabel(),

          // ── Divider
          Container(
            width: 1,
            height: 60,
            color: const Color(0xFFEDE9FE),
            margin: const EdgeInsets.symmetric(horizontal: 12),
          ),

          // ── Category dropdown
          _CategoryDropdown(
            selected: state.selectedCategory,
            onSelect: (cat) =>
                context.read<HotStockBloc>().add(ChangeCategoryEvent(cat)),
          ),

          // ── Divider
          Container(
            width: 1,
            height: 60,
            color: const Color(0xFFEDE9FE),
            margin: const EdgeInsets.symmetric(horizontal: 12),
          ),

          // ── Scrollable stock cards
          Expanded(
            child: state.isLoadingStocks
                ? _LoadingSkeletonRow()
                : state.loadError != null
                ? _ErrorStrip(
                    message: state.loadError!,
                    onRetry: () => context.read<HotStockBloc>().add(
                      ChangeDateEvent(state.selectedDate),
                    ),
                  )
                : state.stocks.where((s) => s.isActive).isEmpty
                ? _EmptyStrip(category: state.selectedCategory)
                : _StockCardRow(
                    stocks: state.stocks.where((s) => s.isActive).toList(),
                  ),
          ),
        ],
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Header Label
// ─────────────────────────────────────────────────────────────────────────────

class _HeaderLabel extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.only(left: 16),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisSize: MainAxisSize.min,
            children: const [
              Icon(
                Icons.local_fire_department_rounded,
                color: Color(0xFFEF4444),
                size: 18,
              ),
              SizedBox(width: 5),
              Text(
                'Model',
                style: TextStyle(
                  fontSize: 13,
                  fontWeight: FontWeight.w800,
                  color: Color(0xFF1F2937),
                  letterSpacing: 0.2,
                ),
              ),
            ],
          ),
          const SizedBox(height: 2),
          const Text(
            'Portfolio',
            style: TextStyle(
              fontSize: 10,
              fontWeight: FontWeight.w500,
              color: Color(0xFF9CA3AF),
              letterSpacing: 0.3,
            ),
          ),
        ],
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Category Dropdown (replaces chip row)
// ─────────────────────────────────────────────────────────────────────────────

class _CategoryDropdown extends StatelessWidget {
  final HotStockCategory selected;
  final void Function(HotStockCategory) onSelect;

  const _CategoryDropdown({required this.selected, required this.onSelect});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      width: 130,
      child: _StyledDropdown(selected: selected, onSelect: onSelect),
    );
  }
}

class _StyledDropdown extends StatefulWidget {
  final HotStockCategory selected;
  final void Function(HotStockCategory) onSelect;

  const _StyledDropdown({required this.selected, required this.onSelect});

  @override
  State<_StyledDropdown> createState() => _StyledDropdownState();
}

class _StyledDropdownState extends State<_StyledDropdown> {
  bool _isOpen = false;
  final LayerLink _layerLink = LayerLink();
  OverlayEntry? _overlay;

  void _openDropdown() {
    if (_isOpen) {
      _closeDropdown();
      return;
    }
    _overlay = _buildOverlay();
    Overlay.of(context).insert(_overlay!);
    setState(() => _isOpen = true);
  }

  void _closeDropdown() {
    _overlay?.remove();
    _overlay = null;
    if (mounted) setState(() => _isOpen = false);
  }

  OverlayEntry _buildOverlay() {
    return OverlayEntry(
      builder: (_) => GestureDetector(
        behavior: HitTestBehavior.translucent,
        onTap: _closeDropdown,
        child: Stack(
          children: [
            CompositedTransformFollower(
              link: _layerLink,
              showWhenUnlinked: false,
              offset: const Offset(0, 44),
              child: Material(
                color: Colors.transparent,
                child: _DropdownMenu(
                  selected: widget.selected,
                  onSelect: (cat) {
                    _closeDropdown();
                    widget.onSelect(cat);
                  },
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _overlay?.remove();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return CompositedTransformTarget(
      link: _layerLink,
      child: InkWell(
        onTap: _openDropdown,
        child: Container(
          height: 36,
          padding: const EdgeInsets.symmetric(horizontal: 10),
          decoration: BoxDecoration(
            color: _isOpen ? const Color(0xFFEDE9FE) : const Color(0xFFF5F3FF),
            borderRadius: BorderRadius.circular(8),
            border: Border.all(
              color: _isOpen
                  ? const Color(0xFF7C3AED)
                  : const Color(0xFFDDD6FE),
              width: _isOpen ? 1.5 : 1,
            ),
          ),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                widget.selected.label,
                style: const TextStyle(
                  fontSize: 11,
                  fontWeight: FontWeight.w700,
                  color: Color(0xFF7C3AED),
                ),
              ),
              AnimatedRotation(
                turns: _isOpen ? 0.5 : 0,
                duration: const Duration(milliseconds: 200),
                child: const Icon(
                  Icons.keyboard_arrow_down_rounded,
                  size: 16,
                  color: Color(0xFF7C3AED),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _DropdownMenu extends StatelessWidget {
  final HotStockCategory selected;
  final void Function(HotStockCategory) onSelect;

  const _DropdownMenu({required this.selected, required this.onSelect});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 130,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: const Color(0xFFEDE9FE), width: 1.5),
        boxShadow: [
          BoxShadow(
            color: const Color(0xFF7C3AED).withOpacity(0.12),
            blurRadius: 16,
            offset: const Offset(0, 6),
          ),
        ],
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: HotStockCategory.values.map((cat) {
          final isSelected = cat == selected;
          return InkWell(
            onTap: () => onSelect(cat),
            borderRadius: BorderRadius.circular(8),
            child: Container(
              width: double.infinity,
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
              decoration: BoxDecoration(
                color: isSelected
                    ? const Color(0xFFF5F3FF)
                    : Colors.transparent,
                borderRadius: BorderRadius.circular(8),
              ),
              child: Row(
                children: [
                  if (isSelected)
                    const Padding(
                      padding: EdgeInsets.only(right: 6),
                      child: Icon(
                        Icons.check_rounded,
                        size: 12,
                        color: Color(0xFF7C3AED),
                      ),
                    ),
                  Text(
                    cat.label,
                    style: TextStyle(
                      fontSize: 11,
                      fontWeight: isSelected
                          ? FontWeight.w700
                          : FontWeight.w500,
                      color: isSelected
                          ? const Color(0xFF7C3AED)
                          : const Color(0xFF6B7280),
                    ),
                  ),
                ],
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Stock Cards — horizontal scrollable row
// ─────────────────────────────────────────────────────────────────────────────

class _StockCardRow extends StatelessWidget {
  final List<HotStockModel> stocks;
  const _StockCardRow({required this.stocks});

  @override
  Widget build(BuildContext context) {
    return ListView.separated(
      scrollDirection: Axis.horizontal,
      padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 4),
      itemCount: stocks.length,
      separatorBuilder: (_, __) => const SizedBox(width: 8),
      itemBuilder: (_, i) => _StockCard(stock: stocks[i]),
    );
  }
}

class _StockCard extends StatelessWidget {
  final HotStockModel stock;
  const _StockCard({required this.stock});

  @override
  Widget build(BuildContext context) {
    final isUp = stock.direction == HotStockDirection.UP;
    final changeColor = isUp
        ? const Color(0xFF10B981)
        : const Color(0xFFEF4444);

    return Container(
      width: 140,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: const Color(0xFFEDE9FE), width: 1.2),
        boxShadow: [
          BoxShadow(
            color: const Color(0xFF7C3AED).withOpacity(0.05),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          // Symbol + signal badge
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Flexible(
                child: Text(
                  stock.symbol,
                  style: const TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.w800,
                    color: Color(0xFF1F2937),
                    letterSpacing: 0.2,
                  ),
                  overflow: TextOverflow.ellipsis,
                ),
              ),
              const SizedBox(width: 4),
              _SignalBadge(signal: stock.signal),
            ],
          ),

          // Price + change
          Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Text(
                '₹${stock.price.toStringAsFixed(0)}',
                style: const TextStyle(
                  fontSize: 13,
                  fontWeight: FontWeight.w700,
                  color: Color(0xFF1F2937),
                ),
              ),
              const SizedBox(width: 4),
              Icon(
                isUp
                    ? Icons.arrow_drop_up_rounded
                    : Icons.arrow_drop_down_rounded,
                size: 16,
                color: changeColor,
              ),
              Text(
                stock.changePercent != null
                    ? '${stock.changePercent!.toStringAsFixed(1)}%'
                    : '-',
                style: TextStyle(
                  fontSize: 10,
                  fontWeight: FontWeight.w600,
                  color: changeColor,
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Signal Badge
// ─────────────────────────────────────────────────────────────────────────────

class _SignalBadge extends StatelessWidget {
  final HotStockSignal signal;
  const _SignalBadge({required this.signal});

  Color get _color {
    switch (signal) {
      case HotStockSignal.BUY:
        return const Color(0xFF10B981);
      case HotStockSignal.STRONG_BUY:
        return const Color(0xFF7C3AED);
      case HotStockSignal.HOLD:
        return const Color(0xFFF59E0B);
      case HotStockSignal.SELL:
        return const Color(0xFFEF4444);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 5, vertical: 2),
      decoration: BoxDecoration(
        color: _color.withOpacity(0.12),
        borderRadius: BorderRadius.circular(4),
      ),
      child: Text(
        signal.label,
        style: TextStyle(
          fontSize: 8,
          fontWeight: FontWeight.w800,
          color: _color,
          letterSpacing: 0.3,
        ),
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Loading Skeleton
// ─────────────────────────────────────────────────────────────────────────────

class _LoadingSkeletonRow extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ListView.separated(
      scrollDirection: Axis.horizontal,
      padding: const EdgeInsets.symmetric(vertical: 14, horizontal: 4),
      itemCount: 5,
      separatorBuilder: (_, __) => const SizedBox(width: 8),
      itemBuilder: (_, __) => Container(
        width: 140,
        decoration: BoxDecoration(
          color: const Color(0xFFF5F3FF),
          borderRadius: BorderRadius.circular(10),
          border: Border.all(color: const Color(0xFFEDE9FE)),
        ),
        padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                _Shimmer(width: 60, height: 10, radius: 4),
                _Shimmer(width: 30, height: 14, radius: 4),
              ],
            ),
            _Shimmer(width: 80, height: 12, radius: 4),
          ],
        ),
      ),
    );
  }
}

class _ErrorStrip extends StatelessWidget {
  final String message;
  final VoidCallback onRetry;
  const _ErrorStrip({required this.message, required this.onRetry});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Icon(
            Icons.error_outline_rounded,
            color: Color(0xFFEF4444),
            size: 14,
          ),
          const SizedBox(width: 6),
          Flexible(
            child: Text(
              message,
              style: const TextStyle(fontSize: 11, color: Color(0xFFEF4444)),
              overflow: TextOverflow.ellipsis,
            ),
          ),
          const SizedBox(width: 10),
          InkWell(
            onTap: onRetry,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
              decoration: BoxDecoration(
                color: const Color(0xFFEF4444).withOpacity(0.10),
                borderRadius: BorderRadius.circular(6),
              ),
              child: const Text(
                'Retry',
                style: TextStyle(
                  fontSize: 10,
                  fontWeight: FontWeight.w700,
                  color: Color(0xFFEF4444),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Empty Strip
// ─────────────────────────────────────────────────────────────────────────────

class _EmptyStrip extends StatelessWidget {
  final HotStockCategory category;
  const _EmptyStrip({required this.category});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Icon(Icons.inbox_outlined, color: Color(0xFFD1D5DB), size: 18),
          const SizedBox(width: 8),
          Text(
            'No ${category.label} stocks today',
            style: const TextStyle(
              fontSize: 11,
              color: Color(0xFF9CA3AF),
              fontWeight: FontWeight.w500,
            ),
          ),
        ],
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shimmer
// ─────────────────────────────────────────────────────────────────────────────

class _Shimmer extends StatefulWidget {
  final double width;
  final double height;
  final double radius;
  const _Shimmer({
    required this.width,
    required this.height,
    required this.radius,
  });

  @override
  State<_Shimmer> createState() => _ShimmerState();
}

class _ShimmerState extends State<_Shimmer>
    with SingleTickerProviderStateMixin {
  late AnimationController _ctrl;
  late Animation<double> _anim;

  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1000),
    )..repeat(reverse: true);
    _anim = Tween<double>(begin: 0.4, end: 0.9).animate(_ctrl);
  }

  @override
  void dispose() {
    _ctrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _anim,
      builder: (_, __) => Opacity(
        opacity: _anim.value,
        child: Container(
          width: widget.width,
          height: widget.height,
          decoration: BoxDecoration(
            color: const Color(0xFFDDD6FE),
            borderRadius: BorderRadius.circular(widget.radius),
          ),
        ),
      ),
    );
  }
}
