import 'package:easy_trade_web/ui_screens/web_screens/web_ip_screen/save_ip_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../../bloc/ip_based_bloc/ip_bloc.dart';
import '../../../service/ip_service.dart';
import 'ip_health_screen.dart';
import 'ip_report_screen.dart';
import 'ip_theme_colors.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen>
    with SingleTickerProviderStateMixin {
  int _currentIndex = 0;
  late final TabController _tabController;

  final List<_NavItem> _navItems = const [
    _NavItem(
      label: 'Save IPs',
      icon: Icons.save_outlined,
      activeIcon: Icons.save_rounded,
    ),
    _NavItem(
      label: 'Health',
      icon: Icons.health_and_safety_outlined,
      activeIcon: Icons.health_and_safety_rounded,
    ),
    _NavItem(
      label: 'Report',
      icon: Icons.assessment_outlined,
      activeIcon: Icons.assessment_rounded,
    ),
  ];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
    _tabController.addListener(() {
      if (!_tabController.indexIsChanging) {
        setState(() => _currentIndex = _tabController.index);
      }
    });
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) => IpBloc(repository: IpRepository()),
      child: Scaffold(
        backgroundColor: AppTheme.background,
        appBar: PreferredSize(
          preferredSize: const Size.fromHeight(112),
          child: _AnimatedAppBar(
            currentIndex: _currentIndex,
            tabController: _tabController,
            navItems: _navItems,
          ),
        ),
        body: TabBarView(
          controller: _tabController,
          children: const [
            SaveIpScreen(),
            IpHealthScreen(),
            IpReportScreen(),
          ],
        ),
      ),
    );
  }
}

// ─── Animated App Bar ─────────────────────────────────────────────────────────

class _AnimatedAppBar extends StatefulWidget {
  final int currentIndex;
  final TabController tabController;
  final List<_NavItem> navItems;

  const _AnimatedAppBar({
    required this.currentIndex,
    required this.tabController,
    required this.navItems,
  });

  @override
  State<_AnimatedAppBar> createState() => _AnimatedAppBarState();
}

class _AnimatedAppBarState extends State<_AnimatedAppBar>
    with SingleTickerProviderStateMixin {
  late final AnimationController _controller;
  late final Animation<double> _fadeIn;
  late final Animation<Offset> _slideIn;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );
    _fadeIn = CurvedAnimation(parent: _controller, curve: Curves.easeOut);
    _slideIn = Tween<Offset>(
      begin: const Offset(0, -0.3),
      end: Offset.zero,
    ).animate(CurvedAnimation(parent: _controller, curve: Curves.easeOutCubic));
    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return FadeTransition(
      opacity: _fadeIn,
      child: SlideTransition(
        position: _slideIn,
        child: Container(
          decoration: BoxDecoration(
            color: AppTheme.surface,
            border: const Border(
              bottom: BorderSide(color: AppTheme.divider, width: 1),
            ),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.04),
                blurRadius: 12,
                offset: const Offset(0, 4),
              ),
            ],
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // Title row
              SafeArea(
                bottom: false,
                child: Padding(
                  padding:
                  const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
                  child: Row(
                    children: [
                      // Logo badge
                      Container(
                        width: 34,
                        height: 34,
                        decoration: BoxDecoration(
                          gradient: const LinearGradient(
                            colors: [Color(0xFF0A6EFF), Color(0xFF0050CC)],
                            begin: Alignment.topLeft,
                            end: Alignment.bottomRight,
                          ),
                          borderRadius: BorderRadius.circular(10),
                          boxShadow: [
                            BoxShadow(
                              color: AppTheme.primary.withOpacity(0.35),
                              blurRadius: 8,
                              offset: const Offset(0, 3),
                            ),
                          ],
                        ),
                        child: const Icon(
                          Icons.dns_rounded,
                          color: Colors.white,
                          size: 16,
                        ),
                      ),
                      const SizedBox(width: 12),
                      const Text(
                        'IP Manager',
                        style: TextStyle(
                          color: AppTheme.textPrimary,
                          fontSize: 18,
                          fontWeight: FontWeight.w700,
                          letterSpacing: -0.6,
                        ),
                      ),
                      const Spacer(),
                      // Status indicator dot
                      Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 10, vertical: 5),
                        decoration: BoxDecoration(
                          color: AppTheme.successLight,
                          borderRadius: BorderRadius.circular(20),
                        ),
                        child: Row(
                          children: [
                            Container(
                              width: 6,
                              height: 6,
                              decoration: const BoxDecoration(
                                color: AppTheme.success,
                                shape: BoxShape.circle,
                              ),
                            ),
                            const SizedBox(width: 5),
                            const Text(
                              'Live',
                              style: TextStyle(
                                color: AppTheme.success,
                                fontSize: 11,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
              ),

              // Tab Bar
              TabBar(
                controller: widget.tabController,
                tabs: widget.navItems.asMap().entries.map((entry) {
                  final i = entry.key;
                  final item = entry.value;
                  final isActive = widget.currentIndex == i;
                  return Tab(
                    child: AnimatedDefaultTextStyle(
                      duration: const Duration(milliseconds: 200),
                      style: TextStyle(
                        fontSize: 13,
                        fontWeight:
                        isActive ? FontWeight.w600 : FontWeight.w400,
                        color: isActive
                            ? AppTheme.primary
                            : AppTheme.textSecondary,
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          AnimatedSwitcher(
                            duration: const Duration(milliseconds: 200),
                            child: Icon(
                              isActive ? item.activeIcon : item.icon,
                              key: ValueKey(isActive),
                              size: 16,
                              color: isActive
                                  ? AppTheme.primary
                                  : AppTheme.textSecondary,
                            ),
                          ),
                          const SizedBox(width: 6),
                          Text(item.label),
                        ],
                      ),
                    ),
                  );
                }).toList(),
                labelPadding: EdgeInsets.zero,
                indicatorPadding:
                const EdgeInsets.symmetric(horizontal: 20),
                indicator: const UnderlineTabIndicator(
                  borderSide: BorderSide(color: AppTheme.primary, width: 2.5),
                  borderRadius: BorderRadius.only(
                    topLeft: Radius.circular(2),
                    topRight: Radius.circular(2),
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

class _NavItem {
  final String label;
  final IconData icon;
  final IconData activeIcon;

  const _NavItem({
    required this.label,
    required this.icon,
    required this.activeIcon,
  });
}