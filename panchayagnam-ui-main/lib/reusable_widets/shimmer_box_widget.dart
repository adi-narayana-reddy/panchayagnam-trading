import 'package:flutter/cupertino.dart';

class ShimmerBox extends StatefulWidget {
  final double height;
  final double width;
  final double radius;

  ShimmerBox({
    required this.height,
    required this.width,
    this.radius = 3,
  });

  @override
  State<ShimmerBox> createState() => ShimmerBoxState();
}

class ShimmerBoxState extends State<ShimmerBox>
    with SingleTickerProviderStateMixin {
  late AnimationController _ctrl;
  late Animation<double> _anim;

  @override
  void initState() {
    super.initState();
    _ctrl = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1200),
    );
    _anim = Tween<double>(
      begin: -1.5,
      end: 2.0,
    ).animate(CurvedAnimation(parent: _ctrl, curve: Curves.easeInOutSine));
    _ctrl.repeat();
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
      builder: (_, __) => Container(
        height: widget.height,
        width: widget.width,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(widget.radius),
          gradient: LinearGradient(
            begin: Alignment(_anim.value - 1, 0),
            end: Alignment(_anim.value, 0),
            colors: const [
              Color(0xFFEEF2F7),
              Color(0xFFE2E8F0),
              Color(0xFFEEF2F7),
            ],
          ),
        ),
      ),
    );
  }
}
