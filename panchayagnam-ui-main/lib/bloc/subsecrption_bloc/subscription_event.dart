part of 'subscription_bloc.dart';

abstract class SubscriptionEvent {}

class CheckSubscriptionStatus extends SubscriptionEvent {
  final int pyId;
  CheckSubscriptionStatus({required this.pyId});
}

class SelectPlan extends SubscriptionEvent {
  final String planType;
  SelectPlan(this.planType);
}

class InitiatePayment extends SubscriptionEvent {
  final String planType;
  final String requestType;
  final int planMonths;
  final double amount;
  final int noOfClients;
  InitiatePayment(
    this.planType, {
    required this.requestType,
    required this.planMonths,
    required this.amount,
    required this.noOfClients,
  });
}

class RaiseAdminRequest extends SubscriptionEvent {
  final String reason;
  final String requestType;
  final int planMonths;
  final double amount;
  final int noOfClients;
  RaiseAdminRequest({
    this.reason = 'I want to manage broker accounts for my team',
    required this.requestType,
    required this.planMonths,
    required this.amount,
    required this.noOfClients,
  });
}
