import 'package:equatable/equatable.dart';

abstract class AuthState extends Equatable {
  const AuthState();
  @override
  List<Object?> get props => [];
}

class AuthInitial extends AuthState {}

class AuthLoading extends AuthState {}

// ── NEW ──────────────────────────────────────────────────────────────────────
class OtpSentSuccess extends AuthState {
  final String message;
  final String? otp;
  const OtpSentSuccess({required this.message, this.otp});
  @override
  List<Object?> get props => [message, otp];
}
// ─────────────────────────────────────────────────────────────────────────────

// Register
class RegisterSuccess extends AuthState {
  final String message;
  final int userId;
  const RegisterSuccess({required this.message, required this.userId});
  @override
  List<Object?> get props => [message, userId];
}

// Login
class LoginSuccess extends AuthState {
  final String token;
  final String fullName;
  final String email;
  final String phoneNumber;
  final String role;
  final int userId;

  const LoginSuccess({
    required this.token,
    required this.fullName,
    required this.email,
    required this.phoneNumber,
    required this.role,
    required this.userId,
  });

  @override
  List<Object?> get props => [token, fullName, email, phoneNumber, role, userId];
}

// Forgot MPIN — OTP sent
class ForgotMpinOtpSent extends AuthState {
  final String message;
  final String phoneNumber;
  final String? otp;
  const ForgotMpinOtpSent({required this.message, required this.phoneNumber, this.otp});
  @override
  List<Object?> get props => [message, phoneNumber, otp];
}

// Reset MPIN
class ResetMpinSuccess extends AuthState {
  final String message;
  const ResetMpinSuccess({required this.message});
  @override
  List<Object?> get props => [message];
}

// Logout
class LogoutSuccess extends AuthState {}

// Error
class AuthError extends AuthState {
  final String message;
  const AuthError({required this.message});
  @override
  List<Object?> get props => [message];
}