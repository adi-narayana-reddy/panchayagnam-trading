import '../../models/individual_drop_downmodel.dart';

abstract class BrokerState {}

class BrokerInitial extends BrokerState {}

class BrokerLoading extends BrokerState {}

class BrokerLoaded extends BrokerState {
  final List<BrokerAccount> brokers;
  final BrokerAccount selectedBroker;

  BrokerLoaded({
    required this.brokers,
    required this.selectedBroker,
  });

  BrokerLoaded copyWith({
    List<BrokerAccount>? brokers,
    BrokerAccount? selectedBroker,
  }) {
    return BrokerLoaded(
      brokers: brokers ?? this.brokers,
      selectedBroker: selectedBroker ?? this.selectedBroker,
    );
  }
}

class BrokerError extends BrokerState {
  final String message;
  BrokerError(this.message);
}