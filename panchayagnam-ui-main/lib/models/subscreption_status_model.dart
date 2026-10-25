class SubscriptionRequestModel {
  final int requestId;
  final int pyId;
  final String fullName;
  final String phoneNumber;
  final String email;
  final String requestedRole;
  final String status; // PENDING | APPROVED | REJECTED
  final String? reason;
  final int? reviewedById;
  final String? reviewedByName;
  final String? reviewedAt;
  final String createdAt;

  const SubscriptionRequestModel({
    required this.requestId,
    required this.pyId,
    required this.fullName,
    required this.phoneNumber,
    required this.email,
    required this.requestedRole,
    required this.status,
    this.reason,
    this.reviewedById,
    this.reviewedByName,
    this.reviewedAt,
    required this.createdAt,
  });

  factory SubscriptionRequestModel.fromJson(Map<String, dynamic> json) {
    return SubscriptionRequestModel(
      requestId: json['requestId'] as int,
      pyId: json['pyId'] as int,
      fullName: json['fullName'] as String,
      phoneNumber: json['phoneNumber'] as String,
      email: json['email'] as String,
      requestedRole: json['requestedRole'] as String,
      status: json['status'] as String,
      reason: json['reason'] as String?,
      reviewedById: json['reviewedById'] as int?,
      reviewedByName: json['reviewedByName'] as String?,
      reviewedAt: json['reviewedAt'] as String?,
      createdAt: json['createdAt'] as String,
    );
  }

  Map<String, dynamic> toJson() => {
    'requestId': requestId,
    'pyId': pyId,
    'fullName': fullName,
    'phoneNumber': phoneNumber,
    'email': email,
    'requestedRole': requestedRole,
    'status': status,
    'reason': reason,
    'reviewedById': reviewedById,
    'reviewedByName': reviewedByName,
    'reviewedAt': reviewedAt,
    'createdAt': createdAt,
  };
}