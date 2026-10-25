part of 'add_demat_bloc.dart';

abstract class AddDematAccountState {}

class AddDematAccountInitial extends AddDematAccountState {}

class AddDematAccountLoading extends AddDematAccountState {}

class AddDematAccountSuccess extends AddDematAccountState {
  final String message;
  AddDematAccountSuccess({this.message = 'Account added successfully!'});
}

class AddDematAccountFailure extends AddDematAccountState {
  final String error;
  AddDematAccountFailure({required this.error});
}