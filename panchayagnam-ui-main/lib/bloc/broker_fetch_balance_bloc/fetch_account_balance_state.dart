part of 'fetch_account_balance_bloc.dart';

abstract class FetchAccountBalanceState {}

class FetchAccountBalanceInitial extends FetchAccountBalanceState {}

/// Loading for a specific broker account id
class FetchAccountBalanceLoading extends FetchAccountBalanceState {
  final int pyBrokerId;
  FetchAccountBalanceLoading(this.pyBrokerId);
}

/// Successfully fetched — carries the refreshed availableCash
class FetchAccountBalanceLoaded extends FetchAccountBalanceState {
  final int pyBrokerId;
  final double availableCash;
  FetchAccountBalanceLoaded(this.pyBrokerId, this.availableCash);
}

class FetchAccountBalanceError extends FetchAccountBalanceState {
  final int pyBrokerId;
  final String message;
  FetchAccountBalanceError(this.pyBrokerId, this.message);
}