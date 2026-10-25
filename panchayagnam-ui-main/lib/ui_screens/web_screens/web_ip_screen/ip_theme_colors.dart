import 'package:flutter/material.dart';

class AppTheme {
  // Brand Colors — Light Mode
  static const Color primary       = Color(0xFF0A6EFF);
  static const Color primaryLight  = Color(0xFFE8F1FF);
  static const Color primaryDark   = Color(0xFF0050CC);
  static const Color surface       = Color(0xFFFFFFFF);
  static const Color surfaceCard   = Color(0xFFF5F7FA);
  static const Color surfaceElevated = Color(0xFFECEFF4);
  static const Color background    = Color(0xFFF0F2F5);
  static const Color textPrimary   = Color(0xFF0D1117);
  static const Color textSecondary = Color(0xFF5A6472);
  static const Color textTertiary  = Color(0xFFACB5C3);
  static const Color success       = Color(0xFF16A34A);
  static const Color successLight  = Color(0xFFDCFCE7);
  static const Color error         = Color(0xFFDC2626);
  static const Color errorLight    = Color(0xFFFEE2E2);
  static const Color warning       = Color(0xFFD97706);
  static const Color warningLight  = Color(0xFFFEF3C7);
  static const Color divider       = Color(0xFFE2E8F0);
  static const Color shadow        = Color(0x0A000000);

  static ThemeData get lightTheme => ThemeData(
    brightness: Brightness.light,
    scaffoldBackgroundColor: background,
    colorScheme: const ColorScheme.light(
      primary: primary,
      surface: surface,
      error: error,
    ),
    fontFamily: 'SF Pro Display',
    appBarTheme: const AppBarTheme(
      backgroundColor: surface,
      elevation: 0,
      shadowColor: Colors.transparent,
      surfaceTintColor: Colors.transparent,
      centerTitle: true,
      titleTextStyle: TextStyle(
        color: textPrimary,
        fontSize: 17,
        fontWeight: FontWeight.w600,
        letterSpacing: -0.4,
      ),
      iconTheme: IconThemeData(color: primary),
    ),
    tabBarTheme: const TabBarThemeData(
      labelColor: primary,
      unselectedLabelColor: textSecondary,
      indicatorColor: primary,
      indicatorSize: TabBarIndicatorSize.label,
      labelStyle: TextStyle(fontWeight: FontWeight.w600, fontSize: 13),
      unselectedLabelStyle: TextStyle(fontWeight: FontWeight.w400, fontSize: 13),
    ),
    elevatedButtonTheme: ElevatedButtonThemeData(
      style: ElevatedButton.styleFrom(
        backgroundColor: primary,
        foregroundColor: Colors.white,
        elevation: 0,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
        textStyle: const TextStyle(
          fontWeight: FontWeight.w600,
          fontSize: 15,
          letterSpacing: -0.3,
        ),
      ),
    ),
    inputDecorationTheme: InputDecorationTheme(
      filled: true,
      fillColor: surface,
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: BorderSide.none,
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: divider, width: 1),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: primary, width: 1.5),
      ),
      errorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: error, width: 1),
      ),
      hintStyle: const TextStyle(color: textTertiary, fontSize: 15),
      labelStyle: const TextStyle(color: textSecondary, fontSize: 13),
    ),
    cardTheme: CardThemeData(
      color: surface,
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: const BorderSide(color: divider, width: 0.5),
      ),
      margin: EdgeInsets.zero,
    ),
    chipTheme: ChipThemeData(
      backgroundColor: surfaceElevated,
      labelStyle: const TextStyle(fontSize: 12, color: textSecondary),
      side: const BorderSide(color: divider, width: 0.5),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
    ),
  );
}