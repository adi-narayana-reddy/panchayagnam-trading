class DashboardCountModel {
  final int totalUsers;
  final int totalAdmins;
  final int totalSuperAdmins;
  final int totalBrokerAccounts;
  final int angelOneAccounts;
  final int motilalAccounts;
  final int profitMartAccounts;
  final int goodWillAccounts;
  final int dhanAccounts;
  final int upstoxAccounts;
  final int totalRequests;
  final int pendingRequests;
  final int approvedRequests;
  final int rejectedRequests;
  final int groupCount;

  const DashboardCountModel({
    this.totalUsers = 0,
    this.totalAdmins = 0,
    this.totalSuperAdmins = 0,
    this.totalBrokerAccounts = 0,
    this.angelOneAccounts = 0,
    this.motilalAccounts = 0,
    this.goodWillAccounts = 0,
    this.profitMartAccounts = 0,
    this.dhanAccounts = 0,
    this.upstoxAccounts = 0,
    this.totalRequests = 0,
    this.pendingRequests = 0,
    this.approvedRequests = 0,
    this.rejectedRequests = 0,
    this.groupCount = 0,
  });

  factory DashboardCountModel.fromJson(Map<String, dynamic> json) {
    return DashboardCountModel(
      totalUsers: json['totalUsers'] ?? 0,
      totalAdmins: json['totalAdmins'] ?? 0,
      totalSuperAdmins: json['totalSuperAdmins'] ?? 0,
      totalBrokerAccounts: json['totalBrokerAccounts'] ?? 0,
      angelOneAccounts: json['angelOneAccounts'] ?? 0,
      motilalAccounts: json['motilalAccounts'] ?? 0,
      profitMartAccounts: json['profitMartAccounts'] ?? json['profitmartAccounts'] ?? 0,
      goodWillAccounts: json['goodWillAccounts'] ?? json['goodwillAccounts'] ?? 0,
      dhanAccounts: json['dhanAccounts'] ?? 0,
      upstoxAccounts: json['upstoxAccounts'] ?? 0,
      totalRequests: json['totalRequests'] ?? 0,
      pendingRequests: json['pendingRequests'] ?? 0,
      approvedRequests: json['approvedRequests'] ?? 0,
      rejectedRequests: json['rejectedRequests'] ?? 0,
      groupCount: json['groupCount'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() => {
    'totalUsers': totalUsers,
    'totalAdmins': totalAdmins,
    'totalSuperAdmins': totalSuperAdmins,
    'totalBrokerAccounts': totalBrokerAccounts,
    'angelOneAccounts': angelOneAccounts,
    'motilalAccounts': motilalAccounts,
    'profitMartAccounts': profitMartAccounts,
    'profitmartAccounts': profitMartAccounts,
    'goodWillAccounts': goodWillAccounts,
    'goodwillAccounts': goodWillAccounts,
    'dhanAccounts': dhanAccounts,
    'upstoxAccounts': upstoxAccounts,
    'totalRequests': totalRequests,
    'pendingRequests': pendingRequests,
    'approvedRequests': approvedRequests,
    'rejectedRequests': rejectedRequests,
    'groupCount': groupCount,
  };
}