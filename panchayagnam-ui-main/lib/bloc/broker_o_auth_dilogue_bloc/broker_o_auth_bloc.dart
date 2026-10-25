import 'package:flutter_bloc/flutter_bloc.dart';
import '../../service/o_auth_broker_service.dart';
import 'broker_o_auth_event.dart';
import 'broker_o_auth_state.dart';

class BrokerOAuthBloc extends Bloc<BrokerOAuthEvent, BrokerOAuthState> {
  final OAuthBrokerService _service = OAuthBrokerService();

  BrokerOAuthBloc() : super(BrokerOAuthIdle()) {
    on<GenerateProfitmartTotp>(_onGenerateProfitmartTotp);
    on<GenerateGoodwillTotp>(_onGenerateGoodwillTotp);
    on<ProfitmartOpenLoginUrl>(_onProfitmartOpenLoginUrl);
    on<ProfitmartExchangeToken>(_onProfitmartExchangeToken);
    on<GoodwillOpenLoginUrl>(_onGoodwillOpenLoginUrl);
    on<GoodwillExchangeToken>(_onGoodwillExchangeToken);
    on<BrokerOAuthReset>(_onReset);
  }

  // ── Reset ──────────────────────────────────────────────────────────────────
  void _onReset(BrokerOAuthReset event, Emitter<BrokerOAuthState> emit) =>
      emit(BrokerOAuthIdle());

  // ── TOTP ───────────────────────────────────────────────────────────────────

  Future<void> _onGenerateProfitmartTotp(
      GenerateProfitmartTotp event,
      Emitter<BrokerOAuthState> emit,
      ) async {
    // Don't emit BrokerOAuthLoading here — the TOTP auto-refreshes silently
    // so we keep showing the old code while fetching a fresh one.
    try {
      final result =
      await _service.generateProfitmartTotp(totpKey: event.totpKey);
      if (result['status'] == 'SUCCESS') {
        emit(ProfitmartTotpGenerated(
          totp: result['totp'] as String,
          totpKey: event.totpKey,
        ));
      } else {
        emit(BrokerOAuthFailure(
          result['message'] as String? ?? 'Failed to generate TOTP',
        ));
      }
    } catch (e) {
      emit(BrokerOAuthFailure('Unexpected error: $e'));
    }
  }

  Future<void> _onGenerateGoodwillTotp(
      GenerateGoodwillTotp event,
      Emitter<BrokerOAuthState> emit,
      ) async {
    try {
      // GoodWill reuses the same Profitmart TOTP endpoint for now;
      // swap the service call if a dedicated endpoint is added.
      final result =
      await _service.generateProfitmartTotp(totpKey: event.totpKey);
      if (result['status'] == 'SUCCESS') {
        emit(GoodwillTotpGenerated(
          totp: result['totp'] as String,
          totpKey: event.totpKey,
        ));
      } else {
        emit(BrokerOAuthFailure(
          result['message'] as String? ?? 'Failed to generate TOTP',
        ));
      }
    } catch (e) {
      emit(BrokerOAuthFailure('Unexpected error: $e'));
    }
  }

  // ── Profitmart ─────────────────────────────────────────────────────────────

  Future<void> _onProfitmartOpenLoginUrl(
      ProfitmartOpenLoginUrl event,
      Emitter<BrokerOAuthState> emit,
      ) async {
    emit(BrokerOAuthLoading());
    try {
      final result = await _service.getProfitmartLoginUrl(
        clientId: event.clientId,
      );
      if (result['status'] == 'SUCCESS') {
        emit(ProfitmartLoginUrlOpened(loginUrl: result['loginUrl'] as String));
      } else {
        emit(BrokerOAuthFailure(
          result['message'] as String? ?? 'Failed to get Profitmart login URL',
        ));
      }
    } catch (e) {
      emit(BrokerOAuthFailure('Unexpected error: $e'));
    }
  }

  Future<void> _onProfitmartExchangeToken(
      ProfitmartExchangeToken event,
      Emitter<BrokerOAuthState> emit,
      ) async {
    emit(BrokerOAuthLoading());
    try {
      final result = await _service.exchangeProfitmartToken(
        userId: event.userId,
        code: event.code,
        secretKey: event.secretKey,
        ip: event.ip,
      );

      if (result['status'] == 'success') {
        emit(ProfitmartTokenSuccess(
          userId: result['userId'] as String? ?? '',
          uname: result['uname'] as String? ?? '',
          accessToken: result['access_token'] as String? ?? '',
          susertoken: result['susertoken'] as String? ?? '',
          expiresAt: result['expires_at'] as String? ?? '',
          brkname: result['brkname'] as String? ?? 'Profitmart',
          lastaccesstime: result['lastaccesstime'] as String? ?? '',
        ));
      } else {
        emit(BrokerOAuthFailure(
          result['message'] as String? ?? 'Profitmart token exchange failed',
        ));
      }
    } catch (e) {
      emit(BrokerOAuthFailure('Unexpected error: $e'));
    }
  }

  // ── GoodWill ───────────────────────────────────────────────────────────────

  Future<void> _onGoodwillOpenLoginUrl(
      GoodwillOpenLoginUrl event,
      Emitter<BrokerOAuthState> emit,
      ) async {
    emit(BrokerOAuthLoading());
    try {
      final result = await _service.getGoodwillLoginUrl(
        clientId: event.clientId,
      );
      if (result['status'] == 'SUCCESS') {
        emit(GoodwillLoginUrlOpened(loginUrl: result['loginUrl'] as String));
      } else {
        emit(BrokerOAuthFailure(
          result['message'] as String? ?? 'Failed to get GoodWill login URL',
        ));
      }
    } catch (e) {
      emit(BrokerOAuthFailure('Unexpected error: $e'));
    }
  }

  Future<void> _onGoodwillExchangeToken(
      GoodwillExchangeToken event,
      Emitter<BrokerOAuthState> emit,
      ) async {
    emit(BrokerOAuthLoading());
    try {
      final result = await _service.exchangeGoodwillToken(
        userId: event.userId,
        code: event.code,
        secretKey: event.secretKey,
        apiKey: event.apiKey,
        ip: event.ip,
      );

      if (result['status'] == 'success') {
        final rawExarr = result['exarr'];
        final List<String> exarr = rawExarr is List
            ? rawExarr.map((e) => e.toString()).toList()
            : <String>[];

        emit(GoodwillTokenSuccess(
          userId: result['userId'] as String? ?? '',
          uname: result['uname'] as String? ?? '',
          accessToken: result['access_token'] as String? ?? '',
          usersessionid: result['usersessionid'] as String? ?? '',
          email: result['email'] as String? ?? '',
          exarr: exarr,
        ));
      } else {
        emit(BrokerOAuthFailure(
          result['message'] as String? ?? 'GoodWill token exchange failed',
        ));
      }
    } catch (e) {
      emit(BrokerOAuthFailure('Unexpected error: $e'));
    }
  }
}