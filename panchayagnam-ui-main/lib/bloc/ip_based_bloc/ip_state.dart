part of 'ip_bloc.dart';

class IpState {
  // ── Save IPs ────────────────────────────────────────────────────────────────
  final bool isSaving;
  final SaveIpResponse? saveResponse;
  final String? saveError;

  // ── Fetch All IPs ───────────────────────────────────────────────────────────
  final bool isFetchingAll;
  final List<String> allIps;
  final String? fetchAllError;

  // ── IP Report ───────────────────────────────────────────────────────────────
  final bool isFetchingReport;
  final IpReportResponse? reportResponse;
  final String? reportError;

  // ── IP Health ───────────────────────────────────────────────────────────────
  final bool isCheckingHealth;
  final IpHealthResponse? healthResponse;
  final String? healthError;

  const IpState({
    this.isSaving = false,
    this.saveResponse,
    this.saveError,
    this.isFetchingAll = false,
    this.allIps = const [],
    this.fetchAllError,
    this.isFetchingReport = false,
    this.reportResponse,
    this.reportError,
    this.isCheckingHealth = false,
    this.healthResponse,
    this.healthError,
  });

  IpState copyWith({
    bool? isSaving,
    SaveIpResponse? saveResponse,
    String? saveError,
    bool clearSaveResponse = false,
    bool? isFetchingAll,
    List<String>? allIps,
    String? fetchAllError,
    bool? isFetchingReport,
    IpReportResponse? reportResponse,
    String? reportError,
    bool? isCheckingHealth,
    IpHealthResponse? healthResponse,
    String? healthError,
    bool clearHealthResponse = false,
  }) {
    return IpState(
      isSaving: isSaving ?? this.isSaving,
      saveResponse: clearSaveResponse ? null : (saveResponse ?? this.saveResponse),
      saveError: saveError ?? this.saveError,
      isFetchingAll: isFetchingAll ?? this.isFetchingAll,
      allIps: allIps ?? this.allIps,
      fetchAllError: fetchAllError ?? this.fetchAllError,
      isFetchingReport: isFetchingReport ?? this.isFetchingReport,
      reportResponse: reportResponse ?? this.reportResponse,
      reportError: reportError ?? this.reportError,
      isCheckingHealth: isCheckingHealth ?? this.isCheckingHealth,
      healthResponse: clearHealthResponse ? null : (healthResponse ?? this.healthResponse),
      healthError: healthError ?? this.healthError,
    );
  }
}