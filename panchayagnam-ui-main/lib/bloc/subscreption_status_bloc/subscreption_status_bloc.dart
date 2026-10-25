import 'package:flutter_bloc/flutter_bloc.dart';

import '../../models/subscreption_status_model.dart';
import '../../service/subscreption_service.dart';

abstract class SubscriptionEvent {}

class FetchSubscriptionStatus extends SubscriptionEvent {
  final int userId;
  final String type;
  FetchSubscriptionStatus(this.userId, this.type);
}

abstract class SubscriptionStatusState {}

class SubscriptionInitial extends SubscriptionStatusState {}

class SubscriptionStatusLoading extends SubscriptionStatusState {}

class SubscriptionInactive extends SubscriptionStatusState {
  final String? selectedPlan;
  final List<SubscriptionRequestModel> requests;

  SubscriptionInactive({this.selectedPlan, this.requests = const []});
}

class SubscriptionPending extends SubscriptionStatusState {
  final SubscriptionRequestModel request;
  SubscriptionPending(this.request);
}

class SubscriptionApproved extends SubscriptionStatusState {
  final SubscriptionRequestModel request;
  SubscriptionApproved(this.request);
}

class SubscriptionRejected extends SubscriptionStatusState {
  final SubscriptionRequestModel request;
  final String? selectedPlan;
  SubscriptionRejected(this.request, {this.selectedPlan});
}

class SubscriptionStatusError extends SubscriptionStatusState {
  final String message;
  SubscriptionStatusError(this.message);
}

class SubscriptionStatusBloc
    extends Bloc<SubscriptionEvent, SubscriptionStatusState> {
  final SubscriptionService _service;

  SubscriptionStatusBloc({SubscriptionService? service})
    : _service = service ?? SubscriptionService(),
      super(SubscriptionInitial()) {
    on<FetchSubscriptionStatus>(_onFetchStatus);
  }

  Future<void> _onFetchStatus(
    FetchSubscriptionStatus event,
    Emitter<SubscriptionStatusState> emit,
  ) async {
    emit(SubscriptionStatusLoading());

    try {
      final requests = await _service.fetchUserRequests(
        event.userId,
        event.type,
      );

      if (requests.isEmpty) {
        emit(SubscriptionInactive(requests: requests));
        return;
      }

      final latest = requests.first;

      switch (latest.status) {
        case 'APPROVED':
          emit(SubscriptionApproved(latest));
          break;
        case 'PENDING':
          emit(SubscriptionPending(latest));
          break;
        case 'REJECTED':
          emit(SubscriptionRejected(latest));
          break;
        default:
          emit(SubscriptionInactive(requests: requests));
      }
    } catch (e) {
      emit(SubscriptionStatusError(e.toString()));
    }
  }
}
