import 'dart:async';
import 'dart:math' as math;
import 'package:easy_trade_web/constants/colors.dart';
import 'package:easy_trade_web/ui_screens/web_screens/web_ip_screen/home_screen.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';
import '../../../app_route/app_route.dart';
import '../../../app_route/user_session.dart';
import '../../../bloc/auth_bloc/auth_event.dart';
import '../../../bloc/auth_bloc/auth_state.dart';
import '../../../bloc/auth_bloc/authbloc.dart';
import '../../../reusable_widets/web_forgot_screen.dart';
import '../web_register_screen/web_register_screen.dart';

class SignInScreen extends StatefulWidget {
  const SignInScreen({super.key});

  @override
  State<SignInScreen> createState() => _SignInScreenState();
}

class _SignInScreenState extends State<SignInScreen>
    with SingleTickerProviderStateMixin {
  bool _obscurePassword = true;
  late AnimationController _entryController;
  late Animation<double> _rightPanelSlide;
  late Animation<double> _rightPanelFade;

  @override
  void initState() {
    super.initState();
    _entryController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 900),
    );
    _rightPanelSlide = Tween<double>(begin: 40, end: 0).animate(
      CurvedAnimation(
        parent: _entryController,
        curve: const Interval(0.2, 1.0, curve: Curves.easeOutCubic),
      ),
    );
    _rightPanelFade = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(
        parent: _entryController,
        curve: const Interval(0.2, 1.0, curve: Curves.easeOut),
      ),
    );
    _entryController.forward();
  }

  @override
  void dispose() {
    _entryController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF3F0FB),
      body: Row(
        children: [
          Expanded(flex: 55, child: _LeftPanel()),
          Expanded(
            flex: 45,
            child: AnimatedBuilder(
              animation: _entryController,
              builder: (context, child) => Opacity(
                opacity: _rightPanelFade.value,
                child: Transform.translate(
                  offset: Offset(_rightPanelSlide.value, 0),
                  child: child,
                ),
              ),
              child: Center(
                child: _RightPanel(
                  obscurePassword: _obscurePassword,
                  onTogglePassword: () =>
                      setState(() => _obscurePassword = !_obscurePassword),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}

// ─── Left Panel ───────────────────────────────────────────────────────────────

class _LeftPanel extends StatefulWidget {
  @override
  State<_LeftPanel> createState() => _LeftPanelState();
}

class _LeftPanelState extends State<_LeftPanel>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _logoScale;
  late Animation<double> _logoFade;
  late Animation<Offset> _titleSlide;
  late Animation<double> _titleFade;
  late Animation<double> _chipsFade;
  late Animation<Offset> _chipsSlide;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1100),
    );

    _logoScale = Tween<double>(begin: 0.6, end: 1.0).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.0, 0.5, curve: Curves.elasticOut),
      ),
    );
    _logoFade = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.0, 0.4, curve: Curves.easeOut),
      ),
    );
    _titleSlide = Tween<Offset>(begin: const Offset(-0.15, 0), end: Offset.zero)
        .animate(
          CurvedAnimation(
            parent: _controller,
            curve: const Interval(0.25, 0.75, curve: Curves.easeOutCubic),
          ),
        );
    _titleFade = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.25, 0.7, curve: Curves.easeOut),
      ),
    );
    _chipsFade = Tween<double>(begin: 0, end: 1).animate(
      CurvedAnimation(
        parent: _controller,
        curve: const Interval(0.55, 1.0, curve: Curves.easeOut),
      ),
    );
    _chipsSlide = Tween<Offset>(begin: const Offset(0, 0.3), end: Offset.zero)
        .animate(
          CurvedAnimation(
            parent: _controller,
            curve: const Interval(0.55, 1.0, curve: Curves.easeOutCubic),
          ),
        );

    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      children: [
        // Gradient background
        Container(
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
              colors: [Color(0xFF7C3AED), Color(0xFF5B21B6), Color(0xFF4C1D95)],
              stops: [0.0, 0.55, 1.0],
            ),
          ),
        ),

        // Decorative orbs
        Positioned(
          top: -80,
          right: -60,
          child: _AnimatedOrb(
            size: 320,
            color: AppColors.accentViolet.withOpacity(0.35),
            duration: const Duration(seconds: 6),
          ),
        ),
        Positioned(
          bottom: -60,
          left: -40,
          child: _AnimatedOrb(
            size: 260,
            color: AppColors.sidebarHover.withOpacity(0.5),
            duration: const Duration(seconds: 8),
            reverse: true,
          ),
        ),
        // Extra subtle orb for depth
        Positioned(
          top: 200,
          right: 80,
          child: _AnimatedOrb(
            size: 140,
            color: Colors.white.withOpacity(0.05),
            duration: const Duration(seconds: 7),
          ),
        ),

        // Grid texture overlay
        Positioned.fill(child: CustomPaint(painter: _GridPainter())),

        // Content
        Padding(
          padding: const EdgeInsets.fromLTRB(40, 36, 32, 0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // Logo row
              AnimatedBuilder(
                animation: _controller,
                builder: (context, child) => Opacity(
                  opacity: _logoFade.value,
                  child: Transform.scale(
                    scale: _logoScale.value,
                    alignment: Alignment.centerLeft,
                    child: child,
                  ),
                ),
                child: Row(
                  children: [
                    _LogoCircle(),
                    const SizedBox(width: 14),
                    RichText(
                      text: const TextSpan(
                        style: TextStyle(fontSize: 21, letterSpacing: 0.8),
                        children: [
                          TextSpan(
                            text: 'PANCHA',
                            style: TextStyle(
                              color: Colors.white,
                              fontWeight: FontWeight.w800,
                            ),
                          ),
                          TextSpan(
                            text: 'YAGNAM',
                            style: TextStyle(
                              color: Color(0xFFDDD6FE),
                              fontWeight: FontWeight.w300,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 30),

              // Title block
              AnimatedBuilder(
                animation: _controller,
                builder: (context, child) => Opacity(
                  opacity: _titleFade.value,
                  child: SlideTransition(position: _titleSlide, child: child),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'Smart Trading.',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 30,
                        fontWeight: FontWeight.w800,
                        height: 1.15,
                        letterSpacing: -0.5,
                      ),
                    ),
                    const Text(
                      'Smarter Returns.',
                      style: TextStyle(
                        color: Color(0xFFDDD6FE),
                        fontSize: 30,
                        fontWeight: FontWeight.w300,
                        height: 1.15,
                        letterSpacing: -0.5,
                      ),
                    ),
                    const SizedBox(height: 20),
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 14,
                        vertical: 10,
                      ),
                      decoration: BoxDecoration(
                        color: Colors.white.withOpacity(0.08),
                        borderRadius: BorderRadius.circular(10),
                        border: Border.all(
                          color: Colors.white.withOpacity(0.12),
                        ),
                      ),
                      child: const Text(
                        'Manage NSE & BSE portfolios with institutional-grade\n'
                        'tools, real-time analytics, and multi-broker support.',
                        style: TextStyle(
                          color: Color(0xFFC4B5FD),
                          fontSize: 13,
                          height: 1.9,
                        ),
                      ),
                    ),
                  ],
                ),
              ),

              const SizedBox(height: 36),

              // Stat chips
              AnimatedBuilder(
                animation: _controller,
                builder: (context, child) => Opacity(
                  opacity: _chipsFade.value,
                  child: SlideTransition(position: _chipsSlide, child: child),
                ),
              ),
            ],
          ),
        ),

        // Candlestick chart at bottom
        const Positioned(
          bottom: 0,
          left: 0,
          right: 0,
          child: SizedBox(height: 180, child: AnimatedCandlestickChart()),
        ),

        // Shimmer line
        Positioned(bottom: 178, left: 0, right: 0, child: _ShimmerLine()),
      ],
    );
  }
}

// ─── Logo circle with glow pulse ─────────────────────────────────────────────

class _LogoCircle extends StatefulWidget {
  @override
  State<_LogoCircle> createState() => _LogoCircleState();
}

class _LogoCircleState extends State<_LogoCircle>
    with SingleTickerProviderStateMixin {
  late AnimationController _glowCtrl;
  late Animation<double> _glow;

  @override
  void initState() {
    super.initState();
    _glowCtrl = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    )..repeat(reverse: true);
    _glow = Tween<double>(
      begin: 12,
      end: 28,
    ).animate(CurvedAnimation(parent: _glowCtrl, curve: Curves.easeInOut));
  }

  @override
  void dispose() {
    _glowCtrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _glowCtrl,
      builder: (context, child) => Container(
        width: 80,
        height: 80,
        decoration: BoxDecoration(
          shape: BoxShape.circle,
          gradient: const RadialGradient(
            colors: [Color(0xFF9D5CFF), Color(0xFF3B0F8C)],
          ),
          border: Border.all(color: Colors.white.withOpacity(0.25), width: 1.5),
          boxShadow: [
            BoxShadow(
              color: const Color(0xFF7C3AED).withOpacity(0.6),
              blurRadius: _glow.value,
              spreadRadius: 2,
              offset: const Offset(0, 4),
            ),
          ],
        ),
        child: ClipOval(
          child: Image.asset(
            'assets/images/login_logo.png',
            width: 68,
            height: 68,
            fit: BoxFit.cover, // ← use cover, not contain
            filterQuality: FilterQuality.high,
          ),
        ),
      ),
    );
  }
}

// ─── Animated stat chip with hover ───────────────────────────────────────────

class _StatChip extends StatefulWidget {
  final IconData icon;
  final String value;
  final String label;
  final int delay;

  const _StatChip({
    required this.icon,
    required this.value,
    required this.label,
    required this.delay,
  });

  @override
  State<_StatChip> createState() => _StatChipState();
}

class _StatChipState extends State<_StatChip> {
  bool _hovered = false;

  @override
  Widget build(BuildContext context) {
    return MouseRegion(
      onEnter: (_) => setState(() => _hovered = true),
      onExit: (_) => setState(() => _hovered = false),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
        decoration: BoxDecoration(
          color: _hovered
              ? Colors.white.withOpacity(0.2)
              : Colors.white.withOpacity(0.12),
          borderRadius: BorderRadius.circular(14),
          border: Border.all(
            color: _hovered
                ? Colors.white.withOpacity(0.35)
                : Colors.white.withOpacity(0.18),
          ),
          boxShadow: _hovered
              ? [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.15),
                    blurRadius: 12,
                    offset: const Offset(0, 4),
                  ),
                ]
              : [],
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Icon(widget.icon, color: const Color(0xFFDDD6FE), size: 17),
            const SizedBox(height: 8),
            Text(
              widget.value,
              style: const TextStyle(
                color: Colors.white,
                fontSize: 18,
                fontWeight: FontWeight.w800,
                letterSpacing: -0.3,
              ),
            ),
            const SizedBox(height: 3),
            Text(
              widget.label,
              style: const TextStyle(
                color: Color(0xFFC4B5FD),
                fontSize: 9,
                letterSpacing: 1.4,
                fontWeight: FontWeight.w600,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// ─── Shimmer line ─────────────────────────────────────────────────────────────

class _ShimmerLine extends StatefulWidget {
  @override
  State<_ShimmerLine> createState() => _ShimmerLineState();
}

class _ShimmerLineState extends State<_ShimmerLine>
    with SingleTickerProviderStateMixin {
  late AnimationController _ctrl;

  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 3),
    )..repeat();
  }

  @override
  void dispose() {
    _ctrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _ctrl,
      builder: (context, _) => CustomPaint(
        painter: _ShimmerLinePainter(_ctrl.value),
        size: const Size(double.infinity, 2),
      ),
    );
  }
}

class _ShimmerLinePainter extends CustomPainter {
  final double progress;
  _ShimmerLinePainter(this.progress);

  @override
  void paint(Canvas canvas, Size size) {
    final w = size.width;
    final shimmerX = w * progress * 1.4 - w * 0.2;
    final paint = Paint()
      ..shader = LinearGradient(
        colors: [
          Colors.white.withOpacity(0),
          Colors.white.withOpacity(0.6),
          Colors.white.withOpacity(0),
        ],
        stops: const [0.0, 0.5, 1.0],
        begin: Alignment(shimmerX / w * 2 - 1 - 0.3, 0),
        end: Alignment(shimmerX / w * 2 - 1 + 0.3, 0),
      ).createShader(Rect.fromLTWH(0, 0, w, 2));
    canvas.drawRect(Rect.fromLTWH(0, 0, w, 1.5), paint);
  }

  @override
  bool shouldRepaint(_ShimmerLinePainter old) => old.progress != progress;
}

// ─── Grid texture painter ─────────────────────────────────────────────────────

class _GridPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    final paint = Paint()
      ..color = Colors.white.withOpacity(0.04)
      ..strokeWidth = 0.5;
    const spacing = 32.0;
    for (double x = 0; x < size.width; x += spacing) {
      canvas.drawLine(Offset(x, 0), Offset(x, size.height), paint);
    }
    for (double y = 0; y < size.height; y += spacing) {
      canvas.drawLine(Offset(0, y), Offset(size.width, y), paint);
    }
  }

  @override
  bool shouldRepaint(_GridPainter old) => false;
}

// ─── Animated orb (floating pulse) ───────────────────────────────────────────

class _AnimatedOrb extends StatefulWidget {
  final double size;
  final Color color;
  final Duration duration;
  final bool reverse;

  const _AnimatedOrb({
    required this.size,
    required this.color,
    required this.duration,
    this.reverse = false,
  });

  @override
  State<_AnimatedOrb> createState() => _AnimatedOrbState();
}

class _AnimatedOrbState extends State<_AnimatedOrb>
    with SingleTickerProviderStateMixin {
  late AnimationController _ctrl;
  late Animation<double> _scale;

  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(vsync: this, duration: widget.duration);
    _scale = Tween<double>(
      begin: 0.88,
      end: 1.12,
    ).animate(CurvedAnimation(parent: _ctrl, curve: Curves.easeInOut));
    if (widget.reverse) {
      _ctrl.repeat(reverse: true);
      _ctrl.value = 0.5;
    } else {
      _ctrl.repeat(reverse: true);
    }
  }

  @override
  void dispose() {
    _ctrl.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return ScaleTransition(
      scale: _scale,
      child: Container(
        width: widget.size,
        height: widget.size,
        decoration: BoxDecoration(
          shape: BoxShape.circle,
          gradient: RadialGradient(colors: [widget.color, Colors.transparent]),
        ),
      ),
    );
  }
}

// ─── Right Panel ──────────────────────────────────────────────────────────────

class _RightPanel extends StatefulWidget {
  final bool obscurePassword;
  final VoidCallback onTogglePassword;

  const _RightPanel({
    required this.obscurePassword,
    required this.onTogglePassword,
  });

  @override
  State<_RightPanel> createState() => _RightPanelState();
}

class _RightPanelState extends State<_RightPanel>
    with SingleTickerProviderStateMixin {
  final _formKey = GlobalKey<FormState>();
  final _phoneCtrl = TextEditingController();
  final _mpinCtrl = TextEditingController();
  final _otpCtrl = TextEditingController();

  bool _isOtpLogin = false;
  bool _otpSent = false;
  bool _canResend = false;
  int _timerSeconds = 60;
  Timer? _timer;
  bool _isSendingOtp = false;

  late AnimationController _staggerCtrl;
  late List<Animation<double>> _itemFades;
  late List<Animation<Offset>> _itemSlides;

  static const int _itemCount = 7;

  @override
  void initState() {
    super.initState();
    _staggerCtrl = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1000),
    );

    _itemFades = List.generate(_itemCount, (i) {
      final start = 0.05 * i;
      final end = (0.05 * i + 0.45).clamp(0.0, 1.0);
      return Tween<double>(begin: 0, end: 1).animate(
        CurvedAnimation(
          parent: _staggerCtrl,
          curve: Interval(start, end, curve: Curves.easeOut),
        ),
      );
    });

    _itemSlides = List.generate(_itemCount, (i) {
      final start = 0.05 * i;
      final end = (0.05 * i + 0.5).clamp(0.0, 1.0);
      return Tween<Offset>(
        begin: const Offset(0, 0.12),
        end: Offset.zero,
      ).animate(
        CurvedAnimation(
          parent: _staggerCtrl,
          curve: Interval(start, end, curve: Curves.easeOutCubic),
        ),
      );
    });

    _staggerCtrl.forward();
  }

  void _startTimer() {
    _timer?.cancel();
    setState(() {
      _timerSeconds = 60;
      _canResend = false;
    });
    _timer = Timer.periodic(const Duration(seconds: 1), (t) {
      if (_timerSeconds <= 1) {
        t.cancel();
        if (mounted) {
          setState(() {
            _timerSeconds = 0;
            _canResend = true;
          });
        }
      } else {
        if (mounted) setState(() => _timerSeconds--);
      }
    });
  }

  String get _timerLabel {
    final m = _timerSeconds ~/ 60;
    final s = _timerSeconds % 60;
    return '${m.toString().padLeft(2, '0')}:${s.toString().padLeft(2, '0')}';
  }

  void _sendLoginOtp() {
    final phone = _phoneCtrl.text.trim();
    if (phone.length != 10 || !RegExp(r'^[0-9]+$').hasMatch(phone)) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(
          content: Text('Please enter a valid 10-digit phone number first'),
          backgroundColor: Color(0xFFEF4444),
          behavior: SnackBarBehavior.floating,
        ),
      );
      return;
    }
    setState(() => _isSendingOtp = true);
    context.read<AuthBloc>().add(
      SendOtpRequested(
        phoneNumber: phone,
        codeReqFor: 'Login',
      ),
    );
  }

  @override
  void dispose() {
    _phoneCtrl.dispose();
    _mpinCtrl.dispose();
    _otpCtrl.dispose();
    _timer?.cancel();
    _staggerCtrl.dispose();
    super.dispose();
  }

  Widget _stagger(int index, Widget child) {
    return AnimatedBuilder(
      animation: _staggerCtrl,
      builder: (context, _) => Opacity(
        opacity: _itemFades[index].value,
        child: SlideTransition(position: _itemSlides[index], child: child),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return BlocConsumer<AuthBloc, AuthState>(
      listener: (context, state) {
        if (state is LoginSuccess) {
          UserSession().set(
            token: state.token,
            role: state.role,
            fullName: state.fullName,
            email: state.email,
            phoneNumber: state.phoneNumber,
            userId: state.userId,
          );
          context.go(AppRoutes.dashboard);
        } else if (state is OtpSentSuccess) {
          setState(() {
            _otpSent = true;
            _canResend = false;
            _isSendingOtp = false;
            if (state.otp != null && state.otp!.isNotEmpty) {
              _otpCtrl.text = state.otp!;
            }
          });
          _startTimer();
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text(
                state.otp != null && state.otp!.isNotEmpty
                    ? '${state.message} (Login OTP: ${state.otp})'
                    : state.message,
              ),
              backgroundColor: const Color(0xFF7C3AED),
              duration: const Duration(seconds: 8),
              behavior: SnackBarBehavior.floating,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(10),
              ),
            ),
          );
        } else if (state is AuthError) {
          setState(() => _isSendingOtp = false);
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(
              content: Text(state.message),
              backgroundColor: const Color(0xFFEF4444),
              behavior: SnackBarBehavior.floating,
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(10),
              ),
            ),
          );
        }
      },
      builder: (context, state) {
        final isLoading = state is AuthLoading;

        return Container(
          decoration: const BoxDecoration(color: Color(0xFFF3F0FB)),
          child: SingleChildScrollView(
            padding: const EdgeInsets.symmetric(horizontal: 52, vertical: 40),
            child: Form(
              key: _formKey,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // 0 — Icon badge
                  _stagger(
                    0,
                    Container(
                      width: 52,
                      height: 52,
                      decoration: BoxDecoration(
                        gradient: const LinearGradient(
                          colors: [Color(0xFFEDE9FE), Color(0xFFDDD6FE)],
                          begin: Alignment.topLeft,
                          end: Alignment.bottomRight,
                        ),
                        borderRadius: BorderRadius.circular(14),
                        boxShadow: [
                          BoxShadow(
                            color: const Color(0xFF7C3AED).withOpacity(0.15),
                            blurRadius: 16,
                            offset: const Offset(0, 6),
                          ),
                        ],
                      ),
                      child: const Icon(
                        Icons.lock_outline_rounded,
                        color: Color(0xFF7C3AED),
                        size: 26,
                      ),
                    ),
                  ),

                  const SizedBox(height: 22),

                  // 1 — Headline & Mode Toggle
                  _stagger(
                    1,
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          'Welcome back!',
                          style: TextStyle(
                            fontSize: 30,
                            fontWeight: FontWeight.w800,
                            color: Color(0xFF1E1B4B),
                            letterSpacing: -0.5,
                            height: 1.1,
                          ),
                        ),
                        const SizedBox(height: 8),
                        const Text(
                          'Sign in to your trading dashboard',
                          style: TextStyle(
                            fontSize: 14,
                            color: Color(0xFF6B7280),
                            height: 1.5,
                          ),
                        ),
                        const SizedBox(height: 20),
                        _loginModeSelector(),
                      ],
                    ),
                  ),

                  const SizedBox(height: 28),

                  // 2 — Phone field
                  _stagger(
                    2,
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        _fieldLabel('PHONE NUMBER'),
                        const SizedBox(height: 8),
                        if (_isOtpLogin)
                          _phoneFieldWithOtpButton()
                        else
                          _phoneField(),
                      ],
                    ),
                  ),

                  const SizedBox(height: 22),

                  // 3 — MPIN or OTP field
                  _stagger(
                    3,
                    Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        if (!_isOtpLogin) ...[
                          _fieldLabel('MPIN'),
                          const SizedBox(height: 8),
                          _mpinField(),
                        ] else ...[
                          _fieldLabel('6-DIGIT OTP'),
                          const SizedBox(height: 8),
                          _otpField(),
                        ],
                      ],
                    ),
                  ),

                  const SizedBox(height: 14),

                  // 4 — Forgot MPIN or OTP Resend status
                  _stagger(
                    4,
                    Row(
                      children: [
                        const Spacer(),
                        if (!_isOtpLogin)
                          InkWell(
                            onTap: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (_) => BlocProvider(
                                    create: (_) => AuthBloc(),
                                    child: const WebForgotMpinScreen(),
                                  ),
                                ),
                              );
                            },
                            borderRadius: BorderRadius.circular(6),
                            child: const Padding(
                              padding: EdgeInsets.symmetric(
                                horizontal: 4,
                                vertical: 2,
                              ),
                              child: Text(
                                'Forgot MPIN?',
                                style: TextStyle(
                                  fontSize: 13,
                                  color: AppColors.primary,
                                  fontWeight: FontWeight.w700,
                                ),
                              ),
                            ),
                          )
                        else if (_otpSent && !_canResend)
                          Padding(
                            padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 2),
                            child: Text(
                              'Resend available in $_timerLabel',
                              style: const TextStyle(
                                fontSize: 12,
                                color: Color(0xFF7C3AED),
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                          ),
                      ],
                    ),
                  ),

                  const SizedBox(height: 24),

                  // 5 — Sign In button
                  _stagger(
                    5,
                    _AnimatedSignInButton(
                      isLoading: isLoading,
                      buttonText: _isOtpLogin ? 'Sign In with OTP' : 'Sign In with MPIN',
                      onPressed: () {
                        if (_formKey.currentState!.validate()) {
                          if (_isOtpLogin) {
                            context.read<AuthBloc>().add(
                              LoginWithOtpRequested(
                                phoneNumber: _phoneCtrl.text.trim(),
                                otp: _otpCtrl.text.trim(),
                              ),
                            );
                          } else {
                            context.read<AuthBloc>().add(
                              LoginRequested(
                                phoneNumber: _phoneCtrl.text.trim(),
                                mpin: _mpinCtrl.text.trim(),
                              ),
                            );
                          }
                        }
                      },
                    ),
                  ),

                  const SizedBox(height: 22),

                  // 6 — Register link
                  _stagger(
                    6,
                    Center(
                      child: RichText(
                        text: TextSpan(
                          text: "Don't have an account? ",
                          style: const TextStyle(
                            fontSize: 14,
                            color: Color(0xFF6B7280),
                          ),
                          children: [
                            WidgetSpan(
                              child: InkWell(
                                onTap: () {
                                  Navigator.push(
                                    context,
                                    MaterialPageRoute(
                                      builder: (_) => BlocProvider(
                                        create: (_) => AuthBloc(),
                                        child: const WebRegisterScreen(),
                                      ),
                                    ),
                                  );
                                },
                                borderRadius: BorderRadius.circular(4),
                                child: const Text(
                                  'Register',
                                  style: TextStyle(
                                    color: AppColors.primary,
                                    fontWeight: FontWeight.w800,
                                    decoration: TextDecoration.underline,
                                    decorationColor: AppColors.primary,
                                    fontSize: 14,
                                  ),
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),

                  const SizedBox(height: 28),

                  // Divider
                  Row(
                    children: [
                      Expanded(child: Divider(color: Colors.grey.shade200)),
                      Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 14),
                        child: Text(
                          'Secured by',
                          style: TextStyle(
                            fontSize: 11,
                            color: Colors.grey.shade400,
                            letterSpacing: 0.3,
                          ),
                        ),
                      ),
                      Expanded(child: Divider(color: Colors.grey.shade200)),
                    ],
                  ),

                  const SizedBox(height: 16),

                  // Security badge
                  Center(
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 14,
                        vertical: 8,
                      ),
                      decoration: BoxDecoration(
                        color: const Color(0xFFEDE9FE),
                        borderRadius: BorderRadius.circular(20),
                        border: Border.all(
                          color: const Color(0xFF7C3AED).withOpacity(0.2),
                        ),
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          const Icon(
                            Icons.shield_outlined,
                            size: 15,
                            color: AppColors.primary,
                          ),
                          const SizedBox(width: 7),
                          const Text(
                            'PANCHAYAGNAM Investments',
                            style: TextStyle(
                              fontSize: 12,
                              color: AppColors.primary,
                              fontWeight: FontWeight.w700,
                              letterSpacing: 0.2,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }

  Widget _fieldLabel(String text) => Text(
    text,
    style: const TextStyle(
      fontSize: 10,
      fontWeight: FontWeight.w800,
      letterSpacing: 1.6,
      color: Color(0xFF6B7280),
    ),
  );

  Widget _phoneField() {
    return TextFormField(
      controller: _phoneCtrl,
      keyboardType: TextInputType.phone,
      style: const TextStyle(
        fontSize: 14,
        color: Color(0xFF1E1B4B),
        fontWeight: FontWeight.w500,
      ),
      decoration: _inputDecoration(
        hint: 'Enter your phone number',
        icon: Icons.phone_outlined,
      ),
      validator: (v) {
        if (v == null || v.isEmpty) return 'Phone number is required';
        if (v.length != 10) return 'Enter valid 10-digit phone number';
        if (!RegExp(r'^[0-9]+$').hasMatch(v)) return 'Only numbers allowed';
        return null;
      },
    );
  }

  Widget _mpinField() {
    return TextFormField(
      controller: _mpinCtrl,
      obscureText: widget.obscurePassword,
      keyboardType: TextInputType.number,
      maxLength: 4,
      style: const TextStyle(
        fontSize: 14,
        color: Color(0xFF1E1B4B),
        fontWeight: FontWeight.w500,
      ),
      decoration: _inputDecoration(
        hint: 'Enter your 4-digit MPIN',
        icon: widget.obscurePassword
            ? Icons.visibility_outlined
            : Icons.visibility_off_outlined,
        iconTap: widget.onTogglePassword,
      ).copyWith(counterText: ''),
      validator: (v) {
        if (v == null || v.isEmpty) return 'MPIN is required';
        if (v.length < 4) return 'MPIN must be 4 digits';
        if (!RegExp(r'^[0-9]+$').hasMatch(v)) return 'Only numbers allowed';
        return null;
      },
    );
  }

  InputDecoration _inputDecoration({
    required String hint,
    required IconData icon,
    VoidCallback? iconTap,
  }) {
    return InputDecoration(
      hintText: hint,
      hintStyle: const TextStyle(fontSize: 13, color: Color(0xFF9CA3AF)),
      suffixIcon: InkWell(
        onTap: iconTap,
        borderRadius: BorderRadius.circular(8),
        child: Icon(icon, size: 18, color: const Color(0xFF9CA3AF)),
      ),
      filled: true,
      fillColor: const Color(0xFFFAF9FF),
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 15),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: Color(0xFF7C3AED), width: 2),
      ),
      errorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: Color(0xFFEF4444)),
      ),
      focusedErrorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: Color(0xFFEF4444), width: 2),
      ),
    );
  }

  Widget _loginModeSelector() {
    return Container(
      padding: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: const Color(0xFFEDE9FE).withOpacity(0.5),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: const Color(0xFFDDD6FE)),
      ),
      child: Row(
        children: [
          Expanded(
            child: _loginModeButton(
              title: 'Login with MPIN',
              icon: Icons.lock_outline_rounded,
              isActive: !_isOtpLogin,
              onTap: () => setState(() => _isOtpLogin = false),
            ),
          ),
          const SizedBox(width: 6),
          Expanded(
            child: _loginModeButton(
              title: 'Login with OTP',
              icon: Icons.sms_outlined,
              isActive: _isOtpLogin,
              onTap: () => setState(() => _isOtpLogin = true),
            ),
          ),
        ],
      ),
    );
  }

  Widget _loginModeButton({
    required String title,
    required IconData icon,
    required bool isActive,
    required VoidCallback onTap,
  }) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(9),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 200),
        padding: const EdgeInsets.symmetric(vertical: 10),
        decoration: BoxDecoration(
          color: isActive ? Colors.white : Colors.transparent,
          borderRadius: BorderRadius.circular(9),
          boxShadow: isActive
              ? [
                  BoxShadow(
                    color: const Color(0xFF7C3AED).withOpacity(0.12),
                    blurRadius: 8,
                    offset: const Offset(0, 2),
                  )
                ]
              : [],
        ),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              icon,
              size: 16,
              color: isActive ? const Color(0xFF7C3AED) : const Color(0xFF6B7280),
            ),
            const SizedBox(width: 8),
            Text(
              title,
              style: TextStyle(
                fontSize: 13,
                fontWeight: isActive ? FontWeight.w700 : FontWeight.w500,
                color: isActive ? const Color(0xFF7C3AED) : const Color(0xFF6B7280),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _phoneFieldWithOtpButton() {
    return TextFormField(
      controller: _phoneCtrl,
      keyboardType: TextInputType.phone,
      style: const TextStyle(
        fontSize: 14,
        color: Color(0xFF1E1B4B),
        fontWeight: FontWeight.w500,
      ),
      decoration: InputDecoration(
        hintText: 'Enter your phone number',
        hintStyle: const TextStyle(fontSize: 13, color: Color(0xFF9CA3AF)),
        prefixIcon: const Icon(Icons.phone_outlined, size: 20, color: Color(0xFF9CA3AF)),
        suffixIcon: Padding(
          padding: const EdgeInsets.only(right: 8),
          child: _isSendingOtp
              ? const SizedBox(
                  width: 20,
                  height: 20,
                  child: Center(
                    child: CircularProgressIndicator(
                      strokeWidth: 2,
                      color: Color(0xFF7C3AED),
                    ),
                  ),
                )
              : TextButton(
                  onPressed: (_otpSent && !_canResend) ? null : _sendLoginOtp,
                  style: TextButton.styleFrom(
                    foregroundColor: const Color(0xFF7C3AED),
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                  ),
                  child: Text(
                    _otpSent ? (_canResend ? 'Resend OTP' : _timerLabel) : 'Get OTP',
                    style: const TextStyle(
                      fontSize: 13,
                      fontWeight: FontWeight.w700,
                      color: Color(0xFF7C3AED),
                    ),
                  ),
                ),
        ),
        filled: true,
        fillColor: const Color(0xFFFAF9FF),
        contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 15),
        border: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
        ),
        enabledBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: Color(0xFFE5E7EB)),
        ),
        focusedBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: Color(0xFF7C3AED), width: 2),
        ),
        errorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: Color(0xFFEF4444)),
        ),
        focusedErrorBorder: OutlineInputBorder(
          borderRadius: BorderRadius.circular(12),
          borderSide: const BorderSide(color: Color(0xFFEF4444), width: 2),
        ),
      ),
      validator: (v) {
        if (v == null || v.isEmpty) return 'Phone number is required';
        if (v.length != 10) return 'Enter valid 10-digit phone number';
        if (!RegExp(r'^[0-9]+$').hasMatch(v)) return 'Only numbers allowed';
        return null;
      },
    );
  }

  Widget _otpField() {
    return TextFormField(
      controller: _otpCtrl,
      keyboardType: TextInputType.number,
      maxLength: 6,
      style: const TextStyle(
        fontSize: 16,
        color: Color(0xFF1E1B4B),
        fontWeight: FontWeight.w700,
        letterSpacing: 4,
      ),
      decoration: _inputDecoration(
        hint: 'Enter 6-digit OTP',
        icon: Icons.shield_outlined,
      ).copyWith(counterText: ''),
      validator: (v) {
        if (v == null || v.isEmpty) return 'OTP is required';
        if (v.length < 6) return 'OTP must be 6 digits';
        if (!RegExp(r'^[0-9]+$').hasMatch(v)) return 'Only numbers allowed';
        return null;
      },
    );
  }
}

// ─── Animated Sign In Button ──────────────────────────────────────────────────

class _AnimatedSignInButton extends StatefulWidget {
  final bool isLoading;
  final VoidCallback onPressed;
  final String? buttonText;

  const _AnimatedSignInButton({
    required this.isLoading,
    required this.onPressed,
    this.buttonText,
  });

  @override
  State<_AnimatedSignInButton> createState() => _AnimatedSignInButtonState();
}

class _AnimatedSignInButtonState extends State<_AnimatedSignInButton>
    with SingleTickerProviderStateMixin {
  bool _hovered = false;
  late AnimationController _rippleCtrl;
  late Animation<double> _ripple;

  @override
  void initState() {
    super.initState();
    _rippleCtrl = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 600),
    );
    _ripple = Tween<double>(
      begin: 0,
      end: 1,
    ).animate(CurvedAnimation(parent: _rippleCtrl, curve: Curves.easeOut));
  }

  @override
  void dispose() {
    _rippleCtrl.dispose();
    super.dispose();
  }

  void _handleTap() {
    if (!widget.isLoading) {
      _rippleCtrl.forward(from: 0);
      widget.onPressed();
    }
  }

  @override
  Widget build(BuildContext context) {
    return MouseRegion(
      onEnter: (_) => setState(() => _hovered = true),
      onExit: (_) => setState(() => _hovered = false),
      child: InkWell(
        onTap: _handleTap,
        child: AnimatedContainer(
          duration: const Duration(milliseconds: 200),
          width: double.infinity,
          height: 52,
          decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: _hovered
                  ? [const Color(0xFF8B5CF6), const Color(0xFF6D28D9)]
                  : [const Color(0xFF7C3AED), const Color(0xFF5B21B6)],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
            borderRadius: BorderRadius.circular(14),
            boxShadow: [
              BoxShadow(
                color: const Color(
                  0xFF7C3AED,
                ).withOpacity(_hovered ? 0.5 : 0.3),
                blurRadius: _hovered ? 20 : 10,
                offset: const Offset(0, 6),
              ),
            ],
          ),
          child: ClipRRect(
            borderRadius: BorderRadius.circular(14),
            child: Stack(
              alignment: Alignment.center,
              children: [
                // Ripple
                AnimatedBuilder(
                  animation: _rippleCtrl,
                  builder: (context, _) {
                    if (_ripple.value == 0) return const SizedBox.shrink();
                    return Positioned.fill(
                      child: CustomPaint(
                        painter: _RipplePainter(_ripple.value),
                      ),
                    );
                  },
                ),
                if (widget.isLoading)
                  const SizedBox(
                    width: 22,
                    height: 22,
                    child: CircularProgressIndicator(
                      color: Colors.white,
                      strokeWidth: 2.5,
                    ),
                  )
                else
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(
                        widget.buttonText ?? 'Sign In',
                        style: const TextStyle(
                          fontSize: 15,
                          fontWeight: FontWeight.w800,
                          color: Colors.white,
                          letterSpacing: 0.5,
                        ),
                      ),
                      const SizedBox(width: 8),
                      const Icon(
                        Icons.arrow_forward_rounded,
                        color: Colors.white,
                        size: 18,
                      ),
                    ],
                  ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _RipplePainter extends CustomPainter {
  final double progress;
  _RipplePainter(this.progress);

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final maxRadius = size.width * 0.75;
    final paint = Paint()
      ..color = Colors.white.withOpacity((1 - progress) * 0.25)
      ..style = PaintingStyle.fill;
    canvas.drawCircle(center, maxRadius * progress, paint);
  }

  @override
  bool shouldRepaint(_RipplePainter old) => old.progress != progress;
}

// ─── Candlestick chart (original, kept as-is) ─────────────────────────────────
// Make sure AnimatedCandlestickChart is defined in your project.
// The widget below is a placeholder referencing your existing one.
