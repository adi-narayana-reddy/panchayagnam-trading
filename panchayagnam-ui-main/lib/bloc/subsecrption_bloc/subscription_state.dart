part of 'subscription_bloc.dart';

abstract class SubscriptionState {}

class SubscriptionInitial extends SubscriptionState {}

class SubscriptionLoading extends SubscriptionState {}

class SubscriptionActive extends SubscriptionState {}

class SubscriptionInactive extends SubscriptionState {
  final String? selectedPlan; // null, 'monthly', 'yearly'
  SubscriptionInactive({this.selectedPlan});
}

class SubscriptionPaymentInitiated extends SubscriptionState {
  final String planType;
  SubscriptionPaymentInitiated(this.planType);
}

class SubscriptionRequestSuccess extends SubscriptionState {
  final String planType;
  final String requestId;
  final String status;
  SubscriptionRequestSuccess({
    required this.planType,
    required this.requestId,
    required this.status,
  });
}

class SubscriptionRequestFailure extends SubscriptionState {
  final String planType;
  final String message;
  SubscriptionRequestFailure({required this.planType, required this.message});
}

class SubscriptionError extends SubscriptionState {
  final String message;
  SubscriptionError(this.message);
}