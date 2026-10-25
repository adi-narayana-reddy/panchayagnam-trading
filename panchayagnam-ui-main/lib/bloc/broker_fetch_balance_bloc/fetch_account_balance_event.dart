part of 'fetch_account_balance_bloc.dart';

abstract class FetchAccountBalanceEvent {}

class LoadAccountBalance extends FetchAccountBalanceEvent {
  final int pyBrokerId;
  LoadAccountBalance(this.pyBrokerId);
}