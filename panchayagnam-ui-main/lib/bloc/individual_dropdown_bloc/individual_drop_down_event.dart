import '../../models/individual_drop_downmodel.dart';

abstract class BrokerEvent {}

class FetchBrokers extends BrokerEvent {
  final int pyId;
  final String broker;

  FetchBrokers({
    required this.pyId,
    required this.broker,
  });
}

class SelectBroker extends BrokerEvent {
  final BrokerAccount broker;

  SelectBroker(this.broker);
}