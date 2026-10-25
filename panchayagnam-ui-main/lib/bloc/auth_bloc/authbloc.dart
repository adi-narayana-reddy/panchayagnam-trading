import 'package:bloc/bloc.dart';
import '../../service/service_class.dart';
import '../../session_storage_helper/session_storage_helper.dart';
import 'auth_event.dart';
import 'auth_state.dart';

class AuthBloc extends Bloc<AuthEvent, AuthState> {
  final ServiceClass serviceClass;

  AuthBloc({ServiceClass? authRepository})
    : serviceClass = authRepository ?? ServiceClass(),
      super(AuthInitial()) {
    on<SendOtpRequested>(_onSendOtp);
    on<RegisterRequested>(_onRegister);
    on<LoginRequested>(_onLogin);
    on<ForgotMpinRequested>(_onForgotMpin);
    on<ResetMpinRequested>(_onResetMpin);
    on<LogoutRequested>(_onLogout);
    on<AuthReset>(_onReset);
  }

  Future<void> _onSendOtp(
    SendOtpRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    try {
      final result = await serviceClass.sendOtp(
        phoneNumber: event.phoneNumber,
        codeReqFor: event.codeReqFor,
      );
      emit(
        OtpSentSuccess(message: result['message'] ?? 'OTP sent successfully'),
      );
    } catch (e) {
      emit(AuthError(message: e.toString().replaceFirst('Exception: ', '')));
    }
  }

  Future<void> _onRegister(
    RegisterRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    try {
      final result = await serviceClass.register(
        fullName: event.fullName,
        email: event.email,
        phoneNumber: event.phoneNumber,
        mpin: event.mpin,
        otp: event.otp,
      );
      emit(
        RegisterSuccess(
          message: result['message'] ?? 'Account created successfully',
          userId: result['data']['userId'],
        ),
      );
    } catch (e) {
      emit(AuthError(message: e.toString().replaceFirst('Exception: ', '')));
    }
  }

  Future<void> _onLogin(LoginRequested event, Emitter<AuthState> emit) async {
    emit(AuthLoading());
    try {
      final result = await serviceClass.login(
        phoneNumber: event.phoneNumber,
        mpin: event.mpin,
      );
      final data = result['data'] as Map<String, dynamic>;

      final token = data['token'] as String;
      final fullName = data['fullName'] as String;
      final email = data['email'] as String;
      final phoneNumber = data['phoneNumber'] as String;
      final role = data['role'] as String;
      final expiredFlag = data['expiredFlag'];

      final userId = data['userId'] as int? ?? 0;
      final expiresInDays = data['expiresInDays'] as int? ?? 0;
      final noOfClients = data['noOfClients'] as int? ?? 0;

      SessionStorageHelper.setString('auth_token', token);
      SessionStorageHelper.setString('user_role', role);
      SessionStorageHelper.setString('user_fullName', fullName);
      SessionStorageHelper.setString('user_email', email);
      SessionStorageHelper.setString('user_phoneNumber', phoneNumber);
      SessionStorageHelper.setInt('user_id', userId);
      SessionStorageHelper.setInt('expiresInDays', expiresInDays);
      SessionStorageHelper.setString('expiredFlag', expiredFlag.toString());
      SessionStorageHelper.setString('noOfClients', noOfClients.toString());
      emit(
        LoginSuccess(
          token: token,
          fullName: fullName,
          email: email,
          phoneNumber: phoneNumber,
          role: role,
          userId: userId,
        ),
      );
    } catch (e) {
      emit(AuthError(message: e.toString().replaceFirst('Exception: ', '')));
    }
  }

  Future<void> _onForgotMpin(
    ForgotMpinRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    try {
      final result = await serviceClass.forgotMpin(
        phoneNumber: event.phoneNumber,
      );
      emit(
        ForgotMpinOtpSent(
          message: result['message'] ?? 'OTP sent successfully',
          phoneNumber: event.phoneNumber,
        ),
      );
    } catch (e) {
      emit(AuthError(message: e.toString().replaceFirst('Exception: ', '')));
    }
  }

  Future<void> _onResetMpin(
    ResetMpinRequested event,
    Emitter<AuthState> emit,
  ) async {
    emit(AuthLoading());
    try {
      final result = await serviceClass.resetMpin(
        phoneNumber: event.phoneNumber,
        otp: event.otp,
        newMpin: event.newMpin,
      );
      emit(
        ResetMpinSuccess(
          message: result['message'] ?? 'MPIN reset successfully',
        ),
      );
    } catch (e) {
      emit(AuthError(message: e.toString().replaceFirst('Exception: ', '')));
    }
  }

  Future<void> _onLogout(LogoutRequested event, Emitter<AuthState> emit) async {
    SessionStorageHelper.clear();
    emit(LogoutSuccess());
  }

  void _onReset(AuthReset event, Emitter<AuthState> emit) {
    emit(AuthInitial());
  }
}
