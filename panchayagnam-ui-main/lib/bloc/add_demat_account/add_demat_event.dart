part of 'add_demat_bloc.dart';

abstract class AddDematEvent {}

class AddDematAccountEvent extends AddDematEvent {
  final int? id;
  final String clientId;
  final String mobileNumber;
  final String broker;
  final String apiKey;
  final String mpin;
  final String password;
  final String totpReferenceKey;
  final String? dob;           // maps to twoFa for Motilal (dd/MM/yyyy)
  final bool isAdmin;
  final List<String> childIds;
  final String nickName;
  final String? ip;
  final String actionType;
  final bool? active;
  final bool? isOwn;

  // ── Motilal-specific ────────────────────────────────────────────────────────
  final String? apiSecretKey;  // Motilal: apiSecretKey
  final String? vendorInfo;    // Motilal: vendorInfo (defaults to clientId)

  AddDematAccountEvent({
    this.id,
    this.active,
    required this.clientId,
    required this.mobileNumber,
    required this.broker,
    required this.apiKey,
    required this.mpin,
    required this.password,
    required this.totpReferenceKey,
    this.dob,
    required this.isAdmin,
    required this.childIds,
    required this.nickName,
    required this.actionType,
    required this.isOwn,
    required this.ip,

    // Motilal-specific (optional — ignored for other brokers)
    this.apiSecretKey,
    this.vendorInfo,
  });
}

class ResetAddDematEvent extends AddDematEvent {}