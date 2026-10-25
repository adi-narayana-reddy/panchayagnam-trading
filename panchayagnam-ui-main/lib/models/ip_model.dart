// ─── Models ───────────────────────────────────────────────────────────────────

class ApiResponse<T> {
  final int statusCode;
  final String status;
  final String message;
  final T? data;

  ApiResponse({
    required this.statusCode,
    required this.status,
    required this.message,
    this.data,
  });
}

// ── Save IP ──────────────────────────────────────────────────────────────────

class SaveIpResponse {
  final int statusCode;
  final String status;
  final String message;
  final List<String> data;

  SaveIpResponse({
    required this.statusCode,
    required this.status,
    required this.message,
    required this.data,
  });

  factory SaveIpResponse.fromJson(Map<String, dynamic> json) {
    return SaveIpResponse(
      statusCode: json['statusCode'] ?? 0,
      status: json['status'] ?? '',
      message: json['message'] ?? '',
      data: List<String>.from(json['data'] ?? []),
    );
  }
}

// ── Fetch All IPs ─────────────────────────────────────────────────────────────

class FetchAllIpsResponse {
  final int statusCode;
  final String status;
  final String message;
  final List<String> data;

  FetchAllIpsResponse({
    required this.statusCode,
    required this.status,
    required this.message,
    required this.data,
  });

  factory FetchAllIpsResponse.fromJson(Map<String, dynamic> json) {
    return FetchAllIpsResponse(
      statusCode: json['statusCode'] ?? 0,
      status: json['status'] ?? '',
      message: json['message'] ?? '',
      data: List<String>.from(json['data'] ?? []),
    );
  }
}

// ── IP Report ─────────────────────────────────────────────────────────────────

class IpAccount {
  final String? userName;
  final String? clientId;
  final String? brokerType;
  final bool active;
  final bool isOwn;

  IpAccount({
    this.userName,
    this.clientId,
    this.brokerType,
    required this.active,
    required this.isOwn,
  });

  factory IpAccount.fromJson(Map<String, dynamic> json) {
    return IpAccount(
      userName: json['userName'],
      clientId: json['clientId'],
      brokerType: json['brokerType'],
      active: json['active'] ?? false,
      isOwn: json['isOwn'] ?? false,
    );
  }
}

class IpDetail {
  final String ipAddress;
  final int? portNo;
  final List<String> brokerTypes;
  final List<IpAccount> accounts;

  IpDetail({
    required this.ipAddress,
    this.portNo,
    required this.brokerTypes,
    required this.accounts,
  });

  bool get isUsed => accounts.isNotEmpty;

  factory IpDetail.fromJson(Map<String, dynamic> json) {
    return IpDetail(
      ipAddress: json['ipAddress'] ?? '',
      portNo: json['portNo'],
      brokerTypes: List<String>.from(json['brokerTypes'] ?? []),
      accounts: (json['accounts'] as List<dynamic>? ?? [])
          .map((a) => IpAccount.fromJson(a))
          .toList(),
    );
  }
}

class IpReportData {
  final int totalIpCount;
  final int usedIpCount;
  final int unusedIpCount;
  final List<IpDetail> details;

  IpReportData({
    required this.totalIpCount,
    required this.usedIpCount,
    required this.unusedIpCount,
    required this.details,
  });

  factory IpReportData.fromJson(Map<String, dynamic> json) {
    return IpReportData(
      totalIpCount: json['totalIpCount'] ?? 0,
      usedIpCount: json['usedIpCount'] ?? 0,
      unusedIpCount: json['unusedIpCount'] ?? 0,
      details: (json['details'] as List<dynamic>? ?? [])
          .map((d) => IpDetail.fromJson(d))
          .toList(),
    );
  }
}

class IpReportResponse {
  final int statusCode;
  final String status;
  final String message;
  final IpReportData data;

  IpReportResponse({
    required this.statusCode,
    required this.status,
    required this.message,
    required this.data,
  });

  factory IpReportResponse.fromJson(Map<String, dynamic> json) {
    return IpReportResponse(
      statusCode: json['statusCode'] ?? 0,
      status: json['status'] ?? '',
      message: json['message'] ?? '',
      data: IpReportData.fromJson(json['data']),
    );
  }
}

// ── IP Health ─────────────────────────────────────────────────────────────────

class IpHealthResult {
  final String ip;
  final String status;
  final String message;
  final String timestamp;

  IpHealthResult({
    required this.ip,
    required this.status,
    required this.message,
    required this.timestamp,
  });

  bool get isUp => status.toUpperCase() == 'UP';

  factory IpHealthResult.fromJson(Map<String, dynamic> json) {
    return IpHealthResult(
      ip: json['ip'] ?? '',
      status: json['status'] ?? '',
      message: json['message'] ?? '',
      timestamp: json['timestamp'] ?? '',
    );
  }
}

class IpHealthResponse {
  final int statusCode;
  final String status;
  final String message;
  final List<IpHealthResult> data;

  IpHealthResponse({
    required this.statusCode,
    required this.status,
    required this.message,
    required this.data,
  });

  factory IpHealthResponse.fromJson(Map<String, dynamic> json) {
    return IpHealthResponse(
      statusCode: json['statusCode'] ?? 0,
      status: json['status'] ?? '',
      message: json['message'] ?? '',
      data: (json['data'] as List<dynamic>? ?? [])
          .map((h) => IpHealthResult.fromJson(h))
          .toList(),
    );
  }
}