import 'package:easy_trade_web/service/service_class.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'dart:html' as html;
part 'subscription_event.dart';
part 'subscription_state.dart';

class SubscriptionBloc extends Bloc<SubscriptionEvent, SubscriptionState> {
  bool _isSubscribed = false;
  int _pyId = 0;
  ServiceClass serviceClass = ServiceClass();

  SubscriptionBloc() : super(SubscriptionInitial()) {
    on<CheckSubscriptionStatus>(_onCheckStatus);
    on<SelectPlan>(_onSelectPlan);
    on<InitiatePayment>(_onInitiatePayment);
    on<RaiseAdminRequest>(_onRaiseAdminRequest);
  }

  Future<void> _onCheckStatus(
    CheckSubscriptionStatus event,
    Emitter<SubscriptionState> emit,
  ) async {
    emit(SubscriptionLoading());

    _pyId = int.tryParse(html.window.sessionStorage['user_id'] ?? '') ?? 0;

    await Future.delayed(const Duration(milliseconds: 300));
    if (_isSubscribed) {
      emit(SubscriptionActive());
    } else {
      emit(SubscriptionInactive());
    }
  }

  void _onSelectPlan(SelectPlan event, Emitter<SubscriptionState> emit) {
    emit(SubscriptionInactive(selectedPlan: event.planType));
  }

  Future<void> _onInitiatePayment(
    InitiatePayment event,
    Emitter<SubscriptionState> emit,
  ) async {
    emit(SubscriptionPaymentInitiated(event.planType));
    add(
      RaiseAdminRequest(
        requestType: event.requestType,
        planMonths: event.planMonths, amount: event.amount, noOfClients: event.noOfClients,
      ),
    );
  }

  Future<void> _onRaiseAdminRequest(
    RaiseAdminRequest event,
    Emitter<SubscriptionState> emit,
  ) async {
    final currentState = state;
    String planType = 'monthly';
    if (currentState is SubscriptionPaymentInitiated) {
      planType = currentState.planType;
    }

    if (_pyId == 0) {
      _pyId = int.tryParse(html.window.sessionStorage['user_id'] ?? '') ?? 0;
    }

    try {
      final responseBody = await serviceClass.raiseAdminRequestApi(
        pyId: _pyId,
        reason: event.reason,
        requestType: event.requestType,
        planMonths: event.planMonths,
        amount: event.amount,
        noOfClients: event.noOfClients,
      );

      if (responseBody['success'] == true) {
        final data = responseBody['data'] as Map<String, dynamic>;
        emit(
          SubscriptionRequestSuccess(
            planType: planType,
            requestId: data['requestId'].toString(),
            status: data['status'].toString(),
          ),
        );
      } else {
        emit(
          SubscriptionRequestFailure(
            planType: planType,
            message: responseBody['message'] ?? 'Request failed',
          ),
        );
      }
    } catch (e) {
      emit(
        SubscriptionRequestFailure(
          planType: planType,
          message: e.toString().contains('Exception:')
              ? e.toString().replaceFirst('Exception: ', '')
              : 'Network error. Please check your connection.',
        ),
      );
    }
  }
}
