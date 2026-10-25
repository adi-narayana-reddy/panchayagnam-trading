abstract class BrokerOAuthState {}

// ── SHARED ────────────────────────────────────────────────────────────────────
class BrokerOAuthIdle extends BrokerOAuthState {}

class BrokerOAuthLoading extends BrokerOAuthState {}

class BrokerOAuthFailure extends BrokerOAuthState {
  final String message;
  BrokerOAuthFailure(this.message);
}

// ── TOTP ───────────────────────────────────────────────────────────────────────
/// Emitted after a successful TOTP generation call.
/// [totp]    — the 6-digit code to display.
/// [totpKey] — echoed back so the UI can use it for the next auto-refresh.
class ProfitmartTotpGenerated extends BrokerOAuthState {
  final String totp;
  final String totpKey;
  ProfitmartTotpGenerated({required this.totp, required this.totpKey});
}

class GoodwillTotpGenerated extends BrokerOAuthState {
  final String totp;
  final String totpKey;
  GoodwillTotpGenerated({required this.totp, required this.totpKey});
}

// ── PROFITMART ────────────────────────────────────────────────────────────────
class ProfitmartLoginUrlOpened extends BrokerOAuthState {
  final String loginUrl;
  ProfitmartLoginUrlOpened({required this.loginUrl});
}

class ProfitmartTokenSuccess extends BrokerOAuthState {
  final String userId;
  final String uname;
  final String accessToken;
  final String susertoken;
  final String expiresAt;
  final String brkname;
  final String lastaccesstime;

  ProfitmartTokenSuccess({
    required this.userId,
    required this.uname,
    required this.accessToken,
    required this.susertoken,
    required this.expiresAt,
    required this.brkname,
    required this.lastaccesstime,
  });
}

// ── GOODWILL ──────────────────────────────────────────────────────────────────
class GoodwillLoginUrlOpened extends BrokerOAuthState {
  final String loginUrl;
  GoodwillLoginUrlOpened({required this.loginUrl});
}

class GoodwillTokenSuccess extends BrokerOAuthState {
  final String userId;
  final String uname;
  final String accessToken;
  final String usersessionid;
  final String email;
  final List<String> exarr;

  GoodwillTokenSuccess({
    required this.userId,
    required this.uname,
    required this.accessToken,
    required this.usersessionid,
    required this.email,
    required this.exarr,
  });
}