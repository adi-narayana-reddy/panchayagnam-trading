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
  InitiatePayment(this.planType);
}

class RaiseAdminRequest extends SubscriptionEvent {
  final String reason;
  RaiseAdminRequest({
    this.reason = 'I want to manage broker accounts for my team',
  });
}
