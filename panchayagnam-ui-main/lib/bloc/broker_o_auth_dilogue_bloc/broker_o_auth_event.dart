abstract class BrokerOAuthEvent {}

// ── TOTP ───────────────────────────────────────────────────────────────────────
/// Trigger a fresh TOTP generation (called on entry AND every 30 s auto-refresh).
class GenerateProfitmartTotp extends BrokerOAuthEvent {
  final String totpKey;
  GenerateProfitmartTotp({required this.totpKey});
}

/// Same for GoodWill (if needed in future; keeps symmetry).
class GenerateGoodwillTotp extends BrokerOAuthEvent {
  final String totpKey;
  GenerateGoodwillTotp({required this.totpKey});
}

// ── PROFITMART ────────────────────────────────────────────────────────────────
class ProfitmartOpenLoginUrl extends BrokerOAuthEvent {
  final String clientId;
  ProfitmartOpenLoginUrl({required this.clientId});
}

class ProfitmartExchangeToken extends BrokerOAuthEvent {
  final String userId;
  final String code;
  final String secretKey;
  final String? ip;

  ProfitmartExchangeToken({
    required this.userId,
    required this.code,
    required this.secretKey,
    this.ip,
  });
}

// ── GOODWILL ──────────────────────────────────────────────────────────────────
class GoodwillOpenLoginUrl extends BrokerOAuthEvent {
  final String clientId;
  GoodwillOpenLoginUrl({required this.clientId});
}

class GoodwillExchangeToken extends BrokerOAuthEvent {
  final String userId;
  final String code;
  final String secretKey;
  final String apiKey;
  final String? ip;

  GoodwillExchangeToken({
    required this.userId,
    required this.code,
    required this.secretKey,
    required this.apiKey,
    this.ip,
  });
}

// ── RESET ─────────────────────────────────────────────────────────────────────
class BrokerOAuthReset extends BrokerOAuthEvent {}