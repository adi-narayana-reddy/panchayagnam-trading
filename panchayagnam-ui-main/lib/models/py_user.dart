class PyUser {
  final int pyId;
  final String fullName;
  final String email;
  final String phoneNumber;
  final String role;
  final bool isActive;
  final String createdAt;
  final int accountsCount;

  PyUser({
    required this.pyId,
    required this.fullName,
    required this.email,
    required this.phoneNumber,
    required this.role,
    required this.isActive,
    required this.createdAt,
    required this.accountsCount,
  });

  factory PyUser.fromJson(Map<String, dynamic> json) => PyUser(
    pyId: json['pyId'] ?? 0,
    fullName: json['fullName'] ?? '',
    email: json['email'] ?? '',
    phoneNumber: json['phoneNumber'] ?? '',
    role: json['role'] ?? '',
    isActive: json['isActive'] ?? false,
    createdAt: json['createdAt'] ?? '',
    accountsCount: json['accountsCount'] ?? 0,
  );
}